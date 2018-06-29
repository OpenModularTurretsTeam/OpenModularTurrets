package omtteam.openmodularturrets.util;

import io.netty.buffer.ByteBuf;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

/**
 * Created by Keridos on 09/02/17.
 * This Class
 */
@SuppressWarnings("unused")
public class TurretHeadSettings {
    public float yaw, pitch;
    public boolean forceFire;

    public TurretHeadSettings() {

    }

    public TurretHeadSettings(float yaw, float pitch, boolean forceFire) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.forceFire = forceFire;
    }

    public TurretHeadSettings(TurretHead turretHead) {
        this.yaw = turretHead.getYaw();
        this.pitch = turretHead.getPitch();
        this.forceFire = turretHead.getAutoFire();
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeBoolean(forceFire);
    }

    public TurretHeadSettings readFromBuf(ByteBuf buf) {
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.forceFire = buf.readBoolean();
        return this;
    }
}
