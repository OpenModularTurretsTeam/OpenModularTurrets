package openmodularturrets.handler;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import openmodularturrets.network.messages.*;
import openmodularturrets.reference.ModInfo;

public class NetworkingHandler {
    public final static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);

    public static void initNetworking() {


        INSTANCE.registerMessage(MessageAddTrustedPlayer.MessageHandlerAddTrustedPlayer.class,
                                 MessageAddTrustedPlayer.class, 0, Side.SERVER);

        INSTANCE.registerMessage(MessageRemoveTrustedPlayer.MessageHandlerRemoveTrustedPlayer.class,
                                 MessageRemoveTrustedPlayer.class, 1, Side.SERVER);

        INSTANCE.registerMessage(MessageToggleAttackMobs.MessageHandlerToggleAttackMobs.class,
                                 MessageToggleAttackMobs.class, 2, Side.SERVER);

        INSTANCE.registerMessage(MessageToggleAttackNeutralMobs.MessageHandlerToggleAttackNeutralMobs.class,
                                 MessageToggleAttackNeutralMobs.class, 3,
                                   Side.SERVER);

        INSTANCE.registerMessage(MessageToggleAttackPlayers.MessageHandlerToggleAttackPlayers.class,
                                 MessageToggleAttackPlayers.class, 4, Side.SERVER);

        INSTANCE.registerMessage(MessageSetTurretOwner.MessageHandlerSetTurretOwner.class, MessageSetTurretOwner.class,
                                 5, Side.SERVER);

        INSTANCE.registerMessage(MessageAdjustYAxisDetect.MessageHandlerAdjustYAxisDetect.class,
                                 MessageAdjustYAxisDetect.class, 7, Side.SERVER);

        INSTANCE.registerMessage(MessageDropTurrets.MessageHandlerDropTurrets.class, MessageDropTurrets.class, 8,
                                 Side.SERVER);

        INSTANCE.registerMessage(MessageDropBase.MessageHandlerDropBase.class, MessageDropBase.class, 9, Side.SERVER);

        INSTANCE.registerMessage(MessageModifyPermissions.MessageHandlerModifyPermissions.class,
                                 MessageModifyPermissions.class, 10, Side.SERVER);

        INSTANCE.registerMessage(MessageSetBaseTargetingType.MessageHandlerSetBaseTargetingType.class,
                                 MessageSetBaseTargetingType.class, 11, Side.SERVER);

        INSTANCE.registerMessage(MessageTurretBase.MessageHandlerTurretBase.class, MessageTurretBase.class, 12,
                                 Side.CLIENT);
    }
}
