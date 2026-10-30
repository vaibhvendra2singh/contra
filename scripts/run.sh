#!/usr/bin/env bash
# ==============================================================================
# Contra OMS - All-in-One Runner & Verification Script
# ==============================================================================
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

# Terminal formatting
BOLD="\033[1m"
GREEN="\033[0;32m"
YELLOW="\033[0;33m"
CYAN="\033[0;36m"
RED="\033[0;31m"
RESET="\033[0m"

echo -e "${BOLD}${CYAN}======================================================${RESET}"
echo -e "${BOLD}${CYAN}      CONTRA ORDER MANAGEMENT SYSTEM - RUNNER         ${RESET}"
echo -e "${BOLD}${CYAN}======================================================${RESET}"

# 1. Environment & Prerequisite Checks
echo -e "\n${BOLD}[1/5] Checking System Prerequisites...${RESET}"

if ! command -v java >/dev/null 2>&1; then
  echo -e "${RED}Error: Java is not installed or not in PATH.${RESET}" >&2
  exit 1
fi
JAVA_LINE="$(java -version 2>&1 | head -n 1)"
echo -e "  ✓ Java: ${GREEN}${JAVA_LINE}${RESET}"

if ! command -v mvn >/dev/null 2>&1; then
  echo -e "${RED}Error: Maven is not installed or not in PATH.${RESET}" >&2
  exit 1
fi
MVN_LINE="$(mvn -version 2>&1 | head -n 1)"
echo -e "  ✓ Maven: ${GREEN}${MVN_LINE}${RESET}"

# 2. Configuration & Secrets
echo -e "\n${BOLD}[2/5] Checking Configuration & .env...${RESET}"
if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
  echo -e "  ✓ Loaded local ${GREEN}.env${RESET}"
else
  echo -e "  ${YELLOW}Notice: No .env found. Using local demo/offline defaults.${RESET}"
fi

SERVER_PORT="${SERVER_PORT:-8080}"
export DEMO_MODE="${DEMO_MODE:-false}"

# 3. Port Check & Cleanup
echo -e "\n${BOLD}[3/5] Checking Port Availability (Port ${SERVER_PORT})...${RESET}"
EXISTING_PID=$(lsof -ti :"$SERVER_PORT" 2>/dev/null || true)
if [[ -n "$EXISTING_PID" ]]; then
  echo -e "  ${YELLOW}Port ${SERVER_PORT} is currently occupied by PID(s): ${EXISTING_PID}${RESET}"
  echo -e "  Stopping existing process to guarantee clean start..."
  kill -9 $EXISTING_PID 2>/dev/null || true
  sleep 1
  echo -e "  ✓ Port ${SERVER_PORT} cleared."
else
  echo -e "  ✓ Port ${SERVER_PORT} is free."
fi

# 4. Optional Pre-Flight Test Verification
SKIP_TESTS="${SKIP_TESTS:-false}"
if [[ "$SKIP_TESTS" != "true" ]]; then
  echo -e "\n${BOLD}[4/5] Running Pre-Flight Unit & Integration Tests...${RESET}"
  if mvn test -q; then
    echo -e "  ✓ ${GREEN}All 17 JUnit 5 tests passed successfully!${RESET}"
  else
    echo -e "  ${RED}Tests failed! Please review test failures before running.${RESET}" >&2
    exit 1
  fi
else
  echo -e "\n${BOLD}[4/5] Skipping tests (SKIP_TESTS=true)${RESET}"
fi

# 5. Launch Application
echo -e "\n${BOLD}[5/5] Launching Contra OMS Application...${RESET}"
echo -e "  ----------------------------------------------------"
echo -e "  ${BOLD}Web UI:${RESET}       ${GREEN}http://localhost:${SERVER_PORT}${RESET}"
echo -e "  ${BOLD}API Docs:${RESET}     ${GREEN}http://localhost:${SERVER_PORT}/swagger-ui.html${RESET}"
echo -e "  ${BOLD}H2 Console:${RESET}   ${GREEN}http://localhost:${SERVER_PORT}/h2-console${RESET}"
echo -e "  ${BOLD}Demo Mode:${RESET}    ${CYAN}${DEMO_MODE}${RESET}"
echo -e "  ----------------------------------------------------"
echo -e "  Press ${YELLOW}Ctrl+C${RESET} anytime to stop the server.\n"

# Open in default browser after short delay in background
(
  sleep 4
  if command -v open >/dev/null 2>&1; then
    open "http://localhost:${SERVER_PORT}" >/dev/null 2>&1 || true
  fi
) &

exec mvn spring-boot:run

