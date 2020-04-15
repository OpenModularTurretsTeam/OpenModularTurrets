package omtteam.openmodularturrets.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

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
    public static SoundEvent plasmaLaunchSound;
    public static SoundEvent warningSound;
    public static SoundEvent amped;

    public static void init(IForgeRegistry<SoundEvent> registry) {
        turretDeploySound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "turret_deploy")).setRegistryName("openmodularturrets", "turret_deploy"), registry);
        turretRetractSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "turret_retract")).setRegistryName("openmodularturrets", "turret_retract"), registry);
        warningSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "warning")).setRegistryName("openmodularturrets", "warning"), registry);
        bulletHitSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "bullet_hit")).setRegistryName("openmodularturrets", "bullet_hit"), registry);
        railGunHitSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "rail_gun_hit")).setRegistryName("openmodularturrets", "rail_gun_hit"), registry);
        laserHitSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "laser_hit")).setRegistryName("openmodularturrets", "laser_hit"), registry);
        disposableLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "disposable")).setRegistryName("openmodularturrets", "disposable"), registry);
        grenadeLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "grenade")).setRegistryName("openmodularturrets", "grenade"), registry);
        machinegunLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "machine_gun")).setRegistryName("openmodularturrets", "machine_gun"), registry);
        incendiaryLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "incendiary")).setRegistryName("openmodularturrets", "incendiary"), registry);
        laserLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "laser")).setRegistryName("openmodularturrets", "laser"), registry);
        potatoLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "potato")).setRegistryName("openmodularturrets", "potato"), registry);
        railgunLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "rail_gun")).setRegistryName("openmodularturrets", "rail_gun"), registry);
        plasmaLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "plasma_launch")).setRegistryName("openmodularturrets", "plasma_launch"), registry);
        relativisticLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "relativistic")).setRegistryName("openmodularturrets", "relativistic"), registry);
        rocketLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "rocket")).setRegistryName("openmodularturrets", "rocket"), registry);
        teleportLaunchSound = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "teleport")).setRegistryName("openmodularturrets", "teleport"), registry);
        amped = registerSound(new SoundEvent(new ResourceLocation("openmodularturrets", "amped")).setRegistryName("openmodularturrets", "amped"), registry);
    }
}
