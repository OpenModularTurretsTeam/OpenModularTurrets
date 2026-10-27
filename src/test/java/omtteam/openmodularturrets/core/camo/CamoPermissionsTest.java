package omtteam.openmodularturrets.core.camo;

import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.AccessProfile;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.core.security.SecurityEnvironment;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.core.security.TrustResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CamoPermissionsTest {
    private static final PlayerInfo OWNER = player("Owner");
    private static final PlayerInfo ADMIN = player("Admin");
    private static final PlayerInfo SETTINGS = player("Settings");
    private static final PlayerInfo TRUSTED = player("Trusted");
    private static final PlayerInfo STRANGER = player("Stranger");

    private static final SecurityEnvironment NO_TEAMS = new SecurityEnvironment() {
        @Override
        public Optional<String> getTeam(UUID uuid, String name) {
            return Optional.empty();
        }

        @Override
        public boolean operatorsCanAccessOwnedBlocks() {
            return false;
        }

        @Override
        public boolean damageTrustedPlayers() {
            return false;
        }
    };

    private TrustManager base;
    private TrustManager trap;

    private static PlayerInfo player(String name) {
        return new PlayerInfo(UUID.nameUUIDFromBytes(name.getBytes()), name, false);
    }

    private static void trust(TrustManager manager, PlayerInfo player, AccessLevel level) {
        assertEquals(TrustResult.SUCCESS, manager.addTrustedPlayer(OWNER, player.identity()));
        assertEquals(TrustResult.SUCCESS, manager.setAccessLevel(OWNER, player.uuid(), level));
    }

    @BeforeEach
    void setUp() {
        base = new TrustManager(NO_TEAMS, () -> { });
        base.setOwner(OWNER.identity());
        trust(base, ADMIN, AccessLevel.ADMIN);
        trust(base, SETTINGS, AccessLevel.CHANGE_SETTINGS);
        trap = new TrustManager(NO_TEAMS, () -> { }, AccessProfile.TRUSTED_AND_ADMIN);
        trap.setOwner(OWNER.identity());
        trust(trap, ADMIN, AccessLevel.ADMIN);
        trust(trap, TRUSTED, AccessLevel.NONE);
    }

    @Test
    void adminsSetTheDisguiseOfABase() {
        assertTrue(CamoPermissions.canSetDisguise(base, OWNER));
        assertTrue(CamoPermissions.canSetDisguise(base, ADMIN));
        assertFalse(CamoPermissions.canSetDisguise(base, SETTINGS));
        assertFalse(CamoPermissions.canSetDisguise(base, STRANGER));
    }

    @Test
    void adminsChangeTheLightOfABase() {
        assertTrue(CamoPermissions.canChangeLight(base, OWNER));
        assertTrue(CamoPermissions.canChangeLight(base, ADMIN));
        assertFalse(CamoPermissions.canChangeLight(base, SETTINGS));
        assertFalse(CamoPermissions.canChangeLight(base, STRANGER));
    }

    @Test
    void trapAdminsManageTheTrapAndTrustedPlayersDoNot() {
        assertTrue(CamoPermissions.canManageTrap(trap, OWNER));
        assertTrue(CamoPermissions.canManageTrap(trap, ADMIN));
        assertFalse(CamoPermissions.canManageTrap(trap, TRUSTED));
        assertFalse(CamoPermissions.canManageTrap(trap, STRANGER));
    }
}
