package omtteam.openmodularturrets.registry;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.TurretRegistrar;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;
import omtteam.openmodularturrets.item.AmmoItem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Turret and ammo definitions: the built-in ones ({@link BuiltinTurrets}) and those of other mods
 * ({@link OMTPlugin} entrypoints). Loaded once, first thing in the mod initializer: the config needs the turrets'
 * default stats and the blocks need the turret types.
 */
public final class TurretRegistry {
    private static final Map<TurretType, TurretDefinition> TURRETS = new LinkedHashMap<>();
    private static final Map<AmmoType, AmmoDefinition> AMMO = new LinkedHashMap<>();
    private static boolean loaded;

    private TurretRegistry() {
    }

    /** Registers the built-in turrets, then calls every {@link OMTPlugin}; ends with the types frozen. */
    public static synchronized void load() {
        if (loaded) return;
        loaded = true;
        BuiltinTurrets.register(new Registrar(OMT.MOD_ID));
        for (EntrypointContainer<OMTPlugin> container : FabricLoader.getInstance()
                .getEntrypointContainers(OMTPlugin.ENTRYPOINT, OMTPlugin.class)) {
            String modId = container.getProvider().getMetadata().getId();
            try {
                container.getEntrypoint().registerTurrets(new Registrar(modId));
            } catch (RuntimeException e) {
                throw new IllegalStateException("Mod " + modId + " failed to register its turrets", e);
            }
        }
        for (TurretType type : TurretTypes.all()) {
            if (!TURRETS.containsKey(type)) {
                throw new IllegalStateException("Turret type " + type + " was registered without a turret definition");
            }
        }
        TurretTypes.freeze();
        OMT.LOGGER.info("Registered {} turrets ({} added by other mods)", TURRETS.size(),
                TURRETS.keySet().stream().filter(type -> !type.isBuiltIn()).count());
    }

    /** @throws IllegalStateException if the type has no definition (it was not registered through the API) */
    public static TurretDefinition get(TurretType type) {
        TurretDefinition definition = TURRETS.get(type);
        if (definition == null) throw new IllegalStateException("Unknown turret type " + type);
        return definition;
    }

    public static Optional<TurretDefinition> find(TurretType type) {
        return Optional.ofNullable(TURRETS.get(type));
    }

    /** All turret definitions, built-in first, in registration order. */
    public static Collection<TurretDefinition> turrets() {
        return Collections.unmodifiableCollection(TURRETS.values());
    }

    public static Optional<AmmoDefinition> ammo(AmmoType type) {
        return Optional.ofNullable(AMMO.get(type));
    }

    /**
     * Whether a stack is ammo of a type: its {@link AmmoDefinition} decides; without one, {@link AmmoItem}s of the
     * type count.
     */
    public static boolean isAmmo(ItemStack stack, AmmoType type) {
        AmmoDefinition definition = AMMO.get(type);
        if (definition != null) return definition.matches(stack);
        return stack.getItem() instanceof AmmoItem item && item.getAmmoType().equals(type);
    }

    /** Whether a stack is ammo of any defined ammo type. */
    public static boolean isAnyAmmo(ItemStack stack) {
        for (AmmoDefinition definition : AMMO.values()) {
            if (definition.matches(stack)) return true;
        }
        return false;
    }

    private record Registrar(String modId) implements TurretRegistrar {
        @Override
        public void registerTurret(TurretDefinition turret) {
            synchronized (TurretRegistry.class) {
                TurretType type = turret.type();
                if (TurretTypes.isFrozen()) {
                    throw new IllegalStateException(modId + " registered turret " + type + " too late");
                }
                if (TURRETS.containsKey(type) || (!type.isBuiltIn() && TurretTypes.byId(type.getId()).isPresent())) {
                    throw new IllegalArgumentException(modId + " registered turret " + type + " twice");
                }
                if (type.isBuiltIn() && (!OMT.MOD_ID.equals(modId) || TurretTypes.byId(type.getId()).orElse(null) != type)) {
                    throw new IllegalArgumentException(modId + " may not register turrets in the "
                            + TurretType.BUILTIN_NAMESPACE + " namespace: " + type);
                }
                if (!type.isBuiltIn()) {
                    if (!type.getNamespace().equals(modId)) {
                        OMT.LOGGER.warn("{} registered turret {} outside its own namespace", modId, type);
                    }
                    TurretTypes.register(type);
                }
                TURRETS.put(type, turret);
            }
        }

        @Override
        public void registerAmmo(AmmoDefinition ammo) {
            synchronized (TurretRegistry.class) {
                if (TurretTypes.isFrozen()) {
                    throw new IllegalStateException(modId + " registered ammo " + ammo.type() + " too late");
                }
                if (AMMO.containsKey(ammo.type())) {
                    throw new IllegalArgumentException(modId + " registered ammo " + ammo.type() + " twice");
                }
                AMMO.put(ammo.type(), ammo);
            }
        }
    }
}
