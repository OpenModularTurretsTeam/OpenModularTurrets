package omtteam.openmodularturrets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.FloodFill;
import omtteam.openmodularturrets.security.OwnedHost;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Owned blocks connected to each other through their faces, for claiming (and the map maker's wand). Turrets connect
 * the blocks around them without having an owner themselves. Only loaded blocks are walked.
 */
public final class ClaimSpread {
    private ClaimSpread() {
    }

    /** What a claim did: the number of blocks claimed, and whether more connected blocks were left past the limit. */
    public record Result(int claimed, boolean limitReached) {
    }

    /**
     * Owned blocks connected to {@code start} through faces (turrets connect but are not returned) for which
     * {@code joins} is true, nearest first; walks at most {@code limit} blocks, turrets included. Empty if the block at
     * {@code start} does not join.
     */
    public static List<OwnedHost> connected(Level level, BlockPos start, int limit, Predicate<OwnedHost> joins) {
        return hosts(level, walk(level, start, limit, joins));
    }

    /**
     * Claims the unowned blocks connected to {@code start}, up to {@code bases.claimSpreadLimit} walked blocks.
     * Blocks that have an owner are never taken, and the walk does not pass through them.
     */
    public static Result claim(Level level, BlockPos start, PlayerIdentity player) {
        int limit = OMTConfig.get().bases.claimSpreadLimit;
        // one block more than the limit tells whether anything was left out
        List<BlockPosition> walked = walk(level, start, limit + 1, host -> host.getOwner() == null);
        boolean limitReached = walked.size() > limit;
        int claimed = 0;
        for (OwnedHost host : hosts(level, walked.subList(0, Math.min(limit, walked.size())))) {
            if (host.claim(player)) claimed++;
        }
        return new Result(claimed, limitReached);
    }

    private static List<BlockPosition> walk(Level level, BlockPos start, int limit, Predicate<OwnedHost> joins) {
        return FloodFill.collect(position(start), position -> joins(level, blockPos(position), joins), limit);
    }

    private static boolean joins(Level level, BlockPos pos, Predicate<OwnedHost> joins) {
        if (!level.isLoaded(pos)) return false;
        if (level.getBlockState(pos).getBlock() instanceof TurretBlock) return true;
        return level.getBlockEntity(pos) instanceof OwnedHost host && joins.test(host);
    }

    private static List<OwnedHost> hosts(Level level, List<BlockPosition> positions) {
        List<OwnedHost> hosts = new ArrayList<>();
        for (BlockPosition position : positions) {
            if (level.getBlockEntity(blockPos(position)) instanceof OwnedHost host) hosts.add(host);
        }
        return hosts;
    }

    private static BlockPosition position(BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    private static BlockPos blockPos(BlockPosition position) {
        return new BlockPos(position.x(), position.y(), position.z());
    }
}
