package omtteam.openmodularturrets.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The contents of {@code config/openmodularturrets.json}. Categories, names and defaults are ported from
 * 1.12.2 {@code handler/config/OMTConfig}. Fields are documented and range-checked with {@link Comment} and
 * {@link Range}, which {@link ConfigWriter} turns into comments in the file.
 */
public class ConfigData {
    @Comment("Turret behaviour and per-turret stats.")
    public Turrets turrets = new Turrets();
    @Comment("Turret base stats per tier.")
    public Bases bases = new Bases();
    @Comment("Targeting lists and access rules.")
    public General general = new General();
    @Comment("Energy generation of addons and the generator block.")
    public Miscellaneous miscellaneous = new Miscellaneous();

    /** Clamps annotated ranges, then validates and parses the lists. */
    void validate(ConfigValidator v) {
        ConfigRanges.validate(this, v, "");
        general.validate(v);
    }

    /**
     * 1.12.2 {@code doTurretsKillsDropMobLoot} and {@code doLootAddonsOverrideMobLootSetting} were removed: loot of turret
     * kills is controlled only by the fake drops addon (see {@code TurretBaseLogic#getLootingLevel}).
     */
    public static class General {
        @Comment("Operators may open every base and are never targeted.")
        public boolean canOPAccessOwnedBlocks = false;
        @Comment("Use an allow list for ammo (which items fit into the ammo slots of a base).")
        public boolean useWhitelistForAmmo = true;
        @Comment("Items usable as disposable ammo, as \"namespace:item[,damageBonus]\" or \"#namespace:tag[,damageBonus]\".\nThe damage bonus defaults to 1.")
        public List<String> ammoAllowList = new ArrayList<>(List.of("minecraft:cobblestone,2", "#minecraft:planks,1"));
        @Comment("Entities that are never targeted.")
        public List<String> mobBlackList = new ArrayList<>(List.of("minecraft:armor_stand"));
        @Comment("Entities that are always seen as hostile.")
        public List<String> mobForceList = new ArrayList<>(List.of("minecraft:creeper"));
        @Comment("Entities that are always seen as neutral, overriding their classification.\nMeant for passive mod entities that are wrongly classified as hostile.\nAn entity on both force lists is ignored on both.")
        public List<String> neutralForceList = new ArrayList<>(List.of("minecraft:cow"));

        // Parsed ammo allow list (id -> damage bonus), built by validate()
        private transient Map<ResourceLocation, Integer> ammoItemDamageBonus = Map.of();
        private transient Map<TagKey<Item>, Integer> ammoTagDamageBonus = Map.of();
        // Parsed entity lists, built by validate(). Entities on both force lists are in neither set.
        private transient Set<ResourceLocation> blockedEntities = Set.of();
        private transient Set<ResourceLocation> forcedHostileEntities = Set.of();
        private transient Set<ResourceLocation> forcedNeutralEntities = Set.of();

        /** Disposable ammo items and their damage bonus. */
        public Map<ResourceLocation, Integer> ammoItemDamageBonus() {
            return ammoItemDamageBonus;
        }

        /** Disposable ammo tags and their damage bonus, in config order. */
        public Map<TagKey<Item>, Integer> ammoTagDamageBonus() {
            return ammoTagDamageBonus;
        }

        /** Entities that are never targeted. */
        public Set<ResourceLocation> blockedEntities() {
            return blockedEntities;
        }

        /** Entities that are always seen as hostile. */
        public Set<ResourceLocation> forcedHostileEntities() {
            return forcedHostileEntities;
        }

        /** Entities that are always seen as neutral. */
        public Set<ResourceLocation> forcedNeutralEntities() {
            return forcedNeutralEntities;
        }

        void validate(ConfigValidator v) {
            ammoAllowList = v.ammoEntries("general.ammoAllowList", ammoAllowList);
            mobBlackList = v.ids("general.mobBlackList", mobBlackList);
            mobForceList = v.ids("general.mobForceList", mobForceList);
            neutralForceList = v.ids("general.neutralForceList", neutralForceList);
            parseAmmoList();

            Set<ResourceLocation> hostile = parseIds(mobForceList);
            Set<ResourceLocation> neutral = parseIds(neutralForceList);
            // An entity on both force lists is ignored on both. The entries stay in the file so the error
            // is logged again on every startup until the user fixes it.
            Set<ResourceLocation> conflicts = new HashSet<>(hostile);
            conflicts.retainAll(neutral);
            for (ResourceLocation id : conflicts) {
                OMT.LOGGER.error("OMTConfigError: {} is in both general.mobForceList and general.neutralForceList, "
                        + "ignoring it in both lists", id);
            }
            hostile.removeAll(conflicts);
            neutral.removeAll(conflicts);

            blockedEntities = Set.copyOf(parseIds(mobBlackList));
            forcedHostileEntities = Set.copyOf(hostile);
            forcedNeutralEntities = Set.copyOf(neutral);
        }

