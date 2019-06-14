package omtteam.openmodularturrets.util;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;
import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.handler.OMConfig;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.TurretBase;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OMTUtilTest {
    private Player playerTrustedNone;
    private Player playerUntrusted;
    private Player playerTeam;
    private Player owner;
    private TurretBase base;
    private TrustedPlayer trustedPlayerNone;
    private World world;

    void setOfflineMode() {
        OMConfig.GENERAL.offlineModeSupport = true;
        playerTrustedNone = new Player(UUID.randomUUID(), "playerNone", "abc");
        playerUntrusted = new Player(UUID.randomUUID(), "playerUntrusted", "cdf");
        playerTeam = new Player(UUID.randomUUID(), "playerTeam", "test");
        base = new TurretBase(50000, 5000, 5, null);
        base.setOwner(owner);
        trustedPlayerNone = new TrustedPlayer(playerTrustedNone);
        trustedPlayerNone.setAccessLevel(EnumAccessLevel.NONE);
        base.getTrustManager().addTrustedPlayer(trustedPlayerNone);
    }

    void setupWorld() {

    }

    @BeforeEach
    void setUp() {
        OMConfig.GENERAL.offlineModeSupport = false;
        playerTrustedNone = new Player(UUID.randomUUID(), "playerNone", "abc");
        playerUntrusted = new Player(UUID.randomUUID(), "playerUntrusted", "cdf");
        playerTeam = new Player(UUID.randomUUID(), "playerTeam", "test");
        owner = new Player(UUID.randomUUID(), "owner", "test");
        base = new TurretBase(50000, 5000, 5, null);
        base.setOwner(owner);
        trustedPlayerNone = new TrustedPlayer(playerTrustedNone);
        trustedPlayerNone.setAccessLevel(EnumAccessLevel.NONE);
        base.getTrustManager().addTrustedPlayer(trustedPlayerNone);
    }

    @org.junit.jupiter.api.Test
    void canDamagePlayer() {
        OMTConfig.TURRETS.turretDamageTrustedPlayers = false;
        assertFalse(OMTUtil.canDamagePlayer(owner, base));
        assertFalse(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertFalse(OMTUtil.canDamagePlayer(playerTeam, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = true;
        assertFalse(OMTUtil.canDamagePlayer(owner, base));
        assertTrue(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertFalse(OMTUtil.canDamagePlayer(playerTeam, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
        setOfflineMode();
        assertFalse(OMTUtil.canDamagePlayer(owner, base));
        assertTrue(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertFalse(OMTUtil.canDamagePlayer(playerTeam, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
        OMTConfig.TURRETS.turretDamageTrustedPlayers = false;
        assertFalse(OMTUtil.canDamagePlayer(owner, base));
        assertFalse(OMTUtil.canDamagePlayer(playerTrustedNone, base));
        assertFalse(OMTUtil.canDamagePlayer(playerTeam, base));
        assertTrue(OMTUtil.canDamagePlayer(playerUntrusted, base));
    }

    @org.junit.jupiter.api.Test
    void canDamageEntity() {
        base.setAttacksMobs(true);
        base.setAttacksNeutrals(false);
        EntityCreeper creeper = new EntityCreeper(world);
        EntityZombie zombie = new EntityZombie(world);
        EntityWolf wolf = new EntityWolf(world);
    }
}