package omtteam.openmodularturrets.blockentity.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.security.OMTOwnershipSettings;

/**
 * The block entity of the hardened blocks, walls and fences (OMPD {@code TileEntityPassiveTiered}): an owner, nothing
 * else. The tier is the block itself.
 */
public class PassiveDefenseBlockEntity extends OwnedBlockEntity {
    public PassiveDefenseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OWNED, pos, state, OMTOwnershipSettings.INSTANCE);
    }
}
