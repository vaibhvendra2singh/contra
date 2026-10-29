# E-Commerce Feature Rollout and Order Management System

**Course:** Agile Software Development (CSET455) · **Team:** Contra

## Problem statement

E-commerce teams must ship checkout, payments, tracking, and self-service returns while keeping live order processing reliable. When development and operations are tracked separately, payment failures, inventory allocation, delayed refunds, hidden dependencies, and release slippage can go unnoticed.

## Research gap and product response

The project addresses the gap between Agile delivery visibility and operational order visibility. It combines a working order lifecycle with a Jira release view, guarded feature flags, readiness checks, and best-effort operational bug reporting. Dependencies and QA evidence are first-class requirements in the specification and Definition of Done.

## Client discovery

The supplied project brief records the key finding: this is not just a storefront; it must coordinate the complete order lifecycle with feature rollout, dependencies, testing, and release readiness. The original Submission 1 document was not available in the workspace during report generation. Replace this section with the exact research method, quotations, and client findings from that submission before hand-in.

## Agile process followed

Implementation proceeded in repository phases: state machine and persistence; core operations and UI; Jira project and backlog setup; Jira integration; and demo/docs. The code has phase commits and an 11-test passing suite. However, Jira was set up after substantial implementation work. Jira Sprint 1, Sprint 2, and Sprint 3 remain empty and unstarted, though each now has a goal; therefore this report does not claim sprint completion, velocity, burndown, or Jira Done stories. The planned sprint scope and actual Jira snapshot are recorded separately in `sprint-plan.md`, `burndown-and-velocity.md`, and `jira-sprint-dashboard.md`.

## Architecture

The browser frontend calls Spring Boot REST controllers. The order service applies domain state transitions and writes through Spring Data JPA to a file-backed H2 database. A mock payment gateway and simulated notifications support the demo. Jira integration is isolated behind `JiraClient`, with Cloud REST and offline implementations so order processing can continue without Jira.

## Jira structure

Jira project **E-Commerce Feature Rollout & Order Management 2** (`OM2`) uses Scrum board 172 and eight epics. The live issue search returned 33 items: eight epics and 25 stories, with one duplicate flag story (OM2-23/OM2-24). Board columns are To Do, In Progress, In Review, QA, Done. Three empty sprints exist with goals configured. Jira has no story estimates or version assignments at the snapshot time. See `jira-setup-status.md` for the exact project and board links.

## Results

- The application supports checkout, CARD/WALLET mock payment, retry, inventory, fulfillment, shipping/tracking, cancellation, return/refund, feature flags, and notification logging.
- The admin screen combines order metrics, Jira sprint progress, and selected-version readiness.
- Readiness shows status groups, points, blockers, and reasons; unavailable Jira produces NOT READY.
- Operational bug reporting is best-effort and checks for an existing open issue before creating another.
- `mvn test -q`: 11 tests passed, 0 failures, 0 errors. The test report maps them to stories.
- The one-command demo script starts the app and seeds a delivered order on first run.

## Limitations

- Jira stories, points, QA tasks, dependencies, versions, sprint goals, and sprint status remain incomplete. Runtime Jira requires an API token in ignored `.env`.
- No live Jira integration call or auto-created Jira bug was tested without credentials.
- No production payment gateway, authentication/authorization, carrier integration, or production monitoring is included.
- Exact Submission 1 findings must be inserted from the original source.

## Future work

Complete Jira grooming and sprint setup, map every flag to a QA issue, run and close the three sprints with real team participation, capture burndown/velocity evidence, and repeat release readiness against configured versions. Add authentication, carrier/payment adapters, and production monitoring before any real deployment.
