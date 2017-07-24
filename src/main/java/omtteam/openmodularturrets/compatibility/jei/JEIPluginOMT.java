package omtteam.openmodularturrets.compatibility.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Created by Keridos on 03/12/16.
 * This Class
 */
@JEIPlugin
public class JEIPluginOMT extends BlankModPlugin {
    @Override
    @ParametersAreNonnullByDefault
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerOMT());
    }
}
