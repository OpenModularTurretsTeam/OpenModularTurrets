package openmodularturrets.gui;

import openmodularturrets.gui.containers.ConfigContainer;
import openmodularturrets.gui.containers.TurretBaseTierFourContainer;
import openmodularturrets.gui.containers.TurretBaseTierOneContainer;
import openmodularturrets.gui.containers.TurretBaseTierThreeContainer;
import openmodularturrets.gui.containers.TurretBaseTierTwoContainer;
import openmodularturrets.gui.containers.TurretBaseTierWoodContainer;
import openmodularturrets.tileentity.turretBase.TurretBase;
import openmodularturrets.tileentity.turretBase.TurretBaseTierFourTileEntity;
import openmodularturrets.tileentity.turretBase.TurretBaseTierOneTileEntity;
import openmodularturrets.tileentity.turretBase.TurretBaseTierThreeTileEntity;
import openmodularturrets.tileentity.turretBase.TurretBaseTierTwoTileEntity;
import openmodularturrets.tileentity.turretBase.TurretWoodBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch (id) {
            case 0:
                return new TurretBaseTierWoodContainer(player.inventory, (TurretWoodBase) tileEntity);
            case 1:
                return new TurretBaseTierOneContainer(player.inventory, (TurretBaseTierOneTileEntity) tileEntity);
            case 2:
                return new TurretBaseTierTwoContainer(player.inventory, (TurretBaseTierTwoTileEntity) tileEntity);
            case 3:
                return new TurretBaseTierThreeContainer(player.inventory, (TurretBaseTierThreeTileEntity) tileEntity);
            case 4:
                return new TurretBaseTierFourContainer(player.inventory, (TurretBaseTierFourTileEntity) tileEntity);
            case 5:
                return new ConfigContainer(player.inventory, (TurretBase) tileEntity);
            default:
                return null;
        }
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch (id) {
            case 0:
                return new TurretBaseTierWoodGui(player.inventory, (TurretWoodBase) tileEntity);
            case 1:
                return new TurretBaseTierOneGui(player.inventory, (TurretBaseTierOneTileEntity) tileEntity);
            case 2:
                return new TurretBaseTierTwoGui(player.inventory, (TurretBaseTierTwoTileEntity) tileEntity);
            case 3:
                return new TurretBaseTierThreeGui(player.inventory, (TurretBaseTierThreeTileEntity) tileEntity);
            case 4:
                return new TurretBaseTierFourGui(player.inventory, (TurretBaseTierFourTileEntity) tileEntity);
            case 5:
                return new ConfigureGui(player.inventory, (TurretBase) tileEntity);
            default:
                return null;
        }
	}
}