package com.vermeeria.model;

import java.util.UUID;

/**
 * Represents a single device action inside a scenario.
 *
 * @author Jette
 */
public class ScenarioAction {

    private final String id;
    private String deviceId;
    private String actionKey;
    private String parameterValue;

    /**
     * Creates an empty scenario action with generated metadata.
     */
    public ScenarioAction() {
        this.id = "scenario-action-" + UUID.randomUUID();
    }

    /**
     * Creates a scenario action for the given device and action key.
     *
     * @param deviceId       the target device identifier
     * @param actionKey      the action key to execute
     * @param parameterValue the optional parameter value
     */
    public ScenarioAction(final String deviceId, final String actionKey, final String parameterValue) {
        this();
        this.deviceId = deviceId;
        this.actionKey = actionKey;
        this.parameterValue = parameterValue;
    }

    /**
     * Returns the unique action identifier.
     *
     * @return the action identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the target device identifier.
     *
     * @return the device identifier
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the target device identifier.
     *
     * @param deviceId the device identifier
     */
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the action key to execute.
     *
     * @return the action key
     */
    public String getActionKey() {
        return actionKey;
    }

    /**
     * Sets the action key to execute.
     *
     * @param actionKey the action key
     */
    public void setActionKey(final String actionKey) {
        this.actionKey = actionKey;
    }

    /**
     * Returns the optional parameter value of the action.
     *
     * @return the parameter value
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * Sets the optional parameter value of the action.
     *
     * @param parameterValue the parameter value
     */
    public void setParameterValue(final String parameterValue) {
        this.parameterValue = parameterValue;
    }

    /**
     * Returns a compact label for UI lists.
     *
     * @return the action label
     */
    @Override
    public String toString() {
        if (parameterValue == null || parameterValue.isBlank()) {
            return actionKey;
        }
        return actionKey + " (" + parameterValue + ")";
    }
}
