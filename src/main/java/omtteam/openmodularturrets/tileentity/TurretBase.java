package omtteam.openmodularturrets.tileentity;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import omtteam.omlib.api.IDebugTile;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.EnumMachineMode;
import omtteam.omlib.tileentity.ICamoSupport;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.tileentity.TileEntityTrustedMachine;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.omlib.util.WorldUtil;
import omtteam.omlib.util.compat.ItemStackList;
import omtteam.omlib.util.compat.ItemStackTools;
import omtteam.openmodularturrets.api.IBaseController;
import omtteam.openmodularturrets.api.network.IPowerExchangeTile;
import omtteam.openmodularturrets.api.network.OMTNetwork;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.items.AddonMetaItem;
import omtteam.openmodularturrets.items.UpgradeMetaItem;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.TargetingSettings;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static omtteam.omlib.compatibility.ModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.ModCompatibility.OpenComputersLoaded;
import static omtteam.omlib.util.BlockUtil.getBlockStateFromNBT;
import static omtteam.omlib.util.BlockUtil.writeBlockFromStateToNBT;
import static omtteam.omlib.util.GeneralUtil.getMachineModeLocalization;
import static omtteam.omlib.util.PlayerUtil.getPlayerUUID;
import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;
import static omtteam.openmodularturrets.util.OMTUtil.isItemStackValidAmmo;

@SuppressWarnings("unused")
@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft")}
)
public class TurretBase extends TileEntityTrustedMachine implements IPeripheral, ICamoSupport, IDebugTile, IPowerExchangeTile {
    public int trustedPlayerIndex = 0;
    protected IBlockState camoBlockState;

    public boolean shouldConcealTurrets;
    private boolean multiTargeting = false;
    private int currentMaxRange;
    private int upperBoundMaxRange;
    private boolean rangeOverridden;
    private boolean attacksMobs;
    private boolean attacksNeutrals;
    private boolean attacksPlayers;
    private int ticks;
    private boolean computerAccessible = false;
    private ArrayList<IComputerAccess> comp;
    protected int tier;
    private boolean forceFire = false;
    private int kills;
    private int playerKills;
    private IBaseController controller;
    private OMTNetwork network;

    public TurretBase() {
        super();
        this.inventory = ItemStackList.create(13);
    }

