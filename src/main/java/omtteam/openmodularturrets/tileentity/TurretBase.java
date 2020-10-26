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
import omtteam.omlib.api.tile.IHasTargetingSettings;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.MessageCamoSettings;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.tileentity.TileEntityTrustedMachine;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.omlib.util.NetworkUtil;
import omtteam.omlib.util.TargetingSettings;
import omtteam.omlib.util.camo.CamoSettings;
import omtteam.omlib.util.world.WorldUtil;
import omtteam.openmodularturrets.api.network.IBaseController;
import omtteam.openmodularturrets.api.tileentity.ITurretBase;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.network.messages.MessageTurretBase;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.util.EnumSlotType;
import omtteam.openmodularturrets.util.OMTUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static omtteam.omlib.compatibility.OMLibModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.OMLibModCompatibility.OpenComputersLoaded;
import static omtteam.omlib.util.player.PlayerUtil.getPlayerUUID;
import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntities;
import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntitiesByClass;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "computercraft")}
)
public class TurretBase extends TileEntityTrustedMachine implements ITurretBase, IPeripheral, ICamoSupport, IDebugTile, IPowerExchangeTile, INetworkTile, ITickable, IHasTargetingSettings {
    public boolean shouldConcealTurrets;
    protected CamoSettings camoSettings;
    protected int tier;
    protected IItemHandlerModifiable inventory;
    protected FluidTank tank;
    private IBlockState camoBlockStateTemp;
    private boolean multiTargeting = false;
    private TargetingSettings targetingSettings;
    private int ticks;
    private boolean forceFire = false;
    private int kills;
    private int playerKills;
    private IBaseController controller;
    private OMLibNetwork network;
    private final List<EntityPlayerMP> openClients = new ArrayList<>(); // for GUI Stuff
    private final HashMap<EnumSlotType, List<Integer>> slotMap = new HashMap<>();
    protected HashMap<EnumFacing, Expander> expanderMap = new HashMap<>(); //Todo: use this (helpful for testing) for caching Expanders

