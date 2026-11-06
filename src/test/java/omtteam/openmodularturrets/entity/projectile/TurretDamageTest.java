package omtteam.openmodularturrets.entity.projectile;

import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.OwnerShareRegister;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.config.OMConfig;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurretDamageTest {
    private static Player playerTrustedNone;
    private static Player playerUntrusted;
    private static Player playerOwner;
    private static Player playerShareOwner;
    private static TurretBase base;
    private static TrustedPlayer trustedPlayerNone;
    /** The global config values the test changes, restored afterwards so other tests see the defaults. */
    private static boolean savedOfflineModeSupport;
    private static boolean savedTurretDamageTrustedPlayers;

    @BeforeAll
    static void setUp() {
        savedOfflineModeSupport = OMConfig.GENERAL.offlineModeSupport;
        savedTurretDamageTrustedPlayers = OMTConfig.TURRETS.turretDamageTrustedPlayers;
        OMConfig.GENERAL.offlineModeSupport = false;
        playerTrustedNone = new Player(UUID.randomUUID(), "playerNone", "abc");
        playerUntrusted = new Player(UUID.randomUUID(), "playerUntrusted", "cde");
        playerShareOwner = new Player(UUID.randomUUID(), "playerShareOwner", "def");
        playerOwner = new Player(UUID.randomUUID(), "owner", "test");
        base = new TurretBase(5, null);
        base.setOwner(playerOwner);
        trustedPlayerNone = new TrustedPlayer(playerTrustedNone);
        trustedPlayerNone.setAccessLevel(EnumAccessLevel.NONE);
        base.getTrustManager().addTrustedPlayer(trustedPlayerNone);
        OwnerShareRegister.instance.addSharePlayer(playerOwner, playerShareOwner, null);
    }

    @AfterAll
    static void tearDown() {
        OwnerShareRegister.instance.removeSharePlayer(playerOwner, playerShareOwner, null);
        OMConfig.GENERAL.offlineModeSupport = savedOfflineModeSupport;
        OMTConfig.TURRETS.turretDamageTrustedPlayers = savedTurretDamageTrustedPlayers;
    }

    void setOfflineMode() {
        OMConfig.GENERAL.offlineModeSupport = true;
        playerTrustedNone = new Player(UUID.randomUUID(), "playerNone", "abc");
        playerUntrusted = new Player(UUID.randomUUID(), "playerUntrusted", "cdf");
        base = new TurretBase(5, null);
        base.setOwner(playerOwner);
        trustedPlayerNone = new TrustedPlayer(playerTrustedNone);
        trustedPlayerNone.setAccessLevel(EnumAccessLevel.NONE);
        base.getTrustManager().addTrustedPlayer(trustedPlayerNone);
    }

    /**
     * Teams come from the server scoreboard ({@code OMTSecurityEnvironment}), which unit tests do not have; the team
     * rule is tested in {@code core.security.DamageRulesTest}.
     */
    @org.junit.jupiter.api.Test
    void canDamagePlayer() {
        assertFalse(TurretDamage.canDamagePlayer(playerOwner, base));
        assertFalse(TurretDamage.canDamagePlayer(playerShareOwner, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = false;
        assertFalse(TurretDamage.canDamagePlayer(playerTrustedNone, base));
        assertTrue(TurretDamage.canDamagePlayer(playerUntrusted, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = true;
        assertTrue(TurretDamage.canDamagePlayer(playerTrustedNone, base));
        assertTrue(TurretDamage.canDamagePlayer(playerUntrusted, base));
        setOfflineMode();
        assertFalse(TurretDamage.canDamagePlayer(playerOwner, base));
        assertFalse(TurretDamage.canDamagePlayer(playerShareOwner, base));
        assertTrue(TurretDamage.canDamagePlayer(playerTrustedNone, base));
        assertTrue(TurretDamage.canDamagePlayer(playerUntrusted, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = false;
        assertFalse(TurretDamage.canDamagePlayer(playerTrustedNone, base));
        assertTrue(TurretDamage.canDamagePlayer(playerUntrusted, base));
    }
}
