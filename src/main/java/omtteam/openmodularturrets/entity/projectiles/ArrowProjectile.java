package omtteam.openmodularturrets.entity.projectiles;

import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Set;

public class ArrowProjectile extends TurretProjectile {
    private final boolean spawned = false;
    private final Set<PotionEffect> customPotionEffects = Sets.newHashSet();
    private double damageBonus;
    private PotionType potion = PotionTypes.EMPTY;

    @SuppressWarnings("unused")
    public ArrowProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.03F;
    }

    public ArrowProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.03F;
        this.potion = PotionUtils.getPotionFromItem(ammo);
        Collection<PotionEffect> collection = PotionUtils.getFullEffectsFromItem(ammo);

        if (!collection.isEmpty()) {
            for (PotionEffect potioneffect : collection) {
                this.customPotionEffects.add(new PotionEffect(potioneffect));
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onHitBlock(IBlockState hitBlock, BlockPos pos) {
        if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
            return;
        }

        if (!hitBlock.getMaterial().isSolid()) {
            // Go through non solid block
            return;
        }

        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity != null && !getEntityWorld().isRemote && !(entity instanceof TurretProjectile) && !this.isDead) {
            int damage = OMTConfig.TURRETS.crossbow_turret.baseDamage;

            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (getDamageAmpBonus() * amp_level));
                }
            }

            if (canDamageEntity(entity)) {
                if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    for (PotionEffect effect : potion.getEffects()) {
                        elb.addPotionEffect(effect);
                    }
                    for (PotionEffect effect : customPotionEffects) {
                        elb.addPotionEffect(effect);
                    }
                }
                entity.attackEntityFrom(new NormalDamageSource("arrow", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage);
                entity.hurtResistantTime = -1;
            } else {
                return;
            }

            this.setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult result) {
    }

    @Override
    public void playSound() {

    }

    @Override
    public double getDamageAmpBonus() {
        return OMTConfig.TURRETS.crossbow_turret.damageAmp;
    }
}