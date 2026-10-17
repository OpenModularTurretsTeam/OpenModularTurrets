package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.card.MemoryCardData;
import omtteam.openmodularturrets.core.card.MemoryCardRules;
import omtteam.openmodularturrets.core.camo.CamoLight;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.AccessProfile;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.SecurityEnvironment;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.security.TrustResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Memory card rules; lives next to the base logic tests to share their fakes.
 */
class MemoryCardRulesTest {
    private static final PlayerInfo OWNER = player("Owner");
    private static final PlayerInfo OTHER_OWNER = player("OtherOwner");
    private static final PlayerInfo ADMIN = player("Admin");
    private static final PlayerInfo SETTINGS = player("Settings");
    private static final PlayerInfo OPENER = player("Opener");
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
        public boolean turretsDamageTrustedPlayers() {
            return false;
        }
    };

    private FakeSettings settings;
    private FakeWorld world;

    private static PlayerInfo player(String name) {
        return new PlayerInfo(UUID.nameUUIDFromBytes(name.getBytes()), name, false);
    }

    @BeforeEach
    void setUp() {
        settings = new FakeSettings();
        world = new FakeWorld();
    }

    private TurretBaseLogic base(PlayerInfo owner) {
        TurretBaseLogic logic = new TurretBaseLogic(4, settings, NO_TEAMS, world, RandomGenerator.getDefault(), changed -> {
        });
        logic.getTrustManager().setOwner(owner.identity());
        return logic;
    }

    private static void trust(TurretBaseLogic logic, PlayerInfo owner, PlayerInfo player, AccessLevel level) {
        assertEquals(TrustResult.SUCCESS, logic.getTrustManager().addTrustedPlayer(owner, player.identity()));
        assertEquals(TrustResult.SUCCESS, logic.getTrustManager().setAccessLevel(owner, player.uuid(), level));
    }

    @Test
    void settingsSurviveACopyAndPaste() {
        TurretBaseLogic source = base(OWNER);
        source.setMode(MachineMode.NONINVERTED);
        source.setMultiTargeting(true);
        source.setTargetPlayers(true);
        source.setTargetMobs(false);
        source.setTargetPassive(true);
        trust(source, OWNER, ADMIN, AccessLevel.ADMIN);
        trust(source, OWNER, OPENER, AccessLevel.OPEN_GUI);

        TurretBaseLogic target = base(OTHER_OWNER);
        MemoryCardRules.apply(MemoryCardRules.capture(source), target);
        MemoryCardRules.applyTrust(MemoryCardRules.captureTrust(source.getTrustManager()), target.getTrustManager());

        assertEquals(MachineMode.NONINVERTED, target.getMode());
        assertTrue(target.isMultiTargeting());
        assertTrue(target.getTargetingSettings().isTargetPlayers());
        assertFalse(target.getTargetingSettings().isTargetMobs());
        assertTrue(target.getTargetingSettings().isTargetPassive());
        assertEquals(AccessLevel.ADMIN, target.getTrustManager().getTrustedPlayer(ADMIN.uuid()).orElseThrow().getAccessLevel());
        assertEquals(AccessLevel.OPEN_GUI, target.getTrustManager().getTrustedPlayer(OPENER.uuid()).orElseThrow().getAccessLevel());
    }

    @Test
    void pastingKeepsTheOwnerOfTheTargetAndDropsItFromTheList() {
        TurretBaseLogic source = base(OWNER);
        trust(source, OWNER, OTHER_OWNER, AccessLevel.ADMIN);
        TurretBaseLogic target = base(OTHER_OWNER);

        MemoryCardRules.applyTrust(MemoryCardRules.captureTrust(source.getTrustManager()), target.getTrustManager());

        assertEquals(OTHER_OWNER.identity(), target.getTrustManager().getOwner());
        assertTrue(target.getTrustManager().getTrustedPlayers().isEmpty());
    }

    @Test
    void pastingReplacesTheExistingTrustList() {
        TurretBaseLogic source = base(OWNER);
        trust(source, OWNER, ADMIN, AccessLevel.ADMIN);
        TurretBaseLogic target = base(OWNER);
        trust(target, OWNER, SETTINGS, AccessLevel.CHANGE_SETTINGS);

        MemoryCardRules.applyTrust(MemoryCardRules.captureTrust(source.getTrustManager()), target.getTrustManager());

        assertTrue(target.getTrustManager().getTrustedPlayer(SETTINGS.uuid()).isEmpty());
        assertTrue(target.getTrustManager().getTrustedPlayer(ADMIN.uuid()).isPresent());
    }

    @Test
    void capturedTrustListIsACopy() {
        TurretBaseLogic source = base(OWNER);
        trust(source, OWNER, ADMIN, AccessLevel.ADMIN);
        MemoryCardData.TrustSettings captured = MemoryCardRules.captureTrust(source.getTrustManager());

        source.getTrustManager().removeTrustedPlayer(OWNER, ADMIN.uuid());

        assertEquals(1, captured.trustedPlayers().size());
    }

    @Test
    void rangeIsClampedToWhatTheTargetCanReach() {
        TurretBaseLogic target = base(OWNER);
        MemoryCardData.BaseSettings settings = new MemoryCardData.BaseSettings(MachineMode.INVERTED, false, false,
                true, false, 99);

        MemoryCardRules.apply(settings, target);

        assertEquals(target.getTargetingSettings().getMaxRange(), target.getTargetingSettings().getRange());
    }

    @Test
    void onlyAdminsCopyAndPaste() {
        TurretBaseLogic logic = base(OWNER);
        trust(logic, OWNER, ADMIN, AccessLevel.ADMIN);
        trust(logic, OWNER, SETTINGS, AccessLevel.CHANGE_SETTINGS);
        trust(logic, OWNER, OPENER, AccessLevel.OPEN_GUI);
        var trust = logic.getTrustManager();

        assertTrue(MemoryCardRules.canCopy(trust, OWNER));
        assertTrue(MemoryCardRules.canPaste(trust, OWNER));
        assertTrue(MemoryCardRules.canCopy(trust, ADMIN));
        assertTrue(MemoryCardRules.canPaste(trust, ADMIN));
        for (PlayerInfo player : List.of(SETTINGS, OPENER, STRANGER)) {
            assertFalse(MemoryCardRules.canCopy(trust, player), player.name());
            assertFalse(MemoryCardRules.canPaste(trust, player), player.name());
        }
    }

    @Test
    void trustListsAreClampedToTheLevelsOfTheTarget() {
        TurretBaseLogic source = base(OWNER);
        trust(source, OWNER, SETTINGS, AccessLevel.CHANGE_SETTINGS);
        trust(source, OWNER, ADMIN, AccessLevel.ADMIN);
        TrustManager trap = new TrustManager(NO_TEAMS, () -> { }, AccessProfile.TRUSTED_AND_ADMIN);
        trap.setOwner(OTHER_OWNER.identity());

        MemoryCardRules.applyTrust(MemoryCardRules.captureTrust(source.getTrustManager()), trap);

        assertEquals(AccessLevel.NONE, trap.getTrustedPlayer(SETTINGS.uuid()).orElseThrow().getAccessLevel());
        assertEquals(AccessLevel.ADMIN, trap.getTrustedPlayer(ADMIN.uuid()).orElseThrow().getAccessLevel());
    }

    @Test
    void trapAdminsCopyAndPasteLikeBaseAdmins() {
        TrustManager trap = new TrustManager(NO_TEAMS, () -> { }, AccessProfile.TRUSTED_AND_ADMIN);
        trap.setOwner(OWNER.identity());
        assertEquals(TrustResult.SUCCESS, trap.addTrustedPlayer(OWNER, ADMIN.identity()));
        assertEquals(TrustResult.SUCCESS, trap.setAccessLevel(OWNER, ADMIN.uuid(), AccessLevel.ADMIN));
        assertEquals(TrustResult.SUCCESS, trap.addTrustedPlayer(OWNER, OPENER.identity()));

        assertTrue(MemoryCardRules.canCopy(trap, OWNER));
        assertTrue(MemoryCardRules.canPaste(trap, ADMIN));
        assertFalse(MemoryCardRules.canCopy(trap, OPENER));
        assertFalse(MemoryCardRules.canPaste(trap, STRANGER));
    }

    @Test
    void cardsFromABaseNeedBaseSettingsAndTrapCardsDoNot() {
        MemoryCardData.CamoSettings camo = new MemoryCardData.CamoSettings("minecraft:stone", CamoLight.DEFAULT);
        MemoryCardData.BaseSettings baseSettings = MemoryCardRules.capture(base(OWNER));
        MemoryCardData.TrustSettings noTrust = new MemoryCardData.TrustSettings(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> new MemoryCardData(MemoryCardData.Source.TURRET_BASE, null, noTrust, camo));
        assertThrows(IllegalArgumentException.class,
                () -> new MemoryCardData(MemoryCardData.Source.CAMO_TRAP, baseSettings, noTrust, camo));
        assertThrows(IllegalArgumentException.class,
                () -> new MemoryCardData(MemoryCardData.Source.CAMO_TRAP, null, noTrust, null));
        assertNull(new MemoryCardData(MemoryCardData.Source.CAMO_TRAP, null, noTrust, camo).base());
        assertNull(new MemoryCardData(MemoryCardData.Source.TURRET_BASE, baseSettings, noTrust, null).camo());
        assertEquals(camo, new MemoryCardData(MemoryCardData.Source.TURRET_BASE, baseSettings, noTrust, camo).camo());
    }
}
