package com.vermeeria.plugin;

import com.vermeeria.model.ActionSpec;

import java.util.List;
import java.util.Map;

/**
 * Defines the common behavior of all device plugins.
 * A device plugin describes a device type, its default state,
 * supported actions and how actions affect the device state.
 *
 * @author Jette
 */
public interface DevicePlugin {

    /**
     * Returns the internal type key of the device.
     *
     * @return the type key
     */
    String getTypeKey();

    /**
     * Returns the display name of the device type.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Creates the default state for a new device of this type.
     *
     * @return the default state
     */
    Map<String, Object> createDefaultState();

    /**
     * Returns the list of actions supported by this device type.
     *
     * @return the supported actions
     */
    List<ActionSpec> supportedActions();

    /**
     * Applies the given action to the current device state.
     *
     * @param currentState the current state of the device
     * @param actionKey the key of the action to execute
     * @param parameterValue the parameter value of the action
     * @return the updated device state
     */
    Map<String, Object> applyAction(Map<String, Object> currentState, String actionKey, String parameterValue);

    /**
     * Formats the device state as a readable string for the user interface.
     *
     * @param state the current device state
     * @return the formatted state string
     */
    String formatState(Map<String, Object> state);
}
