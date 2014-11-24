package openmodularturrets.blocks.turretheads;

import openmodularturrets.ModInfo;
import openmodularturrets.blocks.BlockNames;
import openmodularturrets.tileentity.turrets.MachineGunTurretTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMachineGunTurret extends BlockAbstractTurretHead {

	public BlockMachineGunTurret() {
		super();

		this.setBlockName(BlockNames.unlocalisedMachineGunTurret);
        this.setBlockTextureName(ModInfo.ID + ":machineGunTurret");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new MachineGunTurretTileEntity();
	}
}
