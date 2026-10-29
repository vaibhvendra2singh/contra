# Five-Minute Demo Script

1. **0:00–0:30 — Introduce the problem.** Show the shop and explain that order operations and feature delivery must remain coordinated during releases.
2. **0:30–1:30 — Place an order.** Add a product, choose card or wallet, complete the mock checkout, and show the order ID and initial timeline.
3. **1:30–2:15 — Show payment failure handling.** Use `fail-demo` for a declined payment. Explain that failed payment does not allocate inventory and that retry limits are configurable.
4. **2:15–3:15 — Fulfill and track.** Open Operations, start fulfillment, add a tracking number, mark delivery, then use Track Order to show status history.
5. **3:15–4:00 — Returns and flags.** Request a return after delivery and show the admin receive/refund actions. Show that Jira-linked feature flags are gated on completed story and QA issues.
6. **4:00–5:00 — Release safety.** Show the Jira sprint panel and choose a release version on Readiness. Explain the READY/NOT READY reasons, blocker visibility, and offline behavior.

For a populated first run, use `./scripts/start.sh`; the demo runner creates one delivered order if no prior demo order exists.
