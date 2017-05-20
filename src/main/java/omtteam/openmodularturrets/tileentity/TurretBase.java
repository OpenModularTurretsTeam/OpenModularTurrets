package omtteam.openmodularturrets.tileentity;

import cofh.api.energy.EnergyStorage;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import omtteam.omlib.tileentity.ICamoSupport;
import omtteam.omlib.tileentity.TileEntityMachine;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.omlib.util.WorldUtil;
import omtteam.omlib.util.compat.ItemStackList;
import omtteam.omlib.util.compat.ItemStackTools;
import omtteam.openmodularturrets.compatability.ModCompatibility;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.items.AddonMetaItem;
import omtteam.openmodularturrets.items.UpgradeMetaItem;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.compatability.ModCompatibility.IC2Loaded;
import static omtteam.omlib.handler.ConfigHandler.EUSupport;
import static omtteam.omlib.util.BlockUtil.getBlockStateFromNBT;
import static omtteam.omlib.util.BlockUtil.writeBlockFromStateToNBT;
import static omtteam.omlib.util.MathUtil.getRotationXYFromYawPitch;
import static omtteam.omlib.util.MathUtil.getRotationXZFromYawPitch;
import static omtteam.omlib.util.PlayerUtil.getPlayerUUID;
import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;
import static omtteam.openmodularturrets.util.OMTUtil.isItemStackValidAmmo;


/*import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;*/

@SuppressWarnings("unused")
@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft"),
        @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")}
)
public class TurretBase extends TileEntityMachine implements /*IPeripheral,*/ ITickable, SimpleComponent, ICamoSupport {
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
    //private ArrayList<IComputerAccess> comp;
    protected int tier;
    private boolean forceFire = false;

    public TurretBase() {
        super();
        this.inventory = ItemStackList.create(13);
    }

    public TurretBase(int MaxEnergyStorage, int MaxIO, int tier, IBlockState camoState) {
        super();
        this.currentMaxRange = 0;
        this.upperBoundMaxRange = 0;
        this.rangeOverridden = false;
        this.storage = new EnergyStorage(MaxEnergyStorage, MaxIO);
        this.attacksMobs = true;
        this.attacksNeutrals = true;
        this.attacksPlayers = false;
        this.inventory = ItemStackList.create(tier == 5 ? 13 : tier == 4 ? 12 : tier == 3 ? 12 : tier == 2 ? 12 : 9);
        this.tier = tier;
        this.camoBlockState = camoState;
    }

    @SuppressWarnings("deprecation")
    @Override
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
        nbtTagCompound.setBoolean("attacksMobs", attacksMobs);
        nbtTagCompound.setBoolean("attacksNeutrals", attacksNeutrals);
        nbtTagCompound.setBoolean("attacksPlayers", attacksPlayers);
        nbtTagCompound.setBoolean("computerAccessible", computerAccessible);
        nbtTagCompound.setBoolean("shouldConcealTurrets", shouldConcealTurrets);
        nbtTagCompound.setBoolean("multiTargeting", multiTargeting);
        nbtTagCompound.setBoolean("forceFire", forceFire);
        nbtTagCompound.setInteger("tier", tier);
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
        this.computerAccessible = nbtTagCompound.hasKey("computerAccessible") && nbtTagCompound.getBoolean("computerAccessible");
        if (getBlockStateFromNBT(nbtTagCompound) != null) {
            this.camoBlockState = getBlockStateFromNBT(nbtTagCompound);
        } else {
            this.camoBlockState = getDefaultCamoState();
        }
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

    @Override
    public void update() {
        if (!this.getWorld().isRemote && dropBlock) {
            this.getWorld().destroyBlock(this.pos, true);
            return;
        } else if (IC2Loaded && EUSupport && !wasAddedToEnergyNet && !this.getWorld().isRemote) {
            addToIc2EnergyNetwork();
            wasAddedToEnergyNet = true;
        }
        if (!this.getWorld().isRemote && ticks % 5 == 0) {

            //Concealment
            this.shouldConcealTurrets = TurretHeadUtil.hasConcealmentAddon(this);

            //Extenders
            this.storage.setCapacity(getMaxEnergyStorageWithExtenders());

            //maxRange update
            setBaseUpperBoundRange();
            if (this.currentMaxRange > this.upperBoundMaxRange) {
                this.currentMaxRange = upperBoundMaxRange;
            }

            if (!this.rangeOverridden) {
                this.currentMaxRange = upperBoundMaxRange;
            }

            if (ticks % 20 == 0) {

                //General
                ticks = 0;
                updateRedstoneReactor(this);

                //Computers
                this.computerAccessible = (ModCompatibility.OpenComputersLoaded || ModCompatibility.ComputerCraftLoaded) && TurretHeadUtil.hasSerialPortAddon(
                        this);
            }
        }
    }

