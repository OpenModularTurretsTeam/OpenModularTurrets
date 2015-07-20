package openmodularturrets.tileentity.turretbase;

import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openmodularturrets.blocks.turretbases.BlockTurretBaseTierFive;
import openmodularturrets.blocks.turretbases.BlockTurretBaseTierThree;
import openmodularturrets.handler.ConfigHandler;

import java.util.logging.Logger;

public class TurretBaseTierThreeTileEntity extends TurretBase {

    public TurretBaseTierThreeTileEntity() {
        this(ConfigHandler.getBaseTierThreeMaxCharge(), ConfigHandler.getBaseTierThreeMaxIo());
    }

    public TurretBaseTierThreeTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 3;
    }

    @Override
    public int getSizeInventory() {
        return 12;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbasethree";
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierThreeTurretBase";
    }
}
