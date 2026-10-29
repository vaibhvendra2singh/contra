# Jira Setup Status

**Live readback:** 2026-10-29. Scrum team-managed project **E-Commerce Feature Rollout & Order Management 2** (`OM2`).

- [Project](https://vaibhvendra-vusimq0m.atlassian.net/jira/software/projects/OM2) · [Board](https://vaibhvendra-vusimq0m.atlassian.net/jira/software/projects/OM2/boards/172) · [Backlog](https://vaibhvendra-vusimq0m.atlassian.net/jira/software/projects/OM2/boards/172/backlog)
- **67/67 Jira work items are Done:** 8 epics, 25 unique stories, 9 development tasks, and 25 linked QA subtasks. All stories have acceptance criteria, Fibonacci points, priority, labels, release version and sprint assignment. Every story is blocked by its linked QA subtask; QA comments contain the test evidence.
- Board columns: To Do, In Progress, In Review, QA, Done. Story estimate field: `customfield_10016`.
- Versions: v0.1 Order Core, v0.2 Fulfillment & Tracking, v1.0 Returns, Refunds & Release Gating. The live app reports READY for all three versions with 29/29, 27/27, and 60/60 points respectively.
- All three sprints are closed at 100%: Sprint 1 6/6 stories and 29/29 points; Sprint 2 7/7 and 27/27; Sprint 3 12/12 and 60/60. No carryover items remain.
- The Jira records were populated after the code implementation. The sprint dates are real Jira timestamps, but these short closeouts are verification/catch-up records, not a normal multi-week team's historical cadence. Do not present them as evidence of normal velocity or a historical daily burndown.
- Payment → Inventory/Fulfillment → Shipping → Returns/Refunds dependencies are present. Definition of Done and release-readiness checklist are in Epic OM2-7.
- Runtime credentials are in ignored `.env` only (permissions 0600). Live Jira reads, version readiness, and board sprint data were verified through the app.

## Confluence

Created project space **OM2 — Contra E-Commerce Feature Rollout & Order Management**. The canonical specification is published at [Product Specification](https://vaibhvendra-vusimq0m.atlassian.net/wiki/spaces/OM2/pages/16646145/Product+Specification+Order+Management+and+Safe+Feature+Rollout); the [weekly status and release readiness report](https://vaibhvendra-vusimq0m.atlassian.net/wiki/spaces/OM2/pages/16678913/Weekly+Status+and+Release+Readiness+2026-10-29) is also published there.

## Submission items outside the live project

- Exact client findings from Submission 1 were not supplied, so the report uses only the discovery statement in the brief.
- Capture the screenshots listed in `docs/evidence-checklist.md` from the teacher's browser view. No synthetic auto-bug was created in the live Jira project; bug creation/deduplication is verified with an isolated local HTTP test.
- No teammates were invited or assigned; add real team members if the instructor requires named ownership.
