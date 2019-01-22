package omtteam.openmodularturrets.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import omtteam.openmodularturrets.client.gui.ConfigureGui;
import omtteam.openmodularturrets.client.gui.ExpanderInvGui;
import omtteam.openmodularturrets.client.gui.TurretBaseGui;
import omtteam.openmodularturrets.client.gui.containers.ExpanderInvContainer;
import omtteam.openmodularturrets.client.gui.containers.TurretBaseContainer;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.TurretBase;

public class OMTGuiHandler implements IGuiHandler {
    private static OMTGuiHandler instance;

    private OMTGuiHandler() {
    }

    public static OMTGuiHandler getInstance() {
        if (instance == null) {
            instance = new OMTGuiHandler();
        }
        return instance;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id) {
            case 1:
                return new TurretBaseContainer(player.inventory, (TurretBase) tileEntity);
            case 2:
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
                return new TurretBaseGui(player.inventory, (TurretBase) tileEntity);
            case 2:
                return new ExpanderInvGui(player.inventory, (Expander) tileEntity);
            case 20:
                return new ConfigureGui(player.inventory, (TurretBase) tileEntity);
            default:
                return null;
        }
    }
}