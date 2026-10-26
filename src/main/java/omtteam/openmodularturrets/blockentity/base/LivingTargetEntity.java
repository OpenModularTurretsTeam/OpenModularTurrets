package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.target.EntityCategory;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.world.Pose;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.omlib.security.PlayerAdapter;

import java.util.Optional;
import java.util.UUID;

/**
 * A Minecraft {@link LivingEntity} as a core {@link TargetEntity}, with its position and eyes in a base's coordinates
 * (converted with the base's {@link Pose} when the wrapper is created).
 */
public class LivingTargetEntity extends TargetEntity {
    private final LivingEntity entity;
    private final Vector3 position;
    private final Vector3 eyes;

    /** @param pose the pose of the base the entity is seen from ({@link Pose#IDENTITY} outside contraptions) */
    public LivingTargetEntity(LivingEntity entity, Pose pose) {
        this.entity = entity;
        this.position = pose.toLocal(toVector(entity.position()));
        this.eyes = pose.toLocal(toVector(entity.getEyePosition()));
    }

    private static Vector3 toVector(Vec3 vec) {
        return new Vector3(vec.x, vec.y, vec.z);
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
        return position.x();
    }

    @Override
    public double getY() {
        return position.y();
    }

    @Override
    public double getZ() {
        return position.z();
    }

    @Override
    public double getEyeX() {
        return eyes.x();
    }

    @Override
    public double getEyeY() {
        return eyes.y();
    }

    @Override
    public double getEyeZ() {
        return eyes.z();
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
        return entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    /** 1.12.2: {@code isCreatureType(MONSTER)}, {@code EntityAnimal} / {@code EntityAmbientCreature}. */
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
        UUID owner = VersionCompat.ownerUuid(ownable);
        if (owner == null) return Optional.empty();
        MinecraftServer server = entity.level().getServer();
        String name = server == null ? "" : VersionCompat.cachedName(server, owner).orElse("");
        return Optional.of(new PlayerIdentity(owner, name));
    }
}
