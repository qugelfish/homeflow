package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link RoomService}
 *
 * @author Jette
 */
class RoomServiceTest {

    @Test
    void testCreateRoom_TrimsRoomName() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        roomService.createRoom("  Bedroom   ");

        assertEquals(2, appData.getRooms().size());
        assertEquals("Bedroom", appData.getRooms().get(1).getName());
    }

    @Test
    void testCreateRoom_ThrowsValidationException_WhenRoomNameAlreadyExists() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.createRoom("Living Room"));

        assertEquals("A room with this name already exists.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testCreateRoom_ThrowsValidationException_WhenRoomNameIsBlank(final String roomName) {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.createRoom(roomName));

        assertEquals("Room name cannot be empty.", exception.getMessage());
    }

    @Test
    void testCreateRoom_CreatesRoomSuccessfully() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        roomService.createRoom("Bedroom");

        assertEquals(2, appData.getRooms().size());
        assertEquals("Bedroom", appData.getRooms().get(1).getName());
    }

    @Test
    void testUpdateRoom_ChangesTheNameOfTheSelectedRoom() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        roomService.updateRoom(room.getId(), "Bedroom");

        assertEquals("Bedroom", room.getName());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateRoom_ThrowsValidationException_WhenRoomIdIsBlank(final String roomId) {
        AppData appData = new AppData();
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.updateRoom(roomId, "Bedroom"));

        assertEquals("Please select a room to edit.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testUpdateRoom_ThrowsValidationException_WhenNewRoomNameIsBlank(final String roomName) {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.updateRoom(roomId, roomName));

        assertEquals("Room name cannot be empty.", exception.getMessage());
    }

    @Test
    void testUpdateRoom_ThrowsValidationException_WhenRoomDoesNotExist() {
        AppData appData = new AppData();
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.updateRoom("room-missing", "Bedroom"));

        assertEquals("The selected room no longer exists.", exception.getMessage());
    }

    @Test
    void testUpdateRoom_ThrowsValidationException_WhenAnotherRoomAlreadyUsesTheName() {
        AppData appData = new AppData();
        Room livingRoom = new Room("Living Room");
        Room bedroom = new Room("Bedroom");
        String livingRoomId = livingRoom.getId();
        appData.addRoom(livingRoom);
        appData.addRoom(bedroom);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.updateRoom(livingRoomId, "bedroom"));

        assertEquals("A room with this name already exists.", exception.getMessage());
    }

    @Test
    void testDeleteRoom_ThrowsValidationException_WhenRoomNotFound() {
        AppData appData = new AppData();
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.deleteRoom("room-no-longer-exists"));

        assertEquals("The selected room no longer exists.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void testDeleteRoom_ThrowsValidationException_WhenRoomIdIsInvalid(final String roomId) {
        AppData appData = new AppData();
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        ValidationException exception = assertThrows(ValidationException.class, () -> roomService.deleteRoom(roomId));

        assertEquals("Please select a room to delete.", exception.getMessage());
    }

    @Test
    void testDeleteRoom_DeletesRoomSuccessfully() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        appData.addRoom(room);
        RoomService roomService = ServiceTestHelper.createRoomService(appData);

        roomService.deleteRoom(roomId);

        assertEquals(0, appData.getRooms().size());
    }
}
