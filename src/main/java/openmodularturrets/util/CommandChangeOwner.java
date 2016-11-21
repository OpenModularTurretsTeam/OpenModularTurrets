package openmodularturrets.util;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import openmodularturrets.tileentity.TurretBase;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) {
        if (params.length != 5) {
            sender.addChatMessage(new TextComponentString(getCommandUsage(sender)));
            return;
        }
        try {
            int dimension = Integer.parseInt(params[0]);
            int x = Integer.parseInt(params[1]);
            int y = Integer.parseInt(params[2]);
            int z = Integer.parseInt(params[3]);
            String ownerName = params[4];
            if (DimensionManager.getWorld(dimension) == null) {
                sender.addChatMessage(new TextComponentString("Invalid dimension"));
                return;
            }
            WorldServer worldserver = server.worldServerForDimension(dimension);

            TileEntity tileEntity = worldserver.getTileEntity(new BlockPos(x, y, z));
            if (tileEntity instanceof TurretBase) {
                TurretBase turret = (TurretBase) tileEntity;
                turret.setOwner(ownerName);
                sender.addChatMessage(new TextComponentString("Turret ownership has been updated"));
            } else {
                sender.addChatMessage(new TextComponentString("No turret base was found at that location"));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new TextComponentString("Dimension and coordinates must be numbers"));
        }
    }
}

