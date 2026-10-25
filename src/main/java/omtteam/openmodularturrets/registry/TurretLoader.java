package omtteam.openmodularturrets.registry;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.config.AddonTurretConfig;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.turret.TurretRegistry;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Fills the {@link TurretRegistry} once in OMT's preInit: the built-in turrets and ammo, then the {@link OMTPlugin}s
 * of other mods; then freezes the turret types and loads the config of the turrets of other mods.
 */
public final class TurretLoader {
    private TurretLoader() {
    }

    /**
     * @param asmData         the mod scan data, where the plugins are found by {@link OMTPlugin.Register}
     * @param configDirectory where the addon turret config is
     */
    public static synchronized void load(ASMDataTable asmData, File configDirectory) {
        if (TurretRegistry.isLoaded()) return;
        TurretRegistry.register("openmodularturrets", registrar -> {
            BuiltinTurrets.ammo().forEach(registrar::registerAmmo);
            BuiltinTurrets.definitions().forEach(registrar::registerTurret);
        });
        for (String className : pluginClasses(asmData)) {
            OMTPlugin plugin;
            try {
                plugin = Class.forName(className).asSubclass(OMTPlugin.class).newInstance();
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException("Turret plugin " + className + " could not be loaded", e);
            }
            TurretRegistry.register(className, plugin::registerTurrets);
            Reference.LOGGER.info("Loaded turret plugin {}", className);
        }
        TurretRegistry.freeze();
        Map<TurretType, TurretStats> addonTurrets = new LinkedHashMap<>();
        for (TurretDefinition definition : TurretRegistry.turrets()) {
            if (!definition.type().isBuiltIn()) addonTurrets.put(definition.type(), definition.defaultStats());
        }
        AddonTurretConfig.load(configDirectory, addonTurrets);
    }

    /** Plugin classes sorted by name, so the order does not depend on the scan. */
    private static Collection<String> pluginClasses(ASMDataTable asmData) {
        TreeSet<String> classes = new TreeSet<>();
        for (ASMDataTable.ASMData data : asmData.getAll(OMTPlugin.Register.class.getName())) {
            classes.add(data.getClassName());
        }
        return classes;
    }
}
