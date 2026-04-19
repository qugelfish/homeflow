package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link DeviceDefinition}.
 *
 * @author Jette
 */
class DeviceDefinitionModelTest extends AbstractModelTestHelper {

    @Test
    void testBeanPropertiesRoundTrip() throws Exception {
        assertBeanPropertiesRoundTrip(DeviceDefinition.class, Map.of("name", "Floor Lamp", "typeKey", "lamp", "roomId", "room-1"));
    }

    @Test
    void testGeneratedIdPrefix() throws Exception {
        assertIdPrefix(DeviceDefinition.class, "device-");
    }

    @Test
    void testCurrentStateSetterDefensivelyCopiesMap() throws Exception {
        assertMapSetterDefensivelyCopies(DeviceDefinition.class, "currentState", Map.of("power", true, "brightness", 50), "color", "WHITE");
    }

    @Test
    void testDefaultConstructorInitializesCurrentState() {
        DeviceDefinition deviceDefinition = new DeviceDefinition();

        assertNotNull(deviceDefinition.getCurrentState());
    }

    @Test
    void testToStringUsesDeviceName() {
        DeviceDefinition deviceDefinition = new DeviceDefinition("Floor Lamp", "lamp", "room-1");

        assertEquals("Floor Lamp", deviceDefinition.toString());
    }
}
