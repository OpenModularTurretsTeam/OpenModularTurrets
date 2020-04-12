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
public class ItemBlockRailGunTurret extends AbstractItemBlockBaseAddon {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockRailGunTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.railGunTurret);
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
            tooltip.add(TextFormatting.GOLD + "--" + safeLocalize("text.openmodularturrets.info") + "--");
            tooltip.add(safeLocalize("text.openmodularturrets.tier") + ": " + TextFormatting.WHITE + "5");
            tooltip.add(safeLocalize("text.openmodularturrets.range") + ": " + TextFormatting.WHITE +
                                OMTConfig.TURRETS.railgun_turret.baseRange);
            tooltip.add(safeLocalize("text.openmodularturrets.accuracy") + ": " + TextFormatting.WHITE +
                                safeLocalize("text.openmodularturrets.turret.accuracy.exact"));
            tooltip.add(safeLocalize("text.openmodularturrets.ammo") + ": " + TextFormatting.WHITE +
                                safeLocalize("text.openmodularturrets.turret.ammo.5"));
            tooltip.add(safeLocalize("text.openmodularturrets.tier_required") + ": " + TextFormatting.WHITE +
                                safeLocalize("base.tier.5"));
            tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE + OMTConfig.TURRETS.railgun_turret.maxSimultaneous);
            tooltip.add("");
            tooltip.add(
                    TextFormatting.DARK_PURPLE + "--" + safeLocalize("text.openmodularturrets.damage.label") + "--");
            tooltip.add(safeLocalize("text.openmodularturrets.damage.stat") + ": " + TextFormatting.WHITE +
                                (OMTConfig.TURRETS.railgun_turret.baseDamage / 2F) + " " + safeLocalize(
                    "tooltip" + ".health"));
            tooltip.add(safeLocalize("text.openmodularturrets.aoe") + ": " + TextFormatting.WHITE + "0");
            tooltip.add(safeLocalize("text.openmodularturrets.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                    20.0F / OMTConfig.TURRETS.railgun_turret.baseFireRate));
            tooltip.add(safeLocalize("text.openmodularturrets.energy.stat") + ": " + TextFormatting.WHITE +
                                OMTConfig.TURRETS.railgun_turret.powerUsage + " RF");
            tooltip.add("");
            tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.turret.5"));
        }
    }
}
