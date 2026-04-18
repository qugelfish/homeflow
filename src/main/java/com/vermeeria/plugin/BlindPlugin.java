package com.vermeeria.plugin;

import com.vermeeria.model.ActionParameterKind;
import com.vermeeria.model.ActionSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Device plugin for smart blinds.
 *
 * @author Jette
 */
public class BlindPlugin implements DevicePlugin {

    private static final String TYPE_KEY = "blind";
    private static final String DISPLAY_NAME = "Blind";
    private static final String POSITION_KEY = "position";

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
     * Creates the default blind state for newly added devices.
     *
     * @return the default state map
     */
    @Override
    public Map<String, Object> createDefaultState() {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put(POSITION_KEY, 100);
        return state;
    }

    /**
     * Returns the actions that a blind supports in scenarios.
     *
     * @return the supported actions
     */
    @Override
    public List<ActionSpec> supportedActions() {
        return List.of(
                new ActionSpec("open", "Open", ActionParameterKind.NONE, ""),
                new ActionSpec("close", "Close", ActionParameterKind.NONE, ""),
                new ActionSpec("setPosition", "Set position", ActionParameterKind.PERCENTAGE, "Position")
        );
    }

    /**
     * Applies a blind action and returns the updated state.
     *
     * @param currentState   the current blind state
     * @param actionKey      the action key to execute
     * @param parameterValue the optional action parameter
     * @return the updated state map
     */
    @Override
    public Map<String, Object> applyAction(final Map<String, Object> currentState,
                                           final String actionKey,
                                           final String parameterValue) {
        Map<String, Object> updatedState = new LinkedHashMap<>(currentState);

        switch (actionKey) {
            case "open" -> updatedState.put(POSITION_KEY, 100);
            case "close" -> updatedState.put(POSITION_KEY, 0);
            case "setPosition" -> updatedState.put(POSITION_KEY, Integer.parseInt(parameterValue));
            case null, default -> throw new IllegalArgumentException("Unsupported blind action: " + actionKey);
        }

        return updatedState;
    }

    /**
     * Formats the blind state for display in the user interface.
     *
     * @param state the current blind state
     * @return the formatted blind state
     */
    @Override
    public String formatState(final Map<String, Object> state) {
        Object position = state.getOrDefault(POSITION_KEY, 100);
        return "Position " + position + "%";
    }
}
