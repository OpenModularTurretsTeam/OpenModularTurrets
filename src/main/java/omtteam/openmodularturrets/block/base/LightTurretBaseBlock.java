package omtteam.openmodularturrets.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import omtteam.openmodularturrets.camo.CamoLightProperties;
//? if >=1.21 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.BaseEntityBlock;
import org.jetbrains.annotations.NotNull;
//?}

/**
 * Turret base of tier 4 and 5: its light value and light opacity can be set (1.12.2 {@code BlockTurretBase}
 * properties {@code light_value} / {@code light_opacity}). Only these tiers carry the 16×16 extra block states.
 */
public class LightTurretBaseBlock extends TurretBaseBlock {
    //? if >=1.21 {
    public static final MapCodec<LightTurretBaseBlock> LIGHT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(4, 5).fieldOf("tier").forGetter(TurretBaseBlock::getTier),
            propertiesCodec()
    ).apply(instance, LightTurretBaseBlock::new));
    //?}

    public LightTurretBaseBlock(int tier, Properties properties) {
        super(tier, properties);
        registerDefaultState(CamoLightProperties.withDefaults(defaultBlockState()));
    }

    public static Properties createProperties(int tier) {
        return TurretBaseBlock.createProperties(tier).lightLevel(CamoLightProperties::lightLevel);
    }

    //? if >=1.21 {
    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return LIGHT_CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        CamoLightProperties.addTo(builder);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return CamoLightProperties.lightBlock(state);
    }

    /** Fully transparent bases let sky light through, like glass. */
    @Override
    @SuppressWarnings("deprecation")
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return CamoLightProperties.lightBlock(state) == 0;
    }
}
