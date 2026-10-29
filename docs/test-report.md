# Test Report

**Run:** `mvn test -q`, 2026-10-29. **Result:** 17 tests, 0 failures, 0 errors, 0 skipped. Source targets Java 17; tests ran on Java 27.

| Test area | Cases | Result |
| --- | ---: | --- |
| Order state machine valid/invalid transitions | 4 | PASS |
| Jira integration, feature gating, readiness and offline handling | 6 | PASS |
| Spring/H2 lifecycle, checkout, payments, inventory, fulfillment, tracking and refunds | 5 | PASS |
| Jira REST bug creation and duplicate avoidance (local HTTP mock) | 2 | PASS |

Coverage includes: valid and invalid status paths, retry limits, CARD/WALLET, multi-line totals, inventory allocation/release, fulfillment queue, persisted tracking, notifications, cancellation and return refunds, seed Jira mappings, feature-flag done/QA gates, fail-closed offline behavior, readiness totals/blockers, and Jira bug creation/deduplication through an isolated HTTP mock.

## Test cases mapped to Jira stories

| Test case or test group | Jira story keys | Result |
| --- | --- | --- |
| Full state path and actor/timestamp timeline | OM2-11, OM2-25, OM2-32, OM2-33 | PASS |
| Payment retry maximum and invalid transition rejection | OM2-11, OM2-13, OM2-25 | PASS |
| Cancellation allowed before shipping, blocked after shipping; release and refund | OM2-17, OM2-31 | PASS |
| Successful checkout, inventory allocation, cancellation side effects | OM2-9, OM2-14, OM2-15, OM2-17, OM2-31 | PASS |
| Decline, retry, and inventory allocation only after capture | OM2-12, OM2-13, OM2-15 | PASS |
| Fulfillment queue, shipping, tracking, delivery, notifications, return/refund | OM2-16, OM2-18, OM2-19, OM2-20, OM2-32, OM2-33 | PASS |
| WALLET checkout and exact multi-line totals | OM2-9, OM2-10, OM2-12 | PASS |
| Seeded story/QA mappings persist for flags | OM2-21, OM2-23 | PASS |
| Flag enable requires linked story and QA to be Done; offline fails closed | OM2-22, OM2-23, OM2-24 | PASS |
| Readiness groups, points, blockers and reasons | OM2-26, OM2-27 | PASS |
| Jira unavailable behavior and best-effort operations | OM2-24, OM2-30 | PASS |
| Auto-bug creation payload and open-bug deduplication via local HTTP mock | OM2-29, OM2-30 | PASS |


Separately, the live Jira connection was verified through the app for project/issue reads, versions, sprint progress and release readiness. The app returned Sprint 1 29/29, Sprint 2 27/27, Sprint 3 60/60, and READY for all three release versions after closure. No synthetic operational Bug was created in the real Jira project.

This is automated test evidence; it is not a substitute for every requested manual screenshot or the missing exact Submission 1 client findings.