        /**
         * Entries were already checked by {@link ConfigValidator#ammoEntries}. The damage bonus defaults to 1
         * (1.12.2 {@code OMTConfig.parseDisposableAmmoList}); later duplicates win.
         */
        private void parseAmmoList() {
            Map<ResourceLocation, Integer> items = new LinkedHashMap<>();
            Map<TagKey<Item>, Integer> tags = new LinkedHashMap<>();
            for (String entry : ammoAllowList) {
                String[] parts = entry.split(",");
                String id = parts[0].trim();
                int bonus = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 1;
                if (id.startsWith("#")) {
                    tags.put(TagKey.create(Registries.ITEM, ResourceLocation.tryParse(id.substring(1))), bonus);
                } else {
                    items.put(ResourceLocation.tryParse(id), bonus);
                }
            }
            ammoItemDamageBonus = Map.copyOf(items);
            ammoTagDamageBonus = Collections.unmodifiableMap(tags);
        }

        /** Entries were already checked by {@link ConfigValidator#ids}. */
        private static Set<ResourceLocation> parseIds(List<String> ids) {
            Set<ResourceLocation> result = new HashSet<>();
            for (String id : ids) {
                result.add(ResourceLocation.tryParse(id));
            }
            return result;
        }
    }

    public static class Miscellaneous {
        @Comment("Energy per tick generated by the solar panel addon.") @Range(min = 0)
        public int solarPanelAddonGen = 10;
        @Comment("Energy per tick generated by the redstone reactor addon.") @Range(min = 0)
        public int redstoneReactorAddonGen = 1600;
        @Comment("Generator: energy generated per tick while burning fuel.") @Range(min = 1, max = 1000000)
        public int generatorEnergyPerTick = 40;
        @Comment("Generator: energy buffer. It pauses burning while the buffer is full.") @Range(min = 1, max = 2000000000)
        public int generatorCapacity = 50000;
        @Comment("Generator: energy per tick pushed into all neighbours together.") @Range(min = 1, max = 2000000000)
        public int generatorMaxOutput = 200;
    }

    public static class Turrets {
        @Comment("Rockets steer towards their target.")
        public boolean canRocketsHome = false;
        @Comment("Rocket explosions damage the Ender Dragon (its health is reduced directly).")
        public boolean canRocketsHurtEnderDragon = false;
        @Comment("Rocket explosions destroy blocks.")
        public boolean canRocketsDestroyBlocks = false;
        @Comment("Grenade explosions destroy blocks.")
        public boolean canGrenadesDestroyBlocks = false;
        @Comment("Railgun shots destroy the blocks they hit.")
        public boolean canRailgunDestroyBlocks = false;
        @Comment("Turrets can conceal themselves without the concealment addon.")
        public boolean canTurretsConcealWithoutAddon = false;
        @Comment("Turrets need ammo to fire.")
        public boolean doTurretsNeedAmmo = true;
        @Comment("If turrets can target players.")
        public boolean globalCanTargetPlayers = true;
        @Comment("If turrets can target neutral mobs (cow, sheep, ...).")
        public boolean globalCanTargetNeutrals = true;
        @Comment("If turrets can target hostile mobs.")
        public boolean globalCanTargetMobs = true;
        @Comment("Turrets warn players with an audible alarm when they enter the warning range.")
        public boolean turretAlarmSound = true;
        @Comment("Turrets are breakable with tools.")
        public boolean turretBreakable = false;
        @Comment("Trusted players take damage when accidentally hit.")
        public boolean turretDamageTrustedPlayers = false;
        @Comment("Volume of firing sounds.") @Range(min = 0, max = 10)
        public double turretSoundVolume = 4D;
        @Comment("Ticks between target searches.") @Range(min = 1)
        public int turretTargetSearchTicks = 10;
        @Comment("Turrets warn players with a message when they enter the warning range.")
        public boolean turretWarnMessage = true;
        @Comment("Warning distance in blocks, added to the turret range.") @Range(min = 0)
        public int turretWarningDistance = 5;

