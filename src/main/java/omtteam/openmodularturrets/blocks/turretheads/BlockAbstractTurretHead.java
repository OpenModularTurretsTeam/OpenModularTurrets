package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.api.IHasItemBlock;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.ITurretBaseAddonBlock;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.openmodularturrets.util.TurretHeadUtil.getTurretBaseFacing;

@SuppressWarnings("deprecation")
public abstract class BlockAbstractTurretHead extends BlockAbstractTileEntity implements IHasItemBlock, ITurretBaseAddonBlock {
    public static final PropertyBool CONCEALED = PropertyBool.create("concealed");


    BlockAbstractTurretHead() {
        super(Material.GLASS);

        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONCEALED, false));
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(CONCEALED)) {
            return new AxisAlignedBB(0F, 0F, 0F, 0F, 0F, 0F);
        }
        return new AxisAlignedBB(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
    }

    @Override
    public AxisAlignedBB getBoundingBoxFromState(IBlockState blockState, World world, BlockPos pos) {
        EnumFacing facing = getTurretBaseFacing(world, pos);
        if (facing != null) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(-0.3F, -0.3F, -0.5F, 0.3F, 0.3F, 0.5F);
            axisAlignedBB = MathUtil.rotateAABB(axisAlignedBB, facing.getOpposite());
            return axisAlignedBB.offset(pos).offset(0.5D, 0.5D, 0.5D);
        }
        return new AxisAlignedBB(0.2F, 0.0F, 0.2F, 0.8F, 1F, 0.8F).offset(pos);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(CONCEALED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CONCEALED) ? 1 : 0;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONCEALED);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)) instanceof TurretBase;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }
}
