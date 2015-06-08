package openmodularturrets.handler;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {

    private static int baseTierOneMaxCharge;
    private static int baseTierOneMaxIo;
    private static int baseTierTwoMaxCharge;
    private static int baseTierTwoMaxIo;
    private static int baseTierThreeMaxCharge;
    private static int baseTierThreeMaxIo;
    private static int baseTierFourMaxCharge;
    private static int baseTierFourMaxIo;
    private static int baseTierFiveMaxCharge;
    private static int baseTierFiveMaxIo;
    private static int potentiaToRFRatio;
    private static int potentiaAddonCapacity;
    private static TurretSetting disposable_turret;
    private static TurretSetting potato_cannon_turret;
    private static TurretSetting machine_gun_turret;
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
    private static int damageAmpDmgBonus;
    private static int turretWarningDistance;
    public static boolean turretAlarmSound;
    public static boolean turretBreakable;
    public static boolean turretWarnMessage;
    public static boolean turretDamageTrustedPlayers;
    public static boolean IGWNotification;

    public static String recipes;

    public static boolean shouldDoThaumcraftIntegration;
    public static boolean shouldDoComputerIntegration;

    public static void init(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();

        baseTierOneMaxCharge = config
                .get("TurretBaseTierOne", "MaxCharge", 500).getInt();
        baseTierOneMaxIo = config.get("TurretBaseTierOne", "MaxIo", 50)
                .getInt();

        baseTierTwoMaxCharge = config.get("TurretBaseTierTwo", "MaxCharge",
                50000).getInt();
        baseTierTwoMaxIo = config.get("TurretBaseTierTwo", "MaxIo", 100)
                .getInt();

        baseTierThreeMaxCharge = config.get("TurretBaseTierTwo", "MaxCharge",
                150000).getInt();
        baseTierThreeMaxIo = config.get("TurretBaseTierTwo", "MaxIo", 500)
                .getInt();

        baseTierFourMaxCharge = config.get("TurretBaseTierFour", "MaxCharge",
                500000).getInt();
        baseTierFourMaxIo = config.get("TurretBaseTierFour", "MaxIo", 1500)
                .getInt();

        baseTierFiveMaxCharge = config.get("TurretBaseTierFive", "MaxCharge",
                10000000).getInt();
        baseTierFiveMaxIo = config.get("TurretBaseTierFive", "MaxIo", 5000)
                .getInt();

        disposable_turret = new TurretSetting(config.get("TurretDisposable",
                "Range", 10, "Turret range, in blocks").getInt(), config.get(
                "TurretDisposable", "FireRateCooldown", 25,
                "Number of ticks between firings").getInt(), config.get(
                "TurretDisposable", "Damage", 2, "Measured in half-hearts")
                .getInt(), config.get("TurretDisposable", "Accuracy", 50,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretDisposable", "PowerUsage", 2, "RF used per shot")
                .getInt());

        potato_cannon_turret = new TurretSetting(config.get(
                "TurretPotatoCannon", "Range", 15, "Turret range, in blocks")
                .getInt(), config.get("TurretPotatoCannon", "FireRateCooldown",
                35, "Number of ticks between firings").getInt(), config.get(
                "TurretPotatoCannon", "Damage", 3, "Measured in half-hearts")
                .getInt(), config.get("TurretPotatoCannon", "Accuracy", 30,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretPotatoCannon", "PowerUsage", 10, "RF used per shot")
                .getInt());

        machine_gun_turret = new TurretSetting(config.get("TurretMachineGun",
                "Range", 18, "Turret range, in blocks").getInt(), config.get(
                "TurretMachineGun", "FireRateCooldown", 8,
                "Number of ticks between firings").getInt(), config.get(
                "TurretMachineGun", "Damage", 2, "Measured in half-hearts")
                .getInt(), config.get("TurretMachineGun", "Accuracy", 30,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretMachineGun", "PowerUsage", 100, "RF used per shot")
                .getInt());

        incendiary_turret = new TurretSetting(config.get("TurretIncendiary",
                "Range", 12, "Turret range, in blocks").getInt(), config.get(
                "TurretIncendiary", "FireRateCooldown", 25,
                "Number of ticks between firings").getInt(), config.get(
                "TurretIncendiary", "Damage", 2, "Measured in half-hearts")
                .getInt(), config.get("TurretIncendiary", "Accuracy", 30,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretIncendiary", "PowerUsage", 250, "RF used per shot")
                .getInt());

        grenade_turret = new TurretSetting(config.get("TurretGrenade", "Range",
                20, "Turret range, in blocks").getInt(), config.get(
                "TurretGrenade", "FireRateCooldown", 40,
                "Number of ticks between firings").getInt(), config.get(
                "TurretGrenade", "Damage", 8, "Measured in half-hearts")
                .getInt(), config.get("TurretGrenade", "Accuracy", 30,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretGrenade", "PowerUsage", 3000, "RF used per shot")
                .getInt());

        relativistic_turret = new TurretSetting(config.get("TurretRelativistic", "Range",
                20, "Turret range, in blocks").getInt(), config.get(
                "TurretRelativistic", "FireRateCooldown", 100,
                "Number of ticks between firings").getInt(), config.get(
                "TurretRelativistic", "Damage", 0, "Measured in half-hearts")
                .getInt(), config.get("TurretRelativistic", "Accuracy", 0,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretRelativistic", "PowerUsage", 5000, "RF used per shot")
                .getInt());

        rocket_turret = new TurretSetting(config.get("TurretRocket", "Range",
                30, "Turret range, in blocks").getInt(), config.get(
                "TurretRocket", "FireRateCooldown", 30,
                "Number of ticks between firings").getInt(), config.get(
                "TurretRocket", "Damage", 10, "Measured in half-hearts")
                .getInt(), config.get("TurretRocket", "Accuracy", 10,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretRocket", "PowerUsage", 5000, "RF used per shot")
                .getInt());

        teleporter_turret = new TurretSetting(config.get("TurretTeleporter", "Range",
                20, "Turret range, in blocks").getInt(), config.get(
                "TurretTeleporter", "FireRateCooldown", 100,
                "Number of ticks between firings").getInt(), config.get(
                "TurretTeleporter", "Damage", 0, "Measured in half-hearts")
                .getInt(), config.get("TurretTeleporter", "Accuracy", 0,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretTeleporter", "PowerUsage", 15000, "RF used per shot")
                .getInt());

        laser_turret = new TurretSetting(config.get("TurretLaser", "Range", 25,
                "Turret range, in blocks").getInt(), config.get("TurretLaser",
                "FireRateCooldown", 10, "Number of ticks between firings")
                .getInt(), config.get("TurretLaser", "Damage", 2,
                "Measured in half-hearts").getInt(), config.get("TurretLaser",
                "Accuracy", 10, "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(),
                config.get("TurretLaser", "PowerUsage", 10000,
                        "RF used per shot").getInt());

        railgun_turret = new TurretSetting(config.get("TurretRailgun", "Range",
                30, "Turret range, in blocks").getInt(), config.get(
                "TurretRailgun", "FireRateCooldown", 100,
                "Number of ticks between firings").getInt(), config.get(
                "TurretRailgun", "Damage", 25, "Measured in half-hearts")
                .getInt(), config.get("TurretRailgun", "Accuracy", 0,
                "Value between 0 - 100, where 0 will miss 0% of the time over a distance of 10 blocks, subject to standard deviation.").getDouble(), config.get(
                "TurretRailgun", "PowerUsage", 25000, "RF used per shot")
                .getInt());

        rangeUpgradeBoost = config.get("upgrades", "range", 2,
                "Increases range in blocks linearly").getInt();
        fireRateUpgradeBoostPercentage = config.get("upgrades", "rateOfFire",
                0.1D, "It's a double for some reason, " + "reduces cooldown")
                .getDouble();
        accuracyUpgradeBoost = config.get("upgrades", "accuracy", 0.2D,
                "Increases accuracy linearly").getDouble();
        efficiencyUpgradeBoostPercentage = config.get("upgrades", "efficiency",
                0.08D, "Reduces power consumption " + "linearly").getDouble();

        solarPanelAddonGen = config.get("addons", "solar", 10,
                "Generates specified RF every tick").getInt();
        redstoneReactorAddonGen = config.get("addons", "redstone", 1550,
                "Generates RF from redstone in turret's " + "inventory")
                .getInt();
        damageAmpDmgBonus = config.get("addons", "damage", 2,
                "Increases damage linearly").getInt();

        turretWarningDistance = config.get("miscellaneous", "warningDistance",
                40).getInt();

        turretAlarmSound = config.get("miscellaneous",
                "Enable/Disable turret alarm sound", true).getBoolean();
        turretWarnMessage = config.get("miscellaneous",
                "Should turret warn message be displayed?", true).getBoolean();
        turretBreakable = config.get("miscellaneous",
                "Are turrets breakable by anyone?", false).getBoolean();

        turretDamageTrustedPlayers = config.get(
                "miscellaneous",
                "Can turrets damage their trusted players when they "
                        + "accidentally hit them?", true).getBoolean();

        recipes = config.get("miscellaneous",
                "Which recipes should we do? (auto,enderio, thermalexpansion,mekanism,vanilla)", "auto").getString();

        shouldDoThaumcraftIntegration = config.get("ModCompatability",
                "Should we enable items that integrate with Thaumcraft?", true).getBoolean();

        shouldDoComputerIntegration = config.get("ModCompatability",
                "Should we enable items that integrate with ComputerCraft/OpenComputers?", true).getBoolean();

        IGWNotification = config.get("ModCompatability",
                "Enable IGW Mod notification", true).getBoolean();

        potentiaToRFRatio = config.get("ModCompatability",
                "Potentia Addons' RF conversion ratio per 1 essentia", 500).getInt();

        potentiaAddonCapacity = config.get("ModCompatability",
                "How much potentia the addon can store", 20).getInt();

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

    public static int getTurretWarningDistance() {
        return turretWarningDistance;
    }

    public static TurretSetting getDisposableTurretSettings() {
        return disposable_turret;
    }

    public static TurretSetting getPotatoCannonTurretSettings() {
        return potato_cannon_turret;
    }

    public static TurretSetting getMachineGunTurretSettings() {
        return machine_gun_turret;
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

    public static int getDamageAmpDmgBonus() {
        return damageAmpDmgBonus;
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


	public static class TurretSetting {
        private final int range;
        private final int rof;
        private final int damage;
        private final double accuracy;
        private final int power_usage;

        public TurretSetting(int range, int rof, int damage, double accuracy,
                             int power_usage) {
            this.range = range;
            this.rof = rof;
            this.damage = damage;
            this.accuracy = accuracy;
            this.power_usage = power_usage;
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
    }

}