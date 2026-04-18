package com.vermeeria.ui.view;

import com.vermeeria.model.Scenario;
import com.vermeeria.model.ScenarioAction;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builds the scenario management area of the user interface.
 *
 * @author Jette
 */
public class ScenarioView {

    private final BorderPane root;
    private final ListView<Scenario> scenariosList;
    private final TextField scenarioNameField;
    private final TextArea scenarioDescriptionArea;
    private final ListView<ScenarioAction> actionsList;
    private final Button addScenarioButton;
    private final Button editScenarioButton;
    private final Button deleteScenarioButton;
    private final Button editActionButton;

    /**
     * Creates the scenarios view and initializes the layout.
     */
    public ScenarioView() {
        this.root = new BorderPane();
        this.scenariosList = new ListView<>();
        this.scenarioNameField = new TextField();
        this.scenarioDescriptionArea = new TextArea();
        this.actionsList = new ListView<>();
        this.addScenarioButton = new Button("Add scenario");
        this.editScenarioButton = new Button("✎");
        this.deleteScenarioButton = new Button("Delete scenario");
        this.editActionButton = new Button("Edit actions");

        this.editScenarioButton.setTooltip(new Tooltip("Edit scenario"));
        this.addScenarioButton.getStyleClass().add("success-button");
        this.deleteScenarioButton.getStyleClass().add("danger-button");

        initialize();
    }

    private void initialize() {
        root.setLeft(createScenariosListCard());
        root.setCenter(createScenarioEditorCard());
        root.setPadding(new Insets(0));
    }

    private Parent createScenariosListCard() {
        Label titleLabel = new Label("Scenarios");
        titleLabel.getStyleClass().add("content-title");
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label subtitleLabel = new Label("Manage reusable automation flows for your smart home.");
        subtitleLabel.getStyleClass().add("content-subtitle");
        subtitleLabel.setWrapText(true);

        scenariosList.getStyleClass().add("navigation-list");
        VBox.setVgrow(scenariosList, Priority.ALWAYS);

        VBox listCard = new VBox(12, titleLabel, subtitleLabel, scenariosList);
        listCard.setPadding(new Insets(32));
        listCard.setPrefWidth(320);
        listCard.getStyleClass().add("content-card");

        return listCard;
    }

    private Parent createScenarioEditorCard() {
        Label titleLabel = new Label("Scenario Details");
        titleLabel.getStyleClass().add("content-title");
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label subtitleLabel = new Label("Create a scenario, describe it and later assign device actions.");
        subtitleLabel.getStyleClass().add("content-subtitle");
        subtitleLabel.setWrapText(true);

        Label nameLabel = new Label("Scenario name");
        Label descriptionLabel = new Label("Description");
        Label actionsLabel = new Label("Scenario actions");
        actionsLabel.getStyleClass().add("section-title");

        scenarioNameField.setPromptText("Enter scenario name");
        scenarioNameField.getStyleClass().add("form-input");
        scenarioNameField.setDisable(true);
        HBox.setHgrow(scenarioNameField, Priority.ALWAYS);

        scenarioDescriptionArea.setPromptText("Describe what this scenario should do");
        scenarioDescriptionArea.getStyleClass().add("form-input");
        scenarioDescriptionArea.setDisable(true);
        scenarioDescriptionArea.setWrapText(true);
        scenarioDescriptionArea.setPrefRowCount(4);

        actionsList.getStyleClass().add("navigation-list");
        actionsList.setDisable(true);
        VBox.setVgrow(actionsList, Priority.ALWAYS);

        HBox scenarioNameBar = new HBox(10, scenarioNameField, editScenarioButton);
        Region actionsHeaderSpacer = new Region();
        HBox.setHgrow(actionsHeaderSpacer, Priority.ALWAYS);
        HBox actionsHeaderBar = new HBox(10, actionsLabel, actionsHeaderSpacer, editActionButton);

        Region scenarioButtonSpacer = new Region();
        HBox.setHgrow(scenarioButtonSpacer, Priority.ALWAYS);
        HBox scenarioButtonBar = new HBox(10, addScenarioButton, scenarioButtonSpacer, deleteScenarioButton);

        VBox editorCard = new VBox(12, titleLabel, subtitleLabel, nameLabel, scenarioNameBar, descriptionLabel, scenarioDescriptionArea, actionsHeaderBar, actionsList, scenarioButtonBar);
        editorCard.setPadding(new Insets(32));
        editorCard.getStyleClass().add("content-card");

        BorderPane.setMargin(editorCard, new Insets(0, 0, 0, 24));
        return editorCard;
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
     * Returns the scenarios list.
     *
     * @return the scenarios list
     */
    public ListView<Scenario> getScenariosList() {
        return scenariosList;
    }

    /**
     * Returns the scenario name field.
     *
     * @return the scenario name field
     */
    public TextField getScenarioNameField() {
        return scenarioNameField;
    }

    /**
     * Returns the description area.
     *
     * @return the description area
     */
    public TextArea getScenarioDescriptionArea() {
        return scenarioDescriptionArea;
    }

    /**
     * Returns the actions list.
     *
     * @return the actions list
     */
    public ListView<ScenarioAction> getActionsList() {
        return actionsList;
    }

    /**
     * Returns the add-scenario button.
     *
     * @return the add button
     */
    public Button getAddScenarioButton() {
        return addScenarioButton;
    }

    /**
     * Returns the edit-scenario button.
     *
     * @return the edit button
     */
    public Button getEditScenarioButton() {
        return editScenarioButton;
    }

    /**
     * Returns the delete-scenario button.
     *
     * @return the delete button
     */
    public Button getDeleteScenarioButton() {
        return deleteScenarioButton;
    }

    /**
     * Returns the edit-action button.
     *
     * @return the edit-action button
     */
    public Button getEditActionButton() {
        return editActionButton;
    }
}
