package openmodularturrets.handler.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.items.Items;

class ComputerRecipeHandler {

    public static void init() {
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.serialPortAddon, 1),
                        " A ",
                        "BAB",
                        " A ",
                        'A',
                        net.minecraft.init.Items.quartz,
                        'B',
                        Items.ioBus));
    }
}
