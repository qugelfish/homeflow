package com.vermeeria.service;

import com.vermeeria.model.Room;

import java.util.List;

/**
 * Provides room-related business logic.
 *
 * @author Jette
 */
public class RoomService {

    private final SmartHomeService smartHomeService;

    /**
     * Creates a room service backed by the shared application state.
     *
     * @param smartHomeService the shared smart home service
     */
    public RoomService(final SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    public void createRoom(final String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new ValidationException("Room name cannot be empty.");
        }

        String normalizedName = roomName.trim();

        boolean duplicateNameExists = smartHomeService.getAppData().getRooms().stream()
                .map(Room::getName)
                .anyMatch(existingName -> existingName.equalsIgnoreCase(normalizedName));

        if (duplicateNameExists) {
            throw new ValidationException("A room with this name already exists.");
        }

        smartHomeService.getAppData().addRoom(new Room(normalizedName));
        smartHomeService.saveAll();
    }

    public void updateRoom(final String roomId, final String newRoomName) {
        if (roomId == null || roomId.isBlank()) {
            throw new ValidationException("Please select a room to edit.");
        }
        if (newRoomName == null || newRoomName.isBlank()) {
            throw new ValidationException("Room name cannot be empty.");
        }

        String normalizedName = newRoomName.trim();

        Room roomToUpdate = smartHomeService.getAppData().getRooms().stream()
                .filter(room -> roomId.equals(room.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("The selected room no longer exists."));

        boolean duplicateNameExists = smartHomeService.getAppData().getRooms().stream()
                .filter(room -> !room.getId().equals(roomId))
                .map(Room::getName)
                .anyMatch(existingName -> existingName.equalsIgnoreCase(normalizedName));

        if (duplicateNameExists) {
            throw new ValidationException("A room with this name already exists.");
        }

        roomToUpdate.setName(normalizedName);
        smartHomeService.saveAll();
    }

    public void deleteRoom(final String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new ValidationException("Please select a room to delete.");
        }

        Room roomToDelete = smartHomeService.getAppData().getRooms().stream()
                .filter(room -> roomId.equals(room.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("The selected room no longer exists."));

        smartHomeService.getAppData().removeRoom(roomToDelete);
        smartHomeService.saveAll();
    }

    public List<Room> getAllRooms() {
        return smartHomeService.getAppData().getRooms().stream()
                .sorted((firstRoom, secondRoom) -> firstRoom.getName().compareToIgnoreCase(secondRoom.getName()))
                .toList();
    }
}
