package omtteam.openmodularturrets.core.computer;

import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.core.security.SecurityEnvironment;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.omlib.core.world.Face;
import omtteam.openmodularturrets.core.base.FakeSettings;
import omtteam.openmodularturrets.core.base.FakeSlot;
import omtteam.openmodularturrets.core.base.FakeWorld;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands.Command;
import omtteam.openmodularturrets.core.item.AddonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Ported from 1.12.2 {@code compat.computer.BaseComputerCommandsTest}, plus the 1.20.1+ additions. */
class BaseComputerCommandsTest {
    private static final PlayerInfo OWNER = new PlayerInfo(UUID.randomUUID(), "owner", false);
    private static final PlayerIdentity FRIEND = new PlayerIdentity(UUID.randomUUID(), "friend");
    private static final PlayerIdentity STRANGER = new PlayerIdentity(UUID.randomUUID(), "Stranger");

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

    /** Turrets on some faces; records what the commands do to them. */
    private static final class FakeTurrets implements ComputerTurrets {
        final Map<Face, float[]> aims = new EnumMap<>(Face.class);
        final Map<Face, Boolean> autoFire = new EnumMap<>(Face.class);
        /** Turrets that fire when asked to. */
        final Set<Face> ready = EnumSet.noneOf(Face.class);
        final List<Face> shots = new ArrayList<>();

        FakeTurrets(Face... faces) {
            for (Face face : faces) {
                aims.put(face, new float[]{0, 0});
                autoFire.put(face, false);
            }
        }

        @Override
        public boolean setYawPitch(Face face, float yaw, float pitch) {
            if (!aims.containsKey(face)) return false;
            aims.put(face, new float[]{yaw, pitch});
            return true;
        }

        @Override
        public boolean setAutoFire(Face face, boolean enabled) {
            if (!aims.containsKey(face)) return false;
            autoFire.put(face, enabled);
            return true;
        }

        @Override
        public boolean forceShot(Face face) {
            if (!ready.contains(face)) return false;
            shots.add(face);
            return true;
        }

        @Override
        public Set<Face> faces() {
            return aims.keySet();
        }
    }

    private FakeWorld world;
    private FakeSettings settings;
    private int syncCount;
    private TurretBaseLogic logic;
    private FakeTurrets turrets;
    private final Map<String, PlayerIdentity> knownPlayers = new HashMap<>();
    private BaseComputerCommands commands;

    @BeforeEach
    void setUp() {
        world = new FakeWorld();
        settings = new FakeSettings();
        syncCount = 0;
        logic = base(5); // tier 5 bases are computer accessible
        logic.getTrustManager().addTrustedPlayer(OWNER, FRIEND); // at AccessLevel.NONE
        turrets = new FakeTurrets(Face.UP, Face.NORTH);
        knownPlayers.put(STRANGER.name(), STRANGER);
        knownPlayers.put(FRIEND.name(), FRIEND);
        knownPlayers.put(OWNER.name(), OWNER.identity());
        commands = commands(logic);
    }

    private TurretBaseLogic base(int tier) {
        TurretBaseLogic base = new TurretBaseLogic(tier, settings, NO_TEAMS, world, RandomGenerator.of("L64X128MixRandom"),
                clientRelevant -> {
                    if (clientRelevant) syncCount++;
                });
        base.getTrustManager().setOwner(OWNER.identity());
        base.updateNeighbours(Map.of());
        return base;
    }

    private BaseComputerCommands commands(TurretBaseLogic base) {
        return new BaseComputerCommands(base, turrets, name -> Optional.ofNullable(knownPlayers.get(name)));
    }

    private Optional<TrustedPlayer> trusted(String name) {
        return logic.getTrustManager().getTrustedPlayers().stream().filter(p -> p.getName().equals(name)).findFirst();
    }

    private AccessLevel level(String name) {
        return trusted(name).orElseThrow().getAccessLevel();
    }

    private static Object[] result(Object... values) {
        return values;
    }

    // ---- ported from 1.12.2 ----

    /** 3.x set the level on a copy of the trusted player, so the change was lost. */
    @Test
    void changeAccessLevelIsSaved() {
        int syncsBefore = syncCount;
        assertArrayEquals(result(true), commands.call(Command.changeAccessLevel, new Object[]{"friend", 3.0}));
        assertEquals(AccessLevel.ADMIN, level("friend"));
        assertEquals(syncsBefore + 1, syncCount, "the trust manager saves and syncs the change");
        commands.call(Command.changeAccessLevel, new Object[]{"friend", "1"});
        assertEquals(AccessLevel.OPEN_GUI, level("friend"));
        commands.call(Command.changeAccessLevel, new Object[]{"FRIEND", 2.0});
        assertEquals(AccessLevel.CHANGE_SETTINGS, level("friend"), "names ignore case");
    }

