package omtteam.openmodularturrets.gametest;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import omtteam.omlib.compat.VersionCompat;

import java.util.Objects;

/**
 * An item that stores energy, for the energy drain turret's tests (the mods have none of their own). Its energy is
 * registered per loader in {@code platform/<loader>}: Team Reborn Energy's {@code SimpleEnergyItem} storage on
 * Fabric, an energy capability kept in the stack's NBT on Forge ({@code ForgeTestBatteryEnergy}). Tests charge and read
 * it through OMLib's {@code Energy.itemEnergy}, as the turret does.
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

    public static ResourceLocation id(String path) {
        return Objects.requireNonNull(ResourceLocation.tryParse(ApiTestTurrets.NAMESPACE + ":" + path));
    }

    /** Loads the class, registering the item. */
    public static void init() {
    }
}
