package omtteam.openmodularturrets.registry;

import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.turret.TurretDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The turret definitions (1.20.1+ {@code registry.TurretRegistry}, built-in turrets only until the turret API of
 * phase 5). Loaded in the init phase, after the sounds the weapons use are registered.
 */
public final class TurretRegistry {
    private static final Map<TurretType, TurretDefinition> TURRETS = new LinkedHashMap<>();

    private TurretRegistry() {
    }

    /** Registers the built-in turrets once. */
    public static synchronized void load() {
        if (!TURRETS.isEmpty()) return;
        for (TurretDefinition definition : BuiltinTurrets.definitions()) {
            TURRETS.put(definition.type(), definition);
        }
        for (TurretType type : TurretType.values()) {
            if (!TURRETS.containsKey(type)) {
                throw new IllegalStateException("Turret type " + type + " has no turret definition");
            }
        }
    }

    /** @throws IllegalStateException if the type has no definition (or the registry is not loaded yet) */
    public static TurretDefinition get(TurretType type) {
        TurretDefinition definition = TURRETS.get(type);
        if (definition == null) throw new IllegalStateException("Unknown turret type " + type);
        return definition;
    }

    /** All turret definitions in registration order. */
    public static Collection<TurretDefinition> turrets() {
        return Collections.unmodifiableCollection(TURRETS.values());
    }
}
