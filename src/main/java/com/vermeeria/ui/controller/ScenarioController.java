package com.vermeeria.ui.controller;

import com.vermeeria.model.Scenario;
import com.vermeeria.model.ScenarioAction;
import com.vermeeria.service.ScenarioService;
import com.vermeeria.ui.view.ScenarioView;
import javafx.scene.Parent;

import java.util.List;
import java.util.function.Consumer;

/**
 * Controls the scenarios window of the HomeFlow application.
 *
 * @author Jette
 */
public class ScenarioController {

    private final ScenarioView scenarioView;
    private final ScenarioService scenarioService;
    private final Consumer<String> statusUpdater;
    private boolean editMode;
    private boolean createMode;

    /**
     * Creates the scenario controller and initializes the scenario controls.
     *
     * @param scenarioView    the scenario view
     * @param scenarioService the scenario service
     * @param statusUpdater   the callback used to update status messages
     */
    public ScenarioController(final ScenarioView scenarioView, final ScenarioService scenarioService, final Consumer<String> statusUpdater) {
        this.scenarioView = scenarioView;
        this.scenarioService = scenarioService;
        this.statusUpdater = statusUpdater;
        this.editMode = false;
        this.createMode = false;

        wireActions();
        refresh();
    }

    private void wireActions() {
        scenarioView.getScenariosList().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleScenarioSelection());
        scenarioView.getAddScenarioButton().setOnAction(event -> handleAddScenario());
        scenarioView.getEditScenarioButton().setOnAction(event -> handleEditScenario());
        scenarioView.getDeleteScenarioButton().setOnAction(event -> handleDeleteScenario());
        scenarioView.getEditActionButton().setOnAction(event -> handleEditAction());
    }

    private void handleAddScenario() {
        if (!createMode) {
            enterCreateMode();
            statusUpdater.accept("Enter the details for the new scenario");
            return;
        }

        try {
            scenarioService.createScenario(scenarioView.getScenarioNameField().getText(), scenarioView.getScenarioDescriptionArea().getText());
            refresh();
            leaveCreateMode();
            statusUpdater.accept("Scenario created");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleEditScenario() {
        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        if (selectedScenario == null) {
            statusUpdater.accept("Please select a scenario to edit");
            return;
        }

        if (createMode) {
            leaveCreateMode();
        }
        if (!editMode) {
            enterEditMode();
            statusUpdater.accept("Editing scenario details");
            return;
        }

        try {
            scenarioService.updateScenario(selectedScenario.getId(), scenarioView.getScenarioNameField().getText(), scenarioView.getScenarioDescriptionArea().getText());
            refresh(selectedScenario.getId());
            leaveEditMode();
            statusUpdater.accept("Scenario updated");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleDeleteScenario() {
        if (createMode) {
            leaveCreateMode();
            statusUpdater.accept("Scenario creation cancelled");
            return;
        }

        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        if (selectedScenario == null) {
            statusUpdater.accept("Please select a scenario to delete");
            return;
        }

        try {
            scenarioService.deleteScenario(selectedScenario.getId());
            leaveEditMode();
            leaveCreateMode();
            refresh();
            statusUpdater.accept("Scenario deleted");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleEditAction() {
        if (createMode) {
            statusUpdater.accept("The action editor dialog will handle actions for the new scenario next");
            return;
        }

        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        if (selectedScenario == null) {
            statusUpdater.accept("Please select a scenario first");
            return;
        }
        statusUpdater.accept("The action editor dialog will handle add, edit and delete next");
    }

    private void loadScenarios(final String scenarioIdToReselect) {
        scenarioView.getScenariosList().getItems().setAll(scenarioService.getAllScenarios());
        if (scenarioIdToReselect != null) {
            scenarioView.getScenariosList().getItems().stream()
                    .filter(scenario -> scenarioIdToReselect.equals(scenario.getId()))
                    .findFirst()
                    .ifPresent(scenario -> scenarioView.getScenariosList().getSelectionModel().select(scenario));
        }
    }

    private void handleScenarioSelection() {
        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        boolean scenarioSelected = selectedScenario != null;

        scenarioView.getEditScenarioButton().setDisable(!scenarioSelected);
        scenarioView.getDeleteScenarioButton().setDisable(!scenarioSelected);
        scenarioView.getEditActionButton().setDisable(!scenarioSelected);
        scenarioView.getEditActionButton().setText("Edit actions");

        List<ScenarioAction> actions = scenarioSelected ? selectedScenario.getActions() : List.of();
        scenarioView.getActionsList().getItems().setAll(actions);

        if (scenarioSelected && !createMode && !editMode) {
            scenarioView.getScenarioNameField().setText(selectedScenario.getName());
            scenarioView.getScenarioDescriptionArea().setText(selectedScenario.getDescription());
        } else if (!scenarioSelected && !createMode) {
            clearForm();
        }

        if (!editMode && !createMode) {
            setFormDisabled(true);
            scenarioView.getEditScenarioButton().setText("✎");
        }
    }

    private void clearForm() {
        scenarioView.getScenarioNameField().clear();
        scenarioView.getScenarioDescriptionArea().clear();
        scenarioView.getActionsList().getItems().clear();
    }

    private void setFormDisabled(final boolean disabled) {
        scenarioView.getScenarioNameField().setDisable(disabled);
        scenarioView.getScenarioDescriptionArea().setDisable(disabled);
        scenarioView.getActionsList().setDisable(disabled);
    }

    private void enterEditMode() {
        editMode = true;
        scenarioView.getScenariosList().setDisable(true);
        setFormDisabled(false);
        scenarioView.getEditScenarioButton().setText("Save");
        scenarioView.getScenarioNameField().requestFocus();
        scenarioView.getScenarioNameField().positionCaret(scenarioView.getScenarioNameField().getText().length());
    }

    private void leaveEditMode() {
        editMode = false;
        scenarioView.getScenariosList().setDisable(false);
        setFormDisabled(true);
        scenarioView.getEditScenarioButton().setText("✎");
    }

    private void enterCreateMode() {
        createMode = true;
        editMode = false;
        scenarioView.getScenariosList().setDisable(true);
        scenarioView.getScenariosList().getSelectionModel().clearSelection();
        clearForm();
        setFormDisabled(false);
        scenarioView.getAddScenarioButton().setText("Save scenario");
        scenarioView.getEditScenarioButton().setDisable(true);
        scenarioView.getDeleteScenarioButton().setDisable(false);
        scenarioView.getDeleteScenarioButton().setText("Cancel");
        scenarioView.getEditActionButton().setDisable(false);
        scenarioView.getEditActionButton().setText("Add actions");
        scenarioView.getScenarioNameField().requestFocus();
    }

    private void leaveCreateMode() {
        createMode = false;
        scenarioView.getScenariosList().setDisable(false);
        setFormDisabled(true);
        scenarioView.getAddScenarioButton().setText("Add scenario");
        scenarioView.getDeleteScenarioButton().setText("Delete scenario");
        handleScenarioSelection();
    }

    /**
     * Reloads the current scenario data into the view.
     */
    public void refresh() {
        refresh(null);
    }

    /**
     * Reloads the current scenario data and optionally reselects one entry.
     *
     * @param scenarioIdToReselect the scenario identifier to reselect
     */
    public void refresh(final String scenarioIdToReselect) {
        String targetScenarioId = scenarioIdToReselect;
        if (targetScenarioId == null) {
            Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
            targetScenarioId = selectedScenario == null ? null : selectedScenario.getId();
        }

        if (editMode) {
            leaveEditMode();
        }
        if (createMode) {
            leaveCreateMode();
        }

        loadScenarios(targetScenarioId);
        handleScenarioSelection();
    }

    /**
     * Returns the root view of the controller.
     *
     * @return the root node
     */
    public Parent getView() {
        return scenarioView.getRoot();
    }
}
