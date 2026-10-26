package omtteam.openmodularturrets.network;

import omtteam.omlib.network.TrustPacketHandler;
import omtteam.omlib.network.TrustPackets;
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
