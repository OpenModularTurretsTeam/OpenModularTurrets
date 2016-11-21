package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.Names;

public class BlockHardWallTierThree extends BlockAbstract {
    public BlockHardWallTierThree() {
        super(Material.ROCK);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(30.0F);
        this.setHardness(30.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName(Names.Blocks.hardWallTierThree);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }
}
