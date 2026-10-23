package omtteam.openmodularturrets.core.base;

import omtteam.omlib.core.machine.MachineMode;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.core.security.SecurityEnvironment;
import omtteam.omlib.core.security.TrustManager;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.settings.BaseTierStats;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Neighbour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurretBaseLogicTest {
    private static final PlayerInfo OWNER = new PlayerInfo(UUID.randomUUID(), "Owner", false);
    private static final PlayerInfo STRANGER = new PlayerInfo(UUID.randomUUID(), "Stranger", false);

    private FakeSettings settings;
    private FakeWorld world;
    private FixedRandom random;
    private int dirtyCount;
    private int syncCount;

    /** Random number source returning a fixed value. */
    private static final class FixedRandom extends Random {
        double value;

        @Override
        public double nextDouble() {
            return value;
        }
    }

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

    @BeforeEach
    void setUp() {
        settings = new FakeSettings();
        world = new FakeWorld();
        random = new FixedRandom();
        for (int i = 0; i < BaseTier.AMMO_SLOTS; i++) world.ammoSlots.add(FakeSlot.empty());
        dirtyCount = 0;
        syncCount = 0;
    }

    private static Map<Face, Neighbour> single(Face face, Neighbour neighbour) {
        Map<Face, Neighbour> map = new EnumMap<>(Face.class);
        map.put(face, neighbour);
        return map;
    }

    /** The trust list the tile entity would own; its changes count as client-relevant as in 1.20.1+. */
    private TrustManager trust() {
        return new TrustManager(NO_TEAMS, () -> syncCount++);
    }

    private TurretBaseLogic base(int tier) {
        TurretBaseLogic logic = new TurretBaseLogic(tier, settings, trust(), world, random, clientRelevant -> {
            if (clientRelevant) syncCount++;
            else dirtyCount++;
        });
        logic.getTrustManager().setOwner(OWNER.identity());
        logic.updateNeighbours(new EnumMap<>(Face.class));
        return logic;
    }

    private static Map<Face, Neighbour> turrets(TurretType... types) {
        Map<Face, Neighbour> map = new EnumMap<>(Face.class);
        for (int i = 0; i < types.length; i++) map.put(Face.values()[i], new Neighbour.Turret(types[i]));
        return map;
    }

    private void ammoSlot(int index, FakeSlot slot) {
        world.ammoSlots.set(index, slot);
    }

    private void upgrade(int index, TurretBaseUpgradeType type, int count) {
        world.upgradeSlots.set(index, FakeSlot.upgrade(type, count));
    }

    private void addon(int index, AddonType type, int count) {
        world.addonSlots.set(index, FakeSlot.addon(type, count));
    }

    private static void tick(TurretBaseLogic logic, int ticks) {
        for (int i = 1; i <= ticks; i++) logic.tick(i);
    }

    @Nested
    class Ammo {
        @Test
        void requiredAmmoFollowsTurretTypeAndConfig() {
            TurretBaseLogic logic = base(5);
            assertEquals(Optional.of(AmmoType.BULLET), logic.getRequiredAmmo(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Optional.empty(), logic.getRequiredAmmo(TurretType.LASER_TURRET));
            settings.needAmmo = false;
            assertEquals(Optional.empty(), logic.getRequiredAmmo(TurretType.MACHINE_GUN_TURRET));
        }

        @Test
        void drawTakesOneItemPerShot() {
            TurretBaseLogic logic = base(1);
            FakeSlot potatoes = FakeSlot.ammo(AmmoType.POTATO, 3);
            ammoSlot(4, potatoes);
            int dirtyBefore = dirtyCount;
            Optional<AmmoDraw> draw = logic.drawAmmo(TurretType.POTATO_CANNON_TURRET);
            assertEquals(Optional.of(new AmmoDraw(AmmoType.POTATO, 1, 0, false, AmmoType.POTATO)), draw);
            assertEquals(2, potatoes.count);
            assertEquals(dirtyBefore + 1, dirtyCount, "the used ammo must be saved");
        }

        @Test
        void drawSpansSlotsWithoutTakingTooMuch() {
            TurretBaseLogic logic = base(5);
            upgrade(0, TurretBaseUpgradeType.SCATTERSHOT, 2); // 3 items per shot
            FakeSlot first = FakeSlot.ammo(AmmoType.POTATO, 1);
            FakeSlot second = FakeSlot.ammo(AmmoType.POTATO, 5);
            ammoSlot(0, first);
            ammoSlot(1, FakeSlot.ammo(AmmoType.ARROW, 10));
            ammoSlot(2, second);
            assertEquals(3, logic.drawAmmo(TurretType.POTATO_CANNON_TURRET).get().count());
            assertEquals(0, first.count);
            assertEquals(3, second.count);
        }

        @Test
        void countOnlyIncludesTheRequestedAmmo() {
            TurretBaseLogic logic = base(5);
            ammoSlot(0, FakeSlot.ammo(AmmoType.ARROW, 10));
            ammoSlot(1, FakeSlot.ammo(AmmoType.POTATO, 4));
            ammoSlot(2, FakeSlot.fuel(ReactorFuel.REDSTONE, 3));
            assertEquals(4, logic.countAmmo(AmmoType.POTATO));
            assertEquals(0, logic.countAmmo(AmmoType.BULLET));
        }

        @Test
        void drawIsAllOrNothing() {
            TurretBaseLogic logic = base(5);
            upgrade(0, TurretBaseUpgradeType.SCATTERSHOT, 1);
            FakeSlot potatoes = FakeSlot.ammo(AmmoType.POTATO, 1);
            ammoSlot(0, potatoes);
            assertEquals(Optional.empty(), logic.drawAmmo(TurretType.POTATO_CANNON_TURRET));
            assertEquals(1, potatoes.count);
            potatoes.count = 2; // exactly enough for one shot with a scattershot upgrade
            assertEquals(2, logic.drawAmmo(TurretType.POTATO_CANNON_TURRET).get().count());
            assertEquals(0, potatoes.count);
        }

        @Test
        void disposableDrawReportsDamageBonusOfFirstItem() {
            TurretBaseLogic logic = base(1);
            ammoSlot(3, FakeSlot.disposable(2, 4));
            ammoSlot(5, FakeSlot.disposable(1, 4));
            assertEquals(2, logic.drawAmmo(TurretType.DISPOSABLE_TURRET).get().damageBonus());
        }

        @Test
        void drawingForEnergyTurretsIsAnError() {
            TurretBaseLogic logic = base(5);
            assertThrows(IllegalArgumentException.class, () -> logic.drawAmmo(TurretType.LASER_TURRET));
        }

        @Test
        void recyclerSometimesSavesAmmo() {
            TurretBaseLogic logic = base(2);
            addon(0, AddonType.RECYCLER, 1);
            FakeSlot potatoes = FakeSlot.ammo(AmmoType.POTATO, 5);
            ammoSlot(0, potatoes);

            random.value = 0.05; // below recyclerNegateChance 0.1
            assertTrue(logic.drawAmmo(TurretType.POTATO_CANNON_TURRET).get().recycled());
            assertEquals(5, potatoes.count);

            random.value = 0.5;
            assertFalse(logic.drawAmmo(TurretType.POTATO_CANNON_TURRET).get().recycled());
            assertEquals(4, potatoes.count);

            random.value = 0.1; // exactly the chance: only values below it save the ammo
            assertFalse(logic.drawAmmo(TurretType.POTATO_CANNON_TURRET).get().recycled());
            assertEquals(3, potatoes.count);
        }

        @Test
        void recyclerNeedsAddonSlots() {
            TurretBaseLogic logic = base(1); // tier 1 has no addon slots
            addon(0, AddonType.RECYCLER, 1);
            ammoSlot(0, FakeSlot.ammo(AmmoType.POTATO, 5));
            random.value = 0;
            assertFalse(logic.drawAmmo(TurretType.POTATO_CANNON_TURRET).get().recycled());
        }

        @Test
        void ammoLevel() {
            TurretBaseLogic logic = base(5);
            ammoSlot(0, FakeSlot.ammo(AmmoType.POTATO, 7));
            ammoSlot(8, FakeSlot.ammo(AmmoType.POTATO, 3));
            assertEquals(10, logic.getAmmoLevel(TurretType.POTATO_CANNON_TURRET));

            logic.getEnergy().generate(80000);
            assertEquals(10, logic.getAmmoLevel(TurretType.LASER_TURRET)); // 80000 / 8000
            settings.turrets.put(TurretType.LASER_TURRET, FakeSettings.stats(25, 10, 4, 0, 10, 4, 0.06D, 0.125D, 2,
                    0.2D, 0.08D, 0.1D));
            assertEquals(Integer.MAX_VALUE, logic.getAmmoLevel(TurretType.LASER_TURRET), "no power usage: unlimited");

            settings.needAmmo = false;
            assertEquals(Integer.MAX_VALUE, logic.getAmmoLevel(TurretType.MACHINE_GUN_TURRET));
        }
    }

    @Nested
    class CreativeUpgrade {
        @Test
        void removesAmmoUsage() {
            TurretBaseLogic logic = base(5);
            upgrade(0, TurretBaseUpgradeType.CREATIVE, 1);
            assertEquals(Optional.empty(), logic.getRequiredAmmo(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Integer.MAX_VALUE, logic.getAmmoLevel(TurretType.MACHINE_GUN_TURRET));
        }

        @Test
        void withoutItDoesNotChangeAmmo() {
            TurretBaseLogic logic = base(5);
            assertEquals(Optional.of(AmmoType.BULLET), logic.getRequiredAmmo(TurretType.MACHINE_GUN_TURRET));
        }

        @Test
        void fillsEnergyOnTick() {
            TurretBaseLogic logic = base(5);
            logic.tick(1); // sets the tier's capacity
            tick(logic, 5);
            assertEquals(0, logic.getEnergy().getAmount());
            upgrade(0, TurretBaseUpgradeType.CREATIVE, 1);
            logic.tick(6);
            assertTrue(logic.getEnergy().getCapacity() > 0);
            assertEquals(logic.getEnergy().getCapacity(), logic.getEnergy().getAmount());
        }

        @Test
        void refillsAfterConsumption() {
            TurretBaseLogic logic = base(5);
            tick(logic, 5);
            upgrade(0, TurretBaseUpgradeType.CREATIVE, 1);
            logic.tick(6);
            logic.getEnergy().consume(1000);
            logic.tick(7);
            assertEquals(logic.getEnergy().getCapacity(), logic.getEnergy().getAmount());
        }
    }

    @Nested
    class Upgrades {
        @Test
        void rangeUpgrades() {
            TurretBaseLogic logic = base(5);
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET));
            assertEquals(18, logic.getRange(TurretType.MACHINE_GUN_TURRET));
            int syncsBefore = syncCount;
            int dirtyBefore = dirtyCount;
            upgrade(0, TurretBaseUpgradeType.RANGE, 2);
            upgrade(1, TurretBaseUpgradeType.RANGE, 1);
            logic.onInventoryChanged();
            assertEquals(18 + 2 * 3, logic.getRange(TurretType.MACHINE_GUN_TURRET));
            assertEquals(24, logic.getTargetingSettings().getMaxRange(), "the max range follows the upgrades at once");
            assertEquals(syncsBefore + 1, syncCount, "clients show the new max range");
            assertEquals(dirtyBefore + 1, dirtyCount, "the inventory change is saved");
        }

        @Test
        void upgradeSlotsDependOnTier() {
            upgrade(0, TurretBaseUpgradeType.RANGE, 1);
            upgrade(1, TurretBaseUpgradeType.RANGE, 1);
            assertEquals(0, base(1).getUpgradeCount(TurretBaseUpgradeType.RANGE));
            assertEquals(1, base(2).getUpgradeCount(TurretBaseUpgradeType.RANGE));
            assertEquals(1, base(4).getUpgradeCount(TurretBaseUpgradeType.RANGE));
            assertEquals(2, base(5).getUpgradeCount(TurretBaseUpgradeType.RANGE));
        }

        @Test
        void powerPerShot() {
            TurretBaseLogic logic = base(5);
            assertEquals(100, logic.getPowerPerShot(TurretType.MACHINE_GUN_TURRET));
            upgrade(0, TurretBaseUpgradeType.EFFICIENCY, 2);
            assertEquals(84, logic.getPowerPerShot(TurretType.MACHINE_GUN_TURRET)); // 100 * (1 - 0.16)
            upgrade(1, TurretBaseUpgradeType.SCATTERSHOT, 1);
            assertEquals(168, logic.getPowerPerShot(TurretType.MACHINE_GUN_TURRET)); // * (1 + 1)
        }

        @Test
        void railgunIgnoresScattershot() {
            TurretBaseLogic logic = base(5);
            upgrade(0, TurretBaseUpgradeType.SCATTERSHOT, 4);
            assertEquals(4, logic.getScattershotCount(TurretType.LASER_TURRET));
            assertEquals(0, logic.getScattershotCount(TurretType.RAILGUN_TURRET));
            assertEquals(25000, logic.getPowerPerShot(TurretType.RAILGUN_TURRET), "no extra energy per ray");
            assertEquals(3, logic.getAccuracyDeviation(TurretType.RAILGUN_TURRET), 1e-9, "no extra scatter");
            FakeSlot slugs = FakeSlot.ammo(AmmoType.FERRO_SLUG, 5);
            ammoSlot(0, slugs);
            assertEquals(1, logic.drawAmmo(TurretType.RAILGUN_TURRET).get().count(), "one slug per shot");
            assertEquals(4, slugs.count);
        }

        @Test
        void effectTurretsIgnoreScattershot() {
            TurretBaseLogic logic = base(5);
            upgrade(0, TurretBaseUpgradeType.SCATTERSHOT, 4);
            for (TurretType type : new TurretType[]{TurretType.MELEE_TURRET, TurretType.SLOWING_TURRET,
                    TurretType.TELEPORTER_TURRET}) {
                assertEquals(0, logic.getScattershotCount(type), type.toString());
            }
            assertEquals(5000, logic.getPowerPerShot(TurretType.SLOWING_TURRET), "no extra energy");
        }

        @Test
        void fireCooldown() {
            TurretBaseLogic logic = base(5);
            assertEquals(8, logic.getFireCooldownTicks(TurretType.MACHINE_GUN_TURRET), 1e-9);
            upgrade(0, TurretBaseUpgradeType.FIRE_RATE, 2);
            assertEquals(8 / 1.2, logic.getFireCooldownTicks(TurretType.MACHINE_GUN_TURRET), 1e-9);
        }

        @Test
        void accuracyDeviation() {
            TurretBaseLogic logic = base(5);
            assertEquals(30, logic.getAccuracyDeviation(TurretType.MACHINE_GUN_TURRET), 1e-9);
            upgrade(0, TurretBaseUpgradeType.ACCURACY, 2);
            upgrade(1, TurretBaseUpgradeType.SCATTERSHOT, 1);
            double expected = 30 / Math.pow(1.4, 1.5) * 1.1;
            assertEquals(expected, logic.getAccuracyDeviation(TurretType.MACHINE_GUN_TURRET), 1e-6);
        }

        @Test
        void addonLevels() {
            TurretBaseLogic logic = base(2);
            // the fake drops addon (looting upgrade) stacks to 4 per slot; two addon slots allow up to 8
            assertEquals(TurretBaseLogic.NO_LOOT, logic.getLootingLevel(3), "no addon: no loot at all");
            addon(0, AddonType.FAKE_DROPS, 1);
            assertEquals(0, logic.getLootingLevel(3), "first addon enables loot without looting");
            addon(0, AddonType.FAKE_DROPS, 2);
            assertEquals(1, logic.getLootingLevel(3));
            addon(1, AddonType.FAKE_DROPS, 4);
            assertEquals(3, logic.getLootingLevel(3), "capped at the enchantment maximum");
            assertEquals(5, logic.getLootingLevel(10), "a datapack may raise the maximum");

            addon(0, AddonType.DAMAGE_AMP, 2);
            assertEquals(2, logic.getDamageAmpLevel());
            assertEquals(0.12, logic.getDamageAmpBonus(TurretType.MACHINE_GUN_TURRET), 1e-9);
        }

        @Test
        void concealment() {
            settings.concealWithoutAddon = true;
            assertFalse(base(1).shouldConcealTurrets());
            assertTrue(base(2).shouldConcealTurrets(), "the config conceals without an addon from tier 2");
            settings.concealWithoutAddon = false;
            TurretBaseLogic logic = base(2);
            assertFalse(logic.shouldConcealTurrets());
            assertFalse(logic.isConcealingTurrets());
            addon(1, AddonType.CONCEALER, 1);
            logic.onInventoryChanged();
            assertTrue(logic.isConcealingTurrets());
        }
    }

    @Nested
    class Neighbours {
        @Test
        void placingAFurtherReachingTurretRaisesTheRange() {
            TurretBaseLogic logic = base(4);
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET)); // max 18
            logic.setRange(10);
            Map<Face, Neighbour> both = turrets(TurretType.MACHINE_GUN_TURRET, TurretType.ROCKET_TURRET);
            logic.updateNeighbours(both); // rocket reaches 30 > 18
            assertEquals(30, logic.getTargetingSettings().getRange());
        }

        @Test
        void placingAShorterOrEqualTurretKeepsTheRange() {
            TurretBaseLogic logic = base(4);
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET));
            logic.setRange(10);
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET, TurretType.MACHINE_GUN_TURRET));
            assertEquals(10, logic.getTargetingSettings().getRange());
            // a second rocket turret reaches exactly as far as the first one
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET, TurretType.MACHINE_GUN_TURRET, TurretType.ROCKET_TURRET));
            assertEquals(10, logic.getTargetingSettings().getRange());
        }

        @Test
        void loadingDoesNotResetTheRange() {
            TurretBaseLogic logic = new TurretBaseLogic(4, settings, trust(), world, random, clientRelevant -> {
            });
            logic.loadState(new TurretBaseLogic.State(0, MachineMode.INVERTED, false, false, true, false, 5, 30,
                    false, 0, 0));
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET));
            assertEquals(5, logic.getTargetingSettings().getRange());
        }

        /** {@code TargetingSettings} clamps a saved range above the saved maximum, so tick needs no clamp. */
        @Test
        void loadedRangeAboveTheMaximumIsClamped() {
            TurretBaseLogic logic = new TurretBaseLogic(5, settings, trust(), world, random, clientRelevant -> {
            });
            logic.loadState(new TurretBaseLogic.State(0, MachineMode.INVERTED, false, false, true, false, 40, 30,
                    false, 0, 0));
            assertEquals(30, logic.getTargetingSettings().getRange());
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET));
            assertEquals(18, logic.getTargetingSettings().getRange());
            tick(logic, 20);
            assertEquals(18, logic.getTargetingSettings().getRange());
        }

        @Test
        void remainingTurretSlots() {
            TurretBaseLogic logic = base(3); // max 2 turrets
            assertEquals(2, logic.getRemainingTurretSlots(TurretType.ROCKET_TURRET));
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET));
            assertEquals(1, logic.getRemainingTurretSlots(TurretType.MACHINE_GUN_TURRET));
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET, TurretType.ROCKET_TURRET));
            assertEquals(0, logic.getRemainingTurretSlots(TurretType.MACHINE_GUN_TURRET));
        }

        @Test
        void perTypeLimit() {
            settings.turrets.put(TurretType.ROCKET_TURRET,
                    FakeSettings.stats(30, 30, 10, 5000, 10, 1, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
            TurretBaseLogic logic = base(5);
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET));
            assertEquals(0, logic.getRemainingTurretSlots(TurretType.ROCKET_TURRET));
            assertEquals(3, logic.getRemainingTurretSlots(TurretType.LASER_TURRET));
        }

        @Test
        void turretsNeedASufficientTier() {
            TurretBaseLogic logic = base(3);
            assertTrue(logic.supportsTurret(TurretType.GRENADE_TURRET));
            assertFalse(logic.supportsTurret(TurretType.ROCKET_TURRET));
        }

        @Test
        void maxRangeIsTheBestTurret() {
            TurretBaseLogic logic = base(5);
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET, TurretType.ROCKET_TURRET));
            assertEquals(30, logic.getTargetingSettings().getMaxRange());
            logic.setRange(25);
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET));
            assertEquals(18, logic.getTargetingSettings().getMaxRange());
            assertEquals(18, logic.getTargetingSettings().getRange());
            // both ends of [0, max range] are allowed, values outside are ignored
            logic.setRange(0);
            assertEquals(0, logic.getTargetingSettings().getRange());
            logic.setRange(-1);
            assertEquals(0, logic.getTargetingSettings().getRange());
            logic.setRange(18);
            assertEquals(18, logic.getTargetingSettings().getRange());
            logic.setRange(19);
            assertEquals(18, logic.getTargetingSettings().getRange());
        }

        @Test
        void maxRangeIsKeptUntilNeighboursAreKnown() {
            TurretBaseLogic logic = new TurretBaseLogic(5, settings, trust(), world, random, changed -> {
            });
            TurretBaseLogic.State saved = new TurretBaseLogic.State(0, MachineMode.INVERTED, false, false, true,
                    false, 20, 30, false, 0, 0);
            logic.loadState(saved);
            logic.tick(5);
            assertEquals(20, logic.getTargetingSettings().getRange());
        }
    }

    @Nested
    class Energy {
        @Test
        void externalInsertionIsLimitedByMaxIo() {
            TurretBaseLogic logic = base(1); // capacity 500, io 50
            assertEquals(50, logic.getEnergy().insert(1000, false));
            assertEquals(50, logic.getEnergy().getAmount());
        }

        @Test
        void shotEnergy() {
            TurretBaseLogic logic = base(5);
            logic.getEnergy().generate(150);
            assertTrue(logic.hasEnergyForShot(TurretType.MACHINE_GUN_TURRET));
            assertTrue(logic.consumeShotEnergy(TurretType.MACHINE_GUN_TURRET));
            assertFalse(logic.consumeShotEnergy(TurretType.MACHINE_GUN_TURRET));
            assertEquals(50, logic.getEnergy().getAmount());
            logic.getEnergy().generate(50);
            assertTrue(logic.hasEnergyForShot(TurretType.MACHINE_GUN_TURRET), "exactly enough energy is enough");
            assertTrue(logic.consumeShotEnergy(TurretType.MACHINE_GUN_TURRET));
            assertEquals(0, logic.getEnergy().getAmount());
        }

        /**
         * A config reload replaces the settings; existing bases pick them up on their next refresh (every 5 ticks),
         * a neighbour update applies the tier's capacity at once, and setRange refreshes the max range first.
         */
        @Test
        void configChangesReachTheBase() {
            TurretBaseLogic logic = base(1); // capacity 500, io 50
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET)); // max range 18
            settings.tiers[0] = new BaseTierStats(800, 70, 1);
            settings.turrets.put(TurretType.MACHINE_GUN_TURRET,
                    FakeSettings.stats(20, 8, 2, 100, 30, 4, 0.06D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
            logic.setRange(20);
            assertEquals(20, logic.getTargetingSettings().getRange(), "setRange accepts the new max range at once");

            tick(logic, 5);
            assertEquals(800, logic.getEnergy().getCapacity());
            assertEquals(70, logic.getEnergy().insert(1000, true), "the new insertion rate");

            settings.turrets.put(TurretType.MACHINE_GUN_TURRET,
                    FakeSettings.stats(22, 8, 2, 100, 30, 4, 0.06D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
            tick(logic, 5);
            assertEquals(22, logic.getTargetingSettings().getMaxRange());

            settings.tiers[0] = new BaseTierStats(900, 70, 1);
            logic.updateNeighbours(turrets(TurretType.MACHINE_GUN_TURRET));
            assertEquals(900, logic.getEnergy().getCapacity(), "a neighbour update applies the capacity at once");
        }

        @Test
        void redstoneReactorWithoutFuelGeneratesNothing() {
            TurretBaseLogic logic = base(2);
            addon(0, AddonType.REDSTONE_REACTOR, 1);
            FakeSlot potatoes = FakeSlot.ammo(AmmoType.POTATO, 5);
            ammoSlot(0, potatoes);
            tick(logic, 20);
            assertEquals(0, logic.getEnergy().getAmount());
            assertEquals(5, potatoes.count);
        }

        @Test
        void redstoneReactorBurnsABlockOnlyIfItFits() {
            TurretBaseLogic logic = base(2); // capacity 50000
            addon(0, AddonType.REDSTONE_REACTOR, 1);
            FakeSlot blocks = FakeSlot.fuel(ReactorFuel.REDSTONE_BLOCK, 1);
            ammoSlot(0, blocks);
            logic.getEnergy().generate(40000); // 10000 free: a block (14400) does not fit, and there is no dust
            tick(logic, 20);
            assertEquals(40000, logic.getEnergy().getAmount());
            assertEquals(1, blocks.count);
            logic.getEnergy().consume(4400); // 14400 free: the block fits exactly
            tick(logic, 20);
            assertEquals(50000, logic.getEnergy().getAmount());
            assertEquals(0, blocks.count);
        }

        @Test
        void redstoneReactorPrefersBlocks() {
            TurretBaseLogic logic = base(2);
            addon(0, AddonType.REDSTONE_REACTOR, 1);
            FakeSlot blocks = FakeSlot.fuel(ReactorFuel.REDSTONE_BLOCK, 2);
            FakeSlot dust = FakeSlot.fuel(ReactorFuel.REDSTONE, 2);
            ammoSlot(0, dust);
            ammoSlot(1, blocks);

            tick(logic, 19);
            assertEquals(0, logic.getEnergy().getAmount());
            logic.tick(20);
            assertEquals(1600 * 9, logic.getEnergy().getAmount());
            assertEquals(1, blocks.count);
            assertEquals(2, dust.count);
        }

        @Test
        void redstoneReactorFallsBackToDust() {
            TurretBaseLogic logic = base(2); // capacity 50000
            addon(0, AddonType.REDSTONE_REACTOR, 1);
            FakeSlot blocks = FakeSlot.fuel(ReactorFuel.REDSTONE_BLOCK, 1);
            FakeSlot dust = FakeSlot.fuel(ReactorFuel.REDSTONE, 1);
            ammoSlot(0, blocks);
            ammoSlot(1, dust);
            logic.getEnergy().generate(40000); // 10000 free: a block (14400) does not fit
            tick(logic, 20);
            assertEquals(41600, logic.getEnergy().getAmount());
            assertEquals(1, blocks.count);
            assertEquals(0, dust.count);
        }

        @Test
        void redstoneReactorNeedsTheAddon() {
            TurretBaseLogic logic = base(2);
            ammoSlot(0, FakeSlot.fuel(ReactorFuel.REDSTONE_BLOCK, 1));
            tick(logic, 20);
            assertEquals(0, logic.getEnergy().getAmount());
        }

        @Test
        void solarPanel() {
            TurretBaseLogic logic = base(2);
            addon(1, AddonType.SOLAR_PANEL, 1);
            world.solar = true;
            tick(logic, 3);
            assertEquals(30, logic.getEnergy().getAmount());
            world.solar = false;
            tick(logic, 3);
            assertEquals(30, logic.getEnergy().getAmount());
        }
    }

    @Nested
    class Modes {
        @Test
        void redstoneModes() {
            TurretBaseLogic logic = base(1);
            assertTrue(logic.isActive()); // inverted, unpowered
            logic.setRedstone(true);
            assertFalse(logic.isActive());
            logic.setMode(MachineMode.NONINVERTED);
            assertTrue(logic.isActive());
            logic.setMode(MachineMode.ALWAYS_OFF);
            assertFalse(logic.isActive());
            logic.setMode(MachineMode.ALWAYS_ON);
            logic.setRedstone(false);
            assertTrue(logic.isActive());
        }

        @Test
        void modesCycle() {
            assertEquals(MachineMode.ALWAYS_OFF, MachineMode.ALWAYS_ON.next());
            assertEquals(MachineMode.ALWAYS_ON, MachineMode.NONINVERTED.next());
        }

        @Test
        void changesNotifyTheListener() {
            TurretBaseLogic logic = base(1);
            int syncsBefore = syncCount;
            logic.setRedstone(true);
            logic.setRedstone(true);
            assertEquals(syncsBefore + 1, syncCount, "the active state is shown by clients");
            logic.getTrustManager().addTrustedPlayer(OWNER, STRANGER.identity());
            logic.cycleMode();
            logic.setTargetPlayers(true);
            assertFalse(logic.isMultiTargeting());
            logic.setMultiTargeting(true);
            assertTrue(logic.isMultiTargeting());
            assertEquals(syncsBefore + 5, syncCount);
            logic.setTargetMobs(false);
            logic.setTargetPassive(true);
            logic.setRange(0);
            logic.increaseKillCounter();
            logic.increasePlayerKillCounter();
            assertEquals(syncsBefore + 10, syncCount, "targeting, range and kill counts are shown by clients");
            int dirtyBefore = dirtyCount;
            int syncsAfterSettings = syncCount;
            logic.getEnergy().generate(10);
            assertEquals(dirtyBefore + 1, dirtyCount, "energy only needs saving");
            assertEquals(syncsAfterSettings, syncCount);
        }

        @Test
        void cycleModeFollowsOriginalOrder() {
            TurretBaseLogic logic = base(1);
            assertEquals(MachineMode.INVERTED, logic.getMode());
            logic.cycleMode();
            assertEquals(MachineMode.NONINVERTED, logic.getMode());
            logic.cycleMode();
            assertEquals(MachineMode.ALWAYS_ON, logic.getMode());
        }

        @Test
        void stateRoundTrip() {
            TurretBaseLogic logic = base(5);
            logic.updateNeighbours(turrets(TurretType.ROCKET_TURRET));
            logic.getEnergy().generate(1234);
            logic.setMode(MachineMode.NONINVERTED);
            logic.setTargetPlayers(true);
            logic.setRange(12);
            logic.setMultiTargeting(true);
            logic.increaseKillCounter();
            logic.increasePlayerKillCounter();
            TurretBaseLogic.State state = logic.getState();

            TurretBaseLogic copy = base(5);
            copy.loadState(state);
            assertEquals(state, copy.getState());
            assertEquals(1, copy.getKills());
            assertEquals(1, copy.getPlayerKills());
        }

        /** Each flag on and off, so a getter that ignores its field is noticed. */
        @Test
        void stateKeepsEveryFlag() {
            for (boolean on : new boolean[]{true, false}) {
                TurretBaseLogic copy = base(5);
                copy.loadState(new TurretBaseLogic.State(0, MachineMode.INVERTED, on, on, on, on, 0, 0, on, 0, 0));
                TurretBaseLogic.State state = copy.getState();
                assertEquals(on, state.redstone());
                assertEquals(on, state.targetPlayers());
                assertEquals(on, state.targetMobs());
                assertEquals(on, state.targetPassive());
                assertEquals(on, state.multiTargeting());
                assertEquals(on, copy.isMultiTargeting());
                assertEquals(on, copy.getTargetingSettings().isTargetMobs());
            }
        }

        @Test
        void newBaseHasTheDefaultState() {
            assertEquals(3, base(3).getTier());
            assertEquals(TurretBaseLogic.State.defaults(), base(1).getState());
            assertThrows(IllegalArgumentException.class, () -> base(0));
            assertThrows(IllegalArgumentException.class, () -> base(6));
        }
    }

    @Nested
    class Warnings {
        private TurretBaseLogic logic;
        private FakeTarget stranger;

        @BeforeEach
        void setUpBase() {
            logic = base(5);
            logic.updateNeighbours(single(Face.UP, new Neighbour.Turret(TurretType.MACHINE_GUN_TURRET)));
            logic.setTargetPlayers(true);
            logic.getEnergy().generate(1000);
            // turret at y 65, warn radius 18 + 5
            stranger = new FakeTarget(100, 65 + 22, 100, STRANGER);
            world.entities.add(stranger);
        }

        @Test
        void strangersAreWarnedOncePerCooldown() {
            logic.warnPlayers(10);
            logic.warnPlayers(20);
            assertEquals(Arrays.asList(stranger), world.warned);
            logic.warnPlayers(10 + TurretBaseLogic.WARNING_COOLDOWN_TICKS);
            assertEquals(1, world.warned.size(), "the cooldown includes its last tick");
            logic.warnPlayers(10 + TurretBaseLogic.WARNING_COOLDOWN_TICKS + 1);
            assertEquals(2, world.warned.size());
        }

        @Test
        void warningsAreThrottledToTheSearchInterval() {
            tick(logic, 9);
            assertTrue(world.warned.isEmpty());
            logic.tick(10);
            assertEquals(1, world.warned.size());
        }

        @Test
        void playersOutsideTheWarningRangeAreIgnored() {
            world.entities.clear();
            world.entities.add(new FakeTarget(100, 65 + 24, 100, STRANGER));
            logic.warnPlayers(10);
            assertTrue(world.warned.isEmpty());
        }

        @Test
        void trustedCreativeAndMobsAreNotWarned() {
            world.entities.clear();
            PlayerInfo friend = new PlayerInfo(UUID.randomUUID(), "Friend", false);
            logic.getTrustManager().addTrustedPlayer(OWNER, friend.identity());
            world.entities.add(new FakeTarget(100, 70, 100, friend));
            world.entities.add(new FakeTarget(100, 70, 100, OWNER));
            world.entities.add(new FakeTarget(100, 70, 100, null));
            FakeTarget creative = new FakeTarget(100, 70, 100, STRANGER);
            creative.creative = true;
            world.entities.add(creative);
            logic.warnPlayers(10);
            assertTrue(world.warned.isEmpty());
        }

        @Test
        void noWarningsWhenPlayersAreNotTargeted() {
            logic.setTargetPlayers(false);
            logic.warnPlayers(10);
            settings.globalPlayers = false;
            logic.setTargetPlayers(true);
            logic.warnPlayers(10);
            assertTrue(world.warned.isEmpty());
        }

        @Test
        void noWarningsWhenInactiveOrWithoutPower() {
            logic.setMode(MachineMode.ALWAYS_OFF);
            logic.warnPlayers(10);
            logic.setMode(MachineMode.ALWAYS_ON);
            logic.getEnergy().consume(logic.getEnergy().getAmount());
            logic.warnPlayers(10);
            assertTrue(world.warned.isEmpty());
        }
    }
}
