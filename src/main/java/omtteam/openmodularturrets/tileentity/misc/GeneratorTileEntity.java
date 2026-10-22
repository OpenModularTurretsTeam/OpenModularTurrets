package omtteam.openmodularturrets.tileentity.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import omtteam.omlib.core.energy.EnergyStore;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.openmodularturrets.block.misc.BlockGenerator;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.generator.GeneratorLogic;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.item.ItemGeneratorUpgrade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

/**
 * Generator (new in 4.0.0, replaces the 3.x power generator attachment, as on 1.20.1+): burns furnace fuel into energy
 * and pushes it into all neighbouring energy storages (turret bases, cables). One burn tick of a fuel produces
 * {@code generatorEnergyPerTick}. Burning pauses while the buffer is full, so no fuel is wasted. Energy multiplier
 * upgrades in its upgrade slot make it burn faster ({@link GeneratorLogic#multiplier}).
 * <p>
 * Reads the inventory of a 3.x power generator (its tile entity id is renamed by {@code GeneratorTileEntityFix}):
 * its six fuel slots and the redstone slot land in the first seven fuel slots.
 */
public class GeneratorTileEntity extends TileEntityBase implements ITickable {
    public static final int FUEL_SLOTS = 9;

    private final ItemStackHandler fuel = new ItemStackHandler(FUEL_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isFuel(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    /** One upgrade slot holding up to 4 generator upgrades. */
    private final ItemStackHandler upgrades = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isUpgrade(stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return GeneratorUpgradeType.ENERGY_MULTIPLIER.maxPerSlot();
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    /** Hoppers and pipes may only insert fuel and only extract leftovers (empty buckets). */
    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return FUEL_SLOTS;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return fuel.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return isFuel(stack) ? fuel.insertItem(slot, stack, simulate) : stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return isFuel(fuel.getStackInSlot(slot)) ? ItemStack.EMPTY : fuel.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return fuel.getSlotLimit(slot);
        }
    };
    private final EnergyStore energy;
    private final OMEnergyStorage energyStorage;
    private final GeneratorLogic logic = new GeneratorLogic();
    /** The fuel slots for {@link GeneratorLogic#burn}, in slot order. */
    private final List<GeneratorLogic.FuelSlot> fuelSlots = new ArrayList<>();

    public GeneratorTileEntity() {
        energy = new EnergyStore(OMTConfig.MISCELLANEOUS.generatorCapacity, 0,
                OMTConfig.MISCELLANEOUS.generatorMaxOutput, this::markDirty);
        energyStorage = new OMEnergyStorage(energy);
        for (int slot = 0; slot < FUEL_SLOTS; slot++) {
            final int index = slot;
            fuelSlots.add(() -> consumeFuel(index));
        }
    }

    public static boolean isUpgrade(ItemStack stack) {
        return stack.getItem() instanceof ItemGeneratorUpgrade;
    }

    public static boolean isFuel(ItemStack stack) {
        return !stack.isEmpty() && TileEntityFurnace.getItemBurnTime(stack) > 0;
    }

    public ItemStackHandler getFuel() {
        return fuel;
    }

    public ItemStackHandler getUpgrades() {
        return upgrades;
    }

    /** Number of energy multiplier upgrades in the upgrade slot. */
    public int getMultiplierUpgrades() {
        ItemStack stack = upgrades.getStackInSlot(0);
        return isUpgrade(stack) ? stack.getCount() : 0;
    }

    public long getEnergy() {
        return energy.getAmount();
    }

    public long getCapacity() {
        return energy.getCapacity();
    }

    public int getBurnTime() {
        return logic.getBurnTime();
    }

    public int getBurnDuration() {
        return logic.getBurnDuration();
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        pushEnergy();
        boolean lit = burn();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockGenerator && state.getValue(BlockGenerator.LIT) != lit) {
            world.setBlockState(pos, state.withProperty(BlockGenerator.LIT, lit), 3);
        }
    }

