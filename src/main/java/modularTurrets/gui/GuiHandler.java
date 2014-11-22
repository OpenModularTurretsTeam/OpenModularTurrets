package modularTurrets.gui;

import modularTurrets.gui.containers.ConfigContainer;
import modularTurrets.gui.containers.TurretBaseTierFourContainer;
import modularTurrets.gui.containers.TurretBaseTierOneContainer;
import modularTurrets.gui.containers.TurretBaseTierThreeContainer;
import modularTurrets.gui.containers.TurretBaseTierTwoContainer;
import modularTurrets.gui.containers.TurretBaseTierWoodContainer;
import modularTurrets.tileentity.turretBase.TurretBase;
import modularTurrets.tileentity.turretBase.TurretBaseTierFourTileEntity;
import modularTurrets.tileentity.turretBase.TurretBaseTierOneTileEntity;
import modularTurrets.tileentity.turretBase.TurretBaseTierThreeTileEntity;
import modularTurrets.tileentity.turretBase.TurretBaseTierTwoTileEntity;
import modularTurrets.tileentity.turretBase.TurretWoodBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (id == 0) {
			return new TurretBaseTierWoodContainer(player.inventory,
					(TurretWoodBase) tileEntity);
		}
		if (id == 1) {
			return new TurretBaseTierOneContainer(player.inventory,
					(TurretBaseTierOneTileEntity) tileEntity);
		}
		if (id == 2) {
			return new TurretBaseTierTwoContainer(player.inventory,
					(TurretBaseTierTwoTileEntity) tileEntity);
		}
		if (id == 3) {
			return new TurretBaseTierThreeContainer(player.inventory,
					(TurretBaseTierThreeTileEntity) tileEntity);
		}
		if (id == 4) {
			return new TurretBaseTierFourContainer(player.inventory,
					(TurretBaseTierFourTileEntity) tileEntity);
		}
		if (id == 5) {
			return new ConfigContainer(player.inventory,
					(TurretBase) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (id == 0) {
			return new TurretBaseTierWoodGui(player.inventory,
					(TurretWoodBase) tileEntity);
		}

		if (id == 1) {
			return new TurretBaseTierOneGui(player.inventory,
					(TurretBaseTierOneTileEntity) tileEntity);
		}

		if (id == 2) {
			return new TurretBaseTierTwoGui(player.inventory,
					(TurretBaseTierTwoTileEntity) tileEntity);
		}

		if (id == 3) {
			return new TurretBaseTierThreeGui(player.inventory,
					(TurretBaseTierThreeTileEntity) tileEntity);
		}

		if (id == 4) {
			return new TurretBaseTierFourGui(player.inventory,
					(TurretBaseTierFourTileEntity) tileEntity);
		}

		if (id == 5) {
			return new ConfigureGui(player.inventory,
					(TurretBase) tileEntity);
		}

		return null;

	}
}