package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockTripWireHook extends Block
{
    private static final String __OBFID = "CL_00000329";

    public BlockTripWireHook()
    {
        super(Material.circuits);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
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
        return 29;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 10;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return (dir == NORTH && worldIn.isSideSolid(x, y, z + 1, NORTH)) ||
               (dir == SOUTH && worldIn.isSideSolid(x, y, z - 1, SOUTH)) ||
               (dir == WEST  && worldIn.isSideSolid(x + 1, y, z, WEST )) ||
               (dir == EAST  && worldIn.isSideSolid(x - 1, y, z, EAST ));
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
        byte b0 = 0;

        if (side == 2 && worldIn.isSideSolid(x, y, z + 1, NORTH, true))
        {
            b0 = 2;
        }

        if (side == 3 && worldIn.isSideSolid(x, y, z - 1, SOUTH, true))
        {
            b0 = 0;
        }

        if (side == 4 && worldIn.isSideSolid(x + 1, y, z, WEST, true))
        {
            b0 = 1;
        }

        if (side == 5 && worldIn.isSideSolid(x - 1, y, z, EAST, true))
        {
            b0 = 3;
        }

        return b0;
    }

    /**
     * Called after a block is placed
     */
    public void onPostBlockPlaced(World worldIn, int x, int y, int z, int meta)
    {
        this.func_150136_a(worldIn, x, y, z, false, meta, false, -1, 0);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (neighbor != this)
        {
            if (this.func_150137_e(worldIn, x, y, z))
            {
                int l = worldIn.getBlockMetadata(x, y, z);
                int i1 = l & 3;
                boolean flag = false;

                if (!worldIn.isSideSolid(x - 1, y, z, EAST) && i1 == 3)
                {
                    flag = true;
                }

                if (!worldIn.isSideSolid(x + 1, y, z, WEST) && i1 == 1)
                {
                    flag = true;
                }

                if (!worldIn.isSideSolid(x, y, z - 1, SOUTH) && i1 == 0)
                {
                    flag = true;
                }

                if (!worldIn.isSideSolid(x, y, z + 1, NORTH) && i1 == 2)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.dropBlockAsItem(worldIn, x, y, z, l, 0);
                    worldIn.setBlockToAir(x, y, z);
                }
            }
        }
    }

    public void func_150136_a(World p_150136_1_, int p_150136_2_, int p_150136_3_, int p_150136_4_, boolean p_150136_5_, int p_150136_6_, boolean p_150136_7_, int p_150136_8_, int p_150136_9_)
    {
        int k1 = p_150136_6_ & 3;
        boolean flag2 = (p_150136_6_ & 4) == 4;
        boolean flag3 = (p_150136_6_ & 8) == 8;
        boolean flag4 = !p_150136_5_;
        boolean flag5 = false;
        boolean flag6 = !p_150136_1_.isSideSolid(p_150136_2_, p_150136_3_ - 1, p_150136_4_, UP);
        int l1 = Direction.offsetX[k1];
        int i2 = Direction.offsetZ[k1];
        int j2 = 0;
        int[] aint = new int[42];
        int k2;
        int l2;
        int i3;
        int j3;

        for (k2 = 1; k2 < 42; ++k2)
        {
            l2 = p_150136_2_ + l1 * k2;
            i3 = p_150136_4_ + i2 * k2;
            Block block = p_150136_1_.getBlock(l2, p_150136_3_, i3);

            if (block == Blocks.tripwire_hook)
            {
                j3 = p_150136_1_.getBlockMetadata(l2, p_150136_3_, i3);

                if ((j3 & 3) == Direction.rotateOpposite[k1])
                {
                    j2 = k2;
                }

                break;
            }

            if (block != Blocks.tripwire && k2 != p_150136_8_)
            {
                aint[k2] = -1;
                flag4 = false;
            }
            else
            {
                j3 = k2 == p_150136_8_ ? p_150136_9_ : p_150136_1_.getBlockMetadata(l2, p_150136_3_, i3);
                boolean flag7 = (j3 & 8) != 8;
                boolean flag8 = (j3 & 1) == 1;
                boolean flag9 = (j3 & 2) == 2;
                flag4 &= flag9 == flag6;
                flag5 |= flag7 && flag8;
                aint[k2] = j3;

                if (k2 == p_150136_8_)
                {
                    p_150136_1_.scheduleBlockUpdate(p_150136_2_, p_150136_3_, p_150136_4_, this, this.tickRate(p_150136_1_));
                    flag4 &= flag7;
                }
            }
        }

        flag4 &= j2 > 1;
        flag5 &= flag4;
        k2 = (flag4 ? 4 : 0) | (flag5 ? 8 : 0);
        p_150136_6_ = k1 | k2;
        int k3;

        if (j2 > 0)
        {
            l2 = p_150136_2_ + l1 * j2;
            i3 = p_150136_4_ + i2 * j2;
            k3 = Direction.rotateOpposite[k1];
            p_150136_1_.setBlockMetadataWithNotify(l2, p_150136_3_, i3, k3 | k2, 3);
            this.func_150134_a(p_150136_1_, l2, p_150136_3_, i3, k3);
            this.func_150135_a(p_150136_1_, l2, p_150136_3_, i3, flag4, flag5, flag2, flag3);
        }

        this.func_150135_a(p_150136_1_, p_150136_2_, p_150136_3_, p_150136_4_, flag4, flag5, flag2, flag3);

        if (!p_150136_5_)
        {
            p_150136_1_.setBlockMetadataWithNotify(p_150136_2_, p_150136_3_, p_150136_4_, p_150136_6_, 3);

            if (p_150136_7_)
            {
                this.func_150134_a(p_150136_1_, p_150136_2_, p_150136_3_, p_150136_4_, k1);
            }
        }

        if (flag2 != flag4)
        {
            for (l2 = 1; l2 < j2; ++l2)
            {
                i3 = p_150136_2_ + l1 * l2;
                k3 = p_150136_4_ + i2 * l2;
                j3 = aint[l2];

                if (j3 >= 0)
                {
                    if (flag4)
                    {
                        j3 |= 4;
                    }
                    else
                    {
                        j3 &= -5;
                    }

                    p_150136_1_.setBlockMetadataWithNotify(i3, p_150136_3_, k3, j3, 3);
                }
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        this.func_150136_a(worldIn, x, y, z, false, worldIn.getBlockMetadata(x, y, z), true, -1, 0);
    }

    private void func_150135_a(World p_150135_1_, int p_150135_2_, int p_150135_3_, int p_150135_4_, boolean p_150135_5_, boolean p_150135_6_, boolean p_150135_7_, boolean p_150135_8_)
    {
        if (p_150135_6_ && !p_150135_8_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.click", 0.4F, 0.6F);
        }
        else if (!p_150135_6_ && p_150135_8_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.click", 0.4F, 0.5F);
        }
        else if (p_150135_5_ && !p_150135_7_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.click", 0.4F, 0.7F);
        }
        else if (!p_150135_5_ && p_150135_7_)
        {
            p_150135_1_.playSoundEffect((double)p_150135_2_ + 0.5D, (double)p_150135_3_ + 0.1D, (double)p_150135_4_ + 0.5D, "random.bowhit", 0.4F, 1.2F / (p_150135_1_.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void func_150134_a(World p_150134_1_, int p_150134_2_, int p_150134_3_, int p_150134_4_, int p_150134_5_)
    {
        p_150134_1_.notifyBlocksOfNeighborChange(p_150134_2_, p_150134_3_, p_150134_4_, this);

        if (p_150134_5_ == 3)
        {
            p_150134_1_.notifyBlocksOfNeighborChange(p_150134_2_ - 1, p_150134_3_, p_150134_4_, this);
        }
        else if (p_150134_5_ == 1)
        {
            p_150134_1_.notifyBlocksOfNeighborChange(p_150134_2_ + 1, p_150134_3_, p_150134_4_, this);
        }
        else if (p_150134_5_ == 0)
        {
            p_150134_1_.notifyBlocksOfNeighborChange(p_150134_2_, p_150134_3_, p_150134_4_ - 1, this);
        }
        else if (p_150134_5_ == 2)
        {
            p_150134_1_.notifyBlocksOfNeighborChange(p_150134_2_, p_150134_3_, p_150134_4_ + 1, this);
        }
    }

    private boolean func_150137_e(World p_150137_1_, int p_150137_2_, int p_150137_3_, int p_150137_4_)
    {
        if (!this.canPlaceBlockAt(p_150137_1_, p_150137_2_, p_150137_3_, p_150137_4_))
        {
            this.dropBlockAsItem(p_150137_1_, p_150137_2_, p_150137_3_, p_150137_4_, p_150137_1_.getBlockMetadata(p_150137_2_, p_150137_3_, p_150137_4_), 0);
            p_150137_1_.setBlockToAir(p_150137_2_, p_150137_3_, p_150137_4_);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z) & 3;
        float f = 0.1875F;

        if (l == 3)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        }
        else if (l == 1)
        {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        }
        else if (l == 0)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        }
        else if (l == 2)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        boolean flag = (meta & 4) == 4;
        boolean flag1 = (meta & 8) == 8;

        if (flag || flag1)
        {
            this.func_150136_a(worldIn, x, y, z, true, meta, false, -1, 0);
        }

        if (flag1)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
            int i1 = meta & 3;

            if (i1 == 3)
            {
                worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            }
            else if (i1 == 1)
            {
                worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            }
            else if (i1 == 0)
            {
                worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            }
            else if (i1 == 2)
            {
                worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            }
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return (worldIn.getBlockMetadata(x, y, z) & 8) == 8 ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);

        if ((i1 & 8) != 8)
        {
            return 0;
        }
        else
        {
            int j1 = i1 & 3;
            return j1 == 2 && side == 2 ? 15 : (j1 == 0 && side == 3 ? 15 : (j1 == 1 && side == 4 ? 15 : (j1 == 3 && side == 5 ? 15 : 0)));
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