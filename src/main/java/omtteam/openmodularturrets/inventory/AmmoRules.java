package omtteam.openmodularturrets.inventory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.item.AmmoItem;

import java.util.Map;
import java.util.Set;

/**
 * Which items may go into the ammo slots of a turret base. Ported from 1.12.2 {@code OMTUtil.isItemStackValidAmmo}
 * and {@code AmmoList}.
 */
public final class AmmoRules {
    /** Vanilla items that some turret uses as ammo or fuel. */
    private static final Set<Item> BUILTIN_AMMO = Set.of(Items.POTATO, Items.POISONOUS_POTATO, Items.REDSTONE, Items.REDSTONE_BLOCK);

    private AmmoRules() {
    }

    public static boolean isValidAmmo(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return !OMTConfig.get().general.useWhitelistForAmmo
                || BUILTIN_AMMO.contains(stack.getItem())
                || stack.getItem() instanceof AmmoItem
                || getDisposableAmmoDamageBonus(stack) > 0;
    }

    /**
     * Damage bonus of a stack when fired by the disposable item turret ({@code general.ammoAllowList}), 0 if the
     * item is not on the list.
     */
    public static int getDisposableAmmoDamageBonus(ItemStack stack) {
        ConfigData.General general = OMTConfig.get().general;
        Integer bonus = general.ammoItemDamageBonus().get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        if (bonus != null) return bonus;
        for (Map.Entry<TagKey<Item>, Integer> entry : general.ammoTagDamageBonus().entrySet()) {
            if (stack.is(entry.getKey())) return entry.getValue();
        }
        return 0;
    }
}
