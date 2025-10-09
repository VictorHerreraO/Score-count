# Android App Audit: ScoreCount

This document contains the results of a code audit for the ScoreCount Android application. The findings are categorized by architectural layer, from the build system to the UI.

---

## 1. Build & Dependencies

### `gradle/libs.versions.toml`

*   **Issue: Unstable Dependency Versions**
    *   **Finding**: The Android Gradle Plugin (AGP) version is set to `8.12.3`, and `navigation-compose` is `2.8.0-beta06`.
    *   **Why it's an issue**: Using alpha, beta, or otherwise unstable versions of core dependencies like AGP and Navigation is risky. It can introduce build failures, runtime crashes, and unpredictable behavior. Production applications should always prefer stable releases.
    *   **Solution**: Downgrade to the latest stable versions. For example, use a stable AGP version (e.g., `8.4.1`) and a stable Navigation Compose version.

*   **Issue: Mixed Testing Frameworks**
    *   **Finding**: The project includes dependencies for both JUnit 4 (`junit`) and JUnit 5 (`org.junit.jupiter`).
    *   **Why it's an issue**: This creates a mixed, confusing testing environment. The standard is to use one testing framework. JUnit 5 is the modern choice with better features and a more expressive API.
    *   **Solution**: Remove the JUnit 4 dependency from the version catalog and exclusively use JUnit 5 for all unit tests.

### `app/build.gradle.kts`

*   **Issue: Unstable SDK Targeting**
    *   **Finding**: `compileSdk` and `targetSdk` are set to `36`.
    *   **Why it's an issue**: API level 36 is a future, unreleased, and unstable version of the Android SDK. Targeting it is a significant risk and provides no real benefit. The app should target the latest *stable* API level to ensure compatibility and expected behavior on user devices.
    *   **Solution**: Change `compileSdk` and `targetSdk` to the latest stable version (e.g., `34`).

*   **Issue: Minification Disabled for Release Builds**
    *   **Finding**: `isMinifyEnabled` is `false` in the `release` build type.
    *   **Why it's an issue**: This is a critical oversight. Disabling minification for release builds means the application is shipped with unused code (increasing APK size) and is not obfuscated, making it trivial to reverse-engineer.
    *   **Solution**: Set `isMinifyEnabled = true` for the `release` build type. ProGuard/R8 rules will need to be configured correctly, but this is a standard and necessary step for any production app.

*   **Issue: Deprecated Hilt Configuration**
    *   **Finding**: `hilt.enableAggregatingTask = false` is used.
    *   **Why it's an issue**: This is a deprecated workaround for performance issues in older Hilt versions. It's no longer needed and should be removed to allow the Hilt plugin to use its default, optimized build process.
    *   **Solution**: Remove the entire `hilt { ... }` block from the `android` configuration.

---

## 2. Data Layer

### `data/datasource/LocalScoreDataSource.kt`

*   **Issue: Massive, God-Object Data Source**
    *   **Finding**: `LocalScoreDataSource` is a giant, stateful, singleton class that contains all the game's business logic (`incrementScore`, `determineNextServer`, `decrementScore`, etc.).
    *   **Why it's an issue**: This is a classic "God Object" anti-pattern. It violates the Single Responsibility Principle. The business logic for score calculation is tightly coupled to the data source implementation. This makes the code incredibly difficult to test, debug, and maintain. The logic is complex and has no dedicated unit tests. Furthermore, its state is managed in a global, mutable singleton, which is a huge source of bugs and unpredictable behavior.
    *   **Solution**: The business logic must be extracted from this data source. This logic belongs in the *domain layer*, within use cases. For example, create a `CalculateNextGameStateUseCase` that takes the current state and an action (e.g., "increment player 1"), and returns the new `GameState`. The data source should be dumb—its only job is to hold the current state and update it.

*   **Issue: Fragile In-Memory State Management**
    *   **Finding**: The entire game state, including the `previousGameState` for the undo feature, is stored in-memory in a singleton.
    *   **Why it's an issue**: If the Android system kills the app process (which it can do at any time when the app is in the background), the entire game state is lost. When the user returns to the app, the game will have reset to its initial state, which is a terrible user experience. The undo functionality is also extremely brittle, only storing one previous state in a nullable variable.
    *   **Solution**: The `GameState` should be persisted to disk automatically on every change. A second DataStore or a dedicated Room table would be appropriate for this. This ensures that the game can be resumed from where the user left off. The undo/redo functionality should be implemented as a stack of states (a list of `GameState` objects), which would also be persisted.

