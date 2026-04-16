package com.vermeeria.model;

import java.util.UUID;

/**
 * Represents a room that can contain smart home devices.
 *
 * @author Jette
 */
public class Room {

    private String id;
    private String name;

    /**
     * Creates an empty room instance for JSON deserialization.
     */
    public Room() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Creates a room with the given display name.
     *
     * @param name the room name
     */
    public Room(final String name) {
        this();
        this.name = name;
    }

    /**
     * Returns the unique room identifier.
     *
     * @return the room identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique room identifier.
     *
     * @param id the room identifier
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the room name.
     *
     * @return the room name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the room name.
     *
     * @param name the room name
     */
    public void setName(final String name) {
        this.name = name;
    }
}
