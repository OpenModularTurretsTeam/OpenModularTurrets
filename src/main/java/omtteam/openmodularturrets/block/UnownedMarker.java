package omtteam.openmodularturrets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.security.OwnedHost;

/**
 * Shows that an owned block has no owner, so players see what they can claim: a green sparkle above it, called from
 * the blocks' {@code animateTick} (client only; vanilla calls it for random blocks near the player, so a single
 * particle per call already gives a calm rate). Not used by the camouflage trap, which would give itself away.
 */
public final class UnownedMarker {
    private UnownedMarker() {
    }

    /** A sparkle above {@code pos} if the block there has no owner. */
    public static void animateTick(Level level, BlockPos pos, RandomSource random) {
        animateTick(level, pos, pos, random);
    }

    /** A sparkle above {@code pos} if the block at {@code ownerPos} (e.g. a turret's base) has no owner. */
    public static void animateTick(Level level, BlockPos pos, BlockPos ownerPos, RandomSource random) {
        if (!(level.getBlockEntity(ownerPos) instanceof OwnedHost host) || host.getOwner() != null) return;
        level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + random.nextDouble(),
                pos.getY() + 1.05D + random.nextDouble() * 0.3D, pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
    }
}
