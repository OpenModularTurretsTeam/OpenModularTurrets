package openmodularturrets.tileentity.expander;

/**
 * Created by Niel on 2015/07/19.
 */
public class ExpanderInvTierFourTileEntity extends AbstractInvExpander {

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public int getInventoryStackLimit() {
        return 32;
    }
}
