package openmodularturrets.tileentity.expander;

/**
 * Created by Niel on 2015/07/19.
 */
public class ExpanderInvTierThreeTileEntity extends AbstractInvExpander {

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public int getInventoryStackLimit() {
        return 16;
    }
}
