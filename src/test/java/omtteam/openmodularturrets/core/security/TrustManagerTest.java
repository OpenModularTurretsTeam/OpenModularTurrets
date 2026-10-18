package omtteam.openmodularturrets.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrustManagerTest {
    private static final PlayerInfo OWNER = player("Owner");
    private static final PlayerInfo ALICE = player("Alice");
    private static final PlayerInfo BOB = player("Bob");
    private static final PlayerInfo STRANGER = player("Stranger");

    private FakeSecurityEnvironment environment;
    private TrustManager manager;
    private int changes;

    private static PlayerInfo player(String name) {
        return new PlayerInfo(UUID.nameUUIDFromBytes(name.getBytes()), name, false);
    }

    private static PlayerInfo operator(PlayerInfo player) {
        return new PlayerInfo(player.uuid(), player.name(), true);
    }

    @BeforeEach
    void setUp() {
        environment = new FakeSecurityEnvironment();
        manager = new TrustManager(environment, () -> changes++);
        manager.setOwner(OWNER.identity());
        changes = 0;
    }

    /** Adds a player as owner and sets the level directly. */
    private void trust(PlayerInfo player, AccessLevel level) {
        assertEquals(TrustResult.SUCCESS, manager.addTrustedPlayer(OWNER, player.identity()));
        assertEquals(TrustResult.SUCCESS, manager.setAccessLevel(OWNER, player.uuid(), level));
    }

    @Nested
    class AccessLevels {
        @Test
        void ownerIsAdmin() {
            assertEquals(Optional.of(AccessLevel.ADMIN), manager.getAccessLevel(OWNER));
            assertTrue(manager.canOpenGui(OWNER));
            assertTrue(manager.canChangeSettings(OWNER));
            assertTrue(manager.isAdmin(OWNER));
            assertTrue(manager.canPerformOwnerAction(OWNER));
        }

        @Test
        void strangerHasNoAccess() {
            assertEquals(Optional.empty(), manager.getAccessLevel(STRANGER));
            assertFalse(manager.canOpenGui(STRANGER));
            assertFalse(manager.canChangeSettings(STRANGER));
            assertFalse(manager.isAdmin(STRANGER));
        }

        @Test
        void eachLevelIncludesTheLowerOnes() {
            trust(ALICE, AccessLevel.NONE);
            assertFalse(manager.canOpenGui(ALICE));

            manager.setAccessLevel(OWNER, ALICE.uuid(), AccessLevel.OPEN_GUI);
            assertTrue(manager.canOpenGui(ALICE));
            assertFalse(manager.canChangeSettings(ALICE));

            manager.setAccessLevel(OWNER, ALICE.uuid(), AccessLevel.CHANGE_SETTINGS);
            assertTrue(manager.canChangeSettings(ALICE));
            assertFalse(manager.isAdmin(ALICE));

            manager.setAccessLevel(OWNER, ALICE.uuid(), AccessLevel.ADMIN);
            assertTrue(manager.isAdmin(ALICE));
        }

        @Test
        void onlyTheOwnerMayPerformOwnerActions() {
            trust(ALICE, AccessLevel.ADMIN);
            assertFalse(manager.canPerformOwnerAction(ALICE));
        }

        @Test
        void operatorsGetGuiAccessOnlyIfEnabled() {
            PlayerInfo op = operator(STRANGER);
            assertFalse(manager.canOpenGui(op));

            environment.operatorsCanAccess = true;
            assertEquals(Optional.of(AccessLevel.OPEN_GUI), manager.getAccessLevel(op));
            assertFalse(manager.canChangeSettings(op));
        }

        @Test
        void operatorKeepsHigherTrustLevel() {
            environment.operatorsCanAccess = true;
            trust(ALICE, AccessLevel.CHANGE_SETTINGS);
            assertEquals(Optional.of(AccessLevel.CHANGE_SETTINGS), manager.getAccessLevel(operator(ALICE)));
        }

        @Test
        void identityIsTheUuidNotTheName() {
            trust(ALICE, AccessLevel.ADMIN);
            PlayerInfo impostor = new PlayerInfo(UUID.randomUUID(), "Alice", false);
            assertFalse(manager.canOpenGui(impostor));
        }
    }

    @Nested
    class Teams {
        @Test
        void ownerTeamMembersAreTrustedAtLowestLevel() {
            environment.teams.put("Owner", "red");
            environment.teams.put("Bob", "red");
            assertEquals(Optional.of(AccessLevel.NONE), manager.getAccessLevel(BOB));
            assertFalse(manager.canOpenGui(BOB));
            assertTrue(manager.isProtectedFromTargeting(BOB));
        }

        @Test
        void listEntryWinsOverTeamMembership() {
            environment.teams.put("Owner", "red");
            environment.teams.put("Alice", "red");
            trust(ALICE, AccessLevel.CHANGE_SETTINGS);
            assertEquals(Optional.of(AccessLevel.CHANGE_SETTINGS), manager.getAccessLevel(ALICE));
        }

        @Test
        void otherTeamsAndNoTeamAreNotTrusted() {
            environment.teams.put("Owner", "red");
            environment.teams.put("Bob", "blue");
            assertFalse(manager.isTeamMember(BOB.identity()));
            assertFalse(manager.isTeamMember(STRANGER.identity()));
        }

        @Test
        void playersWithoutTeamAreNotTeamMembersOfATeamlessOwner() {
            assertFalse(manager.isTeamMember(STRANGER.identity()));
        }
    }

    @Nested
    class Targeting {
        @Test
        void ownerAndTrustedPlayersAreNeverTargeted() {
            trust(ALICE, AccessLevel.NONE);
            assertTrue(manager.isProtectedFromTargeting(OWNER));
            assertTrue(manager.isProtectedFromTargeting(ALICE));
            assertFalse(manager.isProtectedFromTargeting(STRANGER));
        }

        @Test
        void trustedPlayersStayProtectedEvenIfTheyMayBeDamaged() {
            environment.damageTrusted = true;
            trust(ALICE, AccessLevel.NONE);
            assertTrue(manager.isProtectedFromTargeting(ALICE));
            assertTrue(manager.canDamagePlayer(ALICE.identity()));
        }

        @Test
        void trustedPlayersAreNotDamagedByDefault() {
            trust(ALICE, AccessLevel.NONE);
            assertFalse(manager.canDamagePlayer(ALICE.identity()));
            assertTrue(manager.canDamagePlayer(STRANGER.identity()));
        }

        @Test
        void ownerAndTeamAreNeverDamaged() {
            environment.damageTrusted = true;
            environment.teams.put("Owner", "red");
            environment.teams.put("Bob", "red");
            assertFalse(manager.canDamagePlayer(OWNER.identity()));
            assertFalse(manager.canDamagePlayer(BOB.identity()));
        }

        @Test
        void operatorsAreProtectedOnlyIfEnabled() {
            PlayerInfo op = operator(STRANGER);
            assertFalse(manager.isProtectedFromTargeting(op));
            environment.operatorsCanAccess = true;
            assertTrue(manager.isProtectedFromTargeting(op));
        }

        @Test
        void petOwnersAreCheckedByIdentity() {
            trust(ALICE, AccessLevel.NONE);
            assertTrue(manager.isOwnerOrTrusted(OWNER.identity()));
            assertTrue(manager.isOwnerOrTrusted(ALICE.identity()));
            assertFalse(manager.isOwnerOrTrusted(STRANGER.identity()));
        }
    }

    @Nested
    class TrustListChanges {
        @Test
        void newEntriesStartAtNone() {
            assertEquals(TrustResult.SUCCESS, manager.addTrustedPlayer(OWNER, ALICE.identity()));
            assertEquals(AccessLevel.NONE, manager.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(1, changes);
        }

        @Test
        void onlyAdminsMayChangeTheList() {
            trust(ALICE, AccessLevel.CHANGE_SETTINGS);
            changes = 0;
            assertEquals(TrustResult.NO_PERMISSION, manager.addTrustedPlayer(ALICE, BOB.identity()));
            assertEquals(TrustResult.NO_PERMISSION, manager.removeTrustedPlayer(ALICE, ALICE.uuid()));
            assertEquals(TrustResult.NO_PERMISSION, manager.changeAccessLevel(ALICE, ALICE.uuid(), 1));
            assertEquals(TrustResult.NO_PERMISSION, manager.setAccessLevel(STRANGER, ALICE.uuid(), AccessLevel.ADMIN));
            assertEquals(TrustResult.NO_PERMISSION, manager.addTrustedPlayer(STRANGER, STRANGER.identity()));
            assertEquals(0, changes);
            assertEquals(1, manager.getTrustedPlayers().size());
        }

        @Test
        void trustedAdminsMayManageTheList() {
            trust(ALICE, AccessLevel.ADMIN);
            assertEquals(TrustResult.SUCCESS, manager.addTrustedPlayer(ALICE, BOB.identity()));
            assertEquals(TrustResult.SUCCESS, manager.changeAccessLevel(ALICE, BOB.uuid(), 1));
            assertEquals(TrustResult.SUCCESS, manager.removeTrustedPlayer(ALICE, BOB.uuid()));
        }

        @Test
        void teamMembersAndOperatorsCannotManageTheList() {
            environment.teams.put("Owner", "red");
            environment.teams.put("Bob", "red");
            environment.operatorsCanAccess = true;
            assertEquals(TrustResult.NO_PERMISSION, manager.addTrustedPlayer(BOB, STRANGER.identity()));
            assertEquals(TrustResult.NO_PERMISSION, manager.addTrustedPlayer(operator(STRANGER), STRANGER.identity()));
        }

        @Test
        void duplicatesAndTheOwnerAreRejected() {
            trust(ALICE, AccessLevel.NONE);
            assertEquals(TrustResult.ALREADY_TRUSTED, manager.addTrustedPlayer(OWNER, ALICE.identity()));
            assertEquals(TrustResult.ALREADY_TRUSTED,
                    manager.addTrustedPlayer(OWNER, ALICE.identity().withName("AliceRenamed")));
            assertEquals(TrustResult.IS_OWNER, manager.addTrustedPlayer(OWNER, OWNER.identity()));
            assertEquals(1, manager.getTrustedPlayers().size());
        }

        @Test
        void accessLevelStepsStopAtTheEnds() {
            trust(ALICE, AccessLevel.NONE);
            changes = 0;
            assertEquals(TrustResult.OUT_OF_RANGE, manager.changeAccessLevel(OWNER, ALICE.uuid(), -1));
            for (int i = 0; i < 3; i++) {
                assertEquals(TrustResult.SUCCESS, manager.changeAccessLevel(OWNER, ALICE.uuid(), 1));
            }
            assertEquals(AccessLevel.ADMIN, manager.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(TrustResult.OUT_OF_RANGE, manager.changeAccessLevel(OWNER, ALICE.uuid(), 1));
            assertEquals(3, changes, "each successful step notifies, the refused ones do not");
            assertEquals(TrustResult.SUCCESS, manager.setAccessLevel(OWNER, ALICE.uuid(), AccessLevel.NONE));
            assertEquals(4, changes);
        }

        @Test
        void unknownPlayersAreReported() {
            assertEquals(TrustResult.NOT_FOUND, manager.removeTrustedPlayer(OWNER, ALICE.uuid()));
            assertEquals(TrustResult.NOT_FOUND, manager.changeAccessLevel(OWNER, ALICE.uuid(), 1));
            assertEquals(TrustResult.NOT_FOUND, manager.setAccessLevel(OWNER, ALICE.uuid(), AccessLevel.ADMIN));
        }

        @Test
        void removingRevokesAccessImmediately() {
            trust(ALICE, AccessLevel.ADMIN);
            trust(BOB, AccessLevel.OPEN_GUI);
            changes = 0;
            assertEquals(TrustResult.SUCCESS, manager.removeTrustedPlayer(OWNER, ALICE.uuid()));
            assertFalse(manager.canOpenGui(ALICE));
            assertFalse(manager.isProtectedFromTargeting(ALICE));
            assertTrue(manager.canOpenGui(BOB), "only the removed player loses access");
            assertEquals(1, changes);
        }
    }

    @Nested
    class OwnerAndLoading {
        @Test
        void baseWithoutOwnerGrantsNothing() {
            TrustManager empty = new TrustManager(environment, () -> {
            });
            assertNull(empty.getOwner());
            assertFalse(empty.canOpenGui(OWNER));
            assertFalse(empty.isTeamMember(BOB.identity()));
        }

        @Test
        void settingTheOwnerRemovesTheirListEntry() {
            trust(ALICE, AccessLevel.ADMIN);
            trust(BOB, AccessLevel.NONE);
            changes = 0;
            manager.setOwner(ALICE.identity());
            assertEquals(List.of(BOB.uuid()), manager.getTrustedPlayers().stream().map(TrustedPlayer::getUuid).toList(),
                    "only the new owner's own entry is removed");
            assertFalse(manager.isAdmin(OWNER));
            assertEquals(1, changes);
        }

        @Test
        void loadSkipsOwnerAndDuplicates() {
            changes = 0;
            manager.load(OWNER.identity(), List.of(
                    new TrustedPlayer(ALICE.identity(), AccessLevel.ADMIN),
                    new TrustedPlayer(ALICE.identity(), AccessLevel.NONE),
                    new TrustedPlayer(OWNER.identity(), AccessLevel.NONE)));
            assertEquals(1, manager.getTrustedPlayers().size());
            assertEquals(AccessLevel.ADMIN, manager.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(1, changes);
        }

        @Test
        void returnedListIsReadOnly() {
            trust(ALICE, AccessLevel.NONE);
            List<TrustedPlayer> list = manager.getTrustedPlayers();
            assertThrows(UnsupportedOperationException.class, list::clear);
        }

        @Test
        void refreshNameUpdatesDisplayNamesOnly() {
            trust(ALICE, AccessLevel.CHANGE_SETTINGS);
            PlayerInfo renamed = new PlayerInfo(ALICE.uuid(), "Alicia", false);
            changes = 0;
            manager.refreshName(renamed);
            manager.refreshName(new PlayerInfo(OWNER.uuid(), "Boss", false));
            assertEquals("Alicia", manager.getTrustedPlayer(ALICE.uuid()).orElseThrow().getName());
            assertEquals("Boss", manager.getOwner().name());
            assertTrue(manager.canChangeSettings(renamed));
            assertEquals(2, changes);
            manager.refreshName(renamed);
            assertEquals(2, changes, "an unchanged name changes nothing");
        }
    }

    @Nested
    class TrapProfile {
        private TrustManager trap;

        @BeforeEach
        void setUpTrap() {
            trap = new TrustManager(environment, () -> changes++, AccessProfile.TRUSTED_AND_ADMIN);
            trap.setOwner(OWNER.identity());
        }

        @Test
        void levelsStepBetweenTrustedAndAdminOnly() {
            assertEquals(TrustResult.SUCCESS, trap.addTrustedPlayer(OWNER, ALICE.identity()));
            assertEquals(AccessLevel.NONE, trap.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(TrustResult.SUCCESS, trap.changeAccessLevel(OWNER, ALICE.uuid(), 1));
            assertEquals(AccessLevel.ADMIN, trap.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(TrustResult.OUT_OF_RANGE, trap.changeAccessLevel(OWNER, ALICE.uuid(), 1));
            assertEquals(TrustResult.SUCCESS, trap.changeAccessLevel(OWNER, ALICE.uuid(), -1));
            assertEquals(AccessLevel.NONE, trap.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(TrustResult.OUT_OF_RANGE, trap.changeAccessLevel(OWNER, ALICE.uuid(), -1));
        }

        @Test
        void levelsOutsideTheProfileCannotBeSet() {
            assertEquals(TrustResult.SUCCESS, trap.addTrustedPlayer(OWNER, ALICE.identity()));
            assertEquals(TrustResult.OUT_OF_RANGE, trap.setAccessLevel(OWNER, ALICE.uuid(), AccessLevel.OPEN_GUI));
            assertEquals(AccessLevel.NONE, trap.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
        }

        @Test
        void loadingClampsLevelsToTheProfile() {
            trap.load(OWNER.identity(), List.of(
                    new TrustedPlayer(ALICE.identity(), AccessLevel.CHANGE_SETTINGS),
                    new TrustedPlayer(BOB.identity(), AccessLevel.ADMIN)));
            assertEquals(AccessLevel.NONE, trap.getTrustedPlayer(ALICE.uuid()).orElseThrow().getAccessLevel());
            assertEquals(AccessLevel.ADMIN, trap.getTrustedPlayer(BOB.uuid()).orElseThrow().getAccessLevel());
        }

        @Test
        void trustedPlayersAreFriendlyButNotAdmins() {
            assertEquals(TrustResult.SUCCESS, trap.addTrustedPlayer(OWNER, ALICE.identity()));
            assertTrue(trap.isOwnerOrTrusted(ALICE.identity()));
            assertFalse(trap.isAdmin(ALICE));
            assertFalse(trap.isOwnerOrTrusted(STRANGER.identity()));
            assertTrue(trap.isAdmin(OWNER));
        }

        @Test
        void operatorAccessIsClampedToTheProfile() {
            environment.operatorsCanAccess = true;
            assertEquals(AccessLevel.NONE, trap.getAccessLevel(operator(STRANGER)).orElseThrow());
            assertFalse(trap.isAdmin(operator(STRANGER)));
        }
    }
}
