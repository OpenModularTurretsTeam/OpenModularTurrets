package openmodularturrets.tileentity.turrets;

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
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.items.addons.DamageAmpAddonItem;
import openmodularturrets.items.addons.RedstoneReactorAddonItem;
import openmodularturrets.items.addons.SolarPanelAddonItem;
import openmodularturrets.items.upgrades.AccuraccyUpgradeItem;
import openmodularturrets.items.upgrades.EfficiencyUpgradeItem;
import openmodularturrets.items.upgrades.FireRateUpgradeItem;
import openmodularturrets.items.upgrades.RangeUpgradeItem;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretWoodBase;

import java.util.List;

public class TurretHeadUtils {

	public static void warnPlayers(TurretBase base, World worldObj,
			int downLowAmount, int xCoord, int yCoord, int zCoord,
			int turretRange) {
		if (base.isAttacksPlayers()) {
			int warnDistance = ConfigHandler.getTurretWarningDistance();
			AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(xCoord
					- turretRange - warnDistance, yCoord - downLowAmount
					- warnDistance, zCoord - turretRange - warnDistance, xCoord
					+ turretRange + warnDistance, yCoord + turretRange
					+ warnDistance, zCoord + turretRange + warnDistance);

			List<EntityPlayerMP> targets = worldObj.getEntitiesWithinAABB(
					EntityPlayerMP.class, axis);

			for (EntityPlayerMP target : targets) {
				if (!target.getDisplayName().equals(base.getOwner())
						&& !isTrustedPlayer(target.getDisplayName(), base)) {
					dispatchWarnMessage(target, worldObj);
				}
			}
		}
	}

