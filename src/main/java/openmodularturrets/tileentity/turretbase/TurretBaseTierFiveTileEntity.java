package openmodularturrets.tileentity.turretbase;

import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.Optional;
import openmodularturrets.handler.ConfigHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TurretBaseTierFiveTileEntity extends TurretBase {

    public TurretBaseTierFiveTileEntity() {
        this(ConfigHandler.getBaseTierFiveMaxCharge(), ConfigHandler.getBaseTierFiveMaxIo());
    }

    public TurretBaseTierFiveTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 5;
    }

    @Override
    public int getSizeInventory() {
        return 13;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasefive";
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierFiveTurretBase";
    }
}
