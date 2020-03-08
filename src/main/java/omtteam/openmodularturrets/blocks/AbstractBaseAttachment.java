package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.api.ITurretBaseAddonBlock;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.world.WorldUtil.getTouchingBlockStates;
import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntities;

/**
 * Created by Keridos on 09/02/17.
 * This Class
 */
@SuppressWarnings("unused")
public abstract class AbstractBaseAttachment extends BlockAbstractTileEntity implements ITurretBaseAddonBlock {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public AbstractBaseAttachment(Material material) {
        super(material);
    }

    public static AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing) {
        AxisAlignedBB alignedBB = MathUtil.rotateAABB(new AxisAlignedBB(-3 / 8F, -3 / 8F, -3 / 16F, 3 / 8F, 3 / 8F, 3 / 16F), facing.getOpposite());
        double[] offset = new double[3];
        offset[0] = 0.5D + facing.getFrontOffsetX() * 0.325D;
        offset[1] = 0.5D + facing.getFrontOffsetY() * 0.325D;
        offset[2] = 0.5D + facing.getFrontOffsetZ() * 0.325D;
        return alignedBB.offset(offset[0], offset[1], offset[2]);
    }

    @Override
    public AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing, World world, BlockPos pos) {
        return getBoundingBoxFromFacing(facing).offset(pos);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState blockState = this.getActualState(state, source, pos);
        EnumFacing facing = blockState.getValue(FACING);
        return getBoundingBoxFromFacing(facing);
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
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean found = false;
        for (IBlockState blockState : getTouchingBlockStates(worldIn, pos)) {
            if (blockState.getBlock() instanceof BlockTurretBase) {
                found = true;
            }
        }
        if (!found) {
            worldIn.destroyBlock(pos, true);
        }
    }
}
