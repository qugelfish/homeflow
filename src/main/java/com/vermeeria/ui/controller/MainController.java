package com.vermeeria.ui.controller;

import com.vermeeria.service.SmartHomeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;

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
    private final ListView<String> navigationList;

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
        this.navigationList = new ListView<>();

        initialize();
        wireActions();
        showRoomsView();
        updateStatus("Ready");
    }

    private void initialize() {
        root.getStyleClass().add("app-root");

        root.setTop(createToolbar());
        root.setLeft(createNavigation());
        root.setCenter(createContentArea());
        root.setBottom(createStatusBar());
    }

    private void wireActions() {
        navigationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Rooms".equals(newValue)) {
                showRoomsView();
            } else if ("Devices".equals(newValue)) {
                showDevicesView();
            } else if ("Scenarios".equals(newValue)) {
                showScenariosView();
            }
        });
    }

    private ToolBar createToolbar() {
        Button newButton = new Button("New");
        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");
        Button executeButton = new Button("Run Scenario");

        newButton.setOnAction(event -> handleNewProject());
        loadButton.setOnAction(event -> handleLoadProject());
        saveButton.setOnAction(event -> handleSaveProject());
        executeButton.setOnAction(event -> handleExecuteScenario());

        Label titleLabel = new Label("Home Flow");
        titleLabel.getStyleClass().add("app-title");

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
        brandBox.getChildren().add(titleLabel);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToolBar toolBar = new ToolBar(brandBox, newButton, loadButton, saveButton, executeButton, spacer);

        toolBar.getStyleClass().add("top-toolbar");
        return toolBar;
    }

    private void handleNewProject() {
        smartHomeService.resetAppData();
        refreshCurrentView();
        updateStatus("Created a new empty project");
    }

    private void handleLoadProject() {
        smartHomeService.reloadAll();
        refreshCurrentView();
        updateStatus("Loaded project data");
    }

    private void handleSaveProject() {
        smartHomeService.saveAll();
        updateStatus("Saved project data");
    }

    private void handleExecuteScenario() {
        updateStatus("Scenario execution will be connected in the scenario view");
    }

    private void refreshCurrentView() {
        String selectedEntry = navigationList.getSelectionModel().getSelectedItem();
        if ("Devices".equals(selectedEntry)) {
            showDevicesView();
        } else if ("Scenarios".equals(selectedEntry)) {
            showScenariosView();
        } else {
            showRoomsView();
        }
    }

    private void updateStatus(final String message) {
        statusLabel.setText(message + " · " + smartHomeService.getRoomCount() + " rooms · " + smartHomeService.getDeviceCount() + " devices · " + smartHomeService.getScenarioCount() + " scenarios");
    }

    private VBox createNavigation() {
        Label navigationTitle = new Label("Navigation");
        navigationTitle.getStyleClass().add("section-title");

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
        return contentArea;
    }

    private void showRoomsView() {
        showView(createSectionPlaceholder("Rooms", "This area will later host the dedicated room management view.", "Current room count: " + smartHomeService.getRoomCount()));
        updateStatus("Rooms view opened");
    }

    private void showDevicesView() {
        showView(createSectionPlaceholder("Devices", "This area will later host the dedicated device management view.", "Current device count: " + smartHomeService.getDeviceCount()));
        updateStatus("Devices view opened");
    }

    private void showScenariosView() {
        showView(createSectionPlaceholder("Scenarios", "This area will later host the dedicated scenario management view.", "Current scenario count: " + smartHomeService.getScenarioCount()));
        updateStatus("Scenarios view opened");
    }

    private Parent createSectionPlaceholder(final String title, final String description, final String metaInformation) {
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

    private void showView(final Parent view) {
        contentArea.getChildren().setAll(view);
    }

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
