package omtteam.openmodularturrets.camo;

/**
 * Marker for blocks that can look like another block ({@link CamoHost} block entities). The client wraps their
 * models so the disguise is drawn.
 */
public interface CamoBlock {
    /**
     * The tint index of the quads of the block's own model that stay visible on top of a disguise (a lamp, say), drawn
     * in the block's own render layer; -1 for none, so the disguise replaces the whole block.
     */
    default int overlayTintIndex() {
        return -1;
    }
}
