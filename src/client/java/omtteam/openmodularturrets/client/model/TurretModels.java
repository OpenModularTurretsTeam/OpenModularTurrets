package omtteam.openmodularturrets.client.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Model layers of the turrets.
 */
public final class TurretModels {
    private static final String HOLDER = "base/holder";
    private static final String HOLDER_WEAPON = "base/holder/weapon";

    private record Entry(ModelLayerLocation layer, Supplier<LayerDefinition> definition,
                         Function<ModelPart, TurretModel> factory) {
    }

    private static final Map<TurretType, Entry> ENTRIES = new EnumMap<>(TurretType.class);

    static {
        aimed(TurretType.MACHINE_GUN_TURRET, TurretModelDefinitions::machineGunTurret);
        aimed(TurretType.DISPOSABLE_TURRET, TurretModelDefinitions::disposableTurret);
        aimed(TurretType.POTATO_CANNON_TURRET, TurretModelDefinitions::potatoCannonTurret);
        aimed(TurretType.CROSSBOW_TURRET, TurretModelDefinitions::crossbowTurret);
        aimed(TurretType.INCENDIARY_TURRET, TurretModelDefinitions::incendiaryTurret);
        aimed(TurretType.GRENADE_TURRET, TurretModelDefinitions::grenadeTurret);
        aimed(TurretType.ROCKET_TURRET, TurretModelDefinitions::rocketTurret);
        aimed(TurretType.LASER_TURRET, TurretModelDefinitions::laserTurret);
        aimed(TurretType.PLASMA_TURRET, TurretModelDefinitions::plasmaTurret);
        // 1.12.2 ModelRailgun: yaw and pitch both on the weapon (no holder)
        add(TurretType.RAILGUN_TURRET, TurretModelDefinitions::railgunTurret, "base/weapon", "base/weapon");
        add(TurretType.ARC_TURRET, TurretModelDefinitions::arcTurret, null, null);
        add(TurretType.SLOWING_TURRET, TurretModelDefinitions::relativisticTurret, null, null);
        add(TurretType.TELEPORTER_TURRET, TurretModelDefinitions::teleporterTurret, null, null);
        put(TurretType.MELEE_TURRET, TurretModelDefinitions::meleeTurret, MeleeTurretModel::new);
    }

    private TurretModels() {
    }

    private static void aimed(TurretType type, Supplier<LayerDefinition> definition) {
        add(type, definition, HOLDER, HOLDER_WEAPON);
    }

    private static void add(TurretType type, Supplier<LayerDefinition> definition, @Nullable String yawPath,
                            @Nullable String pitchPath) {
        ResourceLocation texture = OMT.id("textures/block/turret/" + type.getId() + ".png");
        put(type, definition, part -> new PortedTurretModel(part, texture, yawPath, pitchPath));
    }

    private static void put(TurretType type, Supplier<LayerDefinition> definition,
                            Function<ModelPart, TurretModel> factory) {
        ENTRIES.put(type, new Entry(new ModelLayerLocation(OMT.id(type.getId()), "main"), definition, factory));
    }

    public static void registerLayers() {
        ENTRIES.values().forEach(entry -> EntityModelLayerRegistry.registerModelLayer(entry.layer(), entry.definition()::get));
    }

    /** Bakes all turret models. */
    public static Map<TurretType, TurretModel> bake(EntityModelSet models) {
        Map<TurretType, TurretModel> baked = new EnumMap<>(TurretType.class);
        ENTRIES.forEach((type, entry) -> baked.put(type, entry.factory().apply(models.bakeLayer(entry.layer()))));
        return baked;
    }

    public static boolean hasModel(TurretType type) {
        return ENTRIES.containsKey(type);
    }
}
