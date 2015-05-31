package openmodularturrets.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.items.Items;

public class RecipeHandler {

	@SuppressWarnings("RedundantArrayCreation")
	public static void initRecipes() {

		// Integration
		if (ModCompatibility.ThaumcraftLoaded
				&& ConfigHandler.shouldDoThaumcraftIntegration) {
			ThaumcraftRecipeHandler.init();
		}

		// Recipes
		int recipesDone = 0;
		if (ModCompatibility.ThermalExpansionLoaded
				&& ConfigHandler.shouldCreateThermalExpansionRecipes) {
			ThermalExpansionRecipeHandler.init();
			recipesDone++;
		}

		if (ModCompatibility.EnderIOLoaded
				&& ConfigHandler.shouldCreateEnderIORecipes) {
			EnderIORecipeHandler.init();
			recipesDone++;
		}

		if (ModCompatibility.MekanismLoaded
				&& ConfigHandler.shouldCreateMekanismRecipes) {
			MekanismRecipeHandler.init();
			recipesDone++;
		}

		// Only do vanilla if nothing else is loaded
		if (recipesDone == 0) {
			VanillaRecipeHandler.init();
		}
	}
}
