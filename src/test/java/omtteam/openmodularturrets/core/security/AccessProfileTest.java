package omtteam.openmodularturrets.core.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessProfileTest {
    private static final AccessProfile TRAP = AccessProfile.TRUSTED_AND_ADMIN;

    @Test
    void allKnowsEveryLevelInOrder() {
        assertEquals(List.of(AccessLevel.values()), AccessProfile.ALL.levels());
    }

    @Test
    void trapProfileHasTrustedAndAdminOnly() {
        assertEquals(List.of(AccessLevel.NONE, AccessLevel.ADMIN), TRAP.levels());
        assertTrue(TRAP.allows(AccessLevel.NONE));
        assertTrue(TRAP.allows(AccessLevel.ADMIN));
        assertFalse(TRAP.allows(AccessLevel.OPEN_GUI));
        assertFalse(TRAP.allows(AccessLevel.CHANGE_SETTINGS));
    }

    @Test
    void clampingGoesDownToTheNearestAllowedLevel() {
        assertEquals(AccessLevel.NONE, TRAP.clamp(AccessLevel.NONE));
        assertEquals(AccessLevel.NONE, TRAP.clamp(AccessLevel.OPEN_GUI));
        assertEquals(AccessLevel.NONE, TRAP.clamp(AccessLevel.CHANGE_SETTINGS));
        assertEquals(AccessLevel.ADMIN, TRAP.clamp(AccessLevel.ADMIN));
        for (AccessLevel level : AccessLevel.values()) {
            assertEquals(level, AccessProfile.ALL.clamp(level));
        }
    }

    @Test
    void steppingSkipsTheLevelsOfTheProfile() {
        assertEquals(AccessLevel.ADMIN, TRAP.step(AccessLevel.NONE, 1));
        assertEquals(AccessLevel.NONE, TRAP.step(AccessLevel.ADMIN, -1));
        assertNull(TRAP.step(AccessLevel.ADMIN, 1));
        assertNull(TRAP.step(AccessLevel.NONE, -1));
        assertEquals(AccessLevel.OPEN_GUI, AccessProfile.ALL.step(AccessLevel.NONE, 1));
        assertEquals(AccessLevel.ADMIN, AccessProfile.ALL.step(AccessLevel.CHANGE_SETTINGS, 1));
    }

    @Test
    void aProfileNeedsTheBaselineAndAdmin() {
        assertThrows(IllegalArgumentException.class, () -> AccessProfile.of(AccessLevel.ADMIN));
        assertThrows(IllegalArgumentException.class, () -> AccessProfile.of(AccessLevel.NONE, AccessLevel.OPEN_GUI));
        assertThrows(IllegalArgumentException.class, () -> new AccessProfile(List.of()));
    }

    /** The record's public constructor normalizes and validates like {@link AccessProfile#of}. */
    @Test
    void theConstructorSortsAndDeduplicates() {
        AccessProfile profile = new AccessProfile(List.of(AccessLevel.ADMIN, AccessLevel.NONE, AccessLevel.ADMIN));
        assertEquals(List.of(AccessLevel.NONE, AccessLevel.ADMIN), profile.levels());
        assertEquals(TRAP, profile);
        assertThrows(IllegalArgumentException.class, () -> new AccessProfile(List.of(AccessLevel.OPEN_GUI)));
    }
}
