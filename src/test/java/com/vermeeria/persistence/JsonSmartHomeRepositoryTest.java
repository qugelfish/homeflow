package com.vermeeria.persistence;

import com.vermeeria.model.AppData;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.ExecutionLogEntry;
import com.vermeeria.model.Room;
import com.vermeeria.model.Scenario;
import com.vermeeria.model.ScenarioAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link JsonSmartHomeRepository}.
 *
 * @author Jette
 */
class JsonSmartHomeRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoad_ReturnsEmptyAppData_WhenFileDoesNotExist() {
        Path jsonFile = tempDir.resolve("missing.json");
        JsonSmartHomeRepository repository = new JsonSmartHomeRepository(jsonFile);

        AppData loadedData = repository.load();

        assertTrue(loadedData.getRooms().isEmpty());
        assertTrue(loadedData.getDevices().isEmpty());
        assertTrue(loadedData.getScenarios().isEmpty());
        assertTrue(loadedData.getLogs().isEmpty());
    }

    @Test
    void testLoad_ReturnsEmptyAppData_WhenFileIsBlank() throws Exception {
        Path jsonFile = tempDir.resolve("blank.json");
        Files.writeString(jsonFile, "   ");
        JsonSmartHomeRepository repository = new JsonSmartHomeRepository(jsonFile);

        AppData loadedData = repository.load();

        assertTrue(loadedData.getRooms().isEmpty());
        assertTrue(loadedData.getDevices().isEmpty());
        assertTrue(loadedData.getScenarios().isEmpty());
        assertTrue(loadedData.getLogs().isEmpty());
    }

    @Test
    void testSave_CreatesMissingParentDirectories() {
        Path jsonFile = tempDir.resolve("nested/path/smarthome.json");
        JsonSmartHomeRepository repository = new JsonSmartHomeRepository(jsonFile);

        repository.save(new AppData());

        assertTrue(Files.exists(jsonFile));
    }

    @Test
    void testSaveAndLoad_RoundTripAppDataSuccessfully() {
        Path jsonFile = tempDir.resolve("smarthome.json");
        JsonSmartHomeRepository repository = new JsonSmartHomeRepository(jsonFile);
        AppData appData = createSampleAppData();

        repository.save(appData);
        AppData loadedData = repository.load();

        assertEquals(1, loadedData.getRooms().size());
        assertEquals("Living Room", loadedData.getRooms().getFirst().getName());

        assertEquals(1, loadedData.getDevices().size());
        DeviceDefinition loadedDevice = loadedData.getDevices().getFirst();
        assertEquals("Floor Lamp", loadedDevice.getName());
        assertEquals("lamp", loadedDevice.getTypeKey());
        assertEquals("WARM_WHITE", loadedDevice.getCurrentState().get("color"));
        assertEquals(50, loadedDevice.getCurrentState().get("brightness"));

        assertEquals(1, loadedData.getScenarios().size());
        Scenario loadedScenario = loadedData.getScenarios().getFirst();
        assertEquals("Evening Routine", loadedScenario.getName());
        assertEquals("Turns devices on", loadedScenario.getDescription());
        assertEquals(1, loadedScenario.getActions().size());
        assertEquals("setBrightness", loadedScenario.getActions().getFirst().getActionKey());

        assertEquals(1, loadedData.getLogs().size());
        ExecutionLogEntry loadedLog = loadedData.getLogs().getFirst();
        assertEquals("Evening Routine", loadedLog.getScenarioName());
        assertEquals("Executed Living Room - Floor Lamp - Set brightness: 50%", loadedLog.getMessage());
        assertEquals(LocalDateTime.of(2026, 4, 19, 18, 0), loadedLog.getExecutedAt());
        assertEquals("#000000", loadedData.getAccentColorHex());
    }

    @Test
    void testSave_WritesPrettyPrintedJson() throws Exception {
        Path jsonFile = tempDir.resolve("pretty.json");
        JsonSmartHomeRepository repository = new JsonSmartHomeRepository(jsonFile);

        repository.save(createSampleAppData());

        String jsonContent = Files.readString(jsonFile);

        assertTrue(jsonContent.contains(System.lineSeparator()));
        assertTrue(jsonContent.contains("\"rooms\""));
        assertTrue(jsonContent.contains("\"devices\""));
        assertFalse(jsonContent.isBlank());
        assertTrue(jsonContent.contains("\"accentColorHex\""));
    }

    @Test
    void testLoad_ThrowsRuntimeException_WhenJsonIsInvalid() throws Exception {
        Path jsonFile = tempDir.resolve("invalid.json");
        Files.writeString(jsonFile, "{ invalid json");
        JsonSmartHomeRepository repository = new JsonSmartHomeRepository(jsonFile);

        RuntimeException exception = assertThrows(RuntimeException.class, repository::load);

        assertEquals("Could not load app data.", exception.getMessage());
    }

    private AppData createSampleAppData() {
        Room room = new Room("Living Room");
        DeviceDefinition device = new DeviceDefinition("Floor Lamp", "lamp", room.getId());
        device.setCurrentState(new LinkedHashMap<>(java.util.Map.of("power", true, "brightness", 50, "color", "WARM_WHITE")));

        ScenarioAction action = new ScenarioAction(device.getId(), "setBrightness", "50");
        Scenario scenario = new Scenario("Evening Routine", "Turns devices on");
        scenario.setActions(List.of(action));

        ExecutionLogEntry logEntry = new ExecutionLogEntry(scenario.getId(), scenario.getName(), "Executed Living Room - Floor Lamp - Set brightness: 50%");
        logEntry.setExecutedAt(LocalDateTime.of(2026, 4, 19, 18, 0));

        AppData appData = new AppData();
        appData.addRoom(room);
        appData.addDevice(device);
        appData.addScenario(scenario);
        appData.addLogEntry(logEntry);
        appData.setAccentColorHex("#000000");
        return appData;
    }
}
