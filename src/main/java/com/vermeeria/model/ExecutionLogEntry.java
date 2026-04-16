package com.vermeeria.model;

import java.time.LocalDateTime;

/**
 * Represents one execution log entry shown in the application.
 *
 * @author Jette
 */
public class ExecutionLogEntry {

    private LocalDateTime timestamp;
    private String scenarioName;
    private String deviceName;
    private String actionDescription;
    private String result;

    /**
     * Creates an empty log entry for JSON deserialization.
     */
    public ExecutionLogEntry() {
    }

    /**
     * Creates a log entry with the provided values.
     *
     * @param timestamp the execution timestamp
     * @param scenarioName the scenario name
     * @param deviceName the affected device name
     * @param actionDescription the action description
     * @param result the execution result
     */
    public ExecutionLogEntry(final LocalDateTime timestamp,
                             final String scenarioName,
                             final String deviceName,
                             final String actionDescription,
                             final String result) {
        this.timestamp = timestamp;
        this.scenarioName = scenarioName;
        this.deviceName = deviceName;
        this.actionDescription = actionDescription;
        this.result = result;
    }

    /**
     * Returns the execution timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the execution timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the scenario name.
     *
     * @return the scenario name
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Sets the scenario name.
     *
     * @param scenarioName the scenario name
     */
    public void setScenarioName(final String scenarioName) {
        this.scenarioName = scenarioName;
    }

    /**
     * Returns the device name.
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the device name.
     *
     * @param deviceName the device name
     */
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Returns the action description.
     *
     * @return the action description
     */
    public String getActionDescription() {
        return actionDescription;
    }

    /**
     * Sets the action description.
     *
     * @param actionDescription the action description
     */
    public void setActionDescription(final String actionDescription) {
        this.actionDescription = actionDescription;
    }

    /**
     * Returns the execution result.
     *
     * @return the execution result
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the execution result.
     *
     * @param result the execution result
     */
    public void setResult(final String result) {
        this.result = result;
    }
}
