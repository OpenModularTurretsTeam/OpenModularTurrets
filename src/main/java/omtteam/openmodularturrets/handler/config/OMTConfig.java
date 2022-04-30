package omtteam.openmodularturrets.handler.config;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.lists.AmmoList;
import omtteam.openmodularturrets.api.lists.MobBlockList;
import omtteam.openmodularturrets.api.lists.MobForceList;
import omtteam.openmodularturrets.reference.Reference;

import static omtteam.omlib.util.GeneralUtil.getItem;

@Config(modid = Reference.MOD_ID, category = "")
public class OMTConfig {

    @Config.Name("Turrets")
    public static ConfigTurrets TURRETS = new ConfigTurrets();

    @Config.Name("Bases")
    public static ConfigBases BASES = new ConfigBases();

    @Config.Name("General")
    public static ConfigGeneral GENERAL = new ConfigGeneral();

    @Config.Name("Miscellaneous")
    public static ConfigMisc MISCELLANEOUS = new ConfigMisc();

    public static void parseLists() {
        parseDisposableAmmoList();
        parseModBlockList();
        parseMobForceList();
        parseNeutralForceList();
    }

    private static void parseDisposableAmmoList() {
        try {
            AmmoList.clear();
            for (String itemListEntry : GENERAL.stringAmmoAllowList) {
                int damage = itemListEntry.split(",").length > 1 ? Integer.parseInt(itemListEntry.split(",")[1]) : 1;
                String[] item = itemListEntry.split(":");
                if (item.length == 3) {
                    AmmoList.add(new ItemStack(getItem(item[0], item[1]), 1, Integer.parseInt(item[2])), damage);
                } else {
                    AmmoList.add(new ItemStack(getItem(item[0], item[1]), 2), damage);
                }
            }
        } catch (Exception e) {
            OpenModularTurrets.getLogger().error("error while parsing disp. ammo list config!");
            e.printStackTrace();
        }
    }

    private static void parseModBlockList() {
        try {
            if (GENERAL.stringMobBlackList.length == 0) return;
            MobBlockList.getInstance().clear();
            for (String itemListEntry : GENERAL.stringMobBlackList) {
                MobBlockList.getInstance().add(new ResourceLocation(itemListEntry));
            }
        } catch (Exception e) {
            OpenModularTurrets.getLogger().error("error while parsing mob block list config!");
            e.printStackTrace();
        }
    }

    private static void parseMobForceList() {
        try {
            if (GENERAL.stringMobForceList.length == 0) return;
            MobForceList.getInstance().clear();
            for (String itemListEntry : GENERAL.stringMobForceList) {
                MobForceList.getInstance().add(new ResourceLocation(itemListEntry));
            }
        } catch (Exception e) {
            OpenModularTurrets.getLogger().error("error while parsing mob force list config!");
            e.printStackTrace();
        }
    }

    private static void parseNeutralForceList() {
        try {
            if (GENERAL.stringNeutralForceList.length == 0) return;
            MobBlockList.getInstance().clear();
            for (String itemListEntry : GENERAL.stringNeutralForceList) {
                MobBlockList.getInstance().add(new ResourceLocation(itemListEntry));
            }
        } catch (Exception e) {
            OpenModularTurrets.getLogger().error("error while parsing neutral force list config!");
            e.printStackTrace();
        }
    }

    public static class ConfigGeneral {
        @Config.RequiresMcRestart
        @Config.Comment("Which recipes to use. Valid values: auto, enderio, mekanism, vanilla")
        public String recipes = "auto";

        @Config.Comment("If turrets kills drop loot")
        public boolean doTurretsKillsDropMobLoot = true;

        @Config.Comment("If loot is disabled, do loot addons enable loot anyway?")
        public boolean doLootAddonsOverrideMobLootSetting = true;

        @Config.Comment("Use a whitelist for ammo (which items fit into ammo slots of base)?")
        public boolean useWhitelistForAmmo = true;

        @Config.Comment("If enabled: Which items (modid:item[:meta]) can be used as disposable ammo. Value behind ',' is damage bonus.")
        public String[] stringAmmoAllowList = new String[]{"minecraft:cobblestone,2", "minecraft:planks,1"};

        @Config.Comment("Which entities should not be targeted by turrets?")
        public String[] stringMobBlackList = new String[]{"minecraft:armor_stand"};

        @Config.Comment("Which entities should always be seen as hostile by turrets?")
        public String[] stringMobForceList = new String[]{"minecraft:creeper"};

        @Config.Comment("Which entities should always be seen as neutral by turrets?")
        public String[] stringNeutralForceList = new String[]{"minecraft:cow"};
    }

