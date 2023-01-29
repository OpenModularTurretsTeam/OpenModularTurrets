package openmodularturrets.util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by nico on 6/4/15.
 */

public class CommandChangeOwner extends CommandBase {

    @Override
    public String getCommandName() {
        return "omtchangeowner";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<dimension> <x> <y> <z> <new owner>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] params) {
        if (params.length != 5) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }
        try {
            int dimension = Integer.parseInt(params[0]);
            int x = Integer.parseInt(params[1]);
            int y = Integer.parseInt(params[2]);
            int z = Integer.parseInt(params[3]);
            String ownerName = params[4];
            WorldServer worldserver = MinecraftServer.getServer().worldServerForDimension(dimension);
            if (worldserver == null) {
                sender.addChatMessage(new ChatComponentText("Invalid dimension"));
                return;
            }
            TileEntity tileEntity = worldserver.getTileEntity(x, y, z);
            if (tileEntity instanceof TurretBase) {
                TurretBase turret = (TurretBase) tileEntity;
                turret.setOwner(ownerName);
                sender.addChatMessage(new ChatComponentText("Turret ownership has been updated"));
            } else {
                sender.addChatMessage(new ChatComponentText("No turret base was found at that location"));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("Dimension and coordinates must be numbers"));
        }
    }
}
