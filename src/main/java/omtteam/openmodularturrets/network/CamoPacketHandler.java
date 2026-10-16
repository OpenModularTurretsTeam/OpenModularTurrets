package omtteam.openmodularturrets.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.core.camo.CamoLight;

/**
 * Server handlers for {@link CamoPackets}. The player must be close, the host must have adjustable light and the
 * player must be allowed to change it.
 */
public final class CamoPacketHandler {
    /** Same distance limit as {@link PacketValidation}. */
    private static final double MAX_DISTANCE_SQR = 64.0;

    private CamoPacketHandler() {
    }

    public static void handleSetLight(CamoPackets.SetLight packet, ServerPlayer player) {
        if (!player.level().isLoaded(packet.pos())
                || player.distanceToSqr(Vec3.atCenterOf(packet.pos())) >= MAX_DISTANCE_SQR
                || !(player.level().getBlockEntity(packet.pos()) instanceof CamoHost host)
                || !host.isCamoEnabled() || !host.supportsLight()) {
            return;
        }
        if (!host.canChangeLight(player)) {
            PacketValidation.sendNoPermission(player);
            return;
        }
        host.setLight(new CamoLight(packet.value(), packet.opacity()));
    }
}