    public TurretBase(int MaxEnergyStorage, int MaxIO, int tier, IBlockState camoState) {
        super();
        this.currentMaxRange = 0;
        this.upperBoundMaxRange = 0;
        this.rangeOverridden = false;
        this.storage = new OMEnergyStorage(MaxEnergyStorage, MaxIO);
        this.attacksMobs = true;
        this.attacksNeutrals = true;
        this.attacksPlayers = false;
        this.inventory = ItemStackList.create(tier == 5 ? 13 : tier == 4 ? 12 : tier == 3 ? 12 : tier == 2 ? 12 : 9);
        this.tier = tier;
        this.camoBlockState = camoState;
        this.mode = EnumMachineMode.INVERTED;
        this.maxStorageEU = tier * 7500D;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getDefaultCamoState() {
        return ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation(Reference.MOD_ID + ":" + OMTNames.Blocks.turretBase)).getStateFromMeta(this.tier - 1);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("currentMaxRange", this.currentMaxRange);
        nbtTagCompound.setInteger("upperBoundMaxRange", this.upperBoundMaxRange);
        nbtTagCompound.setBoolean("rangeOverridden", this.rangeOverridden);
        nbtTagCompound.setBoolean("attacksMobs", this.attacksMobs);
        nbtTagCompound.setBoolean("attacksNeutrals", this.attacksNeutrals);
        nbtTagCompound.setBoolean("attacksPlayers", this.attacksPlayers);
        nbtTagCompound.setBoolean("computerAccessible", this.computerAccessible);
        nbtTagCompound.setBoolean("shouldConcealTurrets", this.shouldConcealTurrets);
        nbtTagCompound.setBoolean("multiTargeting", this.multiTargeting);
        nbtTagCompound.setBoolean("forceFire", this.forceFire);
        nbtTagCompound.setInteger("tier", this.tier);
        nbtTagCompound.setInteger("mode", this.mode.ordinal());
        nbtTagCompound.setInteger("kills", this.kills);
        writeBlockFromStateToNBT(nbtTagCompound, this.camoBlockState);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.currentMaxRange = nbtTagCompound.getInteger("currentMaxRange");
        this.upperBoundMaxRange = nbtTagCompound.getInteger("upperBoundMaxRange");
        this.rangeOverridden = nbtTagCompound.getBoolean("rangeOverridden");
        this.attacksMobs = nbtTagCompound.getBoolean("attacksMobs");
        this.attacksNeutrals = nbtTagCompound.getBoolean("attacksNeutrals");
        this.attacksPlayers = nbtTagCompound.getBoolean("attacksPlayers");
        this.shouldConcealTurrets = nbtTagCompound.getBoolean("shouldConcealTurrets");
        this.multiTargeting = nbtTagCompound.getBoolean("multiTargeting");
        this.forceFire = nbtTagCompound.getBoolean("forceFire");
        this.tier = nbtTagCompound.getInteger("tier");
        if (nbtTagCompound.hasKey("mode")) {
            this.mode = EnumMachineMode.values()[nbtTagCompound.getInteger("mode")];
        } else {
            this.mode = EnumMachineMode.INVERTED;
        }
        this.computerAccessible = nbtTagCompound.hasKey("computerAccessible") && nbtTagCompound.getBoolean("computerAccessible");
        if (getBlockStateFromNBT(nbtTagCompound) != null) {
            this.camoBlockState = getBlockStateFromNBT(nbtTagCompound);
        } else {
            this.camoBlockState = getDefaultCamoState();
        }
        if (nbtTagCompound.hasKey("kills")) {
            this.kills = nbtTagCompound.getInteger("kills");
        } else {
            this.kills = 0;
        }
        if (nbtTagCompound.hasKey("playerKills")) {
            this.kills = nbtTagCompound.getInteger("playerKills");
        } else {
            this.playerKills = 0;
        }
        this.maxStorageEU = tier * 7500D;
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

    private void updateControllerSettings() {
        if (controller != null) {
            TargetingSettings settings = controller.getTargetingSettings();
            this.attacksMobs = settings.isTargetMobs();
            this.attacksNeutrals = settings.isTargetPassive();
            this.attacksPlayers = settings.isTargetPlayers();
            this.currentMaxRange = settings.getMaxRange();

            this.trustedPlayers = controller.getTrustedPlayerList();
        }
    }

    @Override
    public void update() {
        super.update();
        if (!this.getWorld().isRemote && dropBlock) {
            this.getWorld().destroyBlock(this.pos, true);
            return;
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

            //Thaumcraft
            /*if (ModCompatibility.ThaumcraftLoaded && TurretHeadUtil.hasPotentiaUpgradeAddon(this)) {
                if (amountOfPotentia > 0.05F && !(storage.getMaxEnergyLevel() - storage.getEnergyLevel() == 0)) {
                    if (VisNetHandler.drainVis(this.getWorld(), xCoord, yCoord, zCoord, Aspect.ORDER, 5) == 5) {
                        this.amountOfPotentia = this.amountOfPotentia - 0.05F;
                        this.storage.modifyEnergyStored(Math.round(OMTConfigHandler.getPotentiaToRFRatio() * 5));
                    } else {
                        this.amountOfPotentia = this.amountOfPotentia - 0.05F;
                        this.storage.modifyEnergyStored(Math.round(OMTConfigHandler.getPotentiaToRFRatio() / 2));
                    }
                }
            }*/

            if (ticks % 20 == 0) {

                //General
                ticks = 0;
                updateRedstoneReactor(this);

                if (network != null && network.getController() != null) {
                    registerController(network.getController());
                }

                //Computers
                this.computerAccessible = (OpenComputersLoaded || ComputerCraftLoaded) && TurretHeadUtil.hasSerialPortAddon(
                        this);
            }
        }
    }

    @Override
    public List<String> getDebugInfo() {
        List<String> debugInfo = new ArrayList<>();
        debugInfo.add("Camo: " + this.camoBlockState.getBlock().getRegistryName() + ", computerAccess: " + this.computerAccessible);
        debugInfo.add("Force Fire: " + this.forceFire + ", UpperMaxRange: " + this.upperBoundMaxRange);
        return debugInfo;
    }

    @Nonnull
    @Override
    public BlockPos getPosition() {
        return this.getPos();
    }

    private void setBaseUpperBoundRange() {
        int maxRange = upperBoundMaxRange;
        List<TileEntity> tileEntities = WorldUtil.getTouchingTileEntities(getWorld(), getPos());
        for (TileEntity te : tileEntities) {
            if (te != null && te instanceof TurretHead) {
                maxRange = Math.max(((TurretHead) te).getTurretRange() + TurretHeadUtil.getRangeUpgrades(this), maxRange);
            }
        }
        this.upperBoundMaxRange = maxRange;
    }

    public NBTTagCompound writeMemoryCardNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("currentMaxRange", this.currentMaxRange);
        nbtTagCompound.setBoolean("attacksMobs", attacksMobs);
        nbtTagCompound.setBoolean("attacksNeutrals", attacksNeutrals);
        nbtTagCompound.setBoolean("attacksPlayers", attacksPlayers);
        nbtTagCompound.setBoolean("multiTargeting", multiTargeting);
        nbtTagCompound.setInteger("mode", mode.ordinal());
        if (this.rangeOverridden) {
            nbtTagCompound.setInteger("range", this.currentMaxRange);
        }
        nbtTagCompound.setTag("trustedPlayers", getTrustedPlayersAsNBT());
        return nbtTagCompound;
    }

