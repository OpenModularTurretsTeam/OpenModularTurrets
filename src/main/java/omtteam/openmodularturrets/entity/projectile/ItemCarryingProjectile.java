package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A projectile that is shown as the fired item (1.12.2 {@code PotatoProjectile} / {@code DisposableTurretProjectile}
 * spawned an unpickable item entity flying alongside).
 */
public abstract class ItemCarryingProjectile extends TurretProjectile {
    private ItemStack item = ItemStack.EMPTY;
    @Nullable
    private ItemEntity carried;

    protected ItemCarryingProjectile(EntityType<? extends ItemCarryingProjectile> type, Level level) {
        super(type, level);
    }

    public void setItem(ItemStack item) {
        this.item = item.copyWithCount(1);
    }

    public ItemStack getItem() {
        return item;
    }

    /** Whether the shown item disappears when the projectile expires (disposable) or stays until despawn (potato). */
    protected abstract boolean removeItemWithProjectile();

    @Override
    public void tick() {
        super.tick();
        if (carried == null && !isRemoved() && !level().isClientSide() && !item.isEmpty()) {
            // Spawned half a step behind the projectile: at the muzzle the item would sit inside the weapon, a full
            // step (several blocks) out it would only show up well away from the turret.
            Vec3 motion = getDeltaMovement();
            Vec3 start = position().subtract(motion.scale(0.5D));
            carried = new ItemEntity(level(), start.x, start.y - 0.2F, start.z, item.copy());
            carried.setDeltaMovement(motion.add(0, getDropPerTick(), 0));
            carried.setNeverPickUp();
            level().addFreshEntity(carried);
        }
    }

    /** Removes the shown item (on hit). */
    protected void removeItem() {
        if (carried != null) {
            carried.discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (removeItemWithProjectile()) removeItem();
        super.remove(reason);
    }
}
