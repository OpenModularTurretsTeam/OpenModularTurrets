package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.render.MessageSpawnParticleQuad;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PlasmaProjectile extends TurretProjectile {
    @SuppressWarnings("unused")
    public PlasmaProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.001F;
    }

    public PlasmaProjectile(World world, TurretBase turretBase) {
        super(world, turretBase);
        this.gravity = 0.001F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 30) {
            this.setDead();
        }
    }

    private void explode() {
        if (this.isDead) {
            return;
        }

        this.setPosition(this.posX + (this.motionX * 0.8),
                         this.posY + (this.motionY * 0.8),
                         this.posZ + (this.motionZ * 0.8));
        this.motionX = 0.0F;
        this.motionY = 0.0F;
        this.motionZ = 0.0F;

        if (!this.getEntityWorld().isRemote) {
            MessageSpawnParticleQuad flameParticles = new MessageSpawnParticleQuad(EnumParticleTypes.FLAME.getParticleID(),
                                                                                   this.getEntityWorld().provider.getDimension(),
                                                                                   posX, posY, posZ, 2, 1, 2, 0.2D, 15);
            MessageSpawnParticleQuad smokeParticles = new MessageSpawnParticleQuad(EnumParticleTypes.SMOKE_LARGE.getParticleID(),
                                                                                   this.getEntityWorld().provider.getDimension(),
                                                                                   posX, posY, posZ, 2, 1, 2, 0.2D, 15);
            OMLibNetworkingHandler.INSTANCE.sendToAllTracking(flameParticles, this);
            OMLibNetworkingHandler.INSTANCE.sendToAllTracking(smokeParticles, this);

            AxisAlignedBB axis = new AxisAlignedBB(this.posX - 2, this.posY - 2, this.posZ - 2,
                                                   this.posX + 2, this.posY + 2, this.posZ + 2);
            List<EntityLivingBase> targets = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase entity : targets) {
                int damage = OMTConfig.TURRETS.plasma_turret.baseDamage;

                if (isAmped) {
                    damage += ((int) entity.getHealth() * (getDamageAmpBonus() * amp_level));
                }

                if ((entity instanceof EntityPlayer && canDamagePlayer((EntityPlayer) entity)) || canDamageEntity(entity)) {
                    if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
                    entity.attackEntityFrom(new NormalDamageSource("plasma", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage * 0.5F);
                    entity.attackEntityFrom(new ArmorBypassDamageSource("plasma", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage * 0.5F);
                    entity.hurtResistantTime = -1;
                }
            }
        }
        this.setDead();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onHitBlock(IBlockState hitBlock, BlockPos pos) {
        if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
            return;
        }

        if (!hitBlock.getMaterial().isSolid() || pos.distanceSq(this.posX, this.posY, this.posZ) > 0.5D) {
            // Go through non solid block
            return;
        }
        explode();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (canDamageEntity(entity)) {
            explode();
        }
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
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
        return OMTConfig.TURRETS.plasma_turret.damageAmp;
    }
}