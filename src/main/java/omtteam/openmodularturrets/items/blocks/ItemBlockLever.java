package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import omtteam.omlib.compatability.minecraft.CompatItemBlock;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

public class ItemBlockLever extends CompatItemBlock {
    public ItemBlockLever(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.lever);
    }
}
