package omtteam.openmodularturrets.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.omlib.network.PacketValidation;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * Server handlers for {@link BasePackets}. Each action requires the access level the 3.x message handler checked.
 */
public final class BasePacketHandler {
    private BasePacketHandler() {
    }

    /** The base, if it is loaded, the player is close enough and passes {@code access}; otherwise tells the player. */
    private static Optional<TurretBase> authorize(EntityPlayerMP player, BlockPos pos,
                                                  BiPredicate<EntityPlayerMP, TurretBase> access) {
        Optional<TurretBase> base = PacketValidation.find(player, pos, TurretBase.class);
        if (base.isPresent() && !access.test(player, base.get())) {
            PacketValidation.sendNoPermission(player);
            return Optional.empty();
        }
        return base;
    }

    static void handleAction(BasePackets.SimpleAction message, EntityPlayerMP player) {
        BiPredicate<EntityPlayerMP, TurretBase> access = message.action == BasePackets.Action.TOGGLE_MULTI_TARGETING
                ? PlayerUtil::canPlayerChangeSetting : PlayerUtil::isPlayerAdmin;
        authorize(player, message.pos, access).ifPresent(base -> {
            switch (message.action) {
                case TOGGLE_MODE:
                    base.toggleMode();
                    base.informUpdate();
                    break;
                case TOGGLE_MULTI_TARGETING:
                    base.setMultiTargeting(!base.isMultiTargeting());
                    base.informUpdate();
                    break;
                case DROP_BASE:
                    player.getEntityWorld().destroyBlock(message.pos, true);
                    break;
                case DROP_TURRETS:
                    dropTurrets(player.getEntityWorld(), message.pos);
                    break;
                default:
            }
        });
    }

    static void handleSetRange(BasePackets.SetRange message, EntityPlayerMP player) {
        authorize(player, message.pos, PlayerUtil::isPlayerAdmin).ifPresent(base -> {
            base.updateMaxRange();
            base.getTargetingSettings().setRange(message.range);
            base.informUpdate();
        });
    }

    static void handleSetTargeting(BasePackets.SetTargeting message, EntityPlayerMP player) {
        authorize(player, message.pos, PlayerUtil::canPlayerChangeSetting).ifPresent(base -> {
            switch (message.category) {
                case MOBS:
                    base.getTargetingSettings().setTargetMobs(message.enabled);
                    break;
                case NEUTRALS:
                    base.getTargetingSettings().setTargetPassive(message.enabled);
                    break;
                case PLAYERS:
                    base.getTargetingSettings().setTargetPlayers(message.enabled);
                    break;
                default:
            }
        });
    }

    /** Destroys (and drops) the turrets on the six faces of the base. */
    private static void dropTurrets(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos turretPos = pos.offset(facing);
            if (world.isBlockLoaded(turretPos) && world.getTileEntity(turretPos) instanceof TurretTileEntity) {
                world.destroyBlock(turretPos, true);
            }
        }
    }

    public static class SimpleActionHandler implements IMessageHandler<BasePackets.SimpleAction, IMessage> {
        @Override
        public IMessage onMessage(BasePackets.SimpleAction message, MessageContext ctx) {
            PacketValidation.schedule(ctx, player -> handleAction(message, player));
            return null;
        }
    }

    public static class SetRangeHandler implements IMessageHandler<BasePackets.SetRange, IMessage> {
        @Override
        public IMessage onMessage(BasePackets.SetRange message, MessageContext ctx) {
            PacketValidation.schedule(ctx, player -> handleSetRange(message, player));
            return null;
        }
    }

    public static class SetTargetingHandler implements IMessageHandler<BasePackets.SetTargeting, IMessage> {
        @Override
        public IMessage onMessage(BasePackets.SetTargeting message, MessageContext ctx) {
            PacketValidation.schedule(ctx, player -> handleSetTargeting(message, player));
            return null;
        }
    }
}
