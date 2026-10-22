package omtteam.openmodularturrets.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.TurretSetting;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.turret.TurretTooltip;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * The item of a turret block (1.12.2: the 14 {@code ItemBlock…Turret} classes) with the 3.x tooltip; the texts that
 * differ per turret come from its {@link TurretTooltip}.
 */
public class ItemBlockTurret extends AbstractItemBlockBaseAddon {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockTurret(Block block) {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    /** The placement preview only shows where the turret may go (base tier, free slot, allowed side). */
    @Override
    public boolean canPlace(EnumFacing facing, World world, BlockPos pos) {
        return super.canPlace(facing, world, pos) && this.getBlock().canPlaceBlockAt(world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
            return;
        }
        BlockTurret block = (BlockTurret) this.getBlock();
        TurretType type = block.getCoreType();
        TurretTooltip texts = TurretRegistry.get(type).tooltip();
        TurretSetting setting = block.getTurretType().getSettings();
        String tier = String.valueOf(type.getRequiredBaseTier());
        tooltip.add(safeLocalize(OMTNames.Localizations.GUI.TURRET_HEAD_DESCRIPTION));
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "--" + safeLocalize("text.openmodularturrets.info") + "--");
        tooltip.add(safeLocalize("text.openmodularturrets.tier") + ": " + TextFormatting.WHITE + tier);
        tooltip.add(safeLocalize("text.openmodularturrets.range") + ": " + TextFormatting.WHITE + setting.baseRange);
        tooltip.add(safeLocalize("text.openmodularturrets.accuracy") + ": " + TextFormatting.WHITE
                + safeLocalize(texts.accuracyKey()));
        if (texts.ammoKey() != null) {
            tooltip.add(safeLocalize("text.openmodularturrets.ammo") + ": " + TextFormatting.WHITE
                    + safeLocalize(texts.ammoKey()));
        }
        tooltip.add(safeLocalize("text.openmodularturrets.tier_required") + ": " + TextFormatting.WHITE
                + safeLocalize("base.tier." + tier));
        tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE
                + setting.maxSimultaneous);
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_PURPLE + "--" + safeLocalize("text.openmodularturrets.damage.label") + "--");
        tooltip.add(safeLocalize("text.openmodularturrets.damage.stat") + ": " + TextFormatting.WHITE
                + (setting.baseDamage / 2F) + " " + safeLocalize("text.openmodularturrets.health"));
        tooltip.add(safeLocalize("text.openmodularturrets.aoe") + ": " + TextFormatting.WHITE + texts.areaOfEffect());
        tooltip.add(safeLocalize("text.openmodularturrets.fire_rate") + ": " + TextFormatting.WHITE
                + df.format(20.0F / setting.baseFireRate));
        tooltip.add(safeLocalize("text.openmodularturrets.energy.stat") + ": " + TextFormatting.WHITE
                + setting.powerUsage + " RF");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + safeLocalize(texts.flavourKey()));
    }
}
