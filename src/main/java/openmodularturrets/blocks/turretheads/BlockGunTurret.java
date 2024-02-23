package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.GunTurretTileEntity;

public class BlockGunTurret extends BlockAbstractTurretHead {

    public BlockGunTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedGunTurret);
        this.setBlockTextureName(ModInfo.ID + ":machineGunTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new GunTurretTileEntity();
    }
}
