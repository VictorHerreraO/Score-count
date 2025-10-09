# Task 07: Persist Game State to Disk

## Description

The current game state (`GameState`) is stored entirely in-memory in a singleton. If the app process is killed by the system, the user's entire game is lost. The game state must be persisted to disk to survive process death.

### Dependencies

- **`task-06.md`**: Refactor Core Architecture

### Current Behavior

-   `LocalScoreDataSource` holds the `GameState` in a `MutableStateFlow`.
-   The "undo" feature uses a simple nullable variable `previousGameState`.
-   When the app process is terminated and restarted, the game resets to its initial state.

### Expected Behavior

-   The `GameState` is automatically saved to a persistent store (either a new Room table or, more simply, a dedicated Jetpack DataStore) on every change.
-   When the app starts, the `LocalScoreDataSource` reads the last saved `GameState` from disk to initialize its state.
-   The "undo" feature is refactored to be more robust, perhaps by storing a list of previous states, which is also persisted.
-   If the user is in the middle of a game, closes the app (or the process is killed), and re-opens it, the game continues from exactly where they left off.

## Acceptance criteria

- Start a game, change the score, and force-stop the application from system settings.
- Re-launch the app. The score and game state are exactly as they were before the app was killed.
- The undo feature works reliably for multiple actions.
