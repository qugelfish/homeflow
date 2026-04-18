package com.vermeeria.plugin;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Device plugin for smart lamps.
 *
 * @author Jette
 */
public class LampPlugin implements DevicePlugin {

    private static final String TYPE_KEY = "lamp";
    private static final String DISPLAY_NAME = "Lamp";
    private static final String POWER_KEY = "power";
    private static final String BRIGHTNESS_KEY = "brightness";
    private static final String COLOR_KEY = "color";
    private static final List<String> AVAILABLE_COLORS = List.of("WHITE", "WARM_WHITE", "BLUE", "RED", "GREEN");

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
     * Creates the default lamp state for newly added devices.
     *
     * @return the default state map
     */
    @Override
    public Map<String, Object> createDefaultState() {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put(POWER_KEY, Boolean.TRUE);
        state.put(BRIGHTNESS_KEY, 50);
        state.put(COLOR_KEY, "WHITE");
        return state;
    }

    /**
     * Returns the actions that a lamp supports in scenarios.
     *
     * @return the supported actions
     */
    @Override
    public List<ActionSpec> supportedActions() {
        return List.of(
                new ActionSpec("setBrightness", "Set brightness", ActionParameterKind.PERCENTAGE, "Set brightness"),
                new ActionSpec("setColor", "Set color", ActionParameterKind.SELECTION, "Color", AVAILABLE_COLORS),
                new ActionSpec("turnOff", "Turn off", ActionParameterKind.NONE, ""),
                new ActionSpec("turnOn", "Turn on", ActionParameterKind.NONE, "")
        );
    }

    /**
     * Applies a lamp action and returns the updated state.
     *
     * @param currentState   the current lamp state
     * @param actionKey      the action key to execute
     * @param parameterValue the optional action parameter
     * @return the updated state map
     */
    @Override
    public Map<String, Object> applyAction(Map<String, Object> currentState, String actionKey, String parameterValue) {
        Map<String, Object> updatedState = new LinkedHashMap<>(currentState);

        switch (actionKey) {
            case "setBrightness" -> {
                int brightness = Integer.parseInt(parameterValue);
                if (brightness < 0 || brightness > 100) {
                    throw new IllegalArgumentException("Lamp brightness must be between 0 and 100.");
                }
                updatedState.put(BRIGHTNESS_KEY, brightness);
                updatedState.put(POWER_KEY, brightness > 0);
            }
            case "setColor" -> {
                if (parameterValue == null || parameterValue.isBlank()) {
                    throw new IllegalArgumentException("Lamp color cannot be empty.");
                }

                String normalizedColor = parameterValue.trim().toUpperCase();
                if (!AVAILABLE_COLORS.contains(normalizedColor)) {
                    throw new IllegalArgumentException("Unsupported lamp color: " + parameterValue);
                }

                updatedState.put(COLOR_KEY, normalizedColor);
                updatedState.put(POWER_KEY, Boolean.TRUE);
            }
            case "turnOff" -> updatedState.put(POWER_KEY, Boolean.FALSE);
            case "turnOn" -> {
                updatedState.put(POWER_KEY, Boolean.TRUE);
                if (Integer.valueOf(0).equals(updatedState.get(BRIGHTNESS_KEY))) {
                    updatedState.put(BRIGHTNESS_KEY, 50);
                }
            }
            case null, default -> throw new IllegalArgumentException("Unsupported lamp action: " + actionKey);
        }

        return updatedState;
    }

    /**
     * Formats the lamp state for display in the user interface.
     *
     * @param state the current lamp state
     * @return the formatted lamp state
     */
    @Override
    public String formatState(Map<String, Object> state) {
        boolean power = Boolean.TRUE.equals(state.get(POWER_KEY));
        Object brightness = state.getOrDefault(BRIGHTNESS_KEY, 50);
        Object color = state.getOrDefault(COLOR_KEY, "WHITE");
        return power ? color + ", " + brightness + " %" : "Off";
    }
}