    public void readMemoryCardNBT(NBTTagCompound nbtTagCompound) {
        this.currentMaxRange = nbtTagCompound.getInteger("currentMaxRange");
        this.attacksMobs = nbtTagCompound.getBoolean("attacksMobs");
        this.attacksNeutrals = nbtTagCompound.getBoolean("attacksNeutrals");
        this.attacksPlayers = nbtTagCompound.getBoolean("attacksPlayers");
        this.multiTargeting = nbtTagCompound.getBoolean("multiTargeting");
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
        buildTrustedPlayersFromNBT(nbtTagCompound.getTagList("trustedPlayers", 10));
    }

    private int getMaxEnergyStorageWithExtenders() {
        int tier = getTier();
        switch (tier) {
            case 1:
                return OMTConfigHandler.getBaseTierOneMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 2:
                return OMTConfigHandler.getBaseTierTwoMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 3:
                return OMTConfigHandler.getBaseTierThreeMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 4:
                return OMTConfigHandler.getBaseTierFourMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 5:
                return OMTConfigHandler.getBaseTierFiveMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
        }
        return 0;
    }

    @Override
    public TileEntityOwnedBlock getOwnedBlock() {
        return this;
    }

    // Getters and Setters

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
        return computerAccessible;
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

    public int getPlayerKills() {
        return playerKills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }

    public int getCurrentMaxRange() {
        return currentMaxRange;
    }

    public int getUpperBoundMaxRange() {
        return upperBoundMaxRange;
    }

    @Nullable
    public IBaseController getController() {
        return controller;
    }

    @Nonnull
    @Override
    public IBlockState getCamoState() {
        return camoBlockState != null ? camoBlockState : this.getDefaultCamoState();
    }

    @Override
    public void setCamoState(IBlockState state) {
        this.camoBlockState = state;
    }

