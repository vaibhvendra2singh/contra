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
| Seed data, REST API docs, state/payment tests | Done | `SeedData`, OpenAPI, `mvn test -q` (17 passing) |

## Jira setup

| Requirement | Status | Evidence |
| --- | --- | --- |
| New Scrum project and OM2 board | Done | Project/board links in `jira-setup-status.md` |
| Eight requested epics | Done | OM2-1 through OM2-8 |
| 3–5 stories per epic with AC, Fibonacci points, priority, labels | Done | 25 unique stories with AC, points, priority, labels, sprint and release assignment; captured in live Jira and `jira/seed.json`. |
| Dev and QA tasks/subtasks; QA-task blocker link per story | Done | 9 development tasks and 25 QA subtasks; each story is blocked by its linked QA subtask. |
| Dependency links between payment, fulfillment, shipping, returns | Done | Payment → fulfillment → shipping → returns/refunds links are present in Jira. |
| Sprint goals, contents, Sprint 1 start | Done for setup | All three sprints are closed: 6/6 (29 points), 7/7 (27 points), and 12/12 (60 points) Done, with QA evidence comments. |
| v0.1, v0.2, v1.0 versions and story assignments | Done | Three live Jira versions exist with stories assigned. |
| DoD in Jira and Confluence spec | Partial | DoD/readiness checklist is in OM2-7 epic description and local docs; team-managed project settings expose no project description field. The specification is published in Confluence OM2. |
| Jira sprint dashboard and weekly report | Done | Snapshot saved in `docs/jira-sprint-dashboard.md`; weekly report saved locally and will be published in Confluence OM2. |
| Meeting-notes task capture ready | Partial | Skill/workflow instructions are available; no client notes have been supplied or processed. |

## App–Jira integration

| Requirement | Status | Evidence |
| --- | --- | --- |
| Jira client interface, mockable/offline-safe | Done | `JiraClient`, `OfflineJiraClient`, unit tests |
| Selected release readiness with statuses, points, blockers, verdict | Done | Live endpoints report READY: v0.1 29/29, v0.2 27/27, v1.0 60/60, with no open blockers. |
| Controlled flag rollout requires completed story and QA | Done | Real story/QA keys are mapped; app fails closed unless both Jira issues are Done. |
| Deduplicated auto-bug after payment max retries or stuck order threshold | Done in code/tests | REST create/dedup logic is covered by local HTTP mock tests; no synthetic operational Bug was created in live Jira. |
| Jira panel on admin dashboard | Done | Live panel reads the latest closed sprint, Jira issue statuses and points; recheck after latest build restart. |
| Optional webhook | Not implemented | Optional requirement. |

## Agile execution and submission materials

| Requirement | Status | Evidence |
| --- | --- | --- |
| Sprint issue movement, closeout, carryover | Partial | All 3 sprints are closed with all assigned work Done and no carryover. Code preceded Jira; items were caught up directly to Done after QA instead of recording historical In Progress/In Review/QA transitions, so this is not a normal multi-week sprint history. |
| Sprint review and retrospective records | Done | `sprint-1..3-review.md` and `sprint-1..3-retro.md` record actual Jira closure, QA evidence, and the tracking retrofit limitation. |
| Planned vs completed points / burndown | Partial | Jira records 29/29, 27/27, and 60/60 points complete; daily history is unavailable, so no factual burndown chart can be reconstructed. |
| Test report mapped to stories | Done | `test-report.md` records 17 passing tests. |
| Screenshot checklist | Done | `evidence-checklist.md` |
| Final report and presentation outline | Done | `final-report.md`, `presentation-outline.md`; exact Submission 1 findings and manual screenshots still need insertion. |
| Single-command demo and lifecycle demo mode | Done | `./scripts/start.sh`; verified demo order reaches DELIVERED. |
