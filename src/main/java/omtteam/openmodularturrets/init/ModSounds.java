package omtteam.openmodularturrets.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import static omtteam.omlib.util.InitHelper.registerSound;


/**
 * Created by Keridos on 24/11/16.
 * This Class handles all the sounds in the mod.
 */
public class ModSounds {
    public static SoundEvent turretDeploySound;
    public static SoundEvent turretRetractSound;
    public static SoundEvent bulletHitSound;
    public static SoundEvent railGunHitSound;
    public static SoundEvent laserHitSound;
    public static SoundEvent disposableLaunchSound;
    public static SoundEvent grenadeLaunchSound;
    public static SoundEvent machinegunLaunchSound;
    public static SoundEvent incendiaryLaunchSound;
    public static SoundEvent laserLaunchSound;
    public static SoundEvent potatoLaunchSound;
    public static SoundEvent railgunLaunchSound;
    public static SoundEvent relativisticLaunchSound;
    public static SoundEvent rocketLaunchSound;
    public static SoundEvent teleportLaunchSound;
    @SuppressWarnings("unused")
    public static SoundEvent turretWarnSound;
    public static SoundEvent amped;

    public static void init() {
        turretDeploySound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "turret_deploy")).setRegistryName("openmodularturrets", "turret_deploy"));
        turretRetractSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "turret_retract")).setRegistryName("openmodularturrets", "turret_retract"));
        bulletHitSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "bullet_hit")).setRegistryName("openmodularturrets", "bullet_hit"));
        railGunHitSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "rail_gun_hit")).setRegistryName("openmodularturrets", "rail_gun_hit"));
        laserHitSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "laser_hit")).setRegistryName("openmodularturrets", "laser_hit"));
        disposableLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "disposable")).setRegistryName("openmodularturrets", "disposable"));
        grenadeLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "grenade")).setRegistryName("openmodularturrets", "grenade"));
        machinegunLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "machine_gun")).setRegistryName("openmodularturrets", "machine_gun"));
        incendiaryLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "incendiary")).setRegistryName("openmodularturrets", "incendiary"));
        laserLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "laser")).setRegistryName("openmodularturrets", "laser"));
        potatoLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "potato")).setRegistryName("openmodularturrets", "potato"));
        railgunLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "rail_gun")).setRegistryName("openmodularturrets", "rail_gun"));
        relativisticLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "relativistic")).setRegistryName("openmodularturrets", "relativistic"));
        rocketLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "rocket")).setRegistryName("openmodularturrets", "rocket"));
        teleportLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "teleport")).setRegistryName("openmodularturrets", "teleport"));
        amped = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "amped")).setRegistryName("openmodularturrets", "amped"));
    }


}
