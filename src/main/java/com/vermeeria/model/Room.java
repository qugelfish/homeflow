package com.vermeeria.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a room in the smart home.
 *
 * @author Jette
 */
public class Room {

    private final String id;
    private String name;
    private LocalDateTime createdAt;

    /**
     * Creates an empty room instance with generated metadata.
     */
    public Room() {
        this.id = "room-" + UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Creates a new room with the given name.
     *
     * @param name the name of the room
     */
    public Room(String name) {
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
    public void setName(String name) {
        this.name = name;
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
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
