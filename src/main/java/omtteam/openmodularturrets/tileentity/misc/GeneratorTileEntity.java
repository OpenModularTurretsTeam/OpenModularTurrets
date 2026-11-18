package omtteam.openmodularturrets.tileentity.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import omtteam.omlib.core.energy.EnergyStore;
import omtteam.omlib.inventory.AutomationItemHandler;
import omtteam.omlib.inventory.LargeStackItemHandler;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.openmodularturrets.block.expander.BlockInventoryExpander;
import omtteam.openmodularturrets.block.misc.BlockGenerator;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.generator.GeneratorLogic;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.openmodularturrets.item.ItemGeneratorUpgrade;
import omtteam.openmodularturrets.item.ModuleToggles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

/**
 * Generator (new in 4.0.0, replaces the 3.x power generator attachment, as on 1.20.1+): burns furnace fuel into energy
 * and pushes it into all neighbouring energy storages (turret bases, cables). One burn tick of a fuel produces
 * {@code generatorEnergyPerTick}. Burning pauses while the buffer is full, so no fuel is wasted. Energy multiplier
 * upgrades in its upgrade slot make it burn faster ({@link GeneratorLogic#multiplier}).
 * <p>
 * Reads the inventory of a 3.x power generator (its tile entity id is renamed by {@code GeneratorTileEntityFix}):
 * its six fuel slots and the redstone slot land in the first seven fuel slots.
 * <p>
 * Inventory expanders next to it make every fuel slot hold more ({@link #getSlotCapacity}), except for fuel that
 * leaves a remainder (a lava bucket), which keeps one stack, so the remainder fits into its slot.
 */
public class GeneratorTileEntity extends TileEntityBase implements ITickable {
    public static final int FUEL_SLOTS = 9;
    /** Max stack size of most items, the limit of an empty fuel slot. */
    private static final int DEFAULT_STACK = 64;

    private final LargeStackItemHandler fuel = new LargeStackItemHandler(FUEL_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isFuel(stack);
        }

        @Override
        public int getSlotLimit(int slot, @Nonnull ItemStack stack) {
            int itemMax = stack.isEmpty() ? DEFAULT_STACK : stack.getMaxStackSize();
            return leavesRemainder(stack) ? itemMax : getSlotCapacity().limit(itemMax);
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
    private final IItemHandler automation = new AutomationItemHandler(fuel, stack -> !isFuel(stack));
    private final EnergyStore energy;
    private final OMEnergyStorage energyStorage;
    private final GeneratorLogic logic = new GeneratorLogic();
    /** The fuel slots for {@link GeneratorLogic#burn}, in slot order. */
    private final List<GeneratorLogic.FuelSlot> fuelSlots = new ArrayList<>();
    /** Plate tiers of the attached inventory expanders (server); null until scanned on the first tick. */
    @Nullable
    private List<Integer> expanderTiers;
    /** The slot capacity the open generator GUI got from the server (clients do not scan the neighbours). */
    private SlotCapacity clientSlotCapacity = SlotCapacity.NONE;

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

    /** Whether burning the item leaves something in its slot (lava bucket → bucket). */
    public static boolean leavesRemainder(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().hasContainerItem(stack);
    }

    public ItemStackHandler getFuel() {
        return fuel;
    }

    /**
     * What the attached inventory expanders add to every fuel slot (the config in effect, see {@link SlotCapacity});
     * on the client the one the open generator GUI got from the server.
     */
    public SlotCapacity getSlotCapacity() {
        if (world != null && world.isRemote) {
            return clientSlotCapacity;
        }
        List<Integer> tiers = expanderTiers;
        return tiers == null || tiers.isEmpty() ? SlotCapacity.NONE
                : SlotCapacity.of(tiers, ConfigLogicSettings.INSTANCE::expanderStacks);
    }

    /** Client: the slot capacity synced by the generator GUI. */
    public void setClientSlotCapacity(SlotCapacity capacity) {
        this.clientSlotCapacity = capacity;
    }

    /** Called by the block when a neighbour changed: attached expanders. */
    public void onNeighbourChanged() {
        if (world == null || world.isRemote) {
            return;
        }
        scanExpanders();
    }

    /**
     * Collects the plates of the expanders next to every face ({@link BlockInventoryExpander#plateTiers}); waits while
     * a neighbour is not loaded, so an expander in a loading chunk does not shrink the slots for a moment.
     */
    private void scanExpanders() {
        List<Integer> tiers = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighbour = pos.offset(facing);
            if (!world.isBlockLoaded(neighbour)) {
                return;
            }
            tiers.addAll(BlockInventoryExpander.plateTiers(world.getBlockState(neighbour)));
        }
        expanderTiers = Collections.unmodifiableList(tiers);
    }

    public ItemStackHandler getUpgrades() {
        return upgrades;
    }

    /** Number of energy multiplier upgrades in the upgrade slot; 0 while the upgrade is disabled in the config. */
    public int getMultiplierUpgrades() {
        ItemStack stack = upgrades.getStackInSlot(0);
        return isUpgrade(stack) && ModuleToggles.isEnabled(((ItemGeneratorUpgrade) stack.getItem()).getUpgradeType())
                ? stack.getCount() : 0;
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
        if (expanderTiers == null) {
            scanExpanders();
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

    /** The inventory of a 3.x power generator ({@link PowerGeneratorNbt}). */
    private void loadPowerGeneratorSlots(NBTTagCompound tag) {
        for (Map.Entry<Integer, NBTTagCompound> slot : PowerGeneratorNbt.readSlots(tag, FUEL_SLOTS).entrySet()) {
            fuel.setStackInSlot(slot.getKey(), new ItemStack(slot.getValue()));
        }
    }
}
