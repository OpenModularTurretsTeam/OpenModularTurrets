package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.items.blocks.ItemBlockPlasmaLauncherTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.PlasmaLauncherTurretTileEntity;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockPlasmaTurret extends BlockAbstractTurretHead {
    public BlockPlasmaTurret() {
        super();

        this.setUnlocalizedName(OMTNames.Blocks.plasmaTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.plasmaTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockPlasmaLauncherTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new PlasmaLauncherTurretTileEntity();
    }

    @Override
    public TurretType getTurretType() {
        return TurretList.getTurretType(OMTNames.Blocks.plasmaTurret);
    }
}
