package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.IncendiaryTurretTileEntity;

public class BlockIncendiaryTurret extends BlockAbstractTurretHead {
    public BlockIncendiaryTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedIncendiaryTurret);
        this.setBlockTextureName(ModInfo.ID + ":incendiaryTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new IncendiaryTurretTileEntity();
    }
}
