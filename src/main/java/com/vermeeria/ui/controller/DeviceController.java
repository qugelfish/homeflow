package com.vermeeria.ui.controller;

import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.Room;
import com.vermeeria.plugin.DevicePlugin;
import com.vermeeria.service.DeviceService;
import com.vermeeria.ui.view.DeviceView;
import javafx.scene.Parent;
import javafx.util.StringConverter;

import java.util.function.Consumer;

/**
 * Controls the devices window of the HomeFlow application.
 *
 * @author Jette
 */
public class DeviceController {

    private final DeviceView deviceView;
    private final DeviceService deviceService;
    private final Consumer<String> statusUpdater;
    private final Consumer<Boolean> navigationLockUpdater;
    private boolean editMode;
    private boolean createMode;

    /**
     * Creates the device controller and initializes the device controls.
     *
     * @param deviceView            the device view
     * @param deviceService         the device service
     * @param statusUpdater         the callback used to update status messages
     * @param navigationLockUpdater the callback used to lock navigation
     */
    public DeviceController(final DeviceView deviceView, final DeviceService deviceService, final Consumer<String> statusUpdater, final Consumer<Boolean> navigationLockUpdater) {
        this.deviceView = deviceView;
        this.deviceService = deviceService;
        this.statusUpdater = statusUpdater;
        this.navigationLockUpdater = navigationLockUpdater;
        this.editMode = false;
        this.createMode = false;

        initializeSelectors();
        wireActions();
        refresh();
    }

    private void initializeSelectors() {
        deviceView.getDeviceTypeBox().setConverter(new StringConverter<>() {
            @Override
            public String toString(final DevicePlugin plugin) {
                return plugin == null ? "" : plugin.getDisplayName();
            }

            @Override
            public DevicePlugin fromString(final String string) {
                return null;
            }
        });

        deviceView.getRoomBox().setConverter(new StringConverter<>() {
            @Override
            public String toString(final Room room) {
                return room == null ? "" : room.getName();
            }

            @Override
            public Room fromString(final String string) {
                return null;
            }
        });
    }

