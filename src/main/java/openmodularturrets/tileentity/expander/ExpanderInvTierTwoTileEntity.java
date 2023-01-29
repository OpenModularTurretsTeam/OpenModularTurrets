package openmodularturrets.tileentity.expander;

/**
 * Created by Niel on 2015/07/19.
 */
public class ExpanderInvTierTwoTileEntity extends AbstractInvExpander {

    @Override
    public int getTier() {
        return 2;
    }

    @Override
    public int getInventoryStackLimit() {
        return 8;
    }
}
