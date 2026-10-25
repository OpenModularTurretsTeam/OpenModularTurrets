package omtteam.openmodularturrets.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.openmodularturrets.reference.Reference;

/**
 * Created by Keridos on 24/11/16.
 * The sounds of the mod. They exist from class loading on (turret weapons reference them before the registries open,
 * as on 1.20.1+) and are registered in the sound registry event.
 */
public final class ModSounds {
    public static final SoundEvent turretDeploySound = sound("turret_deploy");
    public static final SoundEvent turretRetractSound = sound("turret_retract");
    public static final SoundEvent warningSound = sound("warning");
    public static final SoundEvent bulletHitSound = sound("bullet_hit");
    public static final SoundEvent railGunHitSound = sound("rail_gun_hit");
    public static final SoundEvent laserHitSound = sound("laser_hit");
    public static final SoundEvent disposableLaunchSound = sound("disposable");
    public static final SoundEvent grenadeLaunchSound = sound("grenade");
    public static final SoundEvent machinegunLaunchSound = sound("machine_gun");
    public static final SoundEvent incendiaryLaunchSound = sound("incendiary");
    public static final SoundEvent laserLaunchSound = sound("laser");
    public static final SoundEvent potatoLaunchSound = sound("potato");
    public static final SoundEvent railgunLaunchSound = sound("rail_gun");
    public static final SoundEvent plasmaLaunchSound = sound("plasma_launch");
    public static final SoundEvent slowingLaunchSound = sound("slowing");
    public static final SoundEvent rocketLaunchSound = sound("rocket");
    public static final SoundEvent teleportLaunchSound = sound("teleport");
    public static final SoundEvent arcLaunchSound = sound("arc");
    public static final SoundEvent amped = sound("amped");

    private ModSounds() {
    }

    private static SoundEvent sound(String name) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        return new SoundEvent(id).setRegistryName(id);
    }

    public static void init(IForgeRegistry<SoundEvent> registry) {
        registry.register(turretDeploySound);
        registry.register(turretRetractSound);
        registry.register(warningSound);
        registry.register(bulletHitSound);
        registry.register(railGunHitSound);
        registry.register(laserHitSound);
        registry.register(disposableLaunchSound);
        registry.register(grenadeLaunchSound);
        registry.register(machinegunLaunchSound);
        registry.register(incendiaryLaunchSound);
        registry.register(laserLaunchSound);
        registry.register(potatoLaunchSound);
        registry.register(railgunLaunchSound);
        registry.register(plasmaLaunchSound);
        registry.register(slowingLaunchSound);
        registry.register(rocketLaunchSound);
        registry.register(teleportLaunchSound);
        registry.register(arcLaunchSound);
        registry.register(amped);
    }
}