    public TurretBase(int MaxEnergyStorage, int MaxIO, int tier, IBlockState camoState) {
        super();
        this.storage = new OMEnergyStorage(MaxEnergyStorage, MaxIO);
        this.targetingSettings = new TargetingSettings(false, true, false, 0, 0);
        this.tier = tier;
        this.camoBlockStateTemp = camoState;
        this.mode = EnumMachineMode.INVERTED;
        this.camoSettings = new CamoSettings();
        setupInventory();
        setupSlotMap();
        updateExpanders();
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
            ItemStack redstoneBlock = TurretHeadUtil.deductItemStackFromInventories(new ItemStack(
                    Blocks.REDSTONE_BLOCK), base, null);

            if (redstoneBlock != ItemStack.EMPTY) {
                base.storage.modifyEnergyStored(OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen * 9);
                return;
            }
        }
        if (OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen < (storage.getMaxEnergyStored() - storage.getEnergyStored())) {
            ItemStack redstone = TurretHeadUtil.deductItemStackFromInventories(new ItemStack(
                    Items.REDSTONE), base, null);

            if (redstone != ItemStack.EMPTY) {
                storage.modifyEnergyStored(OMTConfig.MISCELLANEOUS.redstoneReactorAddonGen);
            }
        }
    }

    public void updateExpanders() {
        if (world != null) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity te = world.getTileEntity(this.getPos().offset(facing));
                if (te instanceof Expander) {
                    expanderMap.put(facing, (Expander) te);
                } else {
                    expanderMap.put(facing, null);
                }
            }
        }
    }

    @Override
    public IItemHandler getCapabilityInventory(EnumFacing facing) {
        return new RangedWrapper(inventory, 0, 9);
    }

    @Override
    public List<IItemHandler> getAmmoInventories() {
        ArrayList<IItemHandler> list = new ArrayList<>();
        list.add(this.getCapabilityInventory(EnumFacing.DOWN)); // local ammo
        for (EnumFacing facing : EnumFacing.VALUES) {
            Expander te = expanderMap.get(facing);
            if (te != null && !te.isPowerExpander()) {
                list.add(te.inventory);
            }
        }
        return list;
    }

    protected void setupInventory() {
        slotMap.clear();
        inventory = new ItemStackHandler(13) {
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

    private void setupSlotMap() {
        List<Integer> ammoList = new ArrayList<>();
        List<Integer> addonList = new ArrayList<>();
        List<Integer> upgradeList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ammoList.add(i);
        }
        slotMap.put(EnumSlotType.AmmoSlot, ammoList);
        switch (tier) {
            case 5:
                upgradeList.add(12);
            case 4:
            case 3:
            case 2:
                upgradeList.add(11);
                slotMap.put(EnumSlotType.UpgradeSlot, upgradeList);
                addonList.add(9);
                addonList.add(10);
                slotMap.put(EnumSlotType.AddonSlot, addonList);
                break;
            case 1:
                slotMap.put(EnumSlotType.AddonSlot, addonList);
                slotMap.put(EnumSlotType.UpgradeSlot, upgradeList);
        }
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
        tag.setBoolean("shouldConcealTurrets", this.shouldConcealTurrets);
        tag.setBoolean("multiTargeting", this.multiTargeting);
        tag.setBoolean("forceFire", this.forceFire);
        tag.setInteger("tier", this.tier);
        tag.setInteger("mode", this.mode.ordinal());
        tag.setInteger("kills", this.kills);
        targetingSettings.writeToNBT(tag);
        camoSettings.writeNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.targetingSettings = TargetingSettings.readFromNBT(tag);
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
        setupSlotMap();
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
    public void informUpdate() {
        OMTNetworkingHandler.INSTANCE.
                sendToAllTracking(new MessageTurretBase(this), NetworkUtil.
                        getTargetPointFromBlockPos(this.getWorld().provider.getDimension(), this.pos, 100));
    }

    private void updateControllerSettings() {
        if (controller != null) {
            targetingSettings = controller.getTargetingSettings();
        }
    }

    @Override
    public List<String> getDebugInfo() {
        List<String> debugInfo = new ArrayList<>();
        debugInfo.add("Camo: " + this.camoSettings.getCamoBlockState().getBlock().getRegistryName());
        debugInfo.add("Force Fire: " + this.forceFire + ", UpperMaxRange: " + this.targetingSettings.getMaxRange());
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
            this.informUpdate();
            this.updateNBT = false;
        }

        if (!this.getWorld().isRemote) {
            for (EntityPlayerMP player : openClients) {
                OMTNetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this), player);
            }
        }

        ticks++;
        if (!this.getWorld().isRemote && ticks % 5 == 0) {
            //moved by Keridos, added the sync to MessageTurretBase, should sync properly now too.
            updateControllerSettings();

            //Concealment
            this.shouldConcealTurrets = TurretHeadUtil.hasConcealmentAddon(this);

            //Extenders
            this.storage.setCapacity(getMaxEnergyStorageWithExtenders());

            if (ticks % 20 == 0) {
                //ConfigGeneral
                ticks = 0;
                updateRedstoneReactor(this);

                if (this.targetingSettings.getRange() > this.targetingSettings.getMaxRange()) {
                    this.targetingSettings.setRange(this.targetingSettings.getMaxRange());
                }
                this.scrubSyncPlayerList();
            }
        }
    }

    public NBTTagCompound writeMemoryCardNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.targetingSettings.writeToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("multiTargeting", multiTargeting);
        nbtTagCompound.setInteger("mode", mode.ordinal());
        NBTTagCompound trustedPlayers = new NBTTagCompound();
        nbtTagCompound.setTag("trustedPlayers", this.getTrustManager().writeToNBT(trustedPlayers));
        return nbtTagCompound;
    }

    public void readMemoryCardNBT(NBTTagCompound nbtTagCompound) {
        this.multiTargeting = nbtTagCompound.getBoolean("multiTargeting");
        if (nbtTagCompound.hasKey("mode")) {
            this.mode = EnumMachineMode.values()[nbtTagCompound.getInteger("mode")];
        } else {
            this.mode = EnumMachineMode.INVERTED;
        }
        this.targetingSettings = TargetingSettings.readFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("trustedPlayers")) {
            this.getTrustManager().readFromNBT((NBTTagCompound) nbtTagCompound.getTag("trustedPlayers"));
        }
    }

    public void updateMaxRange() {
        int maxRange = 0;
        List<TileEntity> tileEntities = WorldUtil.getTouchingTileEntities(getWorld(), getPos());
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretHead) {
                maxRange = Math.max(((TurretHead) te).getTurretBaseRange() + TurretHeadUtil.getRangeUpgrades(this, (TurretHead) te), maxRange);
            }
        }
        this.targetingSettings.setMaxRange(maxRange);
    }

    public void turretResetCaches() {
        for (TurretHead turretHead : getTouchingTileEntitiesByClass(this.getWorld(), this.getPos(), TurretHead.class)) {
            turretHead.triggerResetCaches();
        }
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

    @Override
    public boolean isAttacksMobs() {
        return this.targetingSettings.isTargetMobs();
    }

    @Override
    public void setAttacksMobs(boolean attacksMobs) {
        this.targetingSettings.setTargetMobs(attacksMobs);
    }

    @Override
    public boolean isAttacksNeutrals() {
        return this.targetingSettings.isTargetPassive();
    }

    @Override
    public void setAttacksNeutrals(boolean attacksNeutrals) {
        this.targetingSettings.setTargetPassive(attacksNeutrals);
    }

    @Override
    public boolean isAttacksPlayers() {
        return this.targetingSettings.isTargetPlayers();
    }

    @Override
    public void setAttacksPlayers(boolean attacksPlayers) {
        this.targetingSettings.setTargetPlayers(attacksPlayers);
    }

    @Override
    public boolean isMultiTargeting() {
        return multiTargeting;
    }

    @Override
    public void setMultiTargeting(boolean multiTargeting) {
        this.multiTargeting = multiTargeting;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    public boolean isComputerAccessible() {
        return (OpenComputersLoaded || ComputerCraftLoaded) && (this.tier == 5 || TurretHeadUtil.hasSerialPortAddon(
                this));
    }

    @Override
    public void increaseKillCounter() {
        kills++;
    }

    @Override
    public void increasePlayerKillCounter() {
        playerKills++;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public int getPlayerKills() {
        return playerKills;
    }

    @Override
    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }

    @Override
    public int getRange() {
        return targetingSettings.getRange();
    }

    @Override
    public void setRange(int range) {
        this.updateMaxRange();
        this.targetingSettings.setRange(range);
    }

    @Override
    public int getMaxRange() {
        return targetingSettings.getMaxRange();
    }

    @Override
    public void setMaxRange(int range) {
        this.targetingSettings.setMaxRange(range);
    }

    @Nullable
    @Override
    public IBaseController getController() {
        return controller;
    }

    public HashMap<EnumFacing, Expander> getExpanderMap() {
        return expanderMap;
    }

    public HashMap<EnumSlotType, List<Integer>> getSlotMap() {
        return slotMap;
    }

    private int getMaxEnergyStorageWithExtenders() {
        int tier = getTier();
        switch (tier) {
            case 1:
                return OMTConfig.BASES.baseTierOne.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this);
            case 2:
                return OMTConfig.BASES.baseTierTwo.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this);
            case 3:
                return OMTConfig.BASES.baseTierThree.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this);
            case 4:
                return OMTConfig.BASES.baseTierFour.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this);
            case 5:
                return OMTConfig.BASES.baseTierFive.baseMaxCharge + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this);
        }
        return 0;
    }

    @Override
    public TargetingSettings getTargetingSettings() {
        return targetingSettings;
    }

    public void setTargetingSettings(TargetingSettings targetingSettings) {
        this.targetingSettings = targetingSettings;
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
        AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - targetingSettings.getRange() - 1, pos.getY() - targetingSettings.getRange() - 1,
                                               pos.getZ() - targetingSettings.getRange() - 1, pos.getX() + targetingSettings.getRange() + 1,
                                               pos.getY() + targetingSettings.getRange() + 1, pos.getZ() + targetingSettings.getRange() + 1);

        return this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);
    }

    @Override
    public void setAllTurretsYawPitch(float yaw, float pitch) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te instanceof AbstractDirectedTurret) {
                ((AbstractDirectedTurret) te).setPitch(pitch);
                ((AbstractDirectedTurret) te).setYaw(yaw);
            }
        }
    }

    @Override
    public boolean setTurretYawPitch(EnumFacing facing, float yaw, float pitch) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead instanceof AbstractDirectedTurret) {
            ((AbstractDirectedTurret) turretHead).setPitch(pitch);
            ((AbstractDirectedTurret) turretHead).setYaw(yaw);
            return true;
        }
        return false;
    }

    @Override
    public void setAllTurretsForceFire(boolean state) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretHead) {
                ((TurretHead) te).setAutoFire(state);
            }
        }
    }

    @Override
    public boolean setTurretForceFire(EnumFacing facing, boolean state) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead instanceof TurretHead) {
            ((TurretHead) turretHead).setAutoFire(state);
            return true;
        }
        return false;
    }

    @Override
    public boolean forceShootTurret(EnumFacing facing) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        return (turretHead instanceof AbstractDirectedTurret && ((AbstractDirectedTurret) turretHead).forceShot());
    }

    @Override
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
        return new String[]{commands.getOwner.toString(), commands.isAttacksPlayers.toString(),
                commands.setAttacksPlayers.toString(), commands.isAttacksMobs.toString(),
                commands.setAttacksMobs.toString(), commands.isAttacksNeutrals.toString(),
                commands.setAttacksNeutrals.toString(), commands.getTrustedPlayers.toString(),
                commands.getTrustedPlayer.toString(), commands.addTrustedPlayer.toString(),
                commands.removeTrustedPlayer.toString(), commands.changeAccessLevel.toString(),
                commands.getActive.toString(), commands.getMode.toString(), commands.getRedstone.toString(),
                commands.setMode.toString(), commands.getType.toString(), commands.setAllAutoForceFire.toString(),
                commands.setTurretAutoForceFire.toString(), commands.forceShootAllTurrets.toString(),
                commands.forceShootTurret.toString(), commands.setAllYawPitch.toString(),
                commands.setTurretYawPitch.toString(), commands.getMaxEnergyStorage.toString(), commands.getCurrentEnergyStorage.toString()};
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
                return new Object[]{this.getOwner().getName()};
            case isAttacksPlayers:
                return new Object[]{this.targetingSettings.isTargetPlayers()};
            case setAttacksPlayers:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"Wrong parameters!"};
                }
                b = (arguments[0].toString().equals("true"));
                this.targetingSettings.setTargetPlayers(b);
                return new Object[]{true};
            case isAttacksMobs:
                return new Object[]{this.targetingSettings.isTargetMobs()};
            case setAttacksMobs:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"Wrong parameters!"};
                }
                b = (arguments[0].toString().equals("true"));
                this.targetingSettings.setTargetMobs(b);
                return new Object[]{true};
            case isAttacksNeutrals:
                return new Object[]{this.targetingSettings.isTargetPassive()};
            case setAttacksNeutrals:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"Wrong parameters!"};
                }
                b = (arguments[0].toString().equals("true"));
                this.targetingSettings.setTargetPassive(b);
                return new Object[]{true};
            case getTrustedPlayers:
                return new Object[]{this.getTrustManager().getTrustedPlayersAsListMap()};
            case getTrustedPlayer:
                if (this.getTrustManager().getTrustedPlayer(arguments[0].toString()) != null) {
                    return new Object[]{this.getTrustManager().getTrustedPlayer(arguments[0].toString()).asMap()};
                }
            case addTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"Wrong parameters!"};
                }
                if (!this.getTrustManager().addTrustedPlayer(arguments[0].toString())) {
                    return new Object[]{"Name not valid!"};
                }
                if (arguments[1].toString().equals("")) {
                    return new Object[]{"successfully added"};
                } else if (!(arguments[1] instanceof Integer)) {
                    return new Object[]{"Wrong parameters!"};
                }
                TrustedPlayer trustedPlayer = this.getTrustManager().getTrustedPlayer(arguments[0].toString());
                trustedPlayer.setAccessLevel(EnumAccessLevel.values()[(Integer) arguments[1]]);
                trustedPlayer.setUuid(getPlayerUUID(arguments[0].toString()));

                return new Object[]{true};
            case removeTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"Wrong parameters!"};
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
                    return new Object[]{"Wrong parameters! expects number between 0 and 3"};
                }
                int mode = (Integer.parseInt(arguments[0].toString()));
                this.setMode(EnumMachineMode.values()[mode]);
                return new Object[]{true};
            case getType:
                return new Object[]{this.getType()};
            case setAllAutoForceFire:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"Wrong parameters!"};
                }
                b = (arguments[0].toString().equals("true"));
                this.setAllTurretsForceFire(b);
                return new Object[]{true};
            case setTurretAutoForceFire:
                try {
                    int t = Integer.parseInt(arguments[0].toString());
                    if (!(t < 0 || t > 6) || !(arguments[1].toString().equals("true") || arguments[1].toString().equals("false")))
                        return new Object[]{"Wrong parameters!"};
                    return new Object[]{this.setTurretForceFire(EnumFacing.getFront(t), Boolean.parseBoolean(arguments[1].toString()))};
                } catch (Exception e) {
                    return new Object[]{"Wrong parameters!"};
                }
            case forceShootAllTurrets:
                return new Object[]{this.forceShootAllTurrets()};
            case forceShootTurret:
                try {
                    int t = Integer.parseInt(arguments[0].toString());
                    if (!(t < 0 || t > 6))
                        return new Object[]{"Wrong parameters!"};
                    return new Object[]{this.forceShootTurret(EnumFacing.getFront(t))};
                } catch (Exception e) {
                    return new Object[]{"Wrong parameters!"};
                }
            case setAllYawPitch:
                try {
                    this.setAllTurretsYawPitch(Float.parseFloat(arguments[0].toString()),
                                               Float.parseFloat(arguments[1].toString()));
                    return new Object[]{"set yaw and pitch successfully"};
                } catch (Exception e) {
                    return new Object[]{"Wrong parameters!"};
                }
            case setTurretYawPitch:
                try {
                    this.setTurretYawPitch(EnumFacing.getFront(Integer.parseInt(arguments[0].toString())),
                                           Float.parseFloat(arguments[1].toString()),
                                           Float.parseFloat(arguments[2].toString()));
                    return new Object[]{"setting successful"};
                } catch (Exception e) {
                    return new Object[]{"Wrong parameters!"};
                }
            case getMaxEnergyStorage:
                return new Object[]{this.getMaxEnergyStorageWithExtenders()};

            case getCurrentEnergyStorage:
                return new Object[]{this.getEnergyStored(null)};
            default:
                break;
        }
        return new Object[]{"Nonexisting command"};
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
        getOwner, isAttacksPlayers, setAttacksPlayers, isAttacksMobs, setAttacksMobs, isAttacksNeutrals, setAttacksNeutrals,
        getTrustedPlayers, getTrustedPlayer, addTrustedPlayer, removeTrustedPlayer, changeAccessLevel, getActive,
        getMode, getRedstone, setMode, getType, setAllAutoForceFire, setTurretAutoForceFire, forceShootAllTurrets,
        forceShootTurret, setAllYawPitch, setTurretYawPitch, getMaxEnergyStorage, getCurrentEnergyStorage
    }
}
