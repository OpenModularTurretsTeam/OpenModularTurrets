package openmodularturrets.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import openmodularturrets.tileentity.turretBase.TurretBase;
import openmodularturrets.tileentity.turrets.TurretHead;
import net.minecraft.world.World;

public class DropBaseMessage implements IMessage,
		IMessageHandler<DropBaseMessage, IMessage> {
	private int x, y, z;
	private String player;

	public DropBaseMessage() {
	}

	public DropBaseMessage(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);

	}

	@Override
	public IMessage onMessage(DropBaseMessage message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		world.func_147480_a(message.getX(), message.getY(), message.getZ(),
				true);

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

	public String getOwner() {
		return player;
	}
}
