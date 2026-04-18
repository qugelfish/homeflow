package com.vermeeria.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a simulated smart home device configured by the user.
 *
 * @author Jette
 */
public class DeviceDefinition {

    private final String id;
    private String name;
    private String typeKey;
    private String roomId;
    private Map<String, Object> currentState;

    /**
     * Creates an empty device definition with generated metadata.
     */
    public DeviceDefinition() {
        this.id = "device-" + UUID.randomUUID();
        this.currentState = new LinkedHashMap<>();
    }

    /**
     * Creates a new device definition for the given device type and room.
     *
     * @param name    the display name of the device
     * @param typeKey the technical device type key
     * @param roomId  the identifier of the assigned room
     */
    public DeviceDefinition(final String name, final String typeKey, final String roomId) {
        this();
        this.name = name;
        this.typeKey = typeKey;
        this.roomId = roomId;
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
     * Returns the display name of the device.
     *
     * @return the device name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the device.
     *
     * @param name the device name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the technical device type key.
     *
     * @return the device type key
     */
    public String getTypeKey() {
        return typeKey;
    }

    /**
     * Sets the technical device type key.
     *
     * @param typeKey the device type key
     */
    public void setTypeKey(final String typeKey) {
        this.typeKey = typeKey;
    }

    /**
     * Returns the assigned room identifier.
     *
     * @return the room identifier
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets the assigned room identifier.
     *
     * @param roomId the room identifier
     */
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns the current simulated state of the device.
     *
     * @return the state map
     */
    public Map<String, Object> getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current simulated state of the device.
     *
     * @param currentState the state map
     */
    public void setCurrentState(final Map<String, Object> currentState) {
        this.currentState = currentState;
    }
}
