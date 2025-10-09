# Task 01: Stabilize Build Configuration

## Description

The project's build files (`build.gradle.kts`, `libs.versions.toml`) use unstable dependency versions, target a future/unstable SDK, and disable critical optimizations for release builds. This task is to stabilize and harden the entire build configuration.

### Current Behavior

-   Android Gradle Plugin (AGP) and Navigation Compose versions are betas or newer, which are not production-ready.
-   Minification (code shrinking and obfuscation) is disabled for release builds, increasing APK size and making the app easy to reverse-engineer.
-   A deprecated Hilt configuration (`enableAggregatingTask`) is present.

### Expected Behavior

-   All dependencies in `libs.versions.toml` are updated to their latest **stable** versions.
-   `isMinifyEnabled` is set to `true` for the `release` build type.
-   The deprecated `hilt { ... }` block is removed from `app/build.gradle.kts`.

## Acceptance criteria

- The app compiles and runs successfully after all version and configuration changes.
- The release build process runs without errors and produces a smaller, obfuscated APK (verifiable with Android Studio's APK Analyzer).
