package com.vermeeria.plugin;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link HeaterPlugin}.
 *
 * @author Jette
 */
class HeaterPluginTest {

    private final HeaterPlugin heaterPlugin = new HeaterPlugin();

    @Test
    void testCreateDefaultState_ReturnsExpectedDefaultValues() {
        Map<String, Object> defaultState = heaterPlugin.createDefaultState();

        assertEquals(true, defaultState.get("power"));
        assertEquals(21, defaultState.get("targetTemperature"));
    }

    @Test
    void testSupportedActions_ReturnExpectedActionDefinitions() {
        List<ActionSpec> actions = heaterPlugin.supportedActions();

        assertEquals(3, actions.size());
        assertEquals(List.of("setTargetTemperature", "turnOff", "turnOn"), actions.stream()
                .map(ActionSpec::actionKey)
                .toList());
        assertEquals(ActionParameterKind.TEMPERATURE, actions.getFirst().parameterKind());
    }

    @Test
    void testApplyAction_SetTargetTemperatureUpdatesTemperatureAndKeepsHeaterOn() {
        Map<String, Object> updatedState = heaterPlugin.applyAction(heaterPlugin.createDefaultState(), "setTargetTemperature", "24");

        assertEquals(24, updatedState.get("targetTemperature"));
        assertEquals(true, updatedState.get("power"));
    }

    @Test
    void testApplyAction_TurnOffTurnsHeaterOff() {
        Map<String, Object> updatedState = heaterPlugin.applyAction(heaterPlugin.createDefaultState(), "turnOff", null);

        assertEquals(false, updatedState.get("power"));
    }

    @Test
    void testApplyAction_TurnOnTurnsHeaterOn() {
        Map<String, Object> updatedState = heaterPlugin.applyAction(Map.of("power", false, "targetTemperature", 21), "turnOn", null);

        assertEquals(true, updatedState.get("power"));
    }

    @Test
    void testApplyAction_ThrowsIllegalArgumentException_ForUnsupportedAction() {
        Map<String, Object> defaultState = heaterPlugin.createDefaultState();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> heaterPlugin.applyAction(defaultState, "boost", null));

        assertEquals("Unsupported heater action: boost", exception.getMessage());
    }

    @Test
    void testFormatState_ReturnsReadableHeaterState() {
        String formattedState = heaterPlugin.formatState(Map.of("power", true, "targetTemperature", 23));

        assertEquals("23 °C", formattedState);
    }

    @Test
    void testFormatState_ReturnsOffWhenHeaterIsPoweredOff() {
        String formattedState = heaterPlugin.formatState(Map.of("power", false, "targetTemperature", 23));

        assertEquals("Off", formattedState);
    }
}
