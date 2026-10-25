package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import omtteam.openmodularturrets.OMT;

import java.util.ArrayList;
import java.util.List;

/**
 * All sounds (1.12.2 {@code ModSounds}); the files and {@code sounds.json} are copied from the 1.12.2 version.
 * <p>
 * Unlike the other registry classes, loading this class only creates the sounds; {@link #init} registers them. The
 * built-in turret definitions refer to them in {@code OMTStartup.init}, which on NeoForge runs before the sound registry
 * opens.
 */
public final class ModSounds {
    private static final List<SoundEvent> ALL = new ArrayList<>();

    public static final SoundEvent TURRET_DEPLOY = create("turret_deploy");
    public static final SoundEvent TURRET_RETRACT = create("turret_retract");
    public static final SoundEvent WARNING = create("warning");
    public static final SoundEvent BULLET_HIT = create("bullet_hit");
    public static final SoundEvent RAIL_GUN_HIT = create("rail_gun_hit");
    public static final SoundEvent LASER_HIT = create("laser_hit");
    public static final SoundEvent DISPOSABLE_LAUNCH = create("disposable");
    public static final SoundEvent GRENADE_LAUNCH = create("grenade");
    public static final SoundEvent MACHINE_GUN_LAUNCH = create("machine_gun");
    public static final SoundEvent INCENDIARY_LAUNCH = create("incendiary");
    public static final SoundEvent LASER_LAUNCH = create("laser");
    public static final SoundEvent POTATO_LAUNCH = create("potato");
    public static final SoundEvent RAIL_GUN_LAUNCH = create("rail_gun");
    public static final SoundEvent PLASMA_LAUNCH = create("plasma_launch");
    public static final SoundEvent SLOWING_LAUNCH = create("slowing");
    public static final SoundEvent ROCKET_LAUNCH = create("rocket");
    public static final SoundEvent TELEPORT_LAUNCH = create("teleport");
    public static final SoundEvent ARC_LAUNCH = create("arc");
    public static final SoundEvent AMPED = create("amped");
    public static final SoundEvent WINDUP = create("windup");

    private ModSounds() {
    }

    private static SoundEvent create(String name) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(OMT.id(name));
        ALL.add(sound);
        return sound;
    }

    /** Registers all sounds (the same instances the fields hold). */
    public static void init() {
        for (SoundEvent sound : ALL) {
            Registry.register(BuiltInRegistries.SOUND_EVENT, sound.getLocation(), sound);
        }
    }
}
