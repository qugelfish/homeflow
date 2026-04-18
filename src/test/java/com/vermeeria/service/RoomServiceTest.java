package com.vermeeria.service;

import com.vermeeria.model.AppData;
import com.vermeeria.model.Room;
import com.vermeeria.persistence.SmartHomeRepository;
import org.junit.jupiter.api.Test;

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
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        roomService.createRoom("  Bedroom   ");

        assertEquals(2, appData.getRooms().size());
        assertEquals("Bedroom", appData.getRooms().get(1).getName());
    }

    @Test
    void testCreateRoom_ThrowsValidationException_WhenRoomNameAlreadyExists() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.createRoom("Living Room")
        );

        assertEquals("A room with this name already exists.", exception.getMessage());
    }

    @Test
    void testCreateRoom_ThrowsValidationException_WhenRoomNameIsBlank() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.createRoom("   ")
        );

        assertEquals("Room name cannot be empty.", exception.getMessage());
    }

    @Test
    void testCreateRoom_CreatesRoomSuccessfully() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        roomService.createRoom("Bedroom");

        assertEquals(2, appData.getRooms().size());
        assertEquals("Bedroom", appData.getRooms().get(1).getName());
    }

    @Test
    void testUpdateRoom_ChangesTheNameOfTheSelectedRoom() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        roomService.updateRoom(room.getId(), "Bedroom");

        assertEquals("Bedroom", room.getName());
    }

    @Test
    void testUpdateRoom_ThrowsValidationException_WhenRoomIdIsBlank() {
        AppData appData = new AppData();
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.updateRoom(" ", "Bedroom")
        );

        assertEquals("Please select a room to edit.", exception.getMessage());
    }

    @Test
    void testUpdateRoom_ThrowsValidationException_WhenNewRoomNameIsBlank() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.updateRoom(room.getId(), "   ")
        );

        assertEquals("Room name cannot be empty.", exception.getMessage());
    }

    @Test
    void testUpdateRoom_ThrowsValidationException_WhenRoomDoesNotExist() {
        AppData appData = new AppData();
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.updateRoom("room-missing", "Bedroom")
        );

        assertEquals("The selected room no longer exists.", exception.getMessage());
    }

    @Test
    void testUpdateRoom_ThrowsValidationException_WhenAnotherRoomAlreadyUsesTheName() {
        AppData appData = new AppData();
        Room livingRoom = new Room("Living Room");
        Room bedroom = new Room("Bedroom");
        appData.getRooms().add(livingRoom);
        appData.getRooms().add(bedroom);
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.updateRoom(livingRoom.getId(), "bedroom")
        );

        assertEquals("A room with this name already exists.", exception.getMessage());
    }

    @Test
    void testDeleteRoom_ThrowsValidationException_WhenRoomNotFound() {
        AppData appData = new AppData();
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.deleteRoom("room-no-longer-exists")
        );

        assertEquals("The selected room no longer exists.", exception.getMessage());
    }

    @Test
    void testDeleteRoom_ThrowsValidationException_WhenRoomIdIsInvalid() {
        AppData appData = new AppData();
        RoomService roomService = createRoomService(appData);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> roomService.deleteRoom("   ")
        );

        assertEquals("Please select a room to delete.", exception.getMessage());
    }

    @Test
    void testDeleteRoom_DeletesRoomSuccessfully() {
        AppData appData = new AppData();
        Room room = new Room("Living Room");
        String roomId = room.getId();
        appData.getRooms().add(room);
        RoomService roomService = createRoomService(appData);

        roomService.deleteRoom(roomId);

        assertEquals(0, appData.getRooms().size());
    }

    private RoomService createRoomService(final AppData appData) {
        SmartHomeRepository repository = new InMemorySmartHomeRepository(appData);
        SmartHomeService smartHomeService = new SmartHomeService(repository);
        return new RoomService(smartHomeService);
    }

    /**
     * Simple in-memory repository used for isolated unit tests.
     */
    private record InMemorySmartHomeRepository(AppData appData) implements SmartHomeRepository {

        private InMemorySmartHomeRepository {
        }

        @Override
        public AppData load() {
            return appData;
        }

        @Override
        public void save(final AppData appData) {
        }
    }
}
