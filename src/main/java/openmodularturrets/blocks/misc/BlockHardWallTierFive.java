package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.Names;

public class BlockHardWallTierFive extends BlockAbstract {
    public BlockHardWallTierFive() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(80.0F);
        this.setHardness(80.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(Block.soundTypeStone);
        this.setUnlocalizedName(Names.Blocks.hardWallTierFive);
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }
}
