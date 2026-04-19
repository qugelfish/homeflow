package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.ExecutionLogEntry;
import com.vermeeria.model.Room;
import com.vermeeria.model.Scenario;
import com.vermeeria.model.ScenarioAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ScenarioService}
 *
 * @author Jette
 */
class ScenarioServiceTest {

    private static final String LAMP_TYPE = "lamp";

    @Test
    void testCreateScenario_TrimsNameAndDescription() {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        scenarioService.createScenario("  Evening Routine  ", "  Turns devices on  ", List.of());

        assertEquals(1, appData.getScenarios().size());
        Scenario createdScenario = appData.getScenarios().getFirst();
        assertEquals("Evening Routine", createdScenario.getName());
        assertEquals("Turns devices on", createdScenario.getDescription());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testCreateScenario_ThrowsValidationException_WhenScenarioNameIsBlank(final String scenarioName) {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        List<ScenarioAction> noActions = List.of();

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.createScenario(scenarioName, "Description", noActions));

        assertEquals("Scenario name cannot be empty.", exception.getMessage());
    }

    @Test
    void testCreateScenario_ThrowsValidationException_WhenScenarioNameAlreadyExists() {
        AppData appData = new AppData();
        appData.addScenario(new Scenario("Evening Routine", "Existing"));
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        List<ScenarioAction> noActions = List.of();

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.createScenario("evening routine", "Duplicate", noActions));