    // Inventory Functions

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canExtractItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        if (i < 9) {
            return isItemStackValidAmmo(stack);
        } else if (i <= 10) {
            return stack.getItem() instanceof AddonMetaItem;
        } else if (i <= 12) {
            return stack.getItem() instanceof UpgradeMetaItem;
        }
        return false;
    }

    private static void updateRedstoneReactor(TurretBase base) {
        OMEnergyStorage storage = (OMEnergyStorage) base.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if (!TurretHeadUtil.hasRedstoneReactor(base) || storage == null) {
            return;
        }

        if (OMTConfigHandler.getRedstoneReactorAddonGen() < (storage.getMaxEnergyStored() - storage.getEnergyStored())) {

            //Prioritise redstone blocks
            ItemStack redstoneBlock = TurretHeadUtil.getSpecificItemStackBlockFromBase(base, new ItemStack(
                    Blocks.REDSTONE_BLOCK));

            if (redstoneBlock == ItemStackTools.getEmptyStack()) {
                redstoneBlock = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorld(),
                        new ItemStack(Blocks.REDSTONE_BLOCK),
                        base);
            }

            if (redstoneBlock != ItemStackTools.getEmptyStack() && OMTConfigHandler.getRedstoneReactorAddonGen() * 9
                    < (storage.getMaxEnergyStored() - storage.getEnergyStored())) {
                base.storage.modifyEnergyStored(OMTConfigHandler.getRedstoneReactorAddonGen() * 9);
                return;
            }

            ItemStack redstone = TurretHeadUtil.getSpecificItemStackItemFromBase(base, new ItemStack(Items.REDSTONE));

            if (redstone == ItemStackTools.getEmptyStack()) {
                redstone = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorld(),
                        new ItemStack(Items.REDSTONE), base);
            }

            if (redstone != ItemStackTools.getEmptyStack()) {
                storage.modifyEnergyStored(OMTConfigHandler.getRedstoneReactorAddonGen());
            }
        }
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
            if (te != null && te instanceof TurretHead) {
                ((TurretHead) te).setPitch(pitch);
                ((TurretHead) te).setYaw(yaw);
            }
        }
    }

    public boolean setTurretYawPitch(EnumFacing facing, float yaw, float pitch) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead != null && turretHead instanceof TurretHead) {
            ((TurretHead) turretHead).setPitch(pitch);
            ((TurretHead) turretHead).setYaw(yaw);
            return true;
        }
        return false;
    }

    public void setAllTurretsForceFire(boolean state) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te != null && te instanceof TurretHead) {
                ((TurretHead) te).setAutoFire(state);
            }
        }
    }

    public boolean setTurretForceFire(EnumFacing facing, boolean state) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead != null && turretHead instanceof TurretHead) {
            ((TurretHead) turretHead).setAutoFire(state);
            return true;
        }
        return false;
    }

    public boolean forceShootTurret(EnumFacing facing) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        return (turretHead != null && turretHead instanceof TurretHead && ((TurretHead) turretHead).forceShot());
    }

    public int forceShootAllTurrets() {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        int successes = 0;
        for (TileEntity te : tileEntities) {
            if (te != null && te instanceof TurretHead) {
                successes += ((TurretHead) te).forceShot() ? 1 : 0;
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
    public OMTNetwork getNetwork() {
        return network;
    }

    @Override
    @Nonnull
    public String getDeviceName() {
        return "TurretBase";
    }

    @Override
    public void setNetwork(OMTNetwork network) {
        this.network = network;
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    @Nonnull
    public String getType() {
        // peripheral.getType returns whaaaaat?
        return "turret_base";
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    @Nonnull
    public String[] getMethodNames() {
        // list commands you want..
        return new String[]{commands.getOwner.toString(), commands.attacksPlayers.toString(),
                commands.setAttacksPlayers.toString(), commands.attacksMobs.toString(),
                commands.setAttacksMobs.toString(), commands.attacksNeutrals.toString(),
                commands.setAttacksNeutrals.toString(), commands.getTrustedPlayers.toString(),
                commands.addTrustedPlayer.toString(), commands.removeTrustedPlayer.toString(),
                commands.getActive.toString(), commands.getMode.toString(),
                commands.getRedstone.toString(), commands.setMode.toString(),
                commands.getType.toString()};
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    @ParametersAreNonnullByDefault
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        // method is command
        boolean b;
        int i;
        if (!computerAccessible) {
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
                HashMap<String, Integer> result = new HashMap<>();
                if (this.getTrustedPlayers() != null && this.getTrustedPlayers().size() > 0) {
                    for (TrustedPlayer trustedPlayer : this.getTrustedPlayers()) {
                        result.put(trustedPlayer.name,
                                (trustedPlayer.canOpenGUI ? 1 : 0) + (trustedPlayer.canChangeTargeting ? 2 : 0) + (trustedPlayer.admin ? 4 : 0));
                    }
                }
                return new Object[]{result};
            case addTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                if (!this.addTrustedPlayer(arguments[0].toString())) {
                    return new Object[]{"Name not valid!"};
                }
                if (arguments[1].toString().equals("")) {
                    return new Object[]{"successfully added"};
                }
                for (i = 1; i <= 4; i++) {
                    if (arguments.length > i && !(arguments[i].toString().equals(
                            "true") || arguments[i].toString().equals("false"))) {
                        return new Object[]{"wrong arguments"};
                    }
                }
                TrustedPlayer trustedPlayer = this.getTrustedPlayer(arguments[0].toString());
                trustedPlayer.canOpenGUI = arguments[1].toString().equals("true");
                trustedPlayer.canChangeTargeting = arguments[2].toString().equals("true");
                trustedPlayer.admin = arguments[3].toString().equals("true");
                trustedPlayer.uuid = getPlayerUUID(arguments[0].toString());

                return new Object[]{"successfully added player to trust list with parameters"};
            case removeTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                this.removeTrustedPlayer(arguments[0].toString());
                return new Object[]{"removed player from trusted list"};
            case getActive:
                return new Object[]{this.active};
            case getMode:
                return new Object[]{getMachineModeLocalization(this.mode)};
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

    @Optional.Method(modid = "ComputerCraft")
    @Override
    @ParametersAreNonnullByDefault
    public void attach(IComputerAccess computer) {
        if (comp == null) {
            comp = new ArrayList<>();
        }
        comp.add(computer);
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    @ParametersAreNonnullByDefault
    public void detach(IComputerAccess computer) {
        if (comp == null) {
            comp = new ArrayList<>();
        }
        comp.remove(computer);
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public boolean equals(IPeripheral other) {
        return other.getType().equals(getType());
    }

    public enum commands {
        getOwner, attacksPlayers, setAttacksPlayers, attacksMobs, setAttacksMobs, attacksNeutrals, setAttacksNeutrals,
        getTrustedPlayers, addTrustedPlayer, removeTrustedPlayer, getActive, getMode, getRedstone, setMode,
        getType
    }
}
