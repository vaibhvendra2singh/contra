#!/usr/bin/env python3
"""Create OM2 releases and planning sprints through Jira REST.

This fallback is for Jira features that the visible team-managed project UI does
not expose. It is idempotent by release/sprint name and supports --dry-run.
Credentials are read only from the environment; never put tokens in this file.
"""

from __future__ import annotations

import argparse
import json
import os
import shlex
import sys
import urllib.error
import urllib.parse
import urllib.request


VERSIONS = [
    {"name": "v0.1", "description": "Order Core"},
    {"name": "v0.2", "description": "Fulfillment & Tracking"},
    {"name": "v1.0", "description": "Returns, Refunds & Release Gating"},
]
SPRINTS = [
    {"name": "OM2 Sprint 1", "goal": "Deliver reliable order creation, checkout, payment capture, and retry paths."},
    {"name": "OM2 Sprint 2", "goal": "Allocate inventory and complete fulfillment, shipping, tracking, and cancellation safely."},
    {"name": "OM2 Sprint 3", "goal": "Complete self-service returns, controlled rollout, release readiness, and Jira visibility."},
]


class Jira:
    def __init__(self, base: str, email: str, token: str, dry_run: bool) -> None:
        self.base = base.rstrip("/")
        self.auth = (email, token)
        self.dry_run = dry_run

    def request(self, method: str, path: str, payload=None):
        url = self.base + path
        body = json.dumps(payload).encode() if payload is not None else None
        req = urllib.request.Request(url, data=body, method=method)
        req.add_header("Accept", "application/json")
        req.add_header("Content-Type", "application/json")
        import base64

        credential = base64.b64encode(f"{self.auth[0]}:{self.auth[1]}".encode()).decode()
        req.add_header("Authorization", f"Basic {credential}")
        try:
            with urllib.request.urlopen(req, timeout=20) as response:
                raw = response.read()
                return json.loads(raw) if raw else None
        except urllib.error.HTTPError as exc:
            detail = exc.read().decode(errors="replace")
            raise RuntimeError(f"Jira returned HTTP {exc.code}: {detail}") from exc


def ensure_versions(jira: Jira):
    existing = jira.request("GET", "/rest/api/3/project/OM2/versions")
    by_name = {item["name"]: item for item in existing}
    for version in VERSIONS:
        if version["name"] in by_name:
            print(f"EXISTS version {version['name']} ({by_name[version['name']].get('id')})")
            continue
        print(f"CREATE version {version['name']}: {version['description']}")
        if not jira.dry_run:
            created = jira.request("POST", "/rest/api/3/version", {**version, "project": "OM2"})
            print(f"  created id={created.get('id')}")


def ensure_sprints(jira: Jira, board_id: int):
    path = f"/rest/agile/1.0/board/{board_id}/sprint?maxResults=100"
    existing = jira.request("GET", path).get("values", [])
    by_name = {item["name"]: item for item in existing}
    for sprint in SPRINTS:
        if sprint["name"] in by_name:
            item = by_name[sprint["name"]]
            print(f"EXISTS sprint {sprint['name']} ({item['id']}, {item['state']})")
            continue
        print(f"CREATE sprint {sprint['name']}: {sprint['goal']}")
        if not jira.dry_run:
            created = jira.request("POST", f"/rest/agile/1.0/board/{board_id}/sprint", sprint)
            print(f"  created id={created.get('id')}")


def load_env_file(path: str = ".env") -> None:
    """Load simple KEY=VALUE entries without replacing exported environment values."""
    try:
        lines = open(path, encoding="utf-8")
    except FileNotFoundError:
        return
    with lines:
        for raw_line in lines:
            line = raw_line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            key = key.strip()
            value = value.strip()
            try:
                parsed = shlex.split(value, comments=False)
                if len(parsed) == 1:
                    value = parsed[0]
            except ValueError:
                continue
            if key and key not in os.environ:
                os.environ[key] = value


def main() -> int:
    load_env_file()
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dry-run", action="store_true", help="inspect existing Jira setup and print planned creates")
    parser.add_argument("--board-id", type=int, default=172)
    args = parser.parse_args()

    base = os.getenv("JIRA_BASE_URL", "").strip()
    email = os.getenv("JIRA_EMAIL", "").strip()
    token = os.getenv("JIRA_API_TOKEN", "").strip()
    if not all((base, email, token)):
        print("Set JIRA_BASE_URL, JIRA_EMAIL, and JIRA_API_TOKEN in your local environment (or .env loader).", file=sys.stderr)
        return 2
    jira = Jira(base, email, token, args.dry_run)
    try:
        ensure_versions(jira)
        ensure_sprints(jira, args.board_id)
    except (urllib.error.URLError, RuntimeError, KeyError, TypeError) as exc:
        print(f"Jira setup failed: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
