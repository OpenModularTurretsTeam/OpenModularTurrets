package omtteam.openmodularturrets.core.target;

import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;

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

    /** Height of the eyes above {@link #getY()}, where line of sight ends. */
    public abstract double getEyeHeight();

    /** Current health in half hearts. */
    public abstract float getHealth();

    public abstract float getMaxHealth();

    /** Armor points (20 = full armor bar), as used in the ray damage formulas. */
    public abstract int getArmor();

    /** False once the entity is dead or removed. */
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

    /** The default class of the entity, before the config lists override it (see {@code TargetSelector#classify}). */
    public abstract EntityCategory getCategory();

    /** Whether this entity is a player. */
    public boolean isPlayer() {
        return getPlayer().isPresent();
    }
}
