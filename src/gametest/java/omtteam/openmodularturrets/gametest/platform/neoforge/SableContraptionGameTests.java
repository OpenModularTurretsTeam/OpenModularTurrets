package omtteam.openmodularturrets.gametest.platform.neoforge;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretMount;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.gametest.OMTGameTests;
import omtteam.openmodularturrets.gametest.TurretScene;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.List;
import java.util.Objects;

/**
 * Turrets on a Sable contraption (NeoForge only: only the NeoForge GameTest server runs with Sable). The scene's base
 * and turret are assembled into a sub-level, which stores them in a plot far away while they are still shown where
 * they were, and the sub-level is turned by 90° around its vertical axis, so plot and world directions differ. It
 * rests on the scene's floor, which stays in the world. Each test checks that the turret acts on the world where it
 * is shown.
 */
@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
@SuppressWarnings("unused")
public class SableContraptionGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 400;
    /** How far the synced aim (turned into the world) may point away from the target's eyes. */
    private static final double MAX_AIM_ERROR_DEGREES = 5.0D;

    /** The assembled sub-level and the positions of base and turret in its plot. */
    private record Contraption(ServerSubLevel subLevel, BlockPos base, BlockPos turret) {
    }

    /** A projectile turret on the turned contraption aims at a target in the world and hits it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "sable_projectile_turret", timeoutTicks = TIMEOUT_TICKS)
    public void projectileTurretOnContraptionHitsTarget(GameTestHelper helper) {
        aimsAtAndHitsTarget(helper, TurretType.MACHINE_GUN_TURRET);
    }

    /** A ray turret on the turned contraption aims at a target in the world and hits it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "sable_ray_turret", timeoutTicks = TIMEOUT_TICKS)
    public void rayTurretOnContraptionHitsTarget(GameTestHelper helper) {
        aimsAtAndHitsTarget(helper, TurretType.LASER_TURRET);
    }

    /** The teleporter pulls its target to where the block above the turret is shown, not into the plot. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "sable_teleporter_turret", timeoutTicks = TIMEOUT_TICKS)
    public void teleporterOnContraptionPullsTargetToTheShownTurret(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.TELEPORTER_TURRET, TIMEOUT_TICKS);
        Contraption contraption = assemble(helper);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        succeedWhen(helper, contraption, () -> {
            // Sable's own pose, not OMT's conversion
            Vector3d destination = contraption.subLevel().logicalPose().transformPosition(
                    new Vector3d(contraption.turret().getX() + 0.5D, contraption.turret().getY() + 1.0D,
                            contraption.turret().getZ() + 0.5D));
            double distance = Math.hypot(target.getX() - destination.x, target.getZ() - destination.z);
            helper.assertTrue(distance < 0.5D && Math.abs(target.getY() - destination.y) < 1.0D,
                    "Target at " + target.position() + " was not pulled to " + destination);
        });
    }

    /** Assembling the base into a contraption moves its items along and drops none (it is {@code Clearable}). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "sable_assembly", timeoutTicks = TIMEOUT_TICKS)
    public void assemblyKeepsTheBaseItemsWithoutDrops(GameTestHelper helper) {
        TurretBaseBlockEntity worldBase = TurretScene.build(helper, TurretType.MACHINE_GUN_TURRET, TIMEOUT_TICKS);
        int ammo = worldBase.getInventory().getItem(TurretBaseInventory.AMMO_START).getCount();
        Contraption contraption = assemble(helper);
        AABB scene = new AABB(helper.absolutePos(BlockPos.ZERO)).minmax(new AABB(helper.absolutePos(new BlockPos(7, 7, 7))));
        List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, scene.inflate(1.0D));
        helper.assertTrue(drops.isEmpty(), "Assembling the base dropped " + drops.size() + " stacks");
        helper.assertTrue(helper.getLevel().getBlockEntity(contraption.base()) instanceof TurretBaseBlockEntity base
                        && base.getInventory().getItem(TurretBaseInventory.AMMO_START).getCount() == ammo,
                "The base in the contraption does not hold its " + ammo + " ammo");
        remove(helper.getLevel(), contraption.subLevel());
        TurretScene.succeed(helper);
    }

    private static void aimsAtAndHitsTarget(GameTestHelper helper, TurretType type) {
        TurretScene.build(helper, type, TIMEOUT_TICKS);
        Contraption contraption = assemble(helper);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        succeedWhen(helper, contraption, () -> {
            assertAimsAt(helper, contraption, target);
            helper.assertTrue(target.getHealth() < target.getMaxHealth(), type + " on the contraption did not hit the target");
        });
    }

    /** The turret's synced aim (relative to its face, in the plot), turned into the world, points at the target's eyes. */
    private static void assertAimsAt(GameTestHelper helper, Contraption contraption, LivingEntity target) {
        ServerLevel level = helper.getLevel();
        helper.assertTrue(level.getBlockEntity(contraption.turret()) instanceof TurretBlockEntity,
                "No turret in the contraption");
        TurretBlockEntity turret = (TurretBlockEntity) level.getBlockEntity(contraption.turret());
        TurretMount mount = turret.getMount();
        helper.assertTrue(mount.isOnContraption(), "The turret does not know it is on a contraption");
        CompoundTag tag = turret.saveWithoutMetadata(level.registryAccess());
        Vector3 local = TurretMath.direction(mount.face(), new TurretMath.Angles(tag.getFloat("Yaw"), tag.getFloat("Pitch")));
        Vector3 aim = mount.pose().directionToWorld(local);
        Vec3 toEyes = target.getEyePosition().subtract(mount.centre()).normalize();
        double cos = (aim.x() * toEyes.x + aim.y() * toEyes.y + aim.z() * toEyes.z) / aim.length();
        double error = Math.toDegrees(Math.acos(Math.max(-1.0D, Math.min(1.0D, cos))));
        helper.assertTrue(error <= MAX_AIM_ERROR_DEGREES, "The turret aims " + Math.round(error) + "° away from the target");
    }

    /**
     * Assembles the scene's base and turret into a sub-level (base as anchor) and turns it by 90° around its
     * vertical axis, in place.
     */
    private static Contraption assemble(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerSubLevelContainer container = Objects.requireNonNull(SubLevelContainer.getContainer(level),
                "Sable has no sub-level container in the test level");
        BlockPos base = helper.absolutePos(TurretScene.BASE);
        BlockPos turret = helper.absolutePos(TurretScene.TURRET);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, base, List.of(base, turret),
                new BoundingBox3i(base, turret));
        container.physicsSystem().getPipeline().teleport(subLevel, new Vector3d(subLevel.logicalPose().position()),
                new Quaterniond().rotateY(Math.PI / 2.0D));
        // a failing test is cleared just before its timeout, like TurretScene
        helper.runAfterDelay(TIMEOUT_TICKS - 1, () -> remove(level, subLevel));
        BlockPos plotBase = subLevel.getPlot().getCenterBlock();
        return new Contraption(subLevel, plotBase, plotBase.above());
    }

    /**
     * Passes once {@code check} holds, removing the contraption and the scene. A timeout says nothing about why (and
     * comes after the scene is cleared), so the test fails just before it with the last failed check.
     */
    private static void succeedWhen(GameTestHelper helper, Contraption contraption, Runnable check) {
        String[] lastFailure = {"nothing was checked"};
        helper.onEachTick(() -> {
            try {
                check.run();
            } catch (GameTestAssertException e) {
                lastFailure[0] = e.getMessage();
                if (helper.getTick() >= TIMEOUT_TICKS - 3) {
                    remove(helper.getLevel(), contraption.subLevel());
                    TurretScene.clear(helper);
                    helper.fail(lastFailure[0]);
                }
                return;
            }
            remove(helper.getLevel(), contraption.subLevel());
            TurretScene.succeed(helper);
        });
    }

    private static void remove(ServerLevel level, ServerSubLevel subLevel) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container != null && !subLevel.isRemoved()) {
            container.removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
        }
    }
}
