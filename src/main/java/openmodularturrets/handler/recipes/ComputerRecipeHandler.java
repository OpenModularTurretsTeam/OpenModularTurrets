package openmodularturrets.handler.recipes;


import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.init.ModItems;

class ComputerRecipeHandler {
    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(RecipeHandler.serialPortAddon, " A ", "BAB", " A ", 'A',
                                                   Items.QUARTZ, 'B', RecipeHandler.ioBus));
    }
}
