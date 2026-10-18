package omtteam.openmodularturrets.core.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessLevelTest {
    @Test
    void eachLevelIncludesTheLowerOnes() {
        for (AccessLevel level : AccessLevel.values()) {
            for (AccessLevel other : AccessLevel.values()) {
                assertEquals(level.ordinal() >= other.ordinal(), level.isAtLeast(other), level + " vs " + other);
            }
        }
        assertTrue(AccessLevel.ADMIN.isAtLeast(AccessLevel.CHANGE_SETTINGS));
        assertFalse(AccessLevel.OPEN_GUI.isAtLeast(AccessLevel.CHANGE_SETTINGS));
    }

    @Test
    void serializedNamesRoundTrip() {
        for (AccessLevel level : AccessLevel.values()) {
            assertEquals(level, AccessLevel.bySerializedName(level.getSerializedName(), AccessLevel.NONE));
        }
        assertEquals("change_settings", AccessLevel.CHANGE_SETTINGS.getSerializedName());
    }

    @Test
    void unknownNamesFallBack() {
        assertEquals(AccessLevel.NONE, AccessLevel.bySerializedName("", AccessLevel.NONE));
        assertEquals(AccessLevel.OPEN_GUI, AccessLevel.bySerializedName("ADMIN", AccessLevel.OPEN_GUI),
                "names are case-sensitive");
        assertEquals(AccessLevel.NONE, AccessLevel.bySerializedName("owner", AccessLevel.NONE));
    }
}
