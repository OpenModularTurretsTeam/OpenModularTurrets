package openmodularturrets.handler.recipes;


import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.init.ModItems;

class ComputerRecipeHandler {
    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(RecipeHandler.serialPortAddon, " A ", "BAB", " A ", 'A',
                                                   net.minecraft.init.Items.quartz, 'B', RecipeHandler.ioBus));
    }
}
