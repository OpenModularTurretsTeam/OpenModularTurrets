package omtteam.openmodularturrets.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.core.base.BaseTier;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.UpgradeItem;

/**
 * The 13 slots of a turret base (oldmod {@code TurretBase.setupInventory} / {@code setupSlotMap}):
 * <ul>
 *     <li>0–8: ammo (the only slots exposed to automation)</li>
 *     <li>9–10: addons (tier 2+)</li>
 *     <li>11–12: upgrades (slot 11 from tier 2, slot 12 on tier 5)</li>
 * </ul>
 * Which slots a tier may use is defined in {@link omtteam.openmodularturrets.core.base.BaseTier}.
 * Deliberately not implemented by the block entity, so vanilla hoppers go through the Transfer API exposure.
 */
public class TurretBaseInventory implements Container {
    public static final int SIZE = 13;
    public static final int AMMO_START = 0;
    public static final int AMMO_COUNT = 9;
    public static final int ADDON_START = 9;
    public static final int UPGRADE_START = 11;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private final Runnable onChange;
    private final StillValid stillValid;

    /** Distance check for menus. */
    @FunctionalInterface
    public interface StillValid {
        boolean test(Player player);
    }

    /**
     * @param onChange called after every change (the block entity saves and informs the base logic)
     */
    public TurretBaseInventory(Runnable onChange, StillValid stillValid) {
        this.onChange = onChange;
        this.stillValid = stillValid;
    }

    public static boolean isAmmoSlot(int slot) {
        return slot >= AMMO_START && slot < AMMO_START + AMMO_COUNT;
    }

    public static boolean isAddonSlot(int slot) {
        return slot >= ADDON_START && slot < UPGRADE_START;
    }

    public static boolean isUpgradeSlot(int slot) {
        return slot >= UPGRADE_START && slot < SIZE;
    }

    /**
     * Whether a slot exists on a base tier (oldmod {@code TurretBaseContainer} only adds the usable slots).
     */
    public static boolean isSlotUsable(int slot, int tier) {
        if (isAddonSlot(slot)) return slot - ADDON_START < BaseTier.addonSlots(tier);
        if (isUpgradeSlot(slot)) return slot - UPGRADE_START < BaseTier.upgradeSlots(tier);
        return isAmmoSlot(slot);
    }

    /**
     * What a player may put into a slot through the GUI (oldmod {@code AmmoSlot}, {@code AddonSlot},
     * {@code UpgradeSlot}).
     */
    public static boolean mayPlace(int slot, ItemStack stack) {
        if (isAmmoSlot(slot)) return AmmoRules.isValidAmmo(stack);
        if (isAddonSlot(slot)) return stack.getItem() instanceof AddonItem;
        if (isUpgradeSlot(slot)) return stack.getItem() instanceof UpgradeItem;
        return false;
    }

    /** Stack limit of a slot for an item (upgrades 4, damage amp / fake drops 4, other addons 1). */
    public static int getSlotLimit(int slot, ItemStack stack) {
        if (isUpgradeSlot(slot)) return UpgradeType.MAX_PER_SLOT;
        if (isAddonSlot(slot) && stack.getItem() instanceof AddonItem addon) return addon.getAddonType().getMaxPerSlot();
        return stack.getMaxStackSize();
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    /**
     * Automation may only insert valid ammo into ammo slots. Addon and upgrade slots are filled through the GUI,
     * whose slots do their own checks.
     */
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isAmmoSlot(slot) && AmmoRules.isValidAmmo(stack);
    }

    @Override
    public void setChanged() {
        onChange.run();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid.test(player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }
}