    /** Burns one tick of fuel ({@link GeneratorLogic#burn}); returns whether it burned. */
    private boolean burn() {
        OptionalLong produced = logic.burn(energy.getFreeSpace(), OMTConfig.MISCELLANEOUS.generatorEnergyPerTick,
                GeneratorLogic.multiplier(getMultiplierUpgrades()), fuelSlots);
        if (!produced.isPresent()) {
            return false;
        }
        energy.generate(produced.getAsLong());
        markDirty(); // the burn time changed, even if nothing was produced
        return true;
    }

    /**
     * Removes the next fuel item from a slot, leaving its remainder (lava bucket → bucket) like a furnace.
     *
     * @return its burn time, 0 if the slot holds no fuel
     */
    private int consumeFuel(int slot) {
        ItemStack stack = fuel.getStackInSlot(slot);
        if (!isFuel(stack)) {
            return 0;
        }
        int ticks = TileEntityFurnace.getItemBurnTime(stack);
        ItemStack remainder = stack.getItem().getContainerItem(stack);
        ItemStack rest = stack.copy();
        rest.shrink(1);
        fuel.setStackInSlot(slot, rest.isEmpty() ? remainder : rest);
        return ticks;
    }

    /** Pushes up to {@code generatorMaxOutput} per tick into all neighbours together. */
    private void pushEnergy() {
        long remaining = energy.getMaxExtract();
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (remaining <= 0 || energy.getAmount() <= 0) {
                return;
            }
            TileEntity neighbour = world.getTileEntity(pos.offset(facing));
            if (neighbour == null || !neighbour.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                continue;
            }
            IEnergyStorage target = neighbour.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }
            int offer = (int) Math.min(Integer.MAX_VALUE, Math.min(remaining, energy.getAmount()));
            int accepted = target.receiveEnergy(offer, false);
            if (accepted > 0) {
                energy.extract(accepted, false);
                remaining -= accepted;
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automation);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound saveToNBT(@Nonnull NBTTagCompound tag) {
        super.saveToNBT(tag);
        tag.setTag("Fuel", fuel.serializeNBT());
        tag.setTag("Upgrades", upgrades.serializeNBT());
        tag.setLong("Energy", energy.getAmount());
        tag.setInteger("BurnTime", logic.getBurnTime());
        tag.setInteger("BurnDuration", logic.getBurnDuration());
        return tag;
    }

    @Override
    public void loadFromNBT(@Nonnull NBTTagCompound tag) {
        super.loadFromNBT(tag);
        if (tag.hasKey("Fuel", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound fuelTag = tag.getCompoundTag("Fuel");
            fuelTag.setInteger("Size", FUEL_SLOTS);
            fuel.deserializeNBT(fuelTag);
        } else {
            loadPowerGeneratorSlots(tag);
        }
        if (tag.hasKey("Upgrades", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound upgradeTag = tag.getCompoundTag("Upgrades");
            upgradeTag.setInteger("Size", 1);
            upgrades.deserializeNBT(upgradeTag);
        }
        energy.setAmountUnchecked(Math.min(energy.getCapacity(), tag.getLong("Energy")));
        logic.load(tag.getInteger("BurnTime"), tag.getInteger("BurnDuration"));
    }

    /**
     * The inventory of a 3.x power generator, in any of the formats of OMLib's {@code TileEntityContainer}: slot lists
     * under {@code Slots}, {@code Items} or {@code Inventory.Items}.
     */
    private void loadPowerGeneratorSlots(NBTTagCompound tag) {
        NBTTagList list;
        if (tag.hasKey("Slots", Constants.NBT.TAG_LIST)) {
            list = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        } else if (tag.hasKey("Items", Constants.NBT.TAG_LIST)) {
            list = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        } else {
            list = tag.getCompoundTag("Inventory").getTagList("Items", Constants.NBT.TAG_COMPOUND);
        }
        for (NBTBase entry : list) {
            NBTTagCompound item = (NBTTagCompound) entry;
            int slot = item.getByte("Slot") & 255;
            if (slot < FUEL_SLOTS) {
                fuel.setStackInSlot(slot, new ItemStack(item));
            }
        }
    }
}
