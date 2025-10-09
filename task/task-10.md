# Task 10: Refactor UI Layer Architecture

## Description

The UI layer violates architectural boundaries by interacting directly with repositories and contains logic that should be in a ViewModel or Use Case. This task is to enforce proper architectural layering.

### Dependencies

- **`task-06.md`**: Refactor Core Architecture

### Current Behavior

-   `MainActivity` injects `SettingsRepository` to manage the `FLAG_KEEP_SCREEN_ON`.
-   `ScoreViewModel` contains business logic to determine the winner before resetting a game.
-   `ScoreViewModel` uses nullable `StateFlow`s (`GameState?`, `GameSettings?`), forcing the UI to handle nulls unnecessarily.

### Expected Behavior

-   A new `MainViewModel` is created. The logic for managing the screen-on flag is moved there. `MainActivity` only interacts with this ViewModel.
-   The winner-detection logic in `ScoreViewModel.resetGame()` is moved into the `ResetGameUseCase`.
-   The `StateFlow`s in `ScoreViewModel` are made non-nullable. The ViewModel and UI should rely on the fact that a valid, non-null initial state is always available.

## Acceptance criteria

- `MainActivity` no longer has any dependency on a repository.
- `ScoreViewModel` is cleaner and delegates all business logic.
- The loading spinner that handles the initial null state in `ScoreScreen` is removed, as the state is now guaranteed to be non-null.
