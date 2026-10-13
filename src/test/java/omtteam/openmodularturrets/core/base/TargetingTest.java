package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.SecurityEnvironment;
import omtteam.openmodularturrets.core.settings.BaseTierStats;
import omtteam.openmodularturrets.core.target.EntityCategory;
import omtteam.openmodularturrets.core.target.TargetSelector;
import omtteam.openmodularturrets.core.target.TargetingPriority;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Neighbour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TargetingTest {
    private static final PlayerInfo OWNER = new PlayerInfo(UUID.randomUUID(), "Owner", false);
    private static final PlayerInfo FRIEND = new PlayerInfo(UUID.randomUUID(), "Friend", false);
    private static final PlayerInfo STRANGER = new PlayerInfo(UUID.randomUUID(), "Stranger", false);
    /** Base at (100, 64, 100); the turret on its UP face is at (100, 65, 100). */
    private static final BlockPosition TURRET = new BlockPosition(100, 65, 100);

    private FakeSettings settings;
    private FakeWorld world;
    private TurretBaseLogic logic;
    private long time;

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
    };

    @BeforeEach
    void setUp() {
        settings = new FakeSettings();
        world = new FakeWorld();
        logic = base(2, Map.of(Face.UP, new Neighbour.Turret(TurretType.MACHINE_GUN_TURRET)));
        logic.setRange(18);
        logic.getTrustManager().addTrustedPlayer(OWNER, FRIEND.identity());
    }

    private TurretBaseLogic base(int tier, Map<Face, Neighbour> neighbours) {
        TurretBaseLogic base = new TurretBaseLogic(tier, settings, NO_TEAMS, world, RandomGenerator.of("L64X128MixRandom"),
                clientRelevant -> {
                });
        base.getTrustManager().setOwner(OWNER.identity());
        base.updateNeighbours(neighbours);
        base.getEnergy().generate(10000);
        return base;
    }

    private FakeTarget zombie(double x, double y, double z) {
        FakeTarget target = FakeTarget.mob(x, y, z, "minecraft:zombie", EntityCategory.MONSTER);
        world.entities.add(target);
        return target;
    }

    private FakeTarget player(double x, double y, double z, PlayerInfo info) {
        FakeTarget target = new FakeTarget(x, y, z, info);
        world.entities.add(target);
        return target;
    }

    /** Runs enough ticks for one target search (the search interval is 10 ticks). */
    private void search() {
        for (int i = 0; i <= settings.searchTicks; i++) logic.tick(++time);
    }

    private Optional<Integer> targetId() {
        return logic.getTarget(Face.UP).map(t -> t.getId());
    }

    @Nested
    class Categories {
        @Test
        void hostileMobsAreTargeted() {
            FakeTarget zombie = zombie(105, 65, 100);
            search();
            assertEquals(Optional.of(zombie.getId()), targetId());
        }

        @Test
        void passiveMobsOnlyWhenEnabled() {
            FakeTarget pig = FakeTarget.mob(105, 65, 100, "minecraft:pig", EntityCategory.PASSIVE);
            world.entities.add(pig);
            search();
            assertEquals(Optional.empty(), targetId());
            logic.setTargetPassive(true);
            search();
            assertEquals(Optional.of(pig.getId()), targetId());
        }

        @Test
        void globalConfigSwitchesWin() {
            zombie(105, 65, 100);
            settings.globalMobs = false;
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void otherEntitiesAreIgnored() {
            world.entities.add(FakeTarget.mob(105, 65, 100, "minecraft:villager", EntityCategory.OTHER));
            logic.setTargetPassive(true);
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void classificationOrderIsBlockThenNeutralThenHostile() {
            TargetSelector selector = logic.getTargetSelector();
            settings.blocked.add("mod:both");
            settings.forcedNeutral.add("mod:both");
            assertEquals(TargetSelector.Classification.BLOCKED,
                    selector.classify(FakeTarget.mob(0, 0, 0, "mod:both", EntityCategory.MONSTER)));
            assertEquals(TargetSelector.Classification.NEUTRAL,
                    selector.classify(FakeTarget.mob(0, 0, 0, "minecraft:cow", EntityCategory.MONSTER)));
            assertEquals(TargetSelector.Classification.HOSTILE,
                    selector.classify(FakeTarget.mob(0, 0, 0, "minecraft:creeper", EntityCategory.OTHER)));
        }

        @Test
        void neutralForceListProtectsMisclassifiedMonsters() {
            settings.forcedNeutral.add("mod:friendly");
            world.entities.add(FakeTarget.mob(105, 65, 100, "mod:friendly", EntityCategory.MONSTER));
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void blockedEntitiesAreNeverTargeted() {
            world.entities.add(FakeTarget.mob(105, 65, 100, "minecraft:armor_stand", EntityCategory.MONSTER));
            logic.setTargetPassive(true);
            logic.setTargetPlayers(true);
            search();
            assertEquals(Optional.empty(), targetId());
        }
    }

    @Nested
    class Players {
        @Test
        void strangersOnlyWhenPlayersAreTargeted() {
            FakeTarget stranger = player(105, 65, 100, STRANGER);
            search();
            assertEquals(Optional.empty(), targetId());
            logic.setTargetPlayers(true);
            search();
            assertEquals(Optional.of(stranger.getId()), targetId());
        }

        @Test
        void ownerAndTrustedAreNeverTargeted() {
            logic.setTargetPlayers(true);
            player(105, 65, 100, OWNER);
            player(104, 65, 100, FRIEND);
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void becomingTrustedDropsTheTarget() {
            logic.setTargetPlayers(true);
            player(105, 65, 100, STRANGER);
            search();
            assertTrue(targetId().isPresent());
            logic.getTrustManager().addTrustedPlayer(OWNER, STRANGER.identity());
            logic.tick(++time);
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void globalPlayerSwitch() {
            logic.setTargetPlayers(true);
            settings.globalPlayers = false;
            player(105, 65, 100, STRANGER);
            search();
            assertEquals(Optional.empty(), targetId());
        }
    }

    @Nested
    class RangeAndSight {
        @Test
        void chebyshevRangeFromTheTurretCorner() {
            logic.setRange(5);
            assertTrue(TargetSelector.isInRange(FakeTarget.mob(105, 65, 100, "a", EntityCategory.MONSTER), TURRET, 5));
            assertFalse(TargetSelector.isInRange(FakeTarget.mob(105.5, 65, 100, "a", EntityCategory.MONSTER), TURRET, 5));
            assertTrue(TargetSelector.isInRange(FakeTarget.mob(104, 69, 96, "a", EntityCategory.MONSTER), TURRET, 5));
        }

        @Test
        void targetsLeavingTheRangeAreDropped() {
            logic.setRange(5);
            zombie(104, 65, 100);
            search();
            assertTrue(targetId().isPresent());
            world.entities.clear();
            zombie(110, 65, 100);
            logic.tick(++time);
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void blockedViewPreventsTargeting() {
            FakeTarget zombie = zombie(105, 65, 100);
            world.blockedView = entity -> entity == zombie;
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void targetsBehindTheBaseAreIgnored() {
            logic.setRange(5);
            zombie(101, 61, 100);
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void deadTargetsAreReplacedImmediately() {
            FakeTarget first = zombie(103, 65, 100);
            FakeTarget second = zombie(106, 65, 100);
            search();
            int chosen = targetId().orElseThrow();
            (chosen == first.getId() ? first : second).alive = false;
            logic.tick(++time);
            assertEquals(Optional.of(chosen == first.getId() ? second.getId() : first.getId()), targetId());
        }
    }

    @Nested
    class Pets {
        @Test
        void petsOfFriendsAreSafe() {
            logic.setTargetPassive(true);
            FakeTarget dog = FakeTarget.mob(105, 65, 100, "minecraft:wolf", EntityCategory.PASSIVE);
            dog.owner = FRIEND.identity();
            world.entities.add(dog);
            search();
            assertEquals(Optional.empty(), targetId());
        }

        @Test
        void petsOfStrangersAreNot() {
            logic.setTargetPassive(true);
            FakeTarget dog = FakeTarget.mob(105, 65, 100, "minecraft:wolf", EntityCategory.PASSIVE);
            dog.owner = STRANGER.identity();
            world.entities.add(dog);
            search();
            assertEquals(Optional.of(dog.getId()), targetId());
        }

        @Test
        void tamedHorsesWithoutOwnerAreSafe() {
            logic.setTargetPassive(true);
            FakeTarget horse = FakeTarget.mob(105, 65, 100, "minecraft:horse", EntityCategory.PASSIVE);
            horse.tamedHorse = true;
            world.entities.add(horse);
            search();
            assertEquals(Optional.empty(), targetId());
        }
    }

    @Nested
    class TurretRules {
        @Test
        void relativisticTurretIgnoresSlowedTargets() {
            logic = base(3, Map.of(Face.UP, new Neighbour.Turret(TurretType.RELATIVISTIC_TURRET)));
            logic.setRange(10);
            FakeTarget zombie = zombie(105, 65, 100);
            zombie.slowed = true;
            search();
            assertEquals(Optional.empty(), targetId());
            zombie.slowed = false;
            search();
            assertEquals(Optional.of(zombie.getId()), targetId());
        }

        @Test
        void inactiveBaseDropsAllTargets() {
            zombie(105, 65, 100);
            search();
            assertTrue(targetId().isPresent());
            logic.setMode(MachineMode.ALWAYS_OFF);
            logic.tick(++time);
            logic.setMode(MachineMode.ALWAYS_ON);
            assertTrue(logic.getTargetIds().isEmpty());
        }

        @Test
        void noSearchWithoutEnergyForAShot() {
            logic.getEnergy().consume(logic.getEnergy().getAmount());
            zombie(105, 65, 100);
            search();
            assertTrue(logic.getTargetIds().isEmpty());
        }

        @Test
        void searchWaitsForTheInterval() {
            zombie(105, 65, 100);
            for (int i = 0; i < settings.searchTicks; i++) logic.tick(++time);
            assertTrue(logic.getTargetIds().isEmpty());
            logic.tick(++time);
            assertFalse(logic.getTargetIds().isEmpty());
        }

        @Test
        void removedTurretsLoseTheirTarget() {
            zombie(105, 65, 100);
            search();
            logic.updateNeighbours(Map.of());
            assertTrue(logic.getTargetIds().isEmpty());
        }
    }

    @Nested
    class MultiTargeting {
        private Map<Face, Neighbour> twoGuns() {
            Map<Face, Neighbour> map = new EnumMap<>(Face.class);
            map.put(Face.UP, new Neighbour.Turret(TurretType.MACHINE_GUN_TURRET));
            map.put(Face.EAST, new Neighbour.Turret(TurretType.MACHINE_GUN_TURRET));
            return map;
        }

        @Test
        void singleTargetingFocusesFire() {
            settings.tiers[1] = new BaseTierStats(50000, 100, 2);
            logic = base(2, twoGuns());
            logic.setRange(18);
            zombie(105, 66, 100);
            zombie(106, 66, 100);
            search();
            assertEquals(logic.getTargetIds().get(Face.UP), logic.getTargetIds().get(Face.EAST));
        }

        @Test
        void multiTargetingSpreadsFire() {
            settings.tiers[1] = new BaseTierStats(50000, 100, 2);
            logic = base(2, twoGuns());
            logic.setRange(18);
            logic.setMultiTargeting(true);
            zombie(105, 66, 100);
            zombie(106, 66, 100);
            search();
            assertEquals(2, logic.getTargetIds().size());
            assertNotEquals(logic.getTargetIds().get(Face.UP), logic.getTargetIds().get(Face.EAST));
        }
    }

    @Nested
    class Priorities {
        @Test
        void factorsFollowOriginal() {
            FakeTarget zombie = FakeTarget.mob(103, 69, 100, "minecraft:zombie", EntityCategory.MONSTER);
            zombie.health = 15.5F;
            zombie.armor = 2;
            assertEquals(15 * 2, TargetingPriority.MAX_HP.value(zombie, TURRET, 2));
            assertEquals(4 * 3, TargetingPriority.HP_REMAINING.value(zombie, TURRET, 3));
            assertEquals(5 * -1, TargetingPriority.DISTANCE.value(zombie, TURRET, -1));
            assertEquals(3 * 4, TargetingPriority.ARMOR.value(zombie, TURRET, 4));
            assertEquals(0, TargetingPriority.PLAYER.value(zombie, TURRET, 10));
            assertEquals(1000, TargetingPriority.PLAYER.value(new FakeTarget(0, 0, 0, STRANGER), TURRET, 10));
        }

        @Test
        void highestScoreWins() {
            // rocket weights {10, 10, 20, 1, 10}: prefers far away targets
            logic = base(4, Map.of(Face.UP, new Neighbour.Turret(TurretType.ROCKET_TURRET)));
            logic.setRange(20);
            FakeTarget near = zombie(102, 65, 100);
            FakeTarget far = zombie(110, 65, 100);
            near.health = 10;
            far.health = 10;
            search();
            assertEquals(Optional.of(far.getId()), targetId());
        }

        @Test
        void negativeWeightsPreferSmallValues() {
            // potato cannon weights {-5, -5, -5, 1, 10}: prefers near, healthy-looking (low max HP) targets
            logic = base(1, Map.of(Face.UP, new Neighbour.Turret(TurretType.POTATO_CANNON_TURRET)));
            logic.setRange(15);
            FakeTarget far = zombie(110, 65, 100);
            FakeTarget near = zombie(102, 65, 100);
            search();
            assertEquals(Optional.of(near.getId()), targetId());
        }

        @Test
        void zeroWeightsAndFullHealthDoNotFlattenTheRanking() {
            // melee {1, 1, 0, 10, 10}: distance ignored, armor counts; full health no longer zeroes everything
            settings.turrets.put(TurretType.MELEE_TURRET, FakeSettings.stats(10, 20, 8, 2, 0, 4, 0.1D, 0.2D, 0, 0D, 0.08D, 0.1D));
            logic = base(1, Map.of(Face.UP, new Neighbour.Turret(TurretType.MELEE_TURRET)));
            logic.setRange(10);
            FakeTarget plain = zombie(101, 65, 100);
            FakeTarget armored = zombie(108, 65, 100);
            armored.armor = 10;
            search();
            assertEquals(Optional.of(armored.getId()), targetId());
        }

        @Test
        void scoresDoNotOverflow() {
            FakeTarget boss = FakeTarget.mob(100, 65, 100, "minecraft:wither", EntityCategory.MONSTER);
            boss.health = Float.MAX_VALUE;
            boss.maxHealth = Float.MAX_VALUE;
            int[] weights = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
            assertTrue(TargetingPriority.score(boss, TURRET, weights) > 0);
            boss.maxHealth = 20;
            boss.health = Float.MAX_VALUE;
            assertTrue(TargetingPriority.score(boss, TURRET, new int[]{10, 10, 20, 40, 10}) > 0);
        }

        @Test
        void playersOutrankMobsWithPositiveWeights() {
            logic = base(4, Map.of(Face.UP, new Neighbour.Turret(TurretType.ROCKET_TURRET)));
            logic.setRange(20);
            logic.setTargetPlayers(true);
            FakeTarget zombie = zombie(102, 65, 100);
            zombie.health = 10;
            FakeTarget stranger = player(110, 65, 100, STRANGER);
            stranger.health = 10;
            search();
            assertEquals(Optional.of(stranger.getId()), targetId());
        }

        @Test
        void everyTurretHasFiveWeights() {
            for (TurretType type : TurretType.values()) {
                assertEquals(TargetingPriority.values().length, type.getDefaultPriorities().length, type.name());
            }
            assertEquals(10, TurretType.MELEE_TURRET.getDefaultPriorities()[TargetingPriority.PLAYER.ordinal()]);
        }

        @Test
        void accessLevelsDoNotMatterForTargeting() {
            logic.getTrustManager().setAccessLevel(OWNER, FRIEND.uuid(), AccessLevel.ADMIN);
            logic.setTargetPlayers(true);
            player(105, 65, 100, FRIEND);
            search();
            assertEquals(Optional.empty(), targetId());
        }
    }
}
