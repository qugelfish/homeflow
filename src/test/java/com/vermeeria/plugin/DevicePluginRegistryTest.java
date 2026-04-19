package com.vermeeria.plugin;

import com.vermeeria.service.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link DevicePluginRegistry}.
 *
 * @author Jette
 */
class DevicePluginRegistryTest {

    @Test
    void testGetAllPlugins_ReturnsAllDiscoveredPluginsSortedByDisplayName() {
        DevicePluginRegistry registry = new DevicePluginRegistry();

        List<DevicePlugin> plugins = registry.getAllPlugins();

        assertEquals(3, plugins.size());
        assertEquals(List.of("Blind", "Heater", "Lamp"), plugins.stream().map(DevicePlugin::getDisplayName).toList());
        assertEquals(List.of("blind", "heater", "lamp"), plugins.stream().map(DevicePlugin::getTypeKey).toList());
    }

    @Test
    void testGetPlugin_ReturnsBlindPluginForBlindType() {
        DevicePluginRegistry registry = new DevicePluginRegistry();

        DevicePlugin plugin = registry.getPlugin("blind");

        assertInstanceOf(BlindPlugin.class, plugin);
        assertEquals("Blind", plugin.getDisplayName());
    }

    @Test
    void testGetPlugin_ReturnsHeaterPluginForHeaterType() {
        DevicePluginRegistry registry = new DevicePluginRegistry();

        DevicePlugin plugin = registry.getPlugin("heater");

        assertInstanceOf(HeaterPlugin.class, plugin);
        assertEquals("Heater", plugin.getDisplayName());
    }

    @Test
    void testGetPlugin_ReturnsLampPluginForLampType() {
        DevicePluginRegistry registry = new DevicePluginRegistry();

        DevicePlugin plugin = registry.getPlugin("lamp");

        assertInstanceOf(LampPlugin.class, plugin);
        assertEquals("Lamp", plugin.getDisplayName());
    }

    @Test
    void testHasPlugin_ReturnsTrueForKnownTypesAndFalseForUnknownType() {
        DevicePluginRegistry registry = new DevicePluginRegistry();

        assertTrue(registry.hasPlugin("blind"));
        assertTrue(registry.hasPlugin("heater"));
        assertTrue(registry.hasPlugin("lamp"));
        assertFalse(registry.hasPlugin("speaker"));
    }

    @Test
    void testGetPlugin_ThrowsValidationException_ForUnknownType() {
        DevicePluginRegistry registry = new DevicePluginRegistry();

        ValidationException exception = assertThrows(ValidationException.class, () -> registry.getPlugin("speaker"));

        assertEquals("Unknown device type: speaker", exception.getMessage());
    }
}
