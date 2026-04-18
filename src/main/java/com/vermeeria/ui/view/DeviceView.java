package com.vermeeria.ui.view;

import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.Room;
import com.vermeeria.plugin.DevicePlugin;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
 * Builds the device management area of the user interface.
 *
 * @author Jette
 */
public class DeviceView {

    private final BorderPane root;
    private final ListView<DeviceDefinition> devicesList;
    private final TextField deviceNameField;
    private final ComboBox<DevicePlugin> deviceTypeBox;
    private final ComboBox<Room> roomBox;
    private final Label deviceStateValueLabel;
    private final Button addDeviceButton;
    private final Button editDeviceButton;
    private final Button deleteDeviceButton;

    /**
     * Creates the devices view and initializes the layout.
     */
    public DeviceView() {
        this.root = new BorderPane();
        this.devicesList = new ListView<>();
        this.deviceNameField = new TextField();
        this.deviceTypeBox = new ComboBox<>();
        this.roomBox = new ComboBox<>();
        this.deviceStateValueLabel = new Label("No device selected");
        this.addDeviceButton = new Button("Add device");
        this.editDeviceButton = new Button("✎");
        this.deleteDeviceButton = new Button("Delete device");
        this.editDeviceButton.setTooltip(new Tooltip("Edit device"));
        this.addDeviceButton.getStyleClass().add("success-button");
        this.deleteDeviceButton.getStyleClass().add("danger-button");

        initialize();
    }

    private void initialize() {
        root.setLeft(createDevicesListCard());
        root.setCenter(createDeviceEditorCard());
        root.setPadding(new Insets(0));
    }

    private Parent createDevicesListCard() {
        Label titleLabel = new Label("Devices");
        titleLabel.getStyleClass().add("content-title");
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label subtitleLabel = new Label("Manage the smart devices in your home.");
        subtitleLabel.getStyleClass().add("content-subtitle");
        subtitleLabel.setWrapText(true);

        devicesList.getStyleClass().add("navigation-list");
        VBox.setVgrow(devicesList, Priority.ALWAYS);

        VBox listCard = new VBox(12, titleLabel, subtitleLabel, devicesList);
        listCard.setPadding(new Insets(32));
        listCard.setPrefWidth(320);
        listCard.getStyleClass().add("content-card");

        return listCard;
    }

    private Parent createDeviceEditorCard() {
        Label titleLabel = new Label("Device Details");
        titleLabel.getStyleClass().add("content-title");
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);

        Label subtitleLabel = new Label("Create a device, assign a type and connect it to a room.");
        subtitleLabel.getStyleClass().add("content-subtitle");
        subtitleLabel.setWrapText(true);

        Label nameLabel = new Label("Device name");
        Label typeLabel = new Label("Device type");
        Label roomLabel = new Label("Assigned room");
        Label stateLabel = new Label("Current state");

        deviceNameField.setPromptText("Enter device name");
        deviceNameField.getStyleClass().add("form-input");
        deviceNameField.setDisable(true);
        HBox.setHgrow(deviceNameField, Priority.ALWAYS);

        deviceTypeBox.setDisable(true);
        deviceTypeBox.setMaxWidth(Double.MAX_VALUE);
        deviceTypeBox.getStyleClass().add("form-input");

        roomBox.setDisable(true);
        roomBox.setMaxWidth(Double.MAX_VALUE);
        roomBox.getStyleClass().add("form-input");

        deviceStateValueLabel.getStyleClass().add("content-subtitle");
        deviceStateValueLabel.getStyleClass().add("state-value");
        deviceStateValueLabel.setWrapText(true);

        HBox deviceNameBar = new HBox(10, deviceNameField, editDeviceButton);
        Region buttonSpacer = new Region();
        HBox.setHgrow(buttonSpacer, Priority.ALWAYS);
        HBox buttonBar = new HBox(10, addDeviceButton, buttonSpacer, deleteDeviceButton);

        VBox editorCard = new VBox(12, titleLabel, subtitleLabel, nameLabel, deviceNameBar, typeLabel, deviceTypeBox, roomLabel, roomBox, stateLabel, deviceStateValueLabel, buttonBar);
        editorCard.setPadding(new Insets(32));
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
     * Returns the device list.
     *
     * @return the device list
     */
    public ListView<DeviceDefinition> getDevicesList() {
        return devicesList;
    }

    /**
     * Returns the device name input field.
     *
     * @return the device name field
     */
    public TextField getDeviceNameField() {
        return deviceNameField;
    }

    /**
     * Returns the device type selector.
     *
     * @return the device type combo box
     */
    public ComboBox<DevicePlugin> getDeviceTypeBox() {
        return deviceTypeBox;
    }

    /**
     * Returns the room selector.
     *
     * @return the room combo box
     */
    public ComboBox<Room> getRoomBox() {
        return roomBox;
    }

    /**
     * Returns the current-state label.
     *
     * @return the state label
     */
    public Label getDeviceStateValueLabel() {
        return deviceStateValueLabel;
    }

    /**
     * Returns the add-device button.
     *
     * @return the add button
     */
    public Button getAddDeviceButton() {
        return addDeviceButton;
    }

    /**
     * Returns the edit-device button.
     *
     * @return the edit button
     */
    public Button getEditDeviceButton() {
        return editDeviceButton;
    }

    /**
     * Returns the delete-device button.
     *
     * @return the delete button
     */
    public Button getDeleteDeviceButton() {
        return deleteDeviceButton;
    }
}
