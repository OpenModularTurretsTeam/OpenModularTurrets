package omtteam.openmodularturrets.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Turret ammunition with an item of its own (new in 4.0.0, as 1.20.1+ {@code AmmoItem}): registered as
 * {@code ammo_<type>}, e.g. the crossbow bolt. The 3.x ammo stays in {@link AmmoMetaItem}.
 */
public class AmmoItem extends Item {
    private final AmmoType ammoType;

    public AmmoItem(AmmoType ammoType) {
        this.ammoType = ammoType;
        String name = "ammo_" + ammoType.getSerializedName();
        this.setTranslationKey(name);
        this.setRegistryName(Reference.MOD_ID, name);
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        // as AmmoMetaItem
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
            return;
        }
        tooltip.add("");
        tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
        tooltip.add("");
        tooltip.add(safeLocalize("text.openmodularturrets.turret.ammo." + ammoType.getSerializedName()));
    }
}