*   **Issue: Direct Dependency on a Repository**
    *   **Finding**: `LocalScoreDataSource` depends on `SettingsRepository`.
    *   **Why it's an issue**: This inverts the dependency flow of Clean Architecture. A data source (implementation detail) should not depend on a repository (abstraction). The data layer should not know about abstractions in the domain layer. This creates a circular dependency (`ViewModel` -> `UseCase` -> `Repository` -> `DataSource` -> `Repository`).
    *   **Solution**: The use case in the domain layer should be responsible for fetching the settings from `SettingsRepository` and passing the required values to the data source or the logic that calculates the next state.

### `data/datasource/SettingsDataStore.kt`

*   **Issue: Global `Context` Extension**
    *   **Finding**: The DataStore instance is created via a global extension property on `Context`.
    *   **Why it's an issue**: While this is a common pattern shown in documentation examples, it's not ideal for dependency injection. It makes the `DataStore` a global singleton tied to the `Context` implementation, which can make testing difficult. It's better to have dependencies explicitly provided by a DI framework.
    *   **Solution**: Instead of a global extension, provide the `DataStore<Preferences>` instance directly via a Hilt module. This makes dependencies explicit and easier to replace in tests.

### `data/mapper/MatchMapper.kt`

*   **Issue: Loss of Data in Mapping**
    *   **Finding**: When mapping from `MatchEntity` to the domain `Match` model, the player IDs are hardcoded to `0`.
    *   **Why it's an issue**: This is a silent bug. The `Match` model in the domain has `Player` objects which include an `id`, but this ID is being discarded and replaced with a meaningless `0`. If the `Player` model were to be used for anything more complex, this would lead to incorrect behavior.
    *   **Solution**: The `MatchEntity` should store player IDs if they are meaningful. If they are not, the `Player` model in the domain layer is incorrect for this context and should perhaps be simplified to just a name. The data representation should be consistent across layers.

### `data/repository` classes

*   **Issue: Repositories are Pass-Through**
    *   **Finding**: The repositories (`MatchRepositoryImpl`, `ScoreRepositoryImpl`, `SettingsRepositoryImpl`) do nothing but call a single data source.
    *   **Why it's an issue**: This adds a layer of abstraction that provides no value. The purpose of a repository is to *mediate* between data sources (e.g., local and remote) and contain logic to decide where to fetch data from. If there's only one data source, the repository is just boilerplate.
    *   **Solution**: For a simple app like this, you could argue to remove the repository layer and have use cases talk directly to the data source interfaces. However, keeping the repository is fine for future-proofing, but it's important to recognize that in their current state, they are just adding verbosity without any real benefit. No immediate change is required, but it's a code smell to be aware of.

---

## 3. Domain Layer

*   **Issue: Anemic Domain Layer**
    *   **Finding**: The domain layer is almost completely devoid of business logic. The use cases (`IncrementScoreUseCase`, `DecrementScoreUseCase`, etc.) are just simple pass-through calls to the repository. All the critical game logic resides in `LocalScoreDataSource` in the data layer. The `IncrementScoreUseCase` has a small piece of logic to detect a deuce, but the actual score calculation is delegated.
    *   **Why it's an issue**: This is the core architectural flaw of the application. It's an "Anemic Domain Model" anti-pattern. The domain layer, which should be the heart of the application containing all business rules, is just a set of dumb data structures and interfaces. The data layer, which should be dumb persistence, is stateful and smart. This completely inverts the principles of Clean Architecture, making the system rigid, fragile, and difficult to test.
    *   **Solution**: All business logic must be moved from `LocalScoreDataSource` into the domain layer.
        1.  Create a `ScoreCalculator` or a set of pure functions within the domain layer that take a `GameState` and an `Action` and return a new `GameState`. This logic should be thoroughly unit-tested.
        2.  Refactor the use cases to use this new logic. For example, `IncrementScoreUseCase` should fetch the current `GameState`, use the `ScoreCalculator` to produce the new state, and then pass the new state back to the repository for saving.
        3.  The `ScoreRepository` interface should be changed from `incrementScore(...)` to something like `updateGameState(newState: GameState)`.

*   **`domain/model/Player.kt`**
    *   **Issue: Mutable Domain Model**
        *   **Finding**: The `score` property in the `Player` data class is a `var`.
        *   **Why it's an issue**: Domain models should be immutable. Using `var` allows the state to be changed from anywhere, which breaks the Unidirectional Data Flow (UDF) pattern and makes state changes unpredictable and hard to trace. A change to a player's score should result in a *new* `Player` object, not a mutation of the existing one.
        *   **Solution**: Change `score` from a `var` to a `val`. All state changes should be handled by creating new instances of the data classes (using the `copy()` method).

