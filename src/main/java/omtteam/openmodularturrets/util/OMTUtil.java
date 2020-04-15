package omtteam.openmodularturrets.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.omlib.util.player.Player;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.api.lists.AmmoList;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.items.AmmoMetaItem;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created by Keridos on 06/02/17.
 * This Class provides some utility functions for OMT related stuff.
 */
public class OMTUtil {
    public static boolean isItemStackValidAmmo(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) return false;
        return !OMTConfig.GENERAL.useWhitelistForAmmo || itemStack.getItem() == Items.POTATO ||
                itemStack.getItem() == Items.REDSTONE || itemStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)
                || AmmoList.contains(itemStack) || itemStack.getItem() instanceof AmmoMetaItem;
    }

    public static int getFakeDropsLevel(EntityLivingBase entity) {
        Set<String> tags = entity.getTags();
        return (tags.contains("openmodularturrets:fake_drops_0") ? 0 : tags.contains("openmodularturrets:fake_drops_1") ? 1 :
                tags.contains("openmodularturrets:fake_drops_2") ? 2 : tags.contains("openmodularturrets:fake_drops_3") ? 3 : -1);
    }

    public static boolean canDamagePlayer(EntityPlayer entityPlayer, TurretBase base) {
        Player player = new Player(entityPlayer);
        return canDamagePlayer(player, base);
    }

    public static boolean canDamagePlayer(Player player, TurretBase base) {
        if (base != null && player != null) {
            if (!OMTConfig.TURRETS.turretDamageTrustedPlayers) {
                if (PlayerUtil.isPlayerTrusted(player, base)) {
                    return false;
                }
            }
            String team = player.getTeamName();
            return !(PlayerUtil.isPlayerOwner(player, base)
                    || (!team.isEmpty() && team.equals(base.getOwner().getTeamName())));
        }
        return false;
    }

    public static boolean canDamageEntity(Entity entity, TurretBase base) {
        if (entity != null && !(entity instanceof TurretProjectile)) {
            if (entity instanceof EntityPlayer) {
                return canDamagePlayer((EntityPlayer) entity, base);
            }
            if (entity instanceof EntityTameable) {
                EntityLivingBase entityOwner = ((EntityTameable) entity).getOwner();
                if (entityOwner instanceof EntityPlayer) {
                    EntityPlayer owner = (EntityPlayer) entityOwner;
                    return canDamagePlayer(owner, base);
                }
            }
        }
        return true;
    }

    public static void setTagsForTurretHit(Entity entity, TurretBase base) {
        EntityLivingBase entityLivingBase;
        if (entity instanceof EntityLivingBase) {
            entityLivingBase = (EntityLivingBase) entity;
            if (!(entityLivingBase instanceof EntityPlayer) && !entityLivingBase.getTags().contains("openmodularturrets:turret_hit")) {
                entityLivingBase.addTag("openmodularturrets:turret_hit");
            }
            if (!(entityLivingBase instanceof EntityPlayer) && TurretHeadUtil.getFakeDropsLevel(base) > -1) {
                entityLivingBase.addTag("openmodularturrets:fake_drops_" + TurretHeadUtil.getFakeDropsLevel(base));
            }
            if (!(entityLivingBase instanceof EntityPlayer) && !TurretHeadUtil.baseHasNoLootDeleter(base)
                    && !entityLivingBase.getTags().contains("openmodularturrets:dont_drop_loot")) {
                entityLivingBase.addTag("openmodularturrets:dont_drop_loot");
            }
        }
    }

    public static int getRemainingTurretSlots(@Nonnull TurretBase base, @Nullable TurretType turret) {
        int numberOfTurretsRemaining = 1000;
        if (turret != null) {
            numberOfTurretsRemaining = turret.getSettings().maxSimultaneous;
            for (TurretHead turretHead : TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).values()) {
                if (turret.getInternalName().equals(turretHead.getTurretType().getInternalName())) {
                    numberOfTurretsRemaining--;
                }
            }
        }

        switch (base.getTier()) {
            case 1:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfig.BASES.baseTierOne.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfig.BASES.baseTierOne.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 2:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfig.BASES.baseTierTwo.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfig.BASES.baseTierTwo.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 3:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfig.BASES.baseTierThree.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfig.BASES.baseTierThree.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 4:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfig.BASES.baseTierFour.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfig.BASES.baseTierFour.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 5:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfig.BASES.baseTierFive.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfig.BASES.baseTierFive.baseMaxTurrets - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
        }
        return 0;
    }
}
