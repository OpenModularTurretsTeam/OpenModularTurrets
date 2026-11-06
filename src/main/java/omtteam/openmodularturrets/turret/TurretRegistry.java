package omtteam.openmodularturrets.turret;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.TurretRegistrar;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.api.weapon.SelfTargetingWeapon;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The turret and ammo definitions (1.20.1+ {@code registry.TurretRegistry}): the built-in ones, then those of the
 * {@link OMTPlugin}s of other mods, registered by {@code registry.TurretLoader} in OMT's preInit, before the blocks
 * are registered; the turret types are frozen afterwards.
 * Kept out of {@code registry}, which depends on all of the mod, so the code that only looks definitions up does not.
 */
public final class TurretRegistry {
    private static final List<Class<? extends TurretWeapon>> WEAPON_KINDS = Arrays.asList(ProjectileWeapon.class,
            RayWeapon.class, EffectWeapon.class, AreaWeapon.class, SelfTargetingWeapon.class);
    private static final Map<TurretType, TurretDefinition> TURRETS = new LinkedHashMap<>();
    private static final Map<AmmoType, AmmoDefinition> AMMO = new LinkedHashMap<>();
    private static final Map<TurretType, Block> BLOCKS = new HashMap<>();
    private static boolean loaded;

    private TurretRegistry() {
    }

    /**
     * Runs one registration (the built-in turrets, or one plugin) with a registrar that refuses calls once it
     * returned.
     *
     * @param owner        who registers, for the error messages
     * @param registration registers turrets and ammo
     */
    public static synchronized void register(String owner, Consumer<TurretRegistrar> registration) {
        if (loaded) throw new IllegalStateException(owner + ": the turret types are frozen already");
        Registrar registrar = new Registrar(owner);
        try {
            registration.accept(registrar);
        } finally {
            registrar.close();
        }
    }

    /**
     * Freezes the turret types after the last registration.
     *
     * @throws IllegalStateException if a turret type has no definition
     */
    public static synchronized void freeze() {
        TurretTypes.freeze();
        for (TurretType type : TurretTypes.all()) {
            if (!TURRETS.containsKey(type)) {
                throw new IllegalStateException("Turret type " + type + " has no turret definition");
            }
        }
        loaded = true;
    }

    /** @throws IllegalStateException if the type has no definition (or the registry is not loaded yet) */
    public static TurretDefinition get(TurretType type) {
        TurretDefinition definition = TURRETS.get(type);
        if (definition == null) throw new IllegalStateException("Unknown turret type " + type);
        return definition;
    }

    public static Optional<TurretDefinition> find(TurretType type) {
        return Optional.ofNullable(TURRETS.get(type));
    }

    /** All turret definitions in registration order, built-in first. */
    public static Collection<TurretDefinition> turrets() {
        return Collections.unmodifiableCollection(TURRETS.values());
    }

    public static Optional<AmmoDefinition> ammo(AmmoType type) {
        return Optional.ofNullable(AMMO.get(type));
    }

    /** Whether the stack is ammo of any turret. */
    public static boolean isAnyAmmo(ItemStack stack) {
        for (AmmoDefinition ammo : AMMO.values()) {
            if (ammo.matches(stack)) return true;
        }
        return false;
    }

    /** Records the block {@code registry.ModBlocks} registered for an enabled turret type. */
    public static synchronized void registerBlock(TurretType type, Block block) {
        BLOCKS.put(type, block);
    }

    /** The block of a turret type, null if the turret is disabled (or the blocks are not registered yet). */
    @Nullable
    public static Block block(TurretType type) {
        return BLOCKS.get(type);
    }

    /** Whether the registry is loaded (all plugins ran). */
    public static boolean isLoaded() {
        return loaded;
    }

    /** The registrar handed to one plugin; it refuses calls once the plugin returned. */
    private static final class Registrar implements TurretRegistrar {
        private final String owner;
        private boolean closed;

        private Registrar(String owner) {
            this.owner = owner;
        }

        private void close() {
            closed = true;
        }

        private void checkOpen() {
            if (closed) throw new IllegalStateException(owner + ": the registrar is only valid during registerTurrets");
        }

        @Override
        public void registerTurret(TurretDefinition turret) {
            checkOpen();
            TurretType type = turret.type();
            if (TURRETS.containsKey(type)) {
                throw new IllegalArgumentException(owner + ": turret " + type + " is already registered");
            }
            if (!WEAPON_KINDS.contains(turret.weapon().getClass())) {
                throw new IllegalArgumentException(owner + ": turret " + type + " uses an unknown weapon kind "
                        + turret.weapon().getClass().getName());
            }
            if (!type.isBuiltIn()) {
                TurretTypes.register(type);
            }
            TURRETS.put(type, turret);
        }

        @Override
        public void registerAmmo(AmmoDefinition ammo) {
            checkOpen();
            if (AMMO.containsKey(ammo.type())) {
                throw new IllegalArgumentException(owner + ": ammo " + ammo.type() + " already has a definition");
            }
            AMMO.put(ammo.type(), ammo);
        }
    }
}
