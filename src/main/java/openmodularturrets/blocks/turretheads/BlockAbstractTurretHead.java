package openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import openmodularturrets.ModularTurrets;

public abstract class BlockAbstractTurretHead extends BlockContainer {
    public BlockAbstractTurretHead() {
        super(Material.rock);

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 1F, 0.8F);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
