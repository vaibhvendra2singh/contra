# Jira Setup Status

The project and backlog were created using the visible Jira browser on `vaibhvendra-vusimq0m.atlassian.net`.

## Project

- Name: **E-Commerce Feature Rollout & Order Management 2**
- Key: **OM2**
- Project type: Scrum team-managed
- Project URL: <https://vaibhvendra-vusimq0m.atlassian.net/jira/software/projects/OM2>
- Board URL: <https://vaibhvendra-vusimq0m.atlassian.net/jira/software/projects/OM2/boards/172>
- Backlog URL: <https://vaibhvendra-vusimq0m.atlassian.net/jira/software/projects/OM2/boards/172/backlog>

## Configured

- Board ID 172; columns To Do, In Progress, In Review, QA, Done.
- Eight epics: OM2-1 through OM2-8 for checkout, payments, inventory/fulfillment, shipping/tracking, feature flags, Jira integration, QA/release readiness, and returns/refunds.
- Jira issue type and story estimate field were inspected. The UI exposes Epic, Feature, Task, Story, Bug, and Sub-task; story point estimate field is `customfield_10016`.
- Three empty sprints exist: OM2 Sprint 1, OM2 Sprint 2, OM2 Sprint 3.
- 25 story-type work items OM2-9 through OM2-33 are in the backlog. OM2-24 duplicates the release flag story that is also OM2-23.

## Still incomplete

- Remove or repurpose duplicate OM2-24; the source `jira/seed.json` defines 24 stories.
- Stories currently have no story point estimates or detailed acceptance criteria entered in Jira; all are To Do and unassigned.
- Development/QA tasks and subtasks have not been created; stories are not linked to QA tasks.
- The three sprints are empty; sprint goals are not set and Sprint 1 is not started.
- Dependency issue links are not configured.
- Release versions v0.1, v0.2, and v1.0 are not configured. The visible team-managed project UI did not expose a Releases page.
- Definition of Done and release-readiness checklist still need to be entered in the Jira project description.
- No sprint dashboard or weekly Jira status report has been created; no Confluence spec page has been published.
- No teammates were invited.

The 404 previously shown was the invalid `/boards/172/not-found` route. The working backlog and board URLs above load; the board remains visually empty until stories are assigned to a sprint and Sprint 1 is started.

## REST fallback for versions and sprint goals

`jira/rest-fallback.py` is idempotent by version/sprint name and supports `--dry-run`. The sprint metadata is available in `jira/seed.json`. It has not been run because no API token is present in the workspace.

To configure it, generate an Atlassian API token from the account security settings and set these variables in the ignored `.env` file:

```sh
JIRA_BASE_URL=https://vaibhvendra-vusimq0m.atlassian.net
JIRA_EMAIL=your-atlassian-login@example.com
JIRA_API_TOKEN=your-local-token
```

Then run `python3 jira/rest-fallback.py --dry-run`, inspect the proposed changes, and run `python3 jira/rest-fallback.py` to apply. Never commit `.env` or print the token.
