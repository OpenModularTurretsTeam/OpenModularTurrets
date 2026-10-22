package omtteam.openmodularturrets.tileentity.base;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.config.DisposableAmmo;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.item.AddonMetaItem;
import omtteam.openmodularturrets.item.UpgradeMetaItem;

import java.util.Optional;

/**
 * What an item stack is for the core logic: ammo, reactor fuel, upgrade or addon (the 3.x item metadata).
 */
public final class ItemClassifier {
    /** {@code UpgradeMetaItem} meta 0–4. */
    private static final TurretBaseUpgradeType[] UPGRADE_BY_META = {TurretBaseUpgradeType.ACCURACY,
            TurretBaseUpgradeType.EFFICIENCY, TurretBaseUpgradeType.FIRE_RATE, TurretBaseUpgradeType.RANGE,
            TurretBaseUpgradeType.SCATTERSHOT};

    private ItemClassifier() {
    }

    /** Whether the stack is ammo of this type, by the type's {@code AmmoDefinition} (built-in or from another mod). */
    public static boolean isAmmo(ItemStack stack, AmmoType type) {
        return !stack.isEmpty() && TurretRegistry.ammo(type).map(ammo -> ammo.matches(stack)).orElse(false);
    }

    /** Damage bonus of an item on the config's ammo allow list, 0 for other items. */
    public static int getDisposableDamageBonus(ItemStack stack) {
        return stack.isEmpty() ? 0 : DisposableAmmo.getDamage(stack);
    }

    /** Redstone dust and redstone blocks feed the redstone reactor addon. */
    public static boolean isReactorFuel(ItemStack stack, ReactorFuel fuel) {
        if (stack.isEmpty()) return false;
        switch (fuel) {
            case REDSTONE:
                return stack.getItem() == Items.REDSTONE;
            case REDSTONE_BLOCK:
                return stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK);
            default:
                return false;
        }
    }

    /** The base upgrade of an {@code UpgradeMetaItem} stack. */
    public static Optional<TurretBaseUpgradeType> getUpgrade(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof UpgradeMetaItem)) return Optional.empty();
        int meta = stack.getMetadata();
        return meta >= 0 && meta < UPGRADE_BY_META.length ? Optional.of(UPGRADE_BY_META[meta]) : Optional.empty();
    }

    /** The addon of an {@code AddonMetaItem} stack; {@link AddonType} is in metadata order. */
    public static Optional<AddonType> getAddon(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof AddonMetaItem)) return Optional.empty();
        int meta = stack.getMetadata();
        AddonType[] addons = AddonType.values();
        return meta >= 0 && meta < addons.length ? Optional.of(addons[meta]) : Optional.empty();
    }
}
