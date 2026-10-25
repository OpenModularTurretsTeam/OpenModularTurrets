package omtteam.openmodularturrets.tileentity.base;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import omtteam.omlib.api.network.ISyncableTE;
import omtteam.omlib.api.permission.TrustedPlayersManager;
import omtteam.omlib.api.render.camo.ICamoSupport;
import omtteam.omlib.api.tile.IDebugTile;
import omtteam.omlib.api.tile.IHasTargetingSettings;
import omtteam.omlib.api.tile.IHasTrustManager;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.omlib.core.camo.CamoPermissions;
import omtteam.omlib.network.MessageCamoSettings;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.power.EnergyCapabilities;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.InventoryNbt;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.omlib.util.NetworkUtil;
import omtteam.omlib.util.RandomUtil;
import omtteam.omlib.util.TargetingSettings;
import omtteam.omlib.util.camo.CamoSettings;
import omtteam.omlib.util.player.Player;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.api.tileentity.ITurretBase;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.BaseTier;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Neighbour;
import omtteam.openmodularturrets.inventory.EnumSlotType;
import omtteam.openmodularturrets.network.MessageTurretBase;
import omtteam.openmodularturrets.network.OMTNetworkingHandler;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;
import omtteam.openmodularturrets.util.OMTUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static omtteam.omlib.compat.OMLibModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compat.OMLibModCompatibility.OpenComputersLoaded;
import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntities;
import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntitiesByClass;

/**
 * The turret base tile entity. The game rules (targeting, ammo, energy, upgrades, addons, redstone mode) live in the
 * core {@link TurretBaseLogic} it owns; this class connects the logic to the world (inventory, neighbours, ticking,
 * syncing), keeps the 3.x NBT format and the 3.x API ({@link ITurretBase}; ComputerCraft and OpenComputers through
 * {@code compat.BaseComputerCommands}).
 */
