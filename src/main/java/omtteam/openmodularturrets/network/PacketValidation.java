package omtteam.openmodularturrets.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.security.TrustHost;

import java.util.Optional;

/**
 * Server-side checks shared by all C2S packets. Never trust the client: every handler re-checks distance and
 * permission.
 */
public final class PacketValidation {
    /** 1.12.2 {@code TurretBaseContainer.canInteractWith}: squared distance below 64. */
    private static final double MAX_DISTANCE_SQR = 64.0;

    private PacketValidation() {
    }

    /**
     * The turret base at {@code pos} if it is loaded and the player is close enough to interact with it.
     */
    public static Optional<TurretBaseBlockEntity> findBase(ServerPlayer player, BlockPos pos) {
        if (!player.level().isLoaded(pos)) return Optional.empty();
        if (player.distanceToSqr(Vec3.atCenterOf(pos)) >= MAX_DISTANCE_SQR) return Optional.empty();
        return player.level().getBlockEntity(pos) instanceof TurretBaseBlockEntity base
                ? Optional.of(base) : Optional.empty();
    }

    /** Like {@link #findBase}, for any block with a trust list (turret bases and camouflage traps). */
    public static Optional<TrustHost> findTrustHost(ServerPlayer player, BlockPos pos) {
        if (!player.level().isLoaded(pos)) return Optional.empty();
        if (player.distanceToSqr(Vec3.atCenterOf(pos)) >= MAX_DISTANCE_SQR) return Optional.empty();
        return player.level().getBlockEntity(pos) instanceof TrustHost host ? Optional.of(host) : Optional.empty();
    }

    /** OMLib {@code status_permission} message. */
    public static void sendNoPermission(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.openmodularturrets.no_permission"));
    }
}
