package openmodularturrets.handler.recipes;


import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

class ComputerRecipeHandler {
    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(RecipeHandler.addonSerialPort, " A ", "BAB", " A ", 'A',
                                                   Items.QUARTZ, 'B', RecipeHandler.ioBus));
    }
}
