package openmodularturrets.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;


/**
 * Created by Keridos on 24/11/16.
 * This Class
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
    public static SoundEvent turretWarnSound;


    public static void init(){
        turretDeploySound = new SoundEvent(new ResourceLocation("openmodularturrets", "turretdeploy"));
        turretRetractSound = new SoundEvent(new ResourceLocation("openmodularturrets", "turretretract"));
        bulletHitSound = new SoundEvent(new ResourceLocation("openmodularturrets", "bullethit"));
        railGunHitSound = new SoundEvent(new ResourceLocation("openmodularturrets", "railgunhit"));
        laserHitSound = new SoundEvent(new ResourceLocation("openmodularturrets", "laserhit"));
        disposableLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "disposable"));
        grenadeLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "grenade"));
        machinegunLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "machinegun"));
        incendiaryLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "incendiary"));
        laserLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "laser"));
        potatoLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "potato"));
        railgunLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "railgun"));
        relativisticLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "relativistic"));
        rocketLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "rocket"));
        teleportLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "teleport"));
        teleportLaunchSound = new SoundEvent(new ResourceLocation("openmodularturrets", "teleport"));
    }
}
