package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ExecutionLogEntry}.
 *
 * @author Jette
 */
class ExecutionLogEntryModelTest extends AbstractModelTestHelper {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 4, 19, 12, 0);

    @Test
    void testBeanPropertiesRoundTrip() throws Exception {
        assertBeanPropertiesRoundTrip(ExecutionLogEntry.class, Map.of("executedAt", FIXED_TIME, "scenarioId", "scenario-1", "scenarioName", "Evening Routine", "message", "Executed Living Room - Floor Lamp - Set brightness: 25%"));
    }

    @Test
    void testGeneratedIdPrefix() throws Exception {
        assertIdPrefix(ExecutionLogEntry.class, "log-");
    }

    @Test
    void testDefaultConstructorInitializesExecutionTimestamp() {
        ExecutionLogEntry logEntry = new ExecutionLogEntry();

        assertNotNull(logEntry.getExecutedAt());
    }

    @Test
    void testToStringContainsScenarioNameAndMessage() {
        ExecutionLogEntry logEntry = new ExecutionLogEntry("scenario-1", "Evening Routine", "Executed action");

        String label = logEntry.toString();

        assertTrue(label.contains("Evening Routine"));
        assertTrue(label.contains("Executed action"));
    }
}
