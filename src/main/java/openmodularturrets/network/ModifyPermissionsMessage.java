package openmodularturrets.network;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class ModifyPermissionsMessage implements IMessage, IMessageHandler<ModifyPermissionsMessage, IMessage> {
	private int x, y, z;
	private String player, perm;
	private boolean canDo;

	public ModifyPermissionsMessage() {
	}

	public ModifyPermissionsMessage(int x, int y, int z, String player,
			String perm, boolean canDo) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.player = player;
		this.perm = perm;
		this.canDo = canDo;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.player = ByteBufUtils.readUTF8String(buf);
		this.perm = ByteBufUtils.readUTF8String(buf);
		this.canDo = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeUTF8String(buf, this.player);
		ByteBufUtils.writeUTF8String(buf, this.perm);
		buf.writeBoolean(canDo);
	}

	@Override
	public IMessage onMessage(ModifyPermissionsMessage message,
			MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;
		TurretBase turret = (TurretBase) world.getTileEntity(message.getX(),
				message.getY(), message.getZ());
		
		System.out.println(turret);

		if (message.getPerm().equals("gui")) {
			turret.getTrustedPlayer(message.getPlayer()).setCanOpenGUI(message.canDo);
		}

		if (message.getPerm().equals("targeting")) {
			turret.getTrustedPlayer(message.getPlayer()).setCanChangeTargeting(message.canDo);
		}

		if (message.getPerm().equals("isAdmin")) {
			turret.getTrustedPlayer(message.getPlayer()).setAdmin(message.canDo);
		}

		return null;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String getPlayer() {
		return player;
	}

	public String getPerm() {
		return perm;
	}

	public boolean getCanDo() {
		return canDo;
	}

}
