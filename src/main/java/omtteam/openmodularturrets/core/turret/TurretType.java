package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.target.TargetingPriority;
import omtteam.openmodularturrets.core.world.Face;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A turret type: its id, the minimum base tier it needs, the ammunition it consumes and its targeting rules. The 14
 * built-in types are constants here (ported from the 1.12.2 turret tile entities: {@code super(tier)},
 * {@code ammo = …}, {@code requiresAmmo()}); 1.20.1+ lets other mods add types with {@link #builder} through its
 * turret API, which registers them in {@link TurretTypes}.
 * <p>
 * Numeric stats (range, fire rate, power, upgrades) are configurable and come from
 * {@link omtteam.openmodularturrets.core.settings.LogicSettings}. Types are compared by identity; there is exactly one
 * instance per id.
 */
public final class TurretType {
    /** Namespace of the built-in turrets (the mod id). */
    public static final String BUILTIN_NAMESPACE = "openmodularturrets";
    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH = Pattern.compile("[a-z0-9_./-]+");
    private static final Set<Face> ALL_FACES = EnumSet.allOf(Face.class);

    public static final TurretType DISPOSABLE_TURRET = builtIn("disposable_turret", 1, AmmoType.DISPOSABLE)
            .priorities(5, 5, -5, 1, 10).build();
    public static final TurretType POTATO_CANNON_TURRET = builtIn("potato_cannon_turret", 1, AmmoType.POTATO)
            .priorities(-5, -5, -5, 1, 10).build();
    public static final TurretType CROSSBOW_TURRET = builtIn("crossbow_turret", 1, AmmoType.BOLT)
            .priorities(-5, -5, -5, 1, 10).build();
    /**
     * The melee turret's blades spin flat around the face normal, so it only mounts on top of or below a base (user
     * decision).
     */
    public static final TurretType MELEE_TURRET = builtIn("melee_turret", 1, null)
            .priorities(1, 1, 0, 10, 10).mountFaces(Face.UP, Face.DOWN).ignoresScattershot().build();
    public static final TurretType MACHINE_GUN_TURRET = builtIn("machine_gun_turret", 2, AmmoType.BULLET)
            .priorities(1, 10, -5, -5, 10).build();
    public static final TurretType INCENDIARY_TURRET = builtIn("incendiary_turret", 2, AmmoType.BLAZING_CLAY)
            .priorities(10, -5, -2, 10, 10).build();
    public static final TurretType GRENADE_TURRET = builtIn("grenade_turret", 3, AmmoType.GRENADE)
            .priorities(10, 10, 20, 1, 10).build();
    /**
     * 1.12.2 {@code RelativisticTurretTileEntity.isEntityValidTarget}: slowed entities are already "handled". The
     * 3.x relativistic turret; its block keeps the 3.x id until the rename to the slowing turret.
     */
    public static final TurretType SLOWING_TURRET = builtIn("slowing_turret", 3, null)
            .priorities(1, 1, 0, 10, 10).ignoresScattershot().skipsSlowedTargets().build();
    public static final TurretType ROCKET_TURRET = builtIn("rocket_turret", 4, AmmoType.ROCKET)
            .priorities(10, 10, 20, 1, 10).build();
    public static final TurretType TELEPORTER_TURRET = builtIn("teleporter_turret", 4, null)
            .priorities(1, -10, 0, 10, 10).ignoresScattershot().build();
    public static final TurretType ARC_TURRET = builtIn("arc_turret", 4, null)
            .priorities(5, 10, 2, -10, 10).build();
    public static final TurretType LASER_TURRET = builtIn("laser_turret", 5, null)
            .priorities(5, 10, 2, -10, 10).build();
    /**
     * The railgun ignores scattershot upgrades (user decision): it hardly scatters, so every extra ray hit and a
     * volley dealt a multiple of its already high damage.
     */
    public static final TurretType RAILGUN_TURRET = builtIn("railgun_turret", 5, AmmoType.FERRO_SLUG)
            .priorities(10, 10, 20, 40, 10).ignoresScattershot().build();
    public static final TurretType PLASMA_TURRET = builtIn("plasma_turret", 5, null)
            .priorities(10, 10, 20, 1, 10).build();

    /** The built-in types in their original order. */
    static final List<TurretType> BUILT_IN = Collections.unmodifiableList(Arrays.asList(DISPOSABLE_TURRET,
            POTATO_CANNON_TURRET, CROSSBOW_TURRET, MELEE_TURRET, MACHINE_GUN_TURRET, INCENDIARY_TURRET, GRENADE_TURRET,
            SLOWING_TURRET, ROCKET_TURRET, TELEPORTER_TURRET, ARC_TURRET, LASER_TURRET, RAILGUN_TURRET, PLASMA_TURRET));

    private final String namespace;
    private final String path;
    private final int requiredBaseTier;
    private final AmmoType ammo;
    private final int[] priorities;
    private final Set<Face> mountFaces;
    private final boolean usesScattershot;
    private final boolean skipsSlowedTargets;

    private TurretType(Builder builder) {
        this.namespace = builder.namespace;
        this.path = builder.path;
        this.requiredBaseTier = builder.requiredBaseTier;
        this.ammo = builder.ammo;
        this.priorities = builder.priorities.clone();
        this.mountFaces = Collections.unmodifiableSet(EnumSet.copyOf(builder.mountFaces));
        this.usesScattershot = builder.usesScattershot;
        this.skipsSlowedTargets = builder.skipsSlowedTargets;
    }

    /**
     * Starts a new turret type. The id {@code namespace:path} becomes the registry id of the turret block and item;
     * use your mod id as namespace.
     */
    public static Builder builder(String namespace, String path) {
        return new Builder(namespace, path);
    }

    private static Builder builtIn(String path, int tier, AmmoType ammo) {
        return builder(BUILTIN_NAMESPACE, path).requiredBaseTier(tier).ammo(ammo);
    }

    /** Namespace of the id, the mod id of the mod that adds the turret. */
    public String getNamespace() {
        return namespace;
    }

    /** Path of the id: registry path of the turret, also the key of a built-in turret's config section. */
    public String getPath() {
        return path;
    }

    /** {@code namespace:path}. */
    public String getId() {
        return namespace + ":" + path;
    }

    /** Whether this is one of the turrets of this mod (not added by another mod). */
    public boolean isBuiltIn() {
        return BUILTIN_NAMESPACE.equals(namespace);
    }

    /** A turret on a base of a lower tier breaks (1.12.2 {@code base.getTier() < turretTier}). */
    public int getRequiredBaseTier() {
        return requiredBaseTier;
    }

    /** The ammunition this turret fires; empty for energy-only turrets. Ignores {@code doTurretsNeedAmmo}. */
    public Optional<AmmoType> getAmmo() {
        return Optional.ofNullable(ammo);
    }

    /**
     * Default targeting weights in {@link TargetingPriority} order (1.12.2 {@code getDefaultPriorities}). Melee,
     * slowing (1.12.2 relativistic) and teleporter had only four values in 1.12.2 (a crash bug); their PLAYER weight
     * is 10 like every other turret (user decision).
     */
    public int[] getDefaultPriorities() {
        return priorities.clone();
    }

    /** Whether this turret may sit on the given face of its base. */
    public boolean canMountOn(Face face) {
        return mountFaces.contains(face);
    }

    /**
     * Whether scattershot upgrades affect this turret (extra shots, ammo, energy and scatter). Turrets that act once
     * per shot (melee, slowing, teleporter) ignore them, as the upgrades would only cost extra energy.
     */
    public boolean usesScattershot() {
        return usesScattershot;
    }

    /** Whether entities that already have slowness are not targeted. */
    public boolean skipsSlowedTargets() {
        return skipsSlowedTargets;
    }

    /** All registered types, see {@link TurretTypes#all()}. */
    public static List<TurretType> values() {
        return TurretTypes.all();
    }

    /** See {@link TurretTypes#byId}. */
    public static Optional<TurretType> byId(String id) {
        return TurretTypes.byId(id);
    }

    /** The id, which is also the registry name. */
    @Override
    public String toString() {
        return getId();
    }

    /** Builds a {@link TurretType}; every setting has a default except the id. */
    public static final class Builder {
        private final String namespace;
        private final String path;
        private int requiredBaseTier = 1;
        private AmmoType ammo;
        private int[] priorities = {1, 1, 1, 1, 10};
        private Set<Face> mountFaces = ALL_FACES;
        private boolean usesScattershot = true;
        private boolean skipsSlowedTargets;

        private Builder(String namespace, String path) {
            if (!NAMESPACE.matcher(Objects.requireNonNull(namespace)).matches()) {
                throw new IllegalArgumentException("Invalid turret namespace: " + namespace);
            }
            if (!PATH.matcher(Objects.requireNonNull(path)).matches()) {
                throw new IllegalArgumentException("Invalid turret path: " + path);
            }
            this.namespace = namespace;
            this.path = path;
        }

        /** Minimum base tier, 1–5 (default 1). */
        public Builder requiredBaseTier(int tier) {
            if (tier < 1 || tier > 5) throw new IllegalArgumentException("Base tier must be 1-5: " + tier);
            this.requiredBaseTier = tier;
            return this;
        }

        /** The ammunition the turret consumes per shot; {@code null} (the default) for energy-only turrets. */
        public Builder ammo(AmmoType ammo) {
            this.ammo = ammo;
            return this;
        }

        /** Default targeting weights, one per {@link TargetingPriority} in declaration order. */
        public Builder priorities(int... weights) {
            if (weights.length != TargetingPriority.values().length) {
                throw new IllegalArgumentException("Expected " + TargetingPriority.values().length
                        + " priority weights, got " + Arrays.toString(weights));
            }
            this.priorities = weights.clone();
            return this;
        }

        /** The base faces the turret may mount on (default: all six). */
        public Builder mountFaces(Face... faces) {
            if (faces.length == 0) throw new IllegalArgumentException("A turret needs at least one mount face");
            this.mountFaces = EnumSet.copyOf(Arrays.asList(faces));
            return this;
        }

        /** Scattershot upgrades do not affect the turret (no extra shots, ammo, energy or scatter). */
        public Builder ignoresScattershot() {
            this.usesScattershot = false;
            return this;
        }

        /** Entities that already have slowness are not targeted. */
        public Builder skipsSlowedTargets() {
            this.skipsSlowedTargets = true;
            return this;
        }

        public TurretType build() {
            return new TurretType(this);
        }
    }
}
