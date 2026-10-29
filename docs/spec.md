# E-Commerce Feature Rollout and Order Management System

**Course:** Agile Software Development (CSET455)  
**Team:** Contra  
**Product:** Order Management and Safe Feature Rollout  
**Stack:** Java 17, Spring Boot 3, Spring Data JPA, H2, HTML/CSS/JavaScript, JUnit 5, OpenAPI, Jira Cloud

## Problem statement

E-commerce teams need to deliver checkout, payment, shipping, tracking, and self-service return features while keeping live order processing reliable. When delivery work is disconnected from order operations, payment failures and inventory changes can become hard to diagnose, fulfillment can be delayed, returns can be missed, and releases can ship before their dependencies and tests are ready.

This product joins an order lifecycle application with an Agile release workflow. It treats dependency visibility, quality checks, release readiness, and feature rollout as part of the operational system rather than as separate documentation.

## Stakeholders

- **Customers:** place orders, choose a payment method, track delivery, cancel eligible orders, and request returns.
- **Admins and order teams:** monitor order health, allocate and release inventory, fulfill orders, record tracking, and operate feature flags.
- **Support teams:** inspect order timelines and payment outcomes and help resolve cancellations, returns, and refunds.
- **Developers:** implement changes and connect operational events to Jira delivery work.
- **Testers:** verify lifecycle transitions, regression safety, dependencies, and release readiness.

## Goals

1. Provide a working order-management application from checkout through delivery, cancellation, return, and refund.
2. Organize implementation in a Scrum Jira project with epics, stories, tasks, estimates, sprints, versions, and visible dependencies.
3. Connect Jira feature work and release evidence to live operational behavior, while keeping the application usable when Jira is unavailable.
4. Make readiness evidence and the Agile process reviewable for the CSET455 submission.

## In scope

### Order lifecycle

The normal path is `CREATED → PAYMENT_PENDING → PAID → INVENTORY_ALLOCATED → FULFILLING → SHIPPED → DELIVERED`.

Supported side paths:

- Payment failures enter `PAYMENT_FAILED`; a customer may retry up to the configured limit.
- Cancellation is available before shipping. It releases allocated inventory and issues a refund when a captured payment exists.
- A delivered order may move to `RETURN_REQUESTED → RETURNED → REFUNDED`.
- Every transition records its prior and next status, actor, and timestamp. Invalid transitions are rejected.

### Order operations

- Product catalog with stock, allocated quantity, and available quantity.
- Checkout and order creation with at least two mock payment methods.
- Payment attempt, failure, retry, capture, and refund records.
- Admin fulfillment queue, tracking number assignment, and customer tracking timeline.
- Dashboard with order counts by status, stuck orders, failed payments, fulfillment queue, and notification log.
- Database-backed feature flags that administrators can toggle; Jira-based release gating is a subsequent integration phase.
- Seed data, REST endpoints, and OpenAPI documentation.

### Delivery management

- Jira is the source of truth for feature progress, QA, blockers, sprint outcomes, and planned releases.
- A release may be considered ready only when feature stories are complete, linked QA work has passed, tests are green, and there are no open blocking bugs or unresolved dependencies.
- Feature flags map to Jira feature work and must be gated by completed implementation and QA evidence when the Jira integration is enabled.
- Operational failures may produce deduplicated Jira bugs with order context.

## Quality and safety requirements

- State changes are validated in the domain model, not only in the interface.
- Payment and inventory changes must be transactionally consistent.
- Concurrent checkout must not allocate more inventory than is available.
- Jira calls are isolated behind an interface and must not stop core order processing when Jira is offline, rate-limited, or misconfigured.
- Secrets are read from environment configuration. Real credentials are never committed.
- The demo payment gateway is simulated and never collects or transmits real card or wallet credentials.
- Important order and feature actions are visible in the application and Jira evidence.

## Acceptance outcomes

1. Customers can create an order using card or wallet and view the resulting timeline.
2. A simulated payment decline is visible, retryable within the configured limit, and does not consume inventory until captured.
3. Only valid state transitions succeed; invalid transitions return an error and leave the order unchanged.
4. Inventory allocation, cancellation release, shipment consumption, and return restocking remain consistent.
5. Admins can fulfill and ship eligible orders, record tracking, and complete return/refund operations.
6. Status changes create simulated notification log entries.
7. Administrators can manage database-backed feature flags.
8. Jira readiness displays progress, points, blockers, and a reasoned READY or NOT READY result.
9. Jira integration failures do not prevent checkout, fulfillment, or customer tracking.
10. The Jira backlog, sprint plan, release versions, QA work, and dependencies can be read back and verified.

## Assumptions and limitations

- The payment gateway and customer notifications are simulated for a college demonstration.
- The application is a local demonstrator and does not yet include production identity management, tax, shipping-carrier APIs, or real payment processing.
- Client discovery details from Submission 1 will be incorporated into the final report when that submission is available in the workspace.
- Jira project creation uses the connected team-managed software project on the account's existing Atlassian site.
