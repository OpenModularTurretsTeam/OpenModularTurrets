package omtteam.openmodularturrets.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.RayShotCallback;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The ray turrets must trace their rays towards a target 5 blocks in front and hit it. They scatter, so a missed
 * shot is repeated with a fresh target, up to {@value TargetAttempts#MAX_ATTEMPTS} times; the test fails if none of them hits.
 */
public class RayTurretGameTests implements FabricGameTest {
    /** The slowest ray turret (railgun) fires every 100 ticks; room for all attempts plus target searches. */
    private static final int TIMEOUT_TICKS = 1500;
    /** A ray starts 0.8 blocks in front of the turret's centre. */
    private static final double MAX_START_DISTANCE = 1.0D;
    /** The aim point is the target's eyes plus the turret's scatter (well below a block at 5 blocks). */
    private static final double MAX_AIM_DISTANCE = 1.0D;

    private static final int SCATTERSHOT_UPGRADES = 8;
    private static final float TARGET_HEALTH = 1024F;

    private record Shot(Vec3 start, Vec3 end, @Nullable Entity hit) {
    }

    /** Rays shot during the tests, by the turret's position. */
    private static final Map<BlockPos, List<Shot>> SHOTS = new ConcurrentHashMap<>();

    static {
        RayShotCallback.EVENT.register((turret, start, end, hit) -> SHOTS
                .computeIfAbsent(turret.getBlockPos(), pos -> new CopyOnWriteArrayList<>())
                .add(new Shot(start, end, hit)));
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "laser_turret", timeoutTicks = TIMEOUT_TICKS)
    public void laserTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.LASER_TURRET);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "railgun_turret", timeoutTicks = TIMEOUT_TICKS)
    public void railgunTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.RAILGUN_TURRET);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "arc_turret", timeoutTicks = TIMEOUT_TICKS)
    public void arcTurretHitsTarget(GameTestHelper helper) {
        hitsTargetInFront(helper, TurretType.ARC_TURRET);
    }

    /**
     * Each scattershot ray of the railgun deals its full damage ({@code baseDamage * (armor / 20 + 0.6)},
     * armor-piercing, the hurt cooldown is removed after every hit), so one volley deals exactly that per ray that
     * hit and nothing more. 1.12.2 dealt suspiciously high damage with many scattershot upgrades, because the
     * railgun barely scatters and all rays hit. The target has the highest possible health (vanilla caps max health
     * at 1024) so it survives the volley.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "railgun_scattershot", timeoutTicks = TIMEOUT_TICKS)
    public void railgunScattershotDamagePerRay(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TurretType.RAILGUN_TURRET);
        // tier 5: two upgrade slots with up to four upgrades each
        base.getInventory().setItem(TurretBaseInventory.UPGRADE_START, new ItemStack(ModItems.UPGRADE_SCATTER_SHOT, 4));
        base.getInventory().setItem(TurretBaseInventory.UPGRADE_START + 1, new ItemStack(ModItems.UPGRADE_SCATTER_SHOT, 4));
        helper.assertTrue(base.getLogic().getScattershotCount() == SCATTERSHOT_UPGRADES,
                "Base has " + base.getLogic().getScattershotCount() + " scattershot upgrades");

        BlockPos turretPos = helper.absolutePos(TurretScene.TURRET);
        List<Shot> shots = SHOTS.computeIfAbsent(turretPos, pos -> new CopyOnWriteArrayList<>());
        shots.clear();
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(TARGET_HEALTH);
        target.setHealth(target.getMaxHealth());
        float startHealth = target.getHealth();
        helper.assertTrue(startHealth == TARGET_HEALTH, "Target health is " + startHealth);

        // all rays of a volley are shot in the same tick, so the first tick with shots has the whole volley
        helper.onEachTick(() -> {
            if (shots.isEmpty()) return;
            helper.assertTrue(shots.size() == SCATTERSHOT_UPGRADES + 1,
                    "Railgun shot " + shots.size() + " rays instead of " + (SCATTERSHOT_UPGRADES + 1));
            long hits = shots.stream().filter(shot -> shot.hit() == target).count();
            helper.assertTrue(hits > 0, "No railgun ray hit the target");
            float perRay = (float) (OMTConfig.get().turrets.get(TurretType.RAILGUN_TURRET).baseDamage
                    * (target.getArmorValue() / 20F + 0.6F));
            float expected = perRay * hits;
            float dealt = startHealth - target.getHealth();
            helper.assertTrue(Math.abs(dealt - expected) < 0.01F, "Railgun dealt " + dealt + " damage with " + hits
                    + " hitting rays, expected " + expected + " (" + perRay + " per ray)");
            TurretScene.succeed(helper);
        });
    }

    private static void hitsTargetInFront(GameTestHelper helper, TurretType type) {
        TurretScene.build(helper, type);
        BlockPos turretPos = helper.absolutePos(TurretScene.TURRET);
        Vec3 turretCentre = Vec3.atCenterOf(turretPos);
        List<Shot> shots = SHOTS.computeIfAbsent(turretPos, pos -> new CopyOnWriteArrayList<>());
        shots.clear();
        TargetAttempts attempts = new TargetAttempts(helper, type, TurretScene.TARGET_IN_FRONT);
        int[] checkedShots = {0};
        // every ray is checked as it is shot; one that does not hit the current target is a miss
        helper.onEachTick(() -> {
            while (checkedShots[0] < shots.size()) {
                Shot shot = shots.get(checkedShots[0]++);
                LivingEntity target = attempts.target();
                Vec3 eyes = target.getEyePosition();
                helper.assertTrue(shot.start().distanceTo(turretCentre) <= MAX_START_DISTANCE,
                        type + " ray starts at " + shot.start() + ", not at the turret " + turretCentre);
                helper.assertTrue(shot.end().distanceTo(eyes) <= MAX_AIM_DISTANCE,
                        type + " ray aims at " + shot.end() + ", not at the target's eyes " + eyes);
                if (shot.hit() == target) {
                    helper.assertTrue(attempts.targetDamaged(), type + " ray hit the target without damaging it");
                    attempts.hit();
                    return;
                }
                attempts.miss();
            }
        });
    }
}
