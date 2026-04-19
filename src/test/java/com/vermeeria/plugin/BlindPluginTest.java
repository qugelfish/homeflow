package com.vermeeria.plugin;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link BlindPlugin}.
 *
 * @author Jette
 */
class BlindPluginTest {

    private final BlindPlugin blindPlugin = new BlindPlugin();

    @Test
    void testCreateDefaultState_ReturnsExpectedDefaultValues() {
        Map<String, Object> defaultState = blindPlugin.createDefaultState();

        assertEquals(100, defaultState.get("position"));
    }

    @Test
    void testSupportedActions_ReturnExpectedActionDefinitions() {
        List<ActionSpec> actions = blindPlugin.supportedActions();

        assertEquals(3, actions.size());
        assertEquals(List.of("open", "close", "setPosition"), actions.stream().map(ActionSpec::actionKey).toList());
        assertEquals(ActionParameterKind.NONE, actions.getFirst().parameterKind());
        assertEquals(ActionParameterKind.PERCENTAGE, actions.get(2).parameterKind());
    }

    @Test
    void testApplyAction_OpenSetsPositionToHundred() {
        Map<String, Object> updatedState = blindPlugin.applyAction(Map.of("position", 0), "open", null);

        assertEquals(100, updatedState.get("position"));
    }

    @Test
    void testApplyAction_CloseSetsPositionToZero() {
        Map<String, Object> updatedState = blindPlugin.applyAction(Map.of("position", 100), "close", null);

        assertEquals(0, updatedState.get("position"));
    }

    @Test
    void testApplyAction_SetPositionUsesProvidedPercentage() {
        Map<String, Object> updatedState = blindPlugin.applyAction(blindPlugin.createDefaultState(), "setPosition", "35");

        assertEquals(35, updatedState.get("position"));
    }

    @Test
    void testApplyAction_ThrowsIllegalArgumentException_ForUnsupportedAction() {
        Map<String, Object> defaultState = blindPlugin.createDefaultState();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> blindPlugin.applyAction(defaultState, "stop", null));

        assertEquals("Unsupported blind action: stop", exception.getMessage());
    }

    @Test
    void testFormatState_ReturnsReadableBlindState() {
        String formattedState = blindPlugin.formatState(Map.of("position", 65));

        assertEquals("Position 65%", formattedState);
    }
}
