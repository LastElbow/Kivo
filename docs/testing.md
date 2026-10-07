# Testing

Kivo's testing strategy and the commands that run it. The verification pointer in `AGENTS.md` reaches this file.

## Stack

| Concern | Choice | Where |
| --- | --- | --- |
| Local (JVM) tests | JUnit4 | `app/src/test/` |
| Compose UI tests | Compose Testing APIs, run on the JVM under Robolectric | `app/src/test/` |
| Robolectric | pinned to SDK 34 | `app/src/test/resources/robolectric.properties` |
| Database | Room, exercised against in-memory SQLite under Robolectric | `app/src/test/.../data/local/` |
| Dependency injection | manual `AppContainer` (ADR-0001): deliberately no Hilt | `di/AppContainer.kt` |
| Screenshot tests | not set up | — |
| End-to-end tests | not set up | — |

Money and balance maths is pure Kotlin and tested directly. ViewModels and repositories are tested against the in-memory DAO fakes in `data/FakeDaos.kt`; the Room layer runs against a real in-memory database.

Robolectric lays the window out at a default **320×470dp**; pin a realistic device for anything layout-sensitive — `@Config(qualifiers = "w360dp-h800dp")` on the test method.

## Commands

| Task | Command |
| --- | --- |
| What CI runs | `./gradlew :app:assembleDebug :app:check` |
| Unit + Robolectric tests | `./gradlew :app:testDebugUnitTest` |
| One test class | `./gradlew :app:testDebugUnitTest --tests "com.bustedelbow.kivo.domain.ledger.LedgerTest"` |
| Lint and ktlint | `./gradlew :app:lintDebug :app:ktlintCheck` |

`:app:check` is the gate: lint, ktlint and every JVM test. CI (`.github/workflows/ci.yml`) runs it on every push and pull request. Instrumented tests in `app/src/androidTest/` need a device and are not part of CI.

## Verifying in isolation

`:app:check` writes to `app/build`, so two sessions sharing one checkout race there (see the build-directory race in `AGENTS.md`). When that happens — or when files you did not touch change or fail to compile — verify in a throwaway worktree instead of the shared tree:

```powershell
git worktree add --detach ../Kivo-verify HEAD
Copy-Item local.properties ../Kivo-verify/local.properties   # holds sdk.dir; gitignored
Set-Location ../Kivo-verify
.\gradlew.bat :app:assembleDebug :app:check --console=plain
```

Remove it afterwards. On Windows `git worktree remove` can fail with *Filename too long* inside `app/build`; mirror an empty directory over it first, then delete:

```powershell
git worktree remove ../Kivo-verify --force
$empty = Join-Path $env:TEMP 'kivo-empty'
New-Item -ItemType Directory $empty -Force | Out-Null
robocopy $empty ../Kivo-verify /MIR /NFL /NDL /NJH /NJS /NP | Out-Null
Remove-Item ../Kivo-verify -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item $empty -Recurse -Force -ErrorAction SilentlyContinue
```

## Verifying on a device

The attached device may be the maintainer's personal phone, holding real data in this local-first app. Prefer an emulator or a Gradle-managed device. If you install on the attached device:

- update in place with `adb install -r`; never uninstall, which wipes the database,
- check what is installed first — `adb shell dumpsys package com.bustedelbow.kivo`, looking for `DEBUGGABLE` and the signing certificate,
- put back the previous build when you finish.

Builds signed with different keys cannot update each other: a production-signed release APK will not install over a debug-signed build, so treat any reinstall across signatures as data loss until proven otherwise.

## Where each kind of test lives

- **Domain** — `app/src/test/.../domain/`: pure maths, no Android.
- **Repository and ViewModel** — `app/src/test/.../data/repository/` and `.../ui/`: real collaborators driven by the DAO fakes, with `MainDispatcherRule` for `viewModelScope`.
- **Database** — `app/src/test/.../data/local/`: in-memory Room under Robolectric, for DAO queries and the seed callback.
- **UI behaviour and navigation** — `app/src/test/.../ui/`: Compose tests under Robolectric; `KivoAppNavigationTest` is the pattern.

## Fakes over mocks

Put a fake at the seam instead of a mock: `FakeAccountDao`/`FakeEntryDao` stand in for Room at the DAO boundary, and in-memory Room stands in for the device. No mocking framework is installed, and none is needed yet.

## Debugging a Compose test

A Compose failure names a matcher, not a cause, and each run costs a Gradle invocation. Four things pay off first:

- **Dump the tree once.** Write it to a file from inside the test so it survives the Gradle output: `java.io.File("build/tree.txt").writeText(composeRule.onRoot(useUnmergedTree = true).printToString(maxDepth = 40))`. Collect every diagnostic you need in that one run.
- **Read the unmerged tree.** A node can be present and still unfindable because an ancestor merges or clears its semantics: the material3 extended FAB hides its own label that way, so match the button through the content description on its icon. `onAllNodesWithText("...", useUnmergedTree = true)` finds it.
- **Remember that a lazy list composes only what is visible.** A section below the fold does not exist until `performScrollToNode(...)` brings it in.
- **Read a test's output from the report.** Its `println` lands in `app/build/test-results/testDebugUnitTest/<Class>.xml` under `<system-out>`, so search that file with `Select-String` instead of passing `-i`, which prints the entire Gradle log.

## Known follow-ups

- **Screenshot tests** are not set up. The Compose Preview Screenshot Testing tool needs AGP 9.5.0-alpha03 or higher for the test-suite setup; this project is on AGP 9.4.1, so that is a separate upgrade decision.
- **Deprecated test rule**: `KivoAppNavigationTest` uses `createComposeRule`; the v2 API (`androidx.compose.ui.test.junit4.v2.createComposeRule`) replaces it.
- **Screen-level behaviour tests** need a fake container: the screens read a concrete `AppContainer` through `LocalAppContainer`. Introducing a repository interface would open that seam and let a test drive Home/Accounts states directly.
