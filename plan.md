# DevConsole Development Plan

This document contains a list of granular tasks (approx. 1 hour each) to evolve the DevConsole library from its current prototype state into a robust developer tool.

## Core Backend (Spring Boot Autoconfigure)

### Execution & Shell Improvements
- [ ] Move `GroovyShell` evaluation to a dedicated thread pool to avoid blocking the WebSocket thread.
- [ ] Implement execution timeout for scripts (e.g., terminate after 30 seconds).
- [ ] Support script cancellation via a `CancelScriptCommand`.
- [ ] Add a `clear` command in the shell to reset the `Binding` variables.
- [ ] Implement `@Grab` support in scripts to allow on-the-fly dependency resolution.
- [ ] Capture `System.out` and `System.err` during script execution and stream back to UI.
- [ ] Add support for "session" scoped variables that persist between script executions in the same WebSocket session.
- [ ] Improve bean filtering logic in `TargetServiceWebSocketHandler` to only include "useful" service beans.
- [ ] Add a way to exclude specific beans from being exposed to the shell via configuration.
- [ ] Implement a `describe(beanName)` helper in the shell to list available methods/properties of a bean.
- [ ] Support for multiple Groovy script files or "shared library" scripts pre-loaded into the shell.

### Connectivity & Reliability
- [ ] Implement heartbeats (ping/pong) between target services and the controller.
- [ ] Add auto-reconnect logic with exponential backoff in `DevConsoleConnector`.
- [ ] Handle `targetSession` being null in `DevConsoleMessageHandler` by sending an error event back to the UI.
- [ ] Implement "Service Offline" notifications in the controller when a target service disconnects.
- [ ] Support multiple instances of the same service name (add instance ID or IP to service identity).
- [ ] Log script execution history on the target service side for auditing.
- [ ] Implement graceful shutdown for the `DevConsoleConnector` to close WebSocket sessions properly.

### Configuration
- [ ] Add a property to enable/disable DevConsole entirely (`dev-console.enabled`).
- [ ] Allow configuring the controller URL via properties (`dev-console.controller-url`).
- [ ] Add a property to define custom star imports for the Groovy shell.
- [ ] Implement conditional configuration to only enable the console in specific Spring profiles (e.g., `dev`, `test`).

## Controller Hub (dev-console-controller)

### Message Routing & Management
- [ ] Implement a registry for active client (UI) sessions to support multiple developers simultaneously.
- [ ] Add broadcast capability: send a script to all instances of a specific service.
- [ ] Implement a REST endpoint to list all currently connected services.
- [ ] Add metrics (using Micrometer) for number of connected services, scripts executed, and failure rates.
- [ ] Implement a "lock" mechanism so only one developer can execute scripts on a specific service at a time.
- [ ] Store last N script results in memory in the controller for UI refresh.

### UI Integration
- [ ] Serve the Angular UI as static content from the Spring Boot controller.
- [ ] Implement a Proxy for the Monaco Editor worker files if served from a different origin.

## Frontend (Angular UI)

### Editor & UX
- [ ] Replace hardcoded `targetServiceName: 'demo'` with a dropdown selector using `availableServices`.
- [ ] Implement a "History" sidebar to show previously executed scripts.
- [ ] Add a "Save to LocalStorage" feature for the current script in the editor.
- [ ] Implement "Execute Selection" (only run the highlighted text in Monaco).
- [ ] Add syntax highlighting for Groovy in the Monaco Editor.
- [ ] Implement basic autocompletion for beans in Monaco (fetched from the backend).
- [ ] Add a "Clear Output" button.
- [ ] Implement a "Theme Switcher" (Light/Dark mode) for the UI and Monaco Editor.
- [ ] Add keyboard shortcuts (e.g., `Ctrl+Enter` to execute).
- [ ] Display execution time for each script result.
- [ ] Implement a "Copy to Clipboard" button for script outputs.
- [ ] Add a "Status Bar" showing connection state to the controller and the selected service.

### Visualization
- [ ] Enhance the `ScriptResult` rendering to format JSON outputs nicely.
- [ ] If the result is a List or Map, render it as a searchable/expandable tree.
- [ ] Add a specialized renderer for Spring `Health` or `Environment` objects if returned.
- [ ] Implement a "Console" view that shows streamed `System.out` logs.

## Security

### Authentication & Authorization
- [ ] Add a simple API Key requirement for target services to connect to the controller.
- [ ] Implement JWT-based authentication for the UI.
- [ ] Add a "ReadOnly" mode for the shell (preventing setters or destructive operations via `SecureASTCustomizer`).
- [ ] Integrate with Spring Security to restrict access based on roles.
- [ ] Add IP whitelisting for the controller.
- [ ] Implement a "Confirmation Dialog" in the UI for scripts containing "dangerous" keywords (e.g., `delete`, `shutdown`).
- [ ] Encrypt WebSocket traffic (WSS).

## Infrastructure & DX

### Packaging & Distribution
- [ ] Create a `dev-console-bom` (Bill of Materials) for easier dependency management.
- [ ] Improve the `demo` project to include sample beans and use cases for testing.
- [ ] Add a Docker Compose file to spin up the Controller and a few Demo services.
- [ ] Create a README with "Quick Start" instructions.
- [ ] Add KDoc/Javadoc for core classes in `dev-console-common`.

### Quality & Testing
- [ ] Write integration tests for `DevConsoleMessageHandler` using `TestWebSocketClient`.
- [ ] Add unit tests for `TargetServiceWebSocketHandler` mocking the `GroovyShell`.
- [ ] Implement a "Sanity Check" script that runs on startup to verify the shell is working.
- [ ] Add linting for the Angular project.
- [ ] Implement a CI pipeline (GitHub Actions/GitLab CI) to build and test all modules.

## Advanced Features (Future Considerations)

- [ ] Support for Kotlin scripts (`KotlinJsr223JvmLocalScriptEngine`).
- [ ] Variable "Watch" window to track bean state changes over time.
- [ ] Visualizer for Spring Integration or Spring Batch flows if present in the target service.
- [ ] "Recording" mode: record a sequence of interactions and save as a test case.
- [ ] Support for uploading and executing `.groovy` files from disk.
- [ ] Integration with Spring Actuator endpoints.
- [ ] Multi-tab support in the UI for working on different scripts/services simultaneously.
