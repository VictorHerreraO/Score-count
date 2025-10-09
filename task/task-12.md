# Task 12: Improve DI Module Structure

## Description

The Hilt modules are not clearly organized and contain redundant annotations. This task is to clean up and reorganize the modules for better clarity and maintainability.

### Dependencies

- **`task-06.md`**: Refactor Core Architecture
- **`task-08.md`**: Fix Data Integrity and Data Layer Issues

### Current Behavior

-   `DataModule` is a miscellaneous collection of database, DAO, and data source providers.
-   `@Binds` methods in `RepositoryModule` and `DataSourceModule` have redundant `@Singleton` annotations.

### Expected Behavior

-   The Hilt modules are reorganized by function:
    -   A new `DatabaseModule.kt` is created to provide the `AppDatabase` and all DAOs.
    -   `DataSourceModule.kt` is updated to provide all data sources, including the `DataStore` and the refactored `LocalScoreDataSource`.
    -   `RepositoryModule.kt` remains for binding repository interfaces.
    -   The old `DataModule.kt` is deleted.
-   The redundant `@Singleton` annotations are removed from all `@Binds` functions.

## Acceptance criteria

- The dependency injection setup works correctly after the refactoring.
- The project contains more, smaller, and more focused Hilt modules.
- No `@Binds` function has a scope annotation.
