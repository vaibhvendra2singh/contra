# Sprint 1 Retrospective — Order Core and Payments

This is a process retrospective from the project work to date; Jira Sprint 1 itself did not run.

## What went well

- The order state machine enforces allowed transitions in the domain layer.
- Payment and order behavior has both state-machine and Spring integration coverage.
- The mock payment gateway makes decline and retry cases reproducible.

## What did not go well

- Jira was created after implementation had already begun, so working code was not tracked as sprint progress.
- Planned stories lack estimates, sprint assignment, detailed Jira acceptance criteria, and linked QA tasks.
- The backlog contains duplicate feature-flag story OM2-24 alongside OM2-23.

## Actions

1. Correct the duplicate and complete story descriptions, estimates, and labels before Sprint 1 starts.
2. Create and link QA tasks; record actual test evidence against the stories.
3. Start Sprint 1 only after its scope and goal are visible in Jira; update status as work moves through review and QA.
