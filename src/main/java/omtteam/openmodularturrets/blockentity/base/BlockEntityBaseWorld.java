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
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.world.BaseWorld;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Box;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * {@link BaseWorld} for a turret base block entity.
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
        // TODO: append the slots of attached inventory expanders once they are ported
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

    @Override
    public List<TargetEntity> getLivingEntities(Box box) {
        Level current = level.get();
        if (current == null) return List.of();
        AABB aabb = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        List<TargetEntity> result = new ArrayList<>();
        for (LivingEntity entity : current.getEntitiesOfClass(LivingEntity.class, aabb, e -> !e.isSpectator())) {
            result.add(new LivingTargetEntity(entity));
        }
        return result;
    }

    @Override
    public Optional<TargetEntity> getEntity(int id) {
        Level current = level.get();
        if (current == null) return Optional.empty();
        Entity entity = current.getEntity(id);
        return entity instanceof LivingEntity living && !living.isSpectator()
                ? Optional.of(new LivingTargetEntity(living)) : Optional.empty();
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
        Vec3 end = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
        Vec3 start = new Vec3(fromX, fromY, fromZ);
        Vec3 delta = end.subtract(start);
        double largestAxis = Math.max(Math.max(Math.abs(delta.x), Math.abs(delta.y)), Math.abs(delta.z));
        if (largestAxis <= 0.5D) return true;
        start = start.add(delta.scale(0.5D / largestAxis)).add(delta.normalize().scale(1.0E-3D));
        BlockHitResult hit = current.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                living.getEntity()));
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