    @Test
    void invalidAccessLevelChangesNothing() {
        assertArrayEquals(result("Invalid Access Level!"),
                commands.call(Command.changeAccessLevel, new Object[]{"friend", 4.0}));
        assertArrayEquals(result("Invalid Access Level!"),
                commands.call(Command.changeAccessLevel, new Object[]{"friend", 1.5}));
        assertArrayEquals(result("Invalid Access Level!"), commands.call(Command.changeAccessLevel, new Object[]{"friend"}));
        assertEquals(AccessLevel.NONE, level("friend"));
        assertArrayEquals(result("Not found!"), commands.call(Command.changeAccessLevel, new Object[]{"stranger", 1.0}));
    }

    @Test
    void missingOrWrongArgumentsGiveAnErrorInsteadOfAnException() {
        Object[] wrong = {BaseComputerCommands.WRONG_PARAMETERS};
        assertArrayEquals(wrong, commands.call(Command.setAttacksMobs, new Object[0]));
        assertArrayEquals(wrong, commands.call(Command.setAttacksMobs, new Object[]{"yes"}));
        assertArrayEquals(wrong, commands.call(Command.setTurretYawPitch, new Object[]{6.0, 10.0, 20.0}));
        assertArrayEquals(wrong, commands.call(Command.setTurretYawPitch, new Object[]{1.0, "up", 20.0}));
        assertArrayEquals(wrong, commands.call(Command.forceShootTurret, new Object[]{-1.0}));
        assertArrayEquals(wrong, commands.call(Command.removeTrustedPlayer, new Object[0]));
        assertArrayEquals(wrong, commands.call(Command.setComputerControl, new Object[]{1.0}));
        assertArrayEquals(wrong, commands.call(Command.setAllYawPitch, new Object[]{Double.POSITIVE_INFINITY, 0.0}));
        assertArrayEquals(wrong, commands.call(Command.setAllAutoForceFire, new Object[]{null}));
    }

    @Test
    void setTargetingAcceptsBooleansAndStrings() {
        assertArrayEquals(result(true), commands.call(Command.setAttacksMobs, new Object[]{false}));
        assertFalse(logic.getTargetingSettings().isTargetMobs());
        commands.call(Command.setAttacksMobs, new Object[]{"true"});
        assertTrue(logic.getTargetingSettings().isTargetMobs());
        commands.call(Command.setAttacksPlayers, new Object[]{true});
        assertArrayEquals(result(true), commands.call(Command.isAttacksPlayers, new Object[0]));
        commands.call(Command.setAttacksNeutrals, new Object[]{true});
        assertTrue(logic.getTargetingSettings().isTargetPassive());
        assertArrayEquals(result(true), commands.call(Command.isAttacksNeutrals, new Object[0]));
        assertArrayEquals(result(true), commands.call(Command.isAttacksMobs, new Object[0]));
    }

    @Test
    void withoutComputerAccessNothingRuns() {
        BaseComputerCommands tierTwo = commands(base(2));
        assertArrayEquals(result(BaseComputerCommands.NO_ACCESS),
                tierTwo.call(Command.setComputerControl, new Object[]{true}));
        assertArrayEquals(result(BaseComputerCommands.NO_ACCESS), tierTwo.modeDescription());
        assertArrayEquals(result(BaseComputerCommands.NO_ACCESS), tierTwo.call(Command.getType, new Object[0]));
    }

    @Test
    void aSerialPortGivesAccess() {
        world.addonSlots.set(0, FakeSlot.addon(AddonType.SERIAL_PORT, 1));
        assertArrayEquals(result(BaseComputerCommands.TYPE), commands(base(2)).call(Command.getType, new Object[0]));
        assertArrayEquals(result(BaseComputerCommands.NO_ACCESS),
                commands(base(1)).call(Command.getType, new Object[0]), "tier 1 bases have no addon slots");
    }

