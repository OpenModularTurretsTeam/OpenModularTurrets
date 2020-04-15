package omtteam.openmodularturrets.entity.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.util.OMTUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("unused")
public abstract class TurretProjectile extends EntityThrowable implements IEntityAdditionalSpawnData {
    public float gravity;
    public boolean isAmped;
    public int amp_level;
    public int fakeDrops;
    public boolean dropLoot = true;
    protected TurretBase turretBase;
    private BlockPos turretBasePos;
    ItemStack ammo;
    private int framesRendered = 0;

    TurretProjectile(World world) {
        super(world);
    }

    TurretProjectile(World world, TurretBase turretBase) {
        super(world);
        this.turretBase = turretBase;
        this.turretBasePos = turretBase.getPos();
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
        fakeDrops = TurretHeadUtil.getFakeDropsLevel(turretBase);
        dropLoot = TurretHeadUtil.baseHasNoLootDeleter(turretBase);
    }

    TurretProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world);
        this.ammo = ammo;
        this.turretBase = turretBase;
        this.turretBasePos = turretBase.getPos();
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
        fakeDrops = TurretHeadUtil.getFakeDropsLevel(turretBase);
        dropLoot = TurretHeadUtil.baseHasNoLootDeleter(turretBase);
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(turretBasePos.getX());
        buf.writeInt(turretBasePos.getY());
        buf.writeInt(turretBasePos.getZ());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        int x, y, z;
        x = additionalData.readInt();
        y = additionalData.readInt();
        z = additionalData.readInt();
        turretBasePos = new BlockPos(x, y, z);
        turretBase = (TurretBase) this.getEntityWorld().getTileEntity(turretBasePos);
    }

    protected boolean canDamagePlayer(EntityPlayer entityPlayer) {
        return OMTUtil.canDamagePlayer(entityPlayer, turretBase);
    }

    protected boolean canDamageEntity(Entity entity) {
        return OMTUtil.canDamageEntity(entity, turretBase);
    }

    public abstract void onHitBlock(IBlockState block, BlockPos pos);

    public abstract void onHitEntity(Entity entity);

    public abstract void playSound();

    public abstract double getDamageAmpBonus();

    @Override
    public void onUpdate() {
        if (this.ticksExisted > 40) {
            this.setDead();
            return;
        }

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.getEntityWorld().rayTraceBlocks(vec3d, vec3d1);
        List<Entity> list = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(motionX * 1.2D, motionY * 1.2D, motionZ * 1.2D));

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                this.onHitEntity(entity);
            }
        }

        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                this.onHitBlock(this.getEntityWorld().getBlockState(raytraceresult.getBlockPos()), raytraceresult.getBlockPos());
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = this.getGravityVelocity();

        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }
            f1 = 0.8F;
        }

        this.motionX *= f1;
        this.motionY *= f1;
        this.motionZ *= f1;

        if (!this.hasNoGravity()) {
            this.motionY -= f2;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean writeToNBTOptional(NBTTagCompound nbtTagCompound) {
        this.setDead();
        return false;
    }

    protected void setTagsForTurretHit(Entity entity) {
        EntityLivingBase entityLivingBase;
        if (entity instanceof EntityLivingBase) {
            entityLivingBase = (EntityLivingBase) entity;
            if (!(entityLivingBase instanceof EntityPlayer) && !entityLivingBase.getTags().contains("openmodularturrets:turret_hit")) {
                entityLivingBase.addTag("openmodularturrets:turret_hit");
            }
            if (!(entityLivingBase instanceof EntityPlayer) && this.fakeDrops > -1) {
                entityLivingBase.addTag("openmodularturrets:fake_drops_" + this.fakeDrops);
            }
            if (!(entityLivingBase instanceof EntityPlayer) && !this.dropLoot && !entityLivingBase.getTags().contains("openmodularturrets:dont_drop_loot")) {
                entityLivingBase.addTag("openmodularturrets:dont_drop_loot");
            }
        }
    }

    public int getFramesRendered() {
        return framesRendered;
    }

    public void setFramesRendered(int framesRendered) {
        this.framesRendered = framesRendered;
    }
}
