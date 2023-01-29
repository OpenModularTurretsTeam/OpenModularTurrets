package openmodularturrets.handler.recipes;

import net.minecraftforge.oredict.ShapedOreRecipe;

import openmodularturrets.items.Items;
import cpw.mods.fml.common.registry.GameRegistry;

class ThaumcraftRecipeHandler {

    public static void init() {
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        Items.potentiaAddon,
                        " A ",
                        "ABA",
                        " A ",
                        'A',
                        "ingotThaumium",
                        'B',
                        Items.redstoneReactorAddon));
    }
}
