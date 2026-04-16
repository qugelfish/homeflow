package com.vermeeria.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vermeeria.model.AppData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Stores and loads application data in JSON format.
 * The data is written to and read from a file on the local file system.
 *
 * @author Jette
 */
public class JsonSmartHomeRepository implements SmartHomeRepository {

    private final Path filePath;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new JSON repository for the given file path.
     *
     * @param filePath the path of the JSON file used for persistence
     */
    public JsonSmartHomeRepository(Path filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Loads the application data from the JSON file.
     * If the file does not exist, a new empty AppData object is returned.
     *
     * @return the loaded application data
     * @throws RuntimeException if the file cannot be read
     */
    @Override
    public AppData load() {
        if (Files.notExists(filePath)) {
            return new AppData();
        }

        try {
            if (Files.size(filePath) == 0L || Files.readString(filePath).isBlank()) {
                return new AppData();
            }

            return objectMapper.readValue(filePath.toFile(), AppData.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not load app data.", e);
        }
    }

    /**
     * Saves the given application data to the JSON file.
     * If the target directory does not exist, it is created automatically.
     *
     * @param appData the application data to save
     * @throws RuntimeException if the file cannot be written
     */
    @Override
    public void save(AppData appData) {
        try {
            Files.createDirectories(filePath.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), appData);
        } catch (IOException e) {
            throw new RuntimeException("Could not save app data.", e);
        }
    }
}