    private void setBaseUpperBoundRange() {
        int maxRange = 0;
        List<TileEntity> tileEntities = WorldUtil.getTouchingTileEntities(getWorld(), getPos());
        for (TileEntity te : tileEntities) {
            if (te != null && te instanceof TurretHead) {
                maxRange = Math.max(((TurretHead) te).getTurretRange() + TurretHeadUtil.getRangeUpgrades(this), maxRange);
            }
        }
        this.upperBoundMaxRange = maxRange;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        if (i < 9) {
            return isItemStackValidAmmo(stack);
        } else if (i >= 9 && i <= 10) {
            return stack.getItem() instanceof AddonMetaItem;
        } else if (i >= 11 && i <= 12) {
            return stack.getItem() instanceof UpgradeMetaItem;
        }
        return false;
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

    public void setAllTurretsYawPitch(float yaw, float pitch) {
        List<TileEntity> tileEntities = getTouchingTileEntities(this.getWorld(), this.pos);
        for (TileEntity te : tileEntities) {
            if (te != null && te instanceof TurretHead) {
                ((TurretHead) te).setRotationXY(getRotationXYFromYawPitch(yaw, pitch));
                ((TurretHead) te).setRotationXZ(getRotationXZFromYawPitch(yaw, pitch));
            }
        }
    }

    public boolean setTurretYawPitch(EnumFacing facing, float yaw, float pitch) {
        TileEntity turretHead = this.getWorld().getTileEntity(this.pos.offset(facing));
        if (turretHead != null && turretHead instanceof TurretHead) {
            ((TurretHead) turretHead).setRotationXY(getRotationXYFromYawPitch(yaw, pitch));
            ((TurretHead) turretHead).setRotationXZ(getRotationXZFromYawPitch(yaw, pitch));
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

    public NBTTagCompound writeMemoryCardNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setBoolean("inverted", inverted);
        nbtTagCompound.setInteger("currentMaxRange", this.currentMaxRange);
        nbtTagCompound.setBoolean("attacksMobs", attacksMobs);
        nbtTagCompound.setBoolean("attacksNeutrals", attacksNeutrals);
        nbtTagCompound.setBoolean("attacksPlayers", attacksPlayers);
        nbtTagCompound.setBoolean("multiTargeting", multiTargeting);
        nbtTagCompound.setTag("trustedPlayers", getTrustedPlayersAsNBT());
        return nbtTagCompound;
    }

    public void readMemoryCardNBT(NBTTagCompound nbtTagCompound) {
        this.currentMaxRange = nbtTagCompound.getInteger("currentMaxRange");
        this.attacksMobs = nbtTagCompound.getBoolean("attacksMobs");
        this.attacksNeutrals = nbtTagCompound.getBoolean("attacksNeutrals");
        this.attacksPlayers = nbtTagCompound.getBoolean("attacksPlayers");
        this.multiTargeting = nbtTagCompound.getBoolean("multiTargeting");
        this.setInverted(nbtTagCompound.getBoolean("inverted"));
        buildTrustedPlayersFromNBT(nbtTagCompound.getTagList("trustedPlayers", 10));
    }

    private static void updateRedstoneReactor(TurretBase base) {
        if (!TurretHeadUtil.hasRedstoneReactor(base)) {
            return;
        }

        if (ConfigHandler.getRedstoneReactorAddonGen() < (base.getMaxEnergyStored(
                EnumFacing.DOWN) - base.getEnergyStored(EnumFacing.DOWN))) {

            //Prioritise redstone blocks
            ItemStack redstoneBlock = TurretHeadUtil.getSpecificItemStackBlockFromBase(base, new ItemStack(
                    Blocks.REDSTONE_BLOCK));

            if (redstoneBlock == ItemStackTools.getEmptyStack()) {
                redstoneBlock = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorld(),
                        new ItemStack(Blocks.REDSTONE_BLOCK),
                        base);
            }

            if (redstoneBlock != ItemStackTools.getEmptyStack() && ConfigHandler.getRedstoneReactorAddonGen() * 9 < (base.getMaxEnergyStored(
                    EnumFacing.DOWN) - base.getEnergyStored(EnumFacing.DOWN))) {
                base.storage.modifyEnergyStored(ConfigHandler.getRedstoneReactorAddonGen() * 9);
                return;
            }

            ItemStack redstone = TurretHeadUtil.getSpecificItemStackItemFromBase(base, new ItemStack(Items.REDSTONE));

            if (redstone == ItemStackTools.getEmptyStack()) {
                redstone = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorld(),
                        new ItemStack(Items.REDSTONE), base);
            }

            if (redstone != ItemStackTools.getEmptyStack()) {
                base.storage.modifyEnergyStored(ConfigHandler.getRedstoneReactorAddonGen());
            }
        }
    }

