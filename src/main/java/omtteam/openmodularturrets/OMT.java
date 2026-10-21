package omtteam.openmodularturrets;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModCreativeTabs;
import omtteam.openmodularturrets.registry.ModEntities;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.registry.ModMenus;
import omtteam.openmodularturrets.registry.ModSounds;
import omtteam.openmodularturrets.registry.RegistrationStep;
import omtteam.openmodularturrets.registry.TurretRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Shared constants and start-up of Open Modular Turrets.
 * The loader entrypoints ({@code platform/<loader>/}) install the platform services, call {@link #init}, run the
 * {@link #registrationSteps()} and wire their loader's events.
 */
public final class OMT {
    public static final String MOD_ID = "openmodularturrets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private OMT() {
    }

    /**
     * Loads the turret definitions and the config and registers the packets. Both entrypoints call it first, before
     * any registration: the config needs the turrets' default stats, blocks need the turret types and their properties
     * come from the config, and NeoForge accepts packets only until its payload registration event.
     *
     * @param plugins other mods' turret plugins, as the loader found them
     */
    public static void init(List<TurretRegistry.Plugin> plugins) {
        LOGGER.info("Initializing Open Modular Turrets");
        TurretRegistry.load(plugins);
        OMTPackets.init();
        OMTConfig.init();
    }

    /**
     * The registration steps in dependency order, each with the registry it fills. Fabric runs them in this order;
     * NeoForge runs each in the {@code RegisterEvent} of its registry.
     */
    public static List<RegistrationStep> registrationSteps() {
        return List.of(
                new RegistrationStep(Registries.SOUND_EVENT, ModSounds::init),
                new RegistrationStep(Registries.BLOCK, ModBlocks::init),
                new RegistrationStep(Registries.ITEM, ModItems::init),
                new RegistrationStep(Registries.BLOCK_ENTITY_TYPE, ModBlockEntities::init),
                new RegistrationStep(Registries.ENTITY_TYPE, ModEntities::init),
                new RegistrationStep(Registries.CREATIVE_MODE_TAB, ModCreativeTabs::init),
                new RegistrationStep(Registries.MENU, ModMenus::init));
    }

    /**
     * Creates a {@link ResourceLocation} in the mod namespace.
     */
    public static ResourceLocation id(String path) {
        return VersionCompat.id(MOD_ID, path);
    }
}
