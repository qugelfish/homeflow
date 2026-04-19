package com.vermeeria.service;

import com.vermeeria.model.ActionSpec;
import com.vermeeria.model.DeviceDefinition;
import com.vermeeria.model.Room;
import com.vermeeria.model.ScenarioAction;
import com.vermeeria.plugin.DevicePlugin;
import com.vermeeria.plugin.DevicePluginRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides device-related business logic.
 *
 * @author Jette
 */
public class DeviceService {

    private final SmartHomeService smartHomeService;
    private final DevicePluginRegistry pluginRegistry;

    /**
     * Creates a device service backed by the shared application state.
     *
     * @param smartHomeService the shared smart home service
     */
    public DeviceService(final SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
        this.pluginRegistry = new DevicePluginRegistry();
    }

    /**
     * Returns all configured devices sorted by name.
     *
     * @return the configured devices
     */
    public List<DeviceDefinition> getAllDevices() {
        return smartHomeService.getAppData().getDevices().stream().sorted((firstDevice, secondDevice) -> firstDevice.getName().compareToIgnoreCase(secondDevice.getName())).toList();
    }

    /**
     * Returns all available device plugins sorted by display name.
     *
     * @return the available device plugins
     */
    public List<DevicePlugin> getAvailableDevicePlugins() {
        return pluginRegistry.getAllPlugins();
    }

    /**
     * Returns all available rooms sorted by name.
     *
     * @return the available rooms
     */
    public List<Room> getAvailableRooms() {
        return smartHomeService.getAppData().getRooms().stream().sorted((firstRoom, secondRoom) -> firstRoom.getName().compareToIgnoreCase(secondRoom.getName())).toList();
    }

    /**
     * Creates a new configured device.
     *
     * @param deviceName the device name
     * @param typeKey    the selected device type
     * @param roomId     the selected room identifier
     */
    public void createDevice(final String deviceName, final String typeKey, final String roomId) {
        String normalizedName = validateAndNormalizeDeviceName(deviceName);
        DevicePlugin plugin = validateAndGetPlugin(typeKey);
        validateRoomExists(roomId);
        validateUniqueDeviceName(normalizedName, null);

        DeviceDefinition device = new DeviceDefinition(normalizedName, plugin.getTypeKey(), roomId);
        device.setCurrentState(new LinkedHashMap<>(plugin.createDefaultState()));
        smartHomeService.getAppData().addDevice(device);
        smartHomeService.saveAll();
    }

    /**
     * Updates an existing device.
     *
     * @param deviceId   the device identifier
     * @param deviceName the updated device name
     * @param typeKey    the selected device type
     * @param roomId     the selected room identifier
     */
    public void updateDevice(final String deviceId, final String deviceName, final String typeKey, final String roomId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new ValidationException("Please select a device to edit.");
        }

        String normalizedName = validateAndNormalizeDeviceName(deviceName);
        DevicePlugin plugin = validateAndGetPlugin(typeKey);
        validateRoomExists(roomId);
        validateUniqueDeviceName(normalizedName, deviceId);

        DeviceDefinition deviceToUpdate = smartHomeService.getAppData().getDevices().stream().filter(device -> deviceId.equals(device.getId())).findFirst().orElseThrow(() -> new ValidationException("The selected device no longer exists."));

        boolean typeChanged = !plugin.getTypeKey().equals(deviceToUpdate.getTypeKey());

        deviceToUpdate.setName(normalizedName);
        deviceToUpdate.setTypeKey(plugin.getTypeKey());
        deviceToUpdate.setRoomId(roomId);

        if (typeChanged) {
            deviceToUpdate.setCurrentState(new LinkedHashMap<>(plugin.createDefaultState()));
        }

