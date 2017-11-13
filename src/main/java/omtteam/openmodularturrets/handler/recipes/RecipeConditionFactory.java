package omtteam.openmodularturrets.handler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.handler.ConfigHandler;

import java.util.function.BooleanSupplier;

import static omtteam.omlib.compatibility.ModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.ModCompatibility.OpenComputersLoaded;

/**
 * Created by Keridos on 13/11/17.
 * This Class
 */
public class RecipeConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String mod = JsonUtils.getString(json, "mod");
        if (ModCompatibility.EnderIOLoaded && ConfigHandler.recipes.equals("enderio") && mod.equals("enderio")) {
            return () -> true;
        } else if (ModCompatibility.MekanismLoaded && ConfigHandler.recipes.equals("mekanism") && mod.equals("mekanism")) {
            return () -> true;
        } else if (ConfigHandler.recipes.equals("vanilla") && mod.equals("vanilla")) {
            return () -> true;
        } else if (ConfigHandler.recipes.equals("auto")) {
            if (ModCompatibility.EnderIOLoaded && mod.equals("enderio")) {
                return () -> true;
            } else if (ModCompatibility.MekanismLoaded && mod.equals("mekanism")) {
                return () -> true;
            } else if (mod.equals("vanilla") && !ModCompatibility.MekanismLoaded && !ModCompatibility.EnderIOLoaded) {
                return () -> true;
            }
        }
        if (mod.equals("computer") && (ComputerCraftLoaded || OpenComputersLoaded)) {
            return () -> true;
        }
        return () -> false;
    }
}