    public static class ConfigMisc {
        @Config.RangeInt(min = 1, max = 2000000000)
        public int expanderPowerTierOneCapacity = 2500;
        @Config.RangeInt(min = 1, max = 2000000000)
        public int expanderPowerTierTwoCapacity = 25000;
        @Config.RangeInt(min = 1, max = 2000000000)
        public int expanderPowerTierThreeCapacity = 75000;
        @Config.RangeInt(min = 1, max = 2000000000)
        public int expanderPowerTierFourCapacity = 250000;
        @Config.RangeInt(min = 1, max = 2000000000)
        public int expanderPowerTierFiveCapacity = 5000000;
        @Config.RangeInt(min = 0)
        public int solarPanelAddonGen = 10;
        @Config.RangeInt(min = 0)
        public int redstoneReactorAddonGen = 1600;
    }

    public static class ConfigTurrets {
        public boolean canRocketsHome = false;
        public boolean canRocketsHurtEnderDragon = false;
        public boolean canRocketsDestroyBlocks = false;
        public boolean canGrenadesDestroyBlocks = false;
        public boolean canRailgunDestroyBlocks = false;
        public boolean canTurretsConcealWithoutAddon = false;
        public boolean doTurretsNeedAmmo = true;

        @Config.Comment("If turrets can target players")
        public boolean globalCanTargetPlayers = true;

        @Config.Comment("If turrets can target neutrals (cow, sheep etc.)")
        public boolean globalCanTargetNeutrals = true;

        @Config.Comment("If turrets can target hostile mobs")
        public boolean globalCanTargetMobs = true;

        @Config.Comment("If turrets should warn players with audible alarm if entering their warning range")
        public boolean turretAlarmSound = true;

        @Config.Comment("If turrets should be breakable with tools")
        public boolean turretBreakable = false;

        @Config.Comment("If trusted players get damage when accidentally hit")
        public boolean turretDamageTrustedPlayers = false;

        @Config.RangeDouble(min = 0D, max = 10D)
        @Config.Comment("Volume of the sound of firing turrets")
        public double turretSoundVolume = 4D;

        @Config.Comment("How many ticks should a turret wait before searching for targets again")
        @Config.RangeInt(min = 1)
        public int turretTargetSearchTicks = 10;

        @Config.Comment("If turrets should warn players with a message if entering their warning range")
        public boolean turretWarnMessage = true;

        @Config.Comment("The warning distance that is added to the range of the turret")
        public int turretWarningDistance = 5;

        @Config.Name("Disposable Turret")
        public TurretSetting disposable_turret = new TurretSetting(true, 10, 25, 2, 2, 50, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Potato Cannon Turret")
        public TurretSetting potato_cannon_turret = new TurretSetting(true, 15, 35, 3, 10, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Machine Gun Turret")
        public TurretSetting machine_gun_turret = new TurretSetting(true, 18, 8, 2, 100, 30, 4, 0.06D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Incendiary Turret")
        public TurretSetting incendiary_turret = new TurretSetting(true, 12, 25, 2, 250, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Grenade Turret")
        public TurretSetting grenade_turret = new TurretSetting(true, 18, 40, 8, 3000, 30, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Relativistic Turret")
        public TurretSetting relativistic_turret = new TurretSetting(true, 20, 25, 0, 5000, 0, 4, 0D, 0.1D, 2, 0, 0.08D, 0.1D, 0.05D);
        @Config.Name("Rocket Turret")
        public TurretSetting rocket_turret = new TurretSetting(true, 30, 30, 10, 5000, 10, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Teleporter Turret")
        public TurretSetting teleporter_turret = new TurretSetting(true, 20, 100, 0, 15000, 0, 1, 0D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Laser Turret")
        public TurretSetting laser_turret = new TurretSetting(true, 25, 10, 4, 8000, 10, 4, 0.06D, 0.125D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Railgun Turret")
        public TurretSetting railgun_turret = new TurretSetting(true, 30, 100, 25, 25000, 3, 2, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Plasma Turret")
        public TurretSetting plasma_turret = new TurretSetting(true, 20, 60, 20, 50000, 8, 1, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Melee Turret")
        public TurretSetting melee_turret = new TurretSetting(true, 1, 20, 8, 2, 0, 4, 0.1D, 0.2D, 0, 0D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Arc Turret")
        public TurretSetting arc_turret = new TurretSetting(true, 15, 60, 14, 40000, 0, 1, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Config.Name("Potato Cannon Turret")
        public TurretSetting crossbow_turret = new TurretSetting(true, 15, 30, 5, 20, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
    }

    public static class ConfigBases {
        @Config.Name("Tier 1")
        public BaseSetting baseTierOne = new BaseSetting(500, 50, 1, 5, 20);
        @Config.Name("Tier 2")
        public BaseSetting baseTierTwo = new BaseSetting(50000, 100, 1, 10, 30);
        @Config.Name("Tier 3")
        public BaseSetting baseTierThree = new BaseSetting(150000, 1000, 2, 15, 40);
        @Config.Name("Tier 4")
        public BaseSetting baseTierFour = new BaseSetting(500000, 2500, 3, 20, 50);
        @Config.Name("Tier 5")
        public BaseSetting baseTierFive = new BaseSetting(10000000, 50000, 4, 25, 60);
        @Config.RequiresMcRestart
        public boolean allowBaseCamo = true;
        public boolean baseBreakable = false;
    }
}