package omtteam.openmodularturrets.blockentity.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.omlib.camo.CamoData;
import omtteam.omlib.camo.DisguiseRules;
import omtteam.omlib.camo.CamoHost;
import omtteam.openmodularturrets.camo.CamoRules;
import omtteam.openmodularturrets.camo.CamoLightProperties;
import omtteam.omlib.compat.CompatBlockEntity;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.openmodularturrets.core.camo.CamoPermissions;
import omtteam.omlib.core.security.AccessProfile;
import omtteam.omlib.core.security.TrustManager;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.omlib.security.LevelSecurityEnvironment;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.omlib.security.SecurityNbt;
import omtteam.omlib.security.OwnershipSettings;
import omtteam.omlib.security.TrustHost;
import omtteam.openmodularturrets.security.OMTOwnershipSettings;
import org.jetbrains.annotations.Nullable;

/**
 * The camouflage trap (OMPD {@code TileEntityCamo}): owner, trusted players, and the block it looks like. Without
 * camouflage it shows its own texture. Its light settings are block state properties, like on the tier 4 and 5 turret
 * bases.
 * <p>
 * The trust list only knows two levels ({@link AccessProfile#TRUSTED_AND_ADMIN}): trusted players can stand on the
 * trap, admins (the owner always is one) also open its menus and manage the list. Traps saved before the trust list
 * existed load with the owner they had.
 */
public class CamoTrapBlockEntity extends CompatBlockEntity implements CamoHost, TrustHost {
    private final CamoData camoData = new CamoData("Camo", state -> true);
    private final TrustManager trustManager = new TrustManager(new LevelSecurityEnvironment(this::getLevel, OMTOwnershipSettings.INSTANCE),
            this::syncToClient, AccessProfile.TRUSTED_AND_ADMIN);

    public CamoTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAMO_TRAP, pos, state);
    }

    @Override
    public OwnershipSettings ownershipSettings() {
        return OMTOwnershipSettings.INSTANCE;
    }

    @Override
    public TrustManager getTrustManager() {
        return trustManager;
    }

    /** Whether the player may stand on the trap: the owner, trusted players and the owner's team. */
    public boolean isTrusted(Player player) {
        return trustManager.isOwnerOrTrusted(PlayerAdapter.identity(player));
    }

    @Nullable
    @Override
    public BlockState getCamo() {
        return camoData.get();
    }

    @Override
    public void setCamo(@Nullable BlockState camo) {
        if (camoData.set(camo)) {
            syncToClient();
        }
    }

    @Override
    public DisguiseRules disguiseRules() {
        return CamoRules.RULES;
    }

    @Override
    public boolean isCamoEnabled() {
        return true;
    }

    @Override
    public boolean supportsLight() {
        return true;
    }

    @Override
    public CamoLight getLight() {
        return CamoLightProperties.read(getBlockState());
    }

    @Override
    public void setLight(CamoLight light) {
        if (level != null) {
            CamoLightProperties.apply(level, worldPosition, light);
        }
    }

    @Override
    public boolean canSeeThroughCamo(Player player) {
        return CamoPermissions.canManageTrap(trustManager, PlayerAdapter.info(player));
    }

    @Override
    public boolean canSetCamo(Player player) {
        return CamoPermissions.canManageTrap(trustManager, PlayerAdapter.info(player));
    }

    @Override
    public boolean canChangeLight(Player player) {
        return CamoPermissions.canManageTrap(trustManager, PlayerAdapter.info(player));
    }

    /** The camouflage the model shows. */
    @Override
    protected @Nullable Object renderData() {
        return camoData.get();
    }

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        SecurityNbt.load(trustManager, tag);
        if (camoData.read(tag)) {
            remesh(); // the model reads the disguise while the chunk is meshed
        }
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        SecurityNbt.save(trustManager, tag);
        camoData.write(tag);
    }
}
