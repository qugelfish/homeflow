package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link Scenario}.
 *
 * @author Jette
 */
class ScenarioModelTest extends AbstractModelTestHelper {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 4, 19, 12, 0);

    @Test
    void testBeanPropertiesRoundTrip() throws Exception {
        assertBeanPropertiesRoundTrip(Scenario.class, Map.of("name", "Evening Routine", "description", "Turns devices on", "createdAt", FIXED_TIME));
    }

    @Test
    void testGeneratedIdPrefix() throws Exception {
        assertIdPrefix(Scenario.class, "scenario-");
    }

    @Test
    void testSetActionsDefensivelyCopiesList() throws Exception {
        assertListSetterDefensivelyCopies(Scenario.class, "actions", List.of(new ScenarioAction("device-1", "turnOn", null)), new ScenarioAction("device-2", "turnOff", null));
    }

    @Test
    void testDefaultConstructorInitializesActions() {
        Scenario scenario = new Scenario();

        assertNotNull(scenario.getActions());
    }

    @Test
    void testToStringUsesScenarioName() {
        Scenario scenario = new Scenario("Evening Routine", "Description");

        assertEquals("Evening Routine", scenario.toString());
    }
}
