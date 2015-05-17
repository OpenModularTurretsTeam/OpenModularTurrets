package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.MachineGunTurretTileEntity;

public class BlockMachineGunTurret extends BlockAbstractTurretHead {

    public BlockMachineGunTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedMachineGunTurret);
        this.setBlockTextureName(ModInfo.ID + ":machineGunTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new MachineGunTurretTileEntity();
    }
}
