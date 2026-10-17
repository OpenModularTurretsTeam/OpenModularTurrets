package omtteam.openmodularturrets.core.target;

import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;

import java.util.Optional;

/**
 * A living entity a turret could target, as seen by the core logic. The Minecraft layer wraps real entities; tests
 * use simple fakes.
 */
public abstract class TargetEntity {
    /** Entity id, unique while the entity is loaded. */
    public abstract int getId();

    /** Entity type id, e.g. {@code minecraft:zombie}. */
    public abstract String getTypeId();

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract double getEyeHeight();

    public abstract float getHealth();

    public abstract float getMaxHealth();

    public abstract int getArmor();

    public abstract boolean isAlive();

    /** The player behind this entity, if it is a player. */
    public abstract Optional<PlayerInfo> getPlayer();

    /** Whether this is a player in creative or spectator mode. */
    public abstract boolean isCreativeOrSpectator();

    /** The owning player of a tamed animal, if any. */
    public abstract Optional<PlayerIdentity> getOwner();

    /** A tamed horse (1.12.2 never targeted those, owned or not). */
    public abstract boolean isTamedHorse();

    /** Has the slowness effect (the slowing turret ignores slowed entities). */
    public abstract boolean hasSlowness();

    public abstract EntityCategory getCategory();

    public boolean isPlayer() {
        return getPlayer().isPresent();
    }
}
