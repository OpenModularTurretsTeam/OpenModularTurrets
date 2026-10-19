package omtteam.openmodularturrets.blockentity.turret;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The listeners of {@link RayShotCallback} (an interface cannot hold a private field).
 */
final class RayShotListeners {
    static final List<RayShotCallback> LISTENERS = new CopyOnWriteArrayList<>();

    private RayShotListeners() {
    }
}
