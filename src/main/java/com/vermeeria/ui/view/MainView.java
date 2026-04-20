package com.vermeeria.ui.view;

import com.vermeeria.model.ExecutionLogEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Consumer;

/**
 * Builds the main application layout of the HomeFlow user interface.
 *
 * @author Jette
 */
public class MainView {

    private static final String EXECUTION_LOG = "Execution Log";
    private static final String ACCENT_SELECTION_STYLE = "-selection-accent-color: %s;";
    private static final List<Color> ACCENT_PALETTE = List.of(
            Color.BLACK,
            Color.DIMGRAY,
            Color.SLATEBLUE,
            Color.ORCHID,
            Color.CORNFLOWERBLUE,
            Color.DODGERBLUE,
            Color.MEDIUMSEAGREEN,
            Color.GOLD,
            Color.DARKORANGE,
            Color.TOMATO,
            Color.CRIMSON,
            Color.HOTPINK
    );
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
    private final Button accentColorButton;
    private final ContextMenu accentColorMenu;
    private Color accentColor;
    private Consumer<Color> accentColorChangeListener;

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
        this.toggleLogButton = new Button(EXECUTION_LOG);
        this.loadButton = new Button("Load");
        this.saveButton = new Button("Save");
        this.executeButton = new Button("Run Scenario");
        this.accentColor = Color.ORCHID;
        this.accentColorButton = new Button();
        this.accentColorMenu = new ContextMenu();

        initialize();
    }

    private void initialize() {
        root.getStyleClass().add("app-root");
        applyAccentColor(accentColor);

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

        accentColorButton.getStyleClass().add("accent-picker");
        accentColorButton.setTooltip(new Tooltip("Choose accent color"));
        accentColorButton.setPrefSize(24, 24);
        accentColorButton.setMinSize(24, 24);
        accentColorButton.setMaxSize(24, 24);
        accentColorButton.setOnAction(event -> {
            if (accentColorMenu.isShowing()) {
                accentColorMenu.hide();
            } else {
                accentColorMenu.show(accentColorButton, javafx.geometry.Side.BOTTOM, 0, 8);
            }
        });
        configureAccentColorMenu();

        HBox titleBox = new HBox(0, homeLabel, flowLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.getChildren().add(titleBox);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToolBar toolBar = new ToolBar(brandBox, loadButton, saveButton, executeButton, spacer, accentColorButton);

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
        Label logTitle = new Label(EXECUTION_LOG);
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
     * Applies the accent color used for selected list entries.
     *
     * @param newAccentColor the new accent color
     */
    public void applyAccentColor(final Color newAccentColor) {
        if (newAccentColor == null) {
            return;
        }
        accentColor = newAccentColor;
        root.setStyle(ACCENT_SELECTION_STYLE.formatted(toCssColor(newAccentColor)));
        accentColorButton.setStyle("-fx-background-color: " + toCssColor(newAccentColor) + ";");
        if (accentColorChangeListener != null) {
            accentColorChangeListener.accept(newAccentColor);
        }
    }

    /**
     * Expands or collapses the execution log area.
     *
     * @param expanded whether the log area should be visible
     */
    public void setLogExpanded(final boolean expanded) {
        executionLogPanel.setVisible(expanded);
        executionLogPanel.setManaged(expanded);
        toggleLogButton.setText(EXECUTION_LOG);
    }

    private String toCssColor(final Color color) {
        int red = (int) Math.round(color.getRed() * 255);
        int green = (int) Math.round(color.getGreen() * 255);
        int blue = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    private void configureAccentColorMenu() {
        accentColorMenu.getStyleClass().add("accent-menu");

        GridPane paletteGrid = new GridPane();
        paletteGrid.getStyleClass().add("accent-menu-grid");
        paletteGrid.setHgap(6);
        paletteGrid.setVgap(6);

        for (int index = 0; index < ACCENT_PALETTE.size(); index++) {
            Color paletteColor = ACCENT_PALETTE.get(index);
            Button swatchButton = new Button();
            swatchButton.getStyleClass().add("accent-swatch");
            swatchButton.setPrefSize(18, 18);
            swatchButton.setMinSize(18, 18);
            swatchButton.setMaxSize(18, 18);
            swatchButton.setStyle("-fx-background-color: " + toCssColor(paletteColor) + ";");
            swatchButton.setOnAction(event -> {
                applyAccentColor(paletteColor);
                accentColorMenu.hide();
            });
            paletteGrid.add(swatchButton, index % 4, index / 4);
        }

        CustomMenuItem paletteItem = new CustomMenuItem(paletteGrid, false);
        accentColorMenu.getItems().setAll(paletteItem);
    }

    /**
     * Registers a listener that is notified whenever the accent color changes.
     *
     * @param accentColorChangeListener the listener to invoke on color changes
     */
    public void setAccentColorChangeListener(final Consumer<Color> accentColorChangeListener) {
        this.accentColorChangeListener = accentColorChangeListener;
    }
}
