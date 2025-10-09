# Task 05: Refactor and Improve Existing Tests

## Description

The only existing unit test file, `SettingsViewModelTest.kt`, is bloated with repetitive tests and uses outdated patterns for coroutine testing. This task is to refactor it to be more concise and maintainable.

### Dependencies

- **`task-04.md`**: Establish Comprehensive Test Coverage

### Current Behavior

-   The test class has a separate, nearly identical test function for every single setting update.
-   It uses manual Main dispatcher manipulation (`Dispatchers.setMain`/`resetMain`) in `@BeforeEach`/`@AfterEach` blocks.
-   The project mixes JUnit 4 and JUnit 5 dependencies.

### Expected Behavior

-   The numerous individual test functions in `SettingsViewModelTest` are replaced with a single `@ParameterizedTest` that provides the different update functions and values as parameters.
-   A JUnit 5 `MainCoroutineExtension` is created and used via `@ExtendWith` to manage the test dispatcher, removing the manual setup/teardown logic.
-   All JUnit 4 dependencies are removed from the build files, and the project uses only JUnit 5.

## Acceptance criteria

- `SettingsViewModelTest.kt` is significantly smaller and easier to read.
- All tests in the refactored file pass.
- The project's dependencies only include JUnit 5.
