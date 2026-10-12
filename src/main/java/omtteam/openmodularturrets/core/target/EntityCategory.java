package omtteam.openmodularturrets.core.target;

/**
 * Default classification of an entity before the config force lists apply (oldmod
 * {@code TurretTargetingUtils.isEntityValidMob} / {@code isEntityValidNeutral}).
 */
public enum EntityCategory {
    /** Creature type monster. */
    MONSTER,
    /** Animals and ambient creatures (bats). */
    PASSIVE,
    /** Everything else (players, golems, villagers, armor stands, water creatures, …). */
    OTHER
}
