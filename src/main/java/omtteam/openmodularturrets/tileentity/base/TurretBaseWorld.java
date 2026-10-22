package omtteam.openmodularturrets.tileentity.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.world.BaseWorld;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Box;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.registry.ModSounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static omtteam.omlib.util.player.PlayerUtil.addChatMessage;

/**
 * {@link BaseWorld} for a turret base tile entity. Slots: ammo 0–8, addons 9–10, upgrades 11–12 (3.x layout).
 */
public class TurretBaseWorld implements BaseWorld {
    public static final int AMMO_SLOTS = 9;
    public static final int ADDON_START = 9;
    public static final int UPGRADE_START = 11;

    private final TurretBase base;

    public TurretBaseWorld(TurretBase base) {
        this.base = base;
    }

    private IItemHandler inventory() {
        return base.getInventory();
    }

    @Override
    public BlockPosition getPosition() {
        BlockPos pos = base.getPos();
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public List<ItemSlot> getAmmoSlots() {
        List<ItemSlot> slots = new ArrayList<>(AMMO_SLOTS);
        for (int i = 0; i < AMMO_SLOTS; i++) {
            slots.add(new ContainerItemSlot(inventory(), i));
        }
        return slots;
    }

    @Override
    public List<ItemSlot> getAddonSlots() {
        return Arrays.asList(new ContainerItemSlot(inventory(), ADDON_START),
                new ContainerItemSlot(inventory(), ADDON_START + 1));
    }

    @Override
    public List<ItemSlot> getUpgradeSlots() {
        return Arrays.asList(new ContainerItemSlot(inventory(), UPGRADE_START),
                new ContainerItemSlot(inventory(), UPGRADE_START + 1));
    }

    @Override
    public boolean canGenerateSolarPower() {
        World world = base.getWorld();
        return world != null && world.isDaytime() && !world.isRaining() && world.canBlockSeeSky(base.getPos().up(2));
    }

    @Override
    public List<TargetEntity> getLivingEntities(Box box) {
        World world = base.getWorld();
        if (world == null) return Collections.emptyList();
        AxisAlignedBB aabb = new AxisAlignedBB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        List<TargetEntity> result = new ArrayList<>();
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
                entity -> entity != null && !isSpectator(entity))) {
            result.add(new LivingTargetEntity(entity));
        }
        return result;
    }

    @Override
    public Optional<TargetEntity> getEntity(int id) {
        World world = base.getWorld();
        if (world == null) return Optional.empty();
        Entity entity = world.getEntityByID(id);
        return entity instanceof EntityLivingBase && !isSpectator(entity)
                ? Optional.<TargetEntity>of(new LivingTargetEntity((EntityLivingBase) entity)) : Optional.<TargetEntity>empty();
    }

    private static boolean isSpectator(Entity entity) {
        return entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator();
    }

    @Override
    public boolean hasLineOfSight(double fromX, double fromY, double fromZ, TargetEntity target) {
        World world = base.getWorld();
        if (world == null) return false;
        Vec3d end = new Vec3d(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
        return isClear(world, new Vec3d(fromX, fromY, fromZ), end);
    }

    /**
     * Whether nothing solid lies between a turret's centre {@code from} and {@code to} (1.20.1+ port of 3.x
     * {@code canSeeTargetFromPos}): the trace starts where it leaves the turret's own block, so the turret does not
     * see itself, and only blocks with a collision box stop it (grass, flowers and fluids do not). The base counts
     * as an obstacle, so turrets cannot see through it. (3.x skipped a whole block from the centre, which could jump
     * over the base and the blocks behind it.)
     */
    public static boolean isClear(World world, Vec3d from, Vec3d to) {
        Vec3d delta = to.subtract(from);
        double largestAxis = Math.max(Math.max(Math.abs(delta.x), Math.abs(delta.y)), Math.abs(delta.z));
        if (largestAxis <= 0.5D) return true;
        Vec3d start = from.add(delta.scale(0.5D / largestAxis)).add(delta.normalize().scale(1.0E-3D));
        RayTraceResult hit = world.rayTraceBlocks(start, to, false, true, false);
        return hit == null || hit.typeOfHit == RayTraceResult.Type.MISS;
    }

    /** 3.x {@code TurretHeadUtil.dispatchWarnMessage}. */
    @Override
    public void warnPlayer(TargetEntity player) {
        if (!(player instanceof LivingTargetEntity)) return;
        EntityLivingBase entity = ((LivingTargetEntity) player).getEntity();
        if (!(entity instanceof EntityPlayerMP)) return;
        EntityPlayerMP serverPlayer = (EntityPlayerMP) entity;
        if (OMTConfig.TURRETS.turretAlarmSound) {
            serverPlayer.playSound(ModSounds.warningSound, 1.0F, 1.0F);
        }
        if (OMTConfig.TURRETS.turretWarnMessage) {
            addChatMessage(serverPlayer, new TextComponentTranslation(OMTNames.Localizations.Text.STATUS_WARNING)
                    .setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }
}
