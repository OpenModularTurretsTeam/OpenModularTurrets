package openmodularturrets.handler.recipes;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.items.ModItems;

class ComputerRecipeHandler {
    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.serialPortAddon, 1), " A ", "BAB", " A ", 'A',
                                                   net.minecraft.init.Items.quartz, 'B', ModItems.ioBus));
    }
}
