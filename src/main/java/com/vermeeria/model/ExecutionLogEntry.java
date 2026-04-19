package com.vermeeria.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single scenario execution log entry.
 *
 * @author Jette
 */
public class ExecutionLogEntry {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final String id;
    private LocalDateTime executedAt;
    private String scenarioId;
    private String scenarioName;
    private String message;

    /**
     * Creates an empty execution log entry with generated metadata.
     */
    public ExecutionLogEntry() {
        this.id = "log-" + UUID.randomUUID();
        this.executedAt = LocalDateTime.now();
    }

    /**
     * Creates a new execution log entry for a scenario action.
     *
     * @param scenarioId   the scenario identifier
     * @param scenarioName the scenario name
     * @param message      the execution message
     */
    public ExecutionLogEntry(final String scenarioId, final String scenarioName, final String message) {
        this();
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.message = message;
    }

    /**
     * Returns the unique log entry identifier.
     *
     * @return the log entry identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the timestamp of the execution.
     *
     * @return the timestamp
     */
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    /**
     * Sets the execution timestamp.
     *
     * @param executedAt the timestamp
     */
    public void setExecutedAt(final LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    /**
     * Returns the scenario identifier.
     *
     * @return the scenario identifier
     */
    public String getScenarioId() {
        return scenarioId;
    }

    /**
     * Sets the scenario identifier.
     *
     * @param scenarioId the scenario identifier
     */
    public void setScenarioId(final String scenarioId) {
        this.scenarioId = scenarioId;
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
     * Returns the log message.
     *
     * @return the log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the log message.
     *
     * @param message the log message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Returns a readable multi-part label for log views.
     *
     * @return the formatted log label
     */
    @Override
    public String toString() {
        String timestamp = executedAt == null ? "" : TIMESTAMP_FORMATTER.format(executedAt);
        String scenarioLabel = scenarioName == null || scenarioName.isBlank() ? "Scenario" : scenarioName;
        return timestamp + " · " + scenarioLabel + " · " + message;
    }
}
