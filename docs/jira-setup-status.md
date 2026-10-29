# Jira setup status

The visible Jira browser was used to create the Scrum team-managed project and
configure its board. The project is **E-Commerce Feature Rollout & Order
Management 2** (`OM2`), with board ID `172`.

## Completed in Jira

- Project and Scrum board created on `vaibhvendra-vusimq0m.atlassian.net`.
- Board columns: To Do, In Progress, In Review, QA, Done.
- Epics created: Order Creation & Checkout (OM2-1), Payment Processing
  (OM2-2), Inventory & Fulfillment (OM2-3), Shipping & Tracking (OM2-4),
  Release Management & Feature Flags (OM2-5), Jira Integration & Dashboard
  (OM2-6), QA & Release Readiness (OM2-7), and Cancellation, Returns & Refunds
  (OM2-8).
- The visible work item form exposes Epic, Feature, Task, Story, Bug, and
  Sub-task. Story point estimate was inspected as `customfield_10016`.
- Three empty sprints were created by Jira: OM2 Sprint 1, OM2 Sprint 2, and
  OM2 Sprint 3.

## Still to configure

- Stories, acceptance criteria, points, priorities, labels, QA tasks, and
  dependencies have not yet been created/assigned in Jira. The full source
  backlog is in [`../jira/seed.json`](../jira/seed.json).
- The browser-visible team-managed project settings expose sprints and story
  point estimation but no Releases page. The direct project Releases route
  returned Jira's `not-found` page. Consequently, versions and sprint goals are
  not claimed as configured.
- `jira/rest-fallback.py` can create missing versions and set up missing sprint
  names/goals using Jira REST. It was not run. Existing sprint names are checked
  first, making reruns safe. Use `--dry-run` before applying changes.
- The optional invite step was skipped; no teammates were invited.

## REST fallback credentials

If you want to run the fallback, generate an Atlassian API token from your
Atlassian account security settings, then put it in a local `.env` (already
ignored by Git) or export the variables in your shell. Keep `.env.example` as a
placeholder only:

```sh
export JIRA_BASE_URL=https://vaibhvendra-vusimq0m.atlassian.net
export JIRA_EMAIL=your-atlassian-login@example.com
export JIRA_API_TOKEN=your-local-token
python3 jira/rest-fallback.py --dry-run
python3 jira/rest-fallback.py
```

The fallback does not create stories, link dependencies, or assign work to
sprints; those require the backlog tickets to exist first.
