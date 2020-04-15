package omtteam.openmodularturrets.fixes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import java.util.Map;

public class TENameFix implements IFixableData {

    private static final Map<String, String> NAME_MAP;

    static {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("minecraft:" + OMTNames.Blocks.turretBase, Reference.MOD_ID + ":" + OMTNames.Blocks.turretBase);
        builder.put("minecraft:" + OMTNames.Blocks.disposableItemTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.disposableItemTurret);
        builder.put("minecraft:" + OMTNames.Blocks.potatoCannonTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.potatoCannonTurret);
        builder.put("minecraft:" + OMTNames.Blocks.rocketTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.rocketTurret);
        builder.put("minecraft:" + OMTNames.Blocks.machineGunTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.machineGunTurret);
        builder.put("minecraft:" + OMTNames.Blocks.grenadeTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.grenadeTurret);
        builder.put("minecraft:" + OMTNames.Blocks.laserTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.laserTurret);
        builder.put("minecraft:" + OMTNames.Blocks.lever, Reference.MOD_ID + ":" + OMTNames.Blocks.lever);
        builder.put("minecraft:" + OMTNames.Blocks.railGunTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.railGunTurret);
        builder.put("minecraft:" + OMTNames.Blocks.incendiaryTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.incendiaryTurret);
        builder.put("minecraft:" + OMTNames.Blocks.relativisticTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.relativisticTurret);
        builder.put("minecraft:" + OMTNames.Blocks.teleporterTurret, Reference.MOD_ID + ":" + OMTNames.Blocks.teleporterTurret);
        builder.put("minecraft:" + OMTNames.Blocks.expander, Reference.MOD_ID + ":" + OMTNames.Blocks.expander);
        builder.put("minecraft:" + OMTNames.Blocks.baseAddon, Reference.MOD_ID + ":" + OMTNames.Blocks.baseAddon);
        NAME_MAP = builder.build();
    }

    @Override
    public int getFixVersion() {
        return 1;
    }

    @Nonnull
    @Override
    public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
        String oldId = compound.getString("id");
        if (NAME_MAP.containsKey(oldId)) {
            OpenModularTurrets.getLogger().info("Fixed TE from {} to {}", oldId, NAME_MAP.get(oldId));
            compound.setString("id", NAME_MAP.get(oldId));
        }
        return compound;
    }
}
