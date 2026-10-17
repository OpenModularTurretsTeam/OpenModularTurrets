package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import omtteam.openmodularturrets.OMT;

/**
 * All sounds (1.12.2 {@code ModSounds}); the files and {@code sounds.json} are copied from the 1.12.2 version.
 */
public final class ModSounds {
    public static final SoundEvent TURRET_DEPLOY = register("turret_deploy");
    public static final SoundEvent TURRET_RETRACT = register("turret_retract");
    public static final SoundEvent WARNING = register("warning");
    public static final SoundEvent BULLET_HIT = register("bullet_hit");
    public static final SoundEvent RAIL_GUN_HIT = register("rail_gun_hit");
    public static final SoundEvent LASER_HIT = register("laser_hit");
    public static final SoundEvent DISPOSABLE_LAUNCH = register("disposable");
    public static final SoundEvent GRENADE_LAUNCH = register("grenade");
    public static final SoundEvent MACHINE_GUN_LAUNCH = register("machine_gun");
    public static final SoundEvent INCENDIARY_LAUNCH = register("incendiary");
    public static final SoundEvent LASER_LAUNCH = register("laser");
    public static final SoundEvent POTATO_LAUNCH = register("potato");
    public static final SoundEvent RAIL_GUN_LAUNCH = register("rail_gun");
    public static final SoundEvent PLASMA_LAUNCH = register("plasma_launch");
    public static final SoundEvent SLOWING_LAUNCH = register("slowing");
    public static final SoundEvent ROCKET_LAUNCH = register("rocket");
    public static final SoundEvent TELEPORT_LAUNCH = register("teleport");
    public static final SoundEvent ARC_LAUNCH = register("arc");
    public static final SoundEvent AMPED = register("amped");
    public static final SoundEvent WINDUP = register("windup");

    private ModSounds() {
    }

    private static SoundEvent register(String name) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, OMT.id(name),
                SoundEvent.createVariableRangeEvent(OMT.id(name)));
    }

    /** Loads the class, registering all sounds. */
    public static void init() {
    }
}
