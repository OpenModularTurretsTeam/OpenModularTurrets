package omtteam.openmodularturrets.network;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.security.TrustResult;
import omtteam.openmodularturrets.security.PlayerAdapter;
import omtteam.openmodularturrets.security.PlayerLookup;
import omtteam.openmodularturrets.security.TrustHost;

import java.util.Optional;

/**
 * Server handlers for {@link TrustPackets}. The permission rules themselves are enforced by the core
 * {@link TrustManager}; this class only adds distance checks, name resolution and feedback.
 */
public final class TrustPacketHandler {
    private TrustPacketHandler() {
    }

    public static void handleAdd(TrustPackets.Add packet, ServerPlayer player) {
        Optional<TrustHost> host = PacketValidation.findTrustHost(player, packet.pos());
        if (host.isEmpty()) return;
        PlayerInfo actor = PlayerAdapter.info(player);
        TrustManager trust = host.get().getTrustManager();
        if (!trust.isAdmin(actor)) {
            PacketValidation.sendNoPermission(player);
            return;
        }
        if (player.getServer() == null) {
            return;
        }
        String name = packet.name().trim();
        Optional<PlayerIdentity> target = name.isEmpty() ? Optional.empty()
                : PlayerLookup.findByName(player.getServer(), name);
        if (target.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.openmodularturrets.player_not_found", name));
            return;
        }
        TrustResult result = trust.addTrustedPlayer(actor, target.get());
        switch (result) {
            case ALREADY_TRUSTED -> player.sendSystemMessage(
                    Component.translatable("message.openmodularturrets.already_trusted", target.get().name()));
            case IS_OWNER -> player.sendSystemMessage(
                    Component.translatable("message.openmodularturrets.owner_not_trustable"));
            case NO_PERMISSION -> PacketValidation.sendNoPermission(player);
            default -> {
            }
        }
    }

    public static void handleRemove(TrustPackets.Remove packet, ServerPlayer player) {
        PacketValidation.findTrustHost(player, packet.pos()).ifPresent(host -> {
            TrustResult result = host.getTrustManager().removeTrustedPlayer(PlayerAdapter.info(player), packet.player());
            if (result == TrustResult.NO_PERMISSION) PacketValidation.sendNoPermission(player);
        });
    }

    public static void handleChangeAccess(TrustPackets.ChangeAccess packet, ServerPlayer player) {
        if (packet.delta() != 1 && packet.delta() != -1) return;
        PacketValidation.findTrustHost(player, packet.pos()).ifPresent(host -> {
            TrustResult result = host.getTrustManager()
                    .changeAccessLevel(PlayerAdapter.info(player), packet.player(), packet.delta());
            if (result == TrustResult.NO_PERMISSION) PacketValidation.sendNoPermission(player);
        });
    }
}
