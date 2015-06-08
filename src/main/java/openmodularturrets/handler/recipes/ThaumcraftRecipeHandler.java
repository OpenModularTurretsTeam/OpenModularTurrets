package openmodularturrets.handler.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.items.Items;

public class ThaumcraftRecipeHandler {

	public static void init() {

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.potentiaAddon,
				new Object[] { " A ", "ABA", " A ", 'A', "ingotThaumium", 'B',
						Items.redstoneReactorAddon }));
	}
}
