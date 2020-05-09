package omtteam.openmodularturrets.tileentity;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import omtteam.omlib.api.network.INetworkTile;
import omtteam.omlib.api.network.IPowerExchangeTile;
import omtteam.omlib.api.network.OMLibNetwork;
import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.api.render.camo.ICamoSupport;
import omtteam.omlib.api.tile.IDebugTile;
import omtteam.omlib.network.ISyncableTE;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.MessageCamoSettings;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.tileentity.TileEntityTrustedMachine;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.omlib.util.NetworkUtil;
import omtteam.omlib.util.WorldUtil;
import omtteam.omlib.util.camo.CamoSettings;
import omtteam.openmodularturrets.api.network.IBaseController;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.network.messages.MessageTurretBase;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.OMTUtil;
import omtteam.openmodularturrets.util.TargetingSettings;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static omtteam.omlib.compatibility.OMLibModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.OMLibModCompatibility.OpenComputersLoaded;
import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;
import static omtteam.omlib.util.player.PlayerUtil.getPlayerUUID;

@SuppressWarnings("unused")
@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "computercraft")}
)
public class TurretBase extends TileEntityTrustedMachine implements IPeripheral, ICamoSupport, IDebugTile, IPowerExchangeTile, INetworkTile, ISyncableTE, ITickable {
    public int trustedPlayerIndex = 0;
    public boolean shouldConcealTurrets;
    protected CamoSettings camoSettings;
    protected int tier;
    protected IItemHandlerModifiable inventory;
    protected FluidTank tank;
    private IBlockState camoBlockStateTemp;
    private boolean multiTargeting = false;
    private int currentMaxRange;
    private int upperBoundMaxRange;
    private boolean rangeOverridden;
    private boolean attacksMobs;
    private boolean attacksNeutrals;
    private boolean attacksPlayers;
    private boolean updateRange;
    private int ticks;
    private boolean forceFire = false;
    private int kills;
    private int playerKills;
    private IBaseController controller;
    private OMLibNetwork network;
    private final List<EntityPlayerMP> openClients = new ArrayList<>(); // for GUI Stuff

    public TurretBase(int MaxEnergyStorage, int MaxIO, int tier, IBlockState camoState) {
        super();
        this.currentMaxRange = 0;
        this.upperBoundMaxRange = 0;
        this.rangeOverridden = false;
        this.storage = new OMEnergyStorage(MaxEnergyStorage, MaxIO);
        this.attacksMobs = true;
        this.attacksNeutrals = false;
        this.attacksPlayers = false;
        this.tier = tier;
        this.camoBlockStateTemp = camoState;
        this.mode = EnumMachineMode.INVERTED;
        this.camoSettings = new CamoSettings();
        setupInventory();
    }

    public TurretBase() {
        super();
        setupInventory();
    }

    private static void updateRedstoneReactor(TurretBase base) {
        OMEnergyStorage storage = (OMEnergyStorage) base.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if (!TurretHeadUtil.hasRedstoneReactor(base) || storage == null) {
            return;
        }

        //Prioritise redstone blocks
        if (OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen * 9 < (storage.getMaxEnergyStored() - storage.getEnergyStored())) {
            ItemStack redstoneBlock = TurretHeadUtil.getSpecificItemStackFromBase(base, new ItemStack(
                    Blocks.REDSTONE_BLOCK));

            if (redstoneBlock == ItemStack.EMPTY) {
                redstoneBlock = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorld(),
                                                                               new ItemStack(Blocks.REDSTONE_BLOCK),
                                                                               base, null);
            }
            if (redstoneBlock != ItemStack.EMPTY) {
                base.storage.modifyEnergyStored(OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen * 9);
                return;
            }
        }

        if (OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen < (storage.getMaxEnergyStored() - storage.getEnergyStored())) {

            ItemStack redstone = TurretHeadUtil.getSpecificItemStackFromBase(base, new ItemStack(Items.REDSTONE));

            if (redstone == ItemStack.EMPTY) {
                redstone = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorld(),
                                                                          new ItemStack(Items.REDSTONE), base, null);
            }

