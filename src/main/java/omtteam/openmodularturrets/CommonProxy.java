package omtteam.openmodularturrets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.handler.OMTEventHandler;
import omtteam.openmodularturrets.network.OMTNetworkingHandler;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModEntities;
import omtteam.openmodularturrets.util.OMTFakePlayer;

import javax.annotation.Nullable;

public class CommonProxy {
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(OMTEventHandler.getInstance());
        ModBlocks.initTileEntities();
        ModEntities.registerProjectiles(OpenModularTurrets.instance);
        initHandlers();
    }

    protected void initTileRenderers() {

    }

    public void initModelLoaders() {

    }

    protected void initEntityRenderers() {

    }

    protected void initHandlers() {
        OMTNetworkingHandler.initNetworking();
    }

    public void init() {
        initEntityRenderers();
        OMTFakePlayer.init();
    }

    public void postInit() {
        OMTConfig.parseLists();
    }

    /** The handler of a server-to-client message: the client proxy supplies the one in client.network, a server none. */
    public <M extends IMessage> IMessageHandler<M, IMessage> clientMessageHandler(Class<M> type) {
        return (message, ctx) -> null;
    }

    /** The GUI screen for {@code OMTGuiHandler}; screens only exist on the client. */
    @Nullable
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    /** Calls the client plugins of other mods (turret models); only the client proxy has some. */
    public void loadClientPlugins(ASMDataTable asmData) {
    }
}
