package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.PotatoCannonTurretTileEntity;

public class BlockPotatoCannonTurret extends BlockAbstractTurretHead {
    public BlockPotatoCannonTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedPotatoCannonTurret);
        this.setBlockTextureName(ModInfo.ID + ":potatoCannonTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new PotatoCannonTurretTileEntity();
    }
}
