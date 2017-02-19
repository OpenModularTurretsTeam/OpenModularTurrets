package openmodularturrets.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.addons.*;
import openmodularturrets.items.upgrades.*;
import openmodularturrets.tileentity.expander.AbstractInvExpander;
import openmodularturrets.tileentity.expander.AbstractPowerExpander;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;
import openmodularturrets.tileentity.turrets.TurretHead;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static openmodularturrets.util.PlayerUtil.isPlayerOwner;

public class TurretHeadUtil {
    private static final HashSet<EntityPlayerMP> warnedPlayers = new HashSet<EntityPlayerMP>();

    public static void warnPlayers(TurretBase base, World worldObj, int downLowAmount, int xCoord, int yCoord, int zCoord, int turretRange) {
        if (base.isAttacksPlayers()) {
            int warnDistance = ConfigHandler.getTurretWarningDistance();
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(xCoord - turretRange - warnDistance,
                                                              yCoord - downLowAmount - warnDistance,
                                                              zCoord - turretRange - warnDistance,
                                                              xCoord + turretRange + warnDistance,
                                                              yCoord + turretRange + warnDistance,
                                                              zCoord + turretRange + warnDistance);

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

    private static void dispatchWarnMessage(EntityPlayerMP player, World worldObj) {
        if (ConfigHandler.turretAlarmSound) {
            worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "openmodularturrets:warning", 1.0F, 1.0F);
        }
        if (ConfigHandler.turretWarnMessage) {
            player.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("status.warning")));
        }
    }

    public static Entity getTarget(TurretBase base, World worldObj, int downLowAmount, int xCoord, int yCoord, int zCoord, int turretRange, TurretHead turret) {
        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(xCoord - turretRange, yCoord - downLowAmount,
                                                              zCoord - turretRange, xCoord + turretRange,
                                                              yCoord + turretRange, zCoord + turretRange);

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
                    if (target1.isCreatureType(EnumCreatureType.monster, false) && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead) {
                        EntityPlayerMP entity = (EntityPlayerMP) target1;

                        if (!isPlayerOwner(entity, base) && !isTrustedPlayer(
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

    public static Entity getTargetWithMinimumRange(TurretBase base, World worldObj, int downLowAmount, int xCoord, int yCoord, int zCoord, int turretRange, TurretHead turret) {
        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(xCoord - turretRange, yCoord - downLowAmount,
                                                              zCoord - turretRange, xCoord + turretRange,
                                                              yCoord + turretRange, zCoord + turretRange);

            List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase target1 : targets) {
                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAnimal && !target1.isDead && target1.getDistance(xCoord, yCoord,
                                                                                                  zCoord) >= 3) {
                        target = target1;
                    }
                }

                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAmbientCreature && !target1.isDead && target1.getDistance(xCoord,
                                                                                                           yCoord,
                                                                                                           zCoord) >= 3) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs() && ConfigHandler.globalCanTargetMobs) {
                    if (target1 instanceof IMob && !target1.isDead && target1.getDistance(xCoord, yCoord,
                                                                                          zCoord) >= 3) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead && target1.getDistance(xCoord, yCoord,
                                                                                                    zCoord) >= 3) {
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

    public static Entity getTargetWithoutSlowEffect(TurretBase base, World worldObj, int downLowAmount, int xCoord, int yCoord, int zCoord, int turretRange, TurretHead turret) {
        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(xCoord - turretRange, yCoord - downLowAmount,
                                                              zCoord - turretRange, xCoord + turretRange,
                                                              yCoord + turretRange, zCoord + turretRange);

            List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase target1 : targets) {
                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAnimal && !target1.isDead && !target1.isPotionActive(
                            Potion.moveSlowdown.id)) {
                        target = target1;
                    }
                }

                if (base.isAttacksNeutrals() && ConfigHandler.globalCanTargetNeutrals) {
                    if (target1 instanceof EntityAmbientCreature && !target1.isDead && !target1.isPotionActive(
                            Potion.moveSlowdown.id)) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs() && ConfigHandler.globalCanTargetMobs) {
                    if (target1 instanceof IMob && !target1.isDead && !target1.isPotionActive(Potion.moveSlowdown.id)) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead && !target1.isPotionActive(
                            Potion.moveSlowdown.id)) {
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
        if (base.getWorldObj().getTileEntity(base.xCoord + 1, base.yCoord, base.zCoord) instanceof TurretHead) {
            TurretHead turret = (TurretHead) base.getWorldObj().getTileEntity(base.xCoord + 1, base.yCoord,
                                                                              base.zCoord);
            if (turret.target != null && entity.equals(turret.target)) {
                return true;
            }
        }

        if (base.getWorldObj().getTileEntity(base.xCoord - 1, base.yCoord, base.zCoord) instanceof TurretHead) {
            TurretHead turret = (TurretHead) base.getWorldObj().getTileEntity(base.xCoord - 1, base.yCoord,
                                                                              base.zCoord);
            if (turret.target != null && entity.equals(turret.target)) {
                return true;
            }
        }

        if (base.getWorldObj().getTileEntity(base.xCoord, base.yCoord + 1, base.zCoord) instanceof TurretHead) {
            TurretHead turret = (TurretHead) base.getWorldObj().getTileEntity(base.xCoord, base.yCoord + 1,
                                                                              base.zCoord);
            if (turret.target != null && entity.equals(turret.target)) {
                return true;
            }
        }

        if (base.getWorldObj().getTileEntity(base.xCoord, base.yCoord - 1, base.zCoord) instanceof TurretHead) {
            TurretHead turret = (TurretHead) base.getWorldObj().getTileEntity(base.xCoord, base.yCoord - 1,
                                                                              base.zCoord);
            if (turret.target != null && entity.equals(turret.target)) {
                return true;
            }
        }

        if (base.getWorldObj().getTileEntity(base.xCoord, base.yCoord, base.zCoord + 1) instanceof TurretHead) {
            TurretHead turret = (TurretHead) base.getWorldObj().getTileEntity(base.xCoord, base.yCoord,
                                                                              base.zCoord + 1);
            if (turret.target != null && entity.equals(turret.target)) {
                return true;
            }
        }

        if (base.getWorldObj().getTileEntity(base.xCoord, base.yCoord, base.zCoord - 1) instanceof TurretHead) {
            TurretHead turret = (TurretHead) base.getWorldObj().getTileEntity(base.xCoord, base.yCoord,
                                                                              base.zCoord - 1);
            if (turret.target != null && entity.equals(turret.target)) {
                return true;
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

    public static int getPowerExpanderTotalExtraCapacity(World world, int x, int y, int z) {
        int totalExtraCap = 0;
        if (world.getTileEntity(x + 1, y, z) instanceof AbstractPowerExpander) {
            totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                    (AbstractPowerExpander) world.getTileEntity(x + 1, y, z));
        }
        if (world.getTileEntity(x - 1, y, z) instanceof AbstractPowerExpander) {
            totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                    (AbstractPowerExpander) world.getTileEntity(x - 1, y, z));
        }
        if (world.getTileEntity(x, y + 1, z) instanceof AbstractPowerExpander) {
            totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                    (AbstractPowerExpander) world.getTileEntity(x, y + 1, z));
        }
        if (world.getTileEntity(x, y - 1, z) instanceof AbstractPowerExpander) {
            totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                    (AbstractPowerExpander) world.getTileEntity(x, y - 1, z));
        }
        if (world.getTileEntity(x, y, z + 1) instanceof AbstractPowerExpander) {
            totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                    (AbstractPowerExpander) world.getTileEntity(x, y, z + 1));
        }
        if (world.getTileEntity(x, y, z - 1) instanceof AbstractPowerExpander) {
            totalExtraCap = totalExtraCap + getPowerExtenderCapacityValue(
                    (AbstractPowerExpander) world.getTileEntity(x, y, z - 1));
        }

        return totalExtraCap;
    }

    private static ItemStack deductFromInvExpander(ItemStack itemStack, AbstractInvExpander exp, TurretBase base) {

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
        int x = base.xCoord;
        int y = base.yCoord;
        int z = base.zCoord;

        if (world.getTileEntity(x + 1, y, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x + 1, y, z);
            ItemStack stack = deductFromInvExpander(itemStack, exp, base);
            if (stack != null) {
                return stack;
            }
        }

        if (world.getTileEntity(x - 1, y, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x - 1, y, z);
            ItemStack stack = deductFromInvExpander(itemStack, exp, base);
            if (stack != null) {
                return stack;
            }
        }

        if (world.getTileEntity(x, y + 1, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y + 1, z);
            ItemStack stack = deductFromInvExpander(itemStack, exp, base);
            if (stack != null) {
                return stack;
            }
        }

        if (world.getTileEntity(x, y - 1, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y - 1, z);
            ItemStack stack = deductFromInvExpander(itemStack, exp, base);
            if (stack != null) {
                return stack;
            }
        }

        if (world.getTileEntity(x, y, z + 1) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y, z + 1);
            ItemStack stack = deductFromInvExpander(itemStack, exp, base);
            if (stack != null) {
                return stack;
            }
        }

        if (world.getTileEntity(x, y, z - 1) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y, z - 1);
            ItemStack stack = deductFromInvExpander(itemStack, exp, base);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    public static ItemStack getAnyItemFromInvExpanders(World world, TurretBase base) {

        int x = base.xCoord;
        int y = base.yCoord;
        int z = base.zCoord;

        if (world.getTileEntity(x + 1, y, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x + 1, y, z);
            for (int i = 0; i < exp.getSizeInventory(); i++) {
                ItemStack itemCheck = exp.getStackInSlot(i);
                if (itemCheck != null) {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(itemCheck.getItem());
                }
            }
        }

        if (world.getTileEntity(x - 1, y, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x - 1, y, z);
            for (int i = 0; i < exp.getSizeInventory(); i++) {
                ItemStack itemCheck = exp.getStackInSlot(i);
                if (itemCheck != null) {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(itemCheck.getItem());
                }
            }
        }

        if (world.getTileEntity(x, y + 1, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y + 1, z);
            for (int i = 0; i < exp.getSizeInventory(); i++) {
                ItemStack itemCheck = exp.getStackInSlot(i);
                if (itemCheck != null) {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(itemCheck.getItem());
                }
            }
        }

        if (world.getTileEntity(x, y - 1, z) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y - 1, z);
            for (int i = 0; i < exp.getSizeInventory(); i++) {
                ItemStack itemCheck = exp.getStackInSlot(i);
                if (itemCheck != null) {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(itemCheck.getItem());
                }
            }
        }

        if (world.getTileEntity(x, y, z + 1) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y, z + 1);
            for (int i = 0; i < exp.getSizeInventory(); i++) {
                ItemStack itemCheck = exp.getStackInSlot(i);
                if (itemCheck != null) {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(itemCheck.getItem());
                }
            }
        }

        if (world.getTileEntity(x, y, z - 1) instanceof AbstractInvExpander) {
            AbstractInvExpander exp = (AbstractInvExpander) world.getTileEntity(x, y, z - 1);
            for (int i = 0; i < exp.getSizeInventory(); i++) {
                ItemStack itemCheck = exp.getStackInSlot(i);
                if (itemCheck != null) {
                    exp.decrStackSize(i, 1);
                    return new ItemStack(itemCheck.getItem());
                }
            }
        }
        return null;
    }

    private static int getPowerExtenderCapacityValue(AbstractPowerExpander expander) {
        if (expander != null) {
            int tier = expander.getTier();

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

    public static TurretBase getTurretBase(World world, int x, int y, int z) {
        if (world == null) {
            return null;
        }

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int offsetX = x + direction.offsetX;
            int offsetY = y + direction.offsetY;
            int offsetZ = z + direction.offsetZ;

            if (world.getTileEntity(offsetX, offsetY, offsetZ) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(offsetX, offsetY, offsetZ);
            }
        }

        return null;
    }

    public static float getAimYaw(Entity target, int xCoord, int zCoord) {
        double dX = (target.posX) - (xCoord);
        double dZ = (target.posZ) - (zCoord);
        float yaw = (float) Math.atan2(dZ, dX);
        yaw = yaw - 1.570796F + 3.1F;
        return yaw;
    }

    public static float getAimPitch(Entity target, int xCoord, int yCoord, int zCoord) {
        double dX = (target.posX - 0.2F) - (xCoord + 0.6F);
        double dY = (target.posY + 0.6F) - (yCoord - 0.6F);
        double dZ = (target.posZ - 0.2F) - (zCoord + 0.6F);
        float pitch = (float) (Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI);
        pitch = pitch + 1.65F;
        return pitch;
    }

    public static ItemStack useAnyItemStackFromBase(TurretBase base) {
        for (int i = 0; i <= 8; i++) {
            ItemStack ammoCheck = base.getStackInSlot(i);
            if (ammoCheck != null && ammoCheck.stackSize > 0 && ammoCheck.getItem() != null) {
                base.decrStackSize(i, 1);
                return new ItemStack(ammoCheck.getItem());
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

    public static ItemStack useSpecificItemStackItemFromBase(TurretBase base, Item item) {
        for (int i = 0; i <= 8; i++) {
            ItemStack ammo_stack = base.getStackInSlot(i);

            if (ammo_stack != null && ammo_stack.stackSize > 0 && ammo_stack.getItem() == item) {
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
        int tier = base.getBaseTier();

        if (tier == 1) {
            return value;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof RangeUpgradeItem) {
                    value += (ConfigHandler.getRangeUpgradeBoost() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof RangeUpgradeItem) {
                value += (ConfigHandler.getRangeUpgradeBoost() * base.getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static int getScattershotUpgrades(TurretBase base) {
        int value = 0;
        int tier = base.getBaseTier();

        if (tier == 1) {
            return value;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof ScattershotUpgradeItem) {
                    value += (ConfigHandler.getRangeUpgradeBoost() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof ScattershotUpgradeItem) {
                value += (ConfigHandler.getRangeUpgradeBoost() * base.getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static float getAccuraccyUpgrades(TurretBase base) {
        float accuracy = 0.0F;
        int tier = base.getBaseTier();

        if (tier == 1) {
            return accuracy;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof AccuracyUpgradeItem) {
                    accuracy += (ConfigHandler.getAccuracyUpgradeBoost() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof AccuracyUpgradeItem) {
                accuracy += (ConfigHandler.getAccuracyUpgradeBoost() * base.getStackInSlot(11).stackSize);
            }
        }

        return accuracy;
    }

    public static float getEfficiencyUpgrades(TurretBase base) {
        float efficiency = 0.0F;
        int tier = base.getBaseTier();

        if (tier == 1) {
            return efficiency;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof EfficiencyUpgradeItem) {
                    efficiency += (ConfigHandler.getEfficiencyUpgradeBoostPercentage() * base.getStackInSlot(
                            12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof EfficiencyUpgradeItem) {
                efficiency += (ConfigHandler.getEfficiencyUpgradeBoostPercentage() * base.getStackInSlot(11).stackSize);
            }
        }

        return efficiency;
    }

    public static float getFireRateUpgrades(TurretBase base) {
        float rof = 0.0F;
        int tier = base.getBaseTier();

        if (tier == 1) {
            return rof;
        }

        if (tier == 5) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof FireRateUpgradeItem) {
                    rof += (ConfigHandler.getFireRateUpgradeBoostPercentage() * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof FireRateUpgradeItem) {
                rof += (ConfigHandler.getFireRateUpgradeBoostPercentage() * base.getStackInSlot(11).stackSize);
            }
        }

        return rof;
    }

    public static boolean hasRedstoneReactor(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof RedstoneReactorAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof RedstoneReactorAddonItem;
        }
        return found;
    }

    public static boolean hasDamageAmpAddon(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof DamageAmpAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof DamageAmpAddonItem;
        }
        return found;
    }

    public static boolean hasConcealmentAddon(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof ConcealerAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof ConcealerAddonItem;
        }
        return found;
    }

    public static boolean hasSolarPanelAddon(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof SolarPanelAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof SolarPanelAddonItem;
        }
        return found;
    }

    public static boolean hasPotentiaUpgradeAddon(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }
        if (!ModCompatibility.ThaumcraftLoaded) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof PotentiaAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof PotentiaAddonItem;
        }
        return found;
    }

    public static boolean hasSerialPortAddon(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }
        if (!ModCompatibility.OpenComputersLoaded && !ModCompatibility.ComputercraftLoaded) {
            return false;
        }

        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof SerialPortAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof SerialPortAddonItem;
        }
        return found;
    }

    private static boolean hasRecyclerAddon(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretBaseTierOneTileEntity) {
            return false;
        }
        if (base.getStackInSlot(9) != null) {
            found = base.getStackInSlot(9).getItem() instanceof RecyclerAddonItem;
        }

        if (base.getStackInSlot(10) != null && !found) {
            found = base.getStackInSlot(10).getItem() instanceof RecyclerAddonItem;
        }
        return found;
    }

    public static int getAmpLevel(TurretBase base) {
        int amp_level = 0;

        if (base == null) {
            return amp_level;
        }

        int tier = base.getBaseTier();

        if (tier == 1) {
            return amp_level;
        }

        if (base.getStackInSlot(10) != null) {
            if (base.getStackInSlot(10).getItem() instanceof DamageAmpAddonItem) {
                amp_level += base.getStackInSlot(10).stackSize;
            }
        }

        if (base.getStackInSlot(9) != null) {
            if (base.getStackInSlot(9).getItem() instanceof DamageAmpAddonItem) {
                amp_level += base.getStackInSlot(9).stackSize;
            }
        }

        return amp_level;
    }

    public static void updateSolarPanelAddon(TurretBase base) {
        if (!hasSolarPanelAddon(base)) {
            return;
        }

        if (base.getWorldObj().isDaytime() && !base.getWorldObj().isRaining() && base.getWorldObj().canBlockSeeTheSky(
                base.xCoord, base.yCoord + 2, base.zCoord)) {
            base.receiveEnergy(ForgeDirection.UNKNOWN, ConfigHandler.getSolarPanelAddonGen(), false);
        }
    }

    public static boolean canTurretSeeTarget(TurretHead turret, EntityLivingBase target) {
        Vec3 traceStart = Vec3.createVectorHelper(turret.xCoord + 0.5F, turret.yCoord + 0.5F, turret.zCoord + 0.5F);
        Vec3 traceEnd = Vec3.createVectorHelper(target.posX, target.posY + target.getEyeHeight(), target.posZ);
        Vec3 vecDelta = Vec3.createVectorHelper(traceEnd.xCoord - traceStart.xCoord,
                                                traceEnd.yCoord - traceStart.yCoord,
                                                traceEnd.zCoord - traceStart.zCoord);

        // Normalize vector to the largest delta axis
        double vecDeltaLength = MathHelper.abs_max(vecDelta.xCoord,
                                                   MathHelper.abs_max(vecDelta.yCoord, vecDelta.zCoord));
        vecDelta.xCoord /= vecDeltaLength;
        vecDelta.yCoord /= vecDeltaLength;
        vecDelta.zCoord /= vecDeltaLength;

        // Limit how many non solid block a turret can see through
        for (int i = 0; i < 10; i++) {
            // Offset start position toward the target to prevent self collision
            traceStart.xCoord += vecDelta.xCoord;
            traceStart.yCoord += vecDelta.yCoord;
            traceStart.zCoord += vecDelta.zCoord;

            MovingObjectPosition traced = turret.getWorldObj().rayTraceBlocks(
                    Vec3.createVectorHelper(traceStart.xCoord, traceStart.yCoord, traceStart.zCoord),
                    Vec3.createVectorHelper(traceEnd.xCoord, traceEnd.yCoord, traceEnd.zCoord));

            if (traced != null && traced.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                Block hitBlock = turret.getWorldObj().getBlock(traced.blockX, traced.blockY, traced.blockZ);

                // If non solid block is in the way then proceed to continue
                // tracing
                if (hitBlock != null && !hitBlock.getMaterial().isSolid() && MathHelper.abs_max(
                        MathHelper.abs_max(traceStart.xCoord - traceEnd.xCoord, traceStart.yCoord - traceEnd.yCoord),
                        traceStart.zCoord - traceEnd.zCoord) > 1) {
                    // Start at new position and continue
                    traceStart = traced.hitVec;
                    continue;
                }
            }

            EntityLivingBase targeted = target != null && traced == null ? target : null;

            return targeted != null && targeted.equals(target);
        }

        // If all above failed, the target cannot be seen
        return false;
    }
}
