package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Called on the server for every ray a ray turret shoots (one per scattershot level), after it hit or missed. Used by
 * the game tests to check that rays are traced towards the target. A plain listener list, the same on every loader.
 */
@FunctionalInterface
public interface RayShotCallback {
    /**
     * @param start where the ray starts (in front of the turret)
     * @param end   the aim point: the target's eyes plus the turret's scatter
     * @param hit   the entity that was hit, or {@code null} if the ray missed
     */
    void onRayShot(TurretBlockEntity turret, Vec3 start, Vec3 end, @Nullable Entity hit);

    /** Adds a listener for the rest of the game (there is no removal). */
    static void register(RayShotCallback listener) {
        RayShotListeners.LISTENERS.add(listener);
    }

    /** Calls every listener; called by {@link RayWeapons} for each ray. */
    static void fire(TurretBlockEntity turret, Vec3 start, Vec3 end, @Nullable Entity hit) {
        for (RayShotCallback listener : RayShotListeners.LISTENERS) {
            listener.onRayShot(turret, start, end, hit);
        }
    }
}
