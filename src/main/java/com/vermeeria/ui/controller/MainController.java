package com.vermeeria.ui.controller;

import com.vermeeria.service.DeviceService;
import com.vermeeria.service.RoomService;
import com.vermeeria.service.ScenarioService;
import com.vermeeria.service.SmartHomeService;
import com.vermeeria.ui.view.DeviceView;
import com.vermeeria.ui.view.MainView;
import com.vermeeria.ui.view.RoomView;
import com.vermeeria.ui.view.ScenarioView;
import javafx.scene.Parent;

/**
 * Controls the main window of the HomeFlow application.
 *
 * @author Jette
 */
public class MainController {

    private final SmartHomeService smartHomeService;
    private final MainView mainView;
    private final RoomController roomController;
    private final DeviceController deviceController;
    private final ScenarioController scenarioController;

    /**
     * Creates the main controller and initializes the basic user interface.
     *
     * @param smartHomeService the central application service
     */
    public MainController(final SmartHomeService smartHomeService, final RoomService roomService, final DeviceService deviceService, final ScenarioService scenarioService) {
        this.smartHomeService = smartHomeService;
        this.mainView = new MainView();

        RoomView roomView = new RoomView();
        DeviceView deviceView = new DeviceView();
        ScenarioView scenarioView = new ScenarioView();
        this.roomController = new RoomController(roomView, roomService, this::updateStatus);
        this.deviceController = new DeviceController(deviceView, deviceService, this::updateStatus);
        this.scenarioController = new ScenarioController(scenarioView, scenarioService, this::updateStatus);

        wireActions();
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
        String selectedEntry = mainView.getNavigationList().getSelectionModel().getSelectedItem();
        if ("Devices".equals(selectedEntry)) {
            showDevicesView();
        } else if ("Scenarios".equals(selectedEntry)) {
            showScenariosView();
        } else {
            showRoomsView();
        }
    }

    private void updateStatus(final String message) {
        mainView.getStatusLabel().setText(message + " · " + smartHomeService.getRoomCount() + " rooms · " + smartHomeService.getDeviceCount() + " devices · " + smartHomeService.getScenarioCount() + " scenarios");
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
