package com.vermeeria.ui.dialog;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.ScenarioAction;
import com.vermeeria.service.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dialog used to add, edit and remove scenario actions.
 *
 * @author Jette
 */
public class ActionEditorDialog {

    private static final int MIN_TEMPERATURE = 5;
    private static final int MAX_TEMPERATURE = 35;
    private static final String FORM_INPUT = "form-input";
    private final DeviceService deviceService;

    /**
     * Creates an action editor dialog.
     *
     * @param deviceService the device service
     */
    public ActionEditorDialog(final DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Opens the action editor and returns the updated list of actions.
     *
     * @param owner          the owning window
     * @param initialActions the current action list
     * @return the updated action list
     */
    public Optional<List<ScenarioAction>> showAndWait(final Window owner, final List<ScenarioAction> initialActions) {
        ObservableList<ScenarioAction> workingActions = FXCollections.observableArrayList(copyActions(initialActions));
        Dialog<List<ScenarioAction>> dialog = createDialog(owner);
        DialogControls controls = createDialogControls(workingActions);

        configureDeviceListener(controls);
        configureActionListener(controls);
        configureActionSelectionListener(controls);
        configureSaveActionHandler(controls, workingActions);
        configureDeleteActionHandler(controls, workingActions);

        dialog.getDialogPane().setContent(buildDialogContent(controls));
        dialog.getDialogPane().setMinWidth(620);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(new ButtonType("Save", ButtonBar.ButtonData.OK_DONE), new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));

        updateParameterInput(null, controls.parameterLabel(), controls.parameterField(), controls.selectionBox());

        dialog.setResultConverter(buttonType -> buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE ? new ArrayList<>(workingActions) : null);

        return dialog.showAndWait();
    }

    private Dialog<List<ScenarioAction>> createDialog(final Window owner) {
        Dialog<List<ScenarioAction>> dialog = new Dialog<>();
        dialog.setTitle("Edit actions");
        dialog.setHeaderText(null);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        return dialog;
    }

    private DialogControls createDialogControls(final ObservableList<ScenarioAction> workingActions) {
        ListView<ScenarioAction> actionsList = createActionsList(workingActions);
        ComboBox<DeviceDefinition> deviceBox = createDeviceBox();
        ComboBox<ActionSpec> actionBox = createActionBox();
        Label parameterLabel = new Label("Parameter");
        TextField parameterField = createParameterField();
        ComboBox<String> selectionBox = createSelectionBox();
        Label validationLabel = createValidationLabel();
        Button saveActionButton = new Button("Add action");
        Button deleteActionButton = new Button("Delete action");
        deleteActionButton.getStyleClass().add("danger-button");
        deleteActionButton.setDisable(true);

        return new DialogControls(actionsList, deviceBox, actionBox, parameterLabel, parameterField, selectionBox, validationLabel, saveActionButton, deleteActionButton);
    }

    private ListView<ScenarioAction> createActionsList(final ObservableList<ScenarioAction> workingActions) {
        ListView<ScenarioAction> actionsList = new ListView<>(workingActions);
        actionsList.setPrefHeight(220);
        actionsList.getStyleClass().add("navigation-list");
        actionsList.setCellFactory(listView -> createActionCell(workingActions));
        return actionsList;
    }

    private ComboBox<DeviceDefinition> createDeviceBox() {
        ComboBox<DeviceDefinition> deviceBox = new ComboBox<>();
        deviceBox.getItems().setAll(deviceService.getAllDevices());
        applyComboBoxStyle(deviceBox);
        return deviceBox;
    }

    private ComboBox<ActionSpec> createActionBox() {
        ComboBox<ActionSpec> actionBox = new ComboBox<>();
        applyComboBoxStyle(actionBox);
        return actionBox;
    }

    private TextField createParameterField() {
        TextField parameterField = new TextField();
        parameterField.setPromptText("Enter parameter");
        parameterField.getStyleClass().add(FORM_INPUT);
        return parameterField;
    }

    private ComboBox<String> createSelectionBox() {
        ComboBox<String> selectionBox = new ComboBox<>();
        applyComboBoxStyle(selectionBox);
        return selectionBox;
    }

