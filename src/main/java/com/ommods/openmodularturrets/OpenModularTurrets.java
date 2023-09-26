package com.ommods.openmodularturrets;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("openmodularturrets")
public class OpenModularTurrets
{
    private static final Logger LOGGER = LogManager.getLogger();

    public OpenModularTurrets() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Hello from omt's preinit");
    }

}
