package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

public class BlockTeleporterTurret extends BlockAbstractTurretHead {

    public BlockTeleporterTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedTeleporterTurret);
        this.setBlockTextureName(ModInfo.ID + ":teleporterTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TeleporterTurretTileEntity();
    }
}
