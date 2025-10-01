# Score-Count Application Architecture

## Overview

The Score-Count application follows a layered architecture pattern, influenced by Clean Architecture principles, to promote separation of concerns, testability, and maintainability.

## Layers

### 1. UI (Presentation Layer)

*   **Framework**: Jetpack Compose for declarative UI development.
*   **Components**:
    *   **Screens (`ScoreScreen.kt`, `SettingsScreen.kt`)**: Composables responsible for displaying data and capturing user input. They observe state from ViewModels.
    *   **ViewModels (`ScoreViewModel.kt`, `SettingsViewModel.kt`)**: Prepare and manage data for the UI. They expose UI state (e.g., `GameState`, `GameSettings`) as `StateFlow`s and handle user actions by delegating to UseCases or directly to Repositories for simple operations.
    *   **Navigation**: Jetpack Navigation Compose (`NavHost`, `Screen.kt` for routes) is used for navigating between screens.
    *   **Preview Data**: Fake/Preview versions of Repositories are used within screen previews to provide sample data and facilitate UI development (e.g., `PreviewSettingsRepository` in `SettingsScreen.kt`, `FakeScoreRepositoryPreview` in `ScoreScreen.kt`).
*   **Responsibilities**: Displaying application state, handling user interactions, and delegating business logic to lower layers.

### 2. Domain Layer

*   **Components**:
    *   **Models (`GameState.kt`, `Player.kt`, `GameSettings.kt`)**: Kotlin data classes representing the core entities and state of the application. These are plain Kotlin objects with no Android framework dependencies.
        *   `GameState`: Holds the current state of a game, including player scores, set scores, serving player, and whether the game is finished.
        *   `GameSettings`: Holds all user-configurable settings that affect game rules and UI display.
    *   **UseCases (e.g., `IncrementScoreUseCase.kt`, `ManualSwitchServeUseCase.kt`, `GetGameStateUseCase.kt`, `GetSettingsUseCase.kt`, `SaveSettingsUseCase.kt`)**: Encapsulate specific pieces of business logic. They are invoked by ViewModels and interact with Repositories.
    *   **Repository Interfaces (`ScoreRepository.kt`, `SettingsRepository.kt`)**: Define contracts for data access. These interfaces are implemented by the Data layer.
*   **Responsibilities**: Contains the core business logic and rules of the application. This layer is independent of UI and Data implementation details.

### 3. Data Layer

*   **Components**:
    *   **Repositories (`ScoreRepositoryImpl.kt`, `SettingsRepositoryImpl.kt`)**: Implement the Repository interfaces defined in the Domain layer. They coordinate data fetching from one or more Data Sources.
    *   **Data Sources**:
        *   **Local (`LocalScoreDataSource.kt`, `SettingsLocalDataSource.kt`)**: Manage data persistence locally.
            *   `LocalScoreDataSource`: Manages the `GameState` in memory (using `MutableStateFlow`). Contains the core game logic for score updates, set/match completion, and serve switching, all influenced by `GameSettings`.
            *   `SettingsLocalDataSource`: Persists `GameSettings` using Android's `SharedPreferences`.
        *   *(Remote Data Sources would reside here if the application had network interactions)*
*   **Responsibilities**: Data retrieval, storage, and management. Abstracts the origin of the data (e.g., memory, database, network) from the Domain layer.

## Dependency Injection

*   **Framework**: Hilt is used for managing dependencies throughout the application.
*   **Modules (`DataModule.kt`, `RepositoryModule.kt`, `AppModule.kt`)**: Define how dependencies are provided and injected (e.g., providing `SharedPreferences`, binding Repository implementations to their interfaces).

## Key Architectural Decisions & Patterns

*   **Unidirectional Data Flow (UDF)**: UI observes state from ViewModels, and user actions flow from the UI to ViewModels, which then update the state.
*   **State Management**: `StateFlow` from Kotlin Coroutines is used to expose observable state from ViewModels and Data Sources.
*   **Immutability**: `GameState` and `GameSettings` are primarily handled as immutable data classes. Updates involve creating new instances with modified values.
*   **Repository Pattern**: Decouples the Domain layer from data source implementations.
*   **UseCase Pattern**: Encapsulates discrete units of business logic, promoting reusability and testability.
*   **Settings Integration**: Game settings are deeply integrated into the `LocalScoreDataSource` to dynamically control game rules and behavior.

## Project Structure (Simplified)

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/soyvictorherrera/scorecount/
│   │   │   ├── di/                 # Hilt Modules
│   │   │   ├── domain/
│   │   │   │   ├── model/          # GameState, Player, GameSettings
│   │   │   │   ├── repository/     # ScoreRepository, SettingsRepository (interfaces)
│   │   │   │   └── usecase/        # Business logic classes
│   │   │   ├── data/
│   │   │   │   ├── datasource/     # LocalScoreDataSource, SettingsLocalDataSource
│   │   │   │   └── repository/     # ScoreRepositoryImpl, SettingsRepositoryImpl
│   │   │   ├── ui/
│   │   │   │   ├── scorescreen/    # ScoreScreen.kt, ScoreViewModel.kt
│   │   │   │   ├── settings/       # SettingsScreen.kt, SettingsViewModel.kt
│   │   │   │   └── navigation/     # Screen.kt, NavHost setup
│   │   │   └── ScoreCountApplication.kt # Application class
│   │   └── res/                    # Android resources
│   └── AndroidManifest.xml
├── build.gradle.kts
ARCHITECTURE.md  # This file
ACTIVE_CONTEXT.md
```

## Diagram (Conceptual)

```
+---------------------+     +---------------------+     +----------------------+
|        UI           | --> |       Domain        | --> |         Data         |
| (Compose, ViewModel)|     | (Models, UseCases,  |     | (Repositories,       |
|                     | <-- |  Repo Interfaces)   | <-- |  DataSources)        |
+---------------------+     +---------------------+     +----------------------+
          ^                           ^                           ^
          |                           |                           |
          +---------------------------HILT--------------------------+
                                (Dependency Injection)
```

This architecture aims to create a scalable, testable, and maintainable codebase for the Score-Count application.
