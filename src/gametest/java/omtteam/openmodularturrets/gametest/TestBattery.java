package omtteam.openmodularturrets.gametest;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import omtteam.omlib.compat.VersionCompat;

import java.util.Objects;

/**
 * An item that stores energy, for the energy drain turret's tests (the mods have none of their own). Its energy is
 * registered per loader in {@code platform/<loader>}: Team Reborn Energy's {@code SimpleEnergyItem} storage on
 * Fabric, NeoForge's {@code ComponentEnergyStorage} with {@link Energy#COMPONENT} on NeoForge. Tests charge and read it
 * through OMLib's {@code Energy.itemEnergy}, as the turret does.
 */
public final class TestBattery {
    /** Capacity and transfer limit per operation, in RF. */
    public static final int CAPACITY = 100_000;
    public static final Item ITEM = register();

    private TestBattery() {
    }

    private static Item register() {
        ResourceLocation id = id("test_battery");
        return Registry.register(BuiltInRegistries.ITEM, id,
                new Item(VersionCompat.itemProperties(new Item.Properties().stacksTo(1), id)));
    }

    static ResourceLocation id(String path) {
        return Objects.requireNonNull(ResourceLocation.tryParse(ApiTestTurrets.NAMESPACE + ":" + path));
    }

    /** Loads the class, registering the item. */
    public static void init() {
    }

    /** The data component that holds the battery's energy on NeoForge (a registration step of its own). */
    public static final class Energy {
        public static final DataComponentType<Integer> COMPONENT = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE, id("test_energy"),
                DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

        private Energy() {
        }

        /** Loads the class, registering the component. */
        public static void init() {
        }
    }
}
