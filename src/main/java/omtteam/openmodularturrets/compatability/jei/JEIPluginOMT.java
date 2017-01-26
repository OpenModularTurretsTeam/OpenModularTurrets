package omtteam.openmodularturrets.compatability.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

/**
 * Created by Keridos on 03/12/16.
 * This Class
 */
@JEIPlugin
public class JEIPluginOMT extends BlankModPlugin {
    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerOMT());
    }
}
