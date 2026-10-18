package omtteam.openmodularturrets.gametest;

import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.TurretRegistrar;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.RayProfile;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModSounds;

/**
 * A turret added the way another mod would add one ({@link OMTPlugin} entrypoint in the game test mod): a ray turret
 * on tier 2 bases that fires arrows as its own ammo type.
 */
public final class ApiTestTurrets implements OMTPlugin {
    public static final String NAMESPACE = "openmodularturrets-gametest";
    public static final AmmoType ARROW_AMMO = AmmoType.of(NAMESPACE, "arrow");
    public static final TurretType API_RAY_TURRET = TurretType.builder(NAMESPACE, "api_ray_turret")
            .requiredBaseTier(2).ammo(ARROW_AMMO).build();
    public static final TurretStats API_RAY_TURRET_STATS = TurretStats.builder(12, 10, 4, 100).accuracyDeviation(1)
            .build();

    @Override
    public void registerTurrets(TurretRegistrar registrar) {
        registrar.registerAmmo(AmmoDefinition.ofItem(ARROW_AMMO, () -> Items.ARROW));
        registrar.registerTurret(new TurretDefinition(API_RAY_TURRET,
                RayWeapon.builder(ModSounds.LASER_LAUNCH, RayProfile.LASER)
                        .color(RayWeapon.argb(0F, 1F, 0F, 0.5F))
                        .damageTypes(ModDamageTypes.LASER, null)
                        .build(),
                API_RAY_TURRET_STATS));
    }
}
