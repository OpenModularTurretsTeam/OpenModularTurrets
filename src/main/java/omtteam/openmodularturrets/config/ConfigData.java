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
 * oldmod {@code handler/config/OMTConfig}.
 */
public class ConfigData {
    public Turrets turrets = new Turrets();
    public Bases bases = new Bases();
    public General general = new General();
    public Miscellaneous miscellaneous = new Miscellaneous();

    void validate(ConfigValidator v) {
        turrets.validate(v);
        bases.validate(v);
        general.validate(v);
        miscellaneous.validate(v);
    }

    /**
     * oldmod {@code doTurretsKillsDropMobLoot} and {@code doLootAddonsOverrideMobLootSetting} were removed: loot of turret
     * kills is controlled only by the fake drops addon (see {@code TurretBaseLogic#getLootingLevel}).
     */
    public static class General {
        /** Operators may open every base and are never targeted (OMLib {@code canOPAccessOwnedBlocks}). */
        public boolean canOPAccessOwnedBlocks = false;
        /** Use a whitelist for ammo (which items fit into the ammo slots of a base). */
        public boolean useWhitelistForAmmo = true;
        /**
         * Items usable as disposable ammo: {@code namespace:item[,damageBonus]} or {@code #namespace:tag[,damageBonus]}.
         * Old {@code minecraft:planks} (any meta) became the {@code #minecraft:planks} tag.
         */
        public List<String> ammoAllowList = new ArrayList<>(List.of("minecraft:cobblestone,2", "#minecraft:planks,1"));
        /** Entities that are never targeted. */
        public List<String> mobBlackList = new ArrayList<>(List.of("minecraft:armor_stand"));
        /** Entities that are always seen as hostile. */
        public List<String> mobForceList = new ArrayList<>(List.of("minecraft:creeper"));
        /**
         * Entities that are always seen as neutral, overriding their classification. Meant for passive mod entities
         * that are wrongly classified as hostile mobs. (oldmod added these to the block list by mistake.)
         */
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
         * (oldmod {@code OMTConfig.parseDisposableAmmoList}); later duplicates win.
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
        public int solarPanelAddonGen = 10;
        public int redstoneReactorAddonGen = 1600;
        /** Generator (new block): energy per tick while burning; one fuel burn tick = one generator tick. */
        public int generatorEnergyPerTick = 40;
        public int generatorCapacity = 50000;
        /** Energy per tick the generator pushes into all neighbours together. */
        public int generatorMaxOutput = 200;

        void validate(ConfigValidator v) {
            String p = "miscellaneous.";
            solarPanelAddonGen = v.min(p + "solarPanelAddonGen", solarPanelAddonGen, 0);
            redstoneReactorAddonGen = v.min(p + "redstoneReactorAddonGen", redstoneReactorAddonGen, 0);
            generatorEnergyPerTick = v.range(p + "generatorEnergyPerTick", generatorEnergyPerTick, 1, 1000000);
            generatorCapacity = v.range(p + "generatorCapacity", generatorCapacity, 1, 2000000000);
            generatorMaxOutput = v.range(p + "generatorMaxOutput", generatorMaxOutput, 1, 2000000000);
        }
    }

    public static class Turrets {
        public boolean canRocketsHome = false;
        public boolean canRocketsHurtEnderDragon = false;
        public boolean canRocketsDestroyBlocks = false;
        public boolean canGrenadesDestroyBlocks = false;
        public boolean canRailgunDestroyBlocks = false;
        public boolean canTurretsConcealWithoutAddon = false;
        public boolean doTurretsNeedAmmo = true;
        /** If turrets can target players. */
        public boolean globalCanTargetPlayers = true;
        /** If turrets can target neutrals (cow, sheep, …). */
        public boolean globalCanTargetNeutrals = true;
        /** If turrets can target hostile mobs. */
        public boolean globalCanTargetMobs = true;
        /** If turrets warn players with an audible alarm when entering their warning range. */
        public boolean turretAlarmSound = true;
        /** If turrets are breakable with tools. */
        public boolean turretBreakable = false;
        /** If trusted players take damage when accidentally hit. */
        public boolean turretDamageTrustedPlayers = false;
        /** Volume of firing sounds. */
        public double turretSoundVolume = 4D;
        /** Ticks between target searches. */
        public int turretTargetSearchTicks = 10;
        /** If turrets warn players with a message when entering their warning range. */
        public boolean turretWarnMessage = true;
        /** Warning distance added to the turret range. */
        public int turretWarningDistance = 5;

