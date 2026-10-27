package omtteam.openmodularturrets.core.settings;

import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.Set;

/**
 * Config values the core logic needs. Implemented by the Minecraft layer on top of the mod config; read on every use
 * so config changes and server-synced values apply immediately.
 */
public interface LogicSettings {
    /** Stats of a turret type, including the config values of its section. */
    TurretStats turret(TurretType type);

    /** @param tier 1–5 */
    BaseTierStats baseTier(int tier);

    /**
     * {@code expanders.tier_N.stacks}: stacks an inventory expander of this tier adds to every ammo and fuel slot of
     * the machine it is attached to.
     *
     * @param tier 1–3
     */
    int expanderStacks(int tier);

    /** {@code miscellaneous.solarPanelAddonGen}. */
    int solarPanelGeneration();

    /** {@code miscellaneous.redstoneReactorAddonGen}. */
    int redstoneReactorGeneration();

    /** {@code turrets.doTurretsNeedAmmo}. */
    boolean turretsNeedAmmo();

    /**
     * {@code turrets.dropTurretsOverMaxSimultaneous}: attached turrets over their type's {@code maxSimultaneous} drop
     * (after the limit was lowered in the config); otherwise the limit only applies to placing.
     */
    boolean dropTurretsOverMaxSimultaneous();

    /** {@code bases.unownedBasesActive}: bases without an owner target and fire; otherwise they are inert. */
    boolean unownedBasesActive();

    /** {@code turrets.canTurretsConcealWithoutAddon}. */
    boolean turretsConcealWithoutAddon();

    /** {@code turrets.globalCanTargetPlayers}. */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean globalCanTargetPlayers();

    /** {@code turrets.globalCanTargetNeutrals}. */
    boolean globalCanTargetNeutrals();

    /** {@code turrets.globalCanTargetMobs}. */
    boolean globalCanTargetMobs();

    /** Entity type ids that are never targeted ({@code general.mobBlackList}). */
    Set<String> blockedEntities();

    /** Entity type ids always treated as hostile ({@code general.mobForceList}, conflicts removed). */
    Set<String> forcedHostileEntities();

    /** Entity type ids always treated as neutral ({@code general.neutralForceList}, conflicts removed). */
    Set<String> forcedNeutralEntities();

    /** {@code turrets.turretWarningDistance}. */
    int turretWarningDistance();

    /** {@code turrets.turretTargetSearchTicks}. */
    int targetSearchTicks();
}
