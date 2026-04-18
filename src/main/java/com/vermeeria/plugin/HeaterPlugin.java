package com.vermeeria.plugin;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Device plugin for smart heaters.
 *
 * @author Jette
 */
public class HeaterPlugin implements DevicePlugin {

    private static final String TYPE_KEY = "heater";
    private static final String DISPLAY_NAME = "Heater";
    private static final String POWER_KEY = "power";
    private static final String TARGET_TEMPERATURE_KEY = "targetTemperature";

    /**
     * Returns the technical type key used for this plugin.
     *
     * @return the type key
     */
    @Override
    public String getTypeKey() {
        return TYPE_KEY;
    }

    /**
     * Returns the user-facing display name of the device type.
     *
     * @return the display name
     */
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * Creates the default heater state for newly added devices.
     *
     * @return the default state map
     */
    @Override
    public Map<String, Object> createDefaultState() {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put(POWER_KEY, Boolean.TRUE);
        state.put(TARGET_TEMPERATURE_KEY, 21);
        return state;
    }

    /**
     * Returns the actions that a heater supports in scenarios.
     *
     * @return the supported actions
     */
    @Override
    public List<ActionSpec> supportedActions() {
        return List.of(
                new ActionSpec("setTargetTemperature", "Set target temperature", ActionParameterKind.TEMPERATURE, "Target temperature"),
                new ActionSpec("turnOff", "Turn off", ActionParameterKind.NONE, ""),
                new ActionSpec("turnOn", "Turn on", ActionParameterKind.NONE, ""));
    }

    /**
     * Applies a heater action and returns the updated state.
     *
     * @param currentState   the current heater state
     * @param actionKey      the action key to execute
     * @param parameterValue the optional action parameter
     * @return the updated state map
     */
    @Override
    public Map<String, Object> applyAction(final Map<String, Object> currentState, final String actionKey, final String parameterValue) {
        Map<String, Object> updatedState = new LinkedHashMap<>(currentState);

        switch (actionKey) {
            case "setTargetTemperature" -> {
                int targetTemperature = Integer.parseInt(parameterValue);
                updatedState.put(TARGET_TEMPERATURE_KEY, targetTemperature);
                updatedState.put(POWER_KEY, Boolean.TRUE);
            }
            case "turnOff" -> updatedState.put(POWER_KEY, Boolean.FALSE);
            case "turnOn" -> updatedState.put(POWER_KEY, Boolean.TRUE);
            case null, default -> throw new IllegalArgumentException("Unsupported heater action: " + actionKey);
        }

        return updatedState;
    }

    /**
     * Formats the heater state for display in the user interface.
     *
     * @param state the current heater state
     * @return the formatted heater state
     */
    @Override
    public String formatState(final Map<String, Object> state) {
        boolean power = Boolean.TRUE.equals(state.get(POWER_KEY));
        Object targetTemperature = state.getOrDefault(TARGET_TEMPERATURE_KEY, 21);
        return power ? targetTemperature + " °C" : "Off";
    }
}
