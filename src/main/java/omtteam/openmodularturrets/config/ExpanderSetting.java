package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Config;

/**
 * Per-tier settings of the inventory expanders (as on 1.20.1+). The blocks and the machines read them while the game
 * runs, so a server's values reach its clients with the config sync (they predict mining progress and slot limits).
 */
public class ExpanderSetting {
    @Config.Comment("Stacks the expander adds to every ammo slot of a turret base and fuel slot of a generator it is attached to (a slot holds one stack plus those of all attached expanders, times the stacks of its turret base tier).")
    @Config.RangeInt(min = 0, max = 1000)
    public int stacks;
    @Config.Comment("Hardness of the expander (stone 1.5, obsidian 50).")
    @Config.RangeDouble(min = 0D, max = 1000000D)
    public double hardness;
    @Config.Comment("Explosion resistance of the expander (stone 6, obsidian 1200).")
    @Config.RangeDouble(min = 0D, max = 3600000D)
    public double explosionResistance;

    public ExpanderSetting(int stacks, double hardness, double explosionResistance) {
        this.stacks = stacks;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
    }
}
