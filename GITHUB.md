# GITHUB.md

## 1. Executive Summary

This document outlines the software architecture of a desktop Chess application. The system is a self-contained program that provides a graphical user interface (GUI) for playing chess, complete with user authentication, game state persistence, and move history tracking.

The primary problem solved is the creation of a complete, playable chess game that encapsulates game logic, user interaction, and data management within a single, executable application. The system is designed as a standalone desktop experience, not as a client-server or web-based application.

## 2. System Architecture

### High-Level Design

The system is implemented using a **Monolithic Architecture**. All core components—including the user interface, game logic, and data persistence layers—are tightly integrated and run within a single process. This design choice is appropriate for a small-scale, standalone desktop application, as it simplifies development and deployment.

The internal design leverages the **Observer Pattern** to decouple components. `GameObserver` instances (`GUIObserver`, `HistoryObserver`, `JsonLogObserver`) are notified of state changes within the `Game` object. This allows for concerns like UI updates, logging, and history tracking to be handled independently of the core move validation and execution logic.

### Data Flow

1.  **User Interaction:** The user interacts with the GUI, which is constructed using components from the `GUI_Components` package (e.g., `LoginAndRegisterWindow`, `GameFrame`).
2.  **Event Handling:** UI events (e.g., button clicks, piece movements) are captured by listeners within the GUI components.
3.  **Logic Execution:** These events trigger method calls into the core `game_logic` package, primarily interacting with the `Game` and `Board` classes.
4.  **State Change:** The `Game` object updates its state (e.g., piece positions, turn).
5.  **Notification:** The `Game` object notifies all registered `GameObserver` instances of the state change.
6.  **Component Updates:** Each observer responds accordingly:
    *   `GUIObserver`: Updates the visual representation of the chess board.
    *   `HistoryObserver`: Records the move.
    *   `PointsObserver`: Recalculates and displays scores.
    *   `JsonLogObserver`: Appends game events to a JSON log.
7.  **Data Persistence:** User and game data are persisted to the local filesystem using utility classes (`JsonReaderUtil`, `JsonWriterUtil`) that read from and write to JSON files (`input/accounts.json`, `input/games.json`).

### Component Analysis

-   `main_package`: Contains the application entry point (`Main.java`), responsible for initializing and launching the application.
-   `GUI_Components`: Manages all aspects of the user interface. It includes frames, buttons, and custom Swing components for rendering the game.
-   `game_logic`: Encapsulates the core rules and state management of the chess game. `Board.java` maintains the state of the pieces, and `Game.java` orchestrates the gameplay.
-   `pieces_details`: Defines the chess pieces and their valid movements. It employs a **Strategy Pattern**, where each piece type has an associated `MoveStrategy` that defines its movement logic.
-   `Interfaces`: A set of Java interfaces (`ChessPiece`, `GameObserver`, `MoveStrategy`) that define the primary contracts between components, promoting a degree of loose coupling.
-   `user_details`: Contains data models for `User` and `Player`.
-   `misc`: A collection of utility classes, including for JSON serialization/deserialization.
-   `errors`: Defines custom, domain-specific exceptions for handling invalid operations.

## 3. Technology Stack & Decision Record

This project intentionally minimizes external dependencies, opting for a standard, self-contained toolchain.

-   **Language: Java**
    -   **Justification:** Java is a mature, object-oriented language with strong typing, which is beneficial for a logic-intensive application like chess. Its robust standard library and platform independence make it a reliable choice for desktop applications.

-   **GUI Framework: Java Swing**
    -   **Justification:** The use of `javax.swing` components is inferred from the `GUI_Components` package. Swing is part of the standard Java SE Development Kit (JDK), requiring no external libraries. This simplifies the build process and ensures that the application can run on any machine with a compatible Java Runtime Environment (JRE). For a standalone desktop application of this scope, it is a practical and sufficient choice.

-   **Data Storage: JSON Files (via `json-simple`)**
    -   **Justification:** Persistence is handled by serializing game and user data to local JSON files. The `json-simple` library is included directly in the `lib/` directory. This approach avoids the operational overhead of a database system (e.g., PostgreSQL, MongoDB), which would be excessive for the project's requirements. File-based storage is adequate for managing a limited amount of data for a single-user application.

-   **Dependency Management: Manual (JAR in `/lib`)**
    -   **Justification:** The project eschews a formal dependency management tool like Maven or Gradle. Instead, the single external dependency (`json-simple-1.1.1.jar`) is stored directly in the repository. This is a pragmatic decision for a project with a minimal number of dependencies, as it removes the need for additional configuration and tooling.

## 4. Repository Structure Strategy

The project's source code is organized using a **package-by-feature** approach, which promotes a clear separation of concerns.

-   **`GUI_Components/` vs. `game_logic/`**: This is the most critical separation, isolating the presentation layer from the business logic. It allows developers to work on the UI and the core game engine independently.
-   **`pieces_details/`**: Grouping all piece-related classes and their movement strategies into a single package aligns with Domain-Driven Design principles, where a core concept of the domain (the chess piece) is encapsulated in its own module.
-   **`Interfaces/`**: Centralizing the application's key interfaces makes the system's contracts explicit and easy to locate.
-   **`input/`**: This directory serves as a simple database, storing the application's state in a clear and accessible location.

This structure enhances modularity and maintainability within the context of a monolithic application.

## 5. Setup & Environment

### Prerequisites

-   A Java Development Kit (JDK), version 8 or later.
-   An IDE such as IntelliJ IDEA or Eclipse is recommended for ease of compilation and execution (the project contains `.idea` and `.iml` files).

### Build & Execution

As there is no `pom.xml` or `build.gradle` file, the project is intended to be compiled and run using standard Java commands or directly from an IDE.

**From the Command Line:**

1.  Navigate to the `PROIECT/` directory.
2.  Compile the source code, including the `json-simple` library in the classpath:
    ```sh
    javac -d out -cp "lib/json-simple-1.1.1.jar" src/**/*.java
    ```
3.  Run the application by specifying the main class:
    ```sh
    java -cp "out;lib/json-simple-1.1.1.jar" main_package.Main
    ```

**Running Tests:**

The project includes a test runner that can be executed similarly:
```sh
java -cp "out;lib/json-simple-1.1.1.jar" Testare.ChessTestRunner
```

## 6. Constraints & Trade-offs

The architecture reflects a series of deliberate trade-offs that prioritize simplicity and rapid development over other concerns.

-   **Simplicity vs. Scalability:** The monolithic architecture and file-based data storage are simple to implement but do not scale. The application is fundamentally designed for a single user on a single machine and cannot be extended to a multi-user, client-server model without a complete architectural refactor.
-   **Coupling:** The `game_logic` is tightly coupled to the Swing-based GUI via the `GUIObserver`. While the Observer pattern provides a layer of abstraction, replacing the UI with a different technology (e.g., a web interface or JavaFX) would still require significant rework.
-   **Data Integrity:** Using raw JSON files for persistence lacks the ACID guarantees of a true database system. There is a risk of data corruption if the application terminates unexpectedly during a file-write operation. This trade-off sacrifices robustness for simplicity.
