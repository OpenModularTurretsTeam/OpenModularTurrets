package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.target.EntityCategory;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.Optional;
import java.util.UUID;

/**
 * A Minecraft {@link LivingEntity} as a core {@link TargetEntity}.
 */
public class LivingTargetEntity extends TargetEntity {
    private final LivingEntity entity;

    public LivingTargetEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public int getId() {
        return entity.getId();
    }

    @Override
    public String getTypeId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    @Override
    public double getX() {
        return entity.getX();
    }

    @Override
    public double getY() {
        return entity.getY();
    }

    @Override
    public double getZ() {
        return entity.getZ();
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
        return entity.getArmorValue();
    }

    @Override
    public boolean isAlive() {
        return entity.isAlive();
    }

    @Override
    public Optional<PlayerInfo> getPlayer() {
        return entity instanceof Player player ? Optional.of(PlayerAdapter.info(player)) : Optional.empty();
    }

    @Override
    public boolean isCreativeOrSpectator() {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }

    @Override
    public boolean isTamedHorse() {
        return entity instanceof AbstractHorse horse && horse.isTamed();
    }

    @Override
    public boolean hasSlowness() {
        return entity.hasEffect(MobEffects.SLOWNESS);
    }

    /** oldmod: {@code isCreatureType(MONSTER)}, {@code EntityAnimal} / {@code EntityAmbientCreature}. */
    @Override
    public EntityCategory getCategory() {
        if (entity.getType().getCategory() == MobCategory.MONSTER) return EntityCategory.MONSTER;
        if (entity instanceof Animal || entity instanceof AmbientCreature) return EntityCategory.PASSIVE;
        return EntityCategory.OTHER;
    }

    /** The owner's name comes from the server's profile cache, so it also works while the owner is offline. */
    @Override
    public Optional<PlayerIdentity> getOwner() {
        if (!(entity instanceof OwnableEntity ownable)) return Optional.empty();
        if (ownable.getOwnerReference() == null) return Optional.empty();
        UUID owner = ownable.getOwnerReference().getUUID();
        MinecraftServer server = entity.level().getServer();
        String name = "";
        if (server != null) {
            name = server.services().nameToIdCache().get(owner).map(profile -> profile.name()).orElse("");
        }
        return Optional.of(new PlayerIdentity(owner, name));
    }
}