public class TurretBase extends TileEntityOwnedBlock implements ITurretBase, IHasTrustManager, ISyncableTE, ICamoSupport,
        IDebugTile, ITickable, IHasTargetingSettings {
    private final List<EntityPlayerMP> openClients = new ArrayList<>(); // for GUI Stuff
    private final HashMap<EnumSlotType, List<Integer>> slotMap = new HashMap<>();
    private final TurretBaseWorld baseWorld = new TurretBaseWorld(this);
    protected CamoSettings camoSettings;
    protected int tier = BaseTier.MIN;
    protected IItemHandlerModifiable inventory;
    protected final TrustedPlayersManager trustManager;
    private TurretBaseLogic logic;
    /** Forge Energy / RF / Tesla access to the logic's energy store (recreated with the logic). */
    private EnergyCapabilities energy;
    /** Whether the attached turrets were scanned since the logic was created (done on the first server tick). */
    private boolean neighboursScanned;
    /** Set while reading NBT, so loading does not count as a change. */
    private boolean loading;
    private IBlockState camoBlockStateTemp;
    private int ticks;
    private boolean forceFire = false;

    /** The energy capacity and maximum input come from the tier's config through the logic. */
    public TurretBase(int tier, IBlockState camoState) {
        super();
        this.trustManager = new TrustedPlayersManager(this.getOwner(), this, OMTSecurityEnvironment.INSTANCE);
        this.tier = tier;
        this.camoBlockStateTemp = camoState;
        this.camoSettings = new CamoSettings();
        setupInventory();
        setupSlotMap();
        createLogic();
    }

    public TurretBase() {
        super();
        this.trustManager = new TrustedPlayersManager(this.getOwner(), this, OMTSecurityEnvironment.INSTANCE);
        setupInventory();
        setupSlotMap();
        createLogic();
    }

    /**
     * (Re)creates the logic for the current tier. Its energy store becomes the Forge energy storage of the base; the
     * state of a previous logic (energy, settings, counters) is kept.
     */
    private void createLogic() {
        TurretBaseLogic.State state = logic != null ? logic.getState() : null;
        logic = new TurretBaseLogic(tier, ConfigLogicSettings.INSTANCE, trustManager.core(), baseWorld,
                RandomUtil.random, this::onLogicChanged);
        energy = new EnergyCapabilities(new OMEnergyStorage(logic.getEnergy()));
        if (state != null) {
            logic.loadState(state);
        }
        neighboursScanned = false;
    }

    /** Saving for every change; client-relevant changes are also sent to the clients next tick. */
    private void onLogicChanged(boolean clientRelevant) {
        if (loading || world == null || world.isRemote) {
            return;
        }
        world.markChunkDirty(pos, this);
        if (clientRelevant) {
            this.updateNBT = true;
        }
    }

    /** The game rules of this base. */
    public TurretBaseLogic getLogic() {
        return logic;
    }

    /** The core face of a direction (both enums are in the same order). */
    public static Face toFace(EnumFacing facing) {
        return Face.values()[facing.ordinal()];
    }

    /**
     * Scans the six neighbours for turrets and tells the logic (1.20.1+ {@code updateNeighbours}). Called on the
     * first server tick and when a neighbour changes.
     */
    public void updateNeighbours() {
        if (world == null) {
            return;
        }
        Map<Face, Neighbour> attached = new EnumMap<>(Face.class);
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighbour = pos.offset(facing);
            if (!world.isBlockLoaded(neighbour)) {
                continue;
            }
            Block block = world.getBlockState(neighbour).getBlock();
            if (block instanceof BlockTurret) {
                attached.put(toFace(facing),
                        new Neighbour.Turret(((BlockTurret) block).getCoreType()));
            }
        }
        logic.updateNeighbours(attached);
        neighboursScanned = true;
    }

    /**
     * The current target of the turret on the given side of the base, if the base picked one.
     *
     * @param side the base face the turret sits on (from the base towards the turret)
     */
    @Nullable
    public EntityLivingBase getTargetEntity(EnumFacing side) {
        java.util.Optional<TargetEntity> target = logic.getTarget(toFace(side));
        return target.isPresent() && target.get() instanceof LivingTargetEntity
                ? ((LivingTargetEntity) target.get()).getEntity() : null;
    }

    /** The ammo slots, the part of the inventory other blocks may use. */
    public IItemHandler getCapabilityInventory(@Nullable EnumFacing facing) {
        return new RangedWrapper(inventory, 0, 9);
    }

    @Override
    public List<IItemHandler> getAmmoInventories() {
        return Collections.singletonList(this.getCapabilityInventory(EnumFacing.DOWN));
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

            @Override
            protected void onContentsChanged(int slot) {
                if (logic != null && !loading) {
                    logic.onInventoryChanged();
                }
            }
        };
    }

    private void setupSlotMap() {
        slotMap.clear();
        List<Integer> ammoList = new ArrayList<>();
        List<Integer> addonList = new ArrayList<>();
        List<Integer> upgradeList = new ArrayList<>();
        for (int i = 0; i < TurretBaseWorld.AMMO_SLOTS; i++) {
            ammoList.add(i);
        }
        for (int i = 0; i < BaseTier.addonSlots(tier); i++) {
            addonList.add(TurretBaseWorld.ADDON_START + i);
        }
        for (int i = 0; i < BaseTier.upgradeSlots(tier); i++) {
            upgradeList.add(TurretBaseWorld.UPGRADE_START + i);
        }
        slotMap.put(EnumSlotType.AmmoSlot, ammoList);
        slotMap.put(EnumSlotType.AddonSlot, addonList);
        slotMap.put(EnumSlotType.UpgradeSlot, upgradeList);
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

    // ---- NBT (3.x format) ----

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public NBTTagCompound saveToNBT(NBTTagCompound tag) {
        super.saveToNBT(tag); // owner
        energy.writeToNBT(tag);
        InventoryNbt.write(inventory, tag);
        trustManager.writeToNBT(tag);
        TurretBaseNbt.save(logic, tag);
        tag.setBoolean("forceFire", this.forceFire);
        tag.setInteger("tier", this.tier);
        getCamoSettings().writeNBT(tag);
        return tag;
    }

    @Override
    public void loadFromNBT(NBTTagCompound tag) {
        loading = true;
        try {
            int savedTier = tag.getInteger("tier");
            if (savedTier >= BaseTier.MIN && savedTier <= BaseTier.MAX && savedTier != this.tier) {
                this.tier = savedTier;
                setupSlotMap();
                createLogic();
            }
            super.loadFromNBT(tag); // owner
            energy.readFromNBT(tag);
            InventoryNbt.read(inventory, tag);
            trustManager.readFromNBT(tag);
            trustManager.setOwner(this.getOwner());
            logic.loadState(TurretBaseNbt.load(tag, logic.getEnergy().getAmount()));
            logic.onInventoryChanged();
            this.forceFire = tag.getBoolean("forceFire");
            this.camoSettings = CamoSettings.getSettingsFromNBT(tag);
            if (camoSettings.getCamoBlockState() != null) {
                this.camoBlockStateTemp = camoSettings.getCamoBlockState();
            } else {
                this.camoBlockStateTemp = getDefaultCamoState();
            }
        } finally {
            loading = false;
        }
    }

    @Override
    public void setOwner(Player owner) {
        this.trustManager.setOwner(owner);
        super.setOwner(owner);
    }

    @Override
    public TrustedPlayersManager getTrustManager() {
        return trustManager;
    }

    // ---- capabilities: energy (EnergyCapabilities) and the ammo slots ----

    @Override
    @ParametersAreNonnullByDefault
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || energy.hasCapability(capability)
                || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    @ParametersAreNonnullByDefault
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) getCapabilityInventory(facing);
        }
        T energyCapability = energy.getCapability(capability);
        return energyCapability != null ? energyCapability : super.getCapability(capability, facing);
    }

    public int getEnergyStored(@Nullable EnumFacing facing) {
        return energy.getStorage().getEnergyStored();
    }

    public int getMaxEnergyStored(@Nullable EnumFacing facing) {
        return energy.getStorage().getMaxEnergyStored();
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

    @Override
    public List<String> getDebugInfo() {
        List<String> debugInfo = new ArrayList<>();
        debugInfo.add("Active: " + isActive() + ", Redstone: " + getRedstone() + ", Mode: " + getMode().getName());
        debugInfo.add("Camo: " + this.camoSettings.getCamoBlockState().getBlock().getRegistryName());
        debugInfo.add("Force Fire: " + this.forceFire + ", UpperMaxRange: " + this.getMaxRange());
        debugInfo.add("Targets: " + logic.getTargetIds());
        return debugInfo;
    }

    @Override
    @Nonnull
    public TileEntity getCamoTileEntity() {
        return this;
    }

    @Override
    public void update() {
        if (this.getWorld().isRemote) {
            return;
        }
        if (dropBlock) {
            this.getWorld().destroyBlock(this.pos, true);
            return;
        }
        if (this.updateNBT) {
            this.markBlockForUpdate();
            this.informUpdate();
            this.updateNBT = false;
        }
        for (EntityPlayerMP player : openClients) {
            OMTNetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this), player);
        }

        if (!neighboursScanned) {
            updateNeighbours();
        }
        logic.tick(this.getWorld().getTotalWorldTime());

        ticks++;
        if (ticks % 20 == 0) {
            ticks = 0;
            this.scrubSyncPlayerList();
        }
    }

    @Override
    public void updateMaxRange() {
        logic.updateMaxRange();
    }

    @Override
    public void turretResetCaches() {
        logic.onInventoryChanged();
        for (TurretTileEntity turretHead : getTouchingTileEntitiesByClass(this.getWorld(), this.getPos(), TurretTileEntity.class)) {
            turretHead.triggerResetCaches();
        }
    }

    /** Whether the turrets of this base hide when they have nothing to shoot at (concealer addon). */
    public boolean shouldConcealTurrets() {
        return logic.isConcealingTurrets();
    }

    // Getters and Setters
    /** The whole inventory: ammo, addon and upgrade slots. */
    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    // ---- redstone mode (3.x TileEntityMachine API, held by the logic) ----

    public boolean isActive() {
        return logic.isActive();
    }

    public EnumMachineMode getMode() {
        return EnumMachineMode.fromCore(logic.getMode());
    }

    public void setMode(EnumMachineMode mode) {
        logic.setMode(mode.toCore());
    }

    public void toggleMode() {
        logic.cycleMode();
    }

    public boolean getRedstone() {
        return logic.getState().redstone();
    }

    public void setRedstone(boolean redstone) {
        logic.setRedstone(redstone);
    }

    // ---- targeting settings ----

    @Override
    public boolean isAttacksMobs() {
        return logic.getTargetingSettings().isTargetMobs();
    }

    @Override
    public void setAttacksMobs(boolean attacksMobs) {
        logic.setTargetMobs(attacksMobs);
    }

    @Override
    public boolean isAttacksNeutrals() {
        return logic.getTargetingSettings().isTargetPassive();
    }

    @Override
    public void setAttacksNeutrals(boolean attacksNeutrals) {
        logic.setTargetPassive(attacksNeutrals);
    }

    @Override
    public boolean isAttacksPlayers() {
        return logic.getTargetingSettings().isTargetPlayers();
    }

    @Override
    public void setAttacksPlayers(boolean attacksPlayers) {
        logic.setTargetPlayers(attacksPlayers);
    }

    @Override
    public boolean isMultiTargeting() {
        return logic.isMultiTargeting();
    }

    @Override
    public void setMultiTargeting(boolean multiTargeting) {
        logic.setMultiTargeting(multiTargeting);
    }

    @Override
    public int getTier() {
        return tier;
    }

    /** Changes the tier (client sync); the logic is recreated for the new tier and keeps its state. */
    @Override
    public void setTier(int tier) {
        if (tier != this.tier && tier >= BaseTier.MIN && tier <= BaseTier.MAX) {
            this.tier = tier;
            setupSlotMap();
            createLogic();
        }
    }

    @Override
    public boolean isComputerAccessible() {
        return (OpenComputersLoaded || ComputerCraftLoaded) && (this.tier == 5 || logic.hasAddon(AddonType.SERIAL_PORT));
    }

    @Override
    public void increaseKillCounter() {
        logic.increaseKillCounter();
    }

    @Override
    public void increasePlayerKillCounter() {
        logic.increasePlayerKillCounter();
    }

    @Override
    public int getKills() {
        return logic.getKills();
    }

    @Override
    public void setKills(int kills) {
        setCounters(kills, getPlayerKills());
    }

    @Override
    public int getPlayerKills() {
        return logic.getPlayerKills();
    }

    @Override
    public void setPlayerKills(int playerKills) {
        setCounters(getKills(), playerKills);
    }

    /** The logic only counts up; the client sync and the API set the counters through its state. */
    private void setCounters(int kills, int playerKills) {
        TurretBaseLogic.State s = logic.getState();
        logic.loadState(new TurretBaseLogic.State(s.energy(), s.mode(), s.redstone(), s.targetPlayers(),
                s.targetMobs(), s.targetPassive(), s.range(), s.maxRange(), s.multiTargeting(), kills, playerKills));
        onLogicChanged(true);
    }

    @Override
    public int getRange() {
        return logic.getTargetingSettings().getRange();
    }

    @Override
    public void setRange(int range) {
        logic.setRange(range);
    }

    @Override
    public int getMaxRange() {
        return logic.getTargetingSettings().getMaxRange();
    }

    @Override
    public void setMaxRange(int range) {
        logic.getTargetingSettings().setMaxRange(range);
    }

    public HashMap<EnumSlotType, List<Integer>> getSlotMap() {
        return slotMap;
    }

    /** A live view of the logic's targeting settings (3.x type). */
    @Override
    public TargetingSettings getTargetingSettings() {
        return TargetingSettings.of(logic.getTargetingSettings());
    }

    // API Functions. TODO: Add more?

    /**
     * List of all entities around the turret base in its range.
     *
     * @return List of EntityLivingBase
     */
    public List<EntityLivingBase> getEntitiesWithinRange() {
        int range = getRange();
        AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - range - 1, pos.getY() - range - 1,
                                               pos.getZ() - range - 1, pos.getX() + range + 1,
                                               pos.getY() + range + 1, pos.getZ() + range + 1);

        return this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);
    }

    @Override
    public void setAllTurretsYawPitch(float yaw, float pitch) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretTileEntity) {
                ((TurretTileEntity) te).setYawPitch(yaw, pitch);
            }
        }
    }

    @Override
    public boolean setTurretYawPitch(EnumFacing facing, float yaw, float pitch) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead instanceof TurretTileEntity) {
            ((TurretTileEntity) turretHead).setYawPitch(yaw, pitch);
            return true;
        }
        return false;
    }

    @Override
    public void setAllTurretsForceFire(boolean state) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretTileEntity) {
                ((TurretTileEntity) te).setAutoFire(state);
            }
        }
    }

    @Override
    public boolean setTurretForceFire(EnumFacing facing, boolean state) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead instanceof TurretTileEntity) {
            ((TurretTileEntity) turretHead).setAutoFire(state);
            return true;
        }
        return false;
    }

    @Override
    public boolean forceShootTurret(EnumFacing facing) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        return (turretHead instanceof TurretTileEntity && ((TurretTileEntity) turretHead).forceShot());
    }

    @Override
    public int forceShootAllTurrets() {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        int successes = 0;
        for (TileEntity te : tileEntities) {
            if (te instanceof TurretTileEntity) {
                successes += ((TurretTileEntity) te).forceShot() ? 1 : 0;
            }
        }
        return successes;
    }

    @Override
    public boolean isCamoEnabled() {
        return OMTConfig.BASES.allowBaseCamo;
    }

    /** Only the tier 4 and 5 bases have adjustable light. */
    @Override
    public boolean supportsLight() {
        return CamoLight.supportedByBaseTier(getTier());
    }

    @Override
    public boolean canSetCamo(EntityPlayer player) {
        return CamoPermissions.canSetDisguise(getTrustManager().core(), PlayerUtil.getPlayerInfo(player));
    }

    @Override
    public boolean canChangeLight(EntityPlayer player) {
        return CamoPermissions.canChangeLight(getTrustManager().core(), PlayerUtil.getPlayerInfo(player));
    }
}
