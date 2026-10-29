# Workspace instructions

## Project setup
- This workspace is a Java 17+ Spring Boot 3 application built with Maven. Read `README.md` and `docs/antigravity-first-run.md` before changing setup or demo behavior.
- Run `mvn test` after Java changes. The test suite uses H2 and an offline/mock Jira client; it does not prove live Jira behavior.
- Start the local app with `./scripts/start.sh`; it loads `.env`, uses the H2 file DB under `data/`, and defaults to the local demo fixture mode.

## Data accuracy
- `jira/seed.json` is the desired backlog plan. It is not evidence that those estimates, stories, tasks, links, versions, or sprint assignments exist in live Jira. Use `docs/jira-setup-status.md` and dated readbacks for live Jira facts.
- Keep planned values distinct from observed Jira values. Never invent completed issues, estimates, sprint velocity, burndown points, client research, or Gantt chart data. If a source is missing, record the gap.
- The seeded Contra catalog and optional delivered order are local demo fixtures, not real customer or merchant records. Do not describe them as client data. Payment capture is intentionally mocked for this course demo.

## Secrets and external changes
- Keep credentials only in ignored `.env`; never print, commit, or include tokens in reports or prompts. `.env.example` must remain blank of secrets.
- Do not make live Jira changes unless the user explicitly requests them. When authorized, verify the live result and update the Jira status snapshot honestly.
