package omtteam.openmodularturrets.turret;

import io.netty.buffer.ByteBuf;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

/**
 * Created by Keridos on 09/02/17.
 * This Class
 */
@SuppressWarnings("unused")
public class TurretHeadSettings {
    public float yaw = 0, pitch = 0;
    public boolean forceFire;
    private boolean isDirected = false;

    public TurretHeadSettings() {

    }

    public TurretHeadSettings(float yaw, float pitch, boolean forceFire) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.forceFire = forceFire;
    }

    public TurretHeadSettings(TurretHead turretHead) {
        if (turretHead instanceof AbstractDirectedTurret) {
            this.yaw = ((AbstractDirectedTurret) turretHead).getYaw();
            this.pitch = ((AbstractDirectedTurret) turretHead).getPitch();
            this.isDirected = true;
        }
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

    public boolean isDirected() {
        return isDirected;
    }

    public void setDirected(boolean directed) {
        isDirected = directed;
    }
}
