package com.vermeeria.ui.view;

import com.vermeeria.model.ExecutionLogEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;

/**
 * Builds the main application layout of the HomeFlow user interface.
 *
 * @author Jette
 */
public class MainView {

    private final BorderPane root;
    private final StackPane contentArea;
    private final Label statusLabel;
    private final ListView<String> navigationList;
    private final ListView<ExecutionLogEntry> executionLogList;
    private final VBox executionLogPanel;
    private final Button toggleLogButton;
    private final Button loadButton;
    private final Button saveButton;
    private final Button executeButton;

    /**
     * Creates the main view and initializes the base layout.
     */
    public MainView() {
        this.root = new BorderPane();
        this.contentArea = new StackPane();
        this.statusLabel = new Label("Ready");
        this.navigationList = new ListView<>();
        this.executionLogList = new ListView<>();
        this.executionLogPanel = new VBox(10);
        this.toggleLogButton = new Button("Execution Log");
        this.loadButton = new Button("Load");
        this.saveButton = new Button("Save");
        this.executeButton = new Button("Run Scenario");

        initialize();
    }

    private void initialize() {
        root.getStyleClass().add("app-root");

        root.setTop(createToolbar());
        root.setLeft(createNavigation());
        root.setCenter(createContentArea());
        root.setBottom(createBottomArea());
    }

    private ToolBar createToolbar() {
        Label homeLabel = new Label("Home");
        homeLabel.getStyleClass().add("app-title");
        homeLabel.setWrapText(false);
        homeLabel.setMinHeight(Region.USE_PREF_SIZE);
        homeLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Label flowLabel = new Label("Flow");
        flowLabel.getStyleClass().addAll("app-title", "app-title-accent");
        flowLabel.setWrapText(false);
        flowLabel.setMinHeight(Region.USE_PREF_SIZE);
        flowLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

        HBox brandBox = new HBox(12);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.getStyleClass().add("brand-box");

        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView logoView = new ImageView(new Image(logoStream));
            logoView.setFitWidth(42);
            logoView.setFitHeight(42);
            logoView.setPreserveRatio(true);
            logoView.getStyleClass().add("app-logo");
            brandBox.getChildren().add(logoView);
        }
        HBox titleBox = new HBox(0, homeLabel, flowLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.getChildren().add(titleBox);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToolBar toolBar = new ToolBar(brandBox, loadButton, saveButton, executeButton, spacer);

        toolBar.getStyleClass().add("top-toolbar");
        toolBar.setMinHeight(68);
        return toolBar;
    }

    private VBox createNavigation() {
        Label navigationTitle = new Label("Navigation");
        navigationTitle.getStyleClass().add("section-title");
        navigationTitle.setMinHeight(Region.USE_PREF_SIZE);

        navigationList.getItems().addAll("Rooms", "Devices", "Scenarios");
        navigationList.getSelectionModel().selectFirst();
        navigationList.getStyleClass().add("navigation-list");

        VBox navigationBox = new VBox(12, navigationTitle, navigationList);
        navigationBox.setPadding(new Insets(20));
        navigationBox.setPrefWidth(220);
        navigationBox.getStyleClass().add("sidebar");

        return navigationBox;
    }

    private Parent createContentArea() {
        contentArea.setPadding(new Insets(24));
        contentArea.getStyleClass().add("content-area");

        ScrollPane contentScrollPane = new ScrollPane(contentArea);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(false);
        contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScrollPane.getStyleClass().add("content-scroll");
        return contentScrollPane;
    }

    /**
     * Creates a reusable placeholder card for one application section.
     *
     * @param title           the section title
     * @param description     the section description
     * @param metaInformation short additional status text
     * @return the placeholder view
     */
    public Parent createSectionPlaceholder(final String title, final String description, final String metaInformation) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("content-title");

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("content-subtitle");
        descriptionLabel.setWrapText(true);

        Label metaLabel = new Label(metaInformation);
        metaLabel.getStyleClass().add("content-subtitle");

        VBox placeholderBox = new VBox(12, titleLabel, descriptionLabel, metaLabel);
        placeholderBox.setPadding(new Insets(32));
        placeholderBox.getStyleClass().add("content-card");

        return placeholderBox;
    }

    private Parent createBottomArea() {
        Label logTitle = new Label("Execution Log");
        logTitle.getStyleClass().add("section-title");

        executionLogList.getStyleClass().add("navigation-list");
        executionLogList.setPrefHeight(180);
        executionLogList.setPlaceholder(new Label("No scenario execution has been logged yet."));

        executionLogPanel.getChildren().setAll(logTitle, executionLogList);
        executionLogPanel.setPadding(new Insets(14, 16, 12, 16));
        executionLogPanel.getStyleClass().add("log-panel");
        executionLogPanel.setVisible(false);
        executionLogPanel.setManaged(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBar = new HBox(statusLabel, spacer, toggleLogButton);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(10, 16, 10, 16));
        statusBar.getStyleClass().add("status-bar");
        return new VBox(executionLogPanel, statusBar);
    }

    /**
     * Returns the root view node.
     *
     * @return the root node
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Returns the central content container.
     *
     * @return the content area
     */
    public StackPane getContentArea() {
        return contentArea;
    }

    /**
     * Returns the status label.
     *
     * @return the status label
     */
    public Label getStatusLabel() {
        return statusLabel;
    }

    /**
     * Returns the navigation list.
     *
     * @return the navigation list
     */
    public ListView<String> getNavigationList() {
        return navigationList;
    }

    /**
     * Returns the load button.
     *
     * @return the load button
     */
    public Button getLoadButton() {
        return loadButton;
    }

    /**
     * Returns the save button.
     *
     * @return the save button
     */
    public Button getSaveButton() {
        return saveButton;
    }

    /**
     * Returns the run-scenario button.
     *
     * @return the execute button
     */
    public Button getExecuteButton() {
        return executeButton;
    }

    /**
     * Returns the execution log list.
     *
     * @return the execution log list
     */
    public ListView<ExecutionLogEntry> getExecutionLogList() {
        return executionLogList;
    }

    /**
     * Returns the log toggle button.
     *
     * @return the toggle button
     */
    public Button getToggleLogButton() {
        return toggleLogButton;
    }

    /**
     * Expands or collapses the execution log area.
     *
     * @param expanded whether the log area should be visible
     */
    public void setLogExpanded(final boolean expanded) {
        executionLogPanel.setVisible(expanded);
        executionLogPanel.setManaged(expanded);
        toggleLogButton.setText("Execution Log");
    }
}
