package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.persistence.SmartHomeRepository;

/**
 * Shared test helper class for service-layer unit tests.
 *
 * @author Jette
 */
final class ServiceTestHelper {

    private ServiceTestHelper() {
    }

    static SmartHomeService createSmartHomeService(final AppData appData) {
        return new SmartHomeService(new InMemorySmartHomeRepository(appData));
    }

    static RoomService createRoomService(final AppData appData) {
        return new RoomService(createSmartHomeService(appData));
    }

    static DeviceService createDeviceService(final AppData appData) {
        return new DeviceService(createSmartHomeService(appData));
    }

    /**
     * Simple in-memory repository used for isolated unit tests.
     */
    private record InMemorySmartHomeRepository(AppData appData) implements SmartHomeRepository {

        @Override
        public AppData load() {
            return appData;
        }

        @Override
        public void save(final AppData appData) {
            // Not needed for the tests, as we directly manipulate the in-memory AppData instance.
        }
    }
}
