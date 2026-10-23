package omtteam.openmodularturrets.core.base;

import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.core.machine.MachineMode;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.SecurityEnvironment;
import omtteam.omlib.core.security.TrustManager;
import omtteam.openmodularturrets.core.card.BaseCardRules;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Neighbour;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseCardRulesTest {
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
        public boolean turretsDamageTrustedPlayers() {
            return false;
        }

        @Override
        public boolean isSharedOwner(PlayerIdentity owner, PlayerIdentity player) {
            return false;
        }

        @Override
        public boolean offlineModeSupport() {
            return false;
        }
    };

    private static TurretBaseLogic base(TurretType turret) {
        return base(turret, new FakeWorld());
    }

    private static TurretBaseLogic base(TurretType turret, FakeWorld world) {
        for (int i = 0; i < BaseTier.AMMO_SLOTS; i++) world.ammoSlots.add(FakeSlot.empty());
        TurretBaseLogic logic = new TurretBaseLogic(5, new FakeSettings(), new TrustManager(NO_TEAMS, () -> {
        }), world, new Random(1), changed -> {
        });
        Map<Face, Neighbour> neighbours = new EnumMap<>(Face.class);
        neighbours.put(Face.UP, new Neighbour.Turret(turret));
        logic.updateNeighbours(neighbours);
        return logic;
    }

    @Test
    void copiesModeTargetingAndRange() {
        TurretBaseLogic source = base(TurretType.MACHINE_GUN_TURRET);
        source.setMode(MachineMode.ALWAYS_ON);
        source.setMultiTargeting(true);
        source.setTargetPlayers(true);
        source.setTargetMobs(false);
        source.setTargetPassive(true);
        source.setRange(3);
        TurretBaseLogic target = base(TurretType.MACHINE_GUN_TURRET);
        BaseCardRules.apply(BaseCardRules.capture(source), target);
        assertEquals(MachineMode.ALWAYS_ON, target.getMode());
        assertTrue(target.isMultiTargeting());
        assertTrue(target.getTargetingSettings().isTargetPlayers());
        assertFalse(target.getTargetingSettings().isTargetMobs());
        assertTrue(target.getTargetingSettings().isTargetPassive());
        assertEquals(3, target.getTargetingSettings().getRange());
    }

    @Test
    void theRangeIsClampedToTheTargetBase() {
        TurretBaseLogic target = base(TurretType.MACHINE_GUN_TURRET);
        int max = target.getTargetingSettings().getMaxRange();
        BaseCardRules.apply(new MemoryCardData.BaseSettings(MachineMode.INVERTED, false, false, true, false, max + 50),
                target);
        assertEquals(max, target.getTargetingSettings().getRange());
    }

    /** A card may be pasted before the base noticed new range upgrades (the max range is updated every 5 ticks). */
    @Test
    void theRangeFollowsUpgradesTheBaseHasNotCountedYet() {
        FakeWorld world = new FakeWorld();
        TurretBaseLogic target = base(TurretType.MACHINE_GUN_TURRET, world);
        assertEquals(18, target.getTargetingSettings().getMaxRange());
        world.upgradeSlots.set(0, FakeSlot.upgrade(TurretBaseUpgradeType.RANGE, 2));
        BaseCardRules.apply(new MemoryCardData.BaseSettings(MachineMode.INVERTED, false, false, true, false, 30),
                target);
        assertEquals(22, target.getTargetingSettings().getRange(), "18 + 2 upgrades * 2, not the old 18");
    }
}
