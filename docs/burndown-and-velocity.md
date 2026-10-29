# Burndown and Velocity

**Jira readback date:** 2026-10-29. OM2 Sprint 1, Sprint 2, and Sprint 3 each had zero assigned work items and none had started. The 25 Jira stories were To Do, unassigned, and displayed no estimates. Thus Jira has no actual sprint points or burndown history.

The source backlog provides planning estimates, but these are not estimates entered in Jira and are shown separately from actual completed points:

| Sprint | Planned points from `jira/seed.json` | Jira assigned points | Jira completed points | Jira outcome |
| --- | ---: | ---: | ---: | --- |
| Sprint 1 | 29 | Not set | 0 | Not started |
| Sprint 2 | 27 | Not set | 0 | Not started |
| Sprint 3 | 57 | Not set | 0 | Not started |

**Velocity cannot be calculated from completed Jira sprints.** The 0 completed points above means there are no Done points recorded in these unstarted Jira sprints; it is not evidence that the team delivered zero software. The repository has working features and 11 passing tests, but they were not tracked as Jira sprint completions.

**Burndown chart unavailable:** Jira has no sprint start/end dates, assigned scope, estimates, or daily scope/status snapshots. Do not infer a line from the planned seed data.

To produce a real burndown and velocity report, configure estimates and versions, assign stories to a sprint, start it, and capture Jira sprint reports at closeout.
