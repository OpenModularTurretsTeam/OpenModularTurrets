package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.ModInfo;
import openmodularturrets.blocks.BlockNames;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;

public class BlockLaserTurret extends BlockAbstractTurretHead {

	public BlockLaserTurret() {
        super();

        this.setBlockName(BlockNames.unlocalisedLaserTurret);
        this.setBlockTextureName(ModInfo.ID + ":laserTurret");
    }

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new LaserTurretTileEntity();
	}
}
