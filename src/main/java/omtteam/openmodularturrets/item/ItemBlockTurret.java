package omtteam.openmodularturrets.item;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.TurretRegistry;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * The item of a turret block (1.12.2: the 14 {@code ItemBlock…Turret} classes); the ammo name in its tooltip comes
 * from the ammo's {@link AmmoDefinition}.
 */
public class ItemBlockTurret extends AbstractItemBlockBaseAddon {
    private static final String PREFIX = "tooltip.openmodularturrets.turret.";

    public ItemBlockTurret(Block block) {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    /** The placement preview only shows where the turret may go (base tier, free slot, allowed side). */
    @Override
    public boolean canPlace(EnumFacing facing, World world, BlockPos pos) {
        return super.canPlace(facing, world, pos) && this.getBlock().canPlaceBlockAt(world, pos);
    }

    /**
     * Turret tooltip as in 1.20.1+: a note on turrets that ignore scattershot upgrades without shift, with shift what
     * the turret does, the base tier it needs, its ammo and its energy per shot.
     */
    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        BlockTurret block = (BlockTurret) this.getBlock();
        TurretType type = block.getCoreType();
        if (!type.usesScattershot()) {
            tooltip.add(TextFormatting.GOLD + safeLocalize(PREFIX + "ignores_scattershot"));
        }
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
            return;
        }
        String ammo = PREFIX + "ammo.none";
        if (OMTConfig.TURRETS.doTurretsNeedAmmo && type.getAmmo().isPresent()) {
            ammo = TurretRegistry.ammo(type.getAmmo().get()).map(AmmoDefinition::translationKey)
                    .orElse(type.getAmmo().get().getId());
        }
        // tooltip.<namespace>.turret.<block path>.description, as on 1.20.1+
        ResourceLocation id = getRegistryName();
        tooltip.add(TextFormatting.GRAY + safeLocalize("tooltip." + id.getResourceDomain() + ".turret."
                + id.getResourcePath() + ".description"));
        tooltip.add("");
        tooltip.add(value("tier", String.valueOf(type.getRequiredBaseTier())));
        tooltip.add(value("ammo", safeLocalize(ammo)));
        tooltip.add(value("energy", I18n.format(PREFIX + "energy.per_shot",
                ConfigLogicSettings.INSTANCE.turret(type).powerUsage())));
    }

    /** "Label: value" with a white value. */
    @SideOnly(Side.CLIENT)
    private static String value(String key, String value) {
        return TextFormatting.GRAY + safeLocalize(PREFIX + key) + ": " + TextFormatting.WHITE + value;
    }
}
