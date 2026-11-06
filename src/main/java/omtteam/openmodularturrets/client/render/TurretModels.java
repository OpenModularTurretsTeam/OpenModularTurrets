package omtteam.openmodularturrets.client.render;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.client.api.OMTClientPlugin;
import omtteam.openmodularturrets.client.api.TurretModelRegistrar;
import omtteam.openmodularturrets.client.model.ModelAbstractTurret;
import omtteam.openmodularturrets.client.model.ModelArcTurret;
import omtteam.openmodularturrets.client.model.ModelCrossbowTurret;
import omtteam.openmodularturrets.client.model.ModelDisposableItemTurret;
import omtteam.openmodularturrets.client.model.ModelGrenadeLauncher;
import omtteam.openmodularturrets.client.model.ModelIncendiaryTurret;
import omtteam.openmodularturrets.client.model.ModelLaserTurret;
import omtteam.openmodularturrets.client.model.ModelMachineGun;
import omtteam.openmodularturrets.client.model.ModelMeleeTurret;
import omtteam.openmodularturrets.client.model.ModelPlasmaLauncherTurret;
import omtteam.openmodularturrets.client.model.ModelPotatoCannonTurret;
import omtteam.openmodularturrets.client.model.ModelRailgun;
import omtteam.openmodularturrets.client.model.ModelRocketTurret;
import omtteam.openmodularturrets.client.model.ModelSlowingTurret;
import omtteam.openmodularturrets.client.model.ModelTeleporterTurret;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.turret.TurretRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * The model and texture of each turret type: the 3.x ones of the built-in turrets (1.12.2: one renderer class per
 * turret; the melee turret's model comes from 1.20.1+), and those {@link OMTClientPlugin}s of other mods register.
 * Turrets without a model are drawn with the machine gun's.
 */
@SideOnly(Side.CLIENT)
public final class TurretModels {
    private static final Map<TurretType, Entry> MODELS = new HashMap<>();
    private static Entry fallback;

    static {
        put(TurretType.DISPOSABLE_TURRET, ModelDisposableItemTurret::new, "dispose_item_turret");
        put(TurretType.POTATO_CANNON_TURRET, ModelPotatoCannonTurret::new, "potato_cannon_turret");
        put(TurretType.CROSSBOW_TURRET, ModelCrossbowTurret::new, "crossbow_turret");
        put(TurretType.MACHINE_GUN_TURRET, ModelMachineGun::new, "machine_gun_turret");
        put(TurretType.INCENDIARY_TURRET, ModelIncendiaryTurret::new, "incendiary_turret");
        put(TurretType.GRENADE_TURRET, ModelGrenadeLauncher::new, "grenade_turret");
        put(TurretType.SLOWING_TURRET, ModelSlowingTurret::new, "slowing_turret");
        put(TurretType.ROCKET_TURRET, ModelRocketTurret::new, "rocket_turret");
        put(TurretType.TELEPORTER_TURRET, ModelTeleporterTurret::new, "teleporter_turret");
        put(TurretType.ARC_TURRET, ModelArcTurret::new, "arc_turret");
        put(TurretType.LASER_TURRET, ModelLaserTurret::new, "laser_turret");
        put(TurretType.RAILGUN_TURRET, ModelRailgun::new, "rail_gun_turret");
        put(TurretType.PLASMA_TURRET, ModelPlasmaLauncherTurret::new, "plasma_turret");
        put(TurretType.MELEE_TURRET, ModelMeleeTurret::new, "melee_turret_retracted", "melee_turret_extended");
        fallback = MODELS.get(TurretType.MACHINE_GUN_TURRET);
    }

    private TurretModels() {
    }

    private static void put(TurretType type, Supplier<ModelAbstractTurret> model, String texture) {
        put(type, model, texture, texture);
    }

    /** @param activeTexture drawn while the turret is active */
    private static void put(TurretType type, Supplier<ModelAbstractTurret> model, String texture,
                            String activeTexture) {
        MODELS.put(type, new Entry(model.get(), texture(texture), texture(activeTexture)));
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(Reference.MOD_ID, "textures/blocks/" + name + ".png");
    }

    /** The model of a turret type, the fallback for turrets without one. */
    static Entry get(TurretType type) {
        Entry entry = MODELS.get(type);
        return entry != null ? entry : fallback;
    }

    /** Calls the {@link OMTClientPlugin}s of other mods (sorted by class name), once, after the turrets are known. */
    public static synchronized void loadPlugins(ASMDataTable asmData) {
        TreeSet<String> classes = new TreeSet<>();
        for (ASMDataTable.ASMData data : asmData.getAll(OMTClientPlugin.Register.class.getName())) {
            classes.add(data.getClassName());
        }
        TurretModelRegistrar registrar = (type, model, texture, activeTexture) -> {
            if (MODELS.containsKey(type)) throw new IllegalArgumentException("Turret " + type + " already has a model");
            if (!TurretRegistry.find(type).isPresent()) {
                throw new IllegalArgumentException("Turret " + type + " is not registered");
            }
            MODELS.put(type, new Entry(model.get(), texture, activeTexture != null ? activeTexture : texture));
        };
        for (String className : classes) {
            try {
                Class.forName(className).asSubclass(OMTClientPlugin.class).newInstance().registerModels(registrar);
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException("Turret client plugin " + className + " could not be loaded", e);
            }
        }
    }

    static final class Entry {
        final ModelAbstractTurret model;
        final ResourceLocation texture;
        final ResourceLocation activeTexture;

        Entry(ModelAbstractTurret model, ResourceLocation texture, ResourceLocation activeTexture) {
            this.model = model;
            this.texture = texture;
            this.activeTexture = activeTexture;
        }
    }
}
