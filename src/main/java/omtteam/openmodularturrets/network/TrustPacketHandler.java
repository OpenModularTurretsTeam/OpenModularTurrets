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

    /**
     * Checks the permission, resolves the name (possibly on a background thread, see {@link PlayerLookup}) and then
     * checks everything again, as the player may have walked away or lost the rights in the meantime. A request sent
     * while the player's previous lookup is still running is ignored.
     */
    public static void handleAdd(TrustPackets.Add packet, ServerPlayer player) {
        String name = packet.name().trim();
        if (player.level().getServer() == null || authorizeAdd(packet, player).isEmpty()) return;
        if (name.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.openmodularturrets.player_not_found", name));
            return;
        }
        PlayerLookup.findByName(player.level().getServer(), player, name, target -> {
            if (player.hasDisconnected()) return;
            authorizeAdd(packet, player).ifPresent(trust -> addTrusted(player, trust, name, target));
        });
    }

    /** The trust list at the packet's position, if the player is close enough and an admin of it. */
    private static Optional<TrustManager> authorizeAdd(TrustPackets.Add packet, ServerPlayer player) {
        Optional<TrustHost> host = PacketValidation.findTrustHost(player, packet.pos());
        if (host.isEmpty()) return Optional.empty();
        TrustManager trust = host.get().getTrustManager();
        if (!trust.isAdmin(PlayerAdapter.info(player))) {
            PacketValidation.sendNoPermission(player);
            return Optional.empty();
        }
        return Optional.of(trust);
    }

    private static void addTrusted(ServerPlayer player, TrustManager trust, String name, Optional<PlayerIdentity> target) {
        if (target.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.openmodularturrets.player_not_found", name));
            return;
        }
        PlayerInfo actor = PlayerAdapter.info(player);
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

    /**
     * Only the owner may disown a block; afterwards it has neither owner nor trust list, and open menus of it close
     * themselves (their {@code stillValid} checks fail).
     */
    public static void handleDisown(TrustPackets.Disown packet, ServerPlayer player) {
        PacketValidation.findTrustHost(player, packet.pos()).ifPresent(host -> {
            if (!host.getTrustManager().canPerformOwnerAction(PlayerAdapter.info(player))) {
                PacketValidation.sendNoPermission(player);
                return;
            }
            host.clearOwner();
            player.displayClientMessage(Component.translatable("message.openmodularturrets.disowned"), true);
        });
    }
}
