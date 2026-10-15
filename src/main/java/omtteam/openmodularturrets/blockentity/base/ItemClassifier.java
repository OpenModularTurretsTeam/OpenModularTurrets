package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.inventory.AmmoRules;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.AmmoItem;
import omtteam.openmodularturrets.item.UpgradeItem;

import java.util.Optional;

/**
 * Maps item stacks to the core item types.
 */
public final class ItemClassifier {
    private ItemClassifier() {
    }

    public static boolean isAmmo(ItemStack stack, AmmoType type) {
        return switch (type) {
            case POTATO -> stack.is(Items.POTATO) || stack.is(Items.POISONOUS_POTATO);
            case DISPOSABLE -> AmmoRules.getDisposableAmmoDamageBonus(stack) > 0;
            case BLAZING_CLAY, BOLT, BULLET, FERRO_SLUG, GRENADE, ROCKET ->
                    stack.getItem() instanceof AmmoItem ammo && ammo.getAmmoType() == type;
        };
    }

    public static boolean isReactorFuel(ItemStack stack, ReactorFuel fuel) {
        return switch (fuel) {
            case REDSTONE -> stack.is(Items.REDSTONE);
            case REDSTONE_BLOCK -> stack.is(Items.REDSTONE_BLOCK);
        };
    }

    public static Optional<UpgradeType> getUpgrade(ItemStack stack) {
        return stack.getItem() instanceof UpgradeItem upgrade ? Optional.of(upgrade.getUpgradeType()) : Optional.empty();
    }

    public static Optional<AddonType> getAddon(ItemStack stack) {
        return stack.getItem() instanceof AddonItem addon ? Optional.of(addon.getAddonType()) : Optional.empty();
    }
}
