package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
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
        if (ticksExisted >= 35) {
            if (!getEntityWorld().isRemote) {
                float strength = OMTConfig.TURRETS.canGrenadesDestroyBlocks ? 1.4F : 0.1F;
                getEntityWorld().createExplosion(null, posX, posY, posZ, strength, true);
                AxisAlignedBB axis = new AxisAlignedBB(this.posX - 3, this.posY - 3, this.posZ - 3,
                                                       this.posX + 3, this.posY + 3, this.posZ + 3);
                List<EntityLivingBase> targets = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);

                for (EntityLivingBase mob : targets) {

                    int damage = OMTConfig.TURRETS.grenade_turret.getBaseDamage();

                    if (isAmped) {
                        damage += ((int) mob.getHealth() * (getDamageAmpBonus() * amp_level));
                    }
                    setTagsForTurretHit(mob);

                    if ((mob instanceof EntityPlayer && canDamagePlayer((EntityPlayer) mob)) || canDamageEntity(mob)) {
                        mob.attackEntityFrom(new NormalDamageSource("grenade", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage * 0.9F);
                        mob.attackEntityFrom(new ArmorBypassDamageSource("grenade", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage * 0.1F);
                        mob.hurtResistantTime = -1;
                    }
                }
                this.setDead();
            }

            for (int i = 0; i <= 20; i++) {
                getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, posX, posY, posZ, 1.0D, 1.0D, 1.0D);
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
        if ((entity instanceof EntityPlayer && canDamagePlayer((EntityPlayer) entity)) || canDamageEntity(entity)) {
            this.setVelocity(this.motionX * 0.4F, this.motionY * 1.2F, this.motionZ * 0.4F);
            this.markVelocityChanged();
            this.hit = true;
            this.ticksExisted = 32;
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
        return OMTConfig.TURRETS.grenade_turret.getDamageAmp();
    }
}