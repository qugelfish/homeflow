package com.vermeeria.ui.view;

import com.vermeeria.model.Room;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builds the room management area of the user interface.
 *
 * @author Jette
 */
public class RoomView {

    private final BorderPane root;
    private final ListView<Room> roomsList;
    private final TextField roomNameField;
    private final Button addRoomButton;
    private final Button editRoomButton;
    private final Button deleteRoomButton;

    /**
     * Creates the rooms view and initializes the layout.
     */
    public RoomView() {
        this.root = new BorderPane();
        this.roomsList = new ListView<>();
        this.roomNameField = new TextField();
        this.addRoomButton = new Button("Add room");
        this.editRoomButton = new Button("✎");
        this.deleteRoomButton = new Button("Delete room");
        this.editRoomButton.setTooltip(new Tooltip("Edit name"));
        this.addRoomButton.getStyleClass().add("success-button");
        this.deleteRoomButton.getStyleClass().add("danger-button");

        initialize();
    }

    private void initialize() {
        root.setLeft(createRoomsListCard());
        root.setCenter(createRoomEditorCard());
        root.setPadding(new Insets(0));
    }

    private Parent createRoomsListCard() {
        Label titleLabel = new Label("Rooms");
        titleLabel.getStyleClass().add("content-title");
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label subtitleLabel = new Label("Manage the rooms in your smart home.");
        subtitleLabel.getStyleClass().add("content-subtitle");
        subtitleLabel.setWrapText(true);

        roomsList.getStyleClass().add("navigation-list");
        VBox.setVgrow(roomsList, Priority.ALWAYS);

        VBox listCard = new VBox(12, titleLabel, subtitleLabel, roomsList);
        listCard.setPadding(new Insets(32));
        listCard.setPrefWidth(320);
        listCard.getStyleClass().add("content-card");

        return listCard;
    }

    private Parent createRoomEditorCard() {
        Label titleLabel = new Label("Room Details");
        titleLabel.getStyleClass().add("content-title");
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label subtitleLabel = new Label("Create a new room or prepare this area for later editing.");
        subtitleLabel.getStyleClass().add("content-subtitle");
        subtitleLabel.setWrapText(true);

        Label nameLabel = new Label("Room name");

        roomNameField.setPromptText("Enter room name");
        roomNameField.getStyleClass().add("form-input");
        roomNameField.setDisable(true);
        HBox.setHgrow(roomNameField, Priority.ALWAYS);

        HBox roomNameBar = new HBox(10, roomNameField, editRoomButton);
        Region buttonSpacer = new Region();
        HBox.setHgrow(buttonSpacer, Priority.ALWAYS);
        HBox buttonBar = new HBox(10, addRoomButton, buttonSpacer, deleteRoomButton);

        VBox editorCard = new VBox(12, titleLabel, subtitleLabel, nameLabel, roomNameBar, buttonBar);
        editorCard.setPadding(new Insets(32));
        editorCard.setSpacing(12);
        editorCard.getStyleClass().add("content-card");

        BorderPane.setMargin(editorCard, new Insets(0, 0, 0, 24));
        return editorCard;
    }

    /**
     * Returns the root view node.
     *
     * @return the root node
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Returns the room list.
     *
     * @return the room list
     */
    public ListView<Room> getRoomsList() {
        return roomsList;
    }

    /**
     * Returns the room name input field.
     *
     * @return the room name field
     */
    public TextField getRoomNameField() {
        return roomNameField;
    }

    /**
     * Returns the add-room button.
     *
     * @return the add button
     */
    public Button getAddRoomButton() {
        return addRoomButton;
    }

    /**
     * Returns the edit-room button.
     *
     * @return the edit button
     */
    public Button getEditRoomButton() {
        return editRoomButton;
    }

    /**
     * Returns the delete-room button.
     *
     * @return the delete button
     */
    public Button getDeleteRoomButton() {
        return deleteRoomButton;
    }
}
