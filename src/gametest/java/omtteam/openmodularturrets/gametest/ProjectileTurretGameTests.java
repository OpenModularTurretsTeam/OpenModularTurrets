package omtteam.openmodularturrets.gametest;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.registry.ModEntities;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The projectile turrets must spawn their projectile and hit a target 5 blocks in front. They scatter, so a missed
 * shot is repeated with a fresh target, up to {@value TargetAttempts#MAX_ATTEMPTS} times; the test fails if none of
 * them hits.
 * <p>
 * An attempt follows the first projectile shot at its target: it is a miss when that projectile is gone (hit
 * something else, or expired) and the target was not damaged. Explosions (grenade, rocket) and fire (incendiary)
 * count as hits, since they damage the target too.
 */
public class ProjectileTurretGameTests implements FabricGameTest {
    /** The slowest projectile turret (plasma) fires every 60 ticks; room for all attempts plus flight times. */
    private static final int TIMEOUT_TICKS = 1500;
    /** Projectiles are spawned at the turret's centre. */
    private static final double MAX_SPAWN_DISTANCE = 0.5D;

    private record Spawn(TurretProjectile projectile, Vec3 position) {
    }

    /** Turret projectiles spawned during the tests, with their spawn position. */
    private static final List<Spawn> SPAWNS = new CopyOnWriteArrayList<>();

    static {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof TurretProjectile projectile) {
                SPAWNS.add(new Spawn(projectile, projectile.position()));
            }
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "disposable_turret", timeoutTicks = TIMEOUT_TICKS)
    public void disposableTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.DISPOSABLE_TURRET, ModEntities.DISPOSABLE);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "potato_cannon_turret", timeoutTicks = TIMEOUT_TICKS)
    public void potatoCannonTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.POTATO_CANNON_TURRET, ModEntities.POTATO);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "crossbow_turret", timeoutTicks = TIMEOUT_TICKS)
    public void crossbowTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.CROSSBOW_TURRET, ModEntities.ARROW);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "machine_gun_turret", timeoutTicks = TIMEOUT_TICKS)
    public void machineGunTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.MACHINE_GUN_TURRET, ModEntities.BULLET);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "incendiary_turret", timeoutTicks = TIMEOUT_TICKS)
    public void incendiaryTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.INCENDIARY_TURRET, ModEntities.BLAZING_CLAY);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "grenade_turret", timeoutTicks = TIMEOUT_TICKS)
    public void grenadeTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.GRENADE_TURRET, ModEntities.GRENADE);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "rocket_turret", timeoutTicks = TIMEOUT_TICKS)
    public void rocketTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.ROCKET_TURRET, ModEntities.ROCKET);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "plasma_turret", timeoutTicks = TIMEOUT_TICKS)
    public void plasmaTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.PLASMA_TURRET, ModEntities.PLASMA);
    }

    private static void hitsTargetInFront(GameTestHelper helper, TurretType type, EntityType<?> projectileType) {
        TurretScene.build(helper, type);
        Vec3 turretCentre = Vec3.atCenterOf(helper.absolutePos(TurretScene.TURRET));
        int[] checkedSpawns = {SPAWNS.size()};
        TargetAttempts attempts = new TargetAttempts(helper, type, TurretScene.TARGET_IN_FRONT);
        TurretProjectile[] followed = {null};
        helper.onEachTick(() -> {
            while (checkedSpawns[0] < SPAWNS.size()) {
                Spawn spawn = SPAWNS.get(checkedSpawns[0]++);
                if (spawn.projectile().level() != helper.getLevel()
                        || spawn.position().distanceTo(turretCentre) > MAX_SPAWN_DISTANCE) {
                    continue; // another test's turret
                }
                helper.assertTrue(spawn.projectile().getType() == projectileType,
                        type + " fired " + spawn.projectile().getType() + " instead of " + projectileType);
                if (followed[0] == null) followed[0] = spawn.projectile();
            }
            if (attempts.targetDamaged()) {
                helper.assertTrue(followed[0] != null, type + " target was damaged before any projectile was fired");
                attempts.hit();
            } else if (followed[0] != null && followed[0].isRemoved()) {
                followed[0] = null;
                attempts.miss();
            }
        });
    }
}
