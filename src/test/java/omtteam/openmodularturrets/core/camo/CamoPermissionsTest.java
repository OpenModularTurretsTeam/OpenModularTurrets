package omtteam.openmodularturrets.core.camo;

import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.SecurityEnvironment;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.security.TrustResult;
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
    private static final PlayerInfo STRANGER = player("Stranger");

    private TrustManager trust;

    private static PlayerInfo player(String name) {
        return new PlayerInfo(UUID.nameUUIDFromBytes(name.getBytes()), name, false);
    }

    @BeforeEach
    void setUp() {
        trust = new TrustManager(new SecurityEnvironment() {
            @Override
            public Optional<String> getTeam(UUID uuid, String name) {
                return Optional.empty();
            }

            @Override
            public boolean operatorsCanAccessOwnedBlocks() {
                return false;
            }

            @Override
            public boolean turretsDamageTrustedPlayers() {
                return false;
            }
        }, () -> { });
        trust.setOwner(OWNER.identity());
        assertEquals(TrustResult.SUCCESS, trust.addTrustedPlayer(OWNER, ADMIN.identity()));
        assertEquals(TrustResult.SUCCESS, trust.setAccessLevel(OWNER, ADMIN.uuid(), AccessLevel.ADMIN));
        assertEquals(TrustResult.SUCCESS, trust.addTrustedPlayer(OWNER, SETTINGS.identity()));
        assertEquals(TrustResult.SUCCESS, trust.setAccessLevel(OWNER, SETTINGS.uuid(), AccessLevel.CHANGE_SETTINGS));
    }

    @Test
    void onlyTheOwnerSetsTheDisguise() {
        assertTrue(CamoPermissions.canSetDisguise(trust, OWNER));
        assertFalse(CamoPermissions.canSetDisguise(trust, ADMIN));
        assertFalse(CamoPermissions.canSetDisguise(trust, SETTINGS));
        assertFalse(CamoPermissions.canSetDisguise(trust, STRANGER));
    }

    @Test
    void adminsChangeTheLight() {
        assertTrue(CamoPermissions.canChangeLight(trust, OWNER));
        assertTrue(CamoPermissions.canChangeLight(trust, ADMIN));
        assertFalse(CamoPermissions.canChangeLight(trust, SETTINGS));
        assertFalse(CamoPermissions.canChangeLight(trust, STRANGER));
    }

    @Test
    void blocksWithOnlyAnOwnerAreOwnerOnly() {
        assertTrue(CamoPermissions.canSetDisguise(OWNER.identity(), OWNER));
        assertTrue(CamoPermissions.canChangeLight(OWNER.identity(), OWNER));
        assertFalse(CamoPermissions.canSetDisguise(OWNER.identity(), STRANGER));
        assertFalse(CamoPermissions.canChangeLight(OWNER.identity(), STRANGER));
        assertFalse(CamoPermissions.canSetDisguise((PlayerIdentity) null, OWNER));
    }
}
