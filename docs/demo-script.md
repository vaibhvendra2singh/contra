# Five-Minute Teacher Demo

## Start before class

1. Install Java 17 or later and Maven 3.8 or later.
2. In Terminal, open the project folder and run `./scripts/start.sh`.
3. Wait for Spring Boot to print `Started OrderManagementApplication`.
4. Open `http://localhost:8080`. The first startup downloads dependencies and creates `data/oms`.
5. Keep Terminal open during the presentation. Use `Ctrl+C` to stop the app afterward.
6. Jira is optional for the app demo. Without Jira credentials the order app works in offline mode and readiness correctly reports missing live Jira data.

## Walkthrough (about five minutes)

1. **0:00–0:30 — Problem.** Explain that order processing must continue safely while e-commerce features are released. Point out the connection between order operations, QA, feature flags, and release readiness.
2. **0:30–1:15 — Catalog and checkout.** Show the seeded products, add a mug to the bag, and place an order using a name, email, `CARD`, and a blank payment token. A blank token simulates successful mock payment. `fail-demo` simulates a declined payment. `WALLET` is the second supported method.
3. **1:15–2:15 — Customer timeline.** The order should appear in Track Order with its ID and status timeline. The seeded order `demo@contra.test` is already delivered with tracking number `DEMO-TRACK-001`; use the full Order ID shown on its Operations card, then enter that ID in Track Order.
4. **2:15–3:15 — Fulfillment.** In Operations, explain the order counts, fulfillment queue, and notifications. For a newly checked-out order, choose Start fulfillment, add a tracking number, and mark it delivered. The state changes are recorded with actor and timestamp.
5. **3:15–4:00 — Returns and payments.** Use the delivered seeded demo order to request a return. In Operations, receive the return and issue the refund. For a payment retry demo, create a checkout with `fail-demo`; use Retry payment and leave the token blank to succeed.
6. **4:00–5:00 — Jira and release safety.** Show the Jira delivery and Release Readiness sections. With Jira credentials configured these display the live Jira version, status, blockers, and points. Offline mode explains why readiness cannot be confirmed and leaves order operations usable. Feature flags start disabled and require the linked delivery issue and QA issue to be Done before enabling; the current Jira seed has no linked QA issues, so keep the flags disabled during the offline demo.

## Reset demo data

Stop the app, then remove the local database files under `data/` and rerun `./scripts/start.sh`. This resets local demo orders and stock. Do this only when you are comfortable losing local demo data.

## Submission note

The Jira project currently has three empty, unstarted sprints. Use the app demo to show running order workflows, and present Jira planning as configured goals plus a backlog with known gaps; do not describe the sprints as completed work.
