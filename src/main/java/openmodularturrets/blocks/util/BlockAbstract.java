package openmodularturrets.blocks.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Keridos on 05/12/2015. This Class
 */
public abstract class BlockAbstract extends Block {

    protected BlockAbstract(Material material) {
        super(material);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
