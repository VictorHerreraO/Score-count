# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Critical Evaluation - READ THIS FIRST

**Before implementing ANY task, you MUST:**

1. **Question the premise** - Does this task solve a real problem or is it refactoring for refactoring's sake?
2. **Analyze the trade-offs** - Will this make the code objectively better or just different?
3. **Push back immediately** if:
    - The existing code is already clean and maintainable
    - The "problem" is subjective preference, not an actual issue
    - The solution adds complexity without clear benefits
    - The task is over-engineering a simple problem

**Your job is to provide engineering judgment, not blindly execute tasks.**

If a task doesn't pass critical evaluation, respond with:

- Clear analysis of why the current approach is adequate
- Specific trade-offs the proposed change would introduce
- Recommendation to close/modify the task

**Do NOT waste time implementing unnecessary changes.** Resources are finite.

## Project Overview

Score-Count is an Android table tennis score tracking application built with Kotlin and Jetpack Compose. It follows Clean Architecture with three layers: UI (Compose), Domain (business logic), and Data (repositories/persistence).

**For detailed architecture information, see [ARCHITECTURE.md](ARCHITECTURE.md).**

## Project Progress Tracking

This project uses dedicated files for tracking work progress:

- **[MEMORY.md](MEMORY.md)**: Current in-progress work and next steps

When working on this project, update these files to maintain continuity across sessions.

## Essential Commands

### Build & Install

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install on device/emulator
./gradlew clean build            # Clean rebuild
```

### Testing

```bash
./gradlew test                   # Run all unit tests
./gradlew app:test               # Run app module tests
./gradlew connectedAndroidTest   # Run instrumented tests (needs device)

# Run specific test class
./gradlew test --tests "com.soyvictorherrera.scorecount.ui.settings.SettingsViewModelTest"
```

### Code Quality

```bash
./gradlew lint                   # Run lint checks
./gradlew lintDebug              # Generate lint report
./gradlew ktlintCheck            # Check Kotlin code formatting
./gradlew ktlintFormat           # Auto-fix Kotlin code formatting
```

### Release Management

```bash
# Trigger release build manually
gh workflow run release-build.yml --ref main

# List recent releases
gh release list

# Download latest release APK
gh release download --pattern '*.apk'

# Create a new version tag
git tag -a v1.1.0 -m "Release v1.1.0"
git push origin v1.1.0
```

## Quick Reference

### Package Structure

```
com.soyvictorherrera.scorecount/
├── di/                 # Hilt DI modules
├── domain/             # Business logic (models, use cases, repo interfaces)
├── data/               # Data layer (repos, data sources, Room DB, mappers)
└── ui/                 # Compose UI (screens, ViewModels, navigation)
```

### Key Files & Locations

- **Navigation routes**: `ui/Screen.kt`
- **Hilt modules**: `di/DataModule.kt`, `di/RepositoryModule.kt`, `di/DataSourceModule.kt`
- **Database**: `data/database/AppDatabase.kt`, `data/database/dao/MatchDao.kt`
- **Domain models**: `domain/model/` (GameState, Player, GameSettings, Match)
- **Use cases**: `domain/usecase/` (IncrementScoreUseCase, SaveMatchUseCase, etc.)

### Configuration

- **Package**: `com.soyvictorherrera.scorecount`
- **Min SDK**: 28 | **Target SDK**: 36 | **JVM**: 11
- **Dependencies**: Managed via `gradle/libs.versions.toml`
- **ProGuard**: Enabled for release builds
- **Code Style**: Enforced by ktlint (see `.editorconfig` for rules)

## Development Workflow

### Adding New Features

See [ARCHITECTURE.md](ARCHITECTURE.md) for the complete workflow. Quick summary:

1. Domain models → Repository interface → Use cases
2. Repository implementation → Data sources
3. ViewModel → Compose UI
4. Wire with Hilt modules

### Important Patterns

- **State management**: ViewModels expose `StateFlow` (see ARCHITECTURE.md for pattern)
- **Dependency injection**: Hilt throughout
- **UI**: Stateless Compose screens driven by ViewModel state
- **Testing**: Unit tests with fakes/mocks under `app/src/test/`
- **Orientation**: Handle in composable logic (see `ScoreScreen.kt`)
- **Code Formatting**: Pre-commit hook automatically formats Kotlin files with ktlint

### Branching Strategy

This project uses a **git-flow** branching model:

- **`develop`** (default branch): Integration branch for active development
  - Feature branches should target `develop`
  - Continuous integration runs on all PRs to `develop`
  - This is where new features are tested and integrated

- **`main`** (release branch): Production-ready code
  - Only receives PRs from `develop` when a new version is ready for release
  - Each merge to `main` **automatically triggers a release**
  - Do NOT commit directly to `main`

### Release Process

1. **Complete feature development** on feature branches targeting `develop`
2. **Bump version** in `version.txt` (manually) - follow semantic versioning (MAJOR.MINOR.PATCH)
3. **Create PR from `develop` → `main`** - this triggers the release workflow
4. **Workflow automatically**:
   - Builds a signed release APK
   - Creates a GitHub Release with tag `v{version}`
   - Uploads APK to the release
5. **Download APK** from GitHub Releases or Actions artifacts

### Version Catalog

Add dependencies to `gradle/libs.versions.toml` before using them in build files.

## Development Methodology

When working on features or changes:

1. **Reason**: Analyze the goal or situation
2. **Plan**: Break down into actionable steps
3. **Validate**: Review plan and confirm approach
4. **Act**: Execute the steps
5. **Document**: Update MEMORY.md as appropriate

Before starting work, always reference ARCHITECTURE.md to understand the layered architecture and ensure changes align with established patterns.

## Feature Development Workflow

1. **Update repository**: `git pull origin develop` to get latest changes
2. **Create feature branch**: `[issue-type]/task-xx-short-task-description` from `develop`
3. **Make commits**: Small, meaningful commits with clear messages
4. **Create PR**: Target `develop` branch (NOT `main`)
5. **Review & Merge**: Don't merge yourself - allow manual review and merge

**Example**:
```bash
git checkout develop
git pull origin develop
git checkout -b feature/task-42-add-match-history
# ... make changes ...
git commit -m "feat: add match history screen"
git push origin feature/task-42-add-match-history
# Create PR targeting develop
```

**Release Flow**: Once `develop` has completed features ready for release → bump version → create PR `develop` → `main` → automatic release
