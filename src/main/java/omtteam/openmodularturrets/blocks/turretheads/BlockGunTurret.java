package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockMachineGunTurret;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.GunTurretTileEntity;

public class BlockGunTurret extends BlockAbstractTurretHead {
    public BlockGunTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.gunTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.gunTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockMachineGunTurret(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new GunTurretTileEntity();
    }
}
