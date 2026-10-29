package omtteam.openmodularturrets.core.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The order of the "Sort Ammo" button of the turret base GUI (1.20.1+ {@code TurretBaseInventory.sortAmmo}): equal
 * items merge into as few slots as their slot limit allows, then Minecraft's items come first, Open Modular Turrets'
 * next and other mods' last, each ordered by registry name and, for the meta items of 1.12 that share one registry
 * name, by the name of their variant. Items of one kind with different data keep their order.
 * The sorted stacks hold exactly the items of the slots before; when they would need more slots than there are (slots
 * above their limit after an inventory expander was removed), there is no result and the slots stay as they are.
 */
public final class AmmoSort {
    /** Namespace of Minecraft's items, sorted first. */
    public static final String MINECRAFT_NAMESPACE = "minecraft";
    /** Namespace of Open Modular Turrets' items (the mod id), sorted second. */
    public static final String OMT_NAMESPACE = "openmodularturrets";

    private AmmoSort() {
    }

    /** What the sort needs to know about an item; the game layer implements it for its item stacks. */
    public interface Items<T> {
        /** Whether two items may share a slot (the same item, metadata and data); the counts do not matter. */
        boolean isSameKind(T a, T b);

        /** The registry name of the item, {@code namespace:path}. */
        String registryName(T item);

        /** The name of the item's variant (its translation key), ordering items with the same registry name. */
        String variantName(T item);

        /** Most items of this kind one slot holds (at least 1). */
        int slotLimit(T item);
    }

    /** The content of one slot: an item and how many there are. */
    public static final class Stack<T> {
        private final T item;
        private final int count;

        public Stack(T item, int count) {
            this.item = item;
            this.count = count;
        }

        public T getItem() {
            return item;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return count + "x" + item;
        }
    }

    /**
     * @param stacks    the slots in slot order; empty ones (count 0 or less) are skipped
     * @param slotCount how many slots there are
     * @return the new contents of the slots, first slot first (the slots after the last stack are empty), or nothing
     * if the stacks would need more than {@code slotCount} slots
     */
    public static <T> Optional<List<Stack<T>>> sort(List<Stack<T>> stacks, int slotCount, Items<T> items) {
        List<Kind<T>> kinds = new ArrayList<>();
        for (Stack<T> stack : stacks) {
            if (stack.count <= 0) continue;
            Kind<T> kind = find(kinds, stack.item, items);
            if (kind == null) {
                kinds.add(new Kind<>(stack.item, stack.count));
            } else {
                kind.count += stack.count;
            }
        }
        // List.sort is stable: kinds that compare equal (same item, other data) keep their order
        kinds.sort(Comparator.comparingInt((Kind<T> kind) -> namespaceRank(items.registryName(kind.item)))
                .thenComparing(kind -> items.registryName(kind.item))
                .thenComparing(kind -> items.variantName(kind.item)));
        List<Stack<T>> sorted = new ArrayList<>();
        for (Kind<T> kind : kinds) {
            int limit = Math.max(1, items.slotLimit(kind.item));
            for (long left = kind.count; left > 0; left -= limit) {
                if (sorted.size() == slotCount) return Optional.empty();
                sorted.add(new Stack<>(kind.item, (int) Math.min(left, limit)));
            }
        }
        return Optional.of(Collections.unmodifiableList(sorted));
    }

    private static <T> Kind<T> find(List<Kind<T>> kinds, T item, Items<T> items) {
        for (Kind<T> kind : kinds) {
            if (items.isSameKind(kind.item, item)) return kind;
        }
        return null;
    }

    /** 0 for Minecraft's items, 1 for Open Modular Turrets', 2 for other mods'. */
    static int namespaceRank(String registryName) {
        int colon = registryName.indexOf(':');
        String namespace = colon < 0 ? MINECRAFT_NAMESPACE : registryName.substring(0, colon);
        if (namespace.equals(MINECRAFT_NAMESPACE)) return 0;
        return namespace.equals(OMT_NAMESPACE) ? 1 : 2;
    }

    /** One kind of item while sorting: the first stack of it and how many there are in all slots. */
    private static final class Kind<T> {
        private final T item;
        private long count;

        private Kind(T item, long count) {
            this.item = item;
            this.count = count;
        }
    }
}
