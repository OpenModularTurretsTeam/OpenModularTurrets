package omtteam.openmodularturrets.handler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import omtteam.omlib.compatibility.OMLibModCompatibility;
import omtteam.openmodularturrets.handler.config.OMTConfig;

import java.util.function.BooleanSupplier;

import static omtteam.omlib.compatibility.OMLibModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.OMLibModCompatibility.OpenComputersLoaded;

/**
 * Created by Keridos on 13/11/17.
 * This determines wether the given recipe should be loaded
 */

@SuppressWarnings("ALL")
public class RecipeConfigFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String mod = JsonUtils.getString(json, "mod");
        if (OMLibModCompatibility.EnderIOLoaded && OMTConfig.GENERAL.recipes.equals("enderio") && mod.equals("enderio")) {
            return () -> true;
        } else if (OMLibModCompatibility.MekanismLoaded && OMTConfig.GENERAL.recipes.equals("mekanism") && mod.equals("mekanism")) {
            return () -> true;
        } else if (OMTConfig.GENERAL.recipes.equals("vanilla") && mod.equals("vanilla")) {
            return () -> true;
        } else if (OMTConfig.GENERAL.recipes.equals("auto")) {
            if (OMLibModCompatibility.EnderIOLoaded && mod.equals("enderio")) {
                return () -> true;
            } else if (OMLibModCompatibility.MekanismLoaded && !OMLibModCompatibility.EnderIOLoaded && mod.equals("mekanism")) {
                return () -> true;
            } else if (mod.equals("vanilla") && !OMLibModCompatibility.MekanismLoaded && !OMLibModCompatibility.EnderIOLoaded) {
                return () -> true;
            }
        }
        if (mod.equals("computer") && (ComputerCraftLoaded || OpenComputersLoaded)) {
            return () -> true;
        }
        return () -> false;
    }
}
