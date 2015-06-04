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

        //RECIPES THAT DON'T CHANGE BASED ON MODS LOADED:
        //Tier 1 static recipes (Because they shouldn't use expensive mod items, only redstone, cobblestone and planks)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierOneItem, 1), new Object[]{"AAA", " B ",
                "AAA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
                "plankWood"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierOneItem, 1), new Object[]{"AAA", " BC",
                "AAA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
                "plankWood", 'C', net.minecraft.init.Items.redstone}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierOneItem, 1),
                new Object[]{" A ", "ABA", " A ", 'A',
                        net.minecraft.init.Items.redstone, 'B', "plankWood"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierOne, 1), new Object[]{"ABA", "BCB",
                "ABA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
                "plankWood", 'C', Items.sensorTierOneItem}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.disposableItemTurret, 1), new Object[]{" A ", "CBC",
                "CDC", 'A', Items.barrelTierOneItem, 'B',
                Items.chamberTierOneItem, 'C',
                net.minecraft.init.Blocks.cobblestone, 'D',
                net.minecraft.init.Items.redstone}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.potatoCannonTurret, 1), new Object[]{"CAC", "CAC",
                "DBD", 'A', Items.barrelTierOneItem, 'B',
                Items.chamberTierOneItem, 'C',
                net.minecraft.init.Blocks.cobblestone, 'D',
                net.minecraft.init.Items.redstone}));

        GameRegistry.addRecipe(new ItemStack(Blocks.leverBlock, 1),
                new Object[]{"AAA", "A  ", "A  ", 'A',
                        net.minecraft.init.Blocks.cobblestone});

        // Addons
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.solarPanelAddon, 1), new Object[]{"AAA", "CBC", "DED",
                'A', net.minecraft.init.Blocks.glass_pane, 'B',
                net.minecraft.init.Blocks.lapis_block, 'C',
                net.minecraft.init.Items.redstone, 'D', "ingotIron", 'E',
                Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.redstoneReactorAddon, 1), new Object[]{"CAC", "ABD",
                "CAC", 'A', "ingotIron", 'B',
                net.minecraft.init.Items.ender_eye, 'C',
                net.minecraft.init.Items.quartz, 'D', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.damageAmpAddon, 1), new Object[]{"AAA", "B B", "AAA",
                'A', "ingotIron", 'B', net.minecraft.init.Items.ender_pearl}));

        // Upgrades
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.efficiencyUpgradeItem, 1), new Object[]{" A ", "ABA",
                " C ", 'A', net.minecraft.init.Items.quartz, 'B',
                net.minecraft.init.Items.ender_eye, 'C', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.accuraccyUpgradeItem, 1), new Object[]{" A ", "ABA",
                " C ", 'A', net.minecraft.init.Items.quartz, 'B', "ingotGold",
                'C', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.fireRateUpgradeItem, 1), new Object[]{" A ", "ABA",
                " C ", 'A', net.minecraft.init.Items.quartz, 'B',
                net.minecraft.init.Items.blaze_powder, 'C', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.rangeUpgradeItem, 1), new Object[]{" A ", "ABA", " C ",
                'A', net.minecraft.init.Items.quartz, 'B',
                net.minecraft.init.Items.diamond, 'C', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.scattershotUpgradeItem, 1), new Object[]{" A ", "ABA",
                " C ", 'A', net.minecraft.init.Items.quartz, 'B',
                net.minecraft.init.Items.flint, 'C', Items.ioBus}));

        //Ammo
        GameRegistry.addRecipe(new ItemStack(Items.blazingClayCraftable, 32),
                new Object[]{"BCB", "CAC", "BCB", 'A',
                        net.minecraft.init.Items.blaze_powder, 'B',
                        net.minecraft.init.Items.clay_ball, 'C',
                        net.minecraft.init.Items.redstone});

    }
}
