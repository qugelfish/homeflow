package com.vermeeria.persistence;

import com.vermeeria.model.AppData;

/**
 * Defines the methods required for loading and saving application data.
 * Implementations of this interface handle the persistence mechanism,
 * for example JSON-based storage.
 *
 * @author Jette
 */
public interface SmartHomeRepository {

    /**
     * Loads all application data from the persistence source.
     *
     * @return the loaded application data
     */
    AppData load();

    /**
     * Saves all application data to the persistence source.
     *
     * @param appData the application data to save
     */
    void save(AppData appData);
}
