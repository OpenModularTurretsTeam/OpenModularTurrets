package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.client.render.models.ModelAbstractTurret;
import omtteam.openmodularturrets.client.render.models.ModelArcTurret;
import omtteam.openmodularturrets.client.render.models.ModelCrossbowTurret;
import omtteam.openmodularturrets.client.render.models.ModelDisposableItemTurret;
import omtteam.openmodularturrets.client.render.models.ModelGrenadeLauncher;
import omtteam.openmodularturrets.client.render.models.ModelIncendiaryTurret;
import omtteam.openmodularturrets.client.render.models.ModelLaserTurret;
import omtteam.openmodularturrets.client.render.models.ModelMachineGun;
import omtteam.openmodularturrets.client.render.models.ModelMeleeTurret;
import omtteam.openmodularturrets.client.render.models.ModelPlasmaLauncherTurret;
import omtteam.openmodularturrets.client.render.models.ModelPotatoCannonTurret;
import omtteam.openmodularturrets.client.render.models.ModelRailgun;
import omtteam.openmodularturrets.client.render.models.ModelRocketTurret;
import omtteam.openmodularturrets.client.render.models.ModelSlowingTurret;
import omtteam.openmodularturrets.client.render.models.ModelTeleporterTurret;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The 3.x model and texture of each turret type (1.12.2: one renderer class per turret); the melee turret's model
 * comes from 1.20.1+ (1.12.2 drew none).
 */
@SideOnly(Side.CLIENT)
final class TurretModels {
    private static final Map<TurretType, Entry> MODELS = new HashMap<>();

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

    /** The model of a turret type, null if it has none. */
    @Nullable
    static Entry get(TurretType type) {
        return MODELS.get(type);
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
