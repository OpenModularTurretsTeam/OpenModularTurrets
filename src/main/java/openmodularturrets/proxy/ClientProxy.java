package openmodularturrets.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.client.render.renderers.blockitem.TileEntityRenderers;
import openmodularturrets.client.render.renderers.projectiles.ProjectileRenderers;
import openmodularturrets.items.IntermediateProductTiered;
import openmodularturrets.init.ModItems;
import openmodularturrets.reference.Names;
import openmodularturrets.reference.Reference;

public class ClientProxy extends CommonProxy {

    private void registerItemModel(final Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName().toLowerCase()));
    }

    private void registerItemModel(final Item item, int meta, final String variantName) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(item.getRegistryName().toLowerCase()), variantName));
    }

    private void registerItemModel(final Item item, int meta, final String customName, boolean useCustomName) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + customName.toLowerCase()));
    }

    private void registerBlockModelAsItem(final Block block, int meta, final String blockName) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + blockName, "inventory"));
    }

    private void registerBlockModelAsItem(final Block block, int meta, final String blockName, String variantName) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + blockName, variantName));
    }

    @Override
    public void preInit() {
        super.preInit();
        for (int i = 0; i < 5; i++) {
            registerBlockModelAsItem(ModBlocks.turretBase, i, Names.Blocks.turretBase, "tier=" + (i + 1));
        }
        for (int i = 0; i < 15; i++) {
            registerItemModel(ModItems.intermediateProductTiered, i, IntermediateProductTiered.subNames[i], true);
        }
        registerItemModel(ModItems.ioBus, 0);
        registerItemModel(ModItems.grenadeThrowable, 0);
        registerItemModel(ModItems.bulletThrowable, 0);
    }

    @Override
    public void initRenderers() {
        super.initRenderers();
        TileEntityRenderers.init();
        ProjectileRenderers.init();

        //ToolTips tooltips = new ToolTips();
        //MinecraftForge.EVENT_BUS.register(tooltips);
    }

    @Override
    public void initHandlers() {
        super.initHandlers();
    }
}