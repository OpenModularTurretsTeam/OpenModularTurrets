package omtteam.openmodularturrets.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.blocks.BlockAbstract;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.ITurretBaseAddon;

import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;

/**
 * Created by Keridos on 09/02/17.
 * This Class
 */
@SuppressWarnings("unused")
public abstract class BlockTurretBaseAddon extends BlockAbstract implements ITurretBaseAddon {
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
}
