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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.blocks.ItemBlockExpander;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.player.PlayerUtil.addChatMessage;

/**
 * Created by Keridos on 19/07/16.
 * This Class implements the block for expanders.
 */
@SuppressWarnings("deprecation")
public class BlockExpander extends AbstractBaseAttachment implements IHasItemBlock {
    private static final PropertyInteger EXPANDER_META = PropertyInteger.create("meta", 0, 9);

    public BlockExpander() {
        super(Material.GLASS);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (!OMTConfig.TURRETS.turretBreakable) {
            this.setBlockUnbreakable();
        }
        this.setResistance(3.0F);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(EXPANDER_META, 0));
        this.setUnlocalizedName(OMTNames.Blocks.expander);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.expander);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockExpander(block);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(EXPANDER_META, meta);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getMetaFromState(IBlockState state) {
        return state.getValue(EXPANDER_META);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EXPANDER_META, FACING);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = (worldIn.getTileEntity(pos));
        if (te instanceof Expander) {
            return state.withProperty(FACING, ((Expander) te).getOrientation());
        } else return state.withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(EXPANDER_META) < 5) {
            return new Expander(state.getValue(EXPANDER_META), false);
        } else {
            return new Expander(state.getValue(EXPANDER_META), true);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hand.equals(EnumHand.OFF_HAND)) return true;
        Expander expander = (Expander) worldIn.getTileEntity(pos);
        if (expander == null) {
            return true;
        }
        TurretBase base = expander.getBase();
        if (base == null) {
            worldIn.destroyBlock(pos, true);
            return true;
        }

        if (PlayerUtil.canPlayerAccessBlock(playerIn, base) && state.getValue(EXPANDER_META) < 5) {
            playerIn.openGui(OpenModularTurrets.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        if (PlayerUtil.isPlayerOwner(playerIn, base)) {
            if (playerIn.isSneaking() && playerIn.getHeldItemMainhand().isEmpty()) {
                worldIn.destroyBlock(pos, true);
            } else if (state.getValue(EXPANDER_META) < 5) {
                playerIn.openGui(OpenModularTurrets.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());
            } else {
                return true;
            }
        } else {
            addChatMessage(playerIn, new TextComponentTranslation(OMLibNames.Localizations.Text.STATUS_PERMISSION));
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            dropItems(worldIn, pos);
            super.breakBlock(worldIn, pos, state);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        Expander expander = (Expander) worldIn.getTileEntity(pos);
        if (expander != null) {
            expander.setOwner(expander.getBase().getOwner());
            expander.setSide();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public int damageDropped(IBlockState state) {
        return state.getValue(EXPANDER_META);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < 10; i++) {
            subItems.add(new ItemStack(ModBlocks.expander, 1, i));
        }
    }
}
