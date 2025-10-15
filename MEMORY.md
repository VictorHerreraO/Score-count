# Memory: Score-Count App Development

This file tracks the current state of development for the Score-Count application. Treat this file as the context that future you will need to finish any incomplete changes.

## Current Work: Replace hardcoded UI strings with string resources (Done)

### Summary
- Replaced all hardcoded strings in the UI with Android string resources.
- Updated `SettingItemData` to carry string resource IDs instead of raw strings so the ViewModel remains context-free.
- Fixed all build and lint errors.

### Files changed
- `app/src/main/res/values/strings.xml` - added multiple string entries for settings and score UI
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/settings/SettingsViewModel.kt` - now uses R.string ids for settings items
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/settings/SettingsScreen.kt` - consumes resource ids via `stringResource()` and updates content descriptions
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/BottomBarActions.kt` - uses string resources
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/CentralControls.kt` - uses string resources
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/ScoreScreen.kt` - title now from string resources
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/matchhistory/MatchHistoryScreen.kt` - uses string resources
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/DeuceIndicator.kt` - uses string resources
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/PlayerScoreCard.kt` - uses string resources
- `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/SetsIndicator.kt` - uses string resources

### Status
- Done: All hardcoded strings have been replaced with string resources. The application builds successfully, and all tests and lint checks pass.

### Next steps
- None.

-Author: Gemini