package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockLadder extends Block
{
    private static final String __OBFID = "CL_00000262";

    protected BlockLadder()
    {
        super(Material.circuits);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.func_149797_b(worldIn.getBlockMetadata(x, y, z));
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

    public void func_149797_b(int p_149797_1_)
    {
        float f = 0.125F;

        if (p_149797_1_ == 2)
        {
            this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

        if (p_149797_1_ == 3)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        }

        if (p_149797_1_ == 4)
        {
            this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (p_149797_1_ == 5)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 8;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return worldIn.isSideSolid(x - 1, y, z, EAST ) ||
               worldIn.isSideSolid(x + 1, y, z, WEST ) ||
               worldIn.isSideSolid(x, y, z - 1, SOUTH) ||
               worldIn.isSideSolid(x, y, z + 1, NORTH);
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        int j1 = meta;

        if ((meta == 0 || side == 2) && worldIn.isSideSolid(x, y, z + 1, NORTH))
        {
            j1 = 2;
        }

        if ((j1 == 0 || side == 3) && worldIn.isSideSolid(x, y, z - 1, SOUTH))
        {
            j1 = 3;
        }

        if ((j1 == 0 || side == 4) && worldIn.isSideSolid(x + 1, y, z, WEST))
        {
            j1 = 4;
        }

        if ((j1 == 0 || side == 5) && worldIn.isSideSolid(x - 1, y, z, EAST))
        {
            j1 = 5;
        }

        return j1;
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        boolean flag = false;

        if (l == 2 && worldIn.isSideSolid(x, y, z + 1, NORTH))
        {
            flag = true;
        }

        if (l == 3 && worldIn.isSideSolid(x, y, z - 1, SOUTH))
        {
            flag = true;
        }

        if (l == 4 && worldIn.isSideSolid(x + 1, y, z, WEST))
        {
            flag = true;
        }

        if (l == 5 && worldIn.isSideSolid(x - 1, y, z, EAST))
        {
            flag = true;
        }

        if (!flag)
        {
            this.dropBlockAsItem(worldIn, x, y, z, l, 0);
            worldIn.setBlockToAir(x, y, z);
        }

        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 1;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        return true;
    }
}