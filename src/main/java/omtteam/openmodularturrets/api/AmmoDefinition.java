package omtteam.openmodularturrets.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import omtteam.openmodularturrets.core.turret.AmmoType;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Which items count as an {@link AmmoType}. Items matching any ammo definition may go into the ammo slots of a base.
 * <p>
 * An {@link omtteam.openmodularturrets.item.AmmoItem} created with an ammo type counts as that type without a
 * definition; register one anyway to get a proper name in the turret tooltip.
 *
 * @param matcher     whether a stack is this ammo
 * @param displayName shown in turret tooltips ("Ammo: …")
 * @param sample      a stack of this ammo (for tests and tools)
 */
public record AmmoDefinition(AmmoType type, Predicate<ItemStack> matcher, Supplier<Component> displayName,
                             Supplier<ItemStack> sample) {
    public AmmoDefinition {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(matcher, "matcher");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(sample, "sample");
    }

    /**
     * Ammo that is exactly one item, named after it. The supplier is only called after registration, so it may
     * point to an item that is registered later.
     */
    public static AmmoDefinition ofItem(AmmoType type, Supplier<? extends ItemLike> item) {
        return new AmmoDefinition(type, stack -> stack.is(item.get().asItem()),
                () -> Component.translatable(item.get().asItem().getDescriptionId()), () -> new ItemStack(item.get()));
    }

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && matcher.test(stack);
    }
}
