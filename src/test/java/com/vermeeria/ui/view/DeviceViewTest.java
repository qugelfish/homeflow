package com.vermeeria.ui.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GUI tests for {@link DeviceView}.
 *
 * @author Jette
 */
@ExtendWith(ApplicationExtension.class)
class DeviceViewTest {

    private DeviceView deviceView;

    @Start
    private void start(final Stage stage) {
        deviceView = new DeviceView();
        stage.setScene(new Scene(deviceView.getRoot(), 1100, 700));
        stage.show();
    }

    @Test
    void testInitialState_ContainsExpectedControlsAndDefaultValues(final FxRobot robot) {
        assertNotNull(deviceView.getRoot());
        assertNotNull(robot.lookup(".list-view").query());
        assertNotNull(robot.lookup(".text-field").query());
        assertNotNull(deviceView.getDeviceTypeBox());
        assertNotNull(deviceView.getRoomBox());
        assertNotNull(deviceView.getDeviceStateValueLabel());

        assertTrue(deviceView.getDeviceNameField().isDisabled());
        assertTrue(deviceView.getDeviceTypeBox().isDisabled());
        assertTrue(deviceView.getRoomBox().isDisabled());

        assertEquals("Add device", deviceView.getAddDeviceButton().getText());
        assertEquals("✎", deviceView.getEditDeviceButton().getText());
        assertEquals("Delete device", deviceView.getDeleteDeviceButton().getText());
        assertEquals("No device selected", deviceView.getDeviceStateValueLabel().getText());

        assertFalse(deviceView.getAddDeviceButton().getStyleClass().isEmpty());
        assertFalse(deviceView.getDeleteDeviceButton().getStyleClass().isEmpty());
    }
}
