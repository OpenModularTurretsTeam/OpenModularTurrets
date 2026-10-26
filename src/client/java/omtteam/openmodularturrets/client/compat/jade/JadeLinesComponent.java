package omtteam.openmodularturrets.client.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
//? if >=1.21 {
import snownee.jade.api.IComponentProvider;
//?} else {
/*import snownee.jade.api.IBlockComponentProvider;
*///?}

import java.util.List;
import java.util.function.Function;

/**
 * Client half of a Jade tooltip: shows the lines that the server half's {@code lines} method made from the data the
 * server sent. The tooltip interface is the only thing that differs between the Jade versions of 1.20.1 and 1.21.
 */
final class JadeLinesComponent implements
        //? if >=1.21 {
        IComponentProvider<BlockAccessor>
        //?} else {
        /*IBlockComponentProvider
        *///?}
{
    private final ResourceLocation uid;
    private final Function<BlockAccessor, List<Component>> lines;

    /** @param uid the id of the server half, also the key of the on/off switch in Jade's plugin config */
    JadeLinesComponent(ResourceLocation uid, Function<BlockAccessor, List<Component>> lines) {
        this.uid = uid;
        this.lines = lines;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        lines.apply(accessor).forEach(tooltip::add);
    }

    @Override
    public ResourceLocation getUid() {
        return uid;
    }
}
