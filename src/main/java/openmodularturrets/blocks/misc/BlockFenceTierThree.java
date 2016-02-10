package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.blocks.util.BlockAbstractMiscPane;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;

import java.util.Random;

public class BlockFenceTierThree extends BlockAbstractMiscPane {
    public BlockFenceTierThree() {
        super("stone", "cobblestone", Material.rock, false);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(30.0F);
        this.setHardness(30.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName(Names.Blocks.unlocalisedFenceTierThree);
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);
        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":fenceTierThree");
    }

    @Override
    public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_) {
        if(!(p_149670_5_ instanceof EntityItem))  p_149670_5_.attackEntityFrom(DamageSource.cactus, 3);
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(Blocks.fenceTierThree);
    }
}
