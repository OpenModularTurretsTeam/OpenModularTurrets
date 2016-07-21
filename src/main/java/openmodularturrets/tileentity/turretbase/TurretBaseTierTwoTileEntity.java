package openmodularturrets.tileentity.turretbase;


import net.minecraftforge.fml.common.Optional;
import openmodularturrets.handler.ConfigHandler;

public class TurretBaseTierTwoTileEntity extends TurretBase {
    public TurretBaseTierTwoTileEntity() {
        this(ConfigHandler.getBaseTierTwoMaxCharge(), ConfigHandler.getBaseTierTwoMaxIo());
    }

    public TurretBaseTierTwoTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public int getSizeInventory() {
        return 12;
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierTwoTurretBase";
    }
}