*   **`domain/usecase/IncrementScoreUseCase.kt`**
    *   **Issue: Blocking `first()` Call on a Flow**
        *   **Finding**: The use case collects the *first* emission from two different flows (`scoreRepository.getGameState().first()` and `settingsRepository.getSettings().first()`) to get the current state.
        *   **Why it's an issue**: While this works, it's not robust. It assumes that the flows will emit a value immediately. More importantly, it couples the use case to the fact that the repository is implemented with a `StateFlow` that has an initial value. A better approach is to design the use case to operate on the latest state without being so tightly coupled to the `Flow` implementation detail.
        *   **Solution**: The correct way to combine the latest values from multiple flows is to use the `combine` operator. The use case should combine the `gameState` flow and the `gameSettings` flow, perform the logic, and then call the repository. However, the more pressing issue is moving the logic out of the data layer. Once that is done, this use case would be responsible for orchestrating the fetch, calculate, and save operations.

*   **`domain/usecase/SwitchServeUseCase.kt`**
    *   **Issue: Dead Code**
        *   **Finding**: The file `SwitchServeUseCase.kt` contains only comments stating it is no longer needed and should be deleted.
        *   **Why it's an issue**: Dead code and obsolete files clutter the codebase, create confusion for new developers, and can be accidentally resurrected.
        *   **Solution**: Delete the file.

*   **General Use Case Implementation**
    *   **Issue: Inconsistent Naming**
        *   **Finding**: All use cases have a single public method named `execute`.
        *   **Why it's an issue**: This is a missed opportunity for a common Kotlin convention. By naming the method `invoke` and marking it with the `operator` keyword, the use case class can be called as if it were a function, leading to cleaner and more idiomatic code in the ViewModels (e.g., `incrementScoreUseCase(playerId)` instead of `incrementScoreUseCase.execute(playerId)`).
        *   **Solution**: Rename all `execute` methods to `invoke` and add the `operator` keyword.

---

## 4. UI Layer (Presentation)

### `MainActivity.kt`

*   **Issue: Leaking Domain/Data Layer into the Activity**
    *   **Finding**: `MainActivity` directly injects and uses `SettingsRepository`. It collects a flow from the repository to manage the `FLAG_KEEP_SCREEN_ON` window flag.
    *   **Why it's an issue**: This is a major violation of Clean Architecture. The `Activity` (a UI-layer component) should *never* interact directly with a repository. Its only source of information should be a ViewModel. This tight coupling makes the code hard to manage and test. The Activity now has business logic awareness, which is the ViewModel's job.
    *   **Solution**: Create a `MainViewModel` responsible for this logic. The `MainViewModel` would inject the `SettingsRepository` (or a `GetKeepScreenOnUseCase`), expose a simple `StateFlow<Boolean>` for the "keep screen on" setting, and the `MainActivity` would collect this state from the ViewModel. This restores the correct architectural layering.

### `ui/scorescreen/ScoreViewModel.kt`

*   **Issue: Unnecessary Nullable State**
    *   **Finding**: `_gameState` and `_gameSettings` are both `MutableStateFlow<T?>` initialized with `null`. The UI then has to do null checks before it can render.
    *   **Why it's an issue**: This introduces complexity and potential `NullPointerException`s for no reason. The data sources (`LocalScoreDataSource` and `SettingsLocalDataSource`) both provide initial, non-null default values. The state should never be null. This creates boilerplate in the UI to handle a loading state that shouldn't exist.
    *   **Solution**: Initialize the `StateFlow`s in the ViewModel with the initial non-null values from the repositories. This can be done by changing the repository methods to return a `stateIn` flow that is pre-seeded, or by having the ViewModel provide a default value. The UI can then bind directly to a non-nullable `GameState` and `GameSettings`.

*   **Issue: Logic in `resetGame()`**
    *   **Finding**: The `resetGame` function in the ViewModel contains logic to determine the winner of the previous game before calling the use case.
    *   **Why it's an issue**: This is business logic. While minor, it belongs in a use case, not the ViewModel. The ViewModel's job is to delegate, not to compute.
    *   **Solution**: Move this winner-detection logic into the `ResetGameUseCase`. The ViewModel should just trigger the action.

### `ui/scorescreen/ScoreScreen.kt`

