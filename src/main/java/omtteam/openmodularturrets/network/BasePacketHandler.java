package omtteam.openmodularturrets.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.omlib.compat.MenuCompat;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.menu.TurretBaseMenu;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.Optional;

/**
 * Server handlers for {@link BasePackets}. Each action requires the access level the 1.12.2 message handler checked.
 */
public final class BasePacketHandler {
    private BasePacketHandler() {
    }

    /** The base, if the player is close enough and has at least {@code required}; otherwise tells the player. */
    private static Optional<TurretBaseBlockEntity> authorize(ServerPlayer player, BlockPos pos, AccessLevel required) {
        Optional<TurretBaseBlockEntity> base = PacketValidation.findBase(player, pos);
        if (base.isEmpty()) return Optional.empty();
        if (!base.get().getTrustManager().hasAccess(PlayerAdapter.info(player), required)) {
            PacketValidation.sendNoPermission(player);
            return Optional.empty();
        }
        return base;
    }

    public static void handleAction(BasePackets.SimpleAction packet, ServerPlayer player) {
        AccessLevel required = switch (packet.action()) {
            case TOGGLE_MODE, DROP_BASE, DROP_TURRETS -> AccessLevel.ADMIN;
            case TOGGLE_MULTI_TARGETING -> AccessLevel.CHANGE_SETTINGS;
            case OPEN_MENU -> AccessLevel.OPEN_GUI;
        };
        authorize(player, packet.pos(), required).ifPresent(base -> {
            TurretBaseLogic logic = base.getLogic();
            switch (packet.action()) {
                case TOGGLE_MODE -> logic.cycleMode();
                case TOGGLE_MULTI_TARGETING -> logic.setMultiTargeting(!logic.isMultiTargeting());
                case DROP_BASE -> player.level().destroyBlock(packet.pos(), true);
                case DROP_TURRETS -> dropTurrets((ServerLevel) player.level(), packet.pos(), logic);
                case OPEN_MENU -> openMenu(player, base);
            }
        });
    }

    public static void handleSetRange(BasePackets.SetRange packet, ServerPlayer player) {
        // 1.12.2 required admin here although the GUI showed the buttons to "change settings" players (user decision)
        authorize(player, packet.pos(), AccessLevel.CHANGE_SETTINGS).ifPresent(base -> base.getLogic().setRange(packet.range()));
    }

    public static void handleSetTargeting(BasePackets.SetTargeting packet, ServerPlayer player) {
        authorize(player, packet.pos(), AccessLevel.CHANGE_SETTINGS).ifPresent(base -> {
            TurretBaseLogic logic = base.getLogic();
            switch (packet.category()) {
                case MOBS -> logic.setTargetMobs(packet.enabled());
                case NEUTRALS -> logic.setTargetPassive(packet.enabled());
                case PLAYERS -> logic.setTargetPlayers(packet.enabled());
            }
        });
    }

    /**
     * 1.12.2 {@code MessageDropTurrets}: destroys (and drops) every turret on the base's faces. Only blocks that are
     * still turrets are destroyed, in case the base's list is out of date (a block swapped without a neighbour update).
     */
    private static void dropTurrets(ServerLevel level, BlockPos pos, TurretBaseLogic logic) {
        for (Face face : logic.getTurrets().keySet()) {
            BlockPos turretPos = pos.offset(face.getStepX(), face.getStepY(), face.getStepZ());
            if (level.getBlockState(turretPos).getBlock() instanceof TurretBlock) {
                level.destroyBlock(turretPos, true);
            }
        }
    }

    /** Opens the base menu (also used by the block on right-click). The caller checked the permission. */
    public static void openMenu(ServerPlayer player, TurretBaseBlockEntity base) {
        PlayerInfo info = PlayerAdapter.info(player);
        if (!base.getTrustManager().canOpenGui(info)) return;
        MenuCompat.open(player, base.getBlockState().getBlock().getName(), base.getBlockPos(),
                (containerId, inventory, menuPlayer) -> TurretBaseMenu.server(containerId, inventory, base));
    }
}
