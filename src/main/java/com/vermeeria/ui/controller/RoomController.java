package com.vermeeria.ui.controller;

import com.vermeeria.model.Room;
import com.vermeeria.service.RoomService;
import com.vermeeria.ui.view.RoomView;
import javafx.scene.Parent;

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
    private boolean editMode;
    private boolean createMode;

    /**
     * Creates the room controller and initializes the basic user interface for room controls.
     *
     * @param roomView the room view
     * @param roomService the room service
     * @param statusUpdater the callback used to update status messages
     */
    public RoomController(RoomView roomView, RoomService roomService, Consumer<String> statusUpdater) {
        this.roomView = roomView;
        this.roomService = roomService;
        this.statusUpdater = statusUpdater;
        this.editMode = false;
        this.createMode = false;

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
        if (!createMode) {
            enterCreateMode();
            statusUpdater.accept("Enter a name for the new room");
            return;
        }

        String roomNameInput = roomView.getRoomNameField().getText().trim();
        if (roomNameInput.isBlank()) {
            statusUpdater.accept("Room name cannot be empty");
            return;
        }

        try {
            roomService.createRoom(roomNameInput);
            roomView.getRoomNameField().clear();
            loadRooms();
            leaveCreateMode();
            statusUpdater.accept("Room created");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleEditRoom() {
        Room selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            statusUpdater.accept("Please select a room to edit");
            return;
        }

        if (createMode) {
            leaveCreateMode();
        }
        if (!editMode) {
            enterEditMode();
            statusUpdater.accept("Editing room name");
            return;
        }

        String roomNameInput = roomView.getRoomNameField().getText().trim();
        if (roomNameInput.isBlank()) {
            statusUpdater.accept("Room name cannot be empty");
            return;
        }

        try {
            roomService.updateRoom(selectedRoom.getId(), roomNameInput);
            loadRooms(selectedRoom.getId());
            leaveEditMode();
            statusUpdater.accept("Room updated");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void handleDeleteRoom() {
        Room selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            statusUpdater.accept("Please select a room to delete");
            return;
        }

        try {
            roomService.deleteRoom(selectedRoom.getId());
            roomView.getRoomNameField().clear();
            leaveEditMode();
            leaveCreateMode();
            loadRooms();
            handleRoomSelection();
            statusUpdater.accept("Room deleted");
        } catch (RuntimeException exception) {
            statusUpdater.accept(exception.getMessage());
        }
    }

    private void loadRooms() {
        loadRooms(null);
    }

    private void loadRooms(final String roomIdToReselect) {
        roomView.getRoomsList().getItems().setAll(roomService.getAllRooms());
        if (roomIdToReselect != null) {
            roomView.getRoomsList().getItems().stream()
                    .filter(room -> roomIdToReselect.equals(room.getId()))
                    .findFirst()
                    .ifPresent(room -> roomView.getRoomsList().getSelectionModel().select(room));
        }
    }

    private void handleRoomSelection() {
        Room selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        boolean roomSelected = selectedRoom != null;

        roomView.getEditRoomButton().setDisable(!roomSelected);
        roomView.getDeleteRoomButton().setDisable(!roomSelected);

        if (roomSelected && !createMode && !editMode) {
            roomView.getRoomNameField().setText(selectedRoom.getName());
        } else if (!roomSelected && !createMode) {
            roomView.getRoomNameField().clear();
        }

        if (!editMode && !createMode) {
            roomView.getRoomNameField().setDisable(true);
            roomView.getEditRoomButton().setText("✎");
        }
    }

    private void enterEditMode() {
        editMode = true;
        roomView.getRoomsList().setDisable(true);
        roomView.getRoomNameField().setDisable(false);
        roomView.getEditRoomButton().setText("Save");
        roomView.getRoomNameField().requestFocus();
        roomView.getRoomNameField().positionCaret(roomView.getRoomNameField().getText().length());
    }

    private void leaveEditMode() {
        editMode = false;
        roomView.getRoomsList().setDisable(false);
        roomView.getRoomNameField().setDisable(true);
        roomView.getEditRoomButton().setText("✎");
    }

    private void enterCreateMode() {
        createMode = true;
        editMode = false;
        roomView.getRoomsList().setDisable(true);
        roomView.getRoomsList().getSelectionModel().clearSelection();
        roomView.getRoomNameField().clear();
        roomView.getRoomNameField().setDisable(false);
        roomView.getEditRoomButton().setDisable(true);
        roomView.getDeleteRoomButton().setDisable(true);
        roomView.getAddRoomButton().setText("Save room");
        roomView.getRoomNameField().requestFocus();
    }

    private void leaveCreateMode() {
        createMode = false;
        roomView.getRoomsList().setDisable(false);
        roomView.getRoomNameField().setDisable(true);
        roomView.getAddRoomButton().setText("Add room");
        handleRoomSelection();
    }

    /**
     * Returns the root view of the controller.
     *
     * @return the root node
     */
    public Parent getView() {
        return roomView.getRoot();
    }

    /**
     * Reloads the current room data into the view.
     */
    public void refresh() {
        Room selectedRoom = roomView.getRoomsList().getSelectionModel().getSelectedItem();
        String roomIdToReselect = selectedRoom == null ? null : selectedRoom.getId();

        if (editMode) {
            leaveEditMode();
        }
        if (createMode) {
            leaveCreateMode();
        }

        loadRooms(roomIdToReselect);
        handleRoomSelection();
    }
}
