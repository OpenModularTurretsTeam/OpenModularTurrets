package omtteam.openmodularturrets.core.turret;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Item ammunition used by turrets. The built-in types are ported from 1.12.2 {@code AmmoMetaItem} (meta 0–5) plus the
 * crossbow bolt and the vanilla potato; other mods create their own with {@link #of}. Energy-only turrets have no ammo
 * type. Which items count as an ammo type is decided in the Minecraft layer.
 * <p>
 * Ammo types are values: two instances with the same id are equal.
 */
public final class AmmoType {
    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH = Pattern.compile("[a-z0-9_./-]+");

    /** 1.12.2 {@code AmmoMetaItem} meta 0. */
    public static final AmmoType BLAZING_CLAY = builtIn("blazing_clay");
    /** 1.12.2 {@code AmmoMetaItem} meta 1. */
    public static final AmmoType BULLET = builtIn("bullet");
    /** 1.12.2 {@code AmmoMetaItem} meta 2. */
    public static final AmmoType FERRO_SLUG = builtIn("ferro_slug");
    /** 1.12.2 {@code AmmoMetaItem} meta 3. */
    public static final AmmoType GRENADE = builtIn("grenade");
    /** 1.12.2 {@code AmmoMetaItem} meta 4. */
    public static final AmmoType ROCKET = builtIn("rocket");
    /** Vanilla potato or poisonous potato. */
    public static final AmmoType POTATO = builtIn("potato");
    /** New in 4.0: crossbow bolt, ammo of the crossbow turret (3.x fired vanilla arrows). */
    public static final AmmoType BOLT = builtIn("bolt");
    /**
     * Any item on the config's ammo allow list (1.12.2 "fake disposable item", meta 5). The item's damage bonus is
     * reported with each draw.
     */
    public static final AmmoType DISPOSABLE = builtIn("disposable");

    private final String namespace;
    private final String path;

    private AmmoType(String namespace, String path) {
        if (!NAMESPACE.matcher(Objects.requireNonNull(namespace)).matches()) {
            throw new IllegalArgumentException("Invalid ammo namespace: " + namespace);
        }
        if (!PATH.matcher(Objects.requireNonNull(path)).matches()) {
            throw new IllegalArgumentException("Invalid ammo path: " + path);
        }
        this.namespace = namespace;
        this.path = path;
    }

    /** An ammo type with the id {@code namespace:path}; use your mod id as namespace. */
    public static AmmoType of(String namespace, String path) {
        return new AmmoType(namespace, path);
    }

    private static AmmoType builtIn(String path) {
        return new AmmoType(TurretType.BUILTIN_NAMESPACE, path);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    /** {@code namespace:path}. */
    public String getId() {
        return namespace + ":" + path;
    }

    /** The path, e.g. used in translation keys. */
    public String getSerializedName() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AmmoType)) return false;
        AmmoType other = (AmmoType) o;
        return namespace.equals(other.namespace) && path.equals(other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    @Override
    public String toString() {
        return getId();
    }
}
