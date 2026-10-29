# Sprint 1 Review — Order Core and Payments

**Jira outcome:** Closed 2026-10-29. Six stories (OM2-9–OM2-14), 29 planned points, all Done. Each linked QA subtask (OM2-34–OM2-39) is Done with test evidence comments. The sprint goal was order creation, checkout, payment capture and retry paths.

**Evidence:** `mvn test -q` passed 12 tests (0 failures). The coverage includes order creation and inventory allocation, exact multi-line totals, order timeline actor/timestamps, CARD and WALLET checkout, decline/retry limits, and capture/refund records. Evidence is on the linked Jira QA subtasks and in `docs/test-report.md`.

**Process note:** Jira tracking was added after implementation, so the sprint was closed once the implemented scope was tested and caught up in Jira. The short Jira interval is not a representative team velocity measure. No daily burndown history exists.

**Review outcome:** Sprint 1 goal scope is accepted against repository and automated test evidence; no carryover items.
