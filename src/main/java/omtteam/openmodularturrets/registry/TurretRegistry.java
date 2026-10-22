package omtteam.openmodularturrets.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import omtteam.openmodularturrets.OpenModularTurrets;
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
import omtteam.openmodularturrets.config.AddonTurretConfig;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

/**
 * The turret and ammo definitions (1.20.1+ {@code registry.TurretRegistry}): the built-in ones, then those of the
 * {@link OMTPlugin}s of other mods. Loaded once in OMT's preInit, before the blocks are registered; the turret types
 * are frozen afterwards.
 */
public final class TurretRegistry {
    private static final List<Class<? extends TurretWeapon>> WEAPON_KINDS = Arrays.asList(ProjectileWeapon.class,
            RayWeapon.class, EffectWeapon.class, AreaWeapon.class, SelfTargetingWeapon.class);
    private static final Map<TurretType, TurretDefinition> TURRETS = new LinkedHashMap<>();
    private static final Map<AmmoType, AmmoDefinition> AMMO = new LinkedHashMap<>();
    private static boolean loaded;

    private TurretRegistry() {
    }

    /**
     * Registers the built-in turrets and ammo and those of all plugins, freezes the turret types and loads the config
     * of the turrets of other mods.
     *
     * @param asmData         the mod scan data, where the plugins are found by {@link OMTPlugin.Register}
     * @param configDirectory where the addon turret config is
     */
    public static synchronized void load(ASMDataTable asmData, File configDirectory) {
        if (loaded) return;
        Registrar builtIn = new Registrar("openmodularturrets");
        BuiltinTurrets.ammo().forEach(builtIn::registerAmmo);
        BuiltinTurrets.definitions().forEach(builtIn::registerTurret);
        builtIn.close();
        for (String className : pluginClasses(asmData)) {
            Registrar registrar = new Registrar(className);
            try {
                OMTPlugin plugin = Class.forName(className).asSubclass(OMTPlugin.class).newInstance();
                plugin.registerTurrets(registrar);
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException("Turret plugin " + className + " could not be loaded", e);
            } finally {
                registrar.close();
            }
            OpenModularTurrets.getLogger().info("Loaded turret plugin {}", className);
        }
        TurretTypes.freeze();
        for (TurretType type : TurretTypes.all()) {
            if (!TURRETS.containsKey(type)) {
                throw new IllegalStateException("Turret type " + type + " has no turret definition");
            }
        }
        List<TurretDefinition> addonTurrets = new ArrayList<>();
        for (TurretDefinition definition : TURRETS.values()) {
            if (!definition.type().isBuiltIn()) addonTurrets.add(definition);
        }
        AddonTurretConfig.load(configDirectory, addonTurrets);
        loaded = true;
    }

    /** Plugin classes sorted by name, so the order does not depend on the scan. */
    private static Collection<String> pluginClasses(ASMDataTable asmData) {
        TreeSet<String> classes = new TreeSet<>();
        for (ASMDataTable.ASMData data : asmData.getAll(OMTPlugin.Register.class.getName())) {
            classes.add(data.getClassName());
        }
        return classes;
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
