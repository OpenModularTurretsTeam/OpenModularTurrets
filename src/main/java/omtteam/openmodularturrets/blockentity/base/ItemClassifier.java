package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.UpgradeItem;
import omtteam.openmodularturrets.registry.TurretRegistry;

import java.util.Optional;

/**
 * Maps item stacks to the core item types.
 */
public final class ItemClassifier {
    private ItemClassifier() {
    }

    /** See {@link TurretRegistry#isAmmo}. */
    public static boolean isAmmo(ItemStack stack, AmmoType type) {
        return TurretRegistry.isAmmo(stack, type);
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
