package com.vermeeria.ui.controller;

import com.vermeeria.service.RoomService;
import com.vermeeria.ui.view.RoomView;
import javafx.scene.Parent;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Controls the rooms window of the HomeFlow application.
 *
 * @author Jette
 */
public class RoomController {

    private final RoomView roomView;
    private final RoomService roomService;
    private final Consumer<String> statusUpdater;

    /**
     * Creates the room controller and initializes the basic user interface for room controls.
     *
     * @param roomService the room service
     */
    public RoomController(RoomView roomView, RoomService roomService, Consumer<String> statusUpdater) {
        this.roomView = roomView;
        this.roomService = roomService;
        this.statusUpdater = statusUpdater;

        wireActions();
        loadRooms();
        handleRoomSelection();
    }

    private void wireActions() {
        roomView.getRoomsList().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleRoomSelection());
        roomView.getAddRoomButton().setOnAction(event -> handleAddRoom());
        roomView.getEditRoomButton().setOnAction(event -> handleEditRoom());
        roomView.getDeleteRoomButton().setOnAction(event -> handleDeleteRoom());
    }

    private void handleAddRoom() {
        String roomNameInput = roomView.getRoomNameField().getText().trim();
        if (roomNameInput.isBlank()) {
            statusUpdater.accept("Room name cannot be empty");
            return;
        }

        try {
            roomService.createRoom(roomNameInput);
            roomView.getRoomNameField().clear();
            loadRooms();
            statusUpdater.accept("Room created");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleEditRoom() {
        String selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        String roomNameInput = roomView.getRoomNameField().getText().trim();

        if (selectedRoom == null) {
            statusUpdater.accept("Please select a room to edit");
            return;
        }
        if (roomNameInput.isBlank()) {
            statusUpdater.accept("Room name cannot be empty");
            return;
        }

        try {
            roomService.updateRoom(selectedRoom, roomNameInput);
            loadRooms();
            roomView.getRoomsList().getSelectionModel().select(roomNameInput);
            statusUpdater.accept("Room updated");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleDeleteRoom() {
        String selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            statusUpdater.accept("Please select a room to delete");
            return;
        }

        try {
            roomService.deleteRoom(selectedRoom);
            roomView.getRoomNameField().clear();
            loadRooms();
            handleRoomSelection();
            statusUpdater.accept("Room deleted");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void loadRooms() {
        roomView.getRoomsList().getItems().setAll(roomService.getAllRooms().stream().filter(Objects::nonNull).map(Object::toString).toList());
    }

    private void handleRoomSelection() {
        String selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        boolean roomSelected = selectedRoom != null;

        roomView.getEditRoomButton().setDisable(!roomSelected);
        roomView.getDeleteRoomButton().setDisable(!roomSelected);

        if (roomSelected) {
            roomView.getRoomNameField().setText(selectedRoom);
        }
    }

    /**
     * Returns the root view of the controller.
     *
     * @return the root node
     */
    public Parent getView() {
        return roomView.getRoot();
    }
}
