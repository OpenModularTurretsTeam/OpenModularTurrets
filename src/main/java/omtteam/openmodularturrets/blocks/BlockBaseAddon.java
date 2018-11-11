package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.omlib.api.IHasItemBlock;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.items.blocks.ItemBlockBaseAddon;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.BaseAddon;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;

/**
 * Created by Keridos on 25/11/17.
 * This Class
 */
public class BlockBaseAddon extends BlockTurretBaseAddon implements IHasItemBlock {
    public static final PropertyInteger MODEL = PropertyInteger.create("model", 0, 1);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockBaseAddon() {
        super(Material.GLASS);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (!OMTConfig.TURRETS.turretBreakable) {
            this.setBlockUnbreakable();
        }
        this.setResistance(3.0F);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState());
        this.setUnlocalizedName(OMTNames.Blocks.baseAddon);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.baseAddon);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockBaseAddon(block);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(MODEL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(MODEL);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MODEL, FACING);
    }

    @SuppressWarnings("WeakerAccess")
    public static AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing) {
        return BlockTurretBaseAddon.getBoundingBoxFromFacing(facing);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        BaseAddon te = ((BaseAddon) worldIn.getTileEntity(pos));
        if (te != null) {
            return state.withProperty(FACING, te.getOrientation());
        } else return state.withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BaseAddon();
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState blockState = this.getActualState(state, source, pos);
        EnumFacing facing = blockState.getValue(FACING);
        return getBoundingBoxFromFacing(facing);
    }

    @Override
    public AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing, World world, BlockPos pos) {
        return getBoundingBoxFromFacing(facing).offset(pos);
    }


    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hand.equals(EnumHand.OFF_HAND)) return true;
        BaseAddon baseAddon = (BaseAddon) worldIn.getTileEntity(pos);
        if (baseAddon == null) {
            return true;
        }
        TurretBase base = baseAddon.getBase();
        if (base == null) {
            worldIn.destroyBlock(pos, true);
            return true;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        BaseAddon baseAddon = (BaseAddon) worldIn.getTileEntity(pos);
        if (baseAddon != null) {
            baseAddon.setSide();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (TileEntity tileEntity : getTouchingTileEntities(worldIn, pos)) {
            if (tileEntity instanceof TurretBase) return true;
        }
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            dropItems(worldIn, pos);
            super.breakBlock(worldIn, pos, state);
        }
    }
}
