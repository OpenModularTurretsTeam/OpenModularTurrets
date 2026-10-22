package omtteam.openmodularturrets.core.item;

/**
 * Addons that go into the addon slots of a base. Ported from 1.12.2 {@code AddonMetaItem} (meta 0–7), in meta order.
 * <p>
 * {@link #POTENTIA} (Thaumcraft) and {@link #SERIAL_PORT} (OpenComputers/ComputerCraft) keep their 3.x behaviour,
 * which lives outside the core.
 */
public enum AddonType {
    CONCEALER("concealer", 1),
    DAMAGE_AMP("damage_amp", 4),
    POTENTIA("potentia", 1),
    RECYCLER("recycler", 1),
    REDSTONE_REACTOR("redstone_reactor", 1),
    SERIAL_PORT("serial_port", 1),
    SOLAR_PANEL("solar_panel", 1),
    FAKE_DROPS("fake_drops", 4);

    private final String serializedName;
    private final int maxPerSlot;

    AddonType(String serializedName, int maxPerSlot) {
        this.serializedName = serializedName;
        this.maxPerSlot = maxPerSlot;
    }

    public String getSerializedName() {
        return serializedName;
    }

    /** 1.12.2 {@code AddonSlot.getItemStackLimit}: damage amp and fake drops stack to 4, the others to 1. */
    public int getMaxPerSlot() {
        return maxPerSlot;
    }
}
