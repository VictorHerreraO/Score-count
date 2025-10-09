# Task 02: Externalize Hardcoded Strings

## Description

The UI code contains many hardcoded, user-facing strings in composables like `ScoreScreen.kt` and `SettingsScreen.kt`. This prevents localization and makes managing UI text difficult. All user-visible text must be moved to string resources.

### Current Behavior

-   Strings like "Table Tennis", "Settings", "Reset", "New Game", "DEUCE" are hardcoded directly in the `@Composable` functions.
-   The `app/src/main/res/values/strings.xml` file is mostly empty.

### Expected Behavior

-   There are no hardcoded user-facing strings in the Kotlin UI code.
-   All UI text is referenced from `strings.xml` using `stringResource(R.string.your_string_name)`.
-   The `strings.xml` file contains all the text displayed in the application.

## Acceptance criteria

- A static analysis check for hardcoded strings in the UI layer passes.
- The application UI displays all text correctly, sourcing it from the resource file.
