package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link ScenarioAction}.
 *
 * @author Jette
 */
class ScenarioActionModelTest extends AbstractModelTestHelper {

    @Test
    void testBeanPropertiesRoundTrip() throws Exception {
        assertBeanPropertiesRoundTrip(ScenarioAction.class, Map.of("deviceId", "device-1", "actionKey", "turnOn", "parameterValue", "25"));
    }

    @Test
    void testGeneratedIdPrefix() throws Exception {
        assertIdPrefix(ScenarioAction.class, "scenario-action-");
    }

    @Test
    void testToStringIncludesParameterValueWhenPresent() {
        ScenarioAction scenarioAction = new ScenarioAction("device-1", "setBrightness", "25");

        assertEquals("setBrightness (25)", scenarioAction.toString());
    }
}
