#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🔄 Reverting frontend to Version 1..."

cp -rf "$PROJECT_ROOT/backups/frontend-v1/"* "$PROJECT_ROOT/src/main/resources/static/"

if [[ -d "$PROJECT_ROOT/target/classes/static" ]]; then
  cp -rf "$PROJECT_ROOT/backups/frontend-v1/"* "$PROJECT_ROOT/target/classes/static/"
fi

echo "✅ Frontend successfully restored to Version 1!"

