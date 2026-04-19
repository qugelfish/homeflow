package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.Room;
import com.vermeeria.model.Scenario;
import com.vermeeria.persistence.SmartHomeRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Test class for {@link SmartHomeService}
 *
 * @author Jette
 */
class SmartHomeServiceTest {

    @Test
    void testConstructor_LoadsInitialAppDataFromRepository() {
        AppData initialData = new AppData();
        RecordingSmartHomeRepository repository = new RecordingSmartHomeRepository(initialData, new AppData());

        SmartHomeService smartHomeService = new SmartHomeService(repository);

        assertSame(initialData, smartHomeService.getAppData());
        assertEquals(1, repository.getLoadCalls());
    }

    @Test
    void testReloadAll_ReplacesTheCurrentAppDataWithFreshlyLoadedData() {
        AppData initialData = new AppData();
        initialData.addRoom(new Room("Living Room"));

        AppData reloadedData = new AppData();
        reloadedData.addRoom(new Room("Bedroom"));

        RecordingSmartHomeRepository repository = new RecordingSmartHomeRepository(initialData, reloadedData);
        SmartHomeService smartHomeService = new SmartHomeService(repository);

        smartHomeService.reloadAll();

        assertSame(reloadedData, smartHomeService.getAppData());
        assertEquals(2, repository.getLoadCalls());
    }

    @Test
    void testResetAppData_CreatesANewEmptyAppDataInstance() {
        AppData initialData = new AppData();
        initialData.addRoom(new Room("Living Room"));
        initialData.addDevice(new DeviceDefinition("Floor Lamp", "lamp", "room-1"));
        initialData.addScenario(new Scenario("Evening Routine", "Description"));

        RecordingSmartHomeRepository repository = new RecordingSmartHomeRepository(initialData, new AppData());
        SmartHomeService smartHomeService = new SmartHomeService(repository);

        smartHomeService.resetAppData();

        assertNotNull(smartHomeService.getAppData());
        assertNotSame(initialData, smartHomeService.getAppData());
        assertEquals(0, smartHomeService.getRoomCount());
        assertEquals(0, smartHomeService.getDeviceCount());
        assertEquals(0, smartHomeService.getScenarioCount());
    }

    @Test
    void testGetCounts_ReturnTheCurrentModelSizes() {
        AppData appData = new AppData();
        appData.addRoom(new Room("Living Room"));
        appData.addRoom(new Room("Bedroom"));
        appData.addDevice(new DeviceDefinition("Floor Lamp", "lamp", "room-1"));
        appData.addScenario(new Scenario("Evening Routine", "Description"));
        appData.addScenario(new Scenario("Night Routine", "Description"));

        RecordingSmartHomeRepository repository = new RecordingSmartHomeRepository(appData, new AppData());
        SmartHomeService smartHomeService = new SmartHomeService(repository);

        assertEquals(2, smartHomeService.getRoomCount());
        assertEquals(1, smartHomeService.getDeviceCount());
        assertEquals(2, smartHomeService.getScenarioCount());
    }

    @Test
    void testSaveAll_PersistsTheCurrentAppData() {
        AppData appData = new AppData();
        appData.addRoom(new Room("Living Room"));

        RecordingSmartHomeRepository repository = new RecordingSmartHomeRepository(appData, new AppData());
        SmartHomeService smartHomeService = new SmartHomeService(repository);

        smartHomeService.saveAll();

        assertEquals(1, repository.getSaveCalls());
        assertSame(appData, repository.getLastSavedAppData());
    }

    /**
     * Repository double that records load and save interactions.
     */
    private static final class RecordingSmartHomeRepository implements SmartHomeRepository {

        private final AppData[] loadSnapshots;
        private int loadCalls;
        private int saveCalls;
        private AppData lastSavedAppData;

        private RecordingSmartHomeRepository(final AppData... loadSnapshots) {
            this.loadSnapshots = loadSnapshots;
        }

        @Override
        public AppData load() {
            int index = Math.min(loadCalls, loadSnapshots.length - 1);
            AppData loadedData = loadSnapshots[index];
            loadCalls++;
            return loadedData;
        }

        @Override
        public void save(final AppData appData) {
            saveCalls++;
            lastSavedAppData = appData;
        }

        int getLoadCalls() {
            return loadCalls;
        }

        int getSaveCalls() {
            return saveCalls;
        }

        AppData getLastSavedAppData() {
            return lastSavedAppData;
        }
    }
}