    @Test
    void arguments() {
        assertEquals(Integer.valueOf(2), BaseComputerCommands.intArgument(new Object[]{2.0}, 0));
        assertEquals(Integer.valueOf(3), BaseComputerCommands.intArgument(new Object[]{"3"}, 0));
        assertNull(BaseComputerCommands.intArgument(new Object[]{2.5}, 0));
        assertNull(BaseComputerCommands.intArgument(new Object[0], 0));
        assertEquals(Face.EAST, BaseComputerCommands.faceArgument(new Object[]{5.0}, 0));
        assertEquals(Face.DOWN, BaseComputerCommands.faceArgument(new Object[]{0.0}, 0));
        assertNull(BaseComputerCommands.faceArgument(new Object[]{6.0}, 0));
        assertNull(BaseComputerCommands.floatArgument(new Object[]{Double.NaN}, 0));
        assertNull(BaseComputerCommands.floatArgument(new Object[]{1e39}, 0), "infinite as a float");
        assertEquals(Float.valueOf(-45.5F), BaseComputerCommands.floatArgument(new Object[]{-45.5}, 0));
        assertEquals(AccessLevel.ADMIN, BaseComputerCommands.accessLevelArgument(new Object[]{3.0}, 0));
        assertNull(BaseComputerCommands.booleanArgument(new Object[]{1.0}, 0));
        assertNull(BaseComputerCommands.stringArgument(new Object[]{""}, 0));
    }

    // ---- owner and trusted players ----

    @Test
    void ownerName() {
        assertArrayEquals(result("owner"), commands.call(Command.getOwner, new Object[0]));
        logic.getTrustManager().clearOwner();
        assertArrayEquals(result("no_owner"), commands.call(Command.getOwner, new Object[0]));
    }

    @Test
    void trustedPlayersAsMapsLikeTheOldApi() {
        Map<String, String> friend = Map.of("name", "friend", "accessLevel", "0: none");
        assertArrayEquals(result(List.of(friend)), commands.call(Command.getTrustedPlayers, new Object[0]));
        assertArrayEquals(result(friend), commands.call(Command.getTrustedPlayer, new Object[]{"FRIEND"}));
        assertArrayEquals(result("Not found!"), commands.call(Command.getTrustedPlayer, new Object[]{"stranger"}));
        assertArrayEquals(result("Not found!"), commands.call(Command.getTrustedPlayer, new Object[0]));
        commands.call(Command.changeAccessLevel, new Object[]{"friend", 3.0});
        assertArrayEquals(result(Map.of("name", "friend", "accessLevel", "3: admin")),
                commands.call(Command.getTrustedPlayer, new Object[]{"friend"}));
    }

    @Test
    void addTrustedPlayer() {
        assertArrayEquals(result("successfully added"),
                commands.call(Command.addTrustedPlayer, new Object[]{"Stranger"}));
        assertEquals(AccessLevel.NONE, level("Stranger"));
        assertEquals(STRANGER.uuid(), trusted("Stranger").orElseThrow().getUuid());
    }

    @Test
    void addTrustedPlayerWithALevel() {
        assertArrayEquals(result(true), commands.call(Command.addTrustedPlayer, new Object[]{"Stranger", 2.0}));
        assertEquals(AccessLevel.CHANGE_SETTINGS, level("Stranger"));
    }

    @Test
    void addTrustedPlayerRefusesUnknownListedAndOwner() {
        Object[] invalid = result("Name not valid!");
        assertArrayEquals(invalid, commands.call(Command.addTrustedPlayer, new Object[]{"nobody"}));
        assertArrayEquals(invalid, commands.call(Command.addTrustedPlayer, new Object[]{"friend"}));
        assertArrayEquals(invalid, commands.call(Command.addTrustedPlayer, new Object[]{"owner"}));
        assertArrayEquals(result(BaseComputerCommands.WRONG_PARAMETERS),
                commands.call(Command.addTrustedPlayer, new Object[]{"Stranger", 4.0}));
        assertArrayEquals(result(BaseComputerCommands.WRONG_PARAMETERS),
                commands.call(Command.addTrustedPlayer, new Object[0]));
        assertEquals(1, logic.getTrustManager().getTrustedPlayers().size());
    }

    @Test
    void removeTrustedPlayerIgnoresCase() {
        assertArrayEquals(result(true), commands.call(Command.removeTrustedPlayer, new Object[]{"nobody"}));
        assertEquals(1, logic.getTrustManager().getTrustedPlayers().size());
        assertArrayEquals(result(true), commands.call(Command.removeTrustedPlayer, new Object[]{"FRIEND"}));
        assertTrue(logic.getTrustManager().getTrustedPlayers().isEmpty());
    }

    @Test
    void trustChangesNeedAnOwner() {
        logic.getTrustManager().clearOwner();
        Object[] noOwner = result("No owner!");
        assertArrayEquals(noOwner, commands.call(Command.addTrustedPlayer, new Object[]{"Stranger"}));
        assertArrayEquals(noOwner, commands.call(Command.removeTrustedPlayer, new Object[]{"friend"}));
        assertArrayEquals(noOwner, commands.call(Command.changeAccessLevel, new Object[]{"friend", 1.0}));
        assertTrue(logic.getTrustManager().getTrustedPlayers().isEmpty());
    }

