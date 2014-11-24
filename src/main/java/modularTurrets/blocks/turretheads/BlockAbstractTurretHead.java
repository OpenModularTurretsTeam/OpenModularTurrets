package modularTurrets.blocks.turretheads;

import modularTurrets.ModularTurrets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;

public abstract class BlockAbstractTurretHead extends BlockContainer {
    public BlockAbstractTurretHead() {
        super(Material.rock);

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(Block.soundTypeStone);
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
