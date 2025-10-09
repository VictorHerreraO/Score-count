# Task 08: Fix Data Integrity and Data Layer Issues

## Description

The data layer has several issues ranging from data loss during mapping to non-ideal dependency provision.

### Dependencies

- **`task-06.md`**: Refactor Core Architecture

### Current Behavior

-   `MatchMapper` hardcodes player IDs to `0` when mapping from a database entity to a domain model, causing data loss.
-   The Jetpack DataStore is provided via a global `Context` extension property, which is not ideal for testing and DI.

### Expected Behavior

-   The `MatchMapper` and `MatchEntity` are updated to correctly handle player IDs. If player IDs are not relevant for a `Match`, the domain model should be simplified to reflect that, but data should not be silently discarded.
-   The global `Context.settingsDataStore` extension is removed.
-   The `DataStore<Preferences>` instance is provided directly via a Hilt module (e.g., a new `DataStoreModule` or an updated `DataSourceModule`).

## Acceptance criteria

- The `Match` domain model accurately represents the data stored in the database without data loss.
- The DataStore dependency is provided exclusively by Hilt.
- Tests for `MatchMapper` are written and pass.
