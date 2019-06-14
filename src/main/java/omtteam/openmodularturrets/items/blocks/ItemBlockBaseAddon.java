package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Created by Keridos on 22/07/16.
 * This Class
 */
@SuppressWarnings("deprecation")
public class ItemBlockBaseAddon extends AbstractItemBlockBaseAddon {
    private final static String[] subNames = {
            OMTNames.Blocks.baseAddonLootDeleter
    };

    public ItemBlockBaseAddon(Block block) {
        super(block);
        setHasSubtypes(true);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.baseAddon);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 1; i++) {
                subItems.add(new ItemStack(ModBlocks.baseAddon, 1, i));
            }
        }
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack itemStack) {
        return "tile." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            switch (stack.getMetadata()) {
                case 0:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.base_addon_loot_deleter.inv1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.base_addon_loot_deleter.inv2"));
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.base_addon_loot_deleter.inv3"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base_addon_loot_deleter.inv.1"));
            }
        }
    }
}
