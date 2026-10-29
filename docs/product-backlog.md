# Product Backlog

Backlog derived from `jira/seed.json` and checked against Jira on 2026-10-29. Jira has 25 stories, including a distinct Jira-unavailable fail-closed flag story. All 25 stories have estimates, sprint assignments, priorities, labels and versions.

| Epic | Story | MoSCoW | Points | Priority | Target sprint | Target version |
| --- | --- | --- | ---: | --- | --- | --- |
| Order Creation & Checkout | As a customer, I want to review my cart and place an order so that I can begin a purchase with confidence. | Should | 5 | High | Sprint 1 | v0.1 |
| Order Creation & Checkout | As a customer, I want to see an accurate order total before payment so that I know what I will be charged. | Should | 3 | High | Sprint 1 | v0.1 |
| Order Creation & Checkout | As a customer, I want an order timeline so that I can understand progress without contacting support. | Could | 3 | Medium | Sprint 1 | v0.1 |
| Payment Processing | As a customer, I want to pay by card or wallet so that I can use my preferred payment method. | Must | 5 | Highest | Sprint 1 | v0.1 |
| Payment Processing | As a customer, I want to retry a declined payment within a configured limit so that a temporary payment problem does not lose my order. | Should | 5 | High | Sprint 1 | v0.1 |
| Payment Processing | As a support agent, I want captured payments and refunds recorded so that I can reconcile customer payment outcomes. | Should | 8 | High | Sprint 1 | v0.1 |
| Inventory & Fulfillment | As an admin, I want inventory allocated only after payment succeeds so that unpaid orders cannot reserve stock indefinitely. | Should | 5 | High | Sprint 2 | v0.2 |
| Inventory & Fulfillment | As an order team member, I want a fulfillment queue of allocated orders so that warehouse work is clear and actionable. | Should | 5 | High | Sprint 2 | v0.2 |
| Inventory & Fulfillment | As an order team member, I want canceled pre-shipment orders to release inventory so that sellable stock is restored. | Should | 3 | High | Sprint 2 | v0.2 |
| Shipping & Tracking | As an order team member, I want to add a tracking number when shipping an order so that delivery can be followed. | Should | 3 | High | Sprint 2 | v0.2 |
| Shipping & Tracking | As a customer, I want to look up my order and tracking status so that I can plan for delivery. | Should | 5 | High | Sprint 2 | v0.2 |
| Shipping & Tracking | As a customer, I want a notification for every order status change so that I do not miss important updates. | Could | 3 | Medium | Sprint 2 | v0.2 |
| Cancellation, Returns & Refunds | As a customer, I want to cancel an order before it ships so that I can stop an unwanted purchase. | Should | 3 | High | Sprint 2 | v0.2 |
| Cancellation, Returns & Refunds | As a customer, I want to request a return after delivery so that I can send back an item I do not want. | Should | 5 | High | Sprint 3 | v1.0 |
| Cancellation, Returns & Refunds | As a support agent, I want to receive returned items and issue a refund so that the return lifecycle can be completed and audited. | Should | 5 | High | Sprint 3 | v1.0 |
| Release Management & Feature Flags | As a developer, I want feature flags stored in the database so that rollout state survives an application restart. | Should | 3 | High | Sprint 3 | v1.0 |
| Release Management & Feature Flags | As an admin, I want to enable or disable a feature flag so that I can control rollout without a code deployment. | Should | 3 | High | Sprint 3 | v1.0 |
| Release Management & Feature Flags | As a release manager, I want a feature flag linked to its Jira story and QA task so that incomplete work cannot be rolled out. | Must | 5 | Highest | Sprint 3 | v1.0 |
| QA & Release Readiness | As a tester, I want automated lifecycle tests so that invalid state changes and payment regressions are caught before release. | Must | 5 | Highest | Sprint 3 | v1.0 |
| QA & Release Readiness | As a tester, I want readiness checks for a Jira version so that I can explain whether the release can ship. | Must | 8 | Highest | Sprint 3 | v1.0 |
| QA & Release Readiness | As a release manager, I want a Definition of Done checklist so that every release is assessed consistently. | Should | 5 | High | Sprint 3 | v1.0 |
| Jira Integration & Dashboard | As an admin, I want Jira release and sprint progress beside order metrics so that delivery work and live operations are visible together. | Should | 8 | High | Sprint 3 | v1.0 |
| Jira Integration & Dashboard | As a support lead, I want a Jira bug created for repeated payment failures or stuck orders so that operational risks are tracked with context. | Should | 5 | High | Sprint 3 | v1.0 |
| Jira Integration & Dashboard | As a developer, I want Jira calls behind a replaceable interface so that the app can be tested offline and remain operational during Jira outages. | Should | 5 | High | Sprint 3 | v1.0 |

| Release Management & Feature Flags | As a developer, I want feature flags to default to off when Jira is unavailable so that unverified features cannot affect live order flow. | Should | 3 | High | Sprint 3 | v1.0 |

## Planned total

116 story points across 25 stories. Jira sprint totals: Sprint 1 29, Sprint 2 27, Sprint 3 60.
