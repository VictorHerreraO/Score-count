# Active Context: Score-Count App Development

This file tracks the development progress of the Score-Count application.

## Accomplished So Far:

*   **Project Initialization & Core Architecture**: Established layered architecture (UI, Domain, Data), core models, use cases for game logic, and initial UI for `ScoreScreen` with Hilt for DI.
*   **Build & Error Resolution**: Iteratively fixed compilation issues related to Hilt, Compose, Kotlin syntax, and API changes. This included resolving mismatches between repository interfaces and their preview/fake implementations.
*   **Settings Screen Implementation (Core Functionality)**:
    *   **Navigation**: Added Jetpack Navigation Compose, defined routes in `Screen.kt`, and updated `MainActivity.kt` to host `NavHost`. Navigation from `ScoreScreen` to `SettingsScreen` via a top app bar icon is functional.
    *   **UI Structure**: Created `SettingsScreen.kt` with Compose UI elements. This includes sections for "Game Controls & Actions" (using `LazyVerticalGrid`) and "Table Tennis Rules Configuration" (using `LazyColumn` with custom row composables like `ActionSettingCard`, `ToggleSettingCard`, `StepperSettingRow`, `SwitchSettingRow`).
    *   **ViewModel**: Created `SettingsViewModel.kt` to manage the state and logic for the settings, exposing `GameSettings` via a `StateFlow`.
    *   **Data Model**: Defined `GameSettings.kt` in the domain layer to hold all configurable setting values. Added `winByTwo` and refined other settings.
    *   **Persistence**: Implemented settings persistence using SharedPreferences:
        *   `SettingsRepository.kt` (domain layer interface).
        *   `SettingsLocalDataSource.kt` (data layer, handles SharedPreferences interaction).
        *   `SettingsRepositoryImpl.kt` (data layer implementation).
    *   **Dependency Injection**: Updated Hilt modules (`DataModule.kt`, `RepositoryModule.kt`) to provide `SharedPreferences`, `SettingsLocalDataSource`, `LocalScoreDataSource` (with `SettingsRepository` dependency), and bind `SettingsRepository`. Added `androidx.hilt:hilt-navigation-compose` dependency.
    *   **Preview**: Implemented `PreviewSettingsRepository` and `FakeSettingsRepository` in `SettingsScreen.kt` and `ScoreScreen.kt` respectively for reliable Compose previews, ensuring alignment with repository interfaces.
    *   **Initial Functionality**: Connected "Switch serve" button in settings to `ScoreViewModel.manualSwitchServe()`.

*   **Integration of Settings with Game Logic/UI**:
    *   **ViewModel Updates**: `ScoreViewModel.kt` now injects `SettingsRepository`, fetches `GameSettings`, and exposes them as a `StateFlow`.
    *   **Core Game Logic in `LocalScoreDataSource.kt`**:
        *   Injected `SettingsRepository`.
        *   `incrementScore()`: Now considers `pointsToWinSet`, `winByTwo`, and `numberOfSets` from `GameSettings` to determine set/match winners. Includes logic to reset scores after a set and mark the game as finished.
        *   `determineNextServer()`: New private helper to manage serve changes based on `serveRotationAfterPoints`, `winnerServesNextGame`, and `serveChangeAfterDeuce` from `GameSettings`.
        *   `manualSwitchServe()`: Renamed from `switchServe` for clarity.
        *   `resetGame()`: Resets scores and set counts; player names are derived from `initialGameState` (display is settings-dependent at UI).
    *   **`GameState.kt` Update**: Added `player1SetsWon` and `player2SetsWon` to track set scores.
    *   **Repository & UseCase Updates**:
        *   `ScoreRepository.kt` and `ScoreRepositoryImpl.kt`: Updated `switchServe` to `manualSwitchServe`.
        *   Created `ManualSwitchServeUseCase.kt`.
        *   Old `SwitchServeUseCase.kt` marked as obsolete.
    *   **`ScoreScreen.kt` UI Updates**:
        *   Observes `gameSettings` from `ScoreViewModel`.
        *   Conditionally renders UI elements based on settings: top app bar title (`showTitle`), history icon (`showPreviousSets`), player names (`showNames`), serving indicator (`markServe`).
        *   Displays set scores ("Sets: X - Y") if `showSets` is true.
        *   Displays "DEUCE" indicator next to player names if `markDeuce` is true and game is in deuce state.

*   **Settings Screen UI Refinement**:
    *   Updated icons in the "Game Controls & Actions" section to outlined variants to match the target design.
    *   Modified `ToggleSettingCard` and `ActionSettingCard` for consistent background color (`surfaceColorAtElevation(3.dp)`), and improved visual feedback for checked states on `ToggleSettingCard` (primary color tint, checkmark icon).
    *   Adjusted `fontWeight` of section headers to `SemiBold`.

## Currently Working On:

*   **Compilation Error Resolution**: Addressing any remaining compilation errors after the recent Settings Screen UI refinements to ensure the app is buildable and runnable.
*   **Finalizing Settings Screen**: Ensuring all UI elements behave as expected and settings are correctly saved and loaded.

## Next Steps:

1.  **Compile & Verify**: Ensure the application compiles successfully with all recent changes.
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
8.  **Documentation Update**: Keep `ARCHITECTURE.MD` and `ACTIVE_CONTEXT.md` updated as the project evolves (ongoing).

## Potential Future Enhancements:

*   Setting for who serves first in a match.
*   Setting for how to decide who serves first in subsequent sets if `winnerServesNextGame` is false (e.g., alternate).
*   More detailed game history/log.
*   Player name customization directly in the app.
