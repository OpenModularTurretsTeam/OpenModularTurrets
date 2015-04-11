package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.blocks.BlockNames;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;

public class BlockDisposableTurret extends BlockAbstractTurretHead {

    public BlockDisposableTurret() {
        super();

        this.setBlockName(BlockNames.unlocalisedDisposableItemTurret);
        this.setBlockTextureName(ModInfo.ID + ":disposableItemTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new DisposableItemTurretTileEntity();
    }
}
