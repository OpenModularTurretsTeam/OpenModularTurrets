package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.inventory.AmmoRules;

import java.util.Optional;

/**
 * A slot of a vanilla {@link Container} as a core {@link ItemSlot}.
 */
public record ContainerItemSlot(Container container, int index) implements ItemSlot {
    private ItemStack stack() {
        return container.getItem(index);
    }

    @Override
    public int getCount() {
        return stack().getCount();
    }

    @Override
    public boolean isAmmo(AmmoType type) {
        return !stack().isEmpty() && ItemClassifier.isAmmo(stack(), type);
    }

    @Override
    public int getDisposableDamageBonus() {
        return AmmoRules.getDisposableAmmoDamageBonus(stack());
    }

    @Override
    public boolean isReactorFuel(ReactorFuel fuel) {
        return !stack().isEmpty() && ItemClassifier.isReactorFuel(stack(), fuel);
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
        return stack().copyWithCount(1);
    }

    @Override
    public int extract(int amount) {
        return amount <= 0 ? 0 : container.removeItem(index, amount).getCount();
    }
}
