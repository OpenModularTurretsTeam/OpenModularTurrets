package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.Names;

public class BlockHardWallTierOne extends BlockAbstract {
    public BlockHardWallTierOne() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(10.0F);
        this.setHardness(10.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(Block.soundTypeStone);
        this.setUnlocalizedName(Names.Blocks.hardWallTierOne);
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }
}
