package omtteam.openmodularturrets.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.network.MessageTurretBase;
import omtteam.openmodularturrets.network.MessageUpdateTurret;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * The client side of OMT's server-to-client messages. The client proxy hands them to the network registration
 * ({@code CommonProxy.clientMessageHandler}); a dedicated server registers no-op handlers instead.
 */
@SideOnly(Side.CLIENT)
public final class ClientMessageHandlers {
    private static final Map<Class<?>, IMessageHandler<?, IMessage>> HANDLERS = new HashMap<>();

    static {
        register(MessageTurretBase.class, ClientMessageHandlers::turretBase);
        register(MessageUpdateTurret.class, ClientMessageHandlers::updateTurret);
    }

    private ClientMessageHandlers() {
    }

    @SuppressWarnings("unchecked")
    public static <M extends IMessage> IMessageHandler<M, IMessage> get(Class<M> type) {
        IMessageHandler<?, IMessage> handler = HANDLERS.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("No client handler for " + type.getName());
        }
        return (IMessageHandler<M, IMessage>) handler;
    }

    private static <M extends IMessage> void register(Class<M> type, IMessageHandler<M, IMessage> handler) {
        HANDLERS.put(type, handler);
    }

    private static IMessage turretBase(MessageTurretBase message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(message.getPos());
            if (tileEntity instanceof TurretBase) {
                message.applyTo((TurretBase) tileEntity);
            }
        });
        return null;
    }

    private static IMessage updateTurret(MessageUpdateTurret message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(message.getPos());
            if (tileEntity instanceof TurretTileEntity) {
                message.applyTo((TurretTileEntity) tileEntity);
            }
        });
        return null;
    }
}
