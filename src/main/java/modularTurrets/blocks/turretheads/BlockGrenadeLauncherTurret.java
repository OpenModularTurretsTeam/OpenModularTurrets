package modularTurrets.blocks.turretheads;

import modularTurrets.ModInfo;
import modularTurrets.blocks.BlockNames;
import modularTurrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGrenadeLauncherTurret extends BlockAbstractTurretHead {

	public BlockGrenadeLauncherTurret() {
		super();

		this.setBlockName(BlockNames.unlocalisedGrenadeTurret);
        this.setBlockTextureName(ModInfo.ID + ":grenadeTurret");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new GrenadeLauncherTurretTileEntity();
	}
}
