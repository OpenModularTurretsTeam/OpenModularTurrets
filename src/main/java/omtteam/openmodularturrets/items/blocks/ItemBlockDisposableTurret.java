package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

@SuppressWarnings("deprecation")
public class ItemBlockDisposableTurret extends AbstractItemBlockBaseAddon {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockDisposableTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.disposableItemTurret);
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            tooltip.add(safeLocalize(OMTNames.Localizations.GUI.TURRET_HEAD_DESCRIPTION));
            tooltip.add("");
            tooltip.add(TextFormatting.GOLD + "--" + safeLocalize("tooltip.info") + "--");
            tooltip.add(safeLocalize("tooltip.tier") + ": " + TextFormatting.WHITE + "1");
            tooltip.add(safeLocalize("tooltip.range") + ": " + TextFormatting.WHITE +
                                OMTConfig.TURRETS.disposable_turret.baseRange);
            tooltip.add(safeLocalize("tooltip.accuracy") + ": " + TextFormatting.WHITE +
                                safeLocalize("turret.accuracy.low"));
            tooltip.add(safeLocalize("tooltip.ammo") + ": " + TextFormatting.WHITE +
                                safeLocalize("turret.ammo.0"));
            tooltip.add(safeLocalize("tooltip.tier_required") + ": " + TextFormatting.WHITE +
                                safeLocalize("base.tier.1"));
            tooltip.add("");
            tooltip.add(
                    TextFormatting.DARK_PURPLE + "--" + safeLocalize("tooltip.damage.label") + "--");
            tooltip.add(safeLocalize("tooltip.damage.stat") + ": " + TextFormatting.WHITE +
                                (OMTConfig.TURRETS.disposable_turret.baseDamage / 2F) + " " + safeLocalize(
                    "tooltip.health"));
            tooltip.add(safeLocalize("tooltip.aoe") + ": " + TextFormatting.WHITE + "0");
            tooltip.add(safeLocalize("tooltip.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                    20.0F / OMTConfig.TURRETS.disposable_turret.baseFireRate));
            tooltip.add(safeLocalize("tooltip.energy.stat") + ": " + TextFormatting.WHITE +
                                OMTConfig.TURRETS.disposable_turret.powerUsage + " RF");
            tooltip.add("");
            tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.turret.0"));
        }
    }
}
