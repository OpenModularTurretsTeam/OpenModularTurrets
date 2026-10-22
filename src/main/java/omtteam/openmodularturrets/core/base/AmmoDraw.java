package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.turret.AmmoType;

import java.util.Objects;

/**
 * Ammunition taken from a base for one shot (a record in 1.20.1+).
 */
public final class AmmoDraw {
    private final AmmoType type;
    private final int count;
    private final int damageBonus;
    private final boolean recycled;
    private final Object item;

    /**
     * @param count       number of items (1 + scattershot upgrades)
     * @param damageBonus damage bonus of the first item drawn (disposable turret only, otherwise 0)
     * @param recycled    the recycler addon saved the items; nothing was removed
     * @param item        {@link omtteam.openmodularturrets.core.item.ItemSlot#snapshot()} of the first item drawn
     */
    public AmmoDraw(AmmoType type, int count, int damageBonus, boolean recycled, Object item) {
        this.type = type;
        this.count = count;
        this.damageBonus = damageBonus;
        this.recycled = recycled;
        this.item = item;
    }

    public AmmoType type() {
        return type;
    }

    /** Number of items (1 + scattershot upgrades). */
    public int count() {
        return count;
    }

    /** Damage bonus of the first item drawn (disposable turret only, otherwise 0). */
    public int damageBonus() {
        return damageBonus;
    }

    /** The recycler addon saved the items; nothing was removed. */
    public boolean recycled() {
        return recycled;
    }

    /** {@link omtteam.openmodularturrets.core.item.ItemSlot#snapshot()} of the first item drawn. */
    public Object item() {
        return item;
    }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AmmoDraw)) return false;
            AmmoDraw other = (AmmoDraw) o;
            return Objects.equals(type, other.type)
                && count == other.count
                && damageBonus == other.damageBonus
                && recycled == other.recycled
                && Objects.equals(item, other.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, count, damageBonus, recycled, item);
        }
}