        @Comment("Disposable turret.")
        @SerializedName("disposable_turret")
        public TurretSetting disposableTurret = new TurretSetting(true, 10, 25, 2, 2, 50, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Potato cannon turret.")
        @SerializedName("potato_cannon_turret")
        public TurretSetting potatoCannonTurret = new TurretSetting(true, 15, 35, 3, 10, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Machine gun turret.")
        @SerializedName("machine_gun_turret")
        public TurretSetting machineGunTurret = new TurretSetting(true, 18, 8, 2, 100, 30, 4, 0.06D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Incendiary turret.")
        @SerializedName("incendiary_turret")
        public TurretSetting incendiaryTurret = new TurretSetting(true, 12, 25, 2, 250, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Grenade turret.")
        @SerializedName("grenade_turret")
        public TurretSetting grenadeTurret = new TurretSetting(true, 18, 40, 8, 3000, 30, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Relativistic turret.")
        @SerializedName("slowing_turret")
        public TurretSetting slowingTurret = new TurretSetting(true, 20, 25, 0, 5000, 0, 4, 0D, 0.1D, 2, 0, 0.08D, 0.1D, 0.05D);
        @Comment("Rocket turret.")
        @SerializedName("rocket_turret")
        public TurretSetting rocketTurret = new TurretSetting(true, 30, 30, 10, 5000, 10, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Teleporter turret.")
        @SerializedName("teleporter_turret")
        public TurretSetting teleporterTurret = new TurretSetting(true, 20, 100, 0, 15000, 0, 1, 0D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Laser turret.")
        @SerializedName("laser_turret")
        public TurretSetting laserTurret = new TurretSetting(true, 25, 10, 4, 8000, 10, 4, 0.06D, 0.125D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Railgun turret.")
        @SerializedName("railgun_turret")
        public TurretSetting railgunTurret = new TurretSetting(true, 30, 100, 25, 25000, 3, 2, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Plasma turret.")
        @SerializedName("plasma_turret")
        public TurretSetting plasmaTurret = new TurretSetting(true, 20, 60, 20, 50000, 8, 1, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Melee turret.")
        @SerializedName("melee_turret")
        public TurretSetting meleeTurret = new TurretSetting(true, 1, 20, 8, 2, 0, 4, 0.1D, 0.2D, 0, 0D, 0.08D, 0.1D, 0.05D);
        @Comment("Arc turret.")
        @SerializedName("arc_turret")
        public TurretSetting arcTurret = new TurretSetting(true, 15, 60, 14, 40000, 0, 1, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @Comment("Crossbow turret.")
        @SerializedName("crossbow_turret")
        public TurretSetting crossbowTurret = new TurretSetting(true, 15, 30, 5, 20, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);

        /** Settings of a turret type. */
        public TurretSetting get(TurretType type) {
            return switch (type) {
                case DISPOSABLE_TURRET -> disposableTurret;
                case POTATO_CANNON_TURRET -> potatoCannonTurret;
                case CROSSBOW_TURRET -> crossbowTurret;
                case MELEE_TURRET -> meleeTurret;
                case MACHINE_GUN_TURRET -> machineGunTurret;
                case INCENDIARY_TURRET -> incendiaryTurret;
                case GRENADE_TURRET -> grenadeTurret;
                case SLOWING_TURRET -> slowingTurret;
                case ROCKET_TURRET -> rocketTurret;
                case TELEPORTER_TURRET -> teleporterTurret;
                case ARC_TURRET -> arcTurret;
                case LASER_TURRET -> laserTurret;
                case RAILGUN_TURRET -> railgunTurret;
                case PLASMA_TURRET -> plasmaTurret;
            };
        }
    }

    public static class Bases {
        @Comment("Tier 1 turret base.")
        @SerializedName("tier_1")
        public BaseSetting tierOne = new BaseSetting(500, 50, 1, 5, 20);
        @Comment("Tier 2 turret base.")
        @SerializedName("tier_2")
        public BaseSetting tierTwo = new BaseSetting(50000, 100, 1, 10, 30);
        @Comment("Tier 3 turret base.")
        @SerializedName("tier_3")
        public BaseSetting tierThree = new BaseSetting(150000, 1000, 2, 15, 40);
        @Comment("Tier 4 turret base.")
        @SerializedName("tier_4")
        public BaseSetting tierFour = new BaseSetting(500000, 2500, 3, 20, 50);
        @Comment("Tier 5 turret base.")
        @SerializedName("tier_5")
        public BaseSetting tierFive = new BaseSetting(10000000, 50000, 4, 25, 60);
        @Comment("Allow camouflage on turret bases. Requires a restart.")
        public boolean allowBaseCamo = true;
        @Comment("Turret bases are breakable with tools (using the hardness and blast resistance below).")
        public boolean baseBreakable = false;

        /** @param tier 1–5 */
        public BaseSetting tier(int tier) {
            return switch (tier) {
                case 1 -> tierOne;
                case 2 -> tierTwo;
                case 3 -> tierThree;
                case 4 -> tierFour;
                case 5 -> tierFive;
                default -> throw new IllegalArgumentException("Invalid base tier: " + tier);
            };
        }
    }
}
