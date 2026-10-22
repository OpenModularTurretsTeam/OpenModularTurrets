package omtteam.openmodularturrets.config;

import omtteam.openmodularturrets.core.settings.BaseTierStats;
import omtteam.openmodularturrets.core.settings.LogicSettings;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * {@link LogicSettings} backed by the Forge config ({@link OMTConfig}), read on every use so config changes apply at
 * once.
 */
public final class ConfigLogicSettings implements LogicSettings {
    public static final ConfigLogicSettings INSTANCE = new ConfigLogicSettings();

    /** Config section of each built-in turret type, by type path (the slowing turret is the 3.x relativistic one). */
    private static final Map<String, Supplier<TurretSetting>> TURRETS = new HashMap<>();

    static {
        TURRETS.put(TurretType.DISPOSABLE_TURRET.getPath(), () -> OMTConfig.TURRETS.disposable_turret);
        TURRETS.put(TurretType.POTATO_CANNON_TURRET.getPath(), () -> OMTConfig.TURRETS.potato_cannon_turret);
        TURRETS.put(TurretType.CROSSBOW_TURRET.getPath(), () -> OMTConfig.TURRETS.crossbow_turret);
        TURRETS.put(TurretType.MELEE_TURRET.getPath(), () -> OMTConfig.TURRETS.melee_turret);
        TURRETS.put(TurretType.MACHINE_GUN_TURRET.getPath(), () -> OMTConfig.TURRETS.machine_gun_turret);
        TURRETS.put(TurretType.INCENDIARY_TURRET.getPath(), () -> OMTConfig.TURRETS.incendiary_turret);
        TURRETS.put(TurretType.GRENADE_TURRET.getPath(), () -> OMTConfig.TURRETS.grenade_turret);
        TURRETS.put(TurretType.SLOWING_TURRET.getPath(), () -> OMTConfig.TURRETS.slowing_turret);
        TURRETS.put(TurretType.ROCKET_TURRET.getPath(), () -> OMTConfig.TURRETS.rocket_turret);
        TURRETS.put(TurretType.TELEPORTER_TURRET.getPath(), () -> OMTConfig.TURRETS.teleporter_turret);
        TURRETS.put(TurretType.ARC_TURRET.getPath(), () -> OMTConfig.TURRETS.arc_turret);
        TURRETS.put(TurretType.LASER_TURRET.getPath(), () -> OMTConfig.TURRETS.laser_turret);
        TURRETS.put(TurretType.RAILGUN_TURRET.getPath(), () -> OMTConfig.TURRETS.railgun_turret);
        TURRETS.put(TurretType.PLASMA_TURRET.getPath(), () -> OMTConfig.TURRETS.plasma_turret);
    }

    /** Entity lists as sets, cached per config array (Forge replaces the arrays when the config changes). */
    private String[] blockedSource;
    private String[] hostileSource;
    private String[] neutralSource;
    private Set<String> blocked = Collections.emptySet();
    private Set<String> forcedHostile = Collections.emptySet();
    private Set<String> forcedNeutral = Collections.emptySet();

    private ConfigLogicSettings() {
    }

    /** The config section of a turret type. */
    public static TurretSetting setting(TurretType type) {
        Supplier<TurretSetting> setting = TURRETS.get(type.getPath());
        if (setting == null || !type.isBuiltIn()) throw new IllegalArgumentException("No config for turret " + type);
        return setting.get();
    }

    private static TurretStats toStats(TurretSetting s) {
        return new TurretStats(s.enabled, s.baseRange, s.baseFireRate, s.baseDamage, s.powerUsage,
                s.baseAccuracyDeviation, s.maxSimultaneous, s.damageAmp, s.fireRateUpgrade, s.rangeUpgrade,
                s.accuracyUpgrade, s.efficiencyUpgrade, s.recyclerNegateChance, s.recyclerAddChance);
    }

    private static Set<String> toSet(String[] entries) {
        Set<String> set = new HashSet<>();
        for (String entry : entries) {
            String id = entry.trim();
            // like ResourceLocation, entries without namespace are vanilla ids
            set.add(id.indexOf(':') >= 0 ? id : "minecraft:" + id);
        }
        return Collections.unmodifiableSet(set);
    }

    private synchronized void refreshLists() {
        String[] blockedList = OMTConfig.GENERAL.stringMobBlackList;
        String[] hostileList = OMTConfig.GENERAL.stringMobForceList;
        String[] neutralList = OMTConfig.GENERAL.stringNeutralForceList;
        if (blockedList != blockedSource) {
            blocked = toSet(blockedList);
            blockedSource = blockedList;
        }
        if (hostileList != hostileSource) {
            forcedHostile = toSet(hostileList);
            hostileSource = hostileList;
        }
        if (neutralList != neutralSource) {
            forcedNeutral = toSet(neutralList);
            neutralSource = neutralList;
        }
    }

    @Override
    public TurretStats turret(TurretType type) {
        return toStats(setting(type));
    }

    @Override
    public BaseTierStats baseTier(int tier) {
        BaseSetting s = baseSetting(tier);
        return new BaseTierStats(s.baseMaxCharge, s.baseMaxIo, s.baseMaxTurrets);
    }

    /** The config section of a base tier, 1–5. */
    public static BaseSetting baseSetting(int tier) {
        BaseSetting[] tiers = {OMTConfig.BASES.baseTierOne, OMTConfig.BASES.baseTierTwo, OMTConfig.BASES.baseTierThree,
                OMTConfig.BASES.baseTierFour, OMTConfig.BASES.baseTierFive};
        if (tier < 1 || tier > tiers.length) throw new IllegalArgumentException("Invalid base tier: " + tier);
        return tiers[tier - 1];
    }

    @Override
    public int solarPanelGeneration() {
        return OMTConfig.MISCELLANEOUS.solarPanelAddonGen;
    }

    @Override
    public int redstoneReactorGeneration() {
        return OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen;
    }

    @Override
    public boolean turretsNeedAmmo() {
        return OMTConfig.TURRETS.doTurretsNeedAmmo;
    }

    @Override
    public boolean turretsConcealWithoutAddon() {
        return OMTConfig.TURRETS.canTurretsConcealWithoutAddon;
    }

    @Override
    public boolean globalCanTargetPlayers() {
        return OMTConfig.TURRETS.globalCanTargetPlayers;
    }

    @Override
    public boolean globalCanTargetNeutrals() {
        return OMTConfig.TURRETS.globalCanTargetNeutrals;
    }

    @Override
    public boolean globalCanTargetMobs() {
        return OMTConfig.TURRETS.globalCanTargetMobs;
    }

    @Override
    public Set<String> blockedEntities() {
        refreshLists();
        return blocked;
    }

    @Override
    public Set<String> forcedHostileEntities() {
        refreshLists();
        return forcedHostile;
    }

    @Override
    public Set<String> forcedNeutralEntities() {
        refreshLists();
        return forcedNeutral;
    }

    @Override
    public int turretWarningDistance() {
        return OMTConfig.TURRETS.turretWarningDistance;
    }

    @Override
    public int targetSearchTicks() {
        return OMTConfig.TURRETS.turretTargetSearchTicks;
    }
}
