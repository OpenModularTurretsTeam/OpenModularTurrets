package omtteam.openmodularturrets.core.world;

import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.target.TargetEntity;

import java.util.List;
import java.util.Optional;

/**
 * The world around one turret base, as seen by the core logic. Implemented by the Minecraft layer.
 */
public interface BaseWorld {
    BlockPosition getPosition();

    /** Ammo slots of the base (0–8), followed by the slots of attached inventory expanders. */
    List<ItemSlot> getAmmoSlots();

    /** Both addon slots of the base (9–10), whether or not the tier can use them. */
    List<ItemSlot> getAddonSlots();

    /** Both upgrade slots of the base (11–12), whether or not the tier can use them. */
    List<ItemSlot> getUpgradeSlots();

    /**
     * oldmod {@code updateSolarPanelAddon}: daytime, not raining and the sky is visible from two blocks above
     * the base.
     */
    boolean canGenerateSolarPower();

    /** Living entities (no spectators) whose position is inside the box. */
    List<TargetEntity> getLivingEntities(Box box);

    /** A loaded living entity by id. */
    Optional<TargetEntity> getEntity(int id);

    /**
     * Whether no solid, collidable block is between the point and the target's eyes (oldmod
     * {@code canSeeTargetFromPos}: blocks without collision such as grass, and fluids, do not block the view).
     */
    boolean hasLineOfSight(double fromX, double fromY, double fromZ, TargetEntity target);

    /** Plays the alarm and/or sends the warning message to a player (oldmod {@code dispatchWarnMessage}). */
    void warnPlayer(TargetEntity player);
}
