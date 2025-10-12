# Score-Count

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/yourusername/score-count) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Welcome to Score-Count! This application provides a simple and intuitive way to keep track of scores during table tennis matches. Whether you're playing a casual game with friends or a more competitive match, Score-Count helps you focus on the game without worrying about scorekeeping.

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack & Architecture](#tech-stack--architecture)
- [How to Build](#how-to-build)
- [Contributing](#contributing)
- [License](#license)
- [Roadmap (Future Enhancements)](#roadmap-future-enhancements)

## Features

*   Increment and decrement scores for two players.
*   Display current scores clearly.
*   Reset scores for a new game.
*   (Add more features as they are developed, e.g., Player names, Game history, Set tracking)

## Screenshots

*(Coming Soon! Add screenshots of the main app screens here.)*

<img src="placeholder_screenshot1.png" width="200"/> <img src="placeholder_screenshot2.png" width="200"/>

## Tech Stack & Architecture

*   **Programming Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose
*   **Dependency Injection:** Hilt
*   **Architecture:** Follows a layered architecture pattern:
    *   **UI Layer:** (Jetpack Compose) Responsible for displaying the application data on the screen and handling user interactions.
    *   **Domain Layer:** Contains the business logic of the application (Use Cases).
    *   **Data Layer:** Responsible for managing application data (Repositories, Data Sources).

## How to Build

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

*   Android Studio (Latest stable version recommended)
*   JDK (Java Development Kit) 17 or higher

### Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/score-count.git
    cd score-count
    ```
    *(Replace `yourusername` with the actual GitHub username/organization if applicable)*

2.  **Set up pre-commit hook (optional but recommended):**

    This project uses ktlint for code formatting. The pre-commit hook automatically formats Kotlin files before each commit.

    ```bash
    cat > .git/hooks/pre-commit << 'EOF'
    #!/bin/bash
    # ktlint pre-commit hook
    # Auto-formats Kotlin files before commit

    echo "Running ktlint format on staged files..."

    # Get list of staged Kotlin files
    STAGED_KOTLIN_FILES=$(git diff --cached --name-only --diff-filter=ACMR | grep -E '\.kt$|\.kts$' || true)

    if [ -z "$STAGED_KOTLIN_FILES" ]; then
        echo "No Kotlin files to format"
        exit 0
    fi

    # Run ktlint format on all files
    ./gradlew ktlintFormat --quiet

    # Check ktlint status
    if [ $? -ne 0 ]; then
        echo "❌ ktlint format failed. Please fix the issues and try again."
        exit 1
    fi

    # Re-add formatted files to staging
    for file in $STAGED_KOTLIN_FILES; do
        if [ -f "$file" ]; then
            git add "$file"
        fi
    done

    echo "✅ ktlint format completed successfully"
    exit 0
    EOF

    chmod +x .git/hooks/pre-commit
    ```

3.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Click on "Open" or "Open an Existing Project".
    *   Navigate to the cloned `score-count` directory and select it.

4.  **Build and Run:**
    *   Let Android Studio sync the project and download dependencies.
    *   Click the "Run" button (green play icon) or select "Run" > "Run 'app'" from the menu.
    *   Choose an available emulator or a connected physical device.

### Code Quality

This project uses ktlint for Kotlin code formatting. Available commands:

```bash
./gradlew ktlintCheck    # Check code formatting
./gradlew ktlintFormat   # Auto-fix formatting issues
```

The `.editorconfig` file contains project-wide style rules that are automatically applied by most IDEs.

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

This project is licensed under the MIT License - see the `LICENSE.md` file for details. (You'll need to create a `LICENSE.md` file with the MIT License text).

## Roadmap (Future Enhancements)

*   [ ] Player name customization
*   [ ] Game/Match history
*   [ ] Set tracking (e.g., best of 3, best of 5)
*   [ ] Themes (Dark/Light)
*   [ ] Data persistence (e.g., using Room or DataStore)

---

_This README was last updated on $(date)._