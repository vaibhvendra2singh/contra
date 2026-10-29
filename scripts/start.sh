#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

if ! command -v java >/dev/null 2>&1; then
  echo "Java 17 or later is required. Install a JDK, then run this script again." >&2
  exit 1
fi
JAVA_VERSION="$(java -version 2>&1 | sed -n '1s/.*version "\([0-9]*\).*/\1/p')"
if [[ -z "$JAVA_VERSION" || "$JAVA_VERSION" -lt 17 ]]; then
  echo "Java 17 or later is required. Detected: $(java -version 2>&1 | head -n 1)" >&2
  exit 1
fi
if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven 3.8 or later is required. Install Maven, then run this script again." >&2
  exit 1
fi
MAVEN_VERSION="$(mvn -version 2>&1 | sed -n '1s/^Apache Maven \([0-9]*\)\.\([0-9]*\).*/\1 \2/p')"
read -r MAVEN_MAJOR MAVEN_MINOR <<< "$MAVEN_VERSION"
if [[ -z "${MAVEN_MAJOR:-}" || "$MAVEN_MAJOR" -lt 3 || ( "$MAVEN_MAJOR" -eq 3 && "$MAVEN_MINOR" -lt 8 ) ]]; then
  echo "Maven 3.8 or later is required. Detected: $(mvn -version 2>&1 | head -n 1)" >&2
  exit 1
fi

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi
mkdir -p "$(dirname "${DB_FILE:-./data/oms}")"
export DEMO_MODE="${DEMO_MODE:-true}"

cat <<INFO
Starting Contra Order Management in demo mode: ${DEMO_MODE}
Open the app:       http://localhost:${SERVER_PORT:-8080}
API documentation:  http://localhost:${SERVER_PORT:-8080}/swagger-ui.html
Stop the app:       press Ctrl+C
INFO
exec mvn spring-boot:run
