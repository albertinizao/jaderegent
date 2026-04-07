# GitHub CI/CD Setup Guide

## 1) Repository detection summary

Detected from this repository:

- **Backend path:** `/` (Spring Boot code under `src/main/java`, Maven root `pom.xml`).
- **Frontend path:** `/frontend` (Vite/React app).
- **Java build system:** Maven (`pom.xml`, `mvnw`).
- **Node package manager:** npm (`frontend/package-lock.json`).
- **Java version:** 21 (`<java.version>21</java.version>` in `pom.xml`).
- **Node version:** 20.10.0 (in `frontend-maven-plugin` config in `pom.xml`; no `.nvmrc`/`.node-version` detected).
- **Frontend scripts detected in `frontend/package.json`:**
  - `lint`: present *(but ESLint config file is currently missing)*
  - `test`: missing
  - `build`: present

## 2) What was implemented

### Workflows

- `.github/workflows/ci-pr.yml`
  - Triggered on `pull_request` and `push` to `main`.
  - Always starts for PRs (no path-filter at trigger level).
  - Detects changed paths, then runs backend/frontend jobs conditionally.
  - Treats changes in `/.github/workflows/` as affecting both backend and frontend validation.
  - Keeps stable required check names:
    - `Detect changes`
    - `Backend CI`
    - `Frontend CI`
  - Uses concurrency cancellation for superseded runs.
  - Backend:
    - Java 21 setup
    - Maven cache
    - `./mvnw -B verify`
    - uploads test/build artifacts when available
  - Frontend:
    - Node 20.10.0 setup
    - npm cache
    - `npm ci`
    - runs `lint` only if script exists and ESLint config is present
    - runs `test` only if script exists (currently skipped because missing)
    - runs `build` only if script exists
    - uploads frontend build artifacts when available

- `.github/workflows/security.yml`
  - Dependency Review on pull requests (PR-only check).
  - CodeQL jobs with stable names:
    - `CodeQL (java-kotlin)`
    - `CodeQL (javascript-typescript)`
  - Java analysis includes explicit Maven compile step.
  - JavaScript/TypeScript analysis uses explicit Node setup and `npm ci` in `/frontend`.

- `.github/workflows/deploy.yml`
  - Safe manual scaffold (`workflow_dispatch`) with `staging`/`production` input.
  - Uses GitHub Environments.
  - Includes explicit TODO markers because deploy target is not clearly defined.

### Governance and maintenance files

- `.github/dependabot.yml`
  - Weekly updates for GitHub Actions, Maven, and npm (`/frontend`).
  - Groups minor/patch updates to reduce noise.

- `.github/CODEOWNERS`
  - Ownership rules for `/src`, `/frontend`, and `/.github`.
  - Placeholder owners included and marked for replacement.

## 3) Assumptions made

1. The backend CI command should be `./mvnw -B verify` to cover compile + tests conservatively.
2. Frontend uses npm (due to lockfile and scripts).
3. Node 20.10.0 is the best available repo-declared version source.
4. Deployment destination (registry/cloud/host) is **not inferable** from repo files, so only a manual scaffold was added.

## 4) Required status checks (recommended)

For branch protection on `main`, require these checks by exact job names:

1. `Detect changes`
2. `Backend CI`
3. `Frontend CI`
4. `Dependency Review` *(PR-only)*
5. `CodeQL (java-kotlin)`
6. `CodeQL (javascript-typescript)`

> Notes:
> - `Dependency Review` runs only for pull requests. Configure required checks in PR/merge context.
> - Keep the names exactly as above to avoid branch protection drift.

## 5) Recommended branch protection / ruleset settings for `main`

Enable:

- Require a pull request before merging.
- Require 1–2 approvals.
- Require review from Code Owners.
- Require status checks to pass before merging.
- Require branches to be up to date before merging.
- Restrict direct pushes to `main`.
- Include administrators (recommended for stronger governance).
- Optionally enable merge queue once team throughput requires it.

## 6) Recommended pull request settings

- Dismiss stale approvals when new commits are pushed.
- Require approval of the most recent reviewable push.
- Require conversation resolution before merge.
- Auto-delete head branches after merge.

## 7) Security features to enable and verify

## 7.1 Code scanning / CodeQL

1. Ensure **Security** tab is enabled in repository settings.
2. Confirm workflow `Security` is active in Actions.
3. Open **Security → Code scanning alerts** and verify new analyses appear.
4. If no alerts/data appear after first run, confirm Actions permissions and default branch workflow execution.

## 7.2 Dependabot alerts

1. In **Settings → Security & analysis**, enable **Dependabot alerts**.
2. Enable **Dependabot security updates**.
3. Verify alerts in **Security → Dependabot**.

## 7.3 Dependency graph

1. In **Settings → Security & analysis**, enable **Dependency graph**.
2. Verify **Insights → Dependency graph** is populated after CI runs.

## 7.4 Secret scanning

1. Enable **Secret scanning** (and alerts) in **Security & analysis**.
2. If your plan supports it, enable org/repo-level custom patterns as needed.

## 7.5 Push protection

1. If available on your plan, enable **Push protection** for secret scanning.
2. Validate with a dry-run process/policy in a test repo before broad rollout.

## 8) GitHub Environments setup (`staging`, `production`)

Create two environments under **Settings → Environments**:

1. `staging`
2. `production`

Recommended protection rules:

- `staging`:
  - Optional reviewer gate (team-dependent)
  - Wait timer optional
- `production`:
  - Required reviewers (at least 1)
  - Optional wait timer (for controlled releases)
  - Restrict deployment branches (e.g., `main` only)

## 9) Secrets management guidance

Use least privilege and scope secrets by environment whenever possible.

- **Repository secrets:** shared non-production values needed by many workflows.
- **Environment secrets:** deployment credentials per target (`staging` vs `production`).

Recommended examples (replace with your platform-specific names):

- `CLOUD_ROLE_ARN`
- `CLOUD_REGION`
- `REGISTRY_USERNAME`
- `REGISTRY_PASSWORD`
- `DEPLOY_API_TOKEN`

Never hardcode secrets in workflow YAML, scripts, or source files.

## 10) What is missing to complete CD

Deployment target details are not explicit in this repository. To complete production-grade CD, define:

1. **Target platform** (e.g., GHCR + VM, Kubernetes, ECS, Azure Web App, etc.).
2. **Artifact strategy** (JAR only, frontend static bundle, container image).
3. **Release versioning** (tagging, semantic versioning, build metadata).
4. **Promotion model** (`staging` → `production`, manual approvals, rollback steps).
5. **Runtime secrets and config mapping** per environment.

Once these are decided, replace the scaffold `deploy.yml` TODO steps with concrete build/publish/deploy steps.
