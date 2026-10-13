package omtteam.openmodularturrets.core.item;

/**
 * Items burned by the redstone reactor addon (1.12.2 {@code TurretBase.updateRedstoneReactor}).
 */
public enum ReactorFuel {
    REDSTONE(1),
    REDSTONE_BLOCK(9);

    private final int multiplier;

    ReactorFuel(int multiplier) {
        this.multiplier = multiplier;
    }

    /** Energy per item as a multiple of {@code redstoneReactorAddonGen}. */
    public int getMultiplier() {
        return multiplier;
    }
}
