package omtteam.openmodularturrets.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.omlib.util.PlayerUtil;
import omtteam.openmodularturrets.api.lists.AmmoList;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.items.AmmoMetaItem;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static omtteam.omlib.util.PlayerUtil.isPlayerOwner;
import static omtteam.omlib.util.PlayerUtil.isPlayerTrusted;

/**
 * Created by Keridos on 06/02/17.
 * This Class
 */
public class OMTUtil {
    public static boolean isItemStackValidAmmo(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) return false;
        return !OMTConfigHandler.useWhitelistForAmmo || itemStack.getItem() == Items.POTATO ||
                itemStack.getItem() == Items.REDSTONE || itemStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)
                || AmmoList.contains(itemStack) || itemStack.getItem() instanceof AmmoMetaItem;
    }

    public static int getFakeDropsLevel(EntityLivingBase entity) {
        Set<String> tags = entity.getTags();
        return (tags.contains("openmodularturrets:fake_drops_0") ? 0 : tags.contains("openmodularturrets:fake_drops_1") ? 1 :
                tags.contains("openmodularturrets:fake_drops_2") ? 2 : tags.contains("openmodularturrets:fake_drops_3") ? 3 : -1);
    }

    public static boolean canDamagePlayer(EntityPlayer entityPlayer, TurretBase turretBase) {
        if (entityPlayer != null && !entityPlayer.getEntityWorld().isRemote) {
            if (!OMTConfigHandler.turretDamageTrustedPlayers) {
                if (PlayerUtil.isPlayerTrusted(entityPlayer, turretBase)) {
                    return false;
                }
            }
            return !PlayerUtil.isPlayerOwner(entityPlayer, turretBase);
        }
        return true;
    }

    public static boolean canDamageEntity(Entity entity, TurretBase turretBase) {
        if (entity != null && !entity.getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {
            if (entity instanceof EntityTameable) {
                EntityLivingBase entityOwner = ((EntityTameable) entity).getOwner();
                if (entityOwner instanceof EntityPlayer) {
                    EntityPlayer owner = (EntityPlayer) entityOwner;
                    return !isPlayerOwner(owner, turretBase) && !isPlayerTrusted(owner, turretBase);
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
            if (!(entityLivingBase instanceof EntityPlayer) && !TurretHeadUtil.baseHasNoLootDeleter(base) && !entityLivingBase.getTags().contains("openmodularturrets:dont_drop_loot")) {
                entityLivingBase.addTag("openmodularturrets:dont_drop_loot");
            }
        }
    }

    public static int getRemainingTurretSlots(@Nonnull TurretBase base, @Nullable TurretType turret) {
        int numberOfTurretsRemaining = 1000;
        if (turret != null) {
            numberOfTurretsRemaining = turret.getSettings().getMaxSimultaneous();
            for (TurretHead turretHead : TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).values()) {
                if (turret.getInternalName().equals(turretHead.getTurretType().getInternalName())) {
                    numberOfTurretsRemaining--;
                }
            }
        }

        switch (base.getTier()) {
            case 1:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfigHandler.getBaseTierOneMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfigHandler.getBaseTierOneMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 2:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfigHandler.getBaseTierTwoMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfigHandler.getBaseTierTwoMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 3:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfigHandler.getBaseTierThreeMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfigHandler.getBaseTierThreeMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 4:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfigHandler.getBaseTierFourMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfigHandler.getBaseTierFourMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
            case 5:
                return numberOfTurretsRemaining == 1000 ? Math.max(0, OMTConfigHandler.getBaseTierFiveMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size()) :
                        Math.max(0, Math.min(OMTConfigHandler.getBaseTierFiveMaxTurrets() - TurretHeadUtil.getBaseTurrets(base.getWorld(), base.getPos()).size(), numberOfTurretsRemaining));
        }
        return 0;
    }
}