        @SerializedName("disposable_turret")
        public TurretSetting disposableTurret = new TurretSetting(true, 10, 25, 2, 2, 50, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("potato_cannon_turret")
        public TurretSetting potatoCannonTurret = new TurretSetting(true, 15, 35, 3, 10, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("machine_gun_turret")
        public TurretSetting machineGunTurret = new TurretSetting(true, 18, 8, 2, 100, 30, 4, 0.06D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("incendiary_turret")
        public TurretSetting incendiaryTurret = new TurretSetting(true, 12, 25, 2, 250, 30, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("grenade_turret")
        public TurretSetting grenadeTurret = new TurretSetting(true, 18, 40, 8, 3000, 30, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("relativistic_turret")
        public TurretSetting relativisticTurret = new TurretSetting(true, 20, 25, 0, 5000, 0, 4, 0D, 0.1D, 2, 0, 0.08D, 0.1D, 0.05D);
        @SerializedName("rocket_turret")
        public TurretSetting rocketTurret = new TurretSetting(true, 30, 30, 10, 5000, 10, 3, 0.08D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("teleporter_turret")
        public TurretSetting teleporterTurret = new TurretSetting(true, 20, 100, 0, 15000, 0, 1, 0D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("laser_turret")
        public TurretSetting laserTurret = new TurretSetting(true, 25, 10, 4, 8000, 10, 4, 0.06D, 0.125D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("railgun_turret")
        public TurretSetting railgunTurret = new TurretSetting(true, 30, 100, 25, 25000, 3, 2, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("plasma_turret")
        public TurretSetting plasmaTurret = new TurretSetting(true, 20, 60, 20, 50000, 8, 1, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        @SerializedName("melee_turret")
        public TurretSetting meleeTurret = new TurretSetting(true, 1, 20, 8, 2, 0, 4, 0.1D, 0.2D, 0, 0D, 0.08D, 0.1D, 0.05D);
        @SerializedName("arc_turret")
        public TurretSetting arcTurret = new TurretSetting(true, 15, 60, 14, 40000, 0, 1, 0.1D, 0.2D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
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
                case RELATIVISTIC_TURRET -> relativisticTurret;
                case ROCKET_TURRET -> rocketTurret;
                case TELEPORTER_TURRET -> teleporterTurret;
                case ARC_TURRET -> arcTurret;
                case LASER_TURRET -> laserTurret;
                case RAILGUN_TURRET -> railgunTurret;
                case PLASMA_TURRET -> plasmaTurret;
            };
        }

        void validate(ConfigValidator v) {
            turretSoundVolume = v.range("turrets.turretSoundVolume", turretSoundVolume, 0D, 10D);
            turretTargetSearchTicks = v.min("turrets.turretTargetSearchTicks", turretTargetSearchTicks, 1);
            turretWarningDistance = v.min("turrets.turretWarningDistance", turretWarningDistance, 0);
            disposableTurret.validate(v, "turrets.disposable_turret");
            potatoCannonTurret.validate(v, "turrets.potato_cannon_turret");
            machineGunTurret.validate(v, "turrets.machine_gun_turret");
            incendiaryTurret.validate(v, "turrets.incendiary_turret");
            grenadeTurret.validate(v, "turrets.grenade_turret");
            relativisticTurret.validate(v, "turrets.relativistic_turret");
            rocketTurret.validate(v, "turrets.rocket_turret");
            teleporterTurret.validate(v, "turrets.teleporter_turret");
            laserTurret.validate(v, "turrets.laser_turret");
            railgunTurret.validate(v, "turrets.railgun_turret");
            plasmaTurret.validate(v, "turrets.plasma_turret");
            meleeTurret.validate(v, "turrets.melee_turret");
            arcTurret.validate(v, "turrets.arc_turret");
            crossbowTurret.validate(v, "turrets.crossbow_turret");
        }
    }

    public static class Bases {
        @SerializedName("tier_1")
        public BaseSetting tierOne = new BaseSetting(500, 50, 1, 5, 20);
        @SerializedName("tier_2")
        public BaseSetting tierTwo = new BaseSetting(50000, 100, 1, 10, 30);
        @SerializedName("tier_3")
        public BaseSetting tierThree = new BaseSetting(150000, 1000, 2, 15, 40);
        @SerializedName("tier_4")
        public BaseSetting tierFour = new BaseSetting(500000, 2500, 3, 20, 50);
        @SerializedName("tier_5")
        public BaseSetting tierFive = new BaseSetting(10000000, 50000, 4, 25, 60);
        /** Requires a restart. */
        public boolean allowBaseCamo = true;
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

        void validate(ConfigValidator v) {
            for (int tier = 1; tier <= 5; tier++) {
                tier(tier).validate(v, "bases.tier_" + tier);
            }
        }
    }
}
