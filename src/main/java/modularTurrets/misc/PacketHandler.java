package modularTurrets.misc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import modularTurrets.ModInfo;
import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turrets.TurretHead;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

public class PacketHandler implements IPacketHandler {

    public static int UPDATE_YAXISDETECT_ON_SERVER = 1;
    public static int CHANGE_TURRETBASE_ATTACKS_MOBS = 2;
    public static int CHANGE_TURRETBASE_ATTACKS_NEUTRALS = 3;
    public static int CHANGE_TURRETBASE_ATTACKS_PLAYERS = 4;
    public static int DROP_ALL_TURRETS_ATTACHED_TO_BASE = 5;
    public static int DROP_BASE_ON_SERVER = 6;
    public static int SET_BASE_OWNER = 7;
    public static int BASE_ADD_TRUSTED_PLAYER = 8;
    public static int BASE_REMOVE_TRUSTED_PLAYER = 9;

    @Override
    public void onPacketData(INetworkManager manager,
	    Packet250CustomPayload packet, Player player) {

	EntityPlayerMP playerMP = (EntityPlayerMP) player;

	if (packet.channel.equals(ModInfo.CHANNEL)) {
	    DataInputStream inputStream = new DataInputStream(
		    new ByteArrayInputStream(packet.data));

	    int function;
	    int value;
	    int xCoord;
	    int yCoord;
	    int zCoord;
	    String stringValue = "";

	    try {

		function = inputStream.readInt();
		xCoord = inputStream.readInt();
		yCoord = inputStream.readInt();
		zCoord = inputStream.readInt();
		value = inputStream.readInt();

		if (function > 6) {
		    stringValue = inputStream.readUTF();
		}

		if (function == 1) {
		    TurretBase base = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    base.setyAxisDetect(value);
		}

		if (function == 2) {
		    boolean changeTo = false;
		    if (value == 1) {
			changeTo = true;
		    }
		    if (value == 0) {
			changeTo = false;
		    }

		    TurretBase base = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    base.setAttacksMobs(changeTo);
		}

		if (function == 3) {
		    boolean changeTo = false;
		    if (value == 1) {
			changeTo = true;
		    }
		    if (value == 0) {
			changeTo = false;
		    }
		    TurretBase base = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    base.setAttacksNeutrals(changeTo);
		}

		if (function == 4) {
		    boolean changeTo = false;
		    if (value == 1) {
			changeTo = true;
		    }
		    if (value == 0) {
			changeTo = false;
		    }
		    TurretBase base = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    base.setAttacksPlayers(changeTo);
		}

		if (function == 5) {

		    if (playerMP.worldObj.getBlockTileEntity(xCoord + 1,
			    yCoord, zCoord) instanceof TurretHead) {
			playerMP.worldObj.destroyBlock(xCoord + 1, yCoord,
				zCoord, true);
		    }

		    if (playerMP.worldObj.getBlockTileEntity(xCoord - 1,
			    yCoord, zCoord) instanceof TurretHead) {
			playerMP.worldObj.destroyBlock(xCoord - 1, yCoord,
				zCoord, true);
		    }

		    if (playerMP.worldObj.getBlockTileEntity(xCoord,
			    yCoord + 1, zCoord) instanceof TurretHead) {
			playerMP.worldObj.destroyBlock(xCoord, yCoord + 1,
				zCoord, true);
		    }

		    if (playerMP.worldObj.getBlockTileEntity(xCoord,
			    yCoord - 1, zCoord) instanceof TurretHead) {
			playerMP.worldObj.destroyBlock(xCoord, yCoord - 1,
				zCoord, true);
		    }

		    if (playerMP.worldObj.getBlockTileEntity(xCoord, yCoord,
			    zCoord + 1) instanceof TurretHead) {
			playerMP.worldObj.destroyBlock(xCoord, yCoord,
				zCoord + 1, true);
		    }

		    if (playerMP.worldObj.getBlockTileEntity(xCoord, yCoord,
			    zCoord - 1) instanceof TurretHead) {
			playerMP.worldObj.destroyBlock(xCoord, yCoord,
				zCoord - 1, true);
		    }

		}

		if (function == 6) {

		    int turretBase = playerMP.worldObj.getBlockId(xCoord,
			    yCoord, zCoord);
		    playerMP.worldObj
			    .destroyBlock(xCoord, yCoord, zCoord, true);
		}

		if (function == 7) {

		    TurretBase turretBase = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    turretBase.setOwner(stringValue);
		}
		
		if (function == 8) {
		    TurretBase turretBase = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    turretBase.addTrustedPlayer(stringValue);
		}
		
		if (function == 9) {
		    TurretBase turretBase = (TurretBase) playerMP.worldObj
			    .getBlockTileEntity(xCoord, yCoord, zCoord);
		    turretBase.removeTrustedPlayer(stringValue);
		}

	    } catch (IOException e) {
		e.printStackTrace();
		return;
	    }
	}

    }
}
