package com.vermeeria.model;

/**
 * Represents a single device action that belongs to a scenario.
 *
 * @author Jette
 */
public class ScenarioAction {

    private String deviceId;
    private String actionKey;
    private String parameterValue;

    /**
     * Creates an empty scenario action for JSON deserialization.
     */
    public ScenarioAction() {
    }

    /**
     * Creates a scenario action with the required fields.
     *
     * @param deviceId the target device identifier
     * @param actionKey the action key
     * @param parameterValue the action parameter
     */
    public ScenarioAction(final String deviceId, final String actionKey, final String parameterValue) {
        this.deviceId = deviceId;
        this.actionKey = actionKey;
        this.parameterValue = parameterValue;
    }

    /**
     * Returns the target device identifier.
     *
     * @return the target device identifier
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the target device identifier.
     *
     * @param deviceId the target device identifier
     */
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the action key.
     *
     * @return the action key
     */
    public String getActionKey() {
        return actionKey;
    }

    /**
     * Sets the action key.
     *
     * @param actionKey the action key
     */
    public void setActionKey(final String actionKey) {
        this.actionKey = actionKey;
    }

    /**
     * Returns the parameter value.
     *
     * @return the parameter value
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * Sets the parameter value.
     *
     * @param parameterValue the parameter value
     */
    public void setParameterValue(final String parameterValue) {
        this.parameterValue = parameterValue;
    }
}
