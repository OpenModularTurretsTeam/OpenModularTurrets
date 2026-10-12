package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.turret.AmmoType;

/**
 * Ammunition taken from a base for one shot.
 *
 * @param count       number of items (1 + scattershot upgrades)
 * @param damageBonus damage bonus of the first item drawn (disposable turret only, otherwise 0)
 * @param recycled    the recycler addon saved the items; nothing was removed
 * @param item        {@link omtteam.openmodularturrets.core.item.ItemSlot#snapshot()} of the first item drawn
 */
public record AmmoDraw(AmmoType type, int count, int damageBonus, boolean recycled, Object item) {
}
