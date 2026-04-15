package com.vermeeria.persistence;

import com.vermeeria.model.AppData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
class JsonSmartHomeRepositoryTest {

	@Test
	void loadReturnsEmptyAppDataWhenFileDoesNotExist() {
		Path file = Path.of(System.getProperty("java.io.tmpdir"), "smarthome-missing-" + System.nanoTime(), "app-data.json");

		JsonSmartHomeRepository repository = new JsonSmartHomeRepository(file);

		AppData appData = assertDoesNotThrow(repository::load);

		assertNotNull(appData);
		assertEquals(0, appData.getRooms().size());
		assertEquals(0, appData.getDevices().size());
		assertEquals(0, appData.getScenarios().size());
		assertEquals(0, appData.getLogs().size());
	}

	@Test
	void loadReturnsEmptyAppDataWhenFileIsEmpty() throws IOException {
		Path directory = Files.createTempDirectory("smarthome-empty-");
		Path file = directory.resolve("app-data.json");
		Files.createFile(file);

		JsonSmartHomeRepository repository = new JsonSmartHomeRepository(file);

		AppData appData = assertDoesNotThrow(repository::load);

		assertNotNull(appData);
		assertEquals(0, appData.getRooms().size());
		assertEquals(0, appData.getDevices().size());
		assertEquals(0, appData.getScenarios().size());
		assertEquals(0, appData.getLogs().size());
	}
}

