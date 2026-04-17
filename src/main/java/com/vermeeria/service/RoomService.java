package com.vermeeria.service;

import com.vermeeria.model.Room;

import java.util.List;

public class RoomService {

    public void createRoom(final String roomName) {
        // TODO validate the room name
        // TODO prevent duplicate room names if room names should be unique
        // TODO create a new Room object
        // TODO add the room to AppData through the shared application state
    }

    public void updateRoom(final String roomId, final String newRoomName) {
        // TODO find the room by its id
        // TODO validate the new room name
        // TODO update the selected room in the application state
    }

    public void deleteRoom(final String roomId) {
        // TODO find the room by its id
        // TODO define what happens when devices are still assigned to the room
        // TODO remove the room from the application state
    }

    public List<Room> getAllRooms() {
        // TODO return all rooms from the application state
        // TODO sort the rooms in a stable way for the UI if needed
        return List.of();
    }
}
