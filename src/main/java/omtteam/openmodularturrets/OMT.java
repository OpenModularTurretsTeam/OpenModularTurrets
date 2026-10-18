package omtteam.openmodularturrets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModCreativeTabs;
import omtteam.openmodularturrets.registry.ModEntities;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.registry.ModMenus;
import omtteam.openmodularturrets.registry.ModSounds;
import omtteam.openmodularturrets.registry.TurretRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint of Open Modular Turrets.
 */
public class OMT implements ModInitializer {
    public static final String MOD_ID = "openmodularturrets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Open Modular Turrets");
        TurretRegistry.load(); // first: the config needs the turrets' default stats, the blocks their types
        OMTPackets.init();
        OMTConfig.init(); // before blocks: block properties come from the config
        ModSounds.init();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModEntities.init();
        KillCounter.init();
        InteractiveBlock.registerCallback();
        ModCreativeTabs.init();
        ModMenus.init();
    }

    /**
     * Creates a {@link ResourceLocation} in the mod namespace.
     */
    public static ResourceLocation id(String path) {
        return VersionCompat.id(MOD_ID, path);
    }
}
