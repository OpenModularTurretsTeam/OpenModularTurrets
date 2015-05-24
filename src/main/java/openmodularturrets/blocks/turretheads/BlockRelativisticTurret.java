package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;

public class BlockRelativisticTurret extends BlockAbstractTurretHead {

    public BlockRelativisticTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedRelativisticTurret);
        this.setBlockTextureName(ModInfo.ID + ":relativisticTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new RelativisticTurretTileEntity();
    }
}
