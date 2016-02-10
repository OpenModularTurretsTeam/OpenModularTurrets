package openmodularturrets.handler;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.client.gui.*;
import openmodularturrets.client.gui.containers.*;
import openmodularturrets.tileentity.expander.AbstractInvExpander;
import openmodularturrets.tileentity.turretbase.*;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch (id) {
            case 1:
                return new TurretBaseTierOneContainer(player.inventory, (TurretBaseTierOneTileEntity) tileEntity);
            case 2:
                return new TurretBaseTierTwoContainer(player.inventory, (TurretBaseTierTwoTileEntity) tileEntity);
            case 3:
                return new TurretBaseTierThreeContainer(player.inventory, (TurretBaseTierThreeTileEntity) tileEntity);
            case 4:
                return new TurretBaseTierFourContainer(player.inventory, (TurretBaseTierFourTileEntity) tileEntity);
            case 5:
                return new TurretBaseTierFiveContainer(player.inventory, (TurretBaseTierFiveTileEntity) tileEntity);
            case 6:
                return new ConfigContainer((TurretBase) tileEntity);
            case 7:
                return new ExpanderInvContainer(player.inventory, (AbstractInvExpander) tileEntity);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch (id) {
            case 1:
                return new TurretBaseTierOneGui(player.inventory, (TurretBaseTierOneTileEntity) tileEntity);
            case 2:
                return new TurretBaseTierTwoGui(player.inventory, (TurretBaseTierTwoTileEntity) tileEntity);
            case 3:
                return new TurretBaseTierThreeGui(player.inventory, (TurretBaseTierThreeTileEntity) tileEntity);
            case 4:
                return new TurretBaseTierFourGui(player.inventory, (TurretBaseTierFourTileEntity) tileEntity);
            case 5:
                return new TurretBaseTierFiveGui(player.inventory, (TurretBaseTierFiveTileEntity) tileEntity);
            case 6:
                return new ConfigureGui(player.inventory, (TurretBase) tileEntity);
            case 7:
                return new ExpanderInvGui(player.inventory, (AbstractInvExpander) tileEntity);
            default:
                return null;
        }
    }
}