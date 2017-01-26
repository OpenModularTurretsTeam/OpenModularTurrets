package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemBlockPotatoCannonTurret extends ItemBlock {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockPotatoCannonTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.potatoCannonTurret);
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack itemStack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "--" + I18n.translateToLocal("tooltip.info") + "--");
        tooltip.add(I18n.translateToLocal("tooltip.tier") + ": " + TextFormatting.WHITE + "1");
        tooltip.add(I18n.translateToLocal("tooltip.range") + ": " + TextFormatting.WHITE +
                ConfigHandler.getPotatoCannonTurretSettings().getRange());
        tooltip.add(I18n.translateToLocal("tooltip.accuracy") + ": " + TextFormatting.WHITE +
                I18n.translateToLocal("turret.accuracy.medium"));
        tooltip.add(I18n.translateToLocal("tooltip.ammo") + ": " + TextFormatting.WHITE +
                I18n.translateToLocal("turret.ammo.6"));
        tooltip.add(I18n.translateToLocal("tooltip.tier_required") + ": " + TextFormatting.WHITE +
                I18n.translateToLocal("base.tier.1"));
        tooltip.add("");
        tooltip.add(
                TextFormatting.DARK_PURPLE + "--" + I18n.translateToLocal("tooltip.damage.label") + "--");
        tooltip.add(I18n.translateToLocal("tooltip.damage.stat") + ": " + TextFormatting.WHITE +
                (ConfigHandler.getPotatoCannonTurretSettings().getDamage() / 2F) + " " + I18n.translateToLocal(
                "tooltip.health"));
        tooltip.add(I18n.translateToLocal("tooltip.aoe") + ": " + TextFormatting.WHITE + "0");
        tooltip.add(I18n.translateToLocal("tooltip.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                20.0F / ConfigHandler.getPotatoCannonTurretSettings().getFireRate()));
        tooltip.add(I18n.translateToLocal("tooltip.energy.stat") + ": " + TextFormatting.WHITE +
                ConfigHandler.getPotatoCannonTurretSettings().getPowerUsage() + " RF");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("flavour.turret.6"));
    }
}
