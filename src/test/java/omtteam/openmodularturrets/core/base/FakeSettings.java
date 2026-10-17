package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.settings.BaseTierStats;
import omtteam.openmodularturrets.core.settings.LogicSettings;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Test settings using the 1.12.2 defaults for the turrets used in tests.
 */
final class FakeSettings implements LogicSettings {
    final Map<TurretType, TurretStats> turrets = new EnumMap<>(TurretType.class);
    BaseTierStats[] tiers = {
            new BaseTierStats(500, 50, 1), new BaseTierStats(50000, 100, 1), new BaseTierStats(150000, 1000, 2),
            new BaseTierStats(500000, 2500, 3), new BaseTierStats(10000000, 50000, 4)};
    int solar = 10;
    int reactor = 1600;
    boolean needAmmo = true;
    boolean concealWithoutAddon;
    boolean globalPlayers = true;
    int warningDistance = 5;
    int searchTicks = 10;
    boolean globalNeutrals = true;
    boolean globalMobs = true;
    final Set<String> blocked = new HashSet<>(Set.of("minecraft:armor_stand"));
    final Set<String> forcedHostile = new HashSet<>(Set.of("minecraft:creeper"));
    final Set<String> forcedNeutral = new HashSet<>(Set.of("minecraft:cow"));

    FakeSettings() {
        // 1.12.2 OMTConfig defaults
        turrets.put(TurretType.DISPOSABLE_TURRET, stats(10, 25, 2, 2, 50, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
        turrets.put(TurretType.POTATO_CANNON_TURRET, stats(15, 35, 3, 10, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
        turrets.put(TurretType.MACHINE_GUN_TURRET, stats(18, 8, 2, 100, 30, 4, 0.06D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
        turrets.put(TurretType.LASER_TURRET, stats(25, 10, 4, 8000, 10, 4, 0.06D, 0.125D, 2, 0.2D, 0.08D, 0.1D));
        turrets.put(TurretType.SLOWING_TURRET, stats(20, 25, 0, 5000, 0, 4, 0D, 0.1D, 2, 0, 0.08D, 0.1D));
        turrets.put(TurretType.ROCKET_TURRET, stats(30, 30, 10, 5000, 10, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D));
        turrets.put(TurretType.RAILGUN_TURRET, stats(30, 100, 25, 25000, 3, 2, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D));
    }

    static TurretStats stats(int range, int fireRate, int damage, int power, double accuracy, int maxSimultaneous,
                             double damageAmp, double fireRateUpgrade, int rangeUpgrade, double accuracyUpgrade,
                             double efficiencyUpgrade, double recyclerNegate) {
        return new TurretStats(true, range, fireRate, damage, power, accuracy, maxSimultaneous, damageAmp,
                fireRateUpgrade, rangeUpgrade, accuracyUpgrade, efficiencyUpgrade, recyclerNegate, 0.05D);
    }

    @Override
    public TurretStats turret(TurretType type) {
        TurretStats stats = turrets.get(type);
        if (stats == null) throw new IllegalArgumentException("No test stats for " + type);
        return stats;
    }

    @Override
    public BaseTierStats baseTier(int tier) {
        return tiers[tier - 1];
    }

    @Override
    public int solarPanelGeneration() {
        return solar;
    }

    @Override
    public int redstoneReactorGeneration() {
        return reactor;
    }

    @Override
    public boolean turretsNeedAmmo() {
        return needAmmo;
    }

    @Override
    public boolean turretsConcealWithoutAddon() {
        return concealWithoutAddon;
    }

    @Override
    public boolean globalCanTargetPlayers() {
        return globalPlayers;
    }

    @Override
    public int turretWarningDistance() {
        return warningDistance;
    }

    @Override
    public boolean globalCanTargetNeutrals() {
        return globalNeutrals;
    }

    @Override
    public boolean globalCanTargetMobs() {
        return globalMobs;
    }

    @Override
    public Set<String> blockedEntities() {
        return blocked;
    }

    @Override
    public Set<String> forcedHostileEntities() {
        return forcedHostile;
    }

    @Override
    public Set<String> forcedNeutralEntities() {
        return forcedNeutral;
    }

    @Override
    public int targetSearchTicks() {
        return searchTicks;
    }
}
