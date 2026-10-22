package omtteam.openmodularturrets.core.turret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * All turret types: the built-in ones, then those added by other mods in registration order. Registration is only
 * possible while the game starts; the registry is frozen before the turret blocks are created.
 */
public final class TurretTypes {
    private static final List<TurretType> TYPES = new ArrayList<>(TurretType.BUILT_IN);
    private static final List<TurretType> VIEW = Collections.unmodifiableList(TYPES);
    private static final Map<String, TurretType> BY_ID = new HashMap<>();
    private static boolean frozen;

    static {
        TYPES.forEach(type -> BY_ID.put(type.getId(), type));
    }

    private TurretTypes() {
    }

    /**
     * Adds a turret type.
     *
     * @throws IllegalStateException    if the registry is already frozen
     * @throws IllegalArgumentException if a type with the same id exists
     */
    public static synchronized TurretType register(TurretType type) {
        if (frozen) throw new IllegalStateException("Turret types are frozen, cannot register " + type);
        if (BY_ID.containsKey(type.getId())) throw new IllegalArgumentException("Duplicate turret type " + type);
        BY_ID.put(type.getId(), type);
        TYPES.add(type);
        return type;
    }

    /** Ends registration; called once all turret types are known. */
    public static synchronized void freeze() {
        frozen = true;
    }

    /** Whether {@link #freeze} was called, after which no types can be added. */
    public static boolean isFrozen() {
        return frozen;
    }

    /** All types: built-in first, then added ones in registration order. */
    public static List<TurretType> all() {
        return VIEW;
    }

    /** The type with the id {@code namespace:path}; a bare path is looked up in the built-in namespace. */
    public static Optional<TurretType> byId(String id) {
        String full = id.indexOf(':') >= 0 ? id : TurretType.BUILTIN_NAMESPACE + ":" + id;
        return Optional.ofNullable(BY_ID.get(full));
    }
}
