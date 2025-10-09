# Task 09: Improve Domain Layer Idioms

## Description

The domain layer contains some non-idiomatic Kotlin code, including a mutable model and inconsistent use case naming. This task is to clean up the domain layer to follow best practices.

### Dependencies

- **`task-06.md`**: Refactor Core Architecture

### Current Behavior

-   The `Player` data class has a mutable `score` property (`var`).
-   Use cases have a public method named `execute`.
-   The file `SwitchServeUseCase.kt` is obsolete and contains only comments.

### Expected Behavior

-   All properties in all domain models (including `Player`) are immutable (`val`). State updates are performed by creating new object instances with `copy()`.
-   All use cases are refactored to use the `operator fun invoke()` convention, allowing them to be called like functions.
-   The obsolete `SwitchServeUseCase.kt` file is deleted from the project.

## Acceptance criteria

- The `Player.score` property is a `val`.
- Use cases are invoked directly (e.g., `myUseCase()`) in ViewModels.
- The `SwitchServeUseCase.kt` file no longer exists.
