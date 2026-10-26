package omtteam.openmodularturrets.network;

import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.compat.network.NetworkCompat;
import omtteam.openmodularturrets.compat.network.PacketType;

/**
 * All packet types of the mod. Client handlers are registered in
 * {@code omtteam.openmodularturrets.client.network.ClientPacketHandlers}.
 */
public final class OMTPackets {
    public static final PacketType<ConfigSyncPacket> CONFIG_SYNC =
            NetworkCompat.registerS2C(OMT.id("config_sync"), ConfigSyncPacket::write, ConfigSyncPacket::read);

    public static final PacketType<RayPacket> RAY =
            NetworkCompat.registerS2C(OMT.id("ray"), RayPacket::write, RayPacket::read);

    // Trusted player screen
    public static final PacketType<TrustPackets.Add> ADD_TRUSTED_PLAYER = NetworkCompat.registerC2S(
            OMT.id("add_trusted_player"), TrustPackets.Add::write, TrustPackets.Add::read,
            TrustPacketHandler::handleAdd);
    public static final PacketType<TrustPackets.Remove> REMOVE_TRUSTED_PLAYER = NetworkCompat.registerC2S(
            OMT.id("remove_trusted_player"), TrustPackets.Remove::write, TrustPackets.Remove::read,
            TrustPacketHandler::handleRemove);
    public static final PacketType<TrustPackets.ChangeAccess> CHANGE_ACCESS_LEVEL = NetworkCompat.registerC2S(
            OMT.id("change_access_level"), TrustPackets.ChangeAccess::write, TrustPackets.ChangeAccess::read,
            TrustPacketHandler::handleChangeAccess);
    public static final PacketType<TrustPackets.Disown> DISOWN = NetworkCompat.registerC2S(
            OMT.id("disown"), TrustPackets.Disown::write, TrustPackets.Disown::read,
            TrustPacketHandler::handleDisown);

    // Base and configure screens
    public static final PacketType<BasePackets.SimpleAction> BASE_ACTION = NetworkCompat.registerC2S(
            OMT.id("base_action"), BasePackets.SimpleAction::write, BasePackets.SimpleAction::read,
            BasePacketHandler::handleAction);
    public static final PacketType<BasePackets.SetRange> SET_RANGE = NetworkCompat.registerC2S(
            OMT.id("set_range"), BasePackets.SetRange::write, BasePackets.SetRange::read,
            BasePacketHandler::handleSetRange);
    public static final PacketType<BasePackets.SetTargeting> SET_TARGETING = NetworkCompat.registerC2S(
            OMT.id("set_targeting"), BasePackets.SetTargeting::write, BasePackets.SetTargeting::read,
            BasePacketHandler::handleSetTargeting);

    // Camouflage menu
    public static final PacketType<CamoPackets.SetLight> CAMO_LIGHT = NetworkCompat.registerC2S(
            OMT.id("camo_light"), CamoPackets.SetLight::write, CamoPackets.SetLight::read,
            CamoPacketHandler::handleSetLight);

    public static final PacketType<CamoPackets.OpenMenu> OPEN_CAMO_MENU = NetworkCompat.registerC2S(
            OMT.id("open_camo_menu"), CamoPackets.OpenMenu::write, CamoPackets.OpenMenu::read,
            CamoPacketHandler::handleOpenMenu);

    public static final PacketType<CamoPackets.Back> CAMO_BACK = NetworkCompat.registerC2S(
            OMT.id("camo_back"), CamoPackets.Back::write, CamoPackets.Back::read,
            CamoPacketHandler::handleBack);

    private OMTPackets() {
    }

    /** Loads the class, registering all packet types. */
    public static void init() {
    }
}
