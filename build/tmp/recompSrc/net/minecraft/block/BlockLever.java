package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockLever extends Block
{
    private static final String __OBFID = "CL_00000264";

    protected BlockLever()
    {
        super(Material.circuits);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
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
        return 12;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return (dir == DOWN  && worldIn.isSideSolid(x, y + 1, z, DOWN )) ||
               (dir == UP    && worldIn.isSideSolid(x, y - 1, z, UP   )) ||
               (dir == NORTH && worldIn.isSideSolid(x, y, z + 1, NORTH)) ||
               (dir == SOUTH && worldIn.isSideSolid(x, y, z - 1, SOUTH)) ||
               (dir == WEST  && worldIn.isSideSolid(x + 1, y, z, WEST )) ||
               (dir == EAST  && worldIn.isSideSolid(x - 1, y, z, EAST ));
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return worldIn.isSideSolid(x - 1, y, z, EAST ) ||
               worldIn.isSideSolid(x + 1, y, z, WEST ) ||
               worldIn.isSideSolid(x, y, z - 1, SOUTH) ||
               worldIn.isSideSolid(x, y, z + 1, NORTH) ||
               worldIn.isSideSolid(x, y - 1, z, UP   ) ||
               worldIn.isSideSolid(x, y + 1, z, DOWN );
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        int k1 = meta & 8;
        int j1 = meta & 7;
        byte b0 = -1;

        if (side == 0 && worldIn.isSideSolid(x, y + 1, z, DOWN))
        {
            b0 = 0;
        }

        if (side == 1 && worldIn.isSideSolid(x, y - 1, z, UP))
        {
            b0 = 5;
        }

        if (side == 2 && worldIn.isSideSolid(x, y, z + 1, NORTH))
        {
            b0 = 4;
        }

        if (side == 3 && worldIn.isSideSolid(x, y, z - 1, SOUTH))
        {
            b0 = 3;
        }

        if (side == 4 && worldIn.isSideSolid(x + 1, y, z, WEST))
        {
            b0 = 2;
        }

        if (side == 5 && worldIn.isSideSolid(x - 1, y, z, EAST))
        {
            b0 = 1;
        }

        return b0 + k1;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = l & 7;
        int j1 = l & 8;

        if (i1 == invertMetadata(1))
        {
            if ((MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 1) == 0)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 5 | j1, 2);
            }
            else
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 6 | j1, 2);
            }
        }
        else if (i1 == invertMetadata(0))
        {
            if ((MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 1) == 0)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 7 | j1, 2);
            }
            else
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 0 | j1, 2);
            }
        }
    }

    public static int invertMetadata(int p_149819_0_)
    {
        switch (p_149819_0_)
        {
            case 0:
                return 0;
            case 1:
                return 5;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
            case 5:
                return 1;
            default:
                return -1;
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (this.func_149820_e(worldIn, x, y, z))
        {
            int l = worldIn.getBlockMetadata(x, y, z) & 7;
            boolean flag = false;

            if (!worldIn.isSideSolid(x - 1, y, z, EAST) && l == 1)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x + 1, y, z, WEST) && l == 2)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y, z - 1, SOUTH) && l == 3)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y, z + 1, NORTH) && l == 4)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y - 1, z, UP) && l == 5)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y - 1, z, UP) && l == 6)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y + 1, z, DOWN) && l == 0)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y + 1, z, DOWN) && l == 7)
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    private boolean func_149820_e(World p_149820_1_, int p_149820_2_, int p_149820_3_, int p_149820_4_)
    {
        if (!this.canPlaceBlockAt(p_149820_1_, p_149820_2_, p_149820_3_, p_149820_4_))
        {
            this.dropBlockAsItem(p_149820_1_, p_149820_2_, p_149820_3_, p_149820_4_, p_149820_1_.getBlockMetadata(p_149820_2_, p_149820_3_, p_149820_4_), 0);
            p_149820_1_.setBlockToAir(p_149820_2_, p_149820_3_, p_149820_4_);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z) & 7;
        float f = 0.1875F;

        if (l == 1)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        }
        else if (l == 2)
        {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        }
        else if (l == 3)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        }
        else if (l == 4)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
        else if (l != 5 && l != 6)
        {
            if (l == 0 || l == 7)
            {
                f = 0.25F;
                this.setBlockBounds(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
            }
        }
        else
        {
            f = 0.25F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            int i1 = worldIn.getBlockMetadata(x, y, z);
            int j1 = i1 & 7;
            int k1 = 8 - (i1 & 8);
            worldIn.setBlockMetadataWithNotify(x, y, z, j1 + k1, 3);
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, k1 > 0 ? 0.6F : 0.5F);
            worldIn.notifyBlocksOfNeighborChange(x, y, z, this);

            if (j1 == 1)
            {
                worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            }
            else if (j1 == 2)
            {
                worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            }
            else if (j1 == 3)
            {
                worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            }
            else if (j1 == 4)
            {
                worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            }
            else if (j1 != 5 && j1 != 6)
            {
                if (j1 == 0 || j1 == 7)
                {
                    worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
                }
            }
            else
            {
                worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            }

            return true;
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        if ((meta & 8) > 0)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
            int i1 = meta & 7;

            if (i1 == 1)
            {
                worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            }
            else if (i1 == 2)
            {
                worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            }
            else if (i1 == 3)
            {
                worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            }
            else if (i1 == 4)
            {
                worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            }
            else if (i1 != 5 && i1 != 6)
            {
                if (i1 == 0 || i1 == 7)
                {
                    worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
                }
            }
            else
            {
                worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            }
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return (worldIn.getBlockMetadata(x, y, z) & 8) > 0 ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);

        if ((i1 & 8) == 0)
        {
            return 0;
        }
        else
        {
            int j1 = i1 & 7;
            return j1 == 0 && side == 0 ? 15 : (j1 == 7 && side == 0 ? 15 : (j1 == 6 && side == 1 ? 15 : (j1 == 5 && side == 1 ? 15 : (j1 == 4 && side == 2 ? 15 : (j1 == 3 && side == 3 ? 15 : (j1 == 2 && side == 4 ? 15 : (j1 == 1 && side == 5 ? 15 : 0)))))));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }
}