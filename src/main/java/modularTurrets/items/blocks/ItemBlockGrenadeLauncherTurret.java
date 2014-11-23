package modularTurrets.items.blocks;

import modularTurrets.misc.Constants;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;
import java.util.List;

public class ItemBlockGrenadeLauncherTurret extends ItemBlock {
    public static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockGrenadeLauncherTurret(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add("\u00A76-Info-");
        p_77624_3_.add("Tier: \u00A7f2");
        p_77624_3_.add("Range: \u00A7f"+ Constants.grenadeTurretRange+" blocks");
        p_77624_3_.add("Accuracy: \u00A7fMedium");
        p_77624_3_.add("Ammo Type: \u00A7fGrenade");
        p_77624_3_.add("Base Minimum Tier: \u00A7fReinforced");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A75-Damage Output-");
        p_77624_3_.add("Projectile Damage: \u00A7f" + (Constants.grenadeTurretDamage/2) + " hearts");
        p_77624_3_.add("AOE Radius: 3");
        p_77624_3_.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.grenadeTurretFireRate));
        p_77624_3_.add("Energy Usage per shot: \u00A7f"+Constants.grenadeTurretPowerUse+" RF");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A78Another example is the inductive");
        p_77624_3_.add("\u00A78form of the horse paradox.");
    }
}