            if (redstone != ItemStack.EMPTY) {
                storage.modifyEnergyStored(OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen);
            }
        }
    }

    @Override
    public IItemHandler getCapabilityInventory(EnumFacing facing) {
        return new RangedWrapper(inventory, 0, 9);
    }

    protected void setupInventory() {
        inventory = new ItemStackHandler(13) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            public boolean isItemValidForSlot(int index, ItemStack stack) {
                if (index < 9) {
                    return OMTUtil.isItemStackValidAmmo(stack);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValidForSlot(slot, stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    protected void setupTank() {
        tank = new FluidTank(4000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return super.canFillFluidType(fluid); //TODO: add supported fluid tanks
            }

            @Override
            public boolean canDrain() {
                return false;
            }
        };
    }

    @Override
    @Nonnull
    public CamoSettings getCamoSettings() {
        return camoSettings == null ? new CamoSettings() : camoSettings;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getDefaultCamoState() {
        Block base = ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation(Reference.MOD_ID + ":" + OMTNames.Blocks.turretBase));
        //noinspection ConstantConditions
        return base.getStateFromMeta(this.tier - 1);
    }

    @Nonnull
    @Override
    public IBlockState getCamoState() {
        return this.getCamoSettings().getCamoBlockState() != null
                && this.getCamoSettings().getCamoBlockState() instanceof IExtendedBlockState
                ? (IExtendedBlockState) this.getCamoSettings().getCamoBlockState()
                : this.getCamoSettings().getCamoBlockState() != null
                ? this.getCamoSettings().getCamoBlockState().getBlock()
                .getExtendedState(this.getCamoSettings().getCamoBlockState(), this.getWorld(), this.getPos())
                : this.getDefaultCamoState();
    }

    @Override
    public void setCamoState(IBlockState state) {
        if (!(state instanceof IExtendedBlockState)) {
            if (this.getCamoState() == state.getBlock().getExtendedState(state, this.getWorld(), this.getPos())) {
                return;
            }
            this.getCamoSettings().setCamoBlockState(state.getBlock().getExtendedState(state, this.getWorld(), this.getPos()));
        } else {
            if (this.getCamoState() == state) {
                return;
            }
            this.getCamoSettings().setCamoBlockState(state);
        }
        this.camoBlockStateTemp = state;
        if (!world.isRemote) {
            OMLibNetworkingHandler.INSTANCE.sendToAllTracking(new MessageCamoSettings(this),
                                                              new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 80));
            this.setUpdateNBT(true);
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("currentMaxRange", this.currentMaxRange);
        tag.setInteger("upperBoundMaxRange", this.upperBoundMaxRange);
        tag.setBoolean("rangeOverridden", this.rangeOverridden);
        tag.setBoolean("attacksMobs", this.attacksMobs);
        tag.setBoolean("attacksNeutrals", this.attacksNeutrals);
        tag.setBoolean("attacksPlayers", this.attacksPlayers);
        tag.setBoolean("shouldConcealTurrets", this.shouldConcealTurrets);
        tag.setBoolean("multiTargeting", this.multiTargeting);
        tag.setBoolean("forceFire", this.forceFire);
        tag.setInteger("tier", this.tier);
        tag.setInteger("mode", this.mode.ordinal());
        tag.setInteger("kills", this.kills);
        camoSettings.writeNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.currentMaxRange = tag.getInteger("currentMaxRange");
        this.upperBoundMaxRange = tag.getInteger("upperBoundMaxRange");
        this.rangeOverridden = tag.getBoolean("rangeOverridden");
        this.attacksMobs = tag.getBoolean("attacksMobs");
        this.attacksNeutrals = tag.getBoolean("attacksNeutrals");
        this.attacksPlayers = tag.getBoolean("attacksPlayers");
        this.shouldConcealTurrets = tag.getBoolean("shouldConcealTurrets");
        this.multiTargeting = tag.getBoolean("multiTargeting");
        this.forceFire = tag.getBoolean("forceFire");
        this.tier = tag.getInteger("tier");
        if (tag.hasKey("mode")) {
            this.mode = EnumMachineMode.values()[tag.getInteger("mode")];
        } else {
            this.mode = EnumMachineMode.INVERTED;
        }
        this.camoSettings = CamoSettings.getSettingsFromNBT(tag);
        if (camoSettings.getCamoBlockState() != null) {
            this.camoBlockStateTemp = camoSettings.getCamoBlockState();
        } else {
            this.camoBlockStateTemp = getDefaultCamoState();
        }
        if (tag.hasKey("kills")) {
            this.kills = tag.getInteger("kills");
        } else {
            this.kills = 0;
        }
        if (tag.hasKey("playerKills")) {
            this.kills = tag.getInteger("playerKills");
        } else {
            this.playerKills = 0;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (camoBlockStateTemp instanceof IExtendedBlockState) {
            this.camoSettings.setCamoBlockState(camoBlockStateTemp);
        } else {
            this.setCamoState(camoBlockStateTemp.getBlock().getExtendedState(camoBlockStateTemp, this.getWorld(), this.getPos()));
        }
        this.updateNBT = true;
    }

    @Override
    public List<EntityPlayerMP> getSyncPlayerList() {
        return openClients;
    }

    @Override
    public TileEntity getTE() {
        return this;
    }

    @Override
    public void sendMessageToAllTracking() {
        OMTNetworkingHandler.INSTANCE.sendToAllAround(new MessageTurretBase(this), NetworkUtil.getTargetPointFromTE(this, 20));
    }

    private void updateControllerSettings() {
        if (controller != null) {
            TargetingSettings settings = controller.getTargetingSettings();
            this.attacksMobs = settings.isTargetMobs();
            this.attacksNeutrals = settings.isTargetPassive();
            this.attacksPlayers = settings.isTargetPlayers();
            this.currentMaxRange = settings.getMaxRange();
        }
    }

    @Override
    public List<String> getDebugInfo() {
        List<String> debugInfo = new ArrayList<>();
        debugInfo.add("Camo: " + this.camoSettings.getCamoBlockState().getBlock().getRegistryName());
        debugInfo.add("Force Fire: " + this.forceFire + ", UpperMaxRange: " + this.upperBoundMaxRange);
        return debugInfo;
    }

    @Override
    @Nonnull
    public TileEntityOwnedBlock getOwnedBlock() {
        return this;
    }

    @Nonnull
    @Override
    public BlockPos getPosition() {
        return this.getPos();
    }

    @Override
    public void update() {
        if (!this.getWorld().isRemote && dropBlock) {
            this.getWorld().destroyBlock(this.pos, true);
            return;
        }
        if (!this.getWorld().isRemote && this.updateNBT) {
            this.markBlockForUpdate();
            OMTNetworkingHandler.INSTANCE.sendToAllAround(new MessageTurretBase(this),
                                                          new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(),
                                                                                          this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 160));
            this.updateNBT = false;
        }

        if (!this.getWorld().isRemote) {
            for (EntityPlayerMP player : openClients) {
                OMTNetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this), player);
            }
        }

        ticks++;
        if (!this.getWorld().isRemote && ticks % 5 == 0) {

            //maxRange update, needs to happen on both client and server else GUI information may become disjoint.
            //moved by Keridos, added the sync to MessageTurretBase, should sync properly now too.
            setBaseUpperBoundRange();
            updateControllerSettings();

            if (this.currentMaxRange > this.upperBoundMaxRange) {
                this.currentMaxRange = upperBoundMaxRange;
            }

            if (!this.rangeOverridden) {
                this.currentMaxRange = upperBoundMaxRange;
            }

            //Concealment
            this.shouldConcealTurrets = TurretHeadUtil.hasConcealmentAddon(this);

            //Extenders
            this.storage.setCapacity(getMaxEnergyStorageWithExtenders());

            if (ticks % 20 == 0) {

                //ConfigGeneral
                ticks = 0;
                updateRedstoneReactor(this);
                if (updateRange) {
                    this.setCurrentMaxRange(upperBoundMaxRange);
                    updateRange = false;
                }

                this.scrubSyncPlayerList();
            }
        }
    }

    public NBTTagCompound writeMemoryCardNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("currentMaxRange", this.currentMaxRange);
        nbtTagCompound.setBoolean("attacksMobs", attacksMobs);
        nbtTagCompound.setBoolean("attacksNeutrals", attacksNeutrals);
        nbtTagCompound.setBoolean("attacksPlayers", attacksPlayers);
        nbtTagCompound.setBoolean("multiTargeting", multiTargeting);
        nbtTagCompound.setInteger("mode", mode.ordinal());
        NBTTagCompound camoTag = new NBTTagCompound();
        this.camoSettings.writeNBT(camoTag);
        nbtTagCompound.setTag("camoSettings", camoTag);
        if (this.rangeOverridden) {
            nbtTagCompound.setInteger("range", this.currentMaxRange);
        }
        NBTTagCompound trustedPlayers = new NBTTagCompound();
        nbtTagCompound.setTag("trustedPlayers", this.getTrustManager().writeToNBT(trustedPlayers));
        return nbtTagCompound;
    }

    public void readMemoryCardNBT(NBTTagCompound nbtTagCompound) {
        this.currentMaxRange = nbtTagCompound.getInteger("currentMaxRange");
        this.attacksMobs = nbtTagCompound.getBoolean("attacksMobs");
        this.attacksNeutrals = nbtTagCompound.getBoolean("attacksNeutrals");
        this.attacksPlayers = nbtTagCompound.getBoolean("attacksPlayers");
        this.multiTargeting = nbtTagCompound.getBoolean("multiTargeting");
        if (nbtTagCompound.hasKey("camoSettings")) {
            this.camoSettings = CamoSettings.getSettingsFromNBT(nbtTagCompound.getCompoundTag("camoSettings"));
        }
        if (nbtTagCompound.hasKey("mode")) {
            this.mode = EnumMachineMode.values()[nbtTagCompound.getInteger("mode")];
        } else {
            this.mode = EnumMachineMode.INVERTED;
        }
        if (nbtTagCompound.hasKey("range")) {
            this.rangeOverridden = true;
            this.currentMaxRange = nbtTagCompound.getInteger("range");
        } else {
            this.rangeOverridden = false;
            this.currentMaxRange = getUpperBoundMaxRange();
        }
        if (nbtTagCompound.hasKey("trustedPlayers")) {
            this.getTrustManager().readFromNBT((NBTTagCompound) nbtTagCompound.getTag("trustedPlayers"));
        }
    }

    private void setBaseUpperBoundRange() {
        int maxRange = 0;
        List<TileEntity> tileEntities = WorldUtil.getTouchingTileEntities(getWorld(), getPos());
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretHead) {
                maxRange = Math.max(((TurretHead) te).getTurretBaseRange() + TurretHeadUtil.getRangeUpgrades(this, (TurretHead) te), maxRange);
            }
        }
        this.upperBoundMaxRange = maxRange;
    }

    // Getters and Setters
    @Nullable
    @Override
    public FluidTank getTank() {
        return tank;
    }

    @Nullable
    @Override
    public FluidTank getCapabilityTank(EnumFacing facing) {
        return tank;
    }

    @Override
    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    @Override
    public boolean isActive() {
        boolean changedActive = false;
        if (controller != null && controller.overridesMode() && controller.getOverriddenMode() != this.mode) {
            refreshActive(controller.getOverriddenMode());
            changedActive = true;
        }
        if (!changedActive) {
            refreshActive(this.mode);
        }

        return active;
    }

    public void setUpdateRange(boolean updateRange) {
        this.updateRange = updateRange;
    }

    public boolean isAttacksMobs() {
        return attacksMobs;
    }

    public void setAttacksMobs(boolean attacksMobs) {
        this.attacksMobs = attacksMobs;
    }

    public boolean isAttacksNeutrals() {
        return attacksNeutrals;
    }

    public void setAttacksNeutrals(boolean attacksNeutrals) {
        this.attacksNeutrals = attacksNeutrals;
    }

    public boolean isAttacksPlayers() {
        return attacksPlayers;
    }

    public void setAttacksPlayers(boolean attacksPlayers) {
        this.attacksPlayers = attacksPlayers;
    }

    public boolean isMultiTargeting() {
        return multiTargeting;
    }

    public void setMultiTargeting(boolean multiTargeting) {
        this.multiTargeting = multiTargeting;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isComputerAccessible() {
        return (OpenComputersLoaded || ComputerCraftLoaded) && (this.tier == 5 || TurretHeadUtil.hasSerialPortAddon(
                this));
    }

    public void increaseKillCounter() {
        kills++;
    }

    public void increasePlayerKillCounter() {
        playerKills++;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }

    public int getCurrentMaxRange() {
        return currentMaxRange;
    }

    public void setCurrentMaxRange(int newCurrentMaxRange) {
        this.currentMaxRange = newCurrentMaxRange;
        this.rangeOverridden = true;

        if (currentMaxRange > this.upperBoundMaxRange) {
            this.currentMaxRange = this.upperBoundMaxRange;
        }

        if (currentMaxRange < 0) {
            this.currentMaxRange = 0;
        }
    }

    public int getUpperBoundMaxRange() {
        return upperBoundMaxRange;
    }

    public void setUpperBoundMaxRange(int upperBoundMaxRange) {
        this.upperBoundMaxRange = upperBoundMaxRange;
    }

    @Nullable
    public IBaseController getController() {
        return controller;
    }

    private int getMaxEnergyStorageWithExtenders() {
        int tier = getTier();
        switch (tier) {
            case 1:
                return OMTConfig.BASES.baseTierOne.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 2:
                return OMTConfig.BASES.baseTierTwo.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 3:
                return OMTConfig.BASES.baseTierThree.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 4:
                return OMTConfig.BASES.baseTierFour.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 5:
                return OMTConfig.BASES.baseTierFive.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
        }
        return 0;
    }

    // API Functions. TODO: Add more?

    /**
     * Try to register the contoller given at the turret base.
     *
     * @param controller instance of the controller to add.
     * @return true if added successfully
     */
    public boolean registerController(IBaseController controller) {
        if (this.controller != null) {
            return false;
        }
        this.controller = controller;
        return true;
    }

    /**
     * List of all entities around the turret base in its range.
     *
     * @return List of EntityLivingBase
     */
    public List<EntityLivingBase> getEntitiesWithinRange() {
        AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - currentMaxRange - 1, pos.getY() - currentMaxRange - 1,
                                               pos.getZ() - currentMaxRange - 1, pos.getX() + currentMaxRange + 1,
                                               pos.getY() + currentMaxRange + 1, pos.getZ() + currentMaxRange + 1);

        return this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);
    }

    public void setAllTurretsYawPitch(float yaw, float pitch) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te instanceof AbstractDirectedTurret) {
                ((AbstractDirectedTurret) te).setPitch(pitch);
                ((AbstractDirectedTurret) te).setYaw(yaw);
            }
        }
    }

    public boolean setTurretYawPitch(EnumFacing facing, float yaw, float pitch) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead instanceof AbstractDirectedTurret) {
            ((AbstractDirectedTurret) turretHead).setPitch(pitch);
            ((AbstractDirectedTurret) turretHead).setYaw(yaw);
            return true;
        }
        return false;
    }

    public void setAllTurretsForceFire(boolean state) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretHead) {
                ((TurretHead) te).setAutoFire(state);
            }
        }
    }

    public boolean setTurretForceFire(EnumFacing facing, boolean state) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead instanceof TurretHead) {
            ((TurretHead) turretHead).setAutoFire(state);
            return true;
        }
        return false;
    }

    public boolean forceShootTurret(EnumFacing facing) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        return (turretHead instanceof AbstractDirectedTurret && ((AbstractDirectedTurret) turretHead).forceShot());
    }

    @SuppressWarnings("deprecation")
    public int forceShootAllTurrets() {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        int successes = 0;
        for (TileEntity te : tileEntities) {
            if (te instanceof AbstractDirectedTurret) {
                successes += ((AbstractDirectedTurret) te).forceShot() ? 1 : 0;
            }
        }
        return successes;
    }

    // Mod Compatibility  functions:

    @Override
    public boolean requiresEnergy() {
        return true;
    }

    @Override
    public boolean deliversEnergy() {
        return false;
    }

    @Override
    public OMEnergyStorage getEnergyStorage() {
        return storage;
    }

    @Nullable
    @Override
    public OMLibNetwork getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(OMLibNetwork network) {
        this.network = network;
    }

    @Nonnull
    @Override
    public String getDeviceName() {
        return "TurretBase";
    }

    @Optional.Method(modid = "computercraft")
    @Override
    @Nonnull
    public String getType() {
        return "turret_base";
    }

    @Optional.Method(modid = "computercraft")
    @Override
    @Nonnull
    public String[] getMethodNames() {
        // list commands you want..
        return new String[]{commands.getOwner.toString(), commands.attacksPlayers.toString(),
                commands.setAttacksPlayers.toString(), commands.attacksMobs.toString(),
                commands.setAttacksMobs.toString(), commands.attacksNeutrals.toString(),
                commands.setAttacksNeutrals.toString(), commands.getTrustedPlayers.toString(),
                commands.getTrustedPlayer.toString(), commands.addTrustedPlayer.toString(),
                commands.removeTrustedPlayer.toString(), commands.changeAccessLevel.toString(),
                commands.getActive.toString(), commands.getMode.toString(), commands.getRedstone.toString(),
                commands.setMode.toString(), commands.getType.toString()};
    }

    @Optional.Method(modid = "computercraft")
    @Override
    @ParametersAreNonnullByDefault
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
        // method is command
        boolean b;
        int i;
        if (!isComputerAccessible()) {
            return new Object[]{"Computer access deactivated!"};
        }
        switch (commands.values()[method]) {
            case getOwner:
                return new Object[]{this.getOwner()};
            case attacksPlayers:
                return new Object[]{this.attacksPlayers};
            case setAttacksPlayers:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.attacksPlayers = b;
                return new Object[]{true};
            case attacksMobs:
                return new Object[]{this.attacksMobs};
            case setAttacksMobs:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.attacksMobs = b;
                return new Object[]{true};
            case attacksNeutrals:
                return new Object[]{this.attacksNeutrals};
            case setAttacksNeutrals:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.attacksNeutrals = b;
                return new Object[]{true};
            case getTrustedPlayers:
                return new Object[]{this.getTrustManager().getTrustedPlayersAsListMap()};
            case getTrustedPlayer:
                if (this.getTrustManager().getTrustedPlayer(arguments[0].toString()) != null) {
                    return new Object[]{this.getTrustManager().getTrustedPlayer(arguments[0].toString()).asMap()};
                }
            case addTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                if (!this.getTrustManager().addTrustedPlayer(arguments[0].toString())) {
                    return new Object[]{"Name not valid!"};
                }
                if (arguments[1].toString().equals("")) {
                    return new Object[]{"successfully added"};
                } else if (!(arguments[1] instanceof Integer)) {
                    return new Object[]{"wrong arguments"};
                }
                TrustedPlayer trustedPlayer = this.getTrustManager().getTrustedPlayer(arguments[0].toString());
                trustedPlayer.setAccessLevel(EnumAccessLevel.values()[(Integer) arguments[1]]);
                trustedPlayer.setUuid(getPlayerUUID(arguments[0].toString()));

                return new Object[]{true};
            case removeTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                this.getTrustManager().removeTrustedPlayer(arguments[0].toString());
                return new Object[]{true};
            case changeAccessLevel:
                if (this.getTrustManager().getTrustedPlayer(arguments[0].toString()) == null) {
                    return new Object[]{"Not found!"};
                } else if (!(arguments[1] instanceof Integer) || (Integer) arguments[1] < 0 || (Integer) arguments[1] > 3) {
                    return new Object[]{"Invalid Access Level!"};
                }
                trustedPlayer = this.getTrustManager().getTrustedPlayer(arguments[0].toString());
                trustedPlayer.setAccessLevel(EnumAccessLevel.values()[(Integer) arguments[1]]);
                return new Object[]{true};
            case getActive:
                return new Object[]{this.active};
            case getMode:
                return new Object[]{this.mode.getName()};
            case getRedstone:
                return new Object[]{this.redstone};
            case setMode:
                String arg = arguments[0].toString();
                if (!(arg.equals("0") || arg.equals("1") || arg.equals("2") || arg.equals("3"))) {
                    return new Object[]{"wrong arguments, expect number between 0 and 3"};
                }
                int mode = (Integer.valueOf(arguments[0].toString()));
                this.setMode(EnumMachineMode.values()[mode]);
                return new Object[]{true};
            case getType:
                return new Object[]{this.getType()};
            default:
                break;
        }
        return new Object[]{false};
    }

    @Optional.Method(modid = "computercraft")
    @Override
    public boolean equals(IPeripheral other) {
        return other.getType().equals(getType());
    }

    @Optional.Method(modid = "computercraft")
    @Override
    public void attach(@Nonnull IComputerAccess computer) {

    }

    @Optional.Method(modid = "computercraft")
    @Override
    public void detach(@Nonnull IComputerAccess computer) {

    }

    public enum commands {
        getOwner, attacksPlayers, setAttacksPlayers, attacksMobs, setAttacksMobs, attacksNeutrals, setAttacksNeutrals,
        getTrustedPlayers, getTrustedPlayer, addTrustedPlayer, removeTrustedPlayer, changeAccessLevel, getActive,
        getMode, getRedstone, setMode, getType
    }
}
