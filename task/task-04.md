# Task 04: Establish Comprehensive Test Coverage

## Description

The project has a critical lack of test coverage. The most complex and important logic (score calculation) is completely untested. This task is to create a foundational test suite that provides a safety net for future refactoring.

**This is a high-priority task that blocks major refactoring.**

### Dependencies

- **`task-01.md`**: Stabilize Build Configuration

### Current Behavior

-   No unit tests exist for any ViewModels (except `SettingsViewModel`), UseCases, Repositories, or Mappers.
-   The core game logic in `LocalScoreDataSource` is untested.
-   The only instrumented test is a placeholder.

### Expected Behavior

-   A new test file is created for `LocalScoreDataSource`.
-   This test file contains thorough unit tests covering all scenarios of the existing score calculation logic: point increments, set wins, match wins, deuce logic, server rotation, etc.
-   Unit tests are created for `ScoreViewModel` and `MatchHistoryViewModel`.
-   Unit tests are created for `MatchMapper`.
-   The placeholder `ExampleInstrumentedTest.kt` is deleted and replaced with a simple, meaningful UI test (e.g., one that launches `MainActivity` and verifies that `ScoreScreen` is displayed).

## Acceptance criteria

- The project's test coverage percentage is significantly increased.
- All new tests pass and accurately reflect the *current* behavior of the application, even if that behavior is flawed. These tests will serve as a safety net to ensure the refactoring in subsequent tasks does not cause regressions.
