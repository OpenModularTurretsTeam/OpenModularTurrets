package omtteam.openmodularturrets.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

/**
 * Small helpers around vanilla APIs that changed between Minecraft versions, so gameplay code does not need to
 * change when they move again.
 */
public final class VersionCompat {
    private VersionCompat() {
    }

    // ---- Identifiers ----

    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    // ---- Entities ----

    /** Sets an entity on fire for the given number of seconds. */
    public static void setOnFire(Entity entity, int seconds) {
        entity.igniteForSeconds(seconds);
    }

    // ---- Enchantments ----

    /** Maximum level of the Looting enchantment (data-driven, so a datapack may change it). */
    public static int maxLootingLevel(Level level) {
        return looting(level).value().getMaxLevel();
    }

    /** Adds Looting at the given level to a stack. */
    public static void enchantLooting(ItemStack stack, Level level, int lootingLevel) {
        stack.enchant(looting(level), lootingLevel);
    }

    private static Holder<Enchantment> looting(Level level) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
    }
}
