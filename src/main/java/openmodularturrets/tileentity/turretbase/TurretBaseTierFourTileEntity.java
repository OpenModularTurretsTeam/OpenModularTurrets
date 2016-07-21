package openmodularturrets.tileentity.turretbase;


import net.minecraftforge.fml.common.Optional;
import openmodularturrets.handler.ConfigHandler;

public class TurretBaseTierFourTileEntity extends TurretBase {
    public TurretBaseTierFourTileEntity() {
        this(ConfigHandler.getBaseTierFourMaxCharge(), ConfigHandler.getBaseTierFourMaxIo());
    }

    public TurretBaseTierFourTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 4;
    }

    @Override
    public int getSizeInventory() {
        return 12;
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierFourTurretBase";
    }
}
