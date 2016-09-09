package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockTorch extends Block
{
    private static final String __OBFID = "CL_00000325";

    protected BlockTorch()
    {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
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
        return 2;
    }

    private boolean canPlaceTorchOn(World p_150107_1_, int p_150107_2_, int p_150107_3_, int p_150107_4_)
    {
        if (World.doesBlockHaveSolidTopSurface(p_150107_1_, p_150107_2_, p_150107_3_, p_150107_4_))
        {
            return true;
        }
        else
        {
            Block block = p_150107_1_.getBlock(p_150107_2_, p_150107_3_, p_150107_4_);
            return block.canPlaceTorchOnTop(p_150107_1_, p_150107_2_, p_150107_3_, p_150107_4_);
        }
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return worldIn.isSideSolid(x - 1, y, z, EAST,  true) ||
               worldIn.isSideSolid(x + 1, y, z, WEST,  true) ||
               worldIn.isSideSolid(x, y, z - 1, SOUTH, true) ||
               worldIn.isSideSolid(x, y, z + 1, NORTH, true) ||
               canPlaceTorchOn(worldIn, x, y - 1, z);
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        int j1 = meta;

        if (side == 1 && this.canPlaceTorchOn(worldIn, x, y - 1, z))
        {
            j1 = 5;
        }

        if (side == 2 && worldIn.isSideSolid(x, y, z + 1, NORTH, true))
        {
            j1 = 4;
        }

        if (side == 3 && worldIn.isSideSolid(x, y, z - 1, SOUTH, true))
        {
            j1 = 3;
        }

        if (side == 4 && worldIn.isSideSolid(x + 1, y, z, WEST, true))
        {
            j1 = 2;
        }

        if (side == 5 && worldIn.isSideSolid(x - 1, y, z, EAST, true))
        {
            j1 = 1;
        }

        return j1;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        super.updateTick(worldIn, x, y, z, random);

        if (worldIn.getBlockMetadata(x, y, z) == 0)
        {
            this.onBlockAdded(worldIn, x, y, z);
        }
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        if (worldIn.getBlockMetadata(x, y, z) == 0)
        {
            if (worldIn.isSideSolid(x - 1, y, z, EAST, true))
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 1, 2);
            }
            else if (worldIn.isSideSolid(x + 1, y, z, WEST, true))
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 2, 2);
            }
            else if (worldIn.isSideSolid(x, y, z - 1, SOUTH, true))
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 3, 2);
            }
            else if (worldIn.isSideSolid(x, y, z + 1, NORTH, true))
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 4, 2);
            }
            else if (this.canPlaceTorchOn(worldIn, x, y - 1, z))
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, 5, 2);
            }
        }

        this.dropTorchIfCantStay(worldIn, x, y, z);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        this.func_150108_b(worldIn, x, y, z, neighbor);
    }

    protected boolean func_150108_b(World p_150108_1_, int p_150108_2_, int p_150108_3_, int p_150108_4_, Block p_150108_5_)
    {
        if (this.dropTorchIfCantStay(p_150108_1_, p_150108_2_, p_150108_3_, p_150108_4_))
        {
            int l = p_150108_1_.getBlockMetadata(p_150108_2_, p_150108_3_, p_150108_4_);
            boolean flag = false;

            if (!p_150108_1_.isSideSolid(p_150108_2_ - 1, p_150108_3_, p_150108_4_, EAST, true) && l == 1)
            {
                flag = true;
            }

            if (!p_150108_1_.isSideSolid(p_150108_2_ + 1, p_150108_3_, p_150108_4_, WEST, true) && l == 2)
            {
                flag = true;
            }

            if (!p_150108_1_.isSideSolid(p_150108_2_, p_150108_3_, p_150108_4_ - 1, SOUTH, true) && l == 3)
            {
                flag = true;
            }

            if (!p_150108_1_.isSideSolid(p_150108_2_, p_150108_3_, p_150108_4_ + 1, NORTH, true) && l == 4)
            {
                flag = true;
            }

            if (!this.canPlaceTorchOn(p_150108_1_, p_150108_2_, p_150108_3_ - 1, p_150108_4_) && l == 5)
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(p_150108_1_, p_150108_2_, p_150108_3_, p_150108_4_, p_150108_1_.getBlockMetadata(p_150108_2_, p_150108_3_, p_150108_4_), 0);
                p_150108_1_.setBlockToAir(p_150108_2_, p_150108_3_, p_150108_4_);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    protected boolean dropTorchIfCantStay(World p_150109_1_, int p_150109_2_, int p_150109_3_, int p_150109_4_)
    {
        if (!this.canPlaceBlockAt(p_150109_1_, p_150109_2_, p_150109_3_, p_150109_4_))
        {
            if (p_150109_1_.getBlock(p_150109_2_, p_150109_3_, p_150109_4_) == this)
            {
                this.dropBlockAsItem(p_150109_1_, p_150109_2_, p_150109_3_, p_150109_4_, p_150109_1_.getBlockMetadata(p_150109_2_, p_150109_3_, p_150109_4_), 0);
                p_150109_1_.setBlockToAir(p_150109_2_, p_150109_3_, p_150109_4_);
            }

            return false;
        }
        else
        {
            return true;
        }
    }

    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec)
    {
        int l = worldIn.getBlockMetadata(x, y, z) & 7;
        float f = 0.15F;

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
        else
        {
            f = 0.1F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }

        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        double d0 = (double)((float)x + 0.5F);
        double d1 = (double)((float)y + 0.7F);
        double d2 = (double)((float)z + 0.5F);
        double d3 = 0.2199999988079071D;
        double d4 = 0.27000001072883606D;

        if (l == 1)
        {
            worldIn.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle("flame", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        }
        else if (l == 2)
        {
            worldIn.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        }
        else if (l == 3)
        {
            worldIn.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle("flame", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
        }
        else if (l == 4)
        {
            worldIn.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle("flame", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
        else
        {
            worldIn.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}