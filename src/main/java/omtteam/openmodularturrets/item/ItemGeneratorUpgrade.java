package omtteam.openmodularturrets.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * An upgrade for the generator's upgrade slot (new in 4.0.0, as on 1.20.1+); one item per
 * {@link GeneratorUpgradeType}, registered as {@code upgrade_<type>}.
 */
public class ItemGeneratorUpgrade extends Item {
    private final GeneratorUpgradeType upgradeType;

    public ItemGeneratorUpgrade(GeneratorUpgradeType upgradeType) {
        this.upgradeType = upgradeType;
        String name = "upgrade_" + upgradeType.getSerializedName();
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setUnlocalizedName(name);
        this.setRegistryName(Reference.MOD_ID, name);
    }

    public GeneratorUpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
            return;
        }
        String key = "tooltip.openmodularturrets.upgrade." + upgradeType.getSerializedName();
        tooltip.add("");
        tooltip.add(TextFormatting.BLUE + safeLocalize("tooltip.openmodularturrets.generator_upgrade.label"));
        tooltip.add("");
        tooltip.add(safeLocalize(key + ".a"));
        tooltip.add(safeLocalize(key + ".b"));
        tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.stacks"));
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + safeLocalize(key + ".flavour"));
    }
}
