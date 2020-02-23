package omtteam.openmodularturrets.handler;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import omtteam.openmodularturrets.network.messages.*;
import omtteam.openmodularturrets.reference.Reference;

public class OMTNetworkingHandler {
    public final static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    public static void initNetworking() {
        INSTANCE.registerMessage(MessageToggleAttackMobs.MessageHandlerToggleAttackMobs.class,
                                 MessageToggleAttackMobs.class, 0, Side.SERVER);

        INSTANCE.registerMessage(MessageToggleAttackNeutralMobs.MessageHandlerToggleAttackNeutralMobs.class,
                                 MessageToggleAttackNeutralMobs.class, 1, Side.SERVER);

        INSTANCE.registerMessage(MessageToggleAttackPlayers.MessageHandlerToggleAttackPlayers.class,
                                 MessageToggleAttackPlayers.class, 2, Side.SERVER);

        INSTANCE.registerMessage(MessageAdjustRange.MessageHandlerAdjustYAxisDetect.class,
                                 MessageAdjustRange.class, 3, Side.SERVER);

        INSTANCE.registerMessage(MessageDropTurrets.MessageHandlerDropTurrets.class, MessageDropTurrets.class,
                                 4, Side.SERVER);

        INSTANCE.registerMessage(MessageDropBase.MessageHandlerDropBase.class, MessageDropBase.class,
                                 5, Side.SERVER);

        INSTANCE.registerMessage(MessageSetBaseTargetingType.MessageHandlerSetBaseTargetingType.class,
                                 MessageSetBaseTargetingType.class, 6, Side.SERVER);

        INSTANCE.registerMessage(MessageTurretBase.MessageHandlerTurretBase.class, MessageTurretBase.class,
                                 7, Side.CLIENT);

        INSTANCE.registerMessage(MessageToggleMode.MessageHandlerToggleMode.class, MessageToggleMode.class,
                                 8, Side.SERVER);

        INSTANCE.registerMessage(MessageAdjustLightValue.MessageHandlerAdjustLightValue.class,
                                 MessageAdjustLightValue.class, 9, Side.SERVER);

        INSTANCE.registerMessage(MessageAdjustLightOpacity.MessageHandlerAdjustLightOpacity.class,
                                 MessageAdjustLightOpacity.class, 10, Side.SERVER);

        INSTANCE.registerMessage(MessageUpdateTurret.MessageHandlerUpdateTurret.class, MessageUpdateTurret.class,
                                 11, Side.CLIENT);
    }
}
