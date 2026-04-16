package com.vermeeria.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a reusable smart home scenario.
 *
 * @author Jette
 */
public class Scenario {

    private String id;
    private String name;
    private String description;
    private List<ScenarioAction> actions = new ArrayList<>();

    /**
     * Creates an empty scenario for JSON deserialization.
     */
    public Scenario() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Creates a scenario with name and description.
     *
     * @param name the scenario name
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
     * Sets the unique scenario identifier.
     *
     * @param id the scenario identifier
     */
    public void setId(final String id) {
        this.id = id;
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
     * Returns the configured scenario actions.
     *
     * @return the list of actions
     */
    public List<ScenarioAction> getActions() {
        return actions;
    }

    /**
     * Sets the configured scenario actions.
     *
     * @param actions the list of actions
     */
    public void setActions(final List<ScenarioAction> actions) {
        this.actions = actions;
    }
}
