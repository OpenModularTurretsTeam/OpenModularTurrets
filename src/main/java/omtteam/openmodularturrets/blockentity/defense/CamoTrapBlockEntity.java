package omtteam.openmodularturrets.blockentity.defense;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.camo.CamoData;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.camo.CamoLightProperties;
import omtteam.openmodularturrets.core.camo.CamoLight;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

/**
 * The camouflage trap (OMPD {@code TileEntityCamo}): owner plus the block it looks like. Without camouflage it shows
 * its own texture. Its light settings are block state properties, like on the tier 4 and 5 turret bases.
 */
public class CamoTrapBlockEntity extends OwnedBlockEntity implements CamoHost, RenderAttachmentBlockEntity {
    private final CamoData camoData = new CamoData();

    public CamoTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAMO_TRAP, pos, state);
    }

    @Nullable
    @Override
    public BlockState getCamo() {
        return camoData.get();
    }

    @Override
    public void setCamo(@Nullable BlockState camo) {
        if (camoData.set(camo)) {
            syncToClient();
        }
    }

    @Override
    public boolean isCamoEnabled() {
        return true;
    }

    @Override
    public boolean supportsLight() {
        return true;
    }

    @Override
    public CamoLight getLight() {
        return CamoLightProperties.read(getBlockState());
    }

    @Override
    public void setLight(CamoLight light) {
        if (level != null) {
            CamoLightProperties.apply(level, worldPosition, light);
        }
    }

    @Override
    public boolean canSeeThroughCamo(Player player) {
        return isOwner(player);
    }

    @Override
    public boolean canSetCamo(Player player) {
        return isOwner(player);
    }

    @Override
    public boolean canChangeLight(Player player) {
        return isOwner(player);
    }

    /** What the model reads while the chunk is meshed (Fabric rendering data attachment). */
    @Override
    public @Nullable Object getRenderAttachmentData() {
        return camoData.get();
    }

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        super.readData(tag, registries);
        CamoData.remeshIfChanged(this, camoData.read(tag));
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeData(tag, registries);
        camoData.write(tag);
    }
}