    private int getMaxEnergyStorageWithExtenders() {
        int tier = getTier();
        switch (tier) {
            case 1:
                return ConfigHandler.getBaseTierOneMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 2:
                return ConfigHandler.getBaseTierTwoMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 3:
                return ConfigHandler.getBaseTierThreeMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 4:
                return ConfigHandler.getBaseTierFourMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
            case 5:
                return ConfigHandler.getBaseTierFiveMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.getWorld(), this.pos);
        }
        return 0;
    }

    public int getCurrentMaxRange() {
        return currentMaxRange;
    }

    public int getUpperBoundMaxRange() {
        return upperBoundMaxRange;
    }

    @Nonnull
    @Override
    public IBlockState getCamoState() {
        return camoBlockState;
    }

    @Override
    public void setCamoState(IBlockState state) {
        this.camoBlockState = state;
    }

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

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "turretBase";
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():string; returns owner of turret base.")
    public Object[] getOwner(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getOwner()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack hostile mobs.")
    public Object[] isAttacksMobs(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isAttacksMobs()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(state:boolean):boolean;  sets to attack hostile mobs or not.")
    public Object[] setAttacksMobs(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setAttacksMobs(args.checkBoolean(0));
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack neutral mobs.")
    public Object[] isAttacksNeutrals(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isAttacksNeutrals()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(state:boolean):boolean; sets to attack neutral mobs or not.")
    public Object[] setAttacksNeutrals(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setAttacksNeutrals(args.checkBoolean(0));
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack players.")
    public Object[] isAttacksPlayers(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isAttacksPlayers()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(state:boolean):boolean; sets to attack players or not.")
    public Object[] setAttacksPlayers(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setAttacksPlayers(args.checkBoolean(0));
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():table; returns a table of trusted players on this base.")
    public Object[] getTrustedPlayers(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getTrustedPlayers()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(name:String, [canOpenGUI:boolean , canChangeTargeting:boolean , " + "admin:boolean]):string; adds Trusted player to Trustlist.")
    public Object[] addTrustedPlayer(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!this.addTrustedPlayer(args.checkString(0))) {
            return new Object[]{"Name not valid!"};
        }
        TrustedPlayer trustedPlayer = this.getTrustedPlayer(args.checkString(0));
        trustedPlayer.canOpenGUI = args.optBoolean(1, false);
        trustedPlayer.canChangeTargeting = args.optBoolean(1, false);
        trustedPlayer.admin = args.optBoolean(1, false);
        trustedPlayer.uuid = getPlayerUUID(args.checkString(0));
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(name:String):string; removes trusted player from trust list.")
    public Object[] removeTrustedPlayer(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.removeTrustedPlayer(args.checkString(0));
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; returns maximum energy storage.")
    public Object[] getMaxEnergyStorage(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.storage.getMaxEnergyStored()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; returns current energy stored.")
    public Object[] getCurrentEnergyStorage(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getEnergyStored(EnumFacing.DOWN)};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently active.")
    public Object[] getActive(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isActive()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(state:boolean):boolean; toggles turret redstone inversion state.")
    public Object[] setInverted(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setInverted(args.checkBoolean(0));
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; shows redstone inversion state.")
    public Object[] getInverted(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getInverted()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; shows redstone state.")
    public Object[] getRedstone(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getRedstone()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(side:int, yaw:double, pitch:double):void; Set yaw and pitch for all turrets (deact. auto targ. before).")
    public Object[] setAllYawPitch(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!args.isDouble(0) || !args.isDouble(1)) return new Object[]{"Wrong parameters!"};
        setAllTurretsYawPitch((float) args.checkDouble(0), (float) args.checkDouble(1));
        return new Object[]{};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(yaw:double, pitch:double):boolean; Set yaw and pitch for a turret (deact. auto targ. before).")
    public Object[] setTurretYawPitch(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!args.isInteger(0) && args.checkInteger(0) <= 6 && args.checkInteger(0) >= 0 && !args.isDouble(1) || !args.isDouble(2))
            return new Object[]{"Wrong parameters!"};

        return new Object[]{setTurretYawPitch(EnumFacing.getFront(args.checkInteger(0)), (float) args.checkDouble(0), (float) args.checkDouble(1))};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(state:boolean):void; Enable auto firing for all Turrets (deact. auto targ. before).")
    public Object[] setAllAutoForceFire(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!args.isBoolean(0)) return new Object[]{"Wrong parameters!"};
        setAllTurretsForceFire(args.checkBoolean(0));
        return new Object[]{};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(state:boolean):boolean; Enable auto firing for specified Turret (deact. auto targ. before).")
    public Object[] setTurretAutoForceFire(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!args.isInteger(0) && args.checkInteger(0) <= 6 && args.checkInteger(0) >= 0 && !args.isBoolean(1))
            return new Object[]{"Wrong parameters!"};
        return new Object[]{setTurretForceFire(EnumFacing.getFront(args.checkInteger(0)), args.checkBoolean(1))};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; Try to shoot all turrets, returns successful shots")
    public Object[] forceShootAll(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }

        return new Object[]{this.forceShootAllTurrets()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(side:int):boolean; Try to shoot specified turret, returns true if successfully shot")
    public Object[] forceShootTurret(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!args.isInteger(0) && args.checkInteger(0) <= 6 && args.checkInteger(0) >= 0)
            return new Object[]{"Wrong parameters!"};
        return new Object[]{this.forceShootTurret(EnumFacing.getFront(args.checkInteger(0)))};
    }

    /*@Optional.Method(modid = "ComputerCraft")
    @Override
    public String getType() {
        // peripheral.getType returns whaaaaat?
        return "OMTBase";
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public String[] getMethodNames() {
        // list commands you want..
        return new String[]{commands.getOwner.toString(), commands.attacksPlayers.toString(),
                commands.setAttacksPlayers.toString(), commands.attacksMobs.toString(),
                commands.setAttacksMobs.toString(), commands.attacksNeutrals.toString(),
                commands.setAttacksNeutrals.toString(), commands.getTrustedPlayers.toString(),
                commands.addTrustedPlayer.toString(), commands.removeTrustedPlayer.toString(),
                commands.getActive.toString(), commands.getInverted.toString(),
                commands.getRedstone.toString(), commands.setInverted.toString(),
                commands.getType.toString()};
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
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
                this.getWorld().markBlockForUpdate(this.pos);
                return new Object[]{"successfully added player to trust list with parameters"};
            case removeTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                this.removeTrustedPlayer(arguments[0].toString());
                this.getWorld().markBlockForUpdate(this.pos);
                return new Object[]{"removed player from trusted list"};
            case getActive:
                return new Object[]{this.active};
            case getInverted:
                return new Object[]{this.inverted};
            case getRedstone:
                return new Object[]{this.redstone};
            case setInverted:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.setInverted(b);
                this.getWorld().markBlockForUpdate(this.pos);
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
    public void attach(IComputerAccess computer) {
        if (comp == null) {
            comp = new ArrayList<IComputerAccess>();
        }
        comp.add(computer);
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public void detach(IComputerAccess computer) {
        if (comp == null) {
            comp = new ArrayList<IComputerAccess>();
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
        getTrustedPlayers, addTrustedPlayer, removeTrustedPlayer, getActive, getInverted, getRedstone, setInverted,
        getType
    }*/
}
