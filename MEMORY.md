# Memory: Score-Count App Development

This file tracks the development work that is currently in progress and has not yet been completed for the Score-Count application.

## In Progress:



## Next Steps:

1.  **~~Compile & Verify~~**: ~~Ensure the application compiles successfully with all recent changes, especially in `SettingsScreen.kt`.~~ (Completed)
2.  **Deploy & Test (Thoroughly)**:
    *   Navigation to and from the Settings screen.
    *   All UI elements on the Settings screen (toggles, steppers, action buttons, switches). Verify correct state representation and persistence.
    *   Persistence of all settings: Change every setting, close and reopen the app, and verify settings are retained.
    *   "Switch serve" functionality from the Settings screen.
    *   Game logic respecting all settings:
        *   Points to win set (e.g., 11, 21).
        *   Win by two rule.
        *   Number of sets for a match.
        *   Serve rotation rules (normal and after deuce).
        *   Winner serves next game rule.
    *   UI display respecting all settings on `ScoreScreen`: title, names, sets, serve marker, deuce marker, previous sets icon.
3.  **Refine `determineNextServer` Logic**: Improve robustness in `LocalScoreDataSource.kt` for edge cases (e.g., first serve of match, alternating server after set if `winnerServesNextGame` is false).
4.  **Implement History Action**: If `showPreviousSets` is true, implement the functionality for the history icon in `ScoreScreen.kt`'s `TopAppBar`. This might involve a new screen or dialog to show previous set scores or game actions.
5.  **Refine `UndoLastActionUseCase` Logic**: The current `undoLastAction()` in `LocalScoreDataSource` is basic. Enhance this to be more robust, potentially storing a more detailed history of `GameState` changes if complex undos are desired.
6.  **Address `TODO`s**: Review and address any pending `TODO` items in the codebase.
7.  **Remove Obsolete Code**: Delete `SwitchServeUseCase.kt` and the old `FakeScoreRepository` in `ScoreScreen.kt` if no longer used.
8.  **Documentation Update**: Keep `ARCHITECTURE.MD` and `ACTIVE_CONTEXT.MD` updated as the project evolves (ongoing).

## Potential Future Enhancements:

*   Setting for who serves first in a match.
*   Setting for how to decide who serves first in subsequent sets if `winnerServesNextGame` is false (e.g., alternate).
*   More detailed game history/log.
*   Player name customization directly in the app.
