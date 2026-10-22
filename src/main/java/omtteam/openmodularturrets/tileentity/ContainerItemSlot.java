package omtteam.openmodularturrets.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;

import java.util.Optional;

/**
 * A slot of a Forge {@link IItemHandler} as a core {@link ItemSlot} (a record in 1.20.1+).
 */
public final class ContainerItemSlot implements ItemSlot {
    private final IItemHandler inventory;
    private final int index;

    public ContainerItemSlot(IItemHandler inventory, int index) {
        this.inventory = inventory;
        this.index = index;
    }

    private ItemStack stack() {
        return inventory.getStackInSlot(index);
    }

    @Override
    public int getCount() {
        return stack().getCount();
    }

    @Override
    public boolean isAmmo(AmmoType type) {
        return ItemClassifier.isAmmo(stack(), type);
    }

    @Override
    public int getDisposableDamageBonus() {
        return ItemClassifier.getDisposableDamageBonus(stack());
    }

    @Override
    public boolean isReactorFuel(ReactorFuel fuel) {
        return ItemClassifier.isReactorFuel(stack(), fuel);
    }

    @Override
    public Optional<TurretBaseUpgradeType> getUpgrade() {
        return ItemClassifier.getUpgrade(stack());
    }

    @Override
    public Optional<AddonType> getAddon() {
        return ItemClassifier.getAddon(stack());
    }

    /** A one-item copy of the stack. */
    @Override
    public Object snapshot() {
        ItemStack copy = stack().copy();
        copy.setCount(1);
        return copy;
    }

    @Override
    public int extract(int amount) {
        return amount <= 0 ? 0 : inventory.extractItem(index, amount, false).getCount();
    }
}
