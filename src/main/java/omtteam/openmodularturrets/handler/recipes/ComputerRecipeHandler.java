package omtteam.openmodularturrets.handler.recipes;


import net.minecraft.init.Items;
import omtteam.omlib.util.JSONRecipeBuilder;

class ComputerRecipeHandler {
    public static void init() {
        JSONRecipeBuilder.addShapedRecipe(OMTRecipeHandler.addonSerialPort, " A ", "BAB", " A ", 'A',
                Items.QUARTZ, 'B', OMTRecipeHandler.ioBus);
    }
}
