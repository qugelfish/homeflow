package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link AppData}.
 *
 * @author Jette
 */
class AppDataModelTest extends AbstractModelTestHelper {

    @Test
    void testSetRoomsDefensivelyCopiesList() throws Exception {
        assertListSetterDefensivelyCopies(AppData.class, "rooms", List.of(new Room("Living Room")), new Room("Bedroom"));
    }

    @Test
    void testSetDevicesDefensivelyCopiesList() throws Exception {
        assertListSetterDefensivelyCopies(AppData.class, "devices", List.of(new DeviceDefinition("Floor Lamp", "lamp", "room-1")), new DeviceDefinition("Desk Lamp", "lamp", "room-2"));
    }

    @Test
    void testSetScenariosDefensivelyCopiesList() throws Exception {
        assertListSetterDefensivelyCopies(AppData.class, "scenarios", List.of(new Scenario("Evening Routine", "Description")), new Scenario("Night Routine", "Description"));
    }

    @Test
    void testSetLogsDefensivelyCopiesList() throws Exception {
        assertListSetterDefensivelyCopies(AppData.class, "logs", List.of(new ExecutionLogEntry("scenario-1", "Evening Routine", "Executed action")), new ExecutionLogEntry("scenario-2", "Night Routine", "Executed action"));
    }

    @Test
    void testDefaultConstructorInitializesAllCollections() {
        AppData appData = new AppData();

        assertNotNull(appData.getRooms());
        assertNotNull(appData.getDevices());
        assertNotNull(appData.getScenarios());
        assertNotNull(appData.getLogs());
        assertEquals("#DA70D6", appData.getAccentColorHex());
    }

    @Test
    void testSetAccentColorHex_UsesDefaultColor_WhenInputIsBlank() {
        AppData appData = new AppData();

        appData.setAccentColorHex("  ");

        assertEquals("#DA70D6", appData.getAccentColorHex());
    }
}
