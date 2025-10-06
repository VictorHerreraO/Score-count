# Score-Count Application Architecture

## Overview

The Score-Count application follows a layered architecture pattern, influenced by Clean Architecture principles, to promote separation of concerns, testability, and maintainability.

## Layers

### 1. UI (Presentation Layer)

*   **Framework**: Jetpack Compose for declarative UI development.
*   **Components**:
    *   **Screens (`ScoreScreen.kt`, `SettingsScreen.kt`, `MatchHistoryScreen.kt`)**: Composables responsible for displaying data and capturing user input. They observe state from ViewModels.
    *   **ViewModels (`ScoreViewModel.kt`, `SettingsViewModel.kt`, `MatchHistoryViewModel.kt`)**: Prepare and manage data for the UI. They expose UI state (e.g., `GameState`, `GameSettings`) as `StateFlow`s and handle user actions by delegating to UseCases.
    *   **Navigation**: Jetpack Navigation Compose (`NavHost`, `Screen.kt` for routes) is used for navigating between screens.
    *   **Preview Data**: Fake/Preview versions of Repositories are used within screen previews to provide sample data and facilitate UI development.
*   **Responsibilities**: Displaying application state, handling user interactions, and delegating business logic to lower layers.

### 2. Domain Layer

*   **Components**:
    *   **Models (`GameState.kt`, `Player.kt`, `GameSettings.kt`, `Match.kt`)**: Kotlin data classes representing the core entities of the application. These are plain Kotlin objects with no Android framework dependencies.
    *   **UseCases (e.g., `IncrementScoreUseCase.kt`, `GetMatchesUseCase.kt`, `SaveMatchUseCase.kt`)**: Encapsulate specific pieces of business logic. They are invoked by ViewModels and interact with Repositories.
    *   **Repository Interfaces (`ScoreRepository.kt`, `SettingsRepository.kt`, `MatchRepository.kt`)**: Define contracts for data access. These interfaces are implemented by the Data layer.
*   **Responsibilities**: Contains the core business logic and rules of the application. This layer is independent of UI and Data implementation details.

### 3. Data Layer

*   **Components**:
    *   **Repositories (`ScoreRepositoryImpl.kt`, `SettingsRepositoryImpl.kt`, `MatchRepositoryImpl.kt`)**: Implement the Repository interfaces. They coordinate data from different data sources.
    *   **Data Sources**:
        *   **Local**: Manage data persistence locally.
            *   `LocalScoreDataSource`: Manages the `GameState` in memory.
            *   `SettingsLocalDataSource`: Persists `GameSettings` using Android's `Preferences DataStore`.
            *   `LocalMatchDataSource`: Fetches and saves match data from the Room database.
    *   **Database (`AppDatabase.kt`)**: A Room database that holds application data.
        *   `MatchDao`: Defines the data access methods for match history.
        *   `MatchEntity`: Represents a match record in the database.
    *   **Mappers (`MatchMapper.kt`)**: Convert data between data layer entities and domain layer models.
*   **Responsibilities**: Data retrieval, storage, and management. Abstracts the origin of the data from the Domain layer.

## Dependency Injection

*   **Framework**: Hilt is used for managing dependencies throughout the application.
*   **Modules (`DataModule.kt`, `RepositoryModule.kt`, `DataSourceModule.kt`)**: Define how dependencies are provided and injected.

## Key Architectural Decisions & Patterns

*   **Unidirectional Data Flow (UDF)**: UI observes state from ViewModels, and user actions flow from the UI to ViewModels, which then update the state.
*   **State Management**: `StateFlow` from Kotlin Coroutines is used to expose observable state.
*   **Immutability**: Domain models are handled as immutable data classes.
*   **Repository Pattern**: Decouples the Domain layer from data source implementations.
*   **UseCase Pattern**: Encapsulates discrete units of business logic, promoting reusability and testability.
*   **Room for Persistence**: Room is used for local database storage to persist match history.
*   **Testability**: The architecture promotes testability. Unit tests are implemented for components like ViewModels, which rely on fakes or mocks of their dependencies.

## Project Structure (Simplified)

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/soyvictorherrera/scorecount/
│   │   │   ├── di/                 # Hilt Modules
│   │   │   ├── domain/
│   │   │   │   ├── model/          # Domain Models
│   │   │   │   ├── repository/     # Repository Interfaces
│   │   │   │   └── usecase/        # Business Logic Classes
│   │   │   ├── data/
│   │   │   │   ├── database/       # Room Database (AppDatabase, DAOs, Entities)
│   │   │   │   ├── datasource/     # Data Source Implementations
│   │   │   │   ├── mapper/         # Data Mappers
│   │   │   │   └── repository/     # Repository Implementations
│   │   │   ├── ui/
│   │   │   │   ├── scorescreen/
│   │   │   │   ├── settings/
│   │   │   │   ├── matchhistory/
│   │   │   │   └── navigation/     # Screen.kt, NavHost setup
│   │   │   └── ScoreCountApplication.kt
│   │   └── res/
│   ├── test/java/com/soyvictorherrera/scorecount/ # Unit tests
│   └── AndroidManifest.xml
├── build.gradle.kts
ARCHITECTURE.md  # This file
```

## Diagram (Conceptual)

```
+---------------------+     +---------------------+     +----------------------------+
|         UI          | --> |       Domain        | --> |            Data            |
| (Compose, ViewModel)|     | (Models, UseCases,  |     | (Repositories, Mappers,   |
|                     | <-- |  Repo Interfaces)   | <-- |  DataSources, Database)  |
+---------------------+     +---------------------+     +----------------------------+
          ^                           ^                           ^
          |                           |                           |
          +---------------------------HILT--------------------------+
                                (Dependency Injection)
```

This architecture aims to create a scalable, testable, and maintainable codebase for the Score-Count application.
