package openmodularturrets.handler.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.items.Items;

class ThaumcraftRecipeHandler {
    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(Items.potentiaAddon, " A ", "ABA", " A ", 'A', "ingotThaumium", 'B',
                                                   Items.redstoneReactorAddon));
    }
}
