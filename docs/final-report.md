# E-Commerce Feature Rollout and Order Management System

**Course:** Agile Software Development (CSET455) · **Team:** Contra

## Problem statement

E-commerce teams must ship checkout, payments, tracking, and self-service returns while keeping live order processing reliable. When development and operations are tracked separately, payment failures, inventory allocation, delayed refunds, hidden dependencies, and release slippage can go unnoticed.

## Research gap and product response

The project addresses the gap between Agile delivery visibility and operational order visibility. It combines a working order lifecycle with a Jira release view, guarded feature flags, readiness checks, and best-effort operational bug reporting. Dependencies and QA evidence are first-class requirements in the specification and Definition of Done.

## Client discovery

The project brief records the client discovery finding: this is not just a storefront; it must coordinate the complete order lifecycle with feature rollout, dependencies, testing, and release readiness. The original Submission 1 file was not in the workspace, so exact research methods, quotations, and client findings must still be inserted from that source before hand-in.

## Agile process followed

Implementation proceeded in repository phases: state machine and persistence; core operations and UI; Jira project and backlog setup; Jira integration; and demo/docs. The implementation was delivered before most Jira tracking was configured, so the issue history does not mirror the coding sequence. All three Jira sprints are closed with all assigned stories Done: 29/29, 27/27 and 60/60 points. The Jira records were caught up after code implementation; these short verification sessions do not represent normal multi-week sprint delivery or a reliable velocity baseline. No daily burndown history exists. Current Jira and app readbacks are recorded in `jira-setup-status.md`, `sprint-plan.md`, and `jira-sprint-dashboard.md`.

## Architecture

The browser frontend calls Spring Boot REST controllers. The order service applies domain state transitions and writes through Spring Data JPA to a file-backed H2 database. A mock payment gateway and simulated notifications support the demo. Jira integration is isolated behind `JiraClient`, with Cloud REST and offline implementations so order processing can continue without Jira.

## Jira structure

Jira project **E-Commerce Feature Rollout & Order Management 2** (`OM2`) uses Scrum board 172 and eight epics. Its specification is published in the new OM2 Confluence space. The live project has eight epics, 25 unique stories, 25 linked QA subtasks, and nine development tasks. All stories have acceptance criteria, estimates, priorities, labels, sprint assignments, and release versions. The live Jira project has 8 epics, 25 stories, 9 development tasks and 25 linked QA subtasks; all are Done. All three sprint scopes are closed at 100%. See `jira-setup-status.md` for exact state.

## Results

- The application supports checkout, CARD/WALLET mock payment, retry, inventory, fulfillment, shipping/tracking, cancellation, return/refund, feature flags, and notification logging.
- The admin screen combines order metrics, Jira sprint progress, and selected-version readiness.
- Readiness shows status groups, points, blockers, and reasons. Live endpoints report READY for all versions: v0.1 29/29, v0.2 27/27, and v1.0 60/60; unavailable Jira fails closed with NOT READY.
- Operational bug reporting is best-effort and checks for an existing open issue before creating another.
- `mvn test -q`: 17 tests passed, 0 failures, 0 errors. The test report maps them to stories.
- The one-command demo script starts the app and seeds a delivered order on first run.

## Limitations

- Jira work and release versions are complete for this demonstration. Sprint statuses were caught up after implementation, and issues moved directly from To Do to Done once QA passed; this is not a historical normal-length Scrum workflow. Runtime Jira requires the configured API token in ignored `.env`.
- Live Jira project, versions, sprint progress and readiness were verified through the app. Bug create/deduplication was tested through a local HTTP mock; no synthetic operational bug was created in the live project.
- No production payment gateway, authentication/authorization, carrier integration, or production monitoring is included.
- Exact Submission 1 findings must be inserted from the original source.

## Future work

For a future project, plan in Jira before implementation and record workflow movement as it happens. Add exact Submission 1 evidence and capture the required screenshots. Add authentication, carrier/payment adapters, and production monitoring before any real deployment.
