package omtteam.openmodularturrets.network;

import omtteam.openmodularturrets.OMT;
import omtteam.omlib.compat.network.NetworkCompat;
import omtteam.omlib.compat.network.PacketType;

/**
 * All packet types of the mod. Client handlers are registered with the client platform services, see
 * {@code ClientNetworkCompat}.
 */
public final class OMTPackets {
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

    private OMTPackets() {
    }

    /** Loads the class, registering all packet types. */
    public static void init() {
    }
}
