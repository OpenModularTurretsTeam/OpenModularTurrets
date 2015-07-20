package openmodularturrets.tileentity.turretbase;

import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openmodularturrets.blocks.turretbases.BlockTurretBaseTierFive;
import openmodularturrets.blocks.turretbases.BlockTurretBaseTierFour;
import openmodularturrets.handler.ConfigHandler;

import java.util.logging.Logger;

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

    @Override
    public String getInventoryName() {
        return "modtur.turretbasefour";
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierFourTurretBase";
    }
}
