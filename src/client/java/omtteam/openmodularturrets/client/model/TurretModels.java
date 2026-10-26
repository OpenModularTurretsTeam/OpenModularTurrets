package omtteam.openmodularturrets.client.model;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.api.OMTClientPlugin;
import omtteam.openmodularturrets.client.api.TurretModelRegistrar;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Model layers of the turrets: the built-in ones and those of {@link OMTClientPlugin}s.
 */
public final class TurretModels {
    private static final String HOLDER = "base/holder";
    private static final String HOLDER_WEAPON = "base/holder/weapon";

    private record Entry(ModelLayerLocation layer, Supplier<LayerDefinition> definition,
                         Function<ModelPart, TurretModel> factory) {
    }

    private static final Map<TurretType, Entry> ENTRIES = new LinkedHashMap<>();

    private TurretModels() {
    }

    /** Another mod's {@link OMTClientPlugin}, as the loader found it. */
    public record Plugin(String modId, OMTClientPlugin plugin) {
    }

    /** Collects the built-in models, then those of the plugins in order. */
    public static void init(List<Plugin> plugins) {
        registerBuiltIn(TurretModels::put);
        for (Plugin plugin : plugins) {
            try {
                plugin.plugin().registerTurretModels(TurretModels::put);
            } catch (RuntimeException e) {
                throw new IllegalStateException("Mod " + plugin.modId() + " failed to register its turret models", e);
            }
        }
        for (TurretType type : TurretTypes.all()) {
            if (!ENTRIES.containsKey(type)) OMT.LOGGER.warn("Turret {} has no model and is invisible", type);
        }
    }

    /** Hands over the model layer of every turret model (after {@link #init}). */
    public static void registerLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar) {
        ENTRIES.values().forEach(entry -> registrar.accept(entry.layer(), entry.definition()));
    }

    private static void registerBuiltIn(TurretModelRegistrar registrar) {
        aimed(registrar, TurretType.MACHINE_GUN_TURRET, TurretModelDefinitions::machineGunTurret);
        aimed(registrar, TurretType.DISPOSABLE_TURRET, TurretModelDefinitions::disposableTurret);
        aimed(registrar, TurretType.POTATO_CANNON_TURRET, TurretModelDefinitions::potatoCannonTurret);
        aimed(registrar, TurretType.CROSSBOW_TURRET, TurretModelDefinitions::crossbowTurret);
        aimed(registrar, TurretType.INCENDIARY_TURRET, TurretModelDefinitions::incendiaryTurret);
        aimed(registrar, TurretType.GRENADE_TURRET, TurretModelDefinitions::grenadeTurret);
        aimed(registrar, TurretType.ROCKET_TURRET, TurretModelDefinitions::rocketTurret);
        aimed(registrar, TurretType.LASER_TURRET, TurretModelDefinitions::laserTurret);
        aimed(registrar, TurretType.PLASMA_TURRET, TurretModelDefinitions::plasmaTurret);
        // 1.12.2 ModelRailgun: yaw and pitch both on the weapon (no holder)
        registrar.register(TurretType.RAILGUN_TURRET, TurretModelDefinitions::railgunTurret,
                texture(TurretType.RAILGUN_TURRET), "base/weapon", "base/weapon");
        registrar.register(TurretType.ARC_TURRET, TurretModelDefinitions::arcTurret, texture(TurretType.ARC_TURRET),
                null, null);
        registrar.register(TurretType.SLOWING_TURRET, TurretModelDefinitions::slowingTurret,
                texture(TurretType.SLOWING_TURRET), null, null);
        registrar.register(TurretType.TELEPORTER_TURRET, TurretModelDefinitions::teleporterTurret,
                texture(TurretType.TELEPORTER_TURRET), null, null);
        registrar.register(TurretType.MELEE_TURRET, TurretModelDefinitions::meleeTurret, MeleeTurretModel::new);
    }

    private static void aimed(TurretModelRegistrar registrar, TurretType type, Supplier<LayerDefinition> definition) {
        registrar.register(type, definition, texture(type), HOLDER, HOLDER_WEAPON);
    }

    private static ResourceLocation texture(TurretType type) {
        return OMT.id("textures/block/turret/" + type.getPath() + ".png");
    }

    private static void put(TurretType type, Supplier<LayerDefinition> definition,
                            Function<ModelPart, TurretModel> factory) {
        if (ENTRIES.containsKey(type)) throw new IllegalArgumentException("Turret " + type + " already has a model");
        ModelLayerLocation layer = new ModelLayerLocation(VersionCompat.id(type.getNamespace(), type.getPath()), "main");
        ENTRIES.put(type, new Entry(layer, definition, factory));
    }

    /** Bakes all turret models. */
    public static Map<TurretType, TurretModel> bake(EntityModelSet models) {
        Map<TurretType, TurretModel> baked = new HashMap<>();
        ENTRIES.forEach((type, entry) -> baked.put(type, entry.factory().apply(models.bakeLayer(entry.layer()))));
        return Collections.unmodifiableMap(baked);
    }

    /** Bakes the model of one turret; {@code null} if it has none. */
    @Nullable
    public static TurretModel bake(EntityModelSet models, TurretType type) {
        Entry entry = ENTRIES.get(type);
        return entry == null ? null : entry.factory().apply(models.bakeLayer(entry.layer()));
    }

    public static boolean hasModel(TurretType type) {
        return ENTRIES.containsKey(type);
    }
}
