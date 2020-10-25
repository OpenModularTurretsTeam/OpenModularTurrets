package omtteam.openmodularturrets.api.tileentity;

import omtteam.omlib.util.TargetingSettings;

public interface ITurretHead {
    /**
     * This should give back the TargetingSettings that this turret uses.
     *
     * @return the corresponding TargetingSettings.
     */
    TargetingSettings getTargetingSettings();

    /**
     * This should give back the Shooting Priorities that this turret uses.
     *
     * @return the corresponding Integer Array.
     */
    Integer[] getPriorities();

    /**
     * Which base does this turret belong to?
     *
     * @return the corresponding base.
     */
    ITurretBase getBase();

    /**
     * Is the turret on cooldown after activating?
     *
     * @return cooldown state.
     */
    boolean isOnCooldown();

    /**
     * Is the turret set to automatically fire?
     *
     * @return autofire state.
     */
    boolean getAutoFire();

    /**
     * Set turret set to automatically fire.
     *
     * @param autoFire the autofire state.
     */
    void setAutoFire(boolean autoFire);

    /**
     * How much power does the turret use per activation?
     *
     * @return power usage per activation
     */
    int getTurretBasePowerUsage();

    /**
     * How fast does the turret fire?
     *
     * @return number of ticks between activations
     */
    int getTurretBaseFireRate();

    /**
     * How accurate is the turret without modifications? (The config setting)
     *
     * @return double from 0 (perfect accuracy) to 1
     */
    double getBaseTurretAccuracyDeviation();

    /**
     * How far can the turret shoot?
     *
     * @return base range of the turret
     */
    int getTurretBaseRange();

    /**
     * How much maxHP% scaling does the turret get for damange amp addons?
     *
     * @return double representation of % of maxHP damage per shot. eg. 0.05D for 5%
     */
    double getTurretDamageAmpBonus();

    /**
     * How accurate is the turret actually?
     *
     * @return double from 0 (perfect accuracy) to 1
     */
    double getActualTurretAccuracyDeviation();

    /**
     * Trigger the caches to reset. Does not need to do anything if no caches are used.
     * This is called after upgrades/addons are added/removed or anything that would invalidate cached items.
     * Vanilla turrets cache accuracy and fire rate currently.
     */
    void triggerResetCaches();
}
