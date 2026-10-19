package omtteam.openmodularturrets.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
//? if fabric {
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//?} else {
/*import net.neoforged.neoforge.common.util.FakePlayerFactory;
*///?}
//? if >=1.21 {
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

import java.util.function.Consumer;

/**
 * Hides differences between the supported Minecraft versions.
 * Version-conditional (Stonecutter) code should live here instead of in gameplay code.
 * <p>
 * Methods taking a {@link HolderLookup.Provider} need it on 1.21+; on 1.20.1 it is ignored.
 */
public final class VersionCompat {
    private VersionCompat() {
    }

    // ---- Resource locations ----

    public static ResourceLocation id(String namespace, String path) {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new ResourceLocation(namespace, path);
        *///?}
    }

    // ---- Entities ----

    /** Sets an entity on fire for the given number of seconds. */
    public static void setOnFire(Entity entity, int seconds) {
        //? if >=1.21 {
        entity.igniteForSeconds(seconds);
        //?} else {
        /*entity.setSecondsOnFire(seconds);
        *///?}
    }

    // ---- Enchantments ----

    /** Maximum level of the Looting enchantment (data-driven on 1.21, so a datapack may change it). */
    public static int maxLootingLevel(Level level) {
        //? if >=1.21 {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.LOOTING)
                .value().getMaxLevel();
        //?} else {
        /*return Enchantments.MOB_LOOTING.getMaxLevel();
        *///?}
    }

    /** Adds Looting at the given level to a stack. */
    public static void enchantLooting(ItemStack stack, Level level, int lootingLevel) {
        //? if >=1.21 {
        stack.enchant(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.LOOTING),
                lootingLevel);
        //?} else {
        /*stack.enchant(Enchantments.MOB_LOOTING, lootingLevel);
        *///?}
    }

    // ---- ItemStack NBT ----

    /**
     * Writes a slot list to {@code tag} under {@code "Items"} (vanilla container format).
     */
    public static void saveItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        //? if >=1.21 {
        ContainerHelper.saveAllItems(tag, items, registries);
        //?} else {
        /*ContainerHelper.saveAllItems(tag, items);
        *///?}
    }

    /**
     * Reads a slot list written by {@link #saveItems}. {@code items} must already have the correct size.
     */
    public static void loadItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        //? if >=1.21 {
        ContainerHelper.loadAllItems(tag, items, registries);
        //?} else {
        /*ContainerHelper.loadAllItems(tag, items);
        *///?}
    }

    // ---- Custom item data (NBT on 1.20.1, CUSTOM_DATA component on 1.21+) ----

    /**
     * Returns a <b>copy</b> of the custom data of a stack (empty tag if none). Use {@link #setCustomData}
     * or {@link #updateCustomData} to change it.
     */
    public static CompoundTag getCustomData(ItemStack stack) {
        //? if >=1.21 {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        //?} else {
        /*CompoundTag tag = stack.getTag();
        return tag == null ? new CompoundTag() : tag.copy();
        *///?}
    }

    /**
     * Replaces the custom data of a stack. An empty tag removes it.
     */
    public static void setCustomData(ItemStack stack, CompoundTag tag) {
        //? if >=1.21 {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        //?} else {
        /*stack.setTag(tag.isEmpty() ? null : tag.copy());
        *///?}
    }

    /**
     * Modifies the custom data of a stack in place.
     */
    public static void updateCustomData(ItemStack stack, Consumer<CompoundTag> updater) {
        CompoundTag tag = getCustomData(stack);
        updater.accept(tag);
        setCustomData(stack, tag);
    }

    // ---- Loader differences ----

    /**
     * Builder of a mod creative tab. Fabric places mod tabs on its own tab pages ({@code FabricItemGroup}); NeoForge
     * sorts mod tabs itself.
     */
    public static CreativeModeTab.Builder creativeTabBuilder() {
        //? if fabric {
        return FabricItemGroup.builder();
        //?} else {
        /*return CreativeModeTab.builder();
        *///?}
    }

    /**
     * The loader's fake player of this level for the profile (one per level and profile, created on first use).
     */
    public static ServerPlayer fakePlayer(ServerLevel level, GameProfile profile) {
        //? if fabric {
        return FakePlayer.get(level, profile);
        //?} else {
        /*return FakePlayerFactory.get(level, profile);
        *///?}
    }
}
