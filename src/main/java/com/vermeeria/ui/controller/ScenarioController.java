package com.vermeeria.ui.controller;

import com.vermeeria.model.Scenario;
import com.vermeeria.model.ScenarioAction;
import com.vermeeria.service.DeviceService;
import com.vermeeria.service.ScenarioService;
import com.vermeeria.ui.dialog.ActionEditorDialog;
import com.vermeeria.ui.view.ScenarioView;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;

import java.util.ArrayList;
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
    private final DeviceService deviceService;
    private final Consumer<String> statusUpdater;
    private final Consumer<Boolean> navigationLockUpdater;
    private final Consumer<Scenario> scenarioRunner;
    private final ActionEditorDialog actionEditorDialog;
    private boolean editMode;
    private boolean createMode;
    private List<ScenarioAction> workingActions;

    /**
     * Creates the scenario controller and initializes the scenario controls.
     *
     * @param scenarioView          the scenario view
     * @param scenarioService       the scenario service
     * @param deviceService         the device service
     * @param statusUpdater         the callback used to update status messages
     * @param navigationLockUpdater the callback used to lock navigation
     * @param scenarioRunner        the callback used to execute a scenario
     */
    public ScenarioController(final ScenarioView scenarioView, final ScenarioService scenarioService, final DeviceService deviceService, final Consumer<String> statusUpdater, final Consumer<Boolean> navigationLockUpdater, final Consumer<Scenario> scenarioRunner) {
        this.scenarioView = scenarioView;
        this.scenarioService = scenarioService;
        this.deviceService = deviceService;
        this.statusUpdater = statusUpdater;
        this.navigationLockUpdater = navigationLockUpdater;
        this.scenarioRunner = scenarioRunner;
        this.actionEditorDialog = new ActionEditorDialog(deviceService);
        this.editMode = false;
        this.createMode = false;
        this.workingActions = new ArrayList<>();

        wireActions();
        initializeActionList();
        refresh();
    }

    private void wireActions() {
        scenarioView.getScenariosList()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleScenarioSelection());
        scenarioView.getAddScenarioButton().setOnAction(event -> handleAddScenario());
        scenarioView.getRunScenarioButton().setOnAction(event -> handleRunScenario());
        scenarioView.getEditScenarioButton().setOnAction(event -> handleEditScenario());
        scenarioView.getDeleteScenarioButton().setOnAction(event -> handleDeleteScenario());
        scenarioView.getEditActionButton().setOnAction(event -> handleEditAction());
    }

    private void initializeActionList() {
        scenarioView.getActionsList().setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(final ScenarioAction item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : deviceService.formatScenarioAction(item));
            }
        });
    }

    private void handleAddScenario() {
        if (editMode) {
            statusUpdater.accept("Please save the current scenario changes first");
            return;
        }

        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        if (!createMode && selectedScenario != null) {
            enterCreateMode();
            statusUpdater.accept("Enter the details for the new scenario");
            return;
        }

        try {
            scenarioService.createScenario(scenarioView.getScenarioNameField()
                    .getText(), scenarioView.getScenarioDescriptionArea().getText(), workingActions);
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
            scenarioService.updateScenario(selectedScenario.getId(), scenarioView.getScenarioNameField()
                    .getText(), scenarioView.getScenarioDescriptionArea().getText(), workingActions);
            refresh(selectedScenario.getId());
            leaveEditMode();
            statusUpdater.accept("Scenario updated");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleRunScenario() {
        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        if (selectedScenario == null) {
            statusUpdater.accept("Please select a scenario to run");
            return;
        }

        scenarioRunner.accept(selectedScenario);
    }

    private void handleDeleteScenario() {
        if (editMode) {
            statusUpdater.accept("Please save the current scenario changes first");
            return;
        }

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
        if (!createMode && scenarioView.getScenariosList().getSelectionModel().getSelectedItem() == null) {
            statusUpdater.accept("Please select a scenario first");
            return;
        }

        actionEditorDialog.showAndWait(scenarioView.getRoot().getScene() == null ? null : scenarioView.getRoot()
                .getScene()
                .getWindow(), workingActions).ifPresent(updatedActions -> {
            workingActions = new ArrayList<>(updatedActions);
            scenarioView.getActionsList().getItems().setAll(workingActions);

            if (createMode) {
                statusUpdater.accept("Scenario actions prepared");
                return;
            }

            Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
            scenarioService.updateScenarioActions(selectedScenario.getId(), workingActions);
            refresh(selectedScenario.getId());
            statusUpdater.accept("Scenario actions updated");
        });
    }

    private void loadScenarios(final String scenarioIdToReselect) {
        scenarioView.getScenariosList().getItems().setAll(scenarioService.getAllScenarios());
        if (scenarioIdToReselect != null) {
            scenarioView.getScenariosList()
                    .getItems()
                    .stream()
                    .filter(scenario -> scenarioIdToReselect.equals(scenario.getId()))
                    .findFirst()
                    .ifPresent(scenario -> scenarioView.getScenariosList().getSelectionModel().select(scenario));
        }
    }

    private void handleScenarioSelection() {
        Scenario selectedScenario = scenarioView.getScenariosList().getSelectionModel().getSelectedItem();
        boolean scenarioSelected = selectedScenario != null;

        scenarioView.getRunScenarioButton().setDisable(!scenarioSelected || createMode || editMode);
        scenarioView.getEditScenarioButton().setDisable(!scenarioSelected);
        scenarioView.getDeleteScenarioButton().setDisable(!scenarioSelected);
        scenarioView.getEditActionButton().setDisable(!scenarioSelected);
        scenarioView.getEditActionButton().setText("Edit actions");

        List<ScenarioAction> actions = scenarioSelected ? selectedScenario.getActions() : List.of();
        workingActions = new ArrayList<>(actions);
        scenarioView.getActionsList().getItems().setAll(workingActions);

        if (scenarioSelected && !createMode && !editMode) {
            scenarioView.getScenarioNameField().setText(selectedScenario.getName());
            scenarioView.getScenarioDescriptionArea().setText(selectedScenario.getDescription());
        } else if (!scenarioSelected && !createMode) {
            activateNewScenarioMode();
            return;
        }

        if (!editMode && !createMode) {
            setFormDisabled(true);
            scenarioView.getEditScenarioButton().setText("✎");
        }
    }

    private void clearForm() {
        scenarioView.getScenarioNameField().clear();
        scenarioView.getScenarioDescriptionArea().clear();
        workingActions = new ArrayList<>();
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
        navigationLockUpdater.accept(true);
        scenarioView.getRunScenarioButton().setDisable(true);
        scenarioView.getAddScenarioButton().setDisable(true);
        scenarioView.getDeleteScenarioButton().setDisable(true);
        scenarioView.getEditScenarioButton().setText("Save");
        scenarioView.getScenarioNameField().requestFocus();
        scenarioView.getScenarioNameField().positionCaret(scenarioView.getScenarioNameField().getText().length());
    }

    private void leaveEditMode() {
        editMode = false;
        scenarioView.getScenariosList().setDisable(false);
        setFormDisabled(true);
        navigationLockUpdater.accept(false);
        scenarioView.getAddScenarioButton().setDisable(false);
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
        scenarioView.getRunScenarioButton().setDisable(true);
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

    private void activateNewScenarioMode() {
        clearForm();
        setFormDisabled(false);
        scenarioView.getRunScenarioButton().setDisable(true);
        scenarioView.getEditScenarioButton().setDisable(true);
        scenarioView.getDeleteScenarioButton().setDisable(true);
        scenarioView.getDeleteScenarioButton().setText("Delete scenario");
        scenarioView.getEditActionButton().setDisable(false);
        scenarioView.getEditActionButton().setText("Add actions");
        scenarioView.getEditScenarioButton().setText("✎");
        scenarioView.getAddScenarioButton().setText("Add scenario");
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
