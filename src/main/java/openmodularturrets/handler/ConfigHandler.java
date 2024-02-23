package openmodularturrets.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static boolean turretAlarmSound;
    public static boolean turretBreakable;
    public static boolean turretWarnMessage;
    public static boolean turretDamageTrustedPlayers;
    public static boolean IGWNotification;
    public static String recipes;
    public static boolean shouldDoThaumcraftIntegration;
    private static boolean shouldDoComputerIntegration;
    public static boolean globalCanTargetPlayers;
    public static boolean globalCanTargetNeutrals;
    public static boolean globalCanTargetMobs;
    public static boolean canRocketsHome;
    public static boolean offlineModeSupport;
    private static int expanderPowerTierOneCapacity;
    private static int expanderPowerTierTwoCapacity;
    private static int expanderPowerTierThreeCapacity;
    private static int expanderPowerTierFourCapacity;
    private static int expanderPowerTierFiveCapacity;
    private static int recyclerNegateChance;
    private static int recyclerAddChance;
    private static int turretTargetSearchTicks;
    public static boolean EUSupport;
    public static double EUtoRFRatio;
    private static int baseTierOneMaxCharge;
    private static int baseTierOneMaxIo;
    private static int baseTierOneBlastResistance;
    private static int baseTierTwoMaxCharge;
    private static int baseTierTwoMaxIo;
    private static int baseTierTwoBlastResistance;
    private static int baseTierThreeMaxCharge;
    private static int baseTierThreeMaxIo;
    private static int baseTierThreeBlastResistance;
    private static int baseTierFourMaxCharge;
    private static int baseTierFourMaxIo;
    private static int baseTierFourBlastResistance;
    private static int baseTierFiveMaxCharge;
    private static int baseTierFiveMaxIo;
    private static int baseTierFiveBlastResistance;
    private static int potentiaToRFRatio;
    private static int potentiaAddonCapacity;
    private static TurretSetting disposable_turret;
    private static TurretSetting potato_cannon_turret;
    private static TurretSetting gun_turret;
    private static TurretSetting incendiary_turret;
    private static TurretSetting grenade_turret;
    private static TurretSetting relativistic_turret;
    private static TurretSetting rocket_turret;
    private static TurretSetting teleporter_turret;
    private static TurretSetting laser_turret;
    private static TurretSetting railgun_turret;
    private static int rangeUpgradeBoost;
    private static double fireRateUpgradeBoostPercentage;
    private static double accuracyUpgradeBoost;
    private static double efficiencyUpgradeBoostPercentage;
    private static int solarPanelAddonGen;
    private static int redstoneReactorAddonGen;
    private static int turretWarningDistance;
    private static float turretSoundVolume;
    private static boolean allowBaseCamo;
    private static boolean canRocketsHurtEnderDragon;
    private static boolean shouldSpawnDungeonLoot;
    public static boolean canOPAccessTurrets;

    public static void init(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();

        baseTierOneMaxCharge = config.get("TurretBaseTierOne", "MaxCharge", 500).getInt();
        baseTierOneMaxIo = config.get("TurretBaseTierOne", "MaxIo", 50).getInt();
        baseTierOneBlastResistance = config.get("TurretBaseTierOne", "BlastResistance", 10).getInt();

        baseTierTwoMaxCharge = config.get("TurretBaseTierTwo", "MaxCharge", 50000).getInt();
        baseTierTwoMaxIo = config.get("TurretBaseTierTwo", "MaxIo", 100).getInt();
        baseTierTwoBlastResistance = config.get("TurretBaseTierTwo", "BlastResistance", 10).getInt();

        baseTierThreeMaxCharge = config.get("TurretBaseTierThree", "MaxCharge", 150000).getInt();
        baseTierThreeMaxIo = config.get("TurretBaseTierThree", "MaxIo", 500).getInt();
        baseTierThreeBlastResistance = config.get("TurretBaseTierThree", "BlastResistance", 10).getInt();

        baseTierFourMaxCharge = config.get("TurretBaseTierFour", "MaxCharge", 500000).getInt();
        baseTierFourMaxIo = config.get("TurretBaseTierFour", "MaxIo", 1500).getInt();
        baseTierFourBlastResistance = config.get("TurretBaseTierFour", "BlastResistance", 10).getInt();

        baseTierFiveMaxCharge = config.get("TurretBaseTierFive", "MaxCharge", 10000000).getInt();
        baseTierFiveMaxIo = config.get("TurretBaseTierFive", "MaxIo", 5000).getInt();
        baseTierFiveBlastResistance = config.get("TurretBaseTierFive", "BlastResistance", 10).getInt();

        disposable_turret = new TurretSetting(
                config.get("TurretDisposable", "Range", 10, "Turret range, in blocks").getInt(),
                config.get("TurretDisposable", "FireRateCooldown", 25, "Number of ticks between firings").getInt(),
                config.get("TurretDisposable", "Damage", 2, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretDisposable",
                        "Accuracy",
                        50,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretDisposable", "PowerUsage", 2, "RF used per shot").getInt(),
                config.get("TurretDisposable", "Enabled", true, "Enabled?").getBoolean());

        potato_cannon_turret = new TurretSetting(
                config.get("TurretPotatoCannon", "Range", 15, "Turret range, in blocks").getInt(),
                config.get("TurretPotatoCannon", "FireRateCooldown", 35, "Number of ticks between firings").getInt(),
                config.get("TurretPotatoCannon", "Damage", 3, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretPotatoCannon",
                        "Accuracy",
                        30,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretPotatoCannon", "PowerUsage", 10, "RF used per shot").getInt(),
                config.get("TurretPotatoCannon", "Enabled", true, "Enabled?").getBoolean());

        gun_turret = new TurretSetting(
                config.get("TurretMachineGun", "Range", 18, "Turret range, in blocks").getInt(),
                config.get("TurretMachineGun", "FireRateCooldown", 8, "Number of ticks between firings").getInt(),
                config.get("TurretMachineGun", "Damage", 2, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretMachineGun",
                        "Accuracy",
                        30,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretMachineGun", "PowerUsage", 100, "RF used per shot").getInt(),
                config.get("TurretMachineGun", "Enabled", true, "Enabled?").getBoolean());

        incendiary_turret = new TurretSetting(
                config.get("TurretIncendiary", "Range", 12, "Turret range, in blocks").getInt(),
                config.get("TurretIncendiary", "FireRateCooldown", 25, "Number of ticks between firings").getInt(),
                config.get("TurretIncendiary", "Damage", 2, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretIncendiary",
                        "Accuracy",
                        30,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretIncendiary", "PowerUsage", 250, "RF used per shot").getInt(),
                config.get("TurretIncendiary", "Enabled", true, "Enabled?").getBoolean());

        grenade_turret = new TurretSetting(
                config.get("TurretGrenade", "Range", 20, "Turret range, in blocks").getInt(),
                config.get("TurretGrenade", "FireRateCooldown", 40, "Number of ticks between firings").getInt(),
                config.get("TurretGrenade", "Damage", 8, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretGrenade",
                        "Accuracy",
                        30,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretGrenade", "PowerUsage", 3000, "RF used per shot").getInt(),
                config.get("TurretGrenade", "Enabled", true, "Enabled?").getBoolean());

        relativistic_turret = new TurretSetting(
                config.get("TurretRelativistic", "Range", 20, "Turret range, in blocks").getInt(),
                config.get("TurretRelativistic", "FireRateCooldown", 100, "Number of ticks between firings").getInt(),
                config.get("TurretRelativistic", "Damage", 0, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretRelativistic",
                        "Accuracy",
                        0,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretRelativistic", "PowerUsage", 5000, "RF used per shot").getInt(),
                config.get("TurretRelativistic", "Enabled", true, "Enabled?").getBoolean());

        rocket_turret = new TurretSetting(
                config.get("TurretRocket", "Range", 30, "Turret range, in blocks").getInt(),
                config.get("TurretRocket", "FireRateCooldown", 30, "Number of ticks between firings").getInt(),
                config.get("TurretRocket", "Damage", 10, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretRocket",
                        "Accuracy",
                        10,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretRocket", "PowerUsage", 5000, "RF used per shot").getInt(),
                config.get("TurretRocket", "Enabled", true, "Enabled?").getBoolean());

        teleporter_turret = new TurretSetting(
                config.get("TurretTeleporter", "Range", 20, "Turret range, in blocks").getInt(),
                config.get("TurretTeleporter", "FireRateCooldown", 100, "Number of ticks between firings").getInt(),
                config.get("TurretTeleporter", "Damage", 0, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretTeleporter",
                        "Accuracy",
                        0,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretTeleporter", "PowerUsage", 15000, "RF used per shot").getInt(),
                config.get("TurretTeleporter", "Enabled", true, "Enabled?").getBoolean());

        laser_turret = new TurretSetting(
                config.get("TurretLaser", "Range", 25, "Turret range, in blocks").getInt(),
                config.get("TurretLaser", "FireRateCooldown", 10, "Number of ticks between firings").getInt(),
                config.get("TurretLaser", "Damage", 2, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretLaser",
                        "Accuracy",
                        10,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretLaser", "PowerUsage", 10000, "RF used per shot").getInt(),
                config.get("TurretLaser", "Enabled", true, "Enabled?").getBoolean());

        railgun_turret = new TurretSetting(
                config.get("TurretRailgun", "Range", 30, "Turret range, in blocks").getInt(),
                config.get("TurretRailgun", "FireRateCooldown", 100, "Number of ticks between firings").getInt(),
                config.get("TurretRailgun", "Damage", 25, "Measured in half-hearts").getInt(),
                config.get(
                        "TurretRailgun",
                        "Accuracy",
                        0,
                        "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.")
                        .getDouble(),
                config.get("TurretRailgun", "PowerUsage", 25000, "RF used per shot").getInt(),
                config.get("TurretRailgun", "Enabled", true, "Enabled?").getBoolean());

        rangeUpgradeBoost = config.get("upgrades", "range", 2, "Increases range in blocks linearly").getInt();
        fireRateUpgradeBoostPercentage = config
                .get("upgrades", "rateOfFire", 0.1D, "It's a double for some reason, reduces cooldown").getDouble();
        accuracyUpgradeBoost = config.get("upgrades", "accuracy", 0.2D, "Increases accuracy linearly").getDouble();
        efficiencyUpgradeBoostPercentage = config
                .get("upgrades", "efficiency", 0.08D, "Reduces power consumption linearly").getDouble();

        solarPanelAddonGen = config.get("addons", "solar", 10, "Generates specified RF every tick in sunlight")
                .getInt();
        redstoneReactorAddonGen = config
                .get("addons", "redstone", 1550, "Generates RF from redstone dust/blocks in turret's inventory")
                .getInt();

        recyclerNegateChance = config.get("addons", "recycler", 10, "Recycler ammo use negation % chance").getInt();

        recyclerAddChance = config.get("addons", "recycler", 5, "Recycler ammo add % chance").getInt();

        turretWarningDistance = config.get(
                "miscellaneous",
                "warningDistance",
                5,
                "Distance outside the max range of a turret players should be warned").getInt();

        turretAlarmSound = config
                .get("miscellaneous", "Enable/Disable turret alarm sound. True=enabled, false=disabled", true)
                .getBoolean();
        turretWarnMessage = config.get("miscellaneous", "Should turret warn message be displayed?", true).getBoolean();
        turretBreakable = config.get("miscellaneous", "Are turrets/turret bases breakable by anyone?", false)
                .getBoolean();

        turretDamageTrustedPlayers = config
                .get("miscellaneous", "Can turrets damage their trusted players when they accidentally hit them?", true)
                .getBoolean();

        canRocketsHome = config
                .get("miscellaneous", "Can rockets fired by the rocket launcher turret home on targets ?", false)
                .getBoolean();

        canRocketsHurtEnderDragon = config
                .get("miscellaneous", "Can rockets fired by the rocket launcher turret hurt the Ender Dragon?", false)
                .getBoolean();

        shouldSpawnDungeonLoot = config.get("miscellaneous", "Should we generate dungeon loot?", true).getBoolean();

        recipes = config.get(
                "miscellaneous",
                "Which recipes should we do? (auto, enderio, thermalexpansion, mekanism, vanilla)",
                "auto").getString();

        turretSoundVolume = config.get("miscellaneous", "Turret sound volume percentage (Between 0 - 100)", 40).getInt()
                / 10;

        allowBaseCamo = config.get("miscellaneous", "Should turret bases be camouflage-able with normal blocks?", true)
                .getBoolean();

        shouldDoThaumcraftIntegration = config
                .get("ModCompatibility", "Should we enable items that integrate with Thaumcraft?", true).getBoolean();

        shouldDoComputerIntegration = config.get(
                "ModCompatibility",
                "Should we enable items that integrate with ComputerCraft/OpenComputers?",
                true).getBoolean();

        IGWNotification = config.get("ModCompatibility", "Enable IGW Mod notification", true).getBoolean();

        potentiaToRFRatio = config.get("ModCompatibility", "Potentia Addons' RF conversion ratio per 1 essentia", 500)
                .getInt();

        potentiaAddonCapacity = config.get("ModCompatibility", "How much essentia the Potentia Addon can store", 20)
                .getInt();

        globalCanTargetPlayers = config.get("GlobalTargetingParameters", "Can turrets attack players?", true)
                .getBoolean();

        globalCanTargetNeutrals = config.get("GlobalTargetingParameters", "Can turrets attack neutrals?", true)
                .getBoolean();

        globalCanTargetMobs = config.get("GlobalTargetingParameters", "Can turrets attack mobs?", true).getBoolean();

        canOPAccessTurrets = config.get("miscellaneous", "Can OPs access all turrets?", false).getBoolean();

        expanderPowerTierOneCapacity = config.get("Expanders", "Power expander tier one capacity", 250).getInt();

        expanderPowerTierTwoCapacity = config.get("Expanders", "Power expander tier two capacity", 25000).getInt();

        expanderPowerTierThreeCapacity = config.get("Expanders", "Power expander tier three capacity", 75000).getInt();

        expanderPowerTierFourCapacity = config.get("Expanders", "Power expander tier four capacity", 250000).getInt();

        expanderPowerTierFiveCapacity = config.get("Expanders", "Power expander tier five capacity", 5000000).getInt();

        turretTargetSearchTicks = config.get(
                "GlobalTargetingParameters",
                "If a turret does not have a target, how many ticks should it wait before looking again?",
                10).getInt();

        EUSupport = config.get("ModCompatability", "Can turrets be powered with EU?", true).getBoolean();
        offlineModeSupport = config
                .get("ModCompatability", "Enable offline mode support?(warning, makes turrets fairly unsafe)", false)
                .getBoolean();
        EUtoRFRatio = config.get("ModCompatability", "EU to RF Ratio", 8.0D).getDouble();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static int getBaseTierOneMaxIo() {
        return baseTierOneMaxIo;
    }

    public static int getBaseTierTwoMaxIo() {
        return baseTierTwoMaxIo;
    }

    public static int getBaseTierThreeMaxIo() {
        return baseTierThreeMaxIo;
    }

    public static int getBaseTierFourMaxIo() {
        return baseTierFourMaxIo;
    }

    public static int getBaseTierFiveMaxCharge() {
        return baseTierFiveMaxCharge;
    }

    public static int getBaseTierFiveMaxIo() {
        return baseTierFiveMaxIo;
    }

    public static int getBaseTierTwoMaxCharge() {
        return baseTierTwoMaxCharge;
    }

    public static int getBaseTierThreeMaxCharge() {
        return baseTierThreeMaxCharge;
    }

    public static int getBaseTierFourMaxCharge() {
        return baseTierFourMaxCharge;
    }

    public static int getBaseTierOneMaxCharge() {
        return baseTierOneMaxCharge;
    }

    public static int getBaseTierOneBlastResistance() {
        return baseTierOneBlastResistance;
    }

    public static int getBaseTierTwoBlastResistance() {
        return baseTierTwoBlastResistance;
    }

    public static int getBaseTierThreeBlastResistance() {
        return baseTierThreeBlastResistance;
    }

    public static int getBaseTierFourBlastResistance() {
        return baseTierFourBlastResistance;
    }

    public static int getBaseTierFiveBlastResistance() {
        return baseTierFiveBlastResistance;
    }

    public static int getTurretWarningDistance() {
        return turretWarningDistance;
    }

    public static TurretSetting getDisposableTurretSettings() {
        return disposable_turret;
    }

    public static TurretSetting getPotatoCannonTurretSettings() {
        return potato_cannon_turret;
    }

    public static TurretSetting getGunTurretSettings() {
        return gun_turret;
    }

    public static TurretSetting getRocketTurretSettings() {
        return rocket_turret;
    }

    public static TurretSetting getGrenadeTurretSettings() {
        return grenade_turret;
    }

    public static TurretSetting getLaserTurretSettings() {
        return laser_turret;
    }

    public static TurretSetting getRailgun_turret() {
        return railgun_turret;
    }

    public static int getRangeUpgradeBoost() {
        return rangeUpgradeBoost;
    }

    public static double getFireRateUpgradeBoostPercentage() {
        return fireRateUpgradeBoostPercentage;
    }

    public static double getAccuracyUpgradeBoost() {
        return accuracyUpgradeBoost;
    }

    public static double getEfficiencyUpgradeBoostPercentage() {
        return efficiencyUpgradeBoostPercentage;
    }

    public static int getSolarPanelAddonGen() {
        return solarPanelAddonGen;
    }

    public static int getRedstoneReactorAddonGen() {
        return redstoneReactorAddonGen;
    }

    public static int getPotentiaToRFRatio() {
        return potentiaToRFRatio;
    }

    public static int getPotentiaAddonCapacity() {
        return potentiaAddonCapacity;
    }

    public static TurretSetting getPotato_cannon_turret() {
        return potato_cannon_turret;
    }

    public static TurretSetting getIncendiary_turret() {
        return incendiary_turret;
    }

    public static TurretSetting getRelativistic_turret() {
        return relativistic_turret;
    }

    public static TurretSetting getTeleporter_turret() {
        return teleporter_turret;
    }

    public static float getTurretSoundVolume() {
        return turretSoundVolume;
    }

    public static int getExpanderPowerTierOneCapacity() {
        return expanderPowerTierOneCapacity;
    }

    public static int getExpanderPowerTierTwoCapacity() {
        return expanderPowerTierTwoCapacity;
    }

    public static int getExpanderPowerTierThreeCapacity() {
        return expanderPowerTierThreeCapacity;
    }

    public static int getExpanderPowerTierFourCapacity() {
        return expanderPowerTierFourCapacity;
    }

    public static int getExpanderPowerTierFiveCapacity() {
        return expanderPowerTierFiveCapacity;
    }

    public static int getRecyclerAddChance() {
        return recyclerAddChance;
    }

    public static int getRecyclerNegateChance() {
        return recyclerNegateChance;
    }

    public static int getTurretTargetSearchTicks() {
        return turretTargetSearchTicks;
    }

    public static boolean isAllowBaseCamo() {
        return allowBaseCamo;
    }

    public static boolean isCanRocketsHurtEnderDragon() {
        return canRocketsHurtEnderDragon;
    }

    public static boolean isShouldSpawnDungeonLoot() {
        return shouldSpawnDungeonLoot;
    }

    public static class TurretSetting {

        private final int range;
        private final int rof;
        private final int damage;
        private final double accuracy;
        private final int power_usage;
        private final boolean enabled;

        public TurretSetting(int range, int rof, int damage, double accuracy, int power_usage, boolean enabled) {
            this.range = range;
            this.rof = rof;
            this.damage = damage;
            this.accuracy = accuracy;
            this.power_usage = power_usage;
            this.enabled = enabled;
        }

        public int getRange() {
            return range;
        }

        public int getFireRate() {
            return rof;
        }

        public int getDamage() {
            return damage;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public int getPowerUsage() {
            return power_usage;
        }

        public boolean isEnabled() {
            return enabled;
        }

    }
}
