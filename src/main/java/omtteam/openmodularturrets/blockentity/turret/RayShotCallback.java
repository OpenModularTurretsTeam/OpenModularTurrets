package omtteam.openmodularturrets.blockentity.turret;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Called on the server for every ray a ray turret shoots (one per scattershot level), after it hit or missed. Used by
 * the game tests to check that rays are traced towards the target.
 */
@FunctionalInterface
public interface RayShotCallback {
    Event<RayShotCallback> EVENT = EventFactory.createArrayBacked(RayShotCallback.class, listeners ->
            (turret, start, end, hit) -> {
                for (RayShotCallback listener : listeners) {
                    listener.onRayShot(turret, start, end, hit);
                }
            });

    /**
     * @param start where the ray starts (in front of the turret)
     * @param end   the aim point: the target's eyes plus the turret's scatter
     * @param hit   the entity that was hit, or {@code null} if the ray missed
     */
    void onRayShot(TurretBlockEntity turret, Vec3 start, Vec3 end, @Nullable Entity hit);
}
