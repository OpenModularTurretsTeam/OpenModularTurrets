package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;

public class BlockHardWallTierFive extends BlockAbstract {
    public BlockHardWallTierFive() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(80.0F);
        this.setHardness(80.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName(Names.Blocks.unlocalisedHardWallTierFive);
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);
        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":hardWallTierFive");
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }
}
