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

## Commands

| Task | Command |
| --- | --- |
| What CI runs | `./gradlew :app:assembleDebug :app:check` |
| Unit + Robolectric tests | `./gradlew :app:testDebugUnitTest` |
| One test class | `./gradlew :app:testDebugUnitTest --tests "com.bustedelbow.kivo.domain.ledger.LedgerTest"` |
| Lint and ktlint | `./gradlew :app:lintDebug :app:ktlintCheck` |

`:app:check` is the gate: lint, ktlint and every JVM test. CI (`.github/workflows/ci.yml`) runs it on every push and pull request. Instrumented tests in `app/src/androidTest/` need a device and are not part of CI.

## Where each kind of test lives

- **Domain** — `app/src/test/.../domain/`: pure maths, no Android.
- **Repository and ViewModel** — `app/src/test/.../data/repository/` and `.../ui/`: real collaborators driven by the DAO fakes, with `MainDispatcherRule` for `viewModelScope`.
- **Database** — `app/src/test/.../data/local/`: in-memory Room under Robolectric, for DAO queries and the seed callback.
- **UI behaviour and navigation** — `app/src/test/.../ui/`: Compose tests under Robolectric; `KivoAppNavigationTest` is the pattern.

## Fakes over mocks

Put a fake at the seam instead of a mock: `FakeAccountDao`/`FakeEntryDao` stand in for Room at the DAO boundary, and in-memory Room stands in for the device. No mocking framework is installed, and none is needed yet.

## Known follow-ups

- **Screenshot tests** are not set up. The Compose Preview Screenshot Testing tool needs AGP 9.5.0-alpha03 or higher for the test-suite setup; this project is on AGP 9.4.1, so that is a separate upgrade decision.
- **Deprecated test rule**: `KivoAppNavigationTest` uses `createComposeRule`; the v2 API (`androidx.compose.ui.test.junit4.v2.createComposeRule`) replaces it.
- **Screen-level behaviour tests** need a fake container: the screens read a concrete `AppContainer` through `LocalAppContainer`. Introducing a repository interface would open that seam and let a test drive Home/Accounts states directly.
