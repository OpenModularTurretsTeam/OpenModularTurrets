package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class ItemBlockLever extends AbtractItemBlockBaseAddon {
    public ItemBlockLever(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.lever);
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(safeLocalize(OMTNames.Localizations.GUI.TURRET_LEVER_DESCRIPTION));
    }
}