    private void wireActions() {
        deviceView.getDevicesList()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleDeviceSelection());
        deviceView.getAddDeviceButton().setOnAction(event -> handleAddDevice());
        deviceView.getEditDeviceButton().setOnAction(event -> handleEditDevice());
        deviceView.getDeleteDeviceButton().setOnAction(event -> handleDeleteDevice());
    }

    private void handleAddDevice() {
        if (editMode) {
            statusUpdater.accept("Please save the current device changes first");
            return;
        }

        DeviceDefinition selectedDevice = deviceView.getDevicesList().getSelectionModel().getSelectedItem();
        if (!createMode && selectedDevice != null) {
            enterCreateMode();
            statusUpdater.accept("Enter the details for the new device");
            return;
        }

        try {
            deviceService.createDevice(deviceView.getDeviceNameField()
                    .getText(), getSelectedTypeKey(), getSelectedRoomId());
            refresh();
            leaveCreateMode();
            statusUpdater.accept("Device created");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleEditDevice() {
        DeviceDefinition selectedDevice = deviceView.getDevicesList().getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            statusUpdater.accept("Please select a device to edit");
            return;
        }

        if (createMode) {
            leaveCreateMode();
        }
        if (!editMode) {
            enterEditMode();
            statusUpdater.accept("Editing device details");
            return;
        }

        try {
            deviceService.updateDevice(selectedDevice.getId(), deviceView.getDeviceNameField()
                    .getText(), getSelectedTypeKey(), getSelectedRoomId());
            refresh(selectedDevice.getId());
            leaveEditMode();
            statusUpdater.accept("Device updated");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleDeleteDevice() {
        if (editMode) {
            statusUpdater.accept("Please save the current device changes first");
            return;
        }

        if (createMode) {
            leaveCreateMode();
            statusUpdater.accept("Device creation cancelled");
            return;
        }

        DeviceDefinition selectedDevice = deviceView.getDevicesList().getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            statusUpdater.accept("Please select a device to delete");
            return;
        }

        try {
            deviceService.deleteDevice(selectedDevice.getId());
            leaveEditMode();
            leaveCreateMode();
            refresh();
            statusUpdater.accept("Device deleted");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private String getSelectedTypeKey() {
        DevicePlugin selectedPlugin = deviceView.getDeviceTypeBox().getSelectionModel().getSelectedItem();
        return selectedPlugin == null ? null : selectedPlugin.getTypeKey();
    }

    private String getSelectedRoomId() {
        Room selectedRoom = deviceView.getRoomBox().getSelectionModel().getSelectedItem();
        return selectedRoom == null ? null : selectedRoom.getId();
    }

    private void loadDevices(final String deviceIdToReselect) {
        deviceView.getDevicesList().getItems().setAll(deviceService.getAllDevices());
        if (deviceIdToReselect != null) {
            deviceView.getDevicesList()
                    .getItems()
                    .stream()
                    .filter(device -> deviceIdToReselect.equals(device.getId()))
                    .findFirst()
                    .ifPresent(device -> deviceView.getDevicesList().getSelectionModel().select(device));
        }
    }

    private void loadSelectableValues() {
        deviceView.getDeviceTypeBox().getItems().setAll(deviceService.getAvailableDevicePlugins());
        deviceView.getRoomBox().getItems().setAll(deviceService.getAvailableRooms());
    }

    private void handleDeviceSelection() {
        DeviceDefinition selectedDevice = deviceView.getDevicesList().getSelectionModel().getSelectedItem();
        boolean deviceSelected = selectedDevice != null;

        deviceView.getEditDeviceButton().setDisable(!deviceSelected);
        deviceView.getDeleteDeviceButton().setDisable(!deviceSelected);

        if (deviceSelected && !createMode && !editMode) {
            deviceView.getDeviceNameField().setText(selectedDevice.getName());
            selectPluginByTypeKey(selectedDevice.getTypeKey());
            selectRoomById(selectedDevice.getRoomId());
            deviceView.getDeviceStateValueLabel().setText(deviceService.formatDeviceStateDetails(selectedDevice));
        } else if (!deviceSelected && !createMode) {
            activateNewDeviceMode();
            return;
        }

        if (!editMode && !createMode) {
            setFormDisabled(true);
            deviceView.getEditDeviceButton().setText("✎");
        }
    }

    private void selectPluginByTypeKey(final String typeKey) {
        deviceView.getDeviceTypeBox()
                .getItems()
                .stream()
                .filter(plugin -> plugin.getTypeKey().equals(typeKey))
                .findFirst()
                .ifPresent(plugin -> deviceView.getDeviceTypeBox().getSelectionModel().select(plugin));
    }

    private void selectRoomById(final String roomId) {
        deviceView.getRoomBox()
                .getItems()
                .stream()
                .filter(room -> roomId.equals(room.getId()))
                .findFirst()
                .ifPresent(room -> deviceView.getRoomBox().getSelectionModel().select(room));
    }

    private void clearForm() {
        deviceView.getDeviceNameField().clear();
        deviceView.getDeviceTypeBox().getSelectionModel().clearSelection();
        deviceView.getRoomBox().getSelectionModel().clearSelection();
        deviceView.getDeviceStateValueLabel().setText("No device selected");
    }

    private void setFormDisabled(final boolean disabled) {
        deviceView.getDeviceNameField().setDisable(disabled);
        deviceView.getDeviceTypeBox().setDisable(disabled);
        deviceView.getRoomBox().setDisable(disabled);
    }

    private void enterEditMode() {
        editMode = true;
        deviceView.getDevicesList().setDisable(true);
        setFormDisabled(false);
        navigationLockUpdater.accept(true);
        deviceView.getAddDeviceButton().setDisable(true);
        deviceView.getDeleteDeviceButton().setDisable(true);
        deviceView.getEditDeviceButton().setText("Save");
        deviceView.getDeviceNameField().requestFocus();
        deviceView.getDeviceNameField().positionCaret(deviceView.getDeviceNameField().getText().length());
    }

    private void leaveEditMode() {
        editMode = false;
        deviceView.getDevicesList().setDisable(false);
        setFormDisabled(true);
        navigationLockUpdater.accept(false);
        deviceView.getAddDeviceButton().setDisable(false);
        deviceView.getEditDeviceButton().setText("✎");
    }

    private void enterCreateMode() {
        createMode = true;
        editMode = false;
        deviceView.getDevicesList().setDisable(true);
        deviceView.getDevicesList().getSelectionModel().clearSelection();
        clearForm();
        setFormDisabled(false);
        if (!deviceView.getDeviceTypeBox().getItems().isEmpty()) {
            deviceView.getDeviceTypeBox().getSelectionModel().selectFirst();
        }
        if (!deviceView.getRoomBox().getItems().isEmpty()) {
            deviceView.getRoomBox().getSelectionModel().selectFirst();
        }
        deviceView.getEditDeviceButton().setDisable(true);
        deviceView.getDeleteDeviceButton().setDisable(false);
        deviceView.getDeleteDeviceButton().setText("Cancel");
        deviceView.getAddDeviceButton().setText("Save device");
        deviceView.getDeviceStateValueLabel().setText("Default state will be applied automatically");
        deviceView.getDeviceNameField().requestFocus();
    }

    private void leaveCreateMode() {
        createMode = false;
        deviceView.getDevicesList().setDisable(false);
        setFormDisabled(true);
        deviceView.getAddDeviceButton().setText("Add device");
        deviceView.getDeleteDeviceButton().setText("Delete device");
        handleDeviceSelection();
    }

    private void activateNewDeviceMode() {
        clearForm();
        setFormDisabled(false);
        if (!deviceView.getDeviceTypeBox().getItems().isEmpty()) {
            deviceView.getDeviceTypeBox().getSelectionModel().selectFirst();
        }
        if (!deviceView.getRoomBox().getItems().isEmpty()) {
            deviceView.getRoomBox().getSelectionModel().selectFirst();
        }
        deviceView.getEditDeviceButton().setDisable(true);
        deviceView.getDeleteDeviceButton().setDisable(true);
        deviceView.getEditDeviceButton().setText("✎");
        deviceView.getDeleteDeviceButton().setText("Delete device");
        deviceView.getAddDeviceButton().setText("Add device");
        deviceView.getDeviceStateValueLabel().setText("Default state will be applied automatically");
    }

    /**
     * Reloads the current device data into the view.
     */
    public void refresh() {
        refresh(null);
    }

    /**
     * Reloads the current device data and optionally reselects one entry.
     *
     * @param deviceIdToReselect the device identifier to reselect
     */
    public void refresh(final String deviceIdToReselect) {
        String targetDeviceId = deviceIdToReselect;
        if (targetDeviceId == null) {
            DeviceDefinition selectedDevice = deviceView.getDevicesList().getSelectionModel().getSelectedItem();
            targetDeviceId = selectedDevice == null ? null : selectedDevice.getId();
        }

        if (editMode) {
            leaveEditMode();
        }
        if (createMode) {
            leaveCreateMode();
        }

        loadSelectableValues();
        loadDevices(targetDeviceId);
        handleDeviceSelection();
    }

    /**
     * Returns the root view of the controller.
     *
     * @return the root node
     */
    public Parent getView() {
        return deviceView.getRoot();
    }
}
