package omtteam.openmodularturrets.proxy;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Keridos on 17/02/17.
 * This Class
 */

@SuppressWarnings("EmptyMethod")
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    protected void initHandlers() {
        super.initHandlers();
    }

    @Override
    public void init() {
        super.init();
    }
}
