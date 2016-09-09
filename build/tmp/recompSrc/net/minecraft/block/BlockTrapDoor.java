package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTrapDoor extends Block
{
    /** Set this to allow trapdoors to remain free-floating */
    public static boolean disableValidation = false;
    private static final String __OBFID = "CL_00000327";

    protected BlockTrapDoor(Material p_i45434_1_)
    {
        super(p_i45434_1_);
        float f = 0.5F;
        float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        return !isTrapdoorOpen(worldIn.getBlockMetadata(x, y, z));
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 0;
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
        this.func_150117_b(worldIn.getBlockMetadata(x, y, z));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.5F - f / 2.0F, 0.0F, 1.0F, 0.5F + f / 2.0F, 1.0F);
    }

    public void func_150117_b(int p_150117_1_)
    {
        float f = 0.1875F;

        if ((p_150117_1_ & 8) != 0)
        {
            this.setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if (isTrapdoorOpen(p_150117_1_))
        {
            if ((p_150117_1_ & 3) == 0)
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }

            if ((p_150117_1_ & 3) == 1)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }

            if ((p_150117_1_ & 3) == 2)
            {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((p_150117_1_ & 3) == 3)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {}

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (this.blockMaterial == Material.iron)
        {
            return true;
        }
        else
        {
            int i1 = worldIn.getBlockMetadata(x, y, z);
            worldIn.setBlockMetadataWithNotify(x, y, z, i1 ^ 4, 2);
            worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
            return true;
        }
    }

    public void func_150120_a(World p_150120_1_, int p_150120_2_, int p_150120_3_, int p_150120_4_, boolean p_150120_5_)
    {
        int l = p_150120_1_.getBlockMetadata(p_150120_2_, p_150120_3_, p_150120_4_);
        boolean flag1 = (l & 4) > 0;

        if (flag1 != p_150120_5_)
        {
            p_150120_1_.setBlockMetadataWithNotify(p_150120_2_, p_150120_3_, p_150120_4_, l ^ 4, 2);
            p_150120_1_.playAuxSFXAtEntity((EntityPlayer)null, 1003, p_150120_2_, p_150120_3_, p_150120_4_, 0);
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!worldIn.isRemote)
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            int i1 = x;
            int j1 = z;

            if ((l & 3) == 0)
            {
                j1 = z + 1;
            }

            if ((l & 3) == 1)
            {
                --j1;
            }

            if ((l & 3) == 2)
            {
                i1 = x + 1;
            }

            if ((l & 3) == 3)
            {
                --i1;
            }

            if (!(isValidSupportBlock(worldIn.getBlock(i1, y, j1)) || worldIn.isSideSolid(i1, y, j1, ForgeDirection.getOrientation((l & 3) + 2))))
            {
                worldIn.setBlockToAir(x, y, z);
                this.dropBlockAsItem(worldIn, x, y, z, l, 0);
            }

            boolean flag = worldIn.isBlockIndirectlyGettingPowered(x, y, z);

            if (flag || neighbor.canProvidePower())
            {
                this.func_150120_a(worldIn, x, y, z, flag);
            }
        }
    }

    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        int j1 = 0;

        if (side == 2)
        {
            j1 = 0;
        }

        if (side == 3)
        {
            j1 = 1;
        }

        if (side == 4)
        {
            j1 = 2;
        }

        if (side == 5)
        {
            j1 = 3;
        }

        if (side != 1 && side != 0 && subY > 0.5F)
        {
            j1 |= 8;
        }

        return j1;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        if (disableValidation) return true;
        if (side == 0)
        {
            return false;
        }
        else if (side == 1)
        {
            return false;
        }
        else
        {
            if (side == 2)
            {
                ++z;
            }

            if (side == 3)
            {
                --z;
            }

            if (side == 4)
            {
                ++x;
            }

            if (side == 5)
            {
                --x;
            }

            return isValidSupportBlock(worldIn.getBlock(x, y, z)) || worldIn.isSideSolid(x, y, z, ForgeDirection.UP);
        }
    }

    /**
     * parameter is metadata
     */
    public static boolean isTrapdoorOpen(int p_150118_0_)
    {
        return (p_150118_0_ & 4) != 0;
    }

    private static boolean isValidSupportBlock(Block p_150119_0_)
    {
        if (disableValidation) return true;
        return p_150119_0_.blockMaterial.isOpaque() && p_150119_0_.renderAsNormalBlock() || p_150119_0_ == Blocks.glowstone || p_150119_0_ instanceof BlockSlab || p_150119_0_ instanceof BlockStairs;
    }
}