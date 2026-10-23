package omtteam.openmodularturrets.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

/**
 * The aim of a turret (yaw and pitch relative to its mounting face, see {@code TurretMath}) and whether it is active,
 * sent to clients.
 */
@SuppressWarnings("unused")
public class MessageUpdateTurret implements IMessage {
    private int x, y, z;
    private float yaw, pitch;
    private boolean active;

    public MessageUpdateTurret() {
    }

    public MessageUpdateTurret(TurretTileEntity turretHead) {
        BlockPos pos = turretHead.getPos();

        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.yaw = turretHead.getYaw();
        this.pitch = turretHead.getPitch();
        this.active = turretHead.isActive();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeBoolean(this.active);
    }

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private int getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public BlockPos getPos() {
        return new BlockPos(x, y, z);
    }

    /** Sets the synced aim and activity on the client's copy of the turret. */
    public void applyTo(TurretTileEntity turret) {
        turret.setYawPitch(yaw, pitch);
        turret.setActive(active);
    }
}
