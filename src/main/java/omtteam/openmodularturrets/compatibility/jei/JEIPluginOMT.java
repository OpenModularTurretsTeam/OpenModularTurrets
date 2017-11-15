package omtteam.openmodularturrets.compatibility.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Created by Keridos on 03/12/16.
 * This Class
 */
@JEIPlugin
public class JEIPluginOMT implements IModPlugin {
    @Override
    @ParametersAreNonnullByDefault
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerOMT());
    }
}
