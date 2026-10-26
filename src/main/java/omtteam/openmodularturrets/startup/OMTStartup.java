package omtteam.openmodularturrets.startup;

import net.minecraft.core.registries.Registries;
import omtteam.omlib.block.InteractiveBlock;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.MemoryCardInteraction;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModCreativeTabs;
import omtteam.openmodularturrets.registry.ModEntities;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.registry.ModMenus;
import omtteam.openmodularturrets.registry.ModSounds;
import omtteam.omlib.registry.RegistrationStep;
import omtteam.openmodularturrets.registry.TurretRegistry;

import java.util.List;

/**
 * Shared start-up of Open Modular Turrets.
 * The loader entrypoints ({@code platform/<loader>/}) install the platform services, call {@link #init}, run the
 * {@link #registrationSteps()} and wire their loader's events.
 * Kept out of {@link OMT}, whose constants the whole mod uses, so nothing reaches back into the start-up.
 */
public final class OMTStartup {
    private OMTStartup() {
    }

    /**
     * Loads the turret definitions and the config and registers the packets. Both entrypoints call it first, before
     * any registration: the config needs the turrets' default stats, blocks need the turret types and their properties
     * come from the config, and NeoForge accepts packets only until its payload registration event.
     *
     * @param plugins other mods' turret plugins, as the loader found them
     */
    public static void init(List<TurretRegistry.Plugin> plugins) {
        OMT.LOGGER.info("Initializing Open Modular Turrets");
        TurretRegistry.load(plugins);
        OMTPackets.init();
        // before a block's own right-click, after the library's (ownership wand, claiming): a memory card in hand
        InteractiveBlock.addInterceptor((player, level, hand, hit) -> MemoryCardInteraction.use(player, level, hand, hit.getBlockPos()));
        OMTConfig.init(TurretRegistry.addonDefaultStats());
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
}
