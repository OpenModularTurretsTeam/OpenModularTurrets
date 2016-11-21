package openmodularturrets.blocks.util;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;


/**
 * Created by Keridos on 05/12/2015.
 * This Class
 */
public abstract class BlockAbstractMiscPane extends BlockPane {
    protected BlockAbstractMiscPane(String par1, String par2, Material material, boolean par4) {
        super(material, par4);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }
}
