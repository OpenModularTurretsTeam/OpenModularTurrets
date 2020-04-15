package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class GrenadeProjectile extends TurretProjectile {
    private boolean hit = false;

    @SuppressWarnings("unused")
    public GrenadeProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.03F;
    }

    public GrenadeProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world, ammo, turretBase);
        this.gravity = 0.03F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted >= 39) {
            if (!getEntityWorld().isRemote) {
                float strength = OMTConfig.TURRETS.canGrenadesDestroyBlocks ? 1.4F : 0.1F;
                getEntityWorld().createExplosion(null, posX, posY, posZ, strength, true);
                AxisAlignedBB axis = new AxisAlignedBB(this.posX - 3, this.posY - 3, this.posZ - 3,
                                                       this.posX + 3, this.posY + 3, this.posZ + 3);
                List<EntityLivingBase> targets = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);

                for (EntityLivingBase entity : targets) {

                    int damage = OMTConfig.TURRETS.grenade_turret.baseDamage;

                    if (isAmped) {
                        damage += ((int) entity.getHealth() * (getDamageAmpBonus() * amp_level));
                    }

                    if (canDamageEntity(entity)) {
                        if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
                        entity.attackEntityFrom(new NormalDamageSource("grenade", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage * 0.9F);
                        entity.attackEntityFrom(new ArmorBypassDamageSource("grenade", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage * 0.1F);
                        entity.hurtResistantTime = -1;
                    }
                }
                this.setDead();
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

        this.motionX = 0.0F;
        this.motionY = 0.0F;
        this.motionZ = 0.0F;
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (hit) {
            return;
        }
        if (canDamageEntity(entity)) {
            this.motionX = this.motionX * 0.2F;
            this.motionY = this.motionY * 1.2F;
            this.motionZ = this.motionZ * 0.2F;
            this.markVelocityChanged();
            this.hit = true;
            this.ticksExisted = 30;
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
        return OMTConfig.TURRETS.grenade_turret.damageAmp;
    }
}