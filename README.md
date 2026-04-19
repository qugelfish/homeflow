# HomeFlow

HomeFlow is a JavaFX desktop application for managing a simulated smart home. The application allows users to create, edit, and execute rooms, devices, and scenarios, while also tracking scenario execution through an integrated log.

## Features

- Create, rename, and delete rooms
- Create, assign, edit, and delete smart home devices
- Extend device types through a plugin-based architecture
- Create and execute scenarios with multiple actions
- View scenario execution logs inside the main application window
- Persist all application data as JSON

## Technology Stack

- Java 25
- JavaFX 26
- Maven
- Jackson for JSON persistence
- JUnit 5
- TestFX for GUI testing
- Checkstyle, PMD, and SpotBugs for static analysis

## Project Structure

```text
src/main/java/com/vermeeria
├── app           application entry point
├── model         domain models
├── persistence   JSON repository and persistence interfaces
├── plugin        extensible device types
├── service       business logic
└── ui
    ├── controller
    ├── dialog
    └── view
```

## Architecture

The project follows an MVC-like structure:

- `model` contains the domain objects
- `ui.view` builds the user interface
- `ui.controller` handles user interaction
- `service` encapsulates application logic
- `persistence` handles loading and saving

For devices, the application also uses a plugin-based extension mechanism:

- `DevicePlugin` defines the behavior of a device type
- `DevicePluginRegistry` discovers available plugins automatically
- new device types can be added by implementing another `DevicePlugin`

## Running the Application

The application is started through the JavaFX Maven plugin:

```bash
mvn javafx:run
```

The entry point is:

- [SmartHomeApplication.java](/Users/vermeeria/Documents/smarthome_pruefungsleistung/src/main/java/com/vermeeria/app/SmartHomeApplication.java)

By default, application data is stored in:

```text
data/app-data.json
```

## Running Tests

Run all tests:

```bash
mvn test
```

Run the full build validation including static analysis:

```bash
mvn verify
```

### GUI Testing Note

- GUI tests use TestFX.
- In GitHub Actions they are executed with `xvfb-run` so they can run in a headless Linux environment.

## Quality Assurance

The build includes the following quality checks:

- Checkstyle
- PMD
- SpotBugs

The project also includes:

- unit tests for services, plugins, and persistence
- reflection-based model tests with a shared base class
- GUI tests for `DeviceView` and `DeviceController`

## Example Workflow

1. Create a room
2. Create a device and assign it to a room
3. Create a scenario with one or more actions
4. Execute the scenario from the scenario view or the header
5. Review the resulting state changes and log entries in the execution log

## Adding New Device Types

To add a new device type, create a new class inside `com.vermeeria.plugin` that implements `DevicePlugin`.

Important methods include:

- `getTypeKey()`
- `getDisplayName()`
- `createDefaultState()`
- `supportedActions()`
- `applyAction(...)`
- `formatState(...)`

The new type will then be discovered automatically by `DevicePluginRegistry`.

## Build and CI

The GitHub Actions workflow is located at:

- [.github/workflows/maven.yml](/Users/vermeeria/Documents/smarthome_pruefungsleistung/.github/workflows/maven.yml)

It runs the Maven build and test suite in CI.

## Notes for Future Development

- Additional GUI tests for `RoomView`, `ScenarioView`, and their controllers would be a natural next step.
- A broader end-to-end integration test for a main application use case would further strengthen coverage.
- Every new device type should be accompanied by dedicated plugin tests.
