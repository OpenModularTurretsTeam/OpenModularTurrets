package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.compat.SubLevelCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.world.BaseWorld;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Box;
import omtteam.openmodularturrets.core.world.Pose;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * {@link BaseWorld} for a turret base block entity. On a Sable contraption the base's coordinates are those of the
 * plot, so entity positions are converted into them with the base's current {@link Pose}, and line of sight starts at
 * the point the plot position is shown at.
 */
public class BlockEntityBaseWorld implements BaseWorld {
    private final Supplier<Level> level;
    private final BlockPos pos;
    private final TurretBaseInventory inventory;

    public BlockEntityBaseWorld(Supplier<Level> level, BlockPos pos, TurretBaseInventory inventory) {
        this.level = level;
        this.pos = pos;
        this.inventory = inventory;
    }

    @Override
    public BlockPosition getPosition() {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public List<ItemSlot> getAmmoSlots() {
        List<ItemSlot> slots = new ArrayList<>();
        for (int i = 0; i < TurretBaseInventory.AMMO_COUNT; i++) {
            slots.add(new ContainerItemSlot(inventory, TurretBaseInventory.AMMO_START + i));
        }
        return slots;
    }

    @Override
    public List<ItemSlot> getAddonSlots() {
        return List.of(new ContainerItemSlot(inventory, TurretBaseInventory.ADDON_START),
                new ContainerItemSlot(inventory, TurretBaseInventory.ADDON_START + 1));
    }

    @Override
    public List<ItemSlot> getUpgradeSlots() {
        return List.of(new ContainerItemSlot(inventory, TurretBaseInventory.UPGRADE_START),
                new ContainerItemSlot(inventory, TurretBaseInventory.UPGRADE_START + 1));
    }

    @Override
    public boolean canGenerateSolarPower() {
        Level current = level.get();
        return current != null && current.isDay() && !current.isRaining() && current.canSeeSky(pos.above(2));
    }

    /**
     * Entities whose bounding box touches the box (as vanilla searches). On a contraption, Sable widens a search
     * inside the plot to the world area around the turned box, so there the entities found are filtered by their
     * position in the base's coordinates.
     */
    @Override
    public List<TargetEntity> getLivingEntities(Box box) {
        Level current = level.get();
        if (current == null) return List.of();
        Pose pose = SubLevelCompat.poseAt(current, pos);
        AABB aabb = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        List<TargetEntity> result = new ArrayList<>();
        for (LivingEntity entity : current.getEntitiesOfClass(LivingEntity.class, aabb, e -> !e.isSpectator())) {
            LivingTargetEntity target = new LivingTargetEntity(entity, pose);
            if (pose.isIdentity() || box.contains(target.getX(), target.getY(), target.getZ())) {
                result.add(target);
            }
        }
        return result;
    }

    @Override
    public Optional<TargetEntity> getEntity(int id) {
        Level current = level.get();
        if (current == null) return Optional.empty();
        Entity entity = current.getEntity(id);
        return entity instanceof LivingEntity living && !living.isSpectator()
                ? Optional.of(new LivingTargetEntity(living, SubLevelCompat.poseAt(current, pos))) : Optional.empty();
    }

    /**
     * 1.12.2 {@code canSeeTargetFromPos}, from a turret's block centre: the trace starts where it leaves the turret's
     * own block (so the turret does not see itself) and only blocks with a collision shape stop it. The base counts
     * as an obstacle, so turrets cannot see through it. (1.12.2 skipped a whole block from the centre, which could
     * jump over the base and the blocks behind it.) The target is passed to {@link ClipContext} as the collision
     * context entity (it must not be null), like vanilla's {@code LivingEntity.hasLineOfSight}.
     */
    @Override
    public boolean hasLineOfSight(double fromX, double fromY, double fromZ, TargetEntity target) {
        Level current = level.get();
        if (current == null || !(target instanceof LivingTargetEntity living)) return false;
        return isClear(current, SubLevelCompat.poseAt(current, pos), new Vec3(fromX, fromY, fromZ),
                living.getEntity().getEyePosition(), living.getEntity());
    }

    /**
     * Whether nothing solid lies between a turret's centre {@code plotFrom} and {@code worldTo}, see
     * {@link #hasLineOfSight}. The point where the trace leaves the turret's block is found in the plot, where the
     * block is axis-aligned, so a turned turret on a contraption does not see its own block.
     *
     * @param pose     the pose of the turret's contraption ({@link Pose#IDENTITY} outside contraptions)
     * @param plotFrom the turret's centre in its own (plot) coordinates
     * @param worldTo  the end point in the world
     * @param context  the entity passed to {@link ClipContext} as collision context (must not be null)
     */
    public static boolean isClear(Level level, Pose pose, Vec3 plotFrom, Vec3 worldTo, Entity context) {
        Vector3 from = new Vector3(plotFrom.x, plotFrom.y, plotFrom.z);
        Vector3 delta = pose.toLocal(new Vector3(worldTo.x, worldTo.y, worldTo.z)).subtract(from);
        double largestAxis = Math.max(Math.max(Math.abs(delta.x()), Math.abs(delta.y())), Math.abs(delta.z()));
        if (largestAxis <= 0.5D) return true;
        Vector3 start = pose.toWorld(from.add(delta.scale(0.5D / largestAxis)).add(delta.scale(1.0E-3D / delta.length())));
        BlockHitResult hit = level.clip(new ClipContext(new Vec3(start.x(), start.y(), start.z()), worldTo,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, context));
        return hit.getType() == HitResult.Type.MISS;
    }

    @Override
    public void warnPlayer(TargetEntity player) {
        if (!(player instanceof LivingTargetEntity living) || !(living.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // 1.12.2 dispatchWarnMessage: only the warned player hears the alarm
        if (OMTConfig.get().turrets.turretAlarmSound) {
            serverPlayer.playNotifySound(ModSounds.WARNING, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (OMTConfig.get().turrets.turretWarnMessage) {
            serverPlayer.sendSystemMessage(Component.translatable("message.openmodularturrets.turret_warning")
                    .withStyle(ChatFormatting.RED));
        }
    }
}
