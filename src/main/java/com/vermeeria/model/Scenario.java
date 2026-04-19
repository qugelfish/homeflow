package com.vermeeria.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a scenario that bundles multiple device actions.
 *
 * @author Jette
 */
public class Scenario {

    private final String id;
    private String name;
    private String description;
    private List<ScenarioAction> actions;
    private LocalDateTime createdAt;

    /**
     * Creates an empty scenario with generated metadata.
     */
    public Scenario() {
        this.id = "scenario-" + UUID.randomUUID();
        this.actions = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Creates a new scenario with the given name and description.
     *
     * @param name        the scenario name
     * @param description the scenario description
     */
    public Scenario(final String name, final String description) {
        this();
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the unique scenario identifier.
     *
     * @return the scenario identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the scenario name.
     *
     * @return the scenario name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the scenario name.
     *
     * @param name the scenario name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the scenario description.
     *
     * @return the scenario description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the scenario description.
     *
     * @param description the scenario description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the configured scenario actions in execution order.
     *
     * @return the scenario actions
     */
    public List<ScenarioAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Sets the scenario actions.
     *
     * @param actions the scenario actions
     */
    public void setActions(final List<ScenarioAction> actions) {
        this.actions = actions == null ? new ArrayList<>() : new ArrayList<>(actions);
    }

    /**
     * Returns the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the scenario name for UI lists.
     *
     * @return the scenario name
     */
    @Override
    public String toString() {
        return name;
    }
}
