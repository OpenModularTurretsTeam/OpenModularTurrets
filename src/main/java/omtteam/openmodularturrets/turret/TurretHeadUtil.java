package omtteam.openmodularturrets.turret;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import omtteam.omlib.api.util.Tuple;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.util.RandomUtil;
import omtteam.omlib.util.player.Player;
import omtteam.omlib.util.world.Pos;
import omtteam.omlib.util.world.WorldUtil;
import omtteam.openmodularturrets.api.lists.AmmoList;
import omtteam.openmodularturrets.blocks.BlockBaseAttachment;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.items.AmmoMetaItem;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.EnumSlotType;
import org.apache.commons.lang3.tuple.Triple;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static omtteam.omlib.compatibility.OMLibModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.OMLibModCompatibility.OpenComputersLoaded;
import static omtteam.omlib.util.inventory.InvUtil.getStackSize;
import static omtteam.omlib.util.player.PlayerUtil.*;
import static omtteam.openmodularturrets.blocks.BlockBaseAttachment.BASE_ADDON_META;

public class TurretHeadUtil {
    private static final HashMap<Tuple<Player, BlockPos>, Long> warnedPlayers = new HashMap<>();
    //caching disposable fake item
    private static final ItemStack disposableAmmoStack = new ItemStack(new AmmoMetaItem(), 1, 5);

    public static void warnPlayers(TurretBase base, World worldObj, BlockPos pos, int turretRange) {
        if (base.isAttacksPlayers()) {
            int warnDistance = OMTConfig.TURRETS.turretWarningDistance;
            AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - turretRange - warnDistance,
                                                   pos.getY() - turretRange - warnDistance,
                                                   pos.getZ() - turretRange - warnDistance,
                                                   pos.getX() + turretRange + warnDistance,
                                                   pos.getY() + turretRange + warnDistance,
                                                   pos.getZ() + turretRange + warnDistance);

            List<EntityPlayerMP> targets = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, axis);

            for (EntityPlayerMP target : targets) {
                Player player = new Player(target);
                Tuple<Player, BlockPos> entry = new Tuple<>(player, base.getPos());
                if (!isPlayerOwner(player, base) && !isPlayerTrusted(player, base) &&
                        !target.capabilities.isCreativeMode) {
                    if (warnedPlayers.containsKey(entry) && warnedPlayers.get(entry) < worldObj.getTotalWorldTime()) {
                        warnedPlayers.remove(entry);
                    } else if (warnedPlayers.containsKey(entry)) {
                        continue;
                    }
                    dispatchWarnMessage(target);
                    warnedPlayers.put(entry, worldObj.getTotalWorldTime() + 12000);
                }
            }
        }
    }

    private static void dispatchWarnMessage(EntityPlayerMP player) {
        if (OMTConfig.TURRETS.turretAlarmSound) {
            player.playSound(ModSounds.warningSound, 1.0F, 1.0F);
        }
        if (OMTConfig.TURRETS.turretWarnMessage) {
            addChatMessage(player, new TextComponentTranslation(OMTNames.Localizations.Text.STATUS_WARNING).setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }

    public static int getPowerExpanderTotalExtraCapacity(TurretBase base) {
        int totalExtraCap = 0;
        for (Map.Entry<EnumFacing, Expander> expanderEntry : base.getExpanderMap().entrySet()) {
            if (expanderEntry.getValue() != null && expanderEntry.getValue().isPowerExpander()) {
                totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(expanderEntry.getValue());
            }
        }
        return totalExtraCap;
    }

    //TODO: write unit tests for this, note: almost impossible in 1.12, wait for port for 1.16 for this
    public static ItemStack deductItemStackFromInventories(ItemStack itemStack, TurretBase base, @Nullable TurretHead turretHead) {
        List<Triple<IItemHandler, Integer, Integer>> foundMap = new ArrayList<>();
        int foundCount = 0;
        if (hasRecyclerAddon(base) && turretHead != null) { //For negating ammo usage
            int chance = RandomUtil.random.nextInt(99);

            if (chance < turretHead.getTurretType().getSettings().recyclerNegateChance) {
                return itemStack.copy();
            }
        }
        for (IItemHandler inventory : base.getAmmoInventories()) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack ammoCheck = inventory.getStackInSlot(i);
                // Second check if requested ammo is disposable ammo
                boolean needDisposable = itemStack.isItemEqual(disposableAmmoStack);
                if (ammoCheck != ItemStack.EMPTY && ammoCheck.isItemEqual(itemStack)
                        || (needDisposable && AmmoList.contains(ammoCheck))) {
                    foundMap.add(Triple.of(inventory, i, Math.min(ammoCheck.getCount(), itemStack.getCount())));
                    foundCount += ammoCheck.getCount();
                }
                if (foundCount >= itemStack.getCount()) {
                    break;
                }
            }
            if (foundCount >= itemStack.getCount()) {
                break;
            }
        }
        foundCount = Math.min(foundCount, itemStack.getCount());

        // remove items from inventories
        if (foundCount == itemStack.getCount()) {
            for (Triple<IItemHandler, Integer, Integer> entry : foundMap) {
                int todo = foundCount;
                entry.getLeft().extractItem(entry.getMiddle(), entry.getRight(), false);
                todo -= entry.getRight();
                if (todo == 0) break;
            }
        }

        return foundCount < itemStack.getCount() ? ItemStack.EMPTY : itemStack;
    }

    public static int getAmmoLevel(TurretHead turret, TurretBase base) {
        int result = 0;

        ItemStack ammoStackRequired = turret.getAmmo();
        if (!OMTConfig.TURRETS.doTurretsNeedAmmo && ammoStackRequired != null) {
            return Integer.MAX_VALUE;
        }
        if (ammoStackRequired == null) {
            return base.getEnergyStored(EnumFacing.DOWN) / turret.getTurretBasePowerUsage();
        }
        for (int i = 0; i <= 8; i++) {
            ItemStack ammoStack = base.getInventory().getStackInSlot(i);

            if (ammoStack != ItemStack.EMPTY && getStackSize(ammoStack) > 0 && ammoStack.getItem() == ammoStackRequired.getItem()
                    && ammoStack.getMetadata() == ammoStackRequired.getMetadata()) {
                result += ammoStack.getCount();
            }
        }

        for (TileEntity tileEntity : WorldUtil.getTouchingTileEntities(base.getWorld(), base.getPos())) {
            if (tileEntity instanceof Expander && !((Expander) tileEntity).isPowerExpander()) {
                Expander exp = (Expander) tileEntity;
                for (int i = 0; i < exp.getInventory().getSlots(); i++) {
                    ItemStack ammoStack = exp.getInventory().getStackInSlot(i);
                    if (ammoStack != ItemStack.EMPTY && ammoStack.getItem() == ammoStackRequired.getItem()) {
                        result += ammoStack.getCount();
                    }
                }
            }
        }
        return result;
    }

    private static int getPowerExtenderCapacityValue(Expander expander) {
        if (expander != null) {
            if (!expander.isPowerExpander()) return 0;
            int tier = (expander.getTier() > 4 ? expander.getTier() - 4 : 0);

            switch (tier) {
                case 1:
                    return OMTConfig.MISCELLANEOUS.expanderPowerTierOneCapacity;
                case 2:
                    return OMTConfig.MISCELLANEOUS.expanderPowerTierTwoCapacity;
                case 3:
                    return OMTConfig.MISCELLANEOUS.expanderPowerTierThreeCapacity;
                case 4:
                    return OMTConfig.MISCELLANEOUS.expanderPowerTierFourCapacity;
                case 5:
                    return OMTConfig.MISCELLANEOUS.expanderPowerTierFiveCapacity;
                default:
                    return 0;
            }
        }
        return 0;
    }

    public static TurretBase getTurretBase(World world, BlockPos pos) {
        if (world == null) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(offsetPos);
            }
        }

        return null;
    }

    public static EnumFacing getTurretBaseFacing(World world, BlockPos pos) {
        if (world == null) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretBase) {
                return facing;
            }
        }

        return null;
    }

    public static Map<EnumFacing, TurretHead> getBaseTurrets(World world, BlockPos pos) {
        Map<EnumFacing, TurretHead> map = new HashMap<>();
        if (world == null) {
            return map;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);

            if (world.getTileEntity(offsetPos) instanceof TurretHead) {
                map.put(facing, (TurretHead) world.getTileEntity(offsetPos));
            }
        }

        return map;
    }

    public static float getAimYaw(Entity target, Pos pos) {
        Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);

        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE
                    .getPhysicsEntityFromShipSpace(target.getEntityWorld(), pos.getBlockPos());
            if (physicsEntity != null) {
                targetPos = physicsEntity.transformVector(targetPos, TransformType.GLOBAL_TO_SUBSPACE);
            }
        }

        double dX = (targetPos.x) - (pos.getX());
        double dZ = (targetPos.z) - (pos.getZ());

        float yaw = (float) ((Math.atan2(dZ, dX)));
        if (yaw < 0) {
            yaw += 2 * Math.PI;
        }

        return yaw / (float) Math.PI * 180F;
    }

    public static float getAimPitch(Entity target, Pos pos) {
        Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);

        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE
                    .getPhysicsEntityFromShipSpace(target.getEntityWorld(), pos.getBlockPos());
            if (physicsEntity != null) {
                targetPos = physicsEntity.transformVector(targetPos, TransformType.GLOBAL_TO_SUBSPACE);
            }
        }

        BlockPos targetBlockPos = new BlockPos(targetPos.x, targetPos.y, targetPos.z);

        double dX = (targetBlockPos.getX() - 0.5F) - (pos.getX() + 0.5F);
        double dY = (targetBlockPos.getY() + 0.5F) - (pos.getY() - 0.5F);
        double dZ = (targetBlockPos.getZ() - 0.5F) - (pos.getZ() + 0.5F);

        float pitch = (float) ((Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY)));
        if (pitch < 0) {
            pitch += 2 * Math.PI;
        }

        return pitch / (float) Math.PI * 180F;
    }

    public static int getRangeUpgrades(TurretBase base, TurretHead turretHead) {
        int value = 0;
        for (int i : base.getSlotMap().get(EnumSlotType.UpgradeSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 3) {
                    value += (turretHead.getTurretType().getSettings().rangeUpgrade * getStackSize(base.getInventory().getStackInSlot(i)));
                }
            }
        }

        return value;
    }

    public static int getScattershotUpgrades(TurretBase base) {
        int value = 0;
        for (int i : base.getSlotMap().get(EnumSlotType.UpgradeSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 4) {
                    value += getStackSize(base.getInventory().getStackInSlot(i));
                }
            }
        }

        return value;
    }

    public static float getAccuracyUpgrades(TurretBase base, TurretHead turretHead) {
        float accuracy = 0.0F;
        for (int i : base.getSlotMap().get(EnumSlotType.UpgradeSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 0) {
                    accuracy += (turretHead.getTurretType().getSettings().accuracyUpgrade * getStackSize(base.getInventory().getStackInSlot(i)));
                }
            }
        }

        return accuracy;
    }

    public static float getEfficiencyUpgrades(TurretBase base, TurretHead turretHead) {
        float efficiency = 0.0F;
        for (int i : base.getSlotMap().get(EnumSlotType.UpgradeSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 1) {
                    efficiency += (turretHead.getTurretType().getSettings().efficiencyUpgrade * getStackSize(base.getInventory().getStackInSlot(i)));
                }
            }
        }

        return efficiency;
    }

    public static float getFireRateUpgrades(TurretBase base, TurretHead turretHead) {
        float rof = 0.0F;
        for (int i : base.getSlotMap().get(EnumSlotType.UpgradeSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 2) {
                    rof += (turretHead.getTurretType().getSettings().fireRateUpgrade * getStackSize(base.getInventory().getStackInSlot(i)));
                }
            }
        }

        return rof;
    }

    public static boolean hasRedstoneReactor(TurretBase base) {
        for (int i : base.getSlotMap().get(EnumSlotType.AddonSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasConcealmentAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }

        if (base.getInventory().getStackInSlot(9) != ItemStack.EMPTY) {
            found = base.getInventory().getStackInSlot(9).getItemDamage() == 0;
        }

        if (base.getInventory().getStackInSlot(10) != ItemStack.EMPTY && !found) {
            found = base.getInventory().getStackInSlot(10).getItemDamage() == 0;
        }
        return found || OMTConfig.TURRETS.canTurretsConcealWithoutAddon;
    }

    public static boolean hasSolarPanelAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }

        if (base.getInventory().getStackInSlot(9) != ItemStack.EMPTY) {
            found = base.getInventory().getStackInSlot(9).getItemDamage() == 6;
        }

        if (base.getInventory().getStackInSlot(10) != ItemStack.EMPTY && !found) {
            found = base.getInventory().getStackInSlot(10).getItemDamage() == 6;
        }
        return found;
    }

    public static boolean hasSerialPortAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }
        if (!OpenComputersLoaded && !ComputerCraftLoaded) {
            return false;
        }

        if (base.getInventory().getStackInSlot(9) != ItemStack.EMPTY) {
            found = base.getInventory().getStackInSlot(9).getItemDamage() == 5;
        }

        if (base.getInventory().getStackInSlot(10) != ItemStack.EMPTY && !found) {
            found = base.getInventory().getStackInSlot(10).getItemDamage() == 5;
        }
        return found;
    }

    private static boolean hasRecyclerAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }
        if (base.getInventory().getStackInSlot(9) != ItemStack.EMPTY) {
            found = base.getInventory().getStackInSlot(9).getItemDamage() == 3;
        }

        if (base.getInventory().getStackInSlot(10) != ItemStack.EMPTY && !found) {
            found = base.getInventory().getStackInSlot(10).getItemDamage() == 3;
        }
        return found;
    }

    public static int getAmpLevel(TurretBase base) {
        int amp_level = 0;

        if (base == null) {
            return amp_level;
        }

        for (int i : base.getSlotMap().get(EnumSlotType.AddonSlot)) {
            if (base.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                if (base.getInventory().getStackInSlot(i).getItemDamage() == 1) {
                    amp_level += getStackSize(base.getInventory().getStackInSlot(i));
                }
            }
        }

        return amp_level;
    }

    public static int getFakeDropsLevel(TurretBase base) {
        int fakeDropsLevel = -1;

        if (base == null) {
            return fakeDropsLevel;
        }

        int tier = base.getTier();

        if (tier == 1) {
            return fakeDropsLevel;
        }

        if (base.getInventory().getStackInSlot(9) != ItemStack.EMPTY) {
            if (base.getInventory().getStackInSlot(9).getItemDamage() == 7) {
                fakeDropsLevel += getStackSize(base.getInventory().getStackInSlot(9));
            }
        }

        if (base.getInventory().getStackInSlot(10) != ItemStack.EMPTY) {
            if (base.getInventory().getStackInSlot(10).getItemDamage() == 7) {
                fakeDropsLevel += getStackSize(base.getInventory().getStackInSlot(10));
            }
        }

        return Math.min(fakeDropsLevel, 3);
    }

    public static boolean baseHasNoLootDeleter(TurretBase base) {
        List<IBlockState> states = WorldUtil.getTouchingBlockStates(base.getWorld(), base.getPos());
        for (IBlockState state : states) {
            if (state.getBlock() instanceof BlockBaseAttachment) {
                if (state.getValue(BASE_ADDON_META) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void updateSolarPanelAddon(TurretBase base) {
        OMEnergyStorage storage = (OMEnergyStorage) base.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if (!hasSolarPanelAddon(base) || storage == null) {
            return;
        }

        if (base.getWorld().isDaytime() && !base.getWorld().isRaining() && base.getWorld().canBlockSeeSky(base.getPos().up(2))) {
            storage.receiveEnergy(OMTConfig.MISCELLANEOUS.solarPanelAddonGen, false);
        }
    }
}
