# Architecture

This document contains technical documentation about the Score-Count project.

The Score-Count application follows a layered architecture based on modern Android development practices, emphasizing separation of concerns, testability, and maintainability.

## Layers

The architecture is primarily divided into three main layers:

1.  **UI Layer**: Responsible for displaying application data on the screen and handling user interactions.
    *   **Activity (`MainActivity.kt`)**: The main entry point of the application, annotated with `@AndroidEntryPoint` for Hilt integration. It hosts the Jetpack Compose UI.
    *   **Compose UI (`ui/scorescreen/ScoreScreen.kt`)**: Jetpack Compose is used for building the user interface. The main screen is `ScoreScreen.kt`, which displays player scores, serving indicators, and action buttons.
    *   **ViewModel (`ui/scorescreen/ScoreViewModel.kt`)**: `ScoreViewModel` acts as a state holder for the `ScoreScreen`. It's annotated with `@HiltViewModel` and dependencies (Use Cases) are injected via its constructor. It prepares and manages UI-related data, exposes it to the Composable UI via `StateFlow`, and handles user actions by delegating to the domain layer.
    *   **Theme (`ui/theme/`)**: This package contains Compose UI theming definitions (`Color.kt`, `Type.kt`, `Theme.kt`), including light and dark color schemes and typography.

2.  **Domain Layer**: Contains the core business logic of the application. This layer is independent of the UI and Data layers.
    *   **Models (`domain/model/`)**:
        *   `Player.kt`: Represents a player with an `id: Int`, `name: String`, and `score: Int`.
        *   `GameState.kt`: Represents the overall state of the game, including two `Player` objects, the `servingPlayerId: Int?` (ID of the serving player), and `isFinished: Boolean`.
    *   **Use Cases (`domain/usecase/`)**: Encapsulate specific business operations. Each use case typically has a single public `execute` method and injects `ScoreRepository`.
        *   `GetGameStateUseCase.kt`: Retrieves the current game state.
        *   `IncrementScoreUseCase.kt`: Increments a player's score.
        *   `DecrementScoreUseCase.kt`: Decrements a player's score.
        *   `SwitchServeUseCase.kt`: Switches the serving player.
        *   `ResetGameUseCase.kt`: Resets the game to its initial state.
        *   `UndoLastActionUseCase.kt`: Reverts the game to its previous state (logic might be a placeholder or viewmodel-managed initially).
    *   **Repository Interface (`domain/repository/ScoreRepository.kt`)**: Defines the contract for data operations related to the game score and state. This allows the domain layer to be independent of specific data source implementations.

3.  **Data Layer**: Responsible for providing and managing data for the application.
    *   **Repository Implementation (`data/repository/ScoreRepositoryImpl.kt`)**: Implements the `ScoreRepository` interface. It injects and orchestrates data from one or more data sources.
    *   **Data Sources (`data/datasource/LocalScoreDataSource.kt`)**: Provides an abstraction for storing and retrieving game state. Currently, this is an in-memory implementation that manages `GameState`.

## Data Flow

*   User interaction in the **UI Layer** (e.g., button click on `ScoreScreen`) calls a method in the `ScoreViewModel`.
*   The `ScoreViewModel` invokes the appropriate **Use Case** from the Domain Layer.
*   The Use Case executes business logic, interacting with the `ScoreRepository` to fetch or modify data.
*   The `ScoreRepository` (implemented in the Data Layer by `ScoreRepositoryImpl`) retrieves or persists data using one or more Data Sources (e.g., `LocalScoreDataSource`).
*   Data flows back up the chain: Data Source -> Repository -> Use Case -> ViewModel.
*   The `ScoreViewModel` updates its `StateFlow` (often by re-fetching state from the Use Case after an action), which the Compose UI (`ScoreScreen`) observes and re-renders accordingly.

## Dependency Management

*   **Hilt** is used for dependency injection throughout the application.
*   **Application Class**: `com.soyvictorherrera.scorecount.ScoreCountApplication` (defined in `ScoreCountApplication.kt`) is annotated with `@HiltAndroidApp` to enable Hilt in the application. This class is registered in the `AndroidManifest.xml`.
*   **ViewModel Injection**: ViewModels, such as `ScoreViewModel`, are annotated with `@HiltViewModel`. Dependencies (like Use Cases) are injected into their constructors using `@Inject`.
*   **Activity/Fragment Injection**: UI entry points like `MainActivity` are annotated with `@AndroidEntryPoint` to allow Hilt to inject dependencies, including ViewModels using `hiltViewModel()`.
*   **Providing Dependencies**: Hilt modules (e.g., `DataModule.kt`, `RepositoryModule.kt` located in `di` package) are used to define how Hilt should provide instances of interfaces (like `ScoreRepository` by binding it to `ScoreRepositoryImpl`) or classes that cannot be constructor-injected (e.g., `LocalScoreDataSource` provided as a singleton).

## Package Structure

The project follows a feature-oriented package structure within functional layers:

*   `com.soyvictorherrera.scorecount`
    *   `di/` (Dependency Injection modules for Hilt)
    *   `ui/`
        *   `scorescreen/` (UI for the main score screen: `ScoreScreen.kt`, `ScoreViewModel.kt`)
        *   `theme/` (Compose theming: `Color.kt`, `Type.kt`, `Theme.kt`)
    *   `domain/`
        *   `model/` (`Player.kt`, `GameState.kt`)
        *   `usecase/` (All use case classes)
        *   `repository/` (`ScoreRepository.kt` interface)
    *   `data/`
        *   `repository/` (`ScoreRepositoryImpl.kt`)
        *   `datasource/` (`LocalScoreDataSource.kt`)
    *   `ScoreCountApplication.kt` (Application class for Hilt)
    *   `MainActivity.kt` (Main UI Activity)

This architecture is designed to be scalable, testable, and maintainable.
