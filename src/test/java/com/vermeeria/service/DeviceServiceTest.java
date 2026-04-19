package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link DeviceService}
 *
 * @author Jette
 */
class DeviceServiceTest {

    private static final String LAMP_TYPE = "lamp";

    @Test
    void testCreateDevice_TrimsDeviceName() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        deviceService.createDevice("  Bedside Lamp   ", LAMP_TYPE, room.getId());

        assertEquals(1, appData.getDevices().size());
        assertEquals("Bedside Lamp", appData.getDevices().getFirst().getName());
    }

    @Test
    void testCreateDevice_ThrowsValidationException_WhenDeviceNameAlreadyExists() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        DeviceDefinition existingDevice = new DeviceDefinition("Floor Lamp", LAMP_TYPE, room.getId());
        appData.addRoom(room);
        appData.addDevice(existingDevice);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.createDevice("Floor Lamp", LAMP_TYPE, roomId));

        assertEquals("A device with this name already exists.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testCreateDevice_ThrowsValidationException_WhenDeviceNameIsBlank(final String deviceName) {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        appData.addRoom(room);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.createDevice(deviceName, LAMP_TYPE, roomId));

        assertEquals("Device name cannot be empty.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testCreateDevice_ThrowsValidationException_WhenTypeIsMissing(final String typeKey) {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        appData.addRoom(room);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.createDevice("Floor Lamp", typeKey, roomId));

        assertEquals("Please select a device type.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testCreateDevice_ThrowsValidationException_WhenRoomIsMissing(final String roomId) {
        AppData appData = new AppData();
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.createDevice("Floor Lamp", LAMP_TYPE, roomId));

        assertEquals("Please select a room.", exception.getMessage());
    }

    @Test
    void testCreateDevice_ThrowsValidationException_WhenRoomDoesNotExist() {
        AppData appData = new AppData();
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.createDevice("Floor Lamp", LAMP_TYPE, "room-missing"));

        assertEquals("The selected room no longer exists.", exception.getMessage());
    }

    @Test
    void testCreateDevice_CreatesDeviceSuccessfully() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        deviceService.createDevice("Floor Lamp", LAMP_TYPE, room.getId());

        assertEquals(1, appData.getDevices().size());
        DeviceDefinition createdDevice = appData.getDevices().getFirst();
        assertEquals("Floor Lamp", createdDevice.getName());
        assertEquals(LAMP_TYPE, createdDevice.getTypeKey());
        assertEquals(room.getId(), createdDevice.getRoomId());
        assertNotNull(createdDevice.getCurrentState());
    }

    @Test
    void testUpdateDevice_ChangesTheSelectedDevice() {
        AppData appData = new AppData();
        Room livingRoom = new Room("Living Room");
        Room bedroom = new Room("Bedroom");
        DeviceDefinition device = new DeviceDefinition("Floor Lamp", LAMP_TYPE, livingRoom.getId());
        appData.addRoom(livingRoom);
        appData.addRoom(bedroom);
        appData.addDevice(device);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        deviceService.updateDevice(device.getId(), "Bedside Lamp", LAMP_TYPE, bedroom.getId());

        assertEquals("Bedside Lamp", device.getName());
        assertEquals(bedroom.getId(), device.getRoomId());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateDevice_ThrowsValidationException_WhenDeviceIdIsBlank(final String deviceId) {
        AppData appData = new AppData();
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.updateDevice(deviceId, "Floor Lamp", LAMP_TYPE, "room-id"));

        assertEquals("Please select a device to edit.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateDevice_ThrowsValidationException_WhenNewDeviceNameIsBlank(final String deviceName) {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        DeviceDefinition device = new DeviceDefinition("Floor Lamp", LAMP_TYPE, room.getId());
        String deviceId = device.getId();
        String roomId = room.getId();
        appData.addRoom(room);
        appData.addDevice(device);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.updateDevice(deviceId, deviceName, LAMP_TYPE, roomId));

        assertEquals("Device name cannot be empty.", exception.getMessage());
    }

    @Test
    void testUpdateDevice_ThrowsValidationException_WhenDeviceDoesNotExist() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        appData.addRoom(room);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.updateDevice("device-missing", "Floor Lamp", LAMP_TYPE, roomId));

        assertEquals("The selected device no longer exists.", exception.getMessage());
    }

    @Test
    void testUpdateDevice_ThrowsValidationException_WhenAnotherDeviceAlreadyUsesTheName() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        DeviceDefinition firstDevice = new DeviceDefinition("Floor Lamp", LAMP_TYPE, room.getId());
        DeviceDefinition secondDevice = new DeviceDefinition("Desk Lamp", LAMP_TYPE, room.getId());
        String firstDeviceId = firstDevice.getId();
        String roomId = room.getId();
        appData.addRoom(room);
        appData.addDevice(firstDevice);
        appData.addDevice(secondDevice);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.updateDevice(firstDeviceId, "desk lamp", LAMP_TYPE, roomId));

        assertEquals("A device with this name already exists.", exception.getMessage());
    }

    @Test
    void testDeleteDevice_ThrowsValidationException_WhenDeviceNotFound() {
        AppData appData = new AppData();
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.deleteDevice("device-no-longer-exists"));

        assertEquals("The selected device no longer exists.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testDeleteDevice_ThrowsValidationException_WhenDeviceIdIsInvalid(final String deviceId) {
        AppData appData = new AppData();
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> deviceService.deleteDevice(deviceId));

        assertEquals("Please select a device to delete.", exception.getMessage());
    }

    @Test
    void testDeleteDevice_DeletesDeviceSuccessfully() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        DeviceDefinition device = new DeviceDefinition("Floor Lamp", LAMP_TYPE, room.getId());
        String deviceId = device.getId();
        appData.addRoom(room);
        appData.addDevice(device);
        DeviceService deviceService = ServiceTestHelper.createDeviceService(appData);

        deviceService.deleteDevice(deviceId);

        assertEquals(0, appData.getDevices().size());
    }
}
