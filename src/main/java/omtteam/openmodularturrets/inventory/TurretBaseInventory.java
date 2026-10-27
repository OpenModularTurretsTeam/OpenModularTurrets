package omtteam.openmodularturrets.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import omtteam.omlib.inventory.LargeStackContainer;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.base.BaseTier;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.TurretBaseUpgradeItem;
import omtteam.openmodularturrets.item.UpgradeItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * The 13 slots of a turret base (1.12.2 {@code TurretBase.setupInventory} / {@code setupSlotMap}):
 * <ul>
 *     <li>0–8: ammo (the only slots exposed to automation)</li>
 *     <li>9–10: addons (tier 2+)</li>
 *     <li>11–12: upgrades (slot 11 from tier 2, slot 12 on tier 5)</li>
 * </ul>
 * Which slots a tier may use is defined in {@link omtteam.openmodularturrets.core.base.BaseTier}. An ammo slot holds
 * one stack plus the stacks of the attached inventory expanders ({@link SlotCapacity}); the base menu's client side
 * uses this class too, with the capacity the server syncs.
 * Deliberately not implemented by the block entity, so vanilla hoppers go through the Transfer API exposure.
 */
public class TurretBaseInventory implements LargeStackContainer {
    public static final int SIZE = 13;
    public static final int AMMO_START = 0;
    public static final int AMMO_COUNT = 9;
    public static final int ADDON_START = 9;
    public static final int UPGRADE_START = 11;
    /** Max stack size of most items, the limit {@link #getSlotLimit} gives for an empty stack. */
    private static final int DEFAULT_STACK = 64;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private final Runnable onChange;
    private final StillValid stillValid;
    private final Supplier<SlotCapacity> capacity;

    /** Distance check for menus. */
    @FunctionalInterface
    public interface StillValid {
        boolean test(Player player);
    }

    /**
     * @param onChange called after every change (the block entity saves and informs the base logic)
     * @param capacity what the attached expanders add to the ammo slots, asked on every limit check
     */
    public TurretBaseInventory(Runnable onChange, StillValid stillValid, Supplier<SlotCapacity> capacity) {
        this.onChange = onChange;
        this.stillValid = stillValid;
        this.capacity = capacity;
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
     * Whether a slot exists on a base tier (1.12.2 {@code TurretBaseContainer} only adds the usable slots).
     */
    public static boolean isSlotUsable(int slot, int tier) {
        if (isAddonSlot(slot)) return slot - ADDON_START < BaseTier.addonSlots(tier);
        if (isUpgradeSlot(slot)) return slot - UPGRADE_START < BaseTier.upgradeSlots(tier);
        return isAmmoSlot(slot);
    }

    /**
     * What a player may put into a slot through the GUI (1.12.2 {@code AmmoSlot}, {@code AddonSlot},
     * {@code UpgradeSlot}).
     */
    public static boolean mayPlace(int slot, ItemStack stack) {
        if (isAmmoSlot(slot)) return AmmoRules.isValidAmmo(stack);
        if (isAddonSlot(slot)) return stack.getItem() instanceof AddonItem;
        if (isUpgradeSlot(slot)) return stack.getItem() instanceof TurretBaseUpgradeItem;
        return false;
    }

    /**
     * Stack limit of a slot for an item without expanders (upgrades 4, damage amp / fake drops 4, other addons 1,
     * ammo one stack).
     */
    public static int getBaseSlotLimit(int slot, ItemStack stack) {
        if (isUpgradeSlot(slot) && stack.getItem() instanceof UpgradeItem upgrade) return upgrade.getUpgradeType().maxPerSlot();
        if (isAddonSlot(slot) && stack.getItem() instanceof AddonItem addon) return addon.getAddonType().getMaxPerSlot();
        return stack.getMaxStackSize();
    }

    /** {@link #getBaseSlotLimit}, with the stacks of the attached expanders in the ammo slots. */
    @Override
    public int getSlotLimit(int slot, ItemStack stack) {
        if (!isAmmoSlot(slot)) return getBaseSlotLimit(slot, stack);
        return capacity.get().limit(stack.isEmpty() ? DEFAULT_STACK : stack.getMaxStackSize());
    }

    /**
     * Sorts the ammo slots (the GUI's sort button): equal items merge into as few slots as their limit allows, then
     * Minecraft's items come first, Open Modular Turrets' next and other mods' last, each ordered by registry name;
     * empty slots end up last. Items of one kind with different data keep their order. Leaves the slots as they are if
     * the merged items would need more than {@link #AMMO_COUNT} slots (slots above their limit after an expander was
     * removed).
     */
    public void sortAmmo() {
        List<AmmoKind> kinds = new ArrayList<>();
        for (int slot = AMMO_START; slot < AMMO_START + AMMO_COUNT; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;
            AmmoKind kind = kinds.stream().filter(k -> sameItem(k.item, stack)).findFirst().orElse(null);
            if (kind == null) {
                kinds.add(new AmmoKind(stack.copyWithCount(1), stack.getCount()));
            } else {
                kind.count += stack.getCount();
            }
        }
        kinds.sort(Comparator.comparingInt((AmmoKind kind) -> namespaceRank(kind.item)).thenComparing(kind -> itemName(kind.item)));
        List<ItemStack> sorted = new ArrayList<>();
        for (AmmoKind kind : kinds) {
            int limit = getSlotLimit(AMMO_START, kind.item);
            for (long left = kind.count; left > 0; left -= limit) {
                sorted.add(kind.item.copyWithCount((int) Math.min(left, limit)));
            }
        }
        if (sorted.size() > AMMO_COUNT) return;
        for (int i = 0; i < AMMO_COUNT; i++) {
            items.set(AMMO_START + i, i < sorted.size() ? sorted.get(i) : ItemStack.EMPTY);
        }
        setChanged();
    }

    /** One kind of ammo while sorting: the item with its data, and how many there are in all slots. */
    private static final class AmmoKind {
        private final ItemStack item;
        private long count;

        private AmmoKind(ItemStack item, long count) {
            this.item = item;
            this.count = count;
        }
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        //? if >=1.21 {
        return ItemStack.isSameItemSameComponents(a, b);
        //?} else {
        /*return ItemStack.isSameItemSameTags(a, b);
        *///?}
    }

    /** 0 for Minecraft's items, 1 for Open Modular Turrets', 2 for other mods'. */
    private static int namespaceRank(ItemStack stack) {
        String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (namespace.equals("minecraft")) return 0;
        return namespace.equals(OMT.MOD_ID) ? 1 : 2;
    }

    private static String itemName(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
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
    public @NotNull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
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
