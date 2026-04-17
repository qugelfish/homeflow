package com.vermeeria.ui.view;

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
    private final Button newButton;
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
        this.newButton = new Button("New");
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
        root.setBottom(createStatusBar());
    }

    private ToolBar createToolbar() {
        Label titleLabel = new Label("Home Flow");
        titleLabel.getStyleClass().add("app-title");
        titleLabel.setWrapText(false);
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

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

        ToolBar toolBar = new ToolBar(
                brandBox,
                newButton,
                loadButton,
                saveButton,
                executeButton,
                spacer
        );

        toolBar.getStyleClass().add("top-toolbar");
        toolBar.setMinHeight(68);
        return toolBar;
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

    /**
     * Creates a reusable placeholder card for one application section.
     *
     * @param title           the section title
     * @param description     the section description
     * @param metaInformation short additional status text
     * @return the placeholder view
     */
    public Parent createSectionPlaceholder(final String title,
                                           final String description,
                                           final String metaInformation) {
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

    private HBox createStatusBar() {
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(10, 16, 10, 16));
        statusBar.getStyleClass().add("status-bar");
        return statusBar;
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
     * Returns the new-project button.
     *
     * @return the new button
     */
    public Button getNewButton() {
        return newButton;
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
}
