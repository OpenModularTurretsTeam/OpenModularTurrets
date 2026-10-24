package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Lets Forge's {@link Configuration} run outside FML: its constructor reads the Minecraft home from
 * {@link FMLInjectionData}, which only FML's launcher sets.
 */
final class ForgeConfigFiles {
    private ForgeConfigFiles() {
    }

    static void setMinecraftHome(File directory) throws ReflectiveOperationException {
        Field home = FMLInjectionData.class.getDeclaredField("minecraftHome");
        home.setAccessible(true);
        home.set(null, directory);
    }
}