        assertEquals("A scenario with this name already exists.", exception.getMessage());
    }

    @Test
    void testCreateScenario_CopiesActions() {
        AppData appData = new AppData();
        ScenarioAction action = new ScenarioAction("device-1", "turnOn", null);
        List<ScenarioAction> actions = List.of(action);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        scenarioService.createScenario("Evening Routine", "Description", actions);

        Scenario storedScenario = appData.getScenarios().getFirst();
        ScenarioAction storedAction = storedScenario.getActions().getFirst();

        assertEquals(1, storedScenario.getActions().size());
        assertEquals("device-1", storedAction.getDeviceId());
        assertEquals("turnOn", storedAction.getActionKey());
        assertNull(storedAction.getParameterValue());
    }

    @Test
    void testUpdateScenario_ChangesTheSelectedScenario() {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Turns devices on");
        appData.addScenario(scenario);
        ScenarioAction updatedAction = new ScenarioAction("device-1", "turnOff", null);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        scenarioService.updateScenario(scenario.getId(), "Night Routine", "Turns devices off", List.of(updatedAction));

        assertEquals("Night Routine", scenario.getName());
        assertEquals("Turns devices off", scenario.getDescription());
        assertEquals(1, scenario.getActions().size());
        assertEquals("turnOff", scenario.getActions().getFirst().getActionKey());
    }

    @Test
    void testUpdateScenario_KeepsActions_WhenActionsAreNull() {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Turns devices on");
        scenario.setActions(List.of(new ScenarioAction("device-1", "turnOn", null)));
        appData.addScenario(scenario);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        scenarioService.updateScenario(scenario.getId(), "Updated Routine", "Updated description", null);

        assertEquals("Updated Routine", scenario.getName());
        assertEquals("Updated description", scenario.getDescription());
        assertEquals(1, scenario.getActions().size());
        assertEquals("turnOn", scenario.getActions().getFirst().getActionKey());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateScenario_ThrowsValidationException_WhenScenarioIdIsBlank(final String scenarioId) {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        List<ScenarioAction> noActions = List.of();

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.updateScenario(scenarioId, "Night Routine", "Description", noActions));

        assertEquals("Please select a scenario to edit.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateScenario_ThrowsValidationException_WhenScenarioNameIsBlank(final String scenarioName) {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Turns devices on");
        String scenarioId = scenario.getId();
        appData.addScenario(scenario);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        List<ScenarioAction> noActions = List.of();

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.updateScenario(scenarioId, scenarioName, "Description", noActions));

        assertEquals("Scenario name cannot be empty.", exception.getMessage());
    }

    @Test
    void testUpdateScenario_ThrowsValidationException_WhenScenarioDoesNotExist() {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        List<ScenarioAction> noActions = List.of();

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.updateScenario("scenario-missing", "Night Routine", "Description", noActions));

        assertEquals("The selected scenario no longer exists.", exception.getMessage());
    }

    @Test
    void testUpdateScenario_ThrowsValidationException_WhenAnotherScenarioAlreadyUsesTheName() {
        AppData appData = new AppData();
        Scenario firstScenario = new Scenario("Evening Routine", "First");
        Scenario secondScenario = new Scenario("Night Routine", "Second");
        String firstScenarioId = firstScenario.getId();
        appData.addScenario(firstScenario);
        appData.addScenario(secondScenario);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        List<ScenarioAction> noActions = List.of();

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.updateScenario(firstScenarioId, "night routine", "Description", noActions));

        assertEquals("A scenario with this name already exists.", exception.getMessage());
    }

    @Test
    void testUpdateScenarioActions_ReplacesActionsSuccessfully() {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Description");
        scenario.setActions(List.of(new ScenarioAction("device-1", "turnOn", null)));
        appData.addScenario(scenario);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        scenarioService.updateScenarioActions(scenario.getId(), List.of(new ScenarioAction("device-2", "turnOff", null)));

        assertEquals(1, scenario.getActions().size());
        assertEquals("device-2", scenario.getActions().getFirst().getDeviceId());
        assertEquals("turnOff", scenario.getActions().getFirst().getActionKey());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateScenarioActions_ThrowsValidationException_WhenScenarioIdIsBlank(final String scenarioId) {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.updateScenarioActions(scenarioId, List.of()));

        assertEquals("Please select a scenario first.", exception.getMessage());
    }

    @Test
    void testUpdateScenarioActions_ThrowsValidationException_WhenScenarioDoesNotExist() {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.updateScenarioActions("scenario-missing", List.of()));

        assertEquals("The selected scenario no longer exists.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testDeleteScenario_ThrowsValidationException_WhenScenarioIdIsInvalid(final String scenarioId) {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.deleteScenario(scenarioId));

        assertEquals("Please select a scenario to delete.", exception.getMessage());
    }

    @Test
    void testDeleteScenario_ThrowsValidationException_WhenScenarioNotFound() {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.deleteScenario("scenario-no-longer-exists"));

        assertEquals("The selected scenario no longer exists.", exception.getMessage());
    }

    @Test
    void testDeleteScenario_DeletesScenarioSuccessfully() {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Description");
        appData.addScenario(scenario);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        scenarioService.deleteScenario(scenario.getId());

        assertEquals(0, appData.getScenarios().size());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testExecuteScenario_ThrowsValidationException_WhenScenarioIdIsBlank(final String scenarioId) {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.executeScenario(scenarioId, deviceService));

        assertEquals("Please select a scenario to run.", exception.getMessage());
    }

    @Test
    void testExecuteScenario_ThrowsValidationException_WhenScenarioDoesNotExist() {
        AppData appData = new AppData();
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.executeScenario("scenario-missing", deviceService));

        assertEquals("The selected scenario no longer exists.", exception.getMessage());
    }

    @Test
    void testExecuteScenario_ThrowsValidationException_WhenScenarioHasNoActions() {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Description");
        String scenarioId = scenario.getId();
        appData.addScenario(scenario);
        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> scenarioService.executeScenario(scenarioId, deviceService));

        assertEquals("The selected scenario has no actions.", exception.getMessage());
    }

    @Test
    void testExecuteScenario_ExecutesActionsAndCreatesLogs() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);

        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);
        deviceService.createDevice("Floor Lamp", LAMP_TYPE, room.getId());

        DeviceDefinition device = appData.getDevices().getFirst();
        Scenario scenario = new Scenario("Evening Routine", "Description");
        scenario.setActions(List.of(new ScenarioAction(device.getId(), "setBrightness", "25")));
        appData.addScenario(scenario);

        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);

        List<ExecutionLogEntry> createdLogs = scenarioService.executeScenario(scenario.getId(), deviceService);

        assertEquals(1, createdLogs.size());
        assertEquals(1, appData.getLogs().size());
        assertNotNull(createdLogs.getFirst().getExecutedAt());
        assertEquals(25, device.getCurrentState().get("brightness"));
        assertEquals(true, device.getCurrentState().get("power"));
        assertEquals("Evening Routine", createdLogs.getFirst().getScenarioName());
        assertEquals("Executed Living Room - Floor Lamp - Set brightness: 25%", createdLogs.getFirst().getMessage());
    }

    @Test
    void testExecuteScenario_CreatesFailureLog_WhenActionCannotBeExecuted() {
        AppData appData = new AppData();
        Scenario scenario = new Scenario("Evening Routine", "Description");
        scenario.setActions(List.of(new ScenarioAction("device-missing", "setBrightness", "25")));
        appData.addScenario(scenario);

        ScenarioService scenarioService = ServiceTestHelper.createScenarioService(appData);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        List<ExecutionLogEntry> createdLogs = scenarioService.executeScenario(scenario.getId(), deviceService);

        assertEquals(1, createdLogs.size());
        assertEquals(1, appData.getLogs().size());
        assertEquals("Failed to execute action: The target device of this action no longer exists.", createdLogs.getFirst()
                .getMessage());
    }
}
