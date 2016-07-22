package openmodularturrets.handler;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import openmodularturrets.client.gui.*;
import openmodularturrets.client.gui.containers.*;
import openmodularturrets.tileentity.Expander;
import openmodularturrets.tileentity.TurretBase;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id) {
            case 1:
                return new TurretBaseTierOneContainer(player.inventory, (TurretBase) tileEntity);
            case 2:
                return new TurretBaseTierTwoContainer(player.inventory, (TurretBase) tileEntity);
            case 3:
                return new TurretBaseTierThreeContainer(player.inventory, (TurretBase) tileEntity);
            case 4:
                return new TurretBaseTierFourContainer(player.inventory, (TurretBase) tileEntity);
            case 5:
                return new TurretBaseTierFiveContainer(player.inventory, (TurretBase) tileEntity);
            case 6:
                return new ConfigContainer((TurretBase) tileEntity);
            case 7:
                return new ExpanderInvContainer(player.inventory, (Expander) tileEntity);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id) {
            case 1:
                return new TurretBaseTierOneGui(player.inventory, (TurretBase) tileEntity);
            case 2:
                return new TurretBaseTierTwoGui(player.inventory, (TurretBase) tileEntity);
            case 3:
                return new TurretBaseTierThreeGui(player.inventory, (TurretBase) tileEntity);
            case 4:
                return new TurretBaseTierFourGui(player.inventory, (TurretBase) tileEntity);
            case 5:
                return new TurretBaseTierFiveGui(player.inventory, (TurretBase) tileEntity);
            case 6:
                return new ConfigureGui(player.inventory, (TurretBase) tileEntity);
            case 7:
                return new ExpanderInvGui(player.inventory, (Expander) tileEntity);
            default:
                return null;
        }
    }
}