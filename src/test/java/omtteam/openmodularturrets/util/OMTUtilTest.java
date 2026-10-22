package omtteam.openmodularturrets.util;

import net.minecraft.world.World;
import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.OwnerShareRegister;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.config.OMConfig;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OMTUtilTest {
    private static Player playerTrustedNone;
    private static Player playerUntrusted;
    private static Player playerOwner;
    private static Player playerShareOwner;
    private static TurretBase base;
    private static TrustedPlayer trustedPlayerNone;
    private static World world;

    @BeforeAll
    static void setUp() {
        OMConfig.GENERAL.offlineModeSupport = false;
        playerTrustedNone = new Player(UUID.randomUUID(), "playerNone", "abc");
        playerUntrusted = new Player(UUID.randomUUID(), "playerUntrusted", "cde");
        playerShareOwner = new Player(UUID.randomUUID(), "playerShareOwner", "def");
        playerOwner = new Player(UUID.randomUUID(), "owner", "test");
        base = new TurretBase(50000, 5000, 5, null);
        base.setOwner(playerOwner);
        trustedPlayerNone = new TrustedPlayer(playerTrustedNone);
        trustedPlayerNone.setAccessLevel(EnumAccessLevel.NONE);
        base.getTrustManager().addTrustedPlayer(trustedPlayerNone);
        OwnerShareRegister.instance.addSharePlayer(playerOwner, playerShareOwner, null);
    }

    void setupWorld() {

    }

    void setOfflineMode() {
        OMConfig.GENERAL.offlineModeSupport = true;
        playerTrustedNone = new Player(UUID.randomUUID(), "playerNone", "abc");
        playerUntrusted = new Player(UUID.randomUUID(), "playerUntrusted", "cdf");
        base = new TurretBase(50000, 5000, 5, null);
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
        assertFalse(OMTUtil.canDamagePlayer(playerOwner, base));
        assertFalse(OMTUtil.canDamagePlayer(playerShareOwner, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = false;
        assertFalse(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = true;
        assertTrue(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
        setOfflineMode();
        assertFalse(OMTUtil.canDamagePlayer(playerOwner, base));
        assertFalse(OMTUtil.canDamagePlayer(playerShareOwner, base));
        assertTrue(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = false;
        assertFalse(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
    }

    @org.junit.jupiter.api.Test
    void canDamageEntity() {
        /*EntityCreeper creeper = new EntityCreeper(world);
        EntityZombie zombie = new EntityZombie(world);
        EntityWolf wolf = new EntityWolf(world);
        EntityCow cow = new EntityCow(world);
        base.setAttacksMobs(true);
        base.setAttacksNeutrals(false);
        assertTrue(OMTUtil.canDamageEntity(creeper, base));
        assertTrue(OMTUtil.canDamageEntity(zombie, base));
        assertFalse(OMTUtil.canDamageEntity(wolf, base));
        assertFalse(OMTUtil.canDamageEntity(cow, base));
        base.setAttacksMobs(false);
        base.setAttacksNeutrals(true);
        assertFalse(OMTUtil.canDamageEntity(creeper, base));
        assertFalse(OMTUtil.canDamageEntity(zombie, base));
        assertTrue(OMTUtil.canDamageEntity(wolf, base));
        assertTrue(OMTUtil.canDamageEntity(cow, base));*/ //TODO: add world simulation
    }
}