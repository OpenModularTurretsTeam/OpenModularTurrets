package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.util.IHasItemBlock;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.UsableMetaItem;
import omtteam.openmodularturrets.items.blocks.ItemBlockTurretBase;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockTurretBase extends BlockAbstractTileEntity implements IHasItemBlock {
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 1, 5);

    //IBlockState camoBlockState = null;

    public BlockTurretBase() {
        super(Material.ROCK);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (!ConfigHandler.turretBreakable) {
            this.setBlockUnbreakable();
        }
        setDefaultState(this.blockState.getBaseState().withProperty(TIER, 1));
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName(OMTNames.Blocks.turretBase);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.turretBase);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockTurretBase(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int MaxCharge;
        int MaxIO;
        switch (state.getValue(TIER) - 1) {
            case 0:
                MaxCharge = ConfigHandler.getBaseTierOneMaxCharge();
                MaxIO = ConfigHandler.getBaseTierOneMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 1);
            case 1:
                MaxCharge = ConfigHandler.getBaseTierTwoMaxCharge();
                MaxIO = ConfigHandler.getBaseTierTwoMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 2);
            case 2:
                MaxCharge = ConfigHandler.getBaseTierThreeMaxCharge();
                MaxIO = ConfigHandler.getBaseTierThreeMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 3);
            case 3:
                MaxCharge = ConfigHandler.getBaseTierFourMaxCharge();
                MaxIO = ConfigHandler.getBaseTierFourMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 4);
            case 4:
                MaxCharge = ConfigHandler.getBaseTierFiveMaxCharge();
                MaxIO = ConfigHandler.getBaseTierFiveMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 5);
        }
        return null;
    }

    @Override

    public boolean isOpaqueCube(IBlockState blockState) {
        return true;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TIER, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TIER);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            TurretBase base = (TurretBase) world.getTileEntity(pos);
        /*if (player.isSneaking() && ConfigHandler.isAllowBaseCamo() && heldItem == null) {
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.setCamoBlock(null);
                    camoBlockState = null;
                } else {
                    player.addChatMessage(
                            new TextComponentString(I18n.translateToLocal("status.ownership")));
                }
            }
        }

        Block heldItemBlock = null;

        if (heldItem != null) {
            heldItemBlock = Block.getBlockFromItem(heldItem.getItem());
        }

        if (!player.isSneaking() && ConfigHandler.isAllowBaseCamo() && heldItem != null &&
                heldItemBlock != null && heldItem.getItem() instanceof ItemBlock &&
                heldItemBlock.isNormalCube(heldItemBlock.getStateFromMeta(heldItem.getMetadata())) && Block.getBlockFromItem(
                heldItem.getItem()).isOpaqueCube(heldItemBlock.getStateFromMeta(heldItem.getMetadata())) && !(Block.getBlockFromItem(
                heldItem.getItem()) instanceof BlockTurretBase)) {
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.setCamoBlock(heldItem);
                    camoBlockState = heldItemBlock.getStateFromMeta(heldItem.getMetadata());
                } else {
                    player.addChatMessage(
                            new TextComponentString(I18n.translateToLocal("status.ownership")));
                }
            }

        } else*/
            if (player.isSneaking() && base != null && player.getHeldItemMainhand() != null &&
                    player.getHeldItemMainhand().getItem() instanceof UsableMetaItem && player.getHeldItemMainhand().getItemDamage() == 2) {
                ((UsableMetaItem) player.getHeldItemMainhand().getItem()).setDataStored(player.getHeldItemMainhand(),base.writeMemoryCardNBT());
            } else if ( !player.isSneaking() && base != null && player.getHeldItemMainhand() != null &&
                    player.getHeldItemMainhand().getItem() instanceof UsableMetaItem && player.getHeldItemMainhand().getItemDamage() == 2 &&
                    ((UsableMetaItem) player.getHeldItemMainhand().getItem()).hasDataStored(player.getHeldItemMainhand())) {
                base.readMemoryCardNBT(((UsableMetaItem) player.getHeldItemMainhand().getItem()).getDataStored(player.getHeldItemMainhand()));
            } else if ( !player.isSneaking() && base != null) {
                TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base);
                if (trustedPlayer != null && trustedPlayer.canOpenGUI) {
                    world.notifyBlockUpdate(pos, state, state, 6);
                    player.openGui(OpenModularTurrets.instance, base.getTier(), world, pos.getX(), pos.getY(), pos.getZ());
                    return true;
                } else if (PlayerUtil.isPlayerOwner(player, base)) {
                    world.notifyBlockUpdate(pos, state, state, 6);
                    player.openGui(OpenModularTurrets.instance, base.getTier(), world, pos.getX(), pos.getY(), pos.getZ());
                } else {
                    player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
                }
            }
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        if (!worldIn.isRemote) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base != null && worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (base != null && worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
            stack) {

        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TurretBase) {
            EntityPlayerMP player = (EntityPlayerMP) placer;
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base == null) {
                return;
            }
            base.setOwner(player.getUniqueID().toString());
            if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
            base.markDirty();
            switch (state.getValue(TIER)) {
                case 1:
                    this.setResistance(ConfigHandler.getBaseTierOneBlastResistance());
                    break;
                case 2:
                    this.setResistance(ConfigHandler.getBaseTierTwoBlastResistance());
                    break;
                case 3:
                    this.setResistance(ConfigHandler.getBaseTierThreeBlastResistance());
                    break;
                case 4:
                    this.setResistance(ConfigHandler.getBaseTierFourBlastResistance());
                    break;
                case 5:
                    this.setResistance(ConfigHandler.getBaseTierFiveBlastResistance());
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            dropItems(worldIn, pos);
            worldIn.removeTileEntity(pos);
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(0, new ItemStack(ModBlocks.turretBase, 1, this.getMetaFromState(state)));
        return drops;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer
            player) {
        return new ItemStack(ModBlocks.turretBase, 1, state.getValue(TIER) - 1);
    }


    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModBlocks.turretBase, 1, i));
        }
    }
}