*   **Issue: Massive, Monolithic Composable**
    *   **Finding**: `ScoreScreen.kt` is a huge file containing multiple large composables (`ScoreScreenPortrait`, `ScoreScreenLandscape`, `PlayerScoreCard`, `CentralControls`, etc.) and a significant amount of preview code with fake repository implementations.
    *   **Why it's an issue**: This makes the file difficult to navigate, read, and maintain. It violates the principle of separation of concerns. Previews are cluttered with fake implementations that are almost as complex as the real ones.
    *   **Solution**:
        1.  Break down the screen into smaller, more focused composable files. `PlayerScoreCard`, `BottomBarActions`, `CentralControls`, etc., should all be in their own files within a `components` sub-package.
        2.  The fake repositories and preview setup code should be moved to a dedicated file (e.g., `ScoreScreenPreviewParameterProvider.kt`) to clean up the main screen file. Use the `@PreviewParameter` annotation to supply preview data in a more organized way.

*   **Issue: Passing `NavController` Deeply**
    *   **Finding**: `NavController` is passed down from `ScoreScreen` to `ScoreScreenPortrait`, `ScoreScreenLandscape`, and then to `CentralControls`.
    *   **Why it's an issue**: This is a common anti-pattern in Compose. It makes composables less reusable and harder to test, as they are now coupled to the navigation framework. A composable's responsibility is to display state and emit events. It shouldn't know *how* to navigate.
    *   **Solution**: Hoist the navigation events up. Instead of passing `navController`, the lower-level composables should accept lambdas for navigation events (e.g., `onNavigateToSettings: () -> Unit`). The top-level composable (`ScoreScreen`) is then responsible for implementing these lambdas by calling `navController`.

### `ui/settings/SettingsViewModel.kt`

*   **Issue: Boilerplate Update Functions**
    *   **Finding**: The ViewModel has a separate update function for every single setting (e.g., `updateShowTitle`, `updateShowNames`, `updateMarkServe`).
    *   **Why it's an issue**: This is a lot of repetitive boilerplate code. It's verbose and error-prone. A single, more generic event handler would be much cleaner.
    *   **Solution**: Create a single `updateSettings(newSettings: GameSettings)` function or a sealed interface for `SettingsEvent`. The UI would call this single entry point with the modified settings object or event, and the ViewModel would handle the update and save. This reduces the API surface of the ViewModel and eliminates redundant code.

### `ui/settings/SettingsScreen.kt`

*   **Issue: Hardcoded Settings Structure**
    *   **Finding**: The lists of settings (`gameControls`, `tableTennisRules`) are constructed directly inside the `SettingsScreen` composable.
    *   **Why it's an issue**: This mixes the "what" (the data) with the "how" (the UI). It makes the screen inflexible. If the settings were to become dynamic (e.g., loaded from a remote config), this structure would require a major refactor. It also makes the composable itself very large and hard to read.
    *   **Solution**: The ViewModel should be responsible for preparing the list of `SettingItemData`. The ViewModel would expose a `StateFlow<List<SettingItemData>>` and the `SettingsScreen` would be a dumb composable that just knows how to render that list. This separates the data preparation from the UI rendering.

---

## 5. Dependency Injection

### `di/DataModule.kt`

*   **Issue: Circular Dependency**
    *   **Finding**: `DataModule` provides `LocalScoreDataSource`. The provider function for `LocalScoreDataSource` takes `SettingsRepository` as a parameter. However, `SettingsRepositoryImpl` (the implementation for `SettingsRepository`) depends on `SettingsLocalDataSource`, which in turn depends on the `DataStore`. This creates a dependency chain of `Data Layer -> Domain Layer -> Data Layer`, which is a circular dependency at an architectural level.
    *   **Why it's an issue**: This is a direct symptom of the "Anemic Domain Model" problem identified earlier. A data source in the data layer should not depend on a repository in the domain layer. This breaks the architectural layering and makes the dependency graph confusing and incorrect.
    *   **Solution**: This must be fixed as part of the larger domain layer refactoring. The `LocalScoreDataSource` should be "dumb" and should not need the `SettingsRepository`. The business logic that requires settings should be moved to a use case in the domain layer, which will correctly depend on the `SettingsRepository`.

*   **Issue: Module Responsibilities are Unclear**
    *   **Finding**: `DataModule.kt` provides a mix of things: the Room database, a DAO, a data source, and the DataStore.
    *   **Why it's an issue**: The module's name is `DataModule`, but it's providing things that could arguably be in more specific modules. For example, `provideLocalScoreDataSource` could be in `DataSourceModule`. This makes it harder to find where a specific dependency is provided.
    *   **Solution**: Reorganize the Hilt modules for better clarity.
        1.  Create a `DatabaseModule.kt` to provide the `AppDatabase` and all DAOs.
        2.  Move the `provideLocalScoreDataSource` and `provideSettingsDataStore` into `DataSourceModule.kt`.
        3.  This leaves `DataModule.kt` empty, so it could be deleted. Clear, single-purpose modules are easier to maintain.

### `di/DataSourceModule.kt` & `di/RepositoryModule.kt`

*   **Issue: Unnecessary `@Singleton` on `@Binds` Methods**
    *   **Finding**: The `@Binds` methods in `RepositoryModule` and `DataSourceModule` are annotated with `@Singleton`.
    *   **Why it's an issue**: When using `@Binds`, Hilt inherits the scope from the concrete implementation class. `LocalMatchDataSource`, `ScoreRepositoryImpl`, `SettingsRepositoryImpl`, and `MatchRepositoryImpl` are all already annotated with `@Singleton` (or their dependencies are, which makes them singletons by extension). Adding `@Singleton` to the `@Binds` method is redundant and adds unnecessary verbosity.
    *   **Solution**: Remove the `@Singleton` annotation from all the `@Binds` functions in these modules. The scope will be correctly inferred from the implementation class.

---

## 6. Testing

*   **Issue: Critically Low Test Coverage**
    *   **Finding**: There are only two test files in the entire project: `ExampleInstrumentedTest` (an auto-generated placeholder) and `SettingsViewModelTest`. There are no tests for the most critical and complex piece of logic in the app: the score calculation in `LocalScoreDataSource`. There are also no tests for any other ViewModels, UseCases, Mappers, or Repositories.
    *   **Why it's an issue**: This is the most severe problem after the architectural flaws. Without tests, there is no safety net. Refactoring the massive `LocalScoreDataSource` (as is urgently required) is incredibly risky and will almost certainly introduce regressions. The lack of tests means the application is unreliable and cannot be safely modified or scaled.
    *   **Solution**: A comprehensive test suite must be written.
        1.  **Top Priority**: Before any refactoring, write extensive unit tests for the existing logic in `LocalScoreDataSource`. Test every possible scenario: score increments, set wins, match wins, deuce logic, server rotation, etc. These tests will fail initially but will serve as a guide and safety net for the refactoring.
        2.  After refactoring the business logic into the domain layer, write clean, focused unit tests for the new `ScoreCalculator` or use cases.
        3.  Write unit tests for all other ViewModels (`ScoreViewModel`, `MatchHistoryViewModel`).
        4.  Write unit tests for all mappers.
        5.  Add UI tests with Compose to verify screen behavior, especially for the `ScoreScreen` and its different states (in-progress, deuce, finished).

*   **`test/ui/settings/SettingsViewModelTest.kt`**
    *   **Issue: Repetitive and Bloated Tests**
        *   **Finding**: The test class has a separate test function for every single setting update (e.g., `updateShowTitle`, `updateShowNames`, etc.). These tests are nearly identical, leading to a huge amount of boilerplate code.
        *   **Why it's an issue**: This makes the test suite verbose, hard to read, and difficult to maintain. If a new setting is added, another large, copy-pasted test function is required.
        *   **Solution**: Use parameterized tests. With JUnit 5, this is straightforward using `@ParameterizedTest` and a `@MethodSource` or `@ValueSource`. A single test function, `testSettingUpdate`, could take the setting-update function and the expected value as parameters, drastically reducing the code size and improving maintainability.

    *   **Issue: Unnecessary Main Dispatcher Manipulation**
        *   **Finding**: The tests use `Dispatchers.setMain(testDispatcher)` and `Dispatchers.resetMain()`.
        *   **Why it's an issue**: While this was a common pattern, the modern and recommended approach for testing coroutines is to use a `MainCoroutineRule` (for JUnit 4) or a custom JUnit 5 extension. This encapsulates the dispatcher setup and teardown, making the tests cleaner and less error-prone.
        *   **Solution**: Implement a `MainCoroutineExtension` for JUnit 5 that handles setting and resetting the main dispatcher. Apply it to the test class using `@ExtendWith(MainCoroutineExtension::class)`. This removes the manual `setUp` and `tearDown` logic from every test class.

*   **`androidTest/ExampleInstrumentedTest.kt`**
    *   **Issue: Placeholder Test**
        *   **Finding**: This is the default instrumented test generated by Android Studio. It does nothing but check the application's package name.
        *   **Why it's an issue**: It provides zero value and gives a false sense of having an instrumentation test suite.
        *   **Solution**: Delete this file and replace it with meaningful instrumentation tests that verify UI behavior or database operations. For example, a test that launches `MainActivity` and verifies that the `ScoreScreen` is displayed.

---

## 7. Android Manifest & Resources

### `app/src/main/AndroidManifest.xml`

*   **Issue: Missing `android:usesCleartextTraffic` attribute**
    *   **Finding**: The `<application>` tag does not explicitly set `android:usesCleartextTraffic`.
    *   **Why it's an issue**: While this app doesn't currently make network requests, it's a best practice to explicitly set this to `false`. This prevents the app from accidentally sending data over unencrypted connections in the future, which is a security risk.
    *   **Solution**: Add `android:usesCleartextTraffic="false"` to the `<application>` tag in the manifest.

*   **Issue: Default Backup Rules**
    *   **Finding**: The app uses the default `dataExtractionRules` and `fullBackupContent` rules.
    *   **Why it's an issue**: The default Auto Backup configuration saves most of the app's files to a user's Google Drive. This can include sensitive information or large files that shouldn't be backed up. For this app, it's backing up the Room database and the DataStore preferences, which is good, but it's not explicit. It's always better to be explicit about what is and isn't backed up.
    *   **Solution**: Review the `backup_rules.xml` and `data_extraction_rules.xml` files. Explicitly include the database and DataStore files and exclude everything else. This ensures that only the necessary data is backed up and prevents accidental data leaks if new files are added to the app's storage in the future.

### `app/src/main/res/values/strings.xml`

*   **Issue: Hardcoded Strings in UI Code**
    *   **Finding**: Throughout the UI code (e.g., `ScoreScreen.kt`, `SettingsScreen.kt`), there are many hardcoded strings like "Table Tennis", "Settings", "Reset", "New Game", "DEUCE", etc. The `strings.xml` file only contains three strings.
    *   **Why it's an issue**: Hardcoding strings is a major anti-pattern. It makes the app impossible to localize into other languages. It also makes it difficult to manage and update UI text, as developers have to hunt through the code instead of looking in a single resource file.
    *   **Solution**: Externalize all user-facing strings into `res/values/strings.xml`. Replace every hardcoded string in the Composables with a `stringResource(R.string.your_string_name)` call. This is a critical step for maintainability and localization.

---

## Summary & Final Recommendations

This audit has revealed several critical issues that compromise the stability, maintainability, and quality of the ScoreCount application. While the project attempts to follow Clean Architecture, it fails in the most important aspect: the separation of business logic from other layers.

**The top three most critical issues that must be addressed immediately are:**

1.  **Anemic Domain Model**: The core business logic resides in the data layer (`LocalScoreDataSource`) instead of the domain layer. This is a fundamental architectural flaw that must be corrected by moving all game logic into domain-layer use cases or services.
2.  **Lack of Test Coverage**: The absence of a meaningful test suite, especially for the complex scoring logic, is a huge risk. No refactoring should be attempted until a comprehensive suite of unit and integration tests is in place to prevent regressions.
3.  **Fragile State Management**: Storing the entire game state in a volatile, in-memory singleton guarantees data loss. The `GameState` must be persisted to disk on every change to provide a reliable user experience.

Other significant issues include disabled minification in release builds, use of unstable dependencies, leaking architectural layers into the UI (`MainActivity`), and rampant use of hardcoded strings.

**Recommended Action Plan:**

1.  **Write Tests**: Before touching any code, write a full suite of unit tests for the existing `LocalScoreDataSource` to document and verify its current behavior.
2.  **Refactor the Core Logic**: Move all business logic from `LocalScoreDataSource` into new use cases within the domain layer. The data layer should become a dumb persistence mechanism.
3.  **Persist Game State**: Modify the implementation to save the `GameState` to a persistent store (DataStore or Room) on every change.
4.  **Address Build & Config Issues**: Fix the build files to use stable dependencies, enable minification, and remove hardcoded strings.
5.  **Clean Up UI and DI**: Refactor the UI layer to remove logic from the Activity, eliminate monolithic composables, and clean up the Hilt modules.

This app has a decent foundation in its project structure, but the implementation details betray the architectural intent. A significant refactoring effort, guided by a robust test suite, is required to bring this application up to a professional standard.
