package omtteam.openmodularturrets.block.defense;

/**
 * OMPD block tiers. The strength is the hardness and blast resistance the blocks are registered with (OMPD
 * {@code getBlockHardness} / {@code getExplosionResistance}, equal values, and the config defaults); while the game
 * runs, the blocks use the config values instead ({@link PassiveDefense#setting}).
 */
public enum DefenseTier {
    ONE("one", 10.0F),
    TWO("two", 20.0F),
    THREE("three", 30.0F),
    FOUR("four", 50.0F),
    FIVE("five", 80.0F);

    private final String name;
    private final float strength;

    DefenseTier(String name, float strength) {
        this.name = name;
        this.strength = strength;
    }

    /** {@code one} … {@code five}, as in the old block names ({@code wall_tier_one}). */
    public String getName() {
        return name;
    }

    /** Hardness and blast resistance the blocks are registered with. */
    public float getStrength() {
        return strength;
    }

    /** 1 … 5. */
    public int getNumber() {
        return ordinal() + 1;
    }
}
