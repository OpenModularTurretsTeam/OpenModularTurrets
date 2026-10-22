package omtteam.openmodularturrets.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;

/**
 * Created by Niel Verster on 11/29/2016.
 * Items drawn by a tile entity renderer; the turret items have their own item renderer (set in the client proxy).
 */
@SuppressWarnings({"DefaultFileTemplate", "deprecation"})
public class ModTESRItems {

    @SuppressWarnings("ConstantConditions")
    public static void init() {
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.leverBlock), 0, LeverTileEntity.class);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.leverBlock), 0, new ModelResourceLocation(ModBlocks.leverBlock.getRegistryName(), "inventory"));
    }
}
