package omtteam.openmodularturrets.turret;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.core.turret.TurretType;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * Everything the game layer needs to know about a turret besides its core {@link TurretType} (1.20.1+
 * {@code api.TurretDefinition}, internal until the turret API of phase 5).
 */
public final class TurretDefinition {
    private final TurretType type;
    private final TurretWeapon weapon;
    private final TurretTooltip tooltip;
    @Nullable
    private final AmbientEffect ambientEffect;

    /**
     * @param weapon        what a shot does
     * @param tooltip       texts of the turret item's tooltip
     * @param ambientEffect particles shown around the placed turret on the client, or null
     */
    public TurretDefinition(TurretType type, TurretWeapon weapon, TurretTooltip tooltip,
                            @Nullable AmbientEffect ambientEffect) {
        this.type = Objects.requireNonNull(type, "type");
        this.weapon = Objects.requireNonNull(weapon, "weapon");
        this.tooltip = Objects.requireNonNull(tooltip, "tooltip");
        this.ambientEffect = ambientEffect;
    }

    public TurretType type() {
        return type;
    }

    public TurretWeapon weapon() {
        return weapon;
    }

    public TurretTooltip tooltip() {
        return tooltip;
    }

    @Nullable
    public AmbientEffect ambientEffect() {
        return ambientEffect;
    }

    /** Client-side particles of a placed turret (1.12.2 {@code randomDisplayTick} of the turret blocks). */
    @FunctionalInterface
    public interface AmbientEffect {
        void display(IBlockState state, World world, BlockPos pos, Random random);
    }
}
