package omtteam.openmodularturrets.core.base;

import omtteam.omlib.core.machine.MachineMode;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.SecurityEnvironment;
import omtteam.omlib.core.security.TrustManager;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretState.Concealment;
import omtteam.openmodularturrets.core.turret.TurretState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** {@link TurretState}; lives here for the base test fakes. */
class TurretStateTest {
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

    private final Random random = new Random(0);
    private FakeWorld world;
    private TurretBaseLogic logic;
    private TurretState state;

    @BeforeEach
    void setUp() {
        world = new FakeWorld();
        for (int i = 0; i < BaseTier.AMMO_SLOTS; i++) world.ammoSlots.add(FakeSlot.empty());
        logic = new TurretBaseLogic(5, new FakeSettings(), new TrustManager(NO_TEAMS, () -> {
        }), world, random, clientRelevant -> {
        });
        // a base without owner is inert
        logic.getTrustManager().setOwner(new PlayerIdentity(UUID.randomUUID(), "Owner"));
        logic.updateNeighbours(new EnumMap<>(Face.class));
        logic.setMode(MachineMode.ALWAYS_ON);
        state = new TurretState();
    }

    private void ticks(int count) {
        for (int i = 0; i < count; i++) state.tick();
    }

    @Nested
    class Shots {
        /** Machine gun: 8 ticks cooldown, 100 energy and one bullet per shot. */
        private FakeSlot bullets;

        @BeforeEach
        void armMachineGun() {
            logic.getEnergy().generate(1000);
            bullets = FakeSlot.ammo(AmmoType.BULLET, 5);
            world.ammoSlots.set(0, bullets);
        }

        @Test
        void waitsForTheCooldown() {
            ticks(7);
            assertFalse(state.beginShot(logic, TurretType.MACHINE_GUN_TURRET).isPresent());
            state.tick();
            Optional<TurretState.Shot> shot = state.beginShot(logic, TurretType.MACHINE_GUN_TURRET);
            assertTrue(shot.isPresent());
            assertEquals(1, shot.get().ammo().get().count());
            assertEquals(900, logic.getEnergy().getAmount());
            assertEquals(4, bullets.count);
            assertEquals(0, state.getTicksSinceShot(), "the cooldown restarts");
            assertFalse(state.beginShot(logic, TurretType.MACHINE_GUN_TURRET).isPresent());
        }

        @Test
        void inactiveBaseDoesNotShoot() {
            logic.setMode(MachineMode.ALWAYS_OFF);
            ticks(8);
            assertFalse(state.beginShot(logic, TurretType.MACHINE_GUN_TURRET).isPresent());
            assertEquals(1000, logic.getEnergy().getAmount());
            assertEquals(5, bullets.count);
        }

        @Test
        void missingEnergyConsumesNothing() {
            logic.getEnergy().consume(950);
            ticks(8);
            assertFalse(state.beginShot(logic, TurretType.MACHINE_GUN_TURRET).isPresent());
            assertEquals(50, logic.getEnergy().getAmount());
            assertEquals(5, bullets.count);
        }

        @Test
        void missingAmmoConsumesNoEnergy() {
            world.ammoSlots.set(0, FakeSlot.empty());
            ticks(8);
            assertFalse(state.beginShot(logic, TurretType.MACHINE_GUN_TURRET).isPresent());
            assertEquals(1000, logic.getEnergy().getAmount());
            assertTrue(state.getTicksSinceShot() >= 8, "the cooldown keeps running");
        }

        @Test
        void energyTurretsShootWithoutAmmo() {
            logic.getEnergy().generate(20000);
            ticks(10);
            Optional<TurretState.Shot> shot = state.beginShot(logic, TurretType.LASER_TURRET);
            assertTrue(shot.isPresent());
            assertFalse(shot.get().ammo().isPresent());
            assertEquals(5, bullets.count);
        }

        @Test
        void readinessIgnoresAmmoAndConsumesNothing() {
            world.ammoSlots.set(0, FakeSlot.empty());
            ticks(7);
            assertFalse(state.isReady(logic, TurretType.MACHINE_GUN_TURRET), "cooldown not passed");
            state.tick();
            assertTrue(state.isReady(logic, TurretType.MACHINE_GUN_TURRET), "no ammo, but ready");
            assertEquals(1000, logic.getEnergy().getAmount());
            logic.setMode(MachineMode.ALWAYS_OFF);
            assertFalse(state.isReady(logic, TurretType.MACHINE_GUN_TURRET), "inactive base");
        }

        @Test
        void savedCooldownIsRestored() {
            state.setTicksSinceShot(8);
            assertTrue(state.beginShot(logic, TurretType.MACHINE_GUN_TURRET).isPresent());
        }
    }

    @Nested
    class Concealing {
        @Test
        void withoutConcealerARetractedTurretComesOutSilently() {
            assertEquals(Concealment.REVEAL, state.updateConcealment(false, true, false));
            assertEquals(Concealment.NONE, state.updateConcealment(false, false, false));
            assertEquals(Concealment.NONE, state.updateConcealment(false, false, true));
        }

        @Test
        void retractsAfterFortyTicksWithoutTarget() {
            for (int i = 0; i < TurretState.CONCEAL_AFTER_TICKS; i++) {
                assertEquals(Concealment.NONE, state.updateConcealment(true, false, false), "tick " + i);
            }
            assertEquals(Concealment.RETRACT, state.updateConcealment(true, false, false));
            // retracted: stays in
            for (int i = 0; i < 100; i++) {
                assertEquals(Concealment.NONE, state.updateConcealment(true, true, false));
            }
        }

        @Test
        void deploysOnceWhenATargetAppears() {
            assertEquals(Concealment.DEPLOY, state.updateConcealment(true, true, true));
            assertEquals(Concealment.NONE, state.updateConcealment(true, false, true));
            assertEquals(Concealment.NONE, state.updateConcealment(true, false, true));
        }

        @Test
        void aTargetRestartsTheCountdown() {
            state.updateConcealment(true, false, true);
            for (int i = 0; i < TurretState.CONCEAL_AFTER_TICKS - 1; i++) state.updateConcealment(true, false, false);
            state.updateConcealment(true, false, true);
            for (int i = 0; i < TurretState.CONCEAL_AFTER_TICKS; i++) {
                assertEquals(Concealment.NONE, state.updateConcealment(true, false, false));
            }
            assertEquals(Concealment.RETRACT, state.updateConcealment(true, false, false));
        }

        @Test
        void deploysAgainAfterRetracting() {
            state.updateConcealment(true, false, true);
            for (int i = 0; i <= TurretState.CONCEAL_AFTER_TICKS; i++) state.updateConcealment(true, false, false);
            assertEquals(Concealment.DEPLOY, state.updateConcealment(true, true, true));
        }
    }
}
