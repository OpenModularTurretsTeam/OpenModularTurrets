package omtteam.openmodularturrets.config;

import omtteam.omlib.config.Comment;
import omtteam.omlib.config.Range;

/**
 * Per-tier settings of the inventory expanders. The blocks and the machines read them while the game runs, so a
 * server's values reach its clients with the config sync (they predict mining progress and slot limits).
 */
public class ExpanderSetting {
    @Comment("Stacks the expander adds to every ammo slot of a turret base and fuel slot of a generator it is attached "
            + "to (a slot holds one stack plus the stacks of all attached expanders).") @Range(min = 0, max = 1000)
    public int stacks;
    @Comment("Hardness of the expander (stone 1.5, obsidian 50).") @Range(min = 0, max = 1000000)
    public double hardness;
    @Comment("Explosion resistance of the expander (stone 6, obsidian 1200).") @Range(min = 0, max = 3600000)
    public double explosionResistance;

    @SuppressWarnings("unused")
    public ExpanderSetting() {
    }

    public ExpanderSetting(int stacks, double hardness, double explosionResistance) {
        this.stacks = stacks;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
    }
}