        smartHomeService.saveAll();
    }

    /**
     * Deletes an existing device.
     *
     * @param deviceId the device identifier
     */
    public void deleteDevice(final String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new ValidationException("Please select a device to delete.");
        }

        DeviceDefinition deviceToDelete = smartHomeService.getAppData().getDevices().stream().filter(device -> deviceId.equals(device.getId())).findFirst().orElseThrow(() -> new ValidationException("The selected device no longer exists."));

        smartHomeService.getAppData().removeDevice(deviceToDelete);
        smartHomeService.saveAll();
    }

    /**
     * Returns a readable label for a device type.
     *
     * @param typeKey the device type key
     * @return the display name
     */
    public String getDeviceTypeDisplayName(final String typeKey) {
        return pluginRegistry.getPlugin(typeKey).getDisplayName();
    }

    /**
     * Returns the configured device for the given identifier.
     *
     * @param deviceId the device identifier
     * @return the configured device or null
     */
    public DeviceDefinition getDeviceById(final String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return null;
        }

        return smartHomeService.getAppData().getDevices().stream().filter(device -> deviceId.equals(device.getId())).findFirst().orElse(null);
    }

    /**
     * Returns the supported action specifications for the given device.
     *
     * @param deviceId the device identifier
     * @return the supported action specifications
     */
    public List<ActionSpec> getSupportedActionsForDevice(final String deviceId) {
        DeviceDefinition device = getDeviceById(deviceId);
        if (device == null) {
            return List.of();
        }

        return pluginRegistry.getPlugin(device.getTypeKey()).supportedActions();
    }

    /**
     * Formats a scenario action for UI lists.
     *
     * @param action the scenario action
     * @return the formatted action label
     */
    public String formatScenarioAction(final ScenarioAction action) {
        if (action == null) {
            return "";
        }

        DeviceDefinition device = getDeviceById(action.getDeviceId());
        String deviceName = device == null ? "Unknown device" : device.getName();
        ActionSpec actionSpec = findActionSpec(action.getDeviceId(), action.getActionKey());
        String actionLabel = actionSpec == null ? action.getActionKey() : actionSpec.label();

        if (action.getParameterValue() == null || action.getParameterValue().isBlank()) {
            return deviceName + " - " + actionLabel;
        }

        return deviceName + " - " + actionLabel + ": " + formatStateValue(action.getActionKey(), action.getParameterValue());
    }

    /**
     * Formats the current state of a configured device.
     *
     * @param device the configured device
     * @return the readable state text
     */
    public String formatDeviceState(final DeviceDefinition device) {
        if (device == null) {
            return "No device selected";
        }
        return pluginRegistry.getPlugin(device.getTypeKey()).formatState(device.getCurrentState());
    }

    /**
     * Formats the device state with one property per line for the details view.
     *
     * @param device the configured device
     * @return the formatted multi-line state text
     */
    public String formatDeviceStateDetails(final DeviceDefinition device) {
        if (device == null || device.getCurrentState() == null || device.getCurrentState().isEmpty()) {
            return "No device selected";
        }

        return device.getCurrentState().entrySet().stream().map(this::formatStateEntry).reduce((firstLine, secondLine) -> firstLine + "\n" + secondLine).orElse("No device selected");
    }

    /**
     * Applies a scenario action to the addressed device and returns a readable log line.
     *
     * @param action the scenario action to execute
     * @return the formatted execution message
     */
    public String executeScenarioAction(final ScenarioAction action) {
        if (action == null) {
            throw new ValidationException("The scenario action is invalid.");
        }

        DeviceDefinition device = getDeviceById(action.getDeviceId());
        if (device == null) {
            throw new ValidationException("The target device of this action no longer exists.");
        }

        ActionSpec actionSpec = findActionSpec(action.getDeviceId(), action.getActionKey());
        if (actionSpec == null) {
            throw new ValidationException("The selected device does not support this action anymore.");
        }

        DevicePlugin plugin = pluginRegistry.getPlugin(device.getTypeKey());
        Map<String, Object> updatedState = plugin.applyAction(
                device.getCurrentState(),
                action.getActionKey(),
                action.getParameterValue()
        );
        device.setCurrentState(new LinkedHashMap<>(updatedState));

        return formatScenarioExecutionAction(action, device);
    }

    private String validateAndNormalizeDeviceName(final String deviceName) {
        if (deviceName == null || deviceName.isBlank()) {
            throw new ValidationException("Device name cannot be empty.");
        }
        return deviceName.trim();
    }

    private DevicePlugin validateAndGetPlugin(final String typeKey) {
        if (typeKey == null || typeKey.isBlank()) {
            throw new ValidationException("Please select a device type.");
        }
        return pluginRegistry.getPlugin(typeKey);
    }

    private void validateRoomExists(final String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new ValidationException("Please select a room.");
        }

        boolean roomExists = smartHomeService.getAppData().getRooms().stream().anyMatch(room -> roomId.equals(room.getId()));

        if (!roomExists) {
            throw new ValidationException("The selected room no longer exists.");
        }
    }

    private void validateUniqueDeviceName(final String deviceName, final String currentDeviceId) {
        boolean duplicateNameExists = smartHomeService.getAppData().getDevices().stream().filter(device -> currentDeviceId == null || !currentDeviceId.equals(device.getId())).map(DeviceDefinition::getName).anyMatch(existingName -> existingName.equalsIgnoreCase(deviceName));

        if (duplicateNameExists) {
            throw new ValidationException("A device with this name already exists.");
        }
    }

    private ActionSpec findActionSpec(final String deviceId, final String actionKey) {
        return getSupportedActionsForDevice(deviceId).stream().filter(actionSpec -> actionSpec.actionKey().equals(actionKey)).findFirst().orElse(null);
    }

    private String formatScenarioExecutionAction(final ScenarioAction action,
                                                 final DeviceDefinition device) {
        String baseLabel = formatScenarioAction(action);
        String roomName = getRoomNameById(device == null ? null : device.getRoomId());
        if (roomName == null || roomName.isBlank()) {
            return baseLabel;
        }
        return roomName + " - " + baseLabel;
    }

    private String getRoomNameById(final String roomId) {
        if (roomId == null || roomId.isBlank()) {
            return null;
        }

        return smartHomeService.getAppData().getRooms().stream()
                .filter(room -> roomId.equals(room.getId()))
                .map(Room::getName)
                .findFirst()
                .orElse(null);
    }

    private String formatStateEntry(final Map.Entry<String, Object> entry) {
        String label = prettifyKey(entry.getKey());
        String value = formatStateValue(entry.getKey(), entry.getValue());
        return label + ": " + value;
    }

    private String prettifyKey(final String key) {
        if (key == null || key.isBlank()) {
            return "Value";
        }

        String withSpaces = key.replaceAll("([a-z])([A-Z])", "$1 $2");
        String normalized = withSpaces.replace('_', ' ').trim();
        if (normalized.isEmpty()) {
            return "Value";
        }

        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String formatStateValue(final String key, final Object value) {
        if ("brightness".equals(key) || "position".equals(key) || "setBrightness".equals(key) || "setPosition".equals(key)) {
            return value + "%";
        }
        if ("power".equals(key) && value instanceof Boolean power) {
            return power ? "On" : "Off";
        }
        if ("targetTemperature".equals(key) || "setTargetTemperature".equals(key)) {
            return value + " °C";
        }
        if (("color".equals(key) || "setColor".equals(key)) && value != null) {
            return prettifyColorValue(String.valueOf(value));
        }
        return String.valueOf(value);
    }

    private String prettifyColorValue(final String colorValue) {
        String normalized = colorValue.replace('_', ' ').trim().toLowerCase();
        if (normalized.isEmpty()) {
            return colorValue;
        }

        String[] words = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < words.length; index++) {
            String word = words[index];
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            if (index < words.length - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }
}
