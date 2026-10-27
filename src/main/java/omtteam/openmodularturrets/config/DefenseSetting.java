package omtteam.openmodularturrets.config;

import omtteam.omlib.config.Comment;
import omtteam.omlib.config.Range;

/**
 * Per-tier settings of the hardened blocks, walls and fences (1.12.2 OMPD {@code DefenseStats}). The blocks read them
 * while the game runs, so a server's values reach its clients with the config sync (they predict mining progress).
 */
public class DefenseSetting {
    @Comment("Hardness of the hardened block, wall and fence (stone 1.5, obsidian 50).") @Range(min = 0, max = 1000000)
    public double hardness;
    @Comment("Explosion resistance of the hardened block, wall and fence (stone 6, obsidian 1200).") @Range(min = 0, max = 3600000)
    public double explosionResistance;
    @Comment("Damage a fence deals per hit to entities touching it (up to two hits per second).") @Range(min = 0, max = 1000000)
    public double fenceDamage;

    @SuppressWarnings("unused")
    public DefenseSetting() {
    }

    public DefenseSetting(double hardness, double explosionResistance, double fenceDamage) {
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.fenceDamage = fenceDamage;
    }
}
