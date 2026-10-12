package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;

import java.util.Optional;

/** Test slot holding a stack of one kind of item. */
final class FakeSlot implements ItemSlot {
    AmmoType ammo;
    int disposableBonus;
    ReactorFuel fuel;
    UpgradeType upgrade;
    AddonType addon;
    int count;

    static FakeSlot empty() {
        return new FakeSlot();
    }

    static FakeSlot ammo(AmmoType type, int count) {
        FakeSlot slot = new FakeSlot();
        slot.ammo = type;
        slot.count = count;
        return slot;
    }

    static FakeSlot disposable(int bonus, int count) {
        FakeSlot slot = ammo(AmmoType.DISPOSABLE, count);
        slot.disposableBonus = bonus;
        return slot;
    }

    static FakeSlot fuel(ReactorFuel fuel, int count) {
        FakeSlot slot = new FakeSlot();
        slot.fuel = fuel;
        slot.count = count;
        return slot;
    }

    static FakeSlot upgrade(UpgradeType type, int count) {
        FakeSlot slot = new FakeSlot();
        slot.upgrade = type;
        slot.count = count;
        return slot;
    }

    static FakeSlot addon(AddonType type, int count) {
        FakeSlot slot = new FakeSlot();
        slot.addon = type;
        slot.count = count;
        return slot;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isAmmo(AmmoType type) {
        return count > 0 && ammo == type;
    }

    @Override
    public int getDisposableDamageBonus() {
        return disposableBonus;
    }

    @Override
    public boolean isReactorFuel(ReactorFuel fuel) {
        return count > 0 && this.fuel == fuel;
    }

    @Override
    public Optional<UpgradeType> getUpgrade() {
        return Optional.ofNullable(upgrade);
    }

    @Override
    public Optional<AddonType> getAddon() {
        return Optional.ofNullable(addon);
    }

    @Override
    public Object snapshot() {
        return ammo != null ? ammo : fuel;
    }

    @Override
    public int extract(int amount) {
        int removed = Math.min(amount, count);
        count -= removed;
        return removed;
    }
}
