package com.vermeeria.ui.controller;

import com.vermeeria.model.ExecutionLogEntry;
import com.vermeeria.model.Scenario;
import javafx.geometry.Insets;
import com.vermeeria.service.DeviceService;
import com.vermeeria.service.RoomService;
import com.vermeeria.service.ScenarioService;
import com.vermeeria.service.SmartHomeService;
import com.vermeeria.ui.view.DeviceView;
import com.vermeeria.ui.view.MainView;
import com.vermeeria.ui.view.RoomView;
import com.vermeeria.ui.view.ScenarioView;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controls the main window of the HomeFlow application.
 *
 * @author Jette
 */
public class MainController {

    private final SmartHomeService smartHomeService;
    private final DeviceService deviceService;
    private final ScenarioService scenarioService;
    private final MainView mainView;
    private final RoomController roomController;
    private final DeviceController deviceController;
    private final ScenarioController scenarioController;
    private boolean logExpanded;

    /**
     * Creates the main controller and initializes the basic user interface.
     *
     * @param smartHomeService the central application service
     */
    public MainController(final SmartHomeService smartHomeService, final RoomService roomService, final DeviceService deviceService, final ScenarioService scenarioService) {
        this.smartHomeService = smartHomeService;
        this.deviceService = deviceService;
        this.scenarioService = scenarioService;
        this.mainView = new MainView();
        this.logExpanded = false;

        RoomView roomView = new RoomView();
        DeviceView deviceView = new DeviceView();
        ScenarioView scenarioView = new ScenarioView();
        this.roomController = new RoomController(roomView, roomService, this::updateStatus, this::setNavigationLocked);
        this.deviceController = new DeviceController(deviceView, deviceService, this::updateStatus, this::setNavigationLocked);
        this.scenarioController = new ScenarioController(scenarioView, scenarioService, deviceService, this::updateStatus, this::setNavigationLocked, this::executeScenario);
        initializeExecutionLogList();

        wireActions();
        refreshExecutionLogs();
        showRoomsView();
        updateStatus("Ready");
    }

    private void wireActions() {
        mainView.getNavigationList().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Rooms".equals(newValue)) {
                showRoomsView();
            } else if ("Devices".equals(newValue)) {
                showDevicesView();
            } else if ("Scenarios".equals(newValue)) {
                showScenariosView();
            }
        });

        mainView.getLoadButton().setOnAction(event -> handleLoadProject());
        mainView.getSaveButton().setOnAction(event -> handleSaveProject());
        mainView.getExecuteButton().setOnAction(event -> handleExecuteScenario());
        mainView.getToggleLogButton().setOnAction(event -> toggleLogPanel());
    }

    private void handleLoadProject() {
        smartHomeService.reloadAll();
        refreshCurrentView();
        refreshExecutionLogs();
        updateStatus("Loaded project data");
    }

    private void handleSaveProject() {
        smartHomeService.saveAll();
        updateStatus("Saved project data");
    }

    private void handleExecuteScenario() {
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        if (scenarios.isEmpty()) {
            updateStatus("No scenarios are available to run");
            return;
        }

        Dialog<Scenario> dialog = new Dialog<>();
        dialog.setTitle("Run scenario");
        dialog.setHeaderText(null);
        dialog.setResizable(false);
        if (mainView.getRoot().getScene() != null) {
            dialog.initOwner(mainView.getRoot().getScene().getWindow());
        }
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ListView<Scenario> scenarioList = new ListView<>();
        scenarioList.getItems().setAll(scenarios);
        scenarioList.getStyleClass().add("navigation-list");
        scenarioList.setPrefSize(320, 220);
        scenarioList.setMaxSize(320, 220);

        VBox content = new VBox(scenarioList);
        content.setPadding(new Insets(14));
        content.getStyleClass().add("dialog-content");

        ButtonType runButtonType = new ButtonType("Run", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(runButtonType, new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(348, 290);
        dialog.getDialogPane().setMinSize(348, 290);
        dialog.getDialogPane().setMaxSize(348, 290);

        Button runButton = (Button) dialog.getDialogPane().lookupButton(runButtonType);
        runButton.setDisable(true);
        scenarioList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> runButton.setDisable(newValue == null)
        );

        dialog.setResultConverter(
                buttonType -> buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE ? scenarioList.getSelectionModel().getSelectedItem() : null
        );

        dialog.showAndWait().ifPresent(this::executeScenario);
    }

    private void refreshCurrentView() {
        String selectedEntry = mainView.getNavigationList().getSelectionModel().getSelectedItem();
        if ("Devices".equals(selectedEntry)) {
            showDevicesView();
        } else if ("Scenarios".equals(selectedEntry)) {
            showScenariosView();
        } else {
            showRoomsView();
        }
    }

    private void initializeExecutionLogList() {
        mainView.getExecutionLogList().setCellFactory(listView -> new ListCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.getStyleClass().add("log-entry-label");
                setGraphic(label);
            }

            @Override
            protected void updateItem(final ExecutionLogEntry item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    label.setText(null);
                    setGraphic(null);
                    return;
                }
                label.setText(item.toString());
                setGraphic(label);
            }
        });
    }

    private void updateStatus(final String message) {
        mainView.getStatusLabel().setText(message + " · " + smartHomeService.getRoomCount() + " rooms · " + smartHomeService.getDeviceCount() + " devices · " + smartHomeService.getScenarioCount() + " scenarios");
    }

    private void executeScenario(final Scenario scenario) {
        if (scenario == null) {
            updateStatus("Please select a scenario to run");
            return;
        }

        try {
            List<ExecutionLogEntry> createdLogs = scenarioService.executeScenario(scenario.getId(), deviceService);
            refreshCurrentView();
            refreshExecutionLogs();
            updateStatus("Executed scenario " + scenario.getName() + " with " + createdLogs.size() + " actions");
        } catch (RuntimeException exception) {
            updateStatus(exception.getMessage());
        }
    }

    private void refreshExecutionLogs() {
        mainView.getExecutionLogList().getItems().setAll(scenarioService.getExecutionLogs());
    }

    private void toggleLogPanel() {
        logExpanded = !logExpanded;
        mainView.setLogExpanded(logExpanded);
    }

    private void setNavigationLocked(final boolean locked) {
        mainView.getNavigationList().setDisable(locked);
    }

    private void showRoomsView() {
        roomController.refresh();
        mainView.getContentArea().getChildren().setAll(roomController.getView());
        updateStatus("Rooms view opened");
    }

    private void showDevicesView() {
        deviceController.refresh();
        mainView.getContentArea().getChildren().setAll(deviceController.getView());
        updateStatus("Devices view opened");
    }

    private void showScenariosView() {
        scenarioController.refresh();
        mainView.getContentArea().getChildren().setAll(scenarioController.getView());
        updateStatus("Scenarios view opened");
    }

    /**
     * Returns the root view of the controller.
     *
     * @return the root node
     */
    public Parent getView() {
        return mainView.getRoot();
    }
}
