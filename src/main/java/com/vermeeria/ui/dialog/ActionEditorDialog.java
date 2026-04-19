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
        Dialog<List<ScenarioAction>> dialog = new Dialog<>();
        dialog.setTitle("Edit actions");
        dialog.setHeaderText(null);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ObservableList<ScenarioAction> workingActions = FXCollections.observableArrayList(copyActions(initialActions));

        ListView<ScenarioAction> actionsList = new ListView<>(workingActions);
        actionsList.setPrefHeight(220);
        actionsList.getStyleClass().add("navigation-list");
        actionsList.setCellFactory(listView -> createActionCell(workingActions));

        ComboBox<DeviceDefinition> deviceBox = new ComboBox<>();
        deviceBox.getItems().setAll(deviceService.getAllDevices());
        deviceBox.setMaxWidth(Double.MAX_VALUE);
        deviceBox.setMinHeight(40);
        deviceBox.setPrefHeight(40);
        deviceBox.setMaxHeight(40);
        deviceBox.getStyleClass().add("form-input");

        ComboBox<ActionSpec> actionBox = new ComboBox<>();
        actionBox.setMaxWidth(Double.MAX_VALUE);
        actionBox.setMinHeight(40);
        actionBox.setPrefHeight(40);
        actionBox.setMaxHeight(40);
        actionBox.getStyleClass().add("form-input");

        Label parameterLabel = new Label("Parameter");
        TextField parameterField = new TextField();
        parameterField.setPromptText("Enter parameter");
        parameterField.getStyleClass().add("form-input");

        ComboBox<String> selectionBox = new ComboBox<>();
        selectionBox.setMaxWidth(Double.MAX_VALUE);
        selectionBox.setMinHeight(40);
        selectionBox.setPrefHeight(40);
        selectionBox.setMaxHeight(40);
        selectionBox.getStyleClass().add("form-input");

        Label validationLabel = new Label();
        validationLabel.getStyleClass().add("validation-message");
        validationLabel.setVisible(false);
        validationLabel.setManaged(false);

        Button saveActionButton = new Button("Add action");
        Button deleteActionButton = new Button("Delete action");
        deleteActionButton.getStyleClass().add("danger-button");

        deviceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String deviceId = newValue == null ? null : newValue.getId();
            actionBox.getItems().setAll(deviceService.getSupportedActionsForDevice(deviceId));
            actionBox.getSelectionModel().clearSelection();
            parameterField.clear();
            selectionBox.getItems().clear();
            updateParameterInput(null, parameterLabel, parameterField, selectionBox);
            clearValidation(validationLabel);
        });

        actionBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            parameterField.clear();
            selectionBox.getItems().setAll(newValue == null ? List.of() : newValue.allowedValues());
            if (!selectionBox.getItems().isEmpty()) {
                selectionBox.getSelectionModel().selectFirst();
            }
            updateParameterInput(newValue, parameterLabel, parameterField, selectionBox);
            clearValidation(validationLabel);
        });

        actionsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean actionSelected = newValue != null;
            deleteActionButton.setDisable(!actionSelected);
            if (!actionSelected) {
                saveActionButton.setText("Add action");
                return;
            }

            saveActionButton.setText("Update action");
            selectDevice(deviceBox, newValue.getDeviceId());
            selectAction(actionBox, newValue.getActionKey());
            ActionSpec selectedSpec = actionBox.getSelectionModel().getSelectedItem();
            if (selectedSpec != null && selectedSpec.parameterKind() == ActionParameterKind.SELECTION) {
                selectionBox.getSelectionModel().select(newValue.getParameterValue());
            } else {
                parameterField.setText(newValue.getParameterValue());
            }
            clearValidation(validationLabel);
        });

        saveActionButton.setOnAction(event -> {
            DeviceDefinition selectedDevice = deviceBox.getSelectionModel().getSelectedItem();
            ActionSpec selectedSpec = actionBox.getSelectionModel().getSelectedItem();
            if (selectedDevice == null || selectedSpec == null) {
                showValidation(validationLabel, "Please select a device and an action.");
                return;
            }

            String parameterValue = extractParameterValue(selectedSpec, parameterField, selectionBox);
            String validationMessage = validateParameterValue(selectedSpec, parameterValue);
            if (validationMessage != null) {
                showValidation(validationLabel, validationMessage);
                return;
            }

            ScenarioAction newAction = new ScenarioAction(selectedDevice.getId(), selectedSpec.actionKey(), parameterValue);

            int selectedIndex = actionsList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                workingActions.set(selectedIndex, newAction);
            } else {
                workingActions.add(newAction);
            }

            actionsList.getSelectionModel().clearSelection();
            actionsList.refresh();
            clearForm(deviceBox, actionBox, parameterField, selectionBox, parameterLabel);
            clearValidation(validationLabel);
        });

        deleteActionButton.setDisable(true);
        deleteActionButton.setOnAction(event -> {
            int selectedIndex = actionsList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                workingActions.remove(selectedIndex);
                actionsList.getSelectionModel().clearSelection();
                actionsList.refresh();
                clearForm(deviceBox, actionBox, parameterField, selectionBox, parameterLabel);
                clearValidation(validationLabel);
            }
        });

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(10);
        formGrid.add(new Label("Device"), 0, 0);
        formGrid.add(deviceBox, 1, 0);
        formGrid.add(new Label("Action"), 0, 1);
        formGrid.add(actionBox, 1, 1);
        formGrid.add(parameterLabel, 0, 2);
        formGrid.add(parameterField, 1, 2);
        formGrid.add(selectionBox, 1, 2);
        GridPane.setHgrow(deviceBox, Priority.ALWAYS);
        GridPane.setHgrow(actionBox, Priority.ALWAYS);
        GridPane.setHgrow(parameterField, Priority.ALWAYS);
        GridPane.setHgrow(selectionBox, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actionButtonBar = new HBox(10, saveActionButton, spacer, deleteActionButton);

        VBox content = new VBox(14, new Label("Configured actions"), actionsList, formGrid, validationLabel, actionButtonBar);
        content.setPadding(new Insets(18));
        content.getStyleClass().add("dialog-content");
        VBox.setVgrow(actionsList, Priority.ALWAYS);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinWidth(620);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(new ButtonType("Save", ButtonBar.ButtonData.OK_DONE), new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));

        updateParameterInput(null, parameterLabel, parameterField, selectionBox);

        dialog.setResultConverter(buttonType -> buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE ? new ArrayList<>(workingActions) : null);

        return dialog.showAndWait();
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
        } catch (NumberFormatException exception) {
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
}
