package omtteam.openmodularturrets.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import omtteam.openmodularturrets.core.base.BaseTier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The 13 slots of a turret base (1.20.1+ {@code inventory.TurretBaseInventory}, 3.x layout):
 * <ul>
 *     <li>0–8: ammo (the only slots other blocks may use)</li>
 *     <li>9–10: addons (tier 2+)</li>
 *     <li>11–12: upgrades (slot 11 from tier 2, slot 12 on tier 5)</li>
 * </ul>
 * Which slots a tier may use is defined in {@link BaseTier}.
 */
public class TurretBaseInventory extends ItemStackHandler {
    public static final int SIZE = 13;
    public static final int AMMO_START = 0;
    public static final int AMMO_COUNT = 9;
    public static final int ADDON_START = 9;
    public static final int UPGRADE_START = 11;

    private final Runnable onChange;

    /** @param onChange called after every change of a slot (the base informs its logic) */
    public TurretBaseInventory(Runnable onChange) {
        super(SIZE);
        this.onChange = onChange;
    }

    /** Automation inserts only ammo, into the ammo slots. */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index < AMMO_START + AMMO_COUNT) {
            return AmmoRules.isItemStackValidAmmo(stack);
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isItemValidForSlot(slot, stack))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        onChange.run();
    }

    /** The ammo slots, the part of the inventory other blocks may use. */
    public IItemHandler getAmmoSlots() {
        return new RangedWrapper(this, AMMO_START, AMMO_START + AMMO_COUNT);
    }

    /** The slots a base tier may use, by kind (the GUI only shows these). */
    public static HashMap<EnumSlotType, List<Integer>> slotMap(int tier) {
        HashMap<EnumSlotType, List<Integer>> slotMap = new HashMap<>();
        List<Integer> ammoList = new ArrayList<>();
        List<Integer> addonList = new ArrayList<>();
        List<Integer> upgradeList = new ArrayList<>();
        for (int i = 0; i < AMMO_COUNT; i++) {
            ammoList.add(AMMO_START + i);
        }
        for (int i = 0; i < BaseTier.addonSlots(tier); i++) {
            addonList.add(ADDON_START + i);
        }
        for (int i = 0; i < BaseTier.upgradeSlots(tier); i++) {
            upgradeList.add(UPGRADE_START + i);
        }
        slotMap.put(EnumSlotType.AmmoSlot, ammoList);
        slotMap.put(EnumSlotType.AddonSlot, addonList);
        slotMap.put(EnumSlotType.UpgradeSlot, upgradeList);
        return slotMap;
    }
}
