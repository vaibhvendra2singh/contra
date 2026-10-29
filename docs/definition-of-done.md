# Definition of Done and Release Readiness

A story is Done when all applicable checks below are true:

- Acceptance criteria are met and demonstrated in the application or API.
- Implementation is reviewed; tests cover the changed behavior and pass.
- The linked QA task is complete and records its test evidence.
- No unresolved blocking Jira dependencies or open blocking bugs remain.
- API and user-facing behavior are documented where needed.
- Feature flags are linked to their Jira story and QA task; rollout is enabled only after both are Done.
- Operational impact, release notes, monitoring, and a flag-off/rollback plan are reviewed.

A release is **READY** only when every issue assigned to the selected Jira version is Done, all linked QA work passed, there are no open blockers or unresolved dependencies, and the application test suite passes. Otherwise report **NOT READY** and list the reasons. Jira unavailability is never treated as READY.
