## Agent skills

### Issue tracker

Issues and specs live in this repo's GitHub Issues, via the `gh` CLI. See `docs/agents/issue-tracker.md`.

### Triage labels

Five canonical roles with label strings equal to their names (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`). See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: root `GLOSSARY.md` + `docs/adr/`. See `docs/agents/domain.md`.

### Coding standards

The reviewer's judgement calls live in `CODING_STANDARDS.md`; ktlint and lint own the mechanical style.

### Android skills

Android and Compose guidance lives in `.agents/skills/`; consult the matching skill before Android-specific work (e.g. `testing-setup` when adding tests, `edge-to-edge` for UI).

## Verifying changes

Run `./gradlew :app:check` before calling work done: it runs lint, ktlint and the tests, matching CI (`.github/workflows/ci.yml` runs `:app:assembleDebug :app:check`). Compose UI tests run on the JVM under Robolectric in `:app:testDebugUnitTest`; `app/src/test/java/com/bustedelbow/kivo/ui/KivoAppNavigationTest.kt` is the pattern. The full strategy is in `docs/testing.md`.
