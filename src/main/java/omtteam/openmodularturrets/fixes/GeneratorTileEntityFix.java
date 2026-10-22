package omtteam.openmodularturrets.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * 4.0.0 replaced the 3.x power generator attachment with the generator: its tile entity id
 * {@code openmodularturrets:power_generator} becomes {@code openmodularturrets:generator}. The generator reads the
 * old inventory itself ({@code GeneratorTileEntity}); the block and item are remapped by {@link MissingIds}.
 */
public class GeneratorTileEntityFix implements IFixableData {
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    private static final String OLD_ID = Reference.MOD_ID + ":" + OMTNames.Blocks.powerGenerator;
    private static final String NEW_ID = Reference.MOD_ID + ":" + OMTNames.Blocks.generator;

    @Override
    public int getFixVersion() {
        return 3;
    }

    @Nonnull
    @Override
    public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
        if (OLD_ID.equals(compound.getString("id"))) {
            LOGGER.debug("Fixed TE from {} to {}", OLD_ID, NEW_ID);
            compound.setString("id", NEW_ID);
        }
        return compound;
    }
}
