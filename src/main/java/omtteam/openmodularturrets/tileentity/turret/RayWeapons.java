package omtteam.openmodularturrets.tileentity.turret;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.omlib.network.MessageRenderLightning;
import omtteam.omlib.network.MessageRenderRay;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.RayMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.ArmorBypassDamageSource;
import omtteam.openmodularturrets.entity.projectile.NormalDamageSource;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Shoots {@link RayWeapon}s (1.12.2 {@code RayTracingTurret} with {@code LaserTurretTileEntity},
 * {@code RailGunTurretTileEntity} and {@code ArcTurretTileEntity}). Visuals come from the weapon, the damage from
 * {@link RayMath}.
 */
public final class RayWeapons {
    /** 1.12.2 {@code blockRange} when nothing was hit. */
    private static final double NO_BLOCK_HIT = 500;
    /** Players this close to a ray see it (1.12.2). */
    private static final double RENDER_RANGE = 120;

    private RayWeapons() {
    }

    /**
     * 1.12.2 {@code doTargetedShot} + {@code shootRay}: one ray per scattershot level towards {@code aim}, scattered
     * by the turret's accuracy (more for small or far targets). The first damageable entity on the ray before the
     * first solid block is hit. Energy and ammo are already consumed.
     *
     * @param targetHeight height of the target, for the scatter of small targets
     * @param renderOrigin where a hit is drawn from if the weapon {@linkplain RayWeapon#drawFromTip() draws from the
     *                     tip}
     */
    static void shoot(TurretTileEntity turret, TurretBase base, RayWeapon weapon, Vec3d aim, float targetHeight,
                      Vec3d origin, Vec3d renderOrigin) {
        if (!(turret.getWorld() instanceof WorldServer)) return;
        WorldServer world = (WorldServer) turret.getWorld();
        TurretType type = turret.getCoreType();
        TurretBaseLogic logic = base.getLogic();
        double accuracy = logic.getAccuracyDeviation(type);
        for (int i = 0; i <= logic.getScattershotCount(type); i++) {
            double scale = ProjectileMath.raySpread(accuracy, targetHeight, aim.distanceTo(origin), logic.getRange(type));
            Vec3d end = aim.add(world.rand.nextGaussian() * scale, world.rand.nextGaussian() * scale,
                    world.rand.nextGaussian() * scale);
            Vec3d start = origin.add(end.subtract(origin).normalize().scale(0.8D));
            turret.playSound(weapon.launchSound());

            RayTraceResult blockHit = world.rayTraceBlocks(start, end, false, true, false);
            boolean hitBlock = blockHit != null && blockHit.typeOfHit == RayTraceResult.Type.BLOCK;
            double blockRange = hitBlock ? blockHit.hitVec.distanceTo(start) : NO_BLOCK_HIT;

            boolean hit = false;
            for (Entity entity : entitiesOnRay(world, start, end)) {
                RayTraceResult intercept = box(entity).calculateIntercept(start, end);
                double distance = intercept == null ? 0 : intercept.hitVec.distanceTo(start);
                if (distance <= blockRange && hitEntity(world, turret, base, weapon, entity)) {
                    ray(world, weapon, weapon.drawFromTip() ? renderOrigin : start, end);
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                if (hitBlock) {
                    ray(world, weapon, start, blockHit.hitVec);
                    if (weapon.blockHit() != null) weapon.blockHit().onBlockHit(world, blockHit.getBlockPos());
                } else {
                    // 1.12.2 drew the ray three times as long when it hit nothing
                    ray(world, weapon, start, end.add(end.subtract(start).scale(2D)));
                }
            }
        }
    }

    private static AxisAlignedBB box(Entity entity) {
        return entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
    }

    /**
     * Entities whose bounding box crosses the segment, nearest first (1.12.2 {@code WorldUtil.traceEntities}, which
     * could report entities next to the ray as hit).
     */
    private static List<Entity> entitiesOnRay(WorldServer world, Vec3d start, Vec3d end) {
        AxisAlignedBB search = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z).grow(1.0D);
        List<Entity> result = new ArrayList<>();
        for (Entity entity : world.getEntitiesInAABBexcluding(null, search, EntitySelectors.NOT_SPECTATING)) {
            if (entity != null && entity.canBeCollidedWith()
                    && (box(entity).contains(start) || box(entity).calculateIntercept(start, end) != null)) {
                result.add(entity);
            }
        }
        result.sort(Comparator.comparingDouble(entity -> {
            RayTraceResult intercept = box(entity).calculateIntercept(start, end);
            return intercept == null ? 0 : intercept.hitVec.squareDistanceTo(start);
        }));
        return result;
    }

