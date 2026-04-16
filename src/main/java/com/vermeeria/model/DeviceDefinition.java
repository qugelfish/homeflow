package com.vermeeria.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a simulated smart home device.
 *
 * @author Jette
 */
public class DeviceDefinition {

    private String id;
    private String name;
    private String typeKey;
    private String roomName;
    private Map<String, Object> currentState = new LinkedHashMap<>();

    /**
     * Creates an empty device definition for JSON deserialization.
     */
    public DeviceDefinition() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Creates a device definition with the essential fields.
     *
     * @param name the device name
     * @param typeKey the device type key
     * @param roomName the room name
     */
    public DeviceDefinition(final String name, final String typeKey, final String roomName) {
        this();
        this.name = name;
        this.typeKey = typeKey;
        this.roomName = roomName;
    }

    /**
     * Returns the unique device identifier.
     *
     * @return the device identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique device identifier.
     *
     * @param id the device identifier
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the device name.
     *
     * @return the device name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the device name.
     *
     * @param name the device name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the technical type key of the device.
     *
     * @return the device type key
     */
    public String getTypeKey() {
        return typeKey;
    }

    /**
     * Sets the technical type key of the device.
     *
     * @param typeKey the device type key
     */
    public void setTypeKey(final String typeKey) {
        this.typeKey = typeKey;
    }

    /**
     * Returns the room name the device belongs to.
     *
     * @return the room name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Sets the room name the device belongs to.
     *
     * @param roomName the room name
     */
    public void setRoomName(final String roomName) {
        this.roomName = roomName;
    }

    /**
     * Returns the current simulated device state.
     *
     * @return the current state map
     */
    public Map<String, Object> getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current simulated device state.
     *
     * @param currentState the current state map
     */
    public void setCurrentState(final Map<String, Object> currentState) {
        this.currentState = currentState;
    }
}
