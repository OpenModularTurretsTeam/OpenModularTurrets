package openmodularturrets.blocks.util;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Keridos on 05/12/2015. This Class
 */
public abstract class BlockAbstractMiscPane extends BlockPane {

    protected BlockAbstractMiscPane(String par1, String par2, Material material, boolean par4) {
        super(par1, par2, material, par4);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
