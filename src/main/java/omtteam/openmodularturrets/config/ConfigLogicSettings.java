package omtteam.openmodularturrets.config;

import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.core.settings.BaseTierStats;
import omtteam.openmodularturrets.core.settings.LogicSettings;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link LogicSettings} backed by the config currently in effect ({@link OMTConfig#get()}).
 */
public final class ConfigLogicSettings implements LogicSettings {
    public static final ConfigLogicSettings INSTANCE = new ConfigLogicSettings();

    /** Entity lists converted to strings, cached per config instance (it is replaced on reload and sync). */
    private ConfigData cachedFor;
    private Set<String> blocked = Set.of();
    private Set<String> forcedHostile = Set.of();
    private Set<String> forcedNeutral = Set.of();

    private ConfigLogicSettings() {
    }

    private synchronized void refreshLists() {
        ConfigData current = OMTConfig.get();
        if (current == cachedFor) return;
        blocked = toStrings(current.general.blockedEntities());
        forcedHostile = toStrings(current.general.forcedHostileEntities());
        forcedNeutral = toStrings(current.general.forcedNeutralEntities());
        cachedFor = current;
    }

    private static Set<String> toStrings(Set<ResourceLocation> ids) {
        return ids.stream().map(ResourceLocation::toString).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean globalCanTargetNeutrals() {
        return OMTConfig.get().turrets.globalCanTargetNeutrals;
    }

    @Override
    public boolean globalCanTargetMobs() {
        return OMTConfig.get().turrets.globalCanTargetMobs;
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
    public TurretStats turret(TurretType type) {
        TurretSetting s = OMTConfig.get().turrets.get(type);
        return new TurretStats(s.enabled, s.baseRange, s.baseFireRate, s.baseDamage, s.powerUsage,
                s.baseAccuracyDeviation, s.maxSimultaneous, s.damageAmp, s.fireRateUpgrade, s.rangeUpgrade,
                s.accuracyUpgrade, s.efficiencyUpgrade, s.recyclerNegateChance, s.recyclerAddChance);
    }

    @Override
    public BaseTierStats baseTier(int tier) {
        BaseSetting s = OMTConfig.get().bases.tier(tier);
        return new BaseTierStats(s.baseMaxCharge, s.baseMaxIo, s.baseMaxTurrets);
    }

    @Override
    public int powerExpanderCapacity(int expanderTier) {
        ConfigData.Miscellaneous misc = OMTConfig.get().miscellaneous;
        return switch (expanderTier) {
            case 1 -> misc.expanderPowerTierOneCapacity;
            case 2 -> misc.expanderPowerTierTwoCapacity;
            case 3 -> misc.expanderPowerTierThreeCapacity;
            case 4 -> misc.expanderPowerTierFourCapacity;
            case 5 -> misc.expanderPowerTierFiveCapacity;
            default -> 0;
        };
    }

    @Override
    public int solarPanelGeneration() {
        return OMTConfig.get().miscellaneous.solarPanelAddonGen;
    }

    @Override
    public int redstoneReactorGeneration() {
        return OMTConfig.get().miscellaneous.redstoneReactorAddonGen;
    }

    @Override
    public boolean turretsNeedAmmo() {
        return OMTConfig.get().turrets.doTurretsNeedAmmo;
    }

    @Override
    public boolean turretsConcealWithoutAddon() {
        return OMTConfig.get().turrets.canTurretsConcealWithoutAddon;
    }

    @Override
    public boolean globalCanTargetPlayers() {
        return OMTConfig.get().turrets.globalCanTargetPlayers;
    }

    @Override
    public int turretWarningDistance() {
        return OMTConfig.get().turrets.turretWarningDistance;
    }

    @Override
    public int targetSearchTicks() {
        return OMTConfig.get().turrets.turretTargetSearchTicks;
    }
}
