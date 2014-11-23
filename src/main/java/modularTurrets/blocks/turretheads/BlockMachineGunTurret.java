package modularTurrets.blocks.turretheads;

import modularTurrets.ModInfo;
import modularTurrets.blocks.BlockNames;
import modularTurrets.tileentity.turrets.MachineGunTurretTileEntity;
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
