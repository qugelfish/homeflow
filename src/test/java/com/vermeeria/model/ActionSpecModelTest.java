package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link ActionSpec}.
 *
 * @author Jette
 */
class ActionSpecModelTest extends AbstractModelTestHelper {

    @Test
    void testConstructorNormalizesAllowedValues() {
        ActionSpec actionSpec = new ActionSpec("setColor", "Set color", ActionParameterKind.SELECTION, "Color", null);

        assertEquals(List.of(), actionSpec.allowedValues());
    }

    @Test
    void testToStringUsesLabel() {
        ActionSpec actionSpec = new ActionSpec("setColor", "Set color", ActionParameterKind.SELECTION, "Color");

        assertEquals("Set color", actionSpec.toString());
    }
}
