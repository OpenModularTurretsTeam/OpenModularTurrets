package omtteam.openmodularturrets.core.security;

/**
 * Access level of a trusted player, in increasing order. Ported from OMLib {@code EnumAccessLevel}.
 * Each level includes the rights of all lower levels.
 */
public enum AccessLevel {
    /** Trusted (turrets do not target the player), but no access to the base. */
    NONE("none"),
    /** May open the base GUI and its inventory. */
    OPEN_GUI("open_gui"),
    /** May also change targeting and other settings. */
    CHANGE_SETTINGS("change_settings"),
    /** May also manage trusted players, change the redstone mode and range, and drop the base/turrets. */
    ADMIN("admin");

    private final String serializedName;

    AccessLevel(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return serializedName;
    }

    /** Whether this level has all the rights of {@code other} (it is the same or higher). */
    public boolean isAtLeast(AccessLevel other) {
        return ordinal() >= other.ordinal();
    }

    /** The level with this serialized name, or {@code fallback} if there is none (unknown or missing saved value). */
    public static AccessLevel bySerializedName(String name, AccessLevel fallback) {
        for (AccessLevel level : values()) {
            if (level.serializedName.equals(name)) return level;
        }
        return fallback;
    }
}
