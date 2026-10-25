package omtteam.openmodularturrets.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.reference.Reference;

public class OMTNetworkingHandler {
    public final static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    public static void initNetworking() {
        // BasePackets replace the 3.x base messages (MessageToggleAttack*, MessageAdjustRange, MessageDropTurrets,
        // MessageDropBase, MessageSetBaseTargetingType, MessageToggleMode on 0-6 and 8)
        INSTANCE.registerMessage(BasePacketHandler.SimpleActionHandler.class, BasePackets.SimpleAction.class, 0,
                                 Side.SERVER);
        INSTANCE.registerMessage(BasePacketHandler.SetRangeHandler.class, BasePackets.SetRange.class, 1, Side.SERVER);
        INSTANCE.registerMessage(BasePacketHandler.SetTargetingHandler.class, BasePackets.SetTargeting.class, 2,
                                 Side.SERVER);

        INSTANCE.registerMessage(OpenModularTurrets.proxy.clientMessageHandler(MessageTurretBase.class), MessageTurretBase.class,
                                 7, Side.CLIENT);

        // 9 and 10 were MessageAdjustLightValue / MessageAdjustLightOpacity (the camouflage menu of OMLib replaced them)

        INSTANCE.registerMessage(OpenModularTurrets.proxy.clientMessageHandler(MessageUpdateTurret.class), MessageUpdateTurret.class,
                                 11, Side.CLIENT);
    }
}
