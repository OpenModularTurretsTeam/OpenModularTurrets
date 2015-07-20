package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;

public class BlockFenceTierFour extends BlockPane {

    public BlockFenceTierFour() {
        super("stone", "cobblestone", Material.rock, false);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(50.0F);
        this.setHardness(50.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName(Names.Blocks.unlocalisedFenceTierFour);
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);
        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":fenceTierFour");
    }

    @Override
    public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_) {
        p_149670_5_.attackEntityFrom(DamageSource.cactus, 4);
    }
}
