package com.vermeeria.ui.controller;

import com.vermeeria.service.SmartHomeService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main window of the HomeFlow application.
 *
 * @author Jette
 */
public class MainController {

    private final SmartHomeService smartHomeService;
    private final BorderPane root;
    private final StackPane contentArea;
    private final Label statusLabel;

    /**
     * Creates the main controller and initializes the basic user interface.
     *
     * @param smartHomeService the central application service
     */
    public MainController(final SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
        this.root = new BorderPane();
        this.contentArea = new StackPane();
        this.statusLabel = new Label("Ready");

        initialize();
        updateStatus("Ready");
    }

    /**
     * Initializes the main layout and all standard UI sections.
     */
    private void initialize() {
        root.getStyleClass().add("app-root");

        root.setTop(createToolbar());
        root.setLeft(createNavigation());
        root.setCenter(createContentArea());
        root.setBottom(createStatusBar());
    }

    /**
     * Creates the top toolbar with the main application actions.
     *
     * @return the toolbar
     */
    private ToolBar createToolbar() {
        Button newButton = new Button("New");
        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");
        Button executeButton = new Button("Run Scenario");

        newButton.setOnAction(event -> updateStatus("New project"));
        loadButton.setOnAction(event -> updateStatus("Loaded"));
        saveButton.setOnAction(event -> updateStatus("Saved"));
        executeButton.setOnAction(event -> updateStatus("Scenario executed"));

        Label titleLabel = new Label("HomeFlow");
        titleLabel.getStyleClass().add("app-title");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToolBar toolBar = new ToolBar(
                titleLabel,
                new Separator(),
                newButton,
                loadButton,
                saveButton,
                new Separator(),
                executeButton,
                spacer
        );

        toolBar.getStyleClass().add("top-toolbar");
        return toolBar;
    }

    /**
     * Updates the status bar with a short message and the current room count.
     *
     * @param message the status message to display
     */
    private void updateStatus(String message) {
        statusLabel.setText(message + " · " + smartHomeService.getAppData().getRooms().size() + " rooms");
    }

    /**
     * Creates the left navigation sidebar.
     *
     * @return the navigation area
     */
    private VBox createNavigation() {
        Label navigationTitle = new Label("Navigation");
        navigationTitle.getStyleClass().add("section-title");

        ListView<String> navigationList = new ListView<>();
        navigationList.getItems().addAll("Rooms", "Devices", "Scenarios");
        navigationList.getSelectionModel().selectFirst();
        navigationList.getStyleClass().add("navigation-list");

        VBox navigationBox = new VBox(12, navigationTitle, navigationList);
        navigationBox.setPadding(new Insets(20));
        navigationBox.setPrefWidth(220);
        navigationBox.getStyleClass().add("sidebar");

        return navigationBox;
    }

    /**
     * Creates the central content area.
     *
     * @return the content area
     */
    private Parent createContentArea() {
        Label placeholderTitle = new Label("Dashboard");
        placeholderTitle.getStyleClass().add("content-title");

        Label placeholderText = new Label("Select a section on the left to start editing rooms, devices, or scenarios.");
        placeholderText.getStyleClass().add("content-subtitle");
        placeholderText.setWrapText(true);

        VBox placeholderBox = new VBox(12, placeholderTitle, placeholderText);
        placeholderBox.setPadding(new Insets(32));
        placeholderBox.getStyleClass().add("content-card");

        contentArea.getChildren().add(placeholderBox);
        contentArea.setPadding(new Insets(24));
        contentArea.getStyleClass().add("content-area");

        return contentArea;
    }

    /**
     * Creates the bottom status bar.
     *
     * @return the status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(10, 16, 10, 16));
        statusBar.getStyleClass().add("status-bar");
        return statusBar;
    }

    /**
     * Returns the root view of the controller.
     *
     * @return the root node
     */
    public Parent getView() {
        return root;
    }
}
