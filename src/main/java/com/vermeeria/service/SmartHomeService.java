package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.persistence.SmartHomeRepository;

/**
 * Provides the central business logic of the smart home application.
 * The service manages the application data in memory and delegates
 * loading and saving operations to the repository.
 *
 * @author Jette
 */
public class SmartHomeService {

    private final SmartHomeRepository repository;
    private AppData appData;

    /**
     * Creates a new SmartHomeService and loads the persisted application data.
     *
     * @param repository the repository used to load and save the data
     */
    public SmartHomeService(SmartHomeRepository repository) {
        this.repository = repository;
        this.appData = repository.load();
    }

    /**
     * Returns the current application data.
     *
     * @return the current application data
     */
    public AppData getAppData() {
        return appData;
    }

    /**
     * Replaces the current in-memory data with a freshly loaded snapshot.
     */
    public void reloadAll() {
        this.appData = repository.load();
    }

    /**
     * Clears the current application state in memory.
     */
    public void resetAppData() {
        this.appData = new AppData();
    }

    /**
     * Returns the current number of rooms.
     *
     * @return the room count
     */
    public int getRoomCount() {
        return appData.getRooms().size();
    }

    /**
     * Returns the current number of devices.
     *
     * @return the device count
     */
    public int getDeviceCount() {
        return appData.getDevices().size();
    }

    /**
     * Returns the current number of scenarios.
     *
     * @return the scenario count
     */
    public int getScenarioCount() {
        return appData.getScenarios().size();
    }

    /**
     * Saves the current application data using the configured repository.
     */
    public void saveAll() {
        repository.save(appData);
    }
}
