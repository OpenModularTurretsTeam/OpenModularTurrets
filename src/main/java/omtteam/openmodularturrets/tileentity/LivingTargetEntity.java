package omtteam.openmodularturrets.tileentity;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.UsernameCache;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.core.target.EntityCategory;
import omtteam.openmodularturrets.core.target.TargetEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * A Minecraft {@link EntityLivingBase} as a core {@link TargetEntity}.
 */
public class LivingTargetEntity extends TargetEntity {
    private final EntityLivingBase entity;

    public LivingTargetEntity(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    @Override
    public int getId() {
        return entity.getEntityId();
    }

    /** Players are not in the entity registry; they get {@code minecraft:player} as in later versions. */
    @Override
    public String getTypeId() {
        if (entity instanceof EntityPlayer) return "minecraft:player";
        ResourceLocation key = EntityList.getKey(entity);
        return key == null ? "" : key.toString();
    }

    @Override
    public double getX() {
        return entity.posX;
    }

    @Override
    public double getY() {
        return entity.posY;
    }

    @Override
    public double getZ() {
        return entity.posZ;
    }

    @Override
    public double getEyeHeight() {
        return entity.getEyeHeight();
    }

    @Override
    public float getHealth() {
        return entity.getHealth();
    }

    @Override
    public float getMaxHealth() {
        return entity.getMaxHealth();
    }

    @Override
    public int getArmor() {
        return entity.getTotalArmorValue();
    }

    @Override
    public boolean isAlive() {
        return entity.isEntityAlive();
    }

    @Override
    public Optional<PlayerInfo> getPlayer() {
        if (!(entity instanceof EntityPlayer)) return Optional.empty();
        EntityPlayer player = (EntityPlayer) entity;
        return Optional.of(new PlayerInfo(player.getUniqueID(), player.getName(), PlayerUtil.isPlayerOP(player)));
    }

    @Override
    public boolean isCreativeOrSpectator() {
        return entity instanceof EntityPlayer && (((EntityPlayer) entity).isCreative()
                || ((EntityPlayer) entity).isSpectator());
    }

    @Override
    public boolean isTamedHorse() {
        return entity instanceof AbstractHorse && ((AbstractHorse) entity).isTame();
    }

    @Override
    public boolean hasSlowness() {
        return entity.isPotionActive(MobEffects.SLOWNESS);
    }

    /** 3.x: {@code isCreatureType(MONSTER)}, {@code EntityAnimal} / {@code EntityAmbientCreature}. */
    @Override
    public EntityCategory getCategory() {
        if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) return EntityCategory.MONSTER;
        if (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature) return EntityCategory.PASSIVE;
        return EntityCategory.OTHER;
    }

    /** The owner's name comes from Forge's username cache, so it also works while the owner is offline. */
    @Override
    public Optional<PlayerIdentity> getOwner() {
        if (!(entity instanceof IEntityOwnable)) return Optional.empty();
        UUID owner = ((IEntityOwnable) entity).getOwnerId();
        if (owner == null) return Optional.empty();
        String name = UsernameCache.getLastKnownUsername(owner);
        return Optional.of(new PlayerIdentity(owner, name == null ? "" : name));
    }
}
