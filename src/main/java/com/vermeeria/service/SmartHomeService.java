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
     * Saves the current application data using the configured repository.
     */
    public void saveAll() {
        repository.save(appData);
    }
}
