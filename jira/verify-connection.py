#!/usr/bin/env python3
"""Verify OM2 Jira API access without printing credentials or issue contents."""

from __future__ import annotations

import base64
import json
import os
import shlex
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path


def load_env_file(path: str = ".env") -> None:
    try:
        lines = Path(path).open(encoding="utf-8")
    except FileNotFoundError:
        return
    with lines:
        for raw_line in lines:
            line = raw_line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            try:
                parsed = shlex.split(value.strip(), comments=False)
            except ValueError:
                continue
            if key.strip() and key.strip() not in os.environ and len(parsed) == 1:
                os.environ[key.strip()] = parsed[0]


def request(base: str, auth: str, method: str, path: str, payload=None):
    data = json.dumps(payload).encode("utf-8") if payload is not None else None
    req = urllib.request.Request(base + path, data=data, method=method)
    req.add_header("Authorization", auth)
    req.add_header("Accept", "application/json")
    if data is not None:
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            raw = response.read()
            return response.status, json.loads(raw) if raw else None
    except urllib.error.HTTPError as error:
        error.read()  # discard response body; it may contain account or project details
        return error.code, None
    except Exception as error:
        return None, type(error).__name__


def report(label: str, result):
    status, _body = result
    if status is None:
        print(f"{label}: request failed ({_body})")
        return False
    print(f"{label}: HTTP {status} {'OK' if 200 <= status < 300 else 'FAILED'}")
    return 200 <= status < 300


def main() -> int:
    load_env_file()
    base = os.getenv("JIRA_BASE_URL", "").strip().rstrip("/")
    email = os.getenv("JIRA_EMAIL", "").strip()
    token = os.getenv("JIRA_API_TOKEN", "").strip()
    project = os.getenv("JIRA_PROJECT_KEY", "OM2").strip() or "OM2"
    site = os.getenv("JIRA_SITE_URL", "").strip()
    if not (base.startswith("https://api.atlassian.com/ex/jira/") and email and token):
        print("Configure JIRA_BASE_URL (scoped-token API gateway), JIRA_EMAIL, and JIRA_API_TOKEN in .env.")
        return 2

    auth = "Basic " + base64.b64encode(f"{email}:{token}".encode("utf-8")).decode("ascii")
    ok = True
    project_result = request(base, auth, "GET", f"/rest/api/3/project/{urllib.parse.quote(project)}")
    ok &= report(f"Project {project}", project_result)

    versions_result = request(base, auth, "GET", f"/rest/api/3/project/{urllib.parse.quote(project)}/versions")
    ok &= report("Project versions", versions_result)
    if versions_result[0] and 200 <= versions_result[0] < 300 and isinstance(versions_result[1], list):
        print(f"Versions visible: {len(versions_result[1])}")

    issues_result = request(base, auth, "POST", "/rest/api/3/search/jql", {
        "jql": f"project = {project}", "maxResults": 1, "fields": ["key", "status"]
    })
    ok &= report("Issue search", issues_result)
    if issues_result[0] and 200 <= issues_result[0] < 300 and isinstance(issues_result[1], dict):
        print(f"Matching OM2 issues returned: {len(issues_result[1].get('issues', []))}")

    board_id = os.getenv("JIRA_BOARD_ID", "172")
    print(f"Using configured Scrum board ID: {board_id}")
    sprints_result = request(base, auth, "GET", f"/rest/agile/1.0/board/{urllib.parse.quote(board_id)}/sprint?state=active&maxResults=1")
    ok &= report("Sprint access", sprints_result)
    if sprints_result[0] and 200 <= sprints_result[0] < 300 and isinstance(sprints_result[1], dict):
        print(f"Active sprints: {len(sprints_result[1].get('values', []))}")

    if site:
        print("Jira site link configured:", site.startswith("https://"))
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
