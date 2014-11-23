package modularTurrets.blocks.turretheads;

import modularTurrets.ModInfo;
import modularTurrets.blocks.BlockNames;
import modularTurrets.tileentity.turrets.DisposableItemTurretTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
