package omtteam.openmodularturrets.config;

import javax.annotation.Nullable;

/**
 * One line of the disposable ammo list: {@code modid:item[:meta][,damage]} or {@code ore:oreName[,damage]}.
 * The damage bonus defaults to 1; a missing meta means any metadata.
 */
public final class AmmoListEntry {
    private static final String ORE_PREFIX = "ore:";

    @Nullable
    public final String oreName;
    @Nullable
    public final String modId;
    @Nullable
    public final String itemName;
    /** The metadata, -1 for any. */
    public final int meta;
    public final int damage;

    private AmmoListEntry(@Nullable String oreName, @Nullable String modId, @Nullable String itemName, int meta, int damage) {
        this.oreName = oreName;
        this.modId = modId;
        this.itemName = itemName;
        this.meta = meta;
        this.damage = damage;
    }

    /** @throws IllegalArgumentException if the line is not in one of the formats above */
    public static AmmoListEntry parse(String line) {
        String[] parts = line.trim().split(",");
        int damage = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 1;
        String id = parts[0].trim();
        if (id.startsWith(ORE_PREFIX)) {
            String ore = id.substring(ORE_PREFIX.length());
            if (ore.isEmpty()) throw new IllegalArgumentException("empty ore name in '" + line + "'");
            return new AmmoListEntry(ore, null, null, -1, damage);
        }
        String[] item = id.split(":");
        if (item.length == 2) return new AmmoListEntry(null, item[0], item[1], -1, damage);
        if (item.length == 3) return new AmmoListEntry(null, item[0], item[1], Integer.parseInt(item[2]), damage);
        throw new IllegalArgumentException("not modid:item[:meta] or ore:name in '" + line + "'");
    }
}
