package com.vermeeria.ui.controller;

import com.vermeeria.model.AppData;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.Room;
import com.vermeeria.persistence.SmartHomeRepository;
import com.vermeeria.service.DeviceService;
import com.vermeeria.service.SmartHomeService;
import com.vermeeria.ui.view.DeviceView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GUI tests for {@link DeviceController}.
 *
 * @author Jette
 */
@ExtendWith(ApplicationExtension.class)
class DeviceControllerTest {

    private AppData appData;
    private DeviceView deviceView;
    private DeviceController deviceController;
    private AtomicReference<String> statusMessage;
    private AtomicBoolean navigationLocked;

    @Start
    private void start(final Stage stage) {
        appData = new AppData();
        deviceView = new DeviceView();
        statusMessage = new AtomicReference<>();
        navigationLocked = new AtomicBoolean(false);
        deviceController = new DeviceController(deviceView, createDeviceService(appData), statusMessage::set, navigationLocked::set);
        stage.setScene(new Scene(deviceController.getView(), 1100, 700));
        stage.show();
    }

    @Test
    void testRefreshWithoutSelection_ActivatesNewDeviceMode(final FxRobot robot) {
        appData.addRoom(new Room("Living Room"));
        robot.interact((Runnable) deviceController::refresh);

        assertFalse(deviceView.getDeviceNameField().isDisabled());
        assertFalse(deviceView.getDeviceTypeBox().isDisabled());
        assertFalse(deviceView.getRoomBox().isDisabled());
        assertTrue(deviceView.getEditDeviceButton().isDisable());
        assertTrue(deviceView.getDeleteDeviceButton().isDisable());
        assertEquals("Add device", deviceView.getAddDeviceButton().getText());
        assertEquals("Default state will be applied automatically", deviceView.getDeviceStateValueLabel().getText());
        assertFalse(navigationLocked.get());
    }

    @Test
    void testDeviceSelection_LoadsSelectedDeviceIntoTheForm(final FxRobot robot) {
        Room room = new Room("Living Room");
        DeviceDefinition device = new DeviceDefinition("Floor Lamp", "lamp", room.getId());
        device.setCurrentState(java.util.Map.of("power", true, "brightness", 50, "color", "WHITE"));
        appData.addRoom(room);
        appData.addDevice(device);
        robot.interact((Runnable) deviceController::refresh);
        robot.interact((Runnable) () -> deviceView.getDevicesList().getSelectionModel().select(device));

        assertEquals("Floor Lamp", deviceView.getDeviceNameField().getText());
        assertEquals("Lamp", deviceView.getDeviceTypeBox().getValue().getDisplayName());
        assertEquals("Living Room", deviceView.getRoomBox().getValue().getName());
        assertTrue(deviceView.getDeviceStateValueLabel().getText().contains("Brightness: 50%"));
        assertFalse(deviceView.getEditDeviceButton().isDisable());
        assertFalse(deviceView.getDeleteDeviceButton().isDisable());
    }

    @Test
    void testAddDevice_CreatesDeviceAndUpdatesStatusMessage(final FxRobot robot) {
        Room room = new Room("Living Room");
        appData.addRoom(room);
        robot.interact((Runnable) deviceController::refresh);
        robot.interact((Runnable) () -> deviceView.getDeviceNameField().setText("Floor Lamp"));
        robot.interact((Runnable) () -> deviceView.getAddDeviceButton().fire());

        assertEquals(1, appData.getDevices().size());
        assertEquals("Floor Lamp", appData.getDevices().getFirst().getName());
        assertEquals("Device created", statusMessage.get());
    }

    @Test
    void testEnterEditMode_DisablesAddAndDeleteAndLocksNavigation(final FxRobot robot) {
        Room room = new Room("Living Room");
        DeviceDefinition device = new DeviceDefinition("Floor Lamp", "lamp", room.getId());
        appData.addRoom(room);
        appData.addDevice(device);
        robot.interact((Runnable) deviceController::refresh);
        robot.interact((Runnable) () -> deviceView.getDevicesList().getSelectionModel().select(device));
        robot.interact((Runnable) () -> deviceView.getEditDeviceButton().fire());

        assertEquals("Save", deviceView.getEditDeviceButton().getText());
        assertTrue(deviceView.getAddDeviceButton().isDisable());
        assertTrue(deviceView.getDeleteDeviceButton().isDisable());
        assertTrue(deviceView.getDevicesList().isDisable());
        assertFalse(deviceView.getDeviceNameField().isDisabled());
        assertTrue(navigationLocked.get());
    }

    private DeviceService createDeviceService(final AppData appData) {
        return new DeviceService(new SmartHomeService(new InMemorySmartHomeRepository(appData)));
    }

    /**
     * In-memory repository for isolated controller tests.
     */
    private record InMemorySmartHomeRepository(AppData appData) implements SmartHomeRepository {

        @Override
        public AppData load() {
            return appData;
        }

        @Override
        public void save(final AppData appData) {
            // No-op for in-memory testing
        }
    }
}