    // ---- base ----

    @Test
    void modes() {
        assertArrayEquals(result("inverted"), commands.call(Command.getMode, new Object[0]));
        assertArrayEquals(result("2 - inverted"), commands.modeDescription());
        assertArrayEquals(result(true), commands.call(Command.setMode, new Object[]{0.0}));
        assertEquals(MachineMode.ALWAYS_ON, logic.getMode());
        assertArrayEquals(result("always_on"), commands.call(Command.getMode, new Object[0]));
        assertArrayEquals(result("0 - Always on"), commands.modeDescription());
        assertArrayEquals(result("Wrong parameters! expects number between 0 and 3",
                        "0 - Always on, 1 - always off, 2 - inverted, 3 - not inverted"),
                commands.call(Command.setMode, new Object[]{4.0}));
        assertEquals(MachineMode.ALWAYS_ON, logic.getMode());
    }

    @Test
    void activeAndRedstone() {
        assertArrayEquals(result(true), commands.call(Command.getActive, new Object[0]));
        assertArrayEquals(result(false), commands.call(Command.getRedstone, new Object[0]));
        logic.setRedstone(true);
        assertArrayEquals(result(false), commands.call(Command.getActive, new Object[0]));
        assertArrayEquals(result(true), commands.call(Command.getRedstone, new Object[0]));
    }

    @Test
    void typeAndEnergy() {
        assertArrayEquals(result("turret_base"), commands.call(Command.getType, new Object[0]));
        logic.getEnergy().generate(1234);
        assertArrayEquals(result(logic.getEnergy().getCapacity()),
                commands.call(Command.getMaxEnergyStorage, new Object[0]));
        assertArrayEquals(result(1234L), commands.call(Command.getCurrentEnergyStorage, new Object[0]));
    }

    @Test
    void computerControl() {
        assertArrayEquals(result(false), commands.call(Command.isComputerControl, new Object[0]));
        assertArrayEquals(result(true), commands.call(Command.setComputerControl, new Object[]{true}));
        assertTrue(logic.isComputerControlled());
        assertArrayEquals(result(true), commands.call(Command.isComputerControl, new Object[0]));
        commands.call(Command.setComputerControl, new Object[]{"false"});
        assertFalse(logic.isComputerControlled());
    }

    // ---- turrets ----

    @Test
    void yawPitchForOneOrAllTurrets() {
        assertArrayEquals(result(true), commands.call(Command.setTurretYawPitch, new Object[]{1.0, 90.0, 10.0}));
        assertArrayEquals(new float[]{90, 10}, turrets.aims.get(Face.UP));
        assertArrayEquals(new float[]{0, 0}, turrets.aims.get(Face.NORTH));
        assertArrayEquals(result(false), commands.call(Command.setTurretYawPitch, new Object[]{0.0, 90.0, 10.0}),
                "no turret on the bottom face");
        assertArrayEquals(result("set yaw and pitch successfully"),
                commands.call(Command.setAllYawPitch, new Object[]{"45", -20.0}));
        assertArrayEquals(new float[]{45, -20}, turrets.aims.get(Face.UP));
        assertArrayEquals(new float[]{45, -20}, turrets.aims.get(Face.NORTH));
    }

    @Test
    void autoFireForOneOrAllTurrets() {
        assertArrayEquals(result(true), commands.call(Command.setTurretAutoForceFire, new Object[]{2.0, true}));
        assertEquals(Map.of(Face.UP, false, Face.NORTH, true), turrets.autoFire);
        assertArrayEquals(result(false), commands.call(Command.setTurretAutoForceFire, new Object[]{5.0, true}));
        assertArrayEquals(result(true), commands.call(Command.setAllAutoForceFire, new Object[]{true}));
        assertEquals(Map.of(Face.UP, true, Face.NORTH, true), turrets.autoFire);
    }

    @Test
    void forceShots() {
        turrets.ready.add(Face.NORTH);
        assertArrayEquals(result(false), commands.call(Command.forceShootTurret, new Object[]{1.0}));
        assertArrayEquals(result(true), commands.call(Command.forceShootTurret, new Object[]{2.0}));
        assertArrayEquals(result(1), commands.call(Command.forceShootAllTurrets, new Object[0]),
                "counts the turrets that fired");
        assertEquals(List.of(Face.NORTH, Face.NORTH), turrets.shots);
    }
}
