package com.vermeeria.plugin;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link LampPlugin}.
 *
 * @author Jette
 */
class LampPluginTest {

    private final LampPlugin lampPlugin = new LampPlugin();

    @Test
    void testCreateDefaultState_ReturnsExpectedDefaultValues() {
        Map<String, Object> defaultState = lampPlugin.createDefaultState();

        assertEquals(true, defaultState.get("power"));
        assertEquals(50, defaultState.get("brightness"));
        assertEquals("WHITE", defaultState.get("color"));
    }

    @Test
    void testSupportedActions_ReturnExpectedActionDefinitions() {
        List<ActionSpec> actions = lampPlugin.supportedActions();

        assertEquals(4, actions.size());
        assertEquals(List.of("setBrightness", "setColor", "turnOff", "turnOn"), actions.stream()
                .map(ActionSpec::actionKey)
                .toList());
        assertEquals(ActionParameterKind.PERCENTAGE, actions.getFirst().parameterKind());
        assertEquals(ActionParameterKind.SELECTION, actions.get(1).parameterKind());
    }

    @Test
    void testApplyAction_SetBrightnessUpdatesBrightnessAndKeepsLampOn() {
        Map<String, Object> updatedState = lampPlugin.applyAction(lampPlugin.createDefaultState(), "setBrightness", "25");

        assertEquals(25, updatedState.get("brightness"));
        assertEquals(true, updatedState.get("power"));
    }

    @Test
    void testApplyAction_SetBrightnessToZeroTurnsLampOff() {
        Map<String, Object> updatedState = lampPlugin.applyAction(lampPlugin.createDefaultState(), "setBrightness", "0");

        assertEquals(0, updatedState.get("brightness"));
        assertEquals(false, updatedState.get("power"));
    }

    @Test
    void testApplyAction_SetColorUpdatesColorAndTurnsLampOn() {
        Map<String, Object> currentState = new LinkedHashMap<>(lampPlugin.createDefaultState());
        currentState.put("power", false);

        Map<String, Object> updatedState = lampPlugin.applyAction(currentState, "setColor", "Blue");

        assertEquals("Blue", updatedState.get("color"));
        assertEquals(true, updatedState.get("power"));
    }

    @Test
    void testApplyAction_TurnOffTurnsLampOff() {
        Map<String, Object> updatedState = lampPlugin.applyAction(lampPlugin.createDefaultState(), "turnOff", null);

        assertEquals(false, updatedState.get("power"));
    }

    @Test
    void testApplyAction_TurnOnRestoresDefaultBrightnessWhenCurrentBrightnessIsZero() {
        Map<String, Object> currentState = new LinkedHashMap<>(lampPlugin.createDefaultState());
        currentState.put("brightness", 0);
        currentState.put("power", false);

        Map<String, Object> updatedState = lampPlugin.applyAction(currentState, "turnOn", null);

        assertEquals(true, updatedState.get("power"));
        assertEquals(50, updatedState.get("brightness"));
    }

    @Test
    void testApplyAction_ThrowsIllegalArgumentException_WhenBrightnessIsOutOfRange() {
        Map<String, Object> defaultState = lampPlugin.createDefaultState();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> lampPlugin.applyAction(defaultState, "setBrightness", "200"));

        assertEquals("Lamp brightness must be between 0 and 100.", exception.getMessage());
    }

    @Test
    void testApplyAction_ThrowsIllegalArgumentException_WhenColorIsUnsupported() {
        Map<String, Object> defaultState = lampPlugin.createDefaultState();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> lampPlugin.applyAction(defaultState, "setColor", "Purple"));

        assertEquals("Unsupported lamp color: Purple", exception.getMessage());
    }

    @Test
    void testFormatState_ReturnsReadableLampState() {
        String formattedState = lampPlugin.formatState(Map.of("power", true, "brightness", 50, "color", "Warm White"));

        assertEquals("Warm White, 50 %", formattedState);
    }

    @Test
    void testFormatState_ReturnsOffWhenLampIsPoweredOff() {
        String formattedState = lampPlugin.formatState(Map.of("power", false, "brightness", 50, "color", "Warm White"));

        assertEquals("Off", formattedState);
    }
}
