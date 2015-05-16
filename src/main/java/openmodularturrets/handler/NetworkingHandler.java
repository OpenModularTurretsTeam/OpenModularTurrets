package openmodularturrets.handler;

import openmodularturrets.network.AddTrustedPlayerMessage;
import openmodularturrets.network.AdjustYAxisDetectMessage;
import openmodularturrets.network.DropBaseMessage;
import openmodularturrets.network.DropTurretsMessage;
import openmodularturrets.network.RemoveTrustedPlayerMessage;
import openmodularturrets.network.SetTurretOwnerMessage;
import openmodularturrets.network.ToggleAttackMobsMessage;
import openmodularturrets.network.ToggleAttackNeutralMobsMessage;
import openmodularturrets.network.ToggleAttackPlayersMessage;
import openmodularturrets.reference.ModInfo;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkingHandler {

	public static SimpleNetworkWrapper initNetworking() {

		SimpleNetworkWrapper networking = new SimpleNetworkWrapper(ModInfo.ID);

		networking.registerMessage(AddTrustedPlayerMessage.class,
				AddTrustedPlayerMessage.class, 0, Side.SERVER);

		networking.registerMessage(RemoveTrustedPlayerMessage.class,
				RemoveTrustedPlayerMessage.class, 1, Side.SERVER);

		networking.registerMessage(ToggleAttackMobsMessage.class,
				ToggleAttackMobsMessage.class, 2, Side.SERVER);

		networking.registerMessage(ToggleAttackNeutralMobsMessage.class,
				ToggleAttackNeutralMobsMessage.class, 3, Side.SERVER);

		networking.registerMessage(ToggleAttackPlayersMessage.class,
				ToggleAttackPlayersMessage.class, 4, Side.SERVER);

		networking.registerMessage(SetTurretOwnerMessage.class,
				SetTurretOwnerMessage.class, 5, Side.SERVER);

		networking.registerMessage(AdjustYAxisDetectMessage.class,
				AdjustYAxisDetectMessage.class, 7, Side.SERVER);

		networking.registerMessage(DropTurretsMessage.class,
				DropTurretsMessage.class, 8, Side.SERVER);

		networking.registerMessage(DropBaseMessage.class,
				DropBaseMessage.class, 9, Side.SERVER);

		return networking;

	}
}
