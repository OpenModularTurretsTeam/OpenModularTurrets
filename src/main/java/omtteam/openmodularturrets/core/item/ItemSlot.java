package omtteam.openmodularturrets.core.item;

import omtteam.openmodularturrets.core.turret.AmmoType;

import java.util.Optional;

/**
 * One inventory slot as seen by the core logic. The Minecraft layer classifies the item in the slot; the core only
 * counts and extracts.
 */
public interface ItemSlot {
    /** Number of items in the slot (0 if empty). */
    int getCount();

    /** Whether the item can be fired as this ammo type; false for an empty slot. */
    boolean isAmmo(AmmoType type);

    /** Damage bonus of the item when fired by the disposable turret (config allow list), 0 otherwise. */
    int getDisposableDamageBonus();

    /** Whether the item is this fuel of the redstone reactor addon; false for an empty slot. */
    boolean isReactorFuel(ReactorFuel fuel);

    /** The base upgrade the item is, empty if it is none. */
    Optional<TurretBaseUpgradeType> getUpgrade();

    /** The base addon the item is, empty if it is none. */
    Optional<AddonType> getAddon();

    /**
     * An opaque copy of one item of this slot, for the game layer (e.g. to show the fired item). The core never
     * looks inside it.
     */
    Object snapshot();

    /**
     * Removes up to {@code amount} items.
     *
     * @return the number of items actually removed
     */
    int extract(int amount);
}
