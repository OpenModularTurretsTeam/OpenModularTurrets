package omtteam.openmodularturrets.api.weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Ignores the base's target (melee turret): the turret is active while any living entity its base may damage is in
 * {@link #area} and strikes all of them once the cooldown passed.
 */
public final class AreaWeapon implements TurretWeapon {
    private final SoundEvent launchSound;
    private final Function<TurretTileEntity, AxisAlignedBB> area;
    private final AreaStrike strike;

    public AreaWeapon(SoundEvent launchSound, Function<TurretTileEntity, AxisAlignedBB> area, AreaStrike strike) {
        this.launchSound = Objects.requireNonNull(launchSound, "launchSound");
        this.area = Objects.requireNonNull(area, "area");
        this.strike = Objects.requireNonNull(strike, "strike");
    }

    @Override
    public SoundEvent launchSound() {
        return launchSound;
    }

    /** The reach of the turret. */
    public Function<TurretTileEntity, AxisAlignedBB> area() {
        return area;
    }

    public AreaStrike strike() {
        return strike;
    }

    @Override
    public boolean aims() {
        return false;
    }

    /** What a shot does. Energy and ammo are already consumed. */
    @FunctionalInterface
    public interface AreaStrike {
        /** @param victims the damageable living entities in the area, never empty */
        void strike(WorldServer world, TurretTileEntity turret, TurretBase base, List<EntityLivingBase> victims);
    }
}
