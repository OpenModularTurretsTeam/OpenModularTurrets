package modularTurrets.items.blocks;

import modularTurrets.misc.Constants;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;
import java.util.List;

public class ItemBlockRocketTurret extends ItemBlock {
    public static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockRocketTurret(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add("\u00A76-Info-");
        p_77624_3_.add("Tier: \u00A7f3");
        p_77624_3_.add("Range: \u00A7f"+ Constants.rocketTurretRange+" blocks");
        p_77624_3_.add("Accuracy: \u00A7fPin Point");
        p_77624_3_.add("Ammo Type: \u00A7fHoming Rocket");
        p_77624_3_.add("Base Minimum Tier: \u00A7fHardened");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A75-Damage Output-");
        p_77624_3_.add("Projectile Damage: \u00A7f" + (Constants.rocketTurretDamage/2) + " hearts");
        p_77624_3_.add("AOE Radius: \u00A7f5");
        p_77624_3_.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.rocketTurretFireRate));
        p_77624_3_.add("Energy Usage per shot: \u00A7f"+Constants.rocketTurretPowerUse+" RF");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A78Swoosh:boom");
    }
}