    /** 1.12.2 {@code onHitEntity}: damage, arc chain and hit sound if the entity may be damaged. */
    private static boolean hitEntity(WorldServer world, TurretTileEntity turret, TurretBase base, RayWeapon weapon,
                                     Entity entity) {
        if (!TurretDamage.canDamageEntity(entity, base)) return false;
        damage(world, turret, base, weapon, entity, RayMath.damageModifier(weapon.profile(), armor(entity)));
        if (weapon.profile().arcChain()) {
            shootArcChain(world, base, turret, weapon, entity, RayMath.ARC_HOPS, new ArrayList<>());
        }
        if (weapon.hitSound() != null) {
            world.playSound(null, entity.getPosition(), weapon.hitSound(), SoundCategory.AMBIENT,
                    (float) OMTConfig.TURRETS.turretSoundVolume, world.rand.nextFloat() + 0.5F);
        }
        return true;
    }

    private static int armor(Entity entity) {
        return entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getTotalArmorValue() : 0;
    }

    /**
     * 1.12.2 {@code ArcTurretTileEntity.applyHitEffects}: each hit uses one hop; the arc jumps to the first damageable
     * living entity within {@link RayMath#arcJumpRange} that was not hit yet, which takes
     * {@link RayMath#ARC_JUMP_FACTOR} of the damage.
     */
    private static void shootArcChain(WorldServer world, TurretBase base, TurretTileEntity turret, RayWeapon weapon,
                                      Entity from, int hops, List<Integer> hitIds) {
        if (hops <= 0) return;
        hitIds.add(from.getEntityId());
        double range = RayMath.arcJumpRange(hops);
        int remaining = hops - 1;
        AxisAlignedBB area = new AxisAlignedBB(from.posX, from.posY, from.posZ, from.posX, from.posY, from.posZ)
                .grow(range);
        for (EntityLivingBase next : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
            if (!hitIds.contains(next.getEntityId()) && TurretDamage.canDamageEntity(next, base)) {
                // 1.12.2 order: the chain continues from the next target before it is damaged
                shootArcChain(world, base, turret, weapon, next, remaining, hitIds);
                damage(world, turret, base, weapon, next, RayMath.ARC_JUMP_FACTOR);
                double eye = from.getEyeHeight();
                ray(world, weapon, from.getPositionVector().add(0, eye, 0),
                        next.getPositionVector().add(0, eye, 0));
                break;
            }
        }
    }

    /**
     * Deals {@link RayMath#damage}, split into a normal and an armor-piercing part ({@link RayMath#split}); as in
     * 1.12.2 the hurt cooldown is reset so every ray of a scattershot hits.
     */
    private static void damage(WorldServer world, TurretTileEntity turret, TurretBase base, RayWeapon weapon,
                               Entity entity, double modifier) {
        TurretBaseLogic logic = base.getLogic();
        TurretType type = turret.getCoreType();
        float health = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHealth() : 0F;
        float damage = RayMath.damage(ConfigLogicSettings.INSTANCE.turret(type).baseDamage(), modifier,
                logic.getDamageAmpBonus(type), health);
        ProjectileMath.Split split = RayMath.split(weapon.profile(), damage);
        int fakeDrops = logic.getLootingLevel(3);
        String name = turret.getDamageTypeName();
        if (!(entity instanceof EntityPlayer)) {
            TurretDamage.setTagsForTurretHit(entity, base);
        }
        if (split.normal() > 0) {
            entity.attackEntityFrom(new NormalDamageSource(name, fakeDrops, base, world, false), split.normal());
        }
        if (split.armorBypass() > 0) {
            entity.attackEntityFrom(new ArmorBypassDamageSource(name, fakeDrops, base, world, false),
                    split.armorBypass());
        }
        entity.hurtResistantTime = -1;
    }

    /**
     * 1.12.2 {@code RailGunTurretTileEntity.handleBlockHit}: with {@code canRailgunDestroyBlocks} the railgun breaks
     * the hit block (no drops) if its hardness is below 200. Unbreakable blocks (hardness -1) are kept; 1.12.2
     * would have destroyed them too.
     */
    public static void railgunBlockHit(WorldServer world, BlockPos pos) {
        if (!OMTConfig.TURRETS.canRailgunDestroyBlocks) return;
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockTurret || !state.getMaterial().isSolid()) return;
        float hardness = state.getBlockHardness(world, pos);
        if (hardness >= 0 && hardness < 200) {
            world.destroyBlock(pos, false);
        }
    }

    private static void ray(WorldServer world, RayWeapon weapon, Vec3d from, Vec3d to) {
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), from.x,
                from.y, from.z, RENDER_RANGE);
        if (weapon.lightning()) {
            OMLibNetworkingHandler.INSTANCE.sendToAllAround(new MessageRenderLightning(from, to, weapon.duration()), point);
        } else {
            OMLibNetworkingHandler.INSTANCE.sendToAllAround(
                    new MessageRenderRay(from, to, weapon.color(), weapon.duration(), true), point);
        }
    }
}
