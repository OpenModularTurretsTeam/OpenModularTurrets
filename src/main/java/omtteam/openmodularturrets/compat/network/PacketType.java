package omtteam.openmodularturrets.compat.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//?}

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A registered packet type. Packets are plain objects (usually records) with an encoder and a decoder;
 * the version-specific transport (raw channel on 1.20.1, {@code CustomPacketPayload} on 1.21+) is hidden here.
 * Create instances through {@link NetworkCompat}.
 */
public final class PacketType<T> {
    private final ResourceLocation id;
    private final BiConsumer<FriendlyByteBuf, T> encoder;
    private final Function<FriendlyByteBuf, T> decoder;
    //? if >=1.21 {
    private final CustomPacketPayload.Type<Payload<T>> payloadType;
    private final StreamCodec<RegistryFriendlyByteBuf, Payload<T>> codec;
    //?}

    PacketType(ResourceLocation id, BiConsumer<FriendlyByteBuf, T> encoder, Function<FriendlyByteBuf, T> decoder) {
        this.id = id;
        this.encoder = encoder;
        this.decoder = decoder;
        //? if >=1.21 {
        this.payloadType = new CustomPacketPayload.Type<>(id);
        this.codec = StreamCodec.of((buf, payload) -> encoder.accept(buf, payload.packet()),
                buf -> new Payload<>(this, decoder.apply(buf)));
        //?}
    }

    public ResourceLocation id() {
        return id;
    }

    public void encode(FriendlyByteBuf buf, T packet) {
        encoder.accept(buf, packet);
    }

    public T decode(FriendlyByteBuf buf) {
        return decoder.apply(buf);
    }

    //? if >=1.21 {
    public CustomPacketPayload.Type<Payload<T>> payloadType() {
        return payloadType;
    }

    public StreamCodec<RegistryFriendlyByteBuf, Payload<T>> codec() {
        return codec;
    }

    public Payload<T> wrap(T packet) {
        return new Payload<>(this, packet);
    }

    /**
     * Carrier for a packet of a given {@link PacketType} on 1.21+.
     */
    public record Payload<T>(PacketType<T> packetType, T packet) implements CustomPacketPayload {
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return packetType.payloadType;
        }
    }
    //?}
}
