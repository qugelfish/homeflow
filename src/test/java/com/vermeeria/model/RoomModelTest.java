package com.vermeeria.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link Room}.
 *
 * @author Jette
 */
class RoomModelTest extends AbstractModelTestHelper {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 4, 19, 12, 0);

    @Test
    void testBeanPropertiesRoundTrip() throws Exception {
        assertBeanPropertiesRoundTrip(Room.class, Map.of("name", "Living Room", "createdAt", FIXED_TIME));
    }

    @Test
    void testGeneratedIdPrefix() throws Exception {
        assertIdPrefix(Room.class, "room-");
    }

    @Test
    void testToStringUsesRoomName() {
        Room room = new Room("Living Room");

        assertEquals("Living Room", room.toString());
    }
}
