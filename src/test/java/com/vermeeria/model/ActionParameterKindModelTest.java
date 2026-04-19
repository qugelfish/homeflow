package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ActionParameterKind}.
 *
 * @author Jette
 */
class ActionParameterKindModelTest extends AbstractModelTestHelper {

    @Test
    void testContainsExpectedValues() {
        List<ActionParameterKind> kinds = List.of(ActionParameterKind.values());

        assertTrue(kinds.contains(ActionParameterKind.NONE));
        assertTrue(kinds.contains(ActionParameterKind.BOOLEAN));
        assertTrue(kinds.contains(ActionParameterKind.SELECTION));
        assertTrue(kinds.contains(ActionParameterKind.PERCENTAGE));
        assertTrue(kinds.contains(ActionParameterKind.TEMPERATURE));
        assertFalse(kinds.isEmpty());
    }
}
