# Task 11: Decompose Monolithic UI Files

## Description

Several UI files, especially `ScoreScreen.kt`, are monolithic. They contain many different composables, preview providers, and fake data implementations in one file, making them hard to read and maintain. This task is to break them down into smaller, reusable components.

### Dependencies

- **`task-10.md`**: Refactor UI Layer Architecture

### Current Behavior

-   `ScoreScreen.kt` is over 500 lines long and contains definitions for the main screen, landscape/portrait layouts, player cards, controls, and multiple complex previews with fake repositories.
-   `SettingsScreen.kt` defines its data (`SettingItemData`) and UI in the same file.
-   `NavController` is passed down through multiple layers of composables.

### Expected Behavior

-   Reusable composables from `ScoreScreen.kt` (e.g., `PlayerScoreCard`, `CentralControls`, `BottomBarActions`) are extracted into their own files in a new `ui/scorescreen/components` package.
-   The extensive preview setup code in `ScoreScreen.kt` is moved to a separate file in the `debug` source set, using a `@PreviewParameterProvider` for cleaner previews.
-   The `SettingItemData` sealed class and the list construction in `SettingsScreen.kt` are moved into the `SettingsViewModel`, so the composable is only responsible for rendering the list.
-   `NavController` is no longer passed down. Navigation events are hoisted up via lambda functions (e.g., `onNavigateToSettings: () -> Unit`).

## Acceptance criteria

- The line count of `ScoreScreen.kt` and `SettingsScreen.kt` is significantly reduced.
- New files for the extracted components exist.
- Composables like `CentralControls` no longer have a `NavController` parameter.
