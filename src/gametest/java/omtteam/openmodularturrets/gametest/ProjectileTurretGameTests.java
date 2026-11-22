package omtteam.openmodularturrets.gametest;

//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.omlib.core.world.Face;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModEntities;

import java.util.ArrayList;
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
//? if forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
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

    // ---- Range falloff and turn rate (issue 539) ----

    /** Share of the damage at the end of the range in the falloff test. */
    private static final double FALLOFF_DAMAGE = 0.5D;
    /** Distances from the turret's centre where a bullet can enter a target standing 5 blocks in front of it. */
    private static final double FALLOFF_NEAREST = 3.5D;
    private static final double FALLOFF_FARTHEST = 6.0D;
    /** Turn rate of the turn rate test, in degrees per tick (far below the machine gun's default). */
    private static final float SLOW_TURN_RATE = 2.0F;
    /** Samples the turn rate tests wait after the first shot for the aim to come to rest on the target. */
    private static final int SETTLE_SAMPLES = 4;
    /** Smallest aim error at the start that makes the turn rate test meaningful, in degrees. */
    private static final double MIN_START_ERROR = 20.0D;
    private static final double ANGLE_EPSILON = 0.01D;

    /**
     * Projectile damage falls off with distance and is no longer rounded to whole points: with a base damage of 1, an
     * optimal range of 0 and half damage at the end of the range, a bullet deals {@code 1 - 0.5 * d / range} at the
     * distance {@code d} where it enters the target (no armor), so less than one point but more than half.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "machine_gun_falloff", timeoutTicks = TIMEOUT_TICKS)
    @SuppressWarnings("DataFlowIssue")
    public void projectileDamageFallsOffWithDistance(GameTestHelper helper) {
        TurretType type = TurretType.MACHINE_GUN_TURRET;
        TurretBaseBlockEntity base = TurretScene.build(helper, type, TIMEOUT_TICKS);
        TurretScene.configure(helper, type, setting -> {
            setting.baseDamage = 1;
            setting.optimalRange = 0;
            setting.falloffDamage = FALLOFF_DAMAGE;
        });
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        target.getAttribute(Attributes.ARMOR).setBaseValue(0);
        float startHealth = target.getHealth();
        TurretScene.onEachTick(helper, () -> {
            float dealt = startHealth - target.getHealth();
            if (dealt <= 0) return;
            int range = base.getLogic().getRange(type);
            float least = (float) ProjectileMath.falloff(FALLOFF_FARTHEST, range, 0, FALLOFF_DAMAGE);
            float most = (float) ProjectileMath.falloff(FALLOFF_NEAREST, range, 0, FALLOFF_DAMAGE);
            helper.assertTrue(dealt >= least - 0.01F && dealt <= most + 0.01F,
                    type + " dealt " + dealt + " damage at about 5 blocks with range " + range + ", expected "
                            + least + " to " + most);
            TurretScene.succeed(helper);
        });
    }

    /**
     * With a turn rate, a turret turns at most that many degrees per tick on each axis, and fires only once its aim
     * is within {@code turrets.aimTolerance} of the target; until then it uses no ammo and no energy.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "machine_gun_turn_rate", timeoutTicks = TIMEOUT_TICKS)
    public void turnRateDelaysTheFirstShot(GameTestHelper helper) {
        turnsAndFires(helper, SLOW_TURN_RATE);
    }

    /** With a turn rate of 0 the turret aims at once and fires as soon as its cooldown allows. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "machine_gun_turn_instant", timeoutTicks = TIMEOUT_TICKS)
    public void turnRateZeroFiresAtOnce(GameTestHelper helper) {
        turnsAndFires(helper, 0.0F);
    }

    /**
     * Follows the machine gun's aim (yaw and pitch from its saved data) every tick from the tick its base picked the
     * target, finds the first shot by the ammo it used, and checks the turn once the aim came to rest on the target.
     */
    private static void turnsAndFires(GameTestHelper helper, float turnRate) {
        TurretType type = TurretType.MACHINE_GUN_TURRET;
        TurretBaseBlockEntity base = TurretScene.build(helper, type, TIMEOUT_TICKS);
        TurretScene.configure(helper, type, setting -> setting.turnRate = turnRate);
        TurretBlockEntity turret = (TurretBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(TurretScene.TURRET));
        long startEnergy = base.getLogic().getEnergy().getAmount();
        int startAmmo = base.getInventory().getItem(TurretBaseInventory.AMMO_START).getCount();
        double tolerance = OMTConfig.get().turrets.aimTolerance;
        int cooldown = OMTConfig.get().turrets.get(type).baseFireRate;
        TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        List<TurretMath.Angles> aims = new ArrayList<>();
        int[] shot = {-1};
        TurretScene.onEachTick(helper, () -> {
            boolean fired = base.getInventory().getItem(TurretBaseInventory.AMMO_START).getCount() < startAmmo;
            if (aims.isEmpty() && base.getLogic().getTarget(Face.UP).isEmpty()) {
                helper.assertTrue(!fired, type + " fired before its base picked a target");
                return;
            }
            aims.add(aim(helper, turret));
            if (shot[0] < 0) {
                if (!fired) {
                    long used = startEnergy - base.getLogic().getEnergy().getAmount();
                    helper.assertTrue(used == 0, type + " used " + used + " energy without firing");
                    return;
                }
                shot[0] = aims.size() - 1;
            }
            int last = aims.size() - 1;
            if (last < shot[0] + SETTLE_SAMPLES || !aims.get(last).equals(aims.get(last - 1))) return;

            TurretMath.Angles rest = aims.get(last);
            double startError = TurretMath.aimError(aims.get(0), rest);
            double shotError = TurretMath.aimError(aims.get(shot[0]), rest);
            helper.assertTrue(shotError <= tolerance + ANGLE_EPSILON, type + " fired " + shotError
                    + " degrees off the target (tolerance " + tolerance + ")");
            if (turnRate <= 0) {
                helper.assertTrue(shotError <= ANGLE_EPSILON, type + " with turn rate 0 fired " + shotError
                        + " degrees off the target");
                helper.assertTrue(shot[0] <= cooldown + 1, type + " with turn rate 0 fired " + shot[0]
                        + " ticks after picking the target (cooldown " + cooldown + ")");
            } else {
                helper.assertTrue(startError >= MIN_START_ERROR, "The turret started only " + startError
                        + " degrees off the target, too little to see the turn");
                for (int i = 1; i <= last; i++) {
                    TurretMath.Angles from = aims.get(i - 1);
                    TurretMath.Angles to = aims.get(i);
                    float yawStep = Math.abs(TurretMath.angleDifference(from.yaw(), to.yaw()));
                    float pitchStep = Math.abs(to.pitch() - from.pitch());
                    helper.assertTrue(yawStep <= turnRate + ANGLE_EPSILON && pitchStep <= turnRate + ANGLE_EPSILON,
                            type + " turned " + yawStep + " / " + pitchStep + " degrees in one tick (turn rate "
                                    + turnRate + ")");
                }
                for (int i = 0; i < shot[0]; i++) {
                    double error = TurretMath.aimError(aims.get(i), rest);
                    helper.assertTrue(error > tolerance, type + " did not fire on tick " + i + " although its aim was "
                            + error + " degrees off (tolerance " + tolerance + ")");
                }
                double fewestTicks = (startError - tolerance) / (turnRate * Math.sqrt(2)) - 1;
                helper.assertTrue(shot[0] >= fewestTicks, type + " fired " + shot[0] + " ticks after picking a target "
                        + startError + " degrees off, at least " + fewestTicks + " expected");
            }
            TurretScene.succeed(helper);
        });
    }

    /** The turret's current aim, as it saves it. */
    private static TurretMath.Angles aim(GameTestHelper helper, TurretBlockEntity turret) {
        CompoundTag tag = turret.saveWithoutMetadata();
        return new TurretMath.Angles(NbtCompat.getFloat(tag, "Yaw", 0), NbtCompat.getFloat(tag, "Pitch", 0));
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
