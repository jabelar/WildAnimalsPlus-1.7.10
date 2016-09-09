package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSign extends BlockContainer
{
    private Class field_149968_a;
    private boolean field_149967_b;
    private static final String __OBFID = "CL_00000306";

    protected BlockSign(Class p_i45426_1_, boolean p_i45426_2_)
    {
        super(Material.wood);
        this.field_149967_b = p_i45426_2_;
        this.field_149968_a = p_i45426_1_;
        float f = 0.25F;
        float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.planks.getBlockTextureFromSide(side);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        if (!this.field_149967_b)
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            float f = 0.28125F;
            float f1 = 0.78125F;
            float f2 = 0.0F;
            float f3 = 1.0F;
            float f4 = 0.125F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

            if (l == 2)
            {
                this.setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
            }

            if (l == 3)
            {
                this.setBlockBounds(f2, f, 0.0F, f3, f1, f4);
            }

            if (l == 4)
            {
                this.setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
            }

            if (l == 5)
            {
                this.setBlockBounds(0.0F, f, f2, f4, f1, f3);
            }
        }
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        return true;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        try
        {
            return (TileEntity)this.field_149968_a.newInstance();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.sign;
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        boolean flag = false;

        if (this.field_149967_b)
        {
            if (!worldIn.getBlock(x, y - 1, z).getMaterial().isSolid())
            {
                flag = true;
            }
        }
        else
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            flag = true;

            if (l == 2 && worldIn.getBlock(x, y, z + 1).getMaterial().isSolid())
            {
                flag = false;
            }

            if (l == 3 && worldIn.getBlock(x, y, z - 1).getMaterial().isSolid())
            {
                flag = false;
            }

            if (l == 4 && worldIn.getBlock(x + 1, y, z).getMaterial().isSolid())
            {
                flag = false;
            }

            if (l == 5 && worldIn.getBlock(x - 1, y, z).getMaterial().isSolid())
            {
                flag = false;
            }
        }

        if (flag)
        {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }

        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.sign;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}
}