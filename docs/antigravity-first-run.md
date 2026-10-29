# Antigravity: First-Run Setup and Verification

This guide starts from a clean checkout on macOS and shows how to verify the local application. The same Maven commands work in Antigravity's integrated terminal on other operating systems once Java and Maven are installed.

## 1. Install the prerequisites

1. Install Google Antigravity from the [official download page](https://antigravity.google/download), or install its official extension in a supported editor. Google’s setup instructions for [opening an existing workspace](https://antigravity.google/docs/getting-started?tab=ide) and [workspace rules](https://antigravity.google/docs/rules/) describe the UI steps and `AGENTS.md` support.
2. Install a JDK (Java 17 or newer) and Apache Maven (3.8 or newer). On a Mac with Homebrew:

   ```sh
   brew install openjdk@17 maven
   export PATH="$(brew --prefix openjdk@17)/bin:$PATH"
   export JAVA_HOME="$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home"
   ```

   If Homebrew is not installed, use the vendor installer for a JDK and the official Maven installation guide. The JDK and Maven are tools on your computer, not dependencies to add to this repository.
3. Confirm versions in Terminal:

   ```sh
   java -version
   mvn -version
   ```

   Java must be version 17 or newer; Maven must be 3.8 or newer.

## 2. Open this project in Antigravity

1. Launch Antigravity and choose **Open Folder** (or add a folder to a project).
2. Select the repository root: `AGILE PROJECT` (the folder containing `pom.xml`, `README.md`, and `AGENTS.md`). Do not open only `src/`.
3. Use local workspace mode if Antigravity asks where the agent should work.
4. Allow the Java project import/indexing to finish. The workspace `AGENTS.md` provides project-specific instructions; no Antigravity-only build plugin is required.
5. Open Antigravity's integrated terminal at the repository root.

## 3. Configure local settings

The app runs without Jira credentials. For the first local run, no `.env` file is required. If you want to set local settings explicitly, copy the template and edit it:

```sh
cp .env.example .env
```

Leave the `JIRA_*` values blank for offline mode. Never put API tokens in `AGENTS.md`, source code, screenshots, chat prompts, or committed files. `.env` is git-ignored.

The default settings are: HTTP port `8080`, H2 file DB `data/oms`, max payment retries `3`, stuck-order threshold `24` hours, and `DEMO_MODE=true`. The local fixture catalog is synthetic sample storefront data; the optional delivered fixture order is clearly labeled Contra Demo Customer. Neither is real client/customer data. The Jira reports use recorded Jira observations, not these app fixtures.

## 4. Run the automated checks

In the integrated terminal, from the repository root, run:

```sh
mvn test
```

The first Maven run downloads dependencies and needs an internet connection. A passing run ends with `BUILD SUCCESS`; the current suite contains 11 tests covering state transitions, checkout/payment/returns, and offline Jira behavior. Tests use an in-memory H2 database and mock/offline Jira boundary. They do not authenticate to Jira or validate a Jira token.

## 5. Start the app

Run:

```sh
./scripts/start.sh
```

The script checks Java/Maven, loads `.env` if present, creates the database directory, and starts Spring Boot. Wait for `Started OrderManagementApplication`. Leave this terminal running. Then open:

- Store: <http://localhost:8080>
- OpenAPI UI: <http://localhost:8080/swagger-ui.html> (Springdoc may redirect to `/swagger-ui/index.html`)
- H2 console (local development only): <http://localhost:8080/h2-console>

To stop the app, focus the terminal running it and press **Ctrl+C**. If port 8080 is occupied, set `SERVER_PORT=8081` in `.env`, restart, and use port 8081 in the links.

## 6. Verify the running app

In another terminal, run these read-only checks:

```sh
curl -fsS http://localhost:8080/api/products
curl -fsS http://localhost:8080/api/admin/dashboard
curl -fsS http://localhost:8080/api/admin/orders
curl -fsS 'http://localhost:8080/api/admin/jira/readiness?version=v1.0'
```

Expected: product JSON; dashboard counts; an optional `demo@contra.test` delivered order only when demo mode is enabled and no such order exists yet; and a Jira readiness response. Without Jira credentials, readiness should be `NOT READY` with an offline reason while order APIs remain usable.

In the browser, place a test order with a name and email, choose `CARD` or `WALLET`, and leave the payment token blank for mock success. Use `fail-demo` in the payment token to test a decline and retry. The generated order appears in **Track order** and **Operations**. The local sample catalog and mock payment provider exist for exercising the required workflows; do not represent them as real client transactions.

For a full teacher walkthrough, see [`demo-script.md`](demo-script.md).

## 7. Optional live Jira connection

Runtime Jira features need an Atlassian API token from the Jira account owner. Create a token through Atlassian account security settings, then put the scoped-token API gateway URL (`https://api.atlassian.com/ex/jira/<cloud-id>`), Jira site URL, Atlassian login email, and token in `.env`. The Jira project URL and observed configuration are recorded in [`jira-setup-status.md`](jira-setup-status.md). Do not paste the token into Antigravity. Restart the app after editing `.env`, then run `python3 jira/verify-connection.py` to check project, issue-search, versions, and the configured board's sprint access without printing credentials or issue contents.

The optional REST fallback loads simple `KEY=VALUE` entries from `.env` and supports a dry run:

```sh
python3 jira/rest-fallback.py --dry-run
```

The dry run performs Jira reads to compare current versions/sprints and prints planned creates; it does not apply changes. Only run the non-dry-run command if you intend to create the missing versions/sprints. No token is included in this repository.

## 8. Project-data integrity

- `jira/seed.json` is a plan, not a readback of live Jira. The live snapshot says 8 epics, 25 stories (including a duplicate), all stories To Do/unassigned/without estimates, and three empty unstarted sprints with goals.
- `docs/burndown-and-velocity.md` explicitly says Jira has no real burndown history or completed velocity. No fake Gantt matrix is included. Do not turn planned story points into claimed completed work.
- Submission 1/client research was not present in the repository; related report text records that gap rather than fabricating findings.
