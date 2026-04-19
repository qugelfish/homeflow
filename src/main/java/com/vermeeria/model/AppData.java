package com.vermeeria.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores all application data that should be persisted in the JSON file.
 * This includes rooms, devices, scenarios and execution log entries.
 *
 * @author Jette, Dario
 */
public class AppData {

    private List<Room> rooms = new ArrayList<>();
    private List<DeviceDefinition> devices = new ArrayList<>();
    private List<Scenario> scenarios = new ArrayList<>();
    private List<ExecutionLogEntry> logs = new ArrayList<>();

    /**
     * Returns the list of all rooms.
     *
     * @return the list of rooms
     */
    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    /**
     * Sets the list of rooms.
     *
     * @param rooms the rooms to store
     */
    public void setRooms(final List<Room> rooms) {
        this.rooms = rooms == null ? new ArrayList<>() : new ArrayList<>(rooms);
    }

    /**
     * Returns the list of all devices.
     *
     * @return the list of devices
     */
    public List<DeviceDefinition> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    /**
     * Sets the list of devices.
     *
     * @param devices the devices to store
     */
    public void setDevices(final List<DeviceDefinition> devices) {
        this.devices = devices == null ? new ArrayList<>() : new ArrayList<>(devices);
    }

    /**
     * Returns the list of all scenarios.
     *
     * @return the list of scenarios
     */
    public List<Scenario> getScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    /**
     * Sets the list of scenarios.
     *
     * @param scenarios the scenarios to store
     */
    public void setScenarios(final List<Scenario> scenarios) {
        this.scenarios = scenarios == null ? new ArrayList<>() : new ArrayList<>(scenarios);
    }

    /**
     * Returns the list of execution log entries.
     *
     * @return the list of log entries
     */
    public List<ExecutionLogEntry> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Sets the list of execution log entries.
     *
     * @param logs the log entries to store
     */
    public void setLogs(final List<ExecutionLogEntry> logs) {
        this.logs = logs == null ? new ArrayList<>() : new ArrayList<>(logs);
    }

    /**
     * Adds one room to the application data.
     *
     * @param room the room to add
     */
    public void addRoom(final Room room) {
        rooms.add(room);
    }

    /**
     * Removes one room from the application data.
     *
     * @param room the room to remove
     */
    public void removeRoom(final Room room) {
        rooms.remove(room);
    }

    /**
     * Adds one device to the application data.
     *
     * @param device the device to add
     */
    public void addDevice(final DeviceDefinition device) {
        devices.add(device);
    }

    /**
     * Removes one device from the application data.
     *
     * @param device the device to remove
     */
    public void removeDevice(final DeviceDefinition device) {
        devices.remove(device);
    }

    /**
     * Adds one scenario to the application data.
     *
     * @param scenario the scenario to add
     */
    public void addScenario(final Scenario scenario) {
        scenarios.add(scenario);
    }

    /**
     * Removes one scenario from the application data.
     *
     * @param scenario the scenario to remove
     */
    public void removeScenario(final Scenario scenario) {
        scenarios.remove(scenario);
    }

    /**
     * Adds one execution log entry to the application data.
     *
     * @param logEntry the log entry to add
     */
    public void addLogEntry(final ExecutionLogEntry logEntry) {
        logs.add(logEntry);
    }
}