    private Label createValidationLabel() {
        Label validationLabel = new Label();
        validationLabel.getStyleClass().add("validation-message");
        validationLabel.setVisible(false);
        validationLabel.setManaged(false);
        return validationLabel;
    }

    private <T> void applyComboBoxStyle(final ComboBox<T> comboBox) {
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setMinHeight(40);
        comboBox.setPrefHeight(40);
        comboBox.setMaxHeight(40);
        comboBox.getStyleClass().add(FORM_INPUT);
    }

    private void configureDeviceListener(final DialogControls controls) {
        controls.deviceBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            String deviceId = newValue == null ? null : newValue.getId();
            controls.actionBox().getItems().setAll(deviceService.getSupportedActionsForDevice(deviceId));
            controls.actionBox().getSelectionModel().clearSelection();
            controls.parameterField().clear();
            controls.selectionBox().getItems().clear();
            updateParameterInput(null, controls.parameterLabel(), controls.parameterField(), controls.selectionBox());
            clearValidation(controls.validationLabel());
        });
    }

    private void configureActionListener(final DialogControls controls) {
        controls.actionBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            controls.parameterField().clear();
            controls.selectionBox().getItems().setAll(newValue == null ? List.of() : newValue.allowedValues());
            if (!controls.selectionBox().getItems().isEmpty()) {
                controls.selectionBox().getSelectionModel().selectFirst();
            }
            updateParameterInput(newValue, controls.parameterLabel(), controls.parameterField(), controls.selectionBox());
            clearValidation(controls.validationLabel());
        });
    }

    private void configureActionSelectionListener(final DialogControls controls) {
        controls.actionsList()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    boolean actionSelected = newValue != null;
                    controls.deleteActionButton().setDisable(!actionSelected);
                    if (!actionSelected) {
                        controls.saveActionButton().setText("Add action");
                        return;
                    }

                    controls.saveActionButton().setText("Update action");
                    selectDevice(controls.deviceBox(), newValue.getDeviceId());
                    selectAction(controls.actionBox(), newValue.getActionKey());
                    ActionSpec selectedSpec = controls.actionBox().getSelectionModel().getSelectedItem();
                    if (selectedSpec != null && selectedSpec.parameterKind() == ActionParameterKind.SELECTION) {
                        controls.selectionBox().getSelectionModel().select(newValue.getParameterValue());
                    } else {
                        controls.parameterField().setText(newValue.getParameterValue());
                    }
                    clearValidation(controls.validationLabel());
                });
    }

    private void configureSaveActionHandler(final DialogControls controls, final ObservableList<ScenarioAction> workingActions) {
        controls.saveActionButton().setOnAction(event -> {
            DeviceDefinition selectedDevice = controls.deviceBox().getSelectionModel().getSelectedItem();
            ActionSpec selectedSpec = controls.actionBox().getSelectionModel().getSelectedItem();
            if (selectedDevice == null || selectedSpec == null) {
                showValidation(controls.validationLabel(), "Please select a device and an action.");
                return;
            }

            String parameterValue = extractParameterValue(selectedSpec, controls.parameterField(), controls.selectionBox());
            String validationMessage = validateParameterValue(selectedSpec, parameterValue);
            if (validationMessage != null) {
                showValidation(controls.validationLabel(), validationMessage);
                return;
            }

            ScenarioAction newAction = new ScenarioAction(selectedDevice.getId(), selectedSpec.actionKey(), parameterValue);

            int selectedIndex = controls.actionsList().getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                workingActions.set(selectedIndex, newAction);
            } else {
                workingActions.add(newAction);
            }

            controls.actionsList().getSelectionModel().clearSelection();
            controls.actionsList().refresh();
            clearForm(controls.deviceBox(), controls.actionBox(), controls.parameterField(), controls.selectionBox(), controls.parameterLabel());
            clearValidation(controls.validationLabel());
        });
    }

    private void configureDeleteActionHandler(final DialogControls controls, final ObservableList<ScenarioAction> workingActions) {
        controls.deleteActionButton().setOnAction(event -> {
            int selectedIndex = controls.actionsList().getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                workingActions.remove(selectedIndex);
                controls.actionsList().getSelectionModel().clearSelection();
                controls.actionsList().refresh();
                clearForm(controls.deviceBox(), controls.actionBox(), controls.parameterField(), controls.selectionBox(), controls.parameterLabel());
                clearValidation(controls.validationLabel());
            }
        });
    }

    private VBox buildDialogContent(final DialogControls controls) {
        GridPane formGrid = buildFormGrid(controls);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actionButtonBar = new HBox(10, controls.saveActionButton(), spacer, controls.deleteActionButton());

        VBox content = new VBox(14, new Label("Configured actions"), controls.actionsList(), formGrid, controls.validationLabel(), actionButtonBar);
        content.setPadding(new Insets(18));
        content.getStyleClass().add("dialog-content");
        VBox.setVgrow(controls.actionsList(), Priority.ALWAYS);
        return content;
    }

    private GridPane buildFormGrid(final DialogControls controls) {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(10);
        formGrid.add(new Label("Device"), 0, 0);
        formGrid.add(controls.deviceBox(), 1, 0);
        formGrid.add(new Label("Action"), 0, 1);
        formGrid.add(controls.actionBox(), 1, 1);
        formGrid.add(controls.parameterLabel(), 0, 2);
        formGrid.add(controls.parameterField(), 1, 2);
        formGrid.add(controls.selectionBox(), 1, 2);
        GridPane.setHgrow(controls.deviceBox(), Priority.ALWAYS);
        GridPane.setHgrow(controls.actionBox(), Priority.ALWAYS);
        GridPane.setHgrow(controls.parameterField(), Priority.ALWAYS);
        GridPane.setHgrow(controls.selectionBox(), Priority.ALWAYS);
        return formGrid;
    }

    private void updateParameterInput(final ActionSpec actionSpec, final Label parameterLabel, final TextField parameterField, final ComboBox<String> selectionBox) {
        ActionParameterKind parameterKind = actionSpec == null ? ActionParameterKind.NONE : actionSpec.parameterKind();
        String parameterText = actionSpec == null || actionSpec.parameterLabel()
                .isBlank() ? "Parameter" : actionSpec.parameterLabel();
        parameterLabel.setText(parameterText);

        boolean requiresParameter = parameterKind != ActionParameterKind.NONE;
        boolean usesSelection = parameterKind == ActionParameterKind.SELECTION;

        parameterLabel.setManaged(requiresParameter);
        parameterLabel.setVisible(requiresParameter);
        parameterField.setManaged(requiresParameter && !usesSelection);
        parameterField.setVisible(requiresParameter && !usesSelection);
        selectionBox.setManaged(requiresParameter && usesSelection);
        selectionBox.setVisible(requiresParameter && usesSelection);
    }

    private String extractParameterValue(final ActionSpec actionSpec, final TextField parameterField, final ComboBox<String> selectionBox) {
        if (actionSpec.parameterKind() == ActionParameterKind.NONE) {
            return "";
        }
        if (actionSpec.parameterKind() == ActionParameterKind.SELECTION) {
            String selectedValue = selectionBox.getSelectionModel().getSelectedItem();
            return selectedValue == null ? "" : selectedValue;
        }
        return parameterField.getText() == null ? "" : parameterField.getText().trim();
    }

    private String validateParameterValue(final ActionSpec actionSpec, final String parameterValue) {
        if (actionSpec.parameterKind() == ActionParameterKind.NONE) {
            return null;
        }

        if (parameterValue == null || parameterValue.isBlank()) {
            return actionSpec.parameterLabel()
                    .isBlank() ? "Please provide a value." : actionSpec.parameterLabel() + " cannot be empty.";
        }

        return switch (actionSpec.parameterKind()) {
            case SELECTION ->
                    actionSpec.allowedValues().contains(parameterValue) ? null : "Please select a valid value.";
            case PERCENTAGE -> validateIntegerRange(parameterValue, 0, 100, "Percentage");
            case TEMPERATURE -> validateIntegerRange(parameterValue, MIN_TEMPERATURE, MAX_TEMPERATURE, "Temperature");
            case BOOLEAN, NONE -> null;
        };
    }

    private String validateIntegerRange(final String value, final int minimum, final int maximum, final String label) {
        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue < minimum || parsedValue > maximum) {
                return label + " must be between " + minimum + " and " + maximum + ".";
            }
            return null;
        } catch (NumberFormatException _) {
            return label + " must be a number.";
        }
    }

    private void selectDevice(final ComboBox<DeviceDefinition> deviceBox, final String deviceId) {
        deviceBox.getItems()
                .stream()
                .filter(device -> deviceId.equals(device.getId()))
                .findFirst()
                .ifPresent(device -> deviceBox.getSelectionModel().select(device));
    }

    private void selectAction(final ComboBox<ActionSpec> actionBox, final String actionKey) {
        actionBox.getItems()
                .stream()
                .filter(actionSpec -> actionKey.equals(actionSpec.actionKey()))
                .findFirst()
                .ifPresent(actionSpec -> actionBox.getSelectionModel().select(actionSpec));
    }

    private void clearForm(final ComboBox<DeviceDefinition> deviceBox, final ComboBox<ActionSpec> actionBox, final TextField parameterField, final ComboBox<String> selectionBox, final Label parameterLabel) {
        deviceBox.getSelectionModel().clearSelection();
        actionBox.getItems().clear();
        actionBox.getSelectionModel().clearSelection();
        parameterField.clear();
        selectionBox.getItems().clear();
        selectionBox.getSelectionModel().clearSelection();
        updateParameterInput(null, parameterLabel, parameterField, selectionBox);
    }

    private void showValidation(final Label validationLabel, final String message) {
        validationLabel.setText(message);
        validationLabel.setVisible(true);
        validationLabel.setManaged(true);
    }

    private void clearValidation(final Label validationLabel) {
        validationLabel.setText("");
        validationLabel.setVisible(false);
        validationLabel.setManaged(false);
    }

    private ListCell<ScenarioAction> createActionCell(final ObservableList<ScenarioAction> workingActions) {
        return new ListCell<>() {
            private final Label actionLabel = new Label();
            private final Region spacer = new Region();
            private final Button moveUpButton = new Button("↑");
            private final Button moveDownButton = new Button("↓");
            private final HBox root = new HBox(10, actionLabel, spacer, moveUpButton, moveDownButton);

            @Override
            protected void updateItem(final ScenarioAction item, final boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                actionLabel.setText(deviceService.formatScenarioAction(item));
                actionLabel.setWrapText(true);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                moveUpButton.getStyleClass().add("action-move-button");
                moveDownButton.getStyleClass().add("action-move-button");
                moveUpButton.setDisable(getIndex() <= 0);
                moveDownButton.setDisable(getIndex() >= workingActions.size() - 1);
                setGraphic(root);
            }

            {
                root.setPadding(new Insets(2, 4, 2, 0));
                moveUpButton.setFocusTraversable(false);
                moveDownButton.setFocusTraversable(false);

                moveUpButton.setOnAction(event -> moveAction(workingActions, -1));
                moveDownButton.setOnAction(event -> moveAction(workingActions, 1));
            }

            private void moveAction(final ObservableList<ScenarioAction> actions, final int direction) {
                int currentIndex = getIndex();
                int targetIndex = currentIndex + direction;
                if (currentIndex < 0 || targetIndex < 0 || targetIndex >= actions.size()) {
                    return;
                }

                ScenarioAction action = actions.remove(currentIndex);
                actions.add(targetIndex, action);
                getListView().getSelectionModel().select(targetIndex);
                getListView().scrollTo(targetIndex);
                getListView().refresh();
            }
        };
    }

    private List<ScenarioAction> copyActions(final List<ScenarioAction> initialActions) {
        if (initialActions == null) {
            return List.of();
        }

        return initialActions.stream()
                .map(action -> new ScenarioAction(action.getDeviceId(), action.getActionKey(), action.getParameterValue()))
                .toList();
    }

    private record DialogControls(ListView<ScenarioAction> actionsList, ComboBox<DeviceDefinition> deviceBox,
                                  ComboBox<ActionSpec> actionBox, Label parameterLabel, TextField parameterField,
                                  ComboBox<String> selectionBox, Label validationLabel, Button saveActionButton,
                                  Button deleteActionButton) {
    }
}
