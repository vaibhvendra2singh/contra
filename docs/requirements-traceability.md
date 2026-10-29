# Requirements Traceability and Final Check

The original Submission 1 file was not present in the workspace. This matrix traces the explicit project brief; replace or extend it with the exact Submission 1 wording before submission.

## Order management app

| Requirement | Status | Evidence |
| --- | --- | --- |
| Valid order state machine, timestamps, actor, invalid transition rejection | Done | `Order`, `OrderStateMachineTest`, test report |
| Payment failure and configurable retry limit | Done | `OrderManagementService`, `MockPaymentGateway`, integration tests |
| Catalog, inventory allocation/release, checkout | Done | `CatalogController`, `OrderManagementService`, integration tests |
| Two mock payment methods, capture, retry, refund | Done | CARD/WALLET gateway and payment records; integration tests |
| Admin fulfillment, shipping/tracking, customer tracking | Done | Admin and customer APIs/UI; demo order |
| Cancellation and self-service returns/refunds | Done | Order/customer APIs and UI; lifecycle integration test |
| Customer timeline, admin status/stuck/failed dashboard | Done | Order transition records and dashboard endpoints |
| Simulated notification log | Done | `NotificationLog` and admin notification view |
| Database feature flags/admin control | Done | `FeatureFlag` entity/repository and admin UI |
| Jira release gate for flags | Done | Each seeded flag has a real Jira story and QA key; live Jira verification requires both Done before enablement. |
| Seed data, REST API docs, state/payment tests | Done | `SeedData`, OpenAPI, `mvn test -q` (12 passing) |

## Jira setup

| Requirement | Status | Evidence |
| --- | --- | --- |
| New Scrum project and OM2 board | Done | Project/board links in `jira-setup-status.md` |
| Eight requested epics | Done | OM2-1 through OM2-8 |
| 3–5 stories per epic with AC, Fibonacci points, priority, labels | Done | 25 unique stories with AC, points, priority, labels, sprint and release assignment; captured in live Jira and `jira/seed.json`. |
| Dev and QA tasks/subtasks; QA-task blocker link per story | Done | 9 development tasks and 25 QA subtasks; each story is blocked by its linked QA subtask. |
| Dependency links between payment, fulfillment, shipping, returns | Done | Payment → fulfillment → shipping → returns/refunds links are present in Jira. |
| Sprint goals, contents, Sprint 1 start | Done for setup | Sprint 1 active (6 stories/29 points); Sprint 2 (7/27) and Sprint 3 (12/60) are future. Execution/closeout remains. |
| v0.1, v0.2, v1.0 versions and story assignments | Done | Three live Jira versions exist with stories assigned. |
| DoD in Jira and Confluence spec | Partial | DoD/readiness checklist is in OM2-7 epic description; team-managed project has no project description field. No appropriate Confluence project space was available. Local spec is in `docs/spec.md`. |
| Jira sprint dashboard and weekly report | Done locally | Current factual snapshot and weekly report are in `docs/`; no Confluence project space was available for publishing. |
| Meeting-notes task capture ready | Partial | Skill/workflow instructions are available; no client notes have been supplied or processed. |

## App–Jira integration

| Requirement | Status | Evidence |
| --- | --- | --- |
| Jira client interface, mockable/offline-safe | Done | `JiraClient`, `OfflineJiraClient`, unit tests |
| Selected release readiness with statuses, points, blockers, verdict | Done | Live endpoint verified for v1.0: 60 total points, 0 done, NOT READY with open-work and blocker reasons. |
| Controlled flag rollout requires completed story and QA | Done | Real story/QA keys are mapped; app fails closed unless both Jira issues are Done. |
| Deduplicated auto-bug after payment max retries or stuck order threshold | Implemented; external create not exercised | Jira connected and REST create/dedup path is implemented; avoiding a synthetic live bug. Mock-boundary tests pass. |
| Jira panel on admin dashboard | Done | Live panel verified: Sprint 1, 0/29 points, six To Do issues and active board link. |
| Optional webhook | Not implemented | Optional requirement. |

## Agile execution and submission materials

| Requirement | Status | Evidence |
| --- | --- | --- |
| Real sprint issue movement, closeout, carryover | In progress | Sprint 1 is active; Jira stories remain To Do. Finish real work and close out/roll over in Jira before claiming delivery. |
| Sprint review and retrospective records | Done as factual status records | Sprint documents state that no Jira sprint ran and record observations/actions. |
| Planned vs completed points / burndown | Partial | Planned seed points and actual Jira zero recorded; true Jira estimates and burndown history unavailable. |
| Test report mapped to stories | Done | `test-report.md` records 12 passing tests. |
| Screenshot checklist | Done | `evidence-checklist.md` |
| Final report and presentation outline | Done | `final-report.md`, `presentation-outline.md`; Submission 1 exact findings still need insertion. |
| Single-command demo and lifecycle demo mode | Done | `./scripts/start.sh`; verified demo order reaches DELIVERED. |
