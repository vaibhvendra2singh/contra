# Evidence Screenshot Checklist

Capture screenshots after the Jira backlog and sprint setup is complete. Keep each image legible and include the page title/URL where possible.

| Evidence | Where to find it |
| --- | --- |
| Jira backlog showing epics and stories | OM2 → Backlog |
| Jira Scrum board and configured columns | OM2 → Board |
| Sprint 1 report and Sprint 1 goal | OM2 → Active sprint → Reports |
| Release versions v0.1, v0.2, v1.0 | OM2 → Releases (or project version page after REST fallback) |
| Payment → fulfillment → shipping → returns dependency links | Open linked Jira stories/epics and show issue-link sections |
| Shop catalog and checkout | `http://localhost:8080` |
| Customer order timeline with delivered tracking number | Open Track order and enter the demo order ID from Operations |
| Admin operations dashboard and feature flags | App → Operations |
| Jira delivery panel and release readiness verdict | App → Operations → Jira delivery / Release readiness |
| Auto-created operational Bug | Jira issue list filtered to `project = OM2 AND labels = auto-created` after a configured trigger |

Do not claim screenshots for Jira items that have not actually been configured or triggered. The visible Jira browser can be used for the Jira evidence; the local application screens can be captured from the running app.
