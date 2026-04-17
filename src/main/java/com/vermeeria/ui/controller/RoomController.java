package com.vermeeria.ui.controller;

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
    }

    private void wireActions() {
        // TODO register selection handling so the editor reflects the selected room
        roomView.getAddRoomButton().setOnAction(event -> handleAddRoom());
        roomView.getEditRoomButton().setOnAction(event -> handleEditRoom());
        roomView.getDeleteRoomButton().setOnAction(event -> handleDeleteRoom());
    }

    private void handleAddRoom() {
        // TODO validate the entered room name before saving
        // TODO create the room via RoomService
        // TODO clear the text field after a successful save
        // TODO refresh the list of rooms after adding
        // TODO report validation errors and success messages via the status bar
        statusUpdater.accept("Adding room...");
    }

    private void handleEditRoom() {
        // TODO ensure a room is selected before editing
        // TODO validate the updated room name
        // TODO update the selected room via RoomService
        // TODO refresh the list and keep the edited room selected
        // TODO report validation errors and success messages via the status bar
        statusUpdater.accept("Editing room...");
    }

    private void handleDeleteRoom() {
        // TODO ensure a room is selected before deleting
        // TODO decide how rooms with assigned devices should be handled
        // TODO delete the selected room via RoomService
        // TODO refresh the list and clear the editor after deletion
        // TODO report validation errors and success messages via the status bar
        statusUpdater.accept("Deleting room...");
    }

    private void loadRooms() {
        // TODO load all existing rooms from RoomService
        // TODO map persisted room data to visible list entries
        // TODO update the ListView with the current room names
    }

    private void handleRoomSelection() {
        // TODO read the selected room from the ListView
        // TODO show the selected room data in the editor field
        // TODO disable edit and delete actions when nothing is selected
    }

    public Parent getView() {
        return roomView.getRoot();
    }
}
