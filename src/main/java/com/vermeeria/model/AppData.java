package com.vermeeria.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores all application data that should be persisted in the JSON file.
 * This includes rooms, devices, scenarios and execution log entries.
 *
 * @author Jette
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
        return rooms;
    }

    /**
     * Sets the list of rooms.
     *
     * @param rooms the rooms to store
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Returns the list of all devices.
     *
     * @return the list of devices
     */
    public List<DeviceDefinition> getDevices() {
        return devices;
    }

    /**
     * Sets the list of devices.
     *
     * @param devices the devices to store
     */
    public void setDevices(List<DeviceDefinition> devices) {
        this.devices = devices;
    }

    /**
     * Returns the list of all scenarios.
     *
     * @return the list of scenarios
     */
    public List<Scenario> getScenarios() {
        return scenarios;
    }

    /**
     * Sets the list of scenarios.
     *
     * @param scenarios the scenarios to store
     */
    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    /**
     * Returns the list of execution log entries.
     *
     * @return the list of log entries
     */
    public List<ExecutionLogEntry> getLogs() {
        return logs;
    }

    /**
     * Sets the list of execution log entries.
     *
     * @param logs the log entries to store
     */
    public void setLogs(List<ExecutionLogEntry> logs) {
        this.logs = logs;
    }
}
