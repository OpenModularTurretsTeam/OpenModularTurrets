package omtteam.openmodularturrets.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.client.render.camo.CamoBlockColor;
import omtteam.openmodularturrets.CommonProxy;
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.client.gui.ConfigureGui;
import omtteam.openmodularturrets.client.gui.GeneratorGui;
import omtteam.openmodularturrets.client.gui.TurretBaseGui;
import omtteam.openmodularturrets.client.model.TurretBaseBakedModel;
import omtteam.openmodularturrets.client.network.ClientMessageHandlers;
import omtteam.openmodularturrets.client.render.ModTESRItems;
import omtteam.openmodularturrets.client.render.ProjectileRenderers;
import omtteam.openmodularturrets.client.render.TileEntityRenderers;
import omtteam.openmodularturrets.client.render.TurretItemRenderer;
import omtteam.openmodularturrets.client.render.TurretModels;
import omtteam.openmodularturrets.item.AddonMetaItem;
import omtteam.openmodularturrets.item.AmmoMetaItem;
import omtteam.openmodularturrets.item.IntermediateProductRegular;
import omtteam.openmodularturrets.item.IntermediateProductTiered;
import omtteam.openmodularturrets.item.UpgradeMetaItem;
import omtteam.openmodularturrets.item.UsableMetaItem;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private void registerItemModel(final Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName().toString().toLowerCase()));
    }

    private void registerItemModel(final Item item, int meta, final String variantName) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(item.getRegistryName().toString().toLowerCase()), variantName));
    }

    @SuppressWarnings("SameParameterValue")
    private void registerItemModel(final Item item, int meta, final String customName, boolean useCustomName) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + customName.toLowerCase()));
    }

    @SuppressWarnings("ConstantConditions")
    private void registerBlockModelAsItem(final Block block, int meta, final String blockName) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + blockName, "inventory"));
    }

    @SuppressWarnings("ConstantConditions")
    private void registerBlockModelAsItem(final Block block, int meta, final String blockName, String variantName) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + blockName, variantName));
    }

    @Override
    public void init() {
        super.init();
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new CamoBlockColor(), ModBlocks.turretBase);
    }

    @Override
    public void initModelLoaders() {
        StateMap ignoreRotation = new StateMap.Builder().ignore(LeverBlock.ROTATION).build();
        StateMap ignoreConcealed = new StateMap.Builder().ignore(BlockTurret.CONCEALED).build();
        ModelLoader.setCustomStateMapper(ModBlocks.leverBlock, ignoreRotation);
        for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            if (block instanceof BlockTurret) {
                ModelLoader.setCustomStateMapper(block, ignoreConcealed);
                Item item = Item.getItemFromBlock(block);
                ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(block.getRegistryName(), "inventory"));
                item.setTileEntityItemStackRenderer(new TurretItemRenderer(((BlockTurret) block).getCoreType()));
            }
        }
        ModelLoaderRegistry.registerLoader(new TurretBaseBakedModel.ModelLoader());
        ModelLoader.setCustomStateMapper(ModBlocks.turretBase, new TurretBaseBakedModel.Statemapper());

        for (int i = 0; i < 5; i++) {
            registerBlockModelAsItem(ModBlocks.turretBase, i, OMTNames.Blocks.turretBase + "_normal", "tier=" + (i + 1));
        }
        registerBlockModelAsItem(ModBlocks.generator, 0, OMTNames.Blocks.generator);
        registerItemModel(ModItems.upgradeEnergyMultiplier, 0);
        for (int i = 0; i < 15; i++) {
            registerItemModel(ModItems.intermediateProductTiered, i, IntermediateProductTiered.subNames[i], true);
        }
        for (int i = 0; i < 8; i++) {
            registerItemModel(ModItems.addonMetaItem, i, AddonMetaItem.subNames[i], true);
        }
        for (int i = 0; i < 5; i++) {
            registerItemModel(ModItems.upgradeMetaItem, i, UpgradeMetaItem.subNames[i], true);
        }
        for (int i = 0; i < 1; i++) {
            registerItemModel(ModItems.intermediateProductRegular, i, IntermediateProductRegular.subNames[i], true);
        }
        for (int i = 0; i < 5; i++) {
            registerItemModel(ModItems.ammoMetaItem, i, AmmoMetaItem.subNames[i], true);
        }
        for (int i = 0; i < 3; i++) {
            registerItemModel(ModItems.usableMetaItem, i, UsableMetaItem.subNames[i], true);
        }
        initTileRenderers();
    }

    @Override
    protected void initTileRenderers() {
        super.initTileRenderers();
        TileEntityRenderers.init();
        ModTESRItems.init();
    }

    @Override
    protected void initEntityRenderers() {
        super.initEntityRenderers();
        ProjectileRenderers.init();
    }

    @Override
    public void initHandlers() {
        super.initHandlers();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void loadClientPlugins(ASMDataTable asmData) {
        TurretModels.loadPlugins(asmData);
    }

    @Override
    public <M extends IMessage> IMessageHandler<M, IMessage> clientMessageHandler(Class<M> type) {
        return ClientMessageHandlers.get(type);
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id) {
            case 1:
                return new TurretBaseGui(player.inventory, (TurretBase) tileEntity);
            case 3:
                return tileEntity instanceof GeneratorTileEntity
                        ? new GeneratorGui(player.inventory, (GeneratorTileEntity) tileEntity) : null;
            case 20:
                return new ConfigureGui(player.inventory, (TurretBase) tileEntity);
            default:
                return null;
        }
    }
}