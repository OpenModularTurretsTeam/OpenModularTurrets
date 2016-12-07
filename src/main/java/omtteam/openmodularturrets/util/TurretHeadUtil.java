package omtteam.openmodularturrets.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.omlib.util.WorldUtil;
import omtteam.openmodularturrets.compatability.ModCompatibility;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TurretHeadUtil {
    private static final HashSet<EntityPlayerMP> warnedPlayers = new HashSet<>();

    public static void warnPlayers(TurretBase base, World worldObj, int downLowAmount, BlockPos pos, int turretRange) {
        if (base.isAttacksPlayers()) {
            int warnDistance = ConfigHandler.getTurretWarningDistance();
            AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - turretRange - warnDistance,
                    pos.getY() - downLowAmount - warnDistance,
                    pos.getZ() - turretRange - warnDistance,
                    pos.getX() + turretRange + warnDistance,
                    pos.getY() + turretRange + warnDistance,
                    pos.getZ() + turretRange + warnDistance);

            if (worldObj.getWorldTime() % 2000 == 0) {
                warnedPlayers.clear();
            }

            List<EntityPlayerMP> targets = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, axis);

            for (EntityPlayerMP target : targets) {
                if (!target.getUniqueID().toString().equals(base.getOwner()) && !isTrustedPlayer(target.getUniqueID(),
                        base) && !warnedPlayers.contains(
                        target) && !target.capabilities.isCreativeMode) {
                    dispatchWarnMessage(target, worldObj);
                    warnedPlayers.add(target);
                }
            }
        }
    }

    @SuppressWarnings({"deprecation", "unused"})
    private static void dispatchWarnMessage(EntityPlayerMP player, World worldObj) {
        if (ConfigHandler.turretAlarmSound) {
            player.playSound(ModSounds.turretWarnSound, 1.0F, 1.0F);
        }
        if (ConfigHandler.turretWarnMessage) {
            player.addChatMessage(new TextComponentString(
                    TextFormatting.DARK_RED + I18n.translateToLocal("status.warning")));
        }
    }

    public static Entity getTarget(TurretBase base, World worldObj, int downLowAmount, BlockPos pos, int turretRange, TurretHead turret) {
        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - turretRange, pos.getY() - downLowAmount,
                    pos.getZ() - turretRange, pos.getX() + turretRange,
                    pos.getY() + turretRange, pos.getZ() + turretRange);

            List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase target1 : targets) {
                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAnimal && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAmbientCreature && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs() && ConfigHandler.globalCanTargetMobs) {
                    if (target1.isCreatureType(EnumCreatureType.MONSTER, false) && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead) {
                        EntityPlayerMP entity = (EntityPlayerMP) target1;

                        if (!entity.getUniqueID().toString().equals(base.getOwner()) && !isTrustedPlayer(
                                entity.getUniqueID(), base) && !entity.capabilities.isCreativeMode) {
                            target = target1;
                        }
                    }
                }

                if (target != null && turret != null) {
                    if (base.isMultiTargeting() && isTargetAlreadyTargeted(base, target)) {
                        continue;
                    }

                    EntityLivingBase targetELB = (EntityLivingBase) target;
                    if (canTurretSeeTarget(turret, targetELB) && targetELB.getHealth() > 0.0F) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    public static Entity getTargetWithMinimumRange(TurretBase base, World worldObj, int downLowAmount, BlockPos pos, int turretRange, TurretHead turret) {
        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - turretRange, pos.getY() - downLowAmount,
                    pos.getZ() - turretRange, pos.getX() + turretRange,
                    pos.getY() + turretRange, pos.getZ() + turretRange);

            List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase target1 : targets) {
                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAnimal && !target1.isDead && target1.getDistance(pos.getX(), pos.getY(),
                            pos.getZ()) >= 3) {
                        target = target1;
                    }
                }

                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAmbientCreature && !target1.isDead && target1.getDistance(pos.getX(),
                            pos.getY(),
                            pos.getZ()) >= 3) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs() && ConfigHandler.globalCanTargetMobs) {
                    if (target1 instanceof IMob && !target1.isDead && target1.getDistance(pos.getX(), pos.getY(),
                            pos.getZ()) >= 3) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead && target1.getDistance(pos.getX(), pos.getY(),
                            pos.getZ()) >= 3) {
                        EntityPlayerMP entity = (EntityPlayerMP) target1;

                        if (!entity.getUniqueID().toString().equals(base.getOwner()) && !isTrustedPlayer(
                                entity.getUniqueID(), base) && !entity.capabilities.isCreativeMode) {
                            target = target1;
                        }
                    }
                }

                if (target != null && turret != null) {
                    if (base.isMultiTargeting() && isTargetAlreadyTargeted(base, target)) {
                        continue;
                    }

                    EntityLivingBase targetELB = (EntityLivingBase) target;

                    if (canTurretSeeTarget(turret, targetELB) && targetELB.getHealth() > 0.0F) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    public static Entity getTargetWithoutSlowEffect(TurretBase base, World worldObj, int downLowAmount, BlockPos pos, int turretRange, TurretHead turret) {
        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - turretRange, pos.getY() - downLowAmount,
                    pos.getZ() - turretRange, pos.getX() + turretRange,
                    pos.getY() + turretRange, pos.getZ() + turretRange);

            List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase target1 : targets) {
                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAnimal && !target1.isDead && !target1.isPotionActive(
                            Potion.getPotionById(2))) {
                        target = target1;
                    }
                }

                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAmbientCreature && !target1.isDead && !target1.isPotionActive(
                            Potion.getPotionById(2))) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs() && ConfigHandler.globalCanTargetMobs) {
                    if (target1 instanceof IMob && !target1.isDead && !target1.isPotionActive(Potion.getPotionById(2))) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead && !target1.isPotionActive(
                            Potion.getPotionById(2))) {
                        EntityPlayerMP entity = (EntityPlayerMP) target1;

                        if (!entity.getUniqueID().toString().equals(base.getOwner()) && !isTrustedPlayer(
                                entity.getUniqueID(), base) && !entity.capabilities.isCreativeMode) {
                            target = target1;
                        }
                    }
                }

                if (target != null && turret != null) {
                    EntityLivingBase targetELB = (EntityLivingBase) target;

                    if (base.isMultiTargeting() && isTargetAlreadyTargeted(base, target)) {
                        continue;
                    }

                    if (canTurretSeeTarget(turret, targetELB) && targetELB.getHealth() > 0.0F) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isTargetAlreadyTargeted(TurretBase base, Entity entity) {
        for (TileEntity tileEntity : WorldUtil.getTouchingTileEntities(base.getWorld(), base.getPos())) {
            if (tileEntity instanceof TurretHead) {
                if (((TurretHead) tileEntity).target != null && entity.equals(((TurretHead) tileEntity).target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTrustedPlayer(UUID uuid, TurretBase base) {
        for (TrustedPlayer trusted_player : base.getTrustedPlayers()) {
            if (trusted_player.uuid.equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public static int getPowerExpanderTotalExtraCapacity(World world, BlockPos pos) {
        int totalExtraCap = 0;
        for (TileEntity tileEntity : WorldUtil.getTouchingTileEntities(world, pos)) {
            if (tileEntity instanceof Expander && ((Expander) tileEntity).isPowerExpander()) {
                totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                        (Expander) tileEntity);
            }
        }
        return totalExtraCap;
    }

    private static ItemStack deductFromInvExpander(ItemStack itemStack, Expander exp, TurretBase base) {

        for (int i = 0; i < exp.getSizeInventory(); i++) {
            ItemStack ammoCheck = exp.getStackInSlot(i);
            if (ammoCheck != null && ammoCheck.getItem() == itemStack.getItem()) {
                if (hasRecyclerAddon(base)) {
                    int chance = new Random().nextInt(99);

                    //For negating
                    if (chance >= 0 && chance < ConfigHandler.getRecyclerNegateChance()) {
                        return new ItemStack(ammoCheck.getItem());
                        //For adding
                    } else if (chance > ConfigHandler.getRecyclerNegateChance() && chance < (ConfigHandler.getRecyclerNegateChance() + ConfigHandler.getRecyclerAddChance())) {
                        exp.decrStackSize(i, -1);
                        return new ItemStack(ammoCheck.getItem());
                    } else {
                        exp.decrStackSize(i, 1);
                        return new ItemStack(ammoCheck.getItem());
                    }
                } else {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(ammoCheck.getItem());
                }
            }
        }
        return null;
    }

    public static ItemStack getSpecificItemFromInvExpanders(World world, ItemStack itemStack, TurretBase base) {
        for (TileEntity tileEntity : WorldUtil.getTouchingTileEntities(world, base.getPos())) {
            if (tileEntity instanceof Expander && !((Expander) tileEntity).isPowerExpander()) {
                Expander exp = (Expander) tileEntity;
                ItemStack stack = deductFromInvExpander(itemStack, exp, base);
                if (stack != null) {
                    return stack;
                }
            }
        }
        return null;
    }

    public static ItemStack getAnyItemFromInvExpanders(World world, TurretBase base) {
        for (TileEntity tileEntity : WorldUtil.getTouchingTileEntities(world, base.getPos())) {
            if (tileEntity instanceof Expander && !((Expander) tileEntity).isPowerExpander()) {
                Expander exp = (Expander) tileEntity;
                for (int i = 0; i < exp.getSizeInventory(); i++) {
                    ItemStack itemCheck = exp.getStackInSlot(i);
                    if (itemCheck != null) {
                        exp.decrStackSize(i, 1);
                        return new ItemStack(itemCheck.getItem(), 1, itemCheck.getItemDamage());
                    }
                }
            }
        }
        return null;
    }

    private static int getPowerExtenderCapacityValue(Expander expander) {
        if (expander != null) {
            if (!expander.isPowerExpander()) return 0;
            int tier = (expander.getTier() > 4?expander.getTier()-4: 0);

            switch (tier) {
                case 1:
                    return ConfigHandler.getExpanderPowerTierOneCapacity();
                case 2:
                    return ConfigHandler.getExpanderPowerTierTwoCapacity();
                case 3:
                    return ConfigHandler.getExpanderPowerTierThreeCapacity();
                case 4:
                    return ConfigHandler.getExpanderPowerTierFourCapacity();
                case 5:
                    return ConfigHandler.getExpanderPowerTierFiveCapacity();
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

    public static float getAimYaw(Entity target, BlockPos pos) {
        double dX = (target.posX) - (pos.getX());
        double dZ = (target.posZ) - (pos.getZ());
        float yaw = (float) Math.atan2(dZ, dX);
        yaw = yaw - 1.570796F + 3.1F;
        return yaw;
    }

    public static float getAimPitch(Entity target, BlockPos pos) {
        double dX = (target.posX - 0.2F) - (pos.getX() + 0.6F);
        double dY = (target.posY + 0.6F) - (pos.getY() - 0.6F);
        double dZ = (target.posZ - 0.2F) - (pos.getZ() + 0.6F);
        float pitch = (float) (Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI);
        pitch = pitch + 1.65F;
        return pitch;
    }

    public static ItemStack useAnyItemStackFromBase(TurretBase base) {
        for (int i = 0; i <= 8; i++) {
            ItemStack itemCheck = base.getStackInSlot(i);
            if (itemCheck != null && itemCheck.stackSize > 0) {
                base.decrStackSize(i, 1);
                return new ItemStack(itemCheck.getItem(), 1, itemCheck.getItemDamage());
            }
        }
        return null;
    }

    public static ItemStack useSpecificItemStackBlockFromBase(TurretBase base, ItemStack stack) {
        for (int i = 0; i <= 8; i++) {
            ItemStack ammo_stack = base.getStackInSlot(i);

            if (ammo_stack != null && ammo_stack.stackSize > 0 && ammo_stack.getItem() == stack.getItem()) {
                base.decrStackSize(i, 1);
                return new ItemStack(ammo_stack.getItem());
            }
        }

        return null;
    }

    public static ItemStack useSpecificItemStackItemFromBase(TurretBase base, ItemStack ammoStackRequired) {
        for (int i = 0; i <= 8; i++) {
            ItemStack ammo_stack = base.getStackInSlot(i);

            if (ammo_stack != null && ammo_stack.stackSize > 0 && ammo_stack.getItem() == ammoStackRequired.getItem()
                    && ammo_stack.getMetadata() == ammoStackRequired.getMetadata()) {
                if (hasRecyclerAddon(base)) {
                    int chance = new Random().nextInt(99);

                    //For negating
                    if (chance > 0 && chance < ConfigHandler.getRecyclerNegateChance()) {
                        return new ItemStack(ammo_stack.getItem());
                        //For adding
                    } else if (chance > ConfigHandler.getRecyclerNegateChance() && chance < (ConfigHandler.getRecyclerNegateChance() + ConfigHandler.getRecyclerAddChance())) {
                        base.decrStackSize(i, -1);
                        return new ItemStack(ammo_stack.getItem());
                    } else {
                        base.decrStackSize(i, 1);
                        return new ItemStack(ammo_stack.getItem());
                    }
                } else {
                    base.decrStackSize(i, 1);
                    return new ItemStack(ammo_stack.getItem());
                }
            }
        }

        return null;
    }

    public static int getRangeUpgrades(TurretBase base) {
        int value = 0;
        int tier = base.getTier();

        if (tier == 1) {
            return value;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItemDamage() == 3) {
                    value += (ConfigHandler.getRangeUpgradeBoost() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItemDamage() == 3) {
                value += (ConfigHandler.getRangeUpgradeBoost() * base.getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static int getScattershotUpgrades(TurretBase base) {
        int value = 0;
        int tier = base.getTier();

        if (tier == 1) {
            return value;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItemDamage() == 4) {
                    value += base.getStackInSlot(12).stackSize;
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItemDamage() == 4) {
                value += base.getStackInSlot(11).stackSize;
            }
        }

        return value;
    }

    public static float getAccuraccyUpgrades(TurretBase base) {
        float accuracy = 0.0F;
        int tier = base.getTier();

        if (tier == 1) {
            return accuracy;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItemDamage() == 0) {
                    accuracy += (ConfigHandler.getAccuracyUpgradeBoost() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItemDamage() == 0) {
                accuracy += (ConfigHandler.getAccuracyUpgradeBoost() * base.getStackInSlot(11).stackSize);
            }
        }

        return accuracy;
    }

    public static float getEfficiencyUpgrades(TurretBase base) {
        float efficiency = 0.0F;
        int tier = base.getTier();

        if (tier == 1) {
            return efficiency;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItemDamage() == 1) {
                    efficiency += (ConfigHandler.getEfficiencyUpgradeBoostPercentage() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItemDamage() == 1) {
                efficiency += (ConfigHandler.getEfficiencyUpgradeBoostPercentage() * base.getStackInSlot(11).stackSize);
            }
        }

        return efficiency;
    }

    public static float getFireRateUpgrades(TurretBase base) {
        float rof = 0.0F;
        int tier = base.getTier();

        if (tier == 1) {
            return rof;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItemDamage() == 2) {
                    rof += (ConfigHandler.getFireRateUpgradeBoostPercentage() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItemDamage() == 2) {
                rof += (ConfigHandler.getFireRateUpgradeBoostPercentage() * base.getStackInSlot(11).stackSize);
            }
        }

        return rof;
    }

    public static boolean hasRedstoneReactor(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 4;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 4;
        }
        return found;
    }

    public static boolean hasDamageAmpAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 1;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 1;
        }
        return found;
    }

    public static boolean hasConcealmentAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 0;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 0;
        }
        return found;
    }

    public static boolean hasSolarPanelAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 6;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 6;
        }
        return found;
    }

    @SuppressWarnings("unused")
    public static boolean hasPotentiaUpgradeAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }
        if (!ModCompatibility.ThaumcraftLoaded) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 2;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 2;
        }
        return found;
    }

    public static boolean hasSerialPortAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }
        if (!ModCompatibility.OpenComputersLoaded && !ModCompatibility.ComputerCraftLoaded) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 5;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 5;
        }
        return found;
    }

    private static boolean hasRecyclerAddon(TurretBase base) {
        boolean found = false;
        if (base.getTier() == 1) {
            return false;
        }
        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItemDamage() == 3;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItemDamage() == 3;
        }
        return found;
    }

    public static int getAmpLevel(TurretBase base) {
        int amp_level = 0;

        if (base == null) {
            return amp_level;
        }

        int tier = base.getTier();

        if (tier == 1) {
            return amp_level;
        }

        if (base.getStackInSlot(9) != null) {
            if (base.getStackInSlot(9).getItemDamage() == 1) {
                amp_level += base.getStackInSlot(9).stackSize;
            }
        }

        if (base.getStackInSlot(10) != null) {
            if (base.getStackInSlot(10).getItemDamage() == 1) {
                amp_level += base.getStackInSlot(10).stackSize;
            }
        }

        return amp_level;
    }

    public static void updateSolarPanelAddon(TurretBase base) {
        if (!hasSolarPanelAddon(base)) {
            return;
        }

        if (base.getWorld().isDaytime() && !base.getWorld().isRaining() && base.getWorld().canBlockSeeSky(base.getPos().up(2))) {
            base.receiveEnergy(EnumFacing.DOWN, ConfigHandler.getSolarPanelAddonGen(), false);
        }
    }

    public static boolean canTurretSeeTarget(TurretHead turret, EntityLivingBase target) {
        Vec3d traceStart = new Vec3d(turret.getPos().getX() + 0.5F, turret.getPos().getY() + 0.5F, turret.getPos().getZ() + 0.5F);
        Vec3d traceEnd = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
        Vec3d vecDelta = new Vec3d(traceEnd.xCoord - traceStart.xCoord,
                traceEnd.yCoord - traceStart.yCoord,
                traceEnd.zCoord - traceStart.zCoord);

        // Normalize vector to the largest delta axis
        vecDelta = vecDelta.normalize();

        // Limit how many non solid block a turret can see through
        for (int i = 0; i < 10; i++) {
            // Offset start position toward the target to prevent self collision
            traceStart = traceStart.add(vecDelta);

            RayTraceResult traced = turret.getWorld().rayTraceBlocks(traceStart, traceEnd);

            if (traced != null && traced.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState hitBlock = turret.getWorld().getBlockState(traced.getBlockPos());

                // If non solid block is in the way then proceed to continue
                // tracing
                if ((traced.getBlockPos().equals(turret.getPos())) || (!hitBlock.getMaterial().isSolid() && MathHelper.abs_max(
                        MathHelper.abs_max(traceStart.xCoord - traceEnd.xCoord, traceStart.yCoord - traceEnd.yCoord),
                        traceStart.zCoord - traceEnd.zCoord) > 1)) {
                    // Start at new position and continue
                    traceStart = traced.hitVec;
                    continue;
                }
            }

            EntityLivingBase targeted = traced == null ? target : null;

            return targeted != null && targeted.equals(target);
        }

        // If all above failed, the target cannot be seen
        return false;
    }
}
