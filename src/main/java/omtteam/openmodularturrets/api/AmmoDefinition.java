package omtteam.openmodularturrets.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.core.turret.AmmoType;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Which items count as an {@link AmmoType} (1.20.1+ {@code api.AmmoDefinition}). Items matching any ammo definition may
 * go into the ammo slots of a base; the turret tooltip names the ammo with {@link #translationKey()}.
 */
public final class AmmoDefinition {
    private final AmmoType type;
    private final Predicate<ItemStack> matcher;
    private final Supplier<String> translationKey;
    private final Supplier<ItemStack> sample;

    /**
     * @param matcher        whether a stack is this ammo
     * @param translationKey shown in turret tooltips ("Ammo: …")
     * @param sample         a stack of this ammo (for tests and tools)
     */
    public AmmoDefinition(AmmoType type, Predicate<ItemStack> matcher, String translationKey,
                          Supplier<ItemStack> sample) {
        this(type, matcher, constant(Objects.requireNonNull(translationKey, "translationKey")), sample);
    }

    private AmmoDefinition(AmmoType type, Predicate<ItemStack> matcher, Supplier<String> translationKey,
                           Supplier<ItemStack> sample) {
        this.type = Objects.requireNonNull(type, "type");
        this.matcher = Objects.requireNonNull(matcher, "matcher");
        this.translationKey = translationKey;
        this.sample = Objects.requireNonNull(sample, "sample");
    }

    private static Supplier<String> constant(String value) {
        return () -> value;
    }

    /**
     * Ammo that is exactly one item with one metadata value, named after it. The supplier is only called after
     * registration, so it may point to an item that is registered later.
     */
    public static AmmoDefinition ofItem(AmmoType type, Supplier<Item> item, int meta) {
        Objects.requireNonNull(item, "item");
        Supplier<ItemStack> sample = () -> new ItemStack(item.get(), 1, meta);
        return new AmmoDefinition(type, stack -> stack.getItem() == item.get() && stack.getMetadata() == meta,
                () -> sample.get().getTranslationKey() + ".name", sample);
    }

    public AmmoType type() {
        return type;
    }

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && matcher.test(stack);
    }

    public String translationKey() {
        return translationKey.get();
    }

    public ItemStack sample() {
        return sample.get();
    }
}
