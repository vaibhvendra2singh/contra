# Requirements Traceability and Final Check

The original Submission 1 file was not in the workspace, so this matrix traces the explicit project brief and its requirements. Replace or extend it with every exact Submission 1 requirement before submission.

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
| Jira release gate for flags | Partial | Story and QA keys are checked; Jira QA issue keys are not configured, so enabling a disabled flag is blocked. |
| Seed data, REST API docs, state/payment tests | Done | `SeedData`, OpenAPI, `mvn test -q` (11 passing) |

## Jira setup

| Requirement | Status | Evidence |
| --- | --- | --- |
| New Scrum project and OM2 board | Done | Project/board links in `jira-setup-status.md` |
| Eight requested epics | Done | OM2-1 through OM2-8 |
| 3–5 stories per epic with AC, Fibonacci points, priority, labels | Partial | 25 story work items exist for 24 desired stories; OM2-24 duplicates OM2-23. Jira AC, points, labels, and priority values need completion. |
| Dev and QA tasks/subtasks; QA-task blocker link per story | Not done | No tasks/subtasks or issue-to-QA links yet. |
| Dependency links between payment, fulfillment, shipping, returns | Not verified / pending | Seed defines desired links; live links were not confirmed. |
| Sprint goals, contents, Sprint 1 start | Not done | Three empty, unstarted sprints. |
| v0.1, v0.2, v1.0 versions and story assignments | Not done | Release versions unavailable in visible team-managed project UI; REST fallback is documented but not run. |
| DoD in Jira description and Confluence spec | Not done | Local documents exist; not entered/published in Jira/Confluence. |
| Jira sprint dashboard and weekly report | Partial | Snapshot and weekly report saved locally; not published as a live Jira dashboard or Confluence page. |
| Meeting-notes task capture ready | Partial | Skill/workflow instructions are available; no client notes have been supplied or processed. |

## App–Jira integration

| Requirement | Status | Evidence |
| --- | --- | --- |
| Jira client interface, mockable/offline-safe | Done | `JiraClient`, `OfflineJiraClient`, unit tests |
| Selected release readiness with statuses, points, blockers, verdict | Partial | UI and endpoint support v0.1/v0.2/v1.0; live Jira and dependency-field behavior require credentials and verification. |
| Controlled flag rollout requires completed story and QA | Partial | Enforced in service; QA issue keys currently absent. |
| Deduplicated auto-bug after payment max retries or stuck order threshold | Implemented, live verification pending | Best-effort Jira REST path and duplicate search; no Jira token in local environment to execute it. |
| Jira panel on admin dashboard | Partial | Live panel implemented; current sprints are empty and credentials not configured. |
| Optional webhook | Not implemented | Optional requirement. |

## Agile execution and submission materials

| Requirement | Status | Evidence |
| --- | --- | --- |
| Real sprint issue movement, closeout, carryover | Not done | Jira sprints remain unstarted. No completed Jira work has been claimed. |
| Sprint review and retrospective records | Done as factual status records | Sprint documents state that no Jira sprint ran and record observations/actions. |
| Planned vs completed points / burndown | Partial | Planned seed points and actual Jira zero recorded; true Jira estimates and burndown history unavailable. |
| Test report mapped to stories | Done | `test-report.md` records 11 passing tests. |
| Screenshot checklist | Done | `evidence-checklist.md` |
| Final report and presentation outline | Done | `final-report.md`, `presentation-outline.md`; Submission 1 exact findings still need insertion. |
| Single-command demo and lifecycle demo mode | Done | `./scripts/start.sh`; verified demo order reaches DELIVERED. |
