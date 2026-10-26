package omtteam.openmodularturrets.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.core.camo.CamoLight;
import org.jetbrains.annotations.Nullable;

/**
 * A block entity that can be camouflaged: the turret bases and the camouflage trap. Everything that sets or shows a
 * disguise (menu, packets, renderer) only talks to this interface, so bases and traps work identically.
 */
public interface CamoHost {
    BlockPos getBlockPos();

    /** Whether the block entity was removed from the world (the menu closes). */
    boolean isRemoved();

    /** The disguise, or {@code null} for the block's own look. */
    @Nullable
    BlockState getCamo();

    void setCamo(@Nullable BlockState camo);

    /** Whether camouflage is enabled for this block at all (config {@code bases.allowBaseCamo}). */
    boolean isCamoEnabled();

    /** Whether light value and opacity can be adjusted on this block. */
    boolean supportsLight();

    /** Current light settings; {@link CamoLight#DEFAULT} if {@link #supportsLight()} is false. */
    CamoLight getLight();

    /** Changes the light settings (server side); ignored if {@link #supportsLight()} is false. */
    void setLight(CamoLight light);

    /**
     * Whether the player sees the real block instead of the disguise in information overlays like Jade (the owner
     * and, for bases, everyone who may open the GUI). Checked on the client from the synced owner and trust list.
     */
    boolean canSeeThroughCamo(Player player);

    /** Server-side permission check for {@link #setCamo}. */
    boolean canSetCamo(Player player);

    /** Server-side permission check for {@link #setLight}. */
    boolean canChangeLight(Player player);

    /** Whether the camouflage menu is opened from another menu of this block, which a "Back" button returns to. */
    default boolean hasParentMenu() {
        return false;
    }

    /** Server side: opens that menu again (only called if {@link #hasParentMenu()}). */
    default void openParentMenu(ServerPlayer player) {
    }
}
