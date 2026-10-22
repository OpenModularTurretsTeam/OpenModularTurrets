package omtteam.openmodularturrets.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 4.0.0 merged the 14 turret tile entities of 3.x into one ({@code TurretTileEntity}): their ids become
 * {@code openmodularturrets:turret}. The NBT keys stay the same.
 */
public class TurretTileEntityFix implements IFixableData {
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    private static final String TURRET = Reference.MOD_ID + ":" + OMTNames.Blocks.turret;
    private static final Set<String> OLD_IDS;

    static {
        Set<String> ids = new HashSet<>();
        for (String name : Arrays.asList(OMTNames.Blocks.disposableItemTurret, OMTNames.Blocks.potatoCannonTurret,
                OMTNames.Blocks.crossbowTurret, OMTNames.Blocks.meleeTurret, OMTNames.Blocks.machineGunTurret,
                OMTNames.Blocks.incendiaryTurret, OMTNames.Blocks.grenadeTurret, OMTNames.Blocks.relativisticTurret,
                OMTNames.Blocks.rocketTurret, OMTNames.Blocks.teleporterTurret, OMTNames.Blocks.arcTurret,
                OMTNames.Blocks.laserTurret, OMTNames.Blocks.railGunTurret, OMTNames.Blocks.plasmaTurret)) {
            ids.add(Reference.MOD_ID + ":" + name);
        }
        OLD_IDS = Collections.unmodifiableSet(ids);
    }

    @Override
    public int getFixVersion() {
        return 2;
    }

    @Nonnull
    @Override
    public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
        String oldId = compound.getString("id");
        if (OLD_IDS.contains(oldId)) {
            LOGGER.debug("Fixed TE from {} to {}", oldId, TURRET);
            compound.setString("id", TURRET);
        }
        return compound;
    }
}
