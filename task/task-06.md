# Task 06: Refactor Core Architecture (Anemic Domain Model)

## Description

This is the most critical task for the project. The application suffers from an Anemic Domain Model, where the core business logic resides in the data layer (`LocalScoreDataSource`) instead of the domain layer. This task involves moving all business logic to its correct place in the architecture.

### Dependencies

- **`task-04.md`**: Establish Comprehensive Test Coverage (CRITICAL)

### Current Behavior

-   `LocalScoreDataSource` is a massive, stateful singleton containing all game rules (scoring, server rotation, set/match completion).
-   Use cases in the domain layer are simple pass-throughs that delegate calls to the repository.
-   The `ScoreRepository` has methods like `incrementScore`, making it responsible for business actions.
-   `LocalScoreDataSource` has a circular dependency on `SettingsRepository`.

### Expected Behavior

-   A new, pure `ScoreCalculator` class or a set of functions is created in the **domain layer**.
-   All game logic from `LocalScoreDataSource` is moved into this new `ScoreCalculator`.
-   `LocalScoreDataSource` becomes a "dumb" component, responsible only for holding the current `GameState` in a `StateFlow` and persisting it.
-   The `ScoreRepository` interface is simplified. Methods like `incrementScore` are removed and replaced with a generic `updateGameState(newState: GameState)`.
-   Use cases (e.g., `IncrementScoreUseCase`) are refactored to be the orchestrators: they fetch the current state and settings, use the `ScoreCalculator` to determine the new state, and then use the repository to save the new state.
-   The circular dependency is broken; `LocalScoreDataSource` no longer depends on any repository.

## Acceptance criteria

- `LocalScoreDataSource` contains no business logic.
- All game logic is contained within the domain layer and is thoroughly unit-tested.
- The application functions exactly as it did before the refactor, verified by the test suite created in `task-04.md`.
