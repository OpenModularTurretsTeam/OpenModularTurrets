package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;

public class ItemBlockLever extends ItemBlock {
    public ItemBlockLever(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.lever);
    }
}
