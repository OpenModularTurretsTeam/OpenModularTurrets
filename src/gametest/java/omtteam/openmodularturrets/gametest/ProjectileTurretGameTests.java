package omtteam.openmodularturrets.gametest;

//? if neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
 * something else, or expired) and the target was not damaged. Damage from any projectile of the turret counts, since
 * projectiles still in flight when a target is replaced can hit the new one. Explosions (grenade, rocket) and fire (incendiary)
 * count as hits, since they damage the target too.
 */
//? if neoforge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
public class ProjectileTurretGameTests implements OMTGameTests {
    /** The slowest projectile turret (plasma) fires every 60 ticks; room for all attempts plus flight times. */
    static final int TIMEOUT_TICKS = 1500;
    /** Projectiles are spawned at the turret's centre. */
    private static final double MAX_SPAWN_DISTANCE = 0.5D;

    private record Spawn(TurretProjectile projectile, Vec3 position) {
    }

    /** Turret projectiles spawned during the tests, with their spawn position. */
    private static final List<Spawn> SPAWNS = new CopyOnWriteArrayList<>();

    /** Records turret projectiles; called by the test mod's loader entrypoint for every entity added on the server. */
    public static void onEntityLoad(Entity entity) {
        if (entity instanceof TurretProjectile projectile) {
            SPAWNS.add(new Spawn(projectile, projectile.position()));
        }
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

    static void hitsTargetInFront(GameTestHelper helper, TurretType type, EntityType<?> projectileType) {
        TurretScene.build(helper, type, TIMEOUT_TICKS);
        Vec3 turretCentre = Vec3.atCenterOf(helper.absolutePos(TurretScene.TURRET));
        int[] checkedSpawns = {SPAWNS.size()};
        TargetAttempts attempts = new TargetAttempts(helper, type, TurretScene.TARGET_IN_FRONT);
        TurretProjectile[] followed = {null};
        // projectiles fired before the current target appeared can still hit it, so any own projectile counts
        boolean[] fired = {false};
        int[] shots = {0};
        long[] lastShotTick = {-1};
        TurretScene.onEachTick(helper, () -> {
            while (checkedSpawns[0] < SPAWNS.size()) {
                Spawn spawn = SPAWNS.get(checkedSpawns[0]++);
                if (spawn.projectile().level() != helper.getLevel()
                        || spawn.position().distanceTo(turretCentre) > MAX_SPAWN_DISTANCE) {
                    continue; // another test's turret
                }
                helper.assertTrue(spawn.projectile().getType() == projectileType,
                        type + " fired " + spawn.projectile().getType() + " instead of " + projectileType);
                fired[0] = true;
                shots[0]++;
                lastShotTick[0] = helper.getTick();
                if (followed[0] == null) followed[0] = spawn.projectile();
            }
            // a timeout says nothing about why, so fail just before it with the test's state
            if (helper.getTick() >= TIMEOUT_TICKS - 2) {
                LivingEntity target = attempts.target();
                helper.fail(type + " made no progress: attempt " + attempts.attempt() + ", " + shots[0] + " shots (last at tick "
                        + lastShotTick[0] + "), followed projectile " + (followed[0] == null ? "none"
                        : "at " + followed[0].position() + ", age " + followed[0].tickCount + ", removed " + followed[0].isRemoved())
                        + ", target at " + target.position() + " with " + target.getHealth() + " health, alive " + target.isAlive());
            }
            if (attempts.targetDamaged()) {
                helper.assertTrue(fired[0], type + " target was damaged before any projectile was fired");
                attempts.hit();
            } else if (followed[0] != null && followed[0].isRemoved()) {
                followed[0] = null;
                attempts.miss();
            }
        });
    }
}
