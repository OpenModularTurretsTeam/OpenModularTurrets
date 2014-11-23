package modularTurrets.tileentity.turrets;

import java.util.ArrayList;
import java.util.List;

import modularTurrets.items.upgrades.AccuraccyUpgradeItem;
import modularTurrets.items.addons.RedstoneReactorAddonItem;
import modularTurrets.items.addons.DamageAmpAddonItem;
import modularTurrets.items.upgrades.EfficiencyUpgradeItem;
import modularTurrets.items.upgrades.FireRateUpgradeItem;
import modularTurrets.items.upgrades.RangeUpgradeItem;
import modularTurrets.items.addons.SolarPanelAddonItem;
import modularTurrets.misc.ConfigHandler;
import modularTurrets.misc.Constants;
import modularTurrets.tileentity.turretBase.TurretBase;
import modularTurrets.tileentity.turretBase.TurretWoodBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TurretHeadUtils {

    private static List<EntityPlayerMP> warnList = new ArrayList<EntityPlayerMP>();

    public static void warnPlayers(TurretBase base, World worldObj, int downLowAmount, int xCoord, int yCoord, int zCoord, int turretRange) {
        if (base.isAttacksPlayers()) {
            if (worldObj.getTotalWorldTime() % 160 == 0) {
                warnList.clear();
            }

            int warnDistance = ConfigHandler.getTurretWarningDistance();
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(
                    xCoord - turretRange - warnDistance, yCoord - downLowAmount - warnDistance,
                    zCoord - turretRange - warnDistance, xCoord + turretRange + warnDistance,
                    yCoord + turretRange + warnDistance, zCoord + turretRange + warnDistance
            );

            List<Entity> targets = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, axis);

            for (Entity target : targets) {
                EntityPlayerMP entity = (EntityPlayerMP) target;
                if (!entity.getDisplayName().equals(base.getOwner())
                        && !isTrustedPlayer(entity.getDisplayName(), base)) {
                    dispatchWarnMessage(entity, worldObj);
                }
            }
        }
    }

    public static void dispatchWarnMessage(EntityPlayerMP player, World worldObj) {
        if (!warnList.contains(player)) {
            warnList.add(player);
            worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "openmodularturrets:warning", 1.0F, 1.0F);
            player.addChatMessage(new ChatComponentText("\u00A74You are entering a turret-protected area. Continue at your own risk."));
        }
    }

    public static Entity getTarget(TurretBase base, World worldObj, int downLowAmount, int xCoord, int yCoord, int zCoord, int turretRange, EntityLivingBase turret) {
        warnPlayers(base, worldObj, downLowAmount, xCoord, yCoord, zCoord, turretRange);

        Entity target = null;

        if (!worldObj.isRemote && base != null && base.getOwner() != null) {
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(
                    xCoord - turretRange, yCoord - downLowAmount,
                    zCoord - turretRange, xCoord + turretRange,
                    yCoord + turretRange,
                    zCoord + turretRange);

            List<Entity> targets = worldObj.getEntitiesWithinAABB(Entity.class, axis);

            for (Entity target1 : targets) {

                if (base.isAttacksNeutrals()) {
                    if (target1 instanceof EntityAnimal && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksNeutrals()) {
                    if (target1 instanceof EntityAmbientCreature && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs()) {
                    if (target1 instanceof EntityMob && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs()) {
                    if (target1 instanceof EntityFlying && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksMobs()) {
                    if (target1 instanceof EntitySlime && !target1.isDead) {
                        target = target1;
                    }
                }

                if (base.isAttacksPlayers()) {
                    if (target1 instanceof EntityPlayerMP && !target1.isDead) {
                        EntityPlayerMP entity = (EntityPlayerMP) target1;

                        if (!entity.getDisplayName().equals(base.getOwner())
                                && !isTrustedPlayer(entity.getDisplayName(), base)) {
                            target = target1;
                        }
                    }
                }

                if (target != null && turret != null) {
                    EntityLivingBase targetELB = (EntityLivingBase) target;
                    if (canTurretSeeTarget(turret, targetELB)) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isTrustedPlayer(String name, TurretBase base) {
        for (String trusted_player : base.getTrustedPlayers()) {
            if (trusted_player.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static TurretBase getTurretBase(World world, int x, int y, int z) {
        if (world != null) {
            if (world.getTileEntity(x + 1, y, z) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(x + 1, y, z);
            }

            if (world.getTileEntity(x - 1, y, z) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(x - 1, y, z);
            }

            if (world.getTileEntity(x, y, z + 1) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(x, y, z + 1);
            }

            if (world.getTileEntity(x, y, z - 1) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(x, y, z - 1);
            }

            if (world.getTileEntity(x, y + 1, z) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(x, y + 1, z);
            }

            if (world.getTileEntity(x, y - 1, z) instanceof TurretBase) {
                return (TurretBase) world.getTileEntity(x, y - 1, z);
            }
        }

        return null;
    }

    public static float getAimYaw(Entity target, int xCoord, int yCoord, int zCoord) {
        double dX = (target.posX) - (xCoord);
        double dZ = (target.posZ) - (zCoord);
        float yaw = (float) Math.atan2(dZ, dX);
        yaw = yaw - 1.570796F + 3.2F;
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

    public static ItemStack useSpecificItemStackItemFromBase(TurretBase base, Item item) {
        for (int i = 0; i <= 8; i++) {
            ItemStack ammo_stack = base.getStackInSlot(i);

            if (ammo_stack != null && ammo_stack.stackSize > 0 && ammo_stack.getItem() == item) {
                base.decrStackSize(i, 1);
                return new ItemStack(ammo_stack.getItem());
            }
        }

        return null;
    }

    public static int getRangeUpgrades(TurretBase base) {
        int value = 0;
        int tier = base.getBaseTier();

        if (tier == 0) {
            return value;
        }

        if (tier == 4) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof RangeUpgradeItem) {
                    value += (Constants.rangeUpgradeBoost * base.getStackInSlot(11).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof RangeUpgradeItem) {
                value += (Constants.rangeUpgradeBoost * base.getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static float getAccuraccyUpgrades(TurretBase base) {

        float value = 0.0F;
        int tier = base.getBaseTier();

        if (tier == 0) {
            return value;
        }

        if (tier == 4) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof AccuraccyUpgradeItem) {
                    value += (Constants.accuraccyUpgradeBoost * base.getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof AccuraccyUpgradeItem) {
                value += (Constants.accuraccyUpgradeBoost * base
                        .getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static float getEfficiencyUpgrades(TurretBase base) {

        float value = 0.0F;
        int tier = base.getBaseTier();

        if (tier == 0) {
            return value;
        }

        if (tier == 4) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof EfficiencyUpgradeItem) {
                    value += (Constants.efficiencyUpgradeBoostPercentage * base
                            .getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof EfficiencyUpgradeItem) {
                value += (Constants.efficiencyUpgradeBoostPercentage * base
                        .getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static float getFireRateUpgrades(TurretBase base) {
        float value = 0.0F;
        int tier = base.getBaseTier();

        if (tier == 0) {
            return value;
        }

        if (tier == 4) {
            if (base.getStackInSlot(12) != null) {
                if (base.getStackInSlot(12).getItem() instanceof FireRateUpgradeItem) {
                    value += (Constants.fireRateUpgradeBoostPercentage * base
                            .getStackInSlot(12).stackSize);
                }
            }
        }

        if (base.getStackInSlot(11) != null) {
            if (base.getStackInSlot(11).getItem() instanceof FireRateUpgradeItem) {
                value += (Constants.fireRateUpgradeBoostPercentage * base
                        .getStackInSlot(11).stackSize);
            }
        }

        return value;
    }

    public static boolean hasRedstoneReactor(TurretBase base) {
        boolean value = false;
        if (!(base instanceof TurretWoodBase)) {
            for (int i = 9; i <= 10; i++) {
                if (base.getStackInSlot(i) != null) {
                    if (base.getStackInSlot(i).getItem() instanceof RedstoneReactorAddonItem) {
                        value = true;
                    }
                }
            }
            return value;
        }
        return false;
    }

    public static boolean hasDamageAmpAddon(TurretBase base) {
        boolean value = false;
        if (!(base instanceof TurretWoodBase)) {
            for (int i = 9; i <= 10; i++) {
                if (base.getStackInSlot(i) != null) {
                    if (base.getStackInSlot(i).getItem() instanceof DamageAmpAddonItem) {
                        value = true;
                    }
                }
            }
            return value;
        }
        return false;
    }

    public static boolean hasSolarPanelAddon(TurretBase base) {
        boolean value = false;
        if (!(base instanceof TurretWoodBase)) {
            for (int i = 9; i <= 10; i++) {
                if (base.getStackInSlot(i) != null) {
                    if (base.getStackInSlot(i).getItem() instanceof SolarPanelAddonItem) {
                        value = true;
                    }
                }
            }
            return value;
        }
        return false;
    }

    public static void updateSolarPanelAddon(TurretBase base) {
        if (hasSolarPanelAddon(base)) {
            if (base.getWorldObj().isDaytime()
                && !base.getWorldObj().isRaining()
                && base.getWorldObj().canBlockSeeTheSky(base.xCoord,
                    base.yCoord + 2, base.zCoord)) {
                base.receiveEnergy(ForgeDirection.UNKNOWN, 1, false);
            }
        }
    }

    public static void updateRedstoneReactor(TurretBase base) {
        if (hasRedstoneReactor(base)
            && Constants.redstoneReactorAddonGen < (base.getMaxEnergyStored(ForgeDirection.UNKNOWN) - base.getEnergyStored(ForgeDirection.UNKNOWN))) {
            ItemStack redstone = useSpecificItemStackItemFromBase(base,
                    Items.redstone);
            if (redstone != null) {
                base.receiveEnergy(ForgeDirection.UNKNOWN, Constants.redstoneReactorAddonGen, false);
            }
        }
    }

    public static boolean canTurretSeeTarget(EntityLivingBase turret, EntityLivingBase target) {
        return turret.canEntityBeSeen(target);
    }
}
