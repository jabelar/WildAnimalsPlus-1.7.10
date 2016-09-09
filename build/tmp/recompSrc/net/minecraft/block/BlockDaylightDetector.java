package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer
{
    private IIcon[] field_149958_a = new IIcon[2];
    private static final String __OBFID = "CL_00000223";

    public BlockDaylightDetector()
    {
        super(Material.wood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return worldIn.getBlockMetadata(x, y, z);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random) {}

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {}

    public void onBlockAdded(World worldIn, int x, int y, int z) {}

    public void func_149957_e(World p_149957_1_, int p_149957_2_, int p_149957_3_, int p_149957_4_)
    {
        if (!p_149957_1_.provider.hasNoSky)
        {
            int l = p_149957_1_.getBlockMetadata(p_149957_2_, p_149957_3_, p_149957_4_);
            int i1 = p_149957_1_.getSavedLightValue(EnumSkyBlock.Sky, p_149957_2_, p_149957_3_, p_149957_4_) - p_149957_1_.skylightSubtracted;
            float f = p_149957_1_.getCelestialAngleRadians(1.0F);

            if (f < (float)Math.PI)
            {
                f += (0.0F - f) * 0.2F;
            }
            else
            {
                f += (((float)Math.PI * 2F) - f) * 0.2F;
            }

            i1 = Math.round((float)i1 * MathHelper.cos(f));

            if (i1 < 0)
            {
                i1 = 0;
            }

            if (i1 > 15)
            {
                i1 = 15;
            }

            if (l != i1)
            {
                p_149957_1_.setBlockMetadataWithNotify(p_149957_2_, p_149957_3_, p_149957_4_, i1, 3);
            }
        }
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDaylightDetector();
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.field_149958_a[0] : this.field_149958_a[1];
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149958_a[0] = reg.registerIcon(this.getTextureName() + "_top");
        this.field_149958_a[1] = reg.registerIcon(this.getTextureName() + "_side");
    }
}