package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.blocks.BlockAbstract;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.ITurretBaseAddonBlock;

import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.WorldUtil.getTouchingBlockStates;
import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;

/**
 * Created by Keridos on 09/02/17.
 * This Class
 */
@SuppressWarnings("unused")
public abstract class BlockTurretBaseAddon extends BlockAbstractTileEntity implements ITurretBaseAddonBlock {
    public BlockTurretBaseAddon() {
        super(Material.ROCK);
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
    protected void clOnNeighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean found = false;
        for (IBlockState blockState : getTouchingBlockStates(worldIn, pos)) {
            if (blockState.getBlock() instanceof BlockTurretBase) {
                found = true;
            }
        }
        if (!found) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }
    }
}