	public static void dispatchWarnMessage(EntityPlayerMP player, World worldObj) {
		worldObj.playSoundEffect(player.posX, player.posY, player.posZ,
				"openmodularturrets:warning", 1.0F, 1.0F);
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED
				+ StatCollector.translateToLocal("status.warning")));
	}

	public static Entity getTarget(TurretBase base, World worldObj,
			int downLowAmount, int xCoord, int yCoord, int zCoord,
			int turretRange, TurretHead turret) {
		warnPlayers(base, worldObj, downLowAmount, xCoord, yCoord, zCoord,
				turretRange);

		Entity target = null;

		if (!worldObj.isRemote && base != null && base.getOwner() != null) {
			AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(xCoord
					- turretRange, yCoord - downLowAmount,
					zCoord - turretRange, xCoord + turretRange, yCoord
							+ turretRange, zCoord + turretRange);

			List<Entity> targets = worldObj.getEntitiesWithinAABB(Entity.class,
					axis);

			for (Entity target1 : targets) {

				if (base.isAttacksNeutrals()) {
					if (target1 instanceof EntityAnimal && !target1.isDead) {
						target = target1;
					}
				}

				if (base.isAttacksNeutrals()) {
					if (target1 instanceof EntityAmbientCreature
							&& !target1.isDead) {
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
								&& !isTrustedPlayer(entity.getDisplayName(),
										base)) {
							target = target1;
						}
					}
				}

				if (target != null && turret != null) {
					EntityLivingBase targetELB = (EntityLivingBase) target;
					if (canTurretSeeTarget(turret, targetELB) || true) {
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
		if (world == null) {
			return null;
		}

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			int offsetX = x + direction.offsetX;
			int offsetY = y + direction.offsetY;
			int offsetZ = z + direction.offsetZ;

			if (world.getTileEntity(offsetX, offsetY, offsetZ) instanceof TurretBase) {
				return (TurretBase) world.getTileEntity(offsetX, offsetY,
						offsetZ);
			}
		}

		return null;
	}

	public static float getAimYaw(Entity target, int xCoord, int yCoord,
			int zCoord) {
		double dX = (target.posX) - (xCoord);
		double dZ = (target.posZ) - (zCoord);
		float yaw = (float) Math.atan2(dZ, dX);
		yaw = yaw - 1.570796F + 3.1F;
		return yaw;
	}

	public static float getAimPitch(Entity target, int xCoord, int yCoord,
			int zCoord) {
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
			if (ammoCheck != null && ammoCheck.stackSize > 0
					&& ammoCheck.getItem() != null) {
				base.decrStackSize(i, 1);
				return new ItemStack(ammoCheck.getItem());
			}
		}

		return null;
	}

	public static ItemStack useSpecificItemStackItemFromBase(TurretBase base,
			Item item) {
		for (int i = 0; i <= 8; i++) {
			ItemStack ammo_stack = base.getStackInSlot(i);

			if (ammo_stack != null && ammo_stack.stackSize > 0
					&& ammo_stack.getItem() == item) {
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
					value += (ConfigHandler.getRangeUpgradeBoost() * base
							.getStackInSlot(11).stackSize);
				}
			}
		}

		if (base.getStackInSlot(11) != null) {
			if (base.getStackInSlot(11).getItem() instanceof RangeUpgradeItem) {
				value += (ConfigHandler.getRangeUpgradeBoost() * base
						.getStackInSlot(11).stackSize);
			}
		}

		return value;
	}

	public static float getAccuraccyUpgrades(TurretBase base) {
		float accuracy = 0.0F;
		int tier = base.getBaseTier();

		if (tier == 0) {
			return accuracy;
		}

		if (tier == 4) {
			if (base.getStackInSlot(12) != null) {
				if (base.getStackInSlot(12).getItem() instanceof AccuraccyUpgradeItem) {
					accuracy += (ConfigHandler.getAccuraccyUpgradeBoost() * base
							.getStackInSlot(12).stackSize);
				}
			}
		}

		if (base.getStackInSlot(11) != null) {
			if (base.getStackInSlot(11).getItem() instanceof AccuraccyUpgradeItem) {
				accuracy += (ConfigHandler.getAccuraccyUpgradeBoost() * base
						.getStackInSlot(11).stackSize);
			}
		}

		return accuracy;
	}

	public static float getEfficiencyUpgrades(TurretBase base) {
		float efficiency = 0.0F;
		int tier = base.getBaseTier();

		if (tier == 0) {
			return efficiency;
		}

		if (tier == 4) {
			if (base.getStackInSlot(12) != null) {
				if (base.getStackInSlot(12).getItem() instanceof EfficiencyUpgradeItem) {
					efficiency += (ConfigHandler
							.getEfficiencyUpgradeBoostPercentage() * base
							.getStackInSlot(12).stackSize);
				}
			}
		}

		if (base.getStackInSlot(11) != null) {
			if (base.getStackInSlot(11).getItem() instanceof EfficiencyUpgradeItem) {
				efficiency += (ConfigHandler
						.getEfficiencyUpgradeBoostPercentage() * base
						.getStackInSlot(11).stackSize);
			}
		}

		return efficiency;
	}

	public static float getFireRateUpgrades(TurretBase base) {
		float rof = 0.0F;
		int tier = base.getBaseTier();

		if (tier == 0) {
			return rof;
		}

		if (tier == 4) {
			if (base.getStackInSlot(12) != null) {
				if (base.getStackInSlot(12).getItem() instanceof FireRateUpgradeItem) {
					rof += (ConfigHandler.getFireRateUpgradeBoostPercentage() * base
							.getStackInSlot(12).stackSize);
				}
			}
		}

		if (base.getStackInSlot(11) != null) {
			if (base.getStackInSlot(11).getItem() instanceof FireRateUpgradeItem) {
				rof += (ConfigHandler.getFireRateUpgradeBoostPercentage() * base
						.getStackInSlot(11).stackSize);
			}
		}

		return rof;
	}

	public static boolean hasRedstoneReactor(TurretBase base) {
        boolean found = false;
        if (base instanceof TurretWoodBase) {
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
        if (base instanceof TurretWoodBase) {
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

	public static boolean hasSolarPanelAddon(TurretBase base) {
		boolean found = false;
        if (base instanceof TurretWoodBase) {
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

	public static int getAmpLevel(TurretBase base) {
		int amp_level = 0;
		int tier = base.getBaseTier();

		if (tier == 0) {
			return amp_level;
		}

		if (base.getStackInSlot(10) != null) {
			if (base.getStackInSlot(10).getItem() instanceof DamageAmpAddonItem) {
				amp_level += (ConfigHandler.getDamageAmpDmgBonus() * base
						.getStackInSlot(10).stackSize);
			}
		}

		if (base.getStackInSlot(9) != null) {
			if (base.getStackInSlot(9).getItem() instanceof DamageAmpAddonItem) {
				amp_level += (ConfigHandler.getDamageAmpDmgBonus() * base
						.getStackInSlot(9).stackSize);
			}
		}

		return amp_level;
	}

	public static void updateSolarPanelAddon(TurretBase base) {
		if (!hasSolarPanelAddon(base)) {
			return;
		}

		if (base.getWorldObj().isDaytime()
				&& !base.getWorldObj().isRaining()
				&& base.getWorldObj().canBlockSeeTheSky(base.xCoord,
						base.yCoord + 2, base.zCoord)) {
			base.receiveEnergy(ForgeDirection.UNKNOWN,
					ConfigHandler.getSolarPanelAddonGen(), false);
		}
	}

	public static void updateRedstoneReactor(TurretBase base) {
		if (!hasRedstoneReactor(base)) {
			return;
		}

		if (ConfigHandler.getRedstoneReactorAddonGen() < (base
				.getMaxEnergyStored(ForgeDirection.UNKNOWN) - base
				.getEnergyStored(ForgeDirection.UNKNOWN))) {
			ItemStack redstone = useSpecificItemStackItemFromBase(base,
					Items.redstone);

			if (redstone != null) {
				base.receiveEnergy(ForgeDirection.UNKNOWN,
						ConfigHandler.getRedstoneReactorAddonGen(), false);
			}
		}
	}

	public static boolean canTurretSeeTarget(TurretHead turret,
			EntityLivingBase target) {

		InvisibleTurretHelperEntity helper = new InvisibleTurretHelperEntity(
				turret.getWorldObj());

		helper.posX = turret.xCoord;
		helper.posY = turret.yCoord;
		helper.posZ = turret.zCoord;

		if (helper.canEntityBeSeen(target)) {
			helper.setDead();
			return true;			
		}
		helper.setDead();
		return false;

	}
}
