package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMushroom extends BlockBush implements IGrowable
{
    private static final String __OBFID = "CL_00000272";

    protected BlockMushroom()
    {
        float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (random.nextInt(25) == 0)
        {
            byte b0 = 4;
            int l = 5;
            int i1;
            int j1;
            int k1;

            for (i1 = x - b0; i1 <= x + b0; ++i1)
            {
                for (j1 = z - b0; j1 <= z + b0; ++j1)
                {
                    for (k1 = y - 1; k1 <= y + 1; ++k1)
                    {
                        if (worldIn.getBlock(i1, k1, j1) == this)
                        {
                            --l;

                            if (l <= 0)
                            {
                                return;
                            }
                        }
                    }
                }
            }

            i1 = x + random.nextInt(3) - 1;
            j1 = y + random.nextInt(2) - random.nextInt(2);
            k1 = z + random.nextInt(3) - 1;

            for (int l1 = 0; l1 < 4; ++l1)
            {
                if (worldIn.isAirBlock(i1, j1, k1) && this.canBlockStay(worldIn, i1, j1, k1))
                {
                    x = i1;
                    y = j1;
                    z = k1;
                }

                i1 = x + random.nextInt(3) - 1;
                j1 = y + random.nextInt(2) - random.nextInt(2);
                k1 = z + random.nextInt(3) - 1;
            }

            if (worldIn.isAirBlock(i1, j1, k1) && this.canBlockStay(worldIn, i1, j1, k1))
            {
                worldIn.setBlock(i1, j1, k1, this, 0, 2);
            }
        }
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return super.canPlaceBlockAt(worldIn, x, y, z) && this.canBlockStay(worldIn, x, y, z);
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground.isFullBlock();
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World worldIn, int x, int y, int z)
    {
        if (y >= 0 && y < 256)
        {
            Block block = worldIn.getBlock(x, y - 1, z);
            return block == Blocks.mycelium || block == Blocks.dirt && worldIn.getBlockMetadata(x, y - 1, z) == 2 || worldIn.getFullBlockLightValue(x, y, z) < 13 && block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, this);
        }
        else
        {
            return false;
        }
    }

    public boolean fertilizeMushroom(World p_149884_1_, int p_149884_2_, int p_149884_3_, int p_149884_4_, Random p_149884_5_)
    {
        int l = p_149884_1_.getBlockMetadata(p_149884_2_, p_149884_3_, p_149884_4_);
        p_149884_1_.setBlockToAir(p_149884_2_, p_149884_3_, p_149884_4_);
        WorldGenBigMushroom worldgenbigmushroom = null;

        if (this == Blocks.brown_mushroom)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(0);
        }
        else if (this == Blocks.red_mushroom)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(1);
        }

        if (worldgenbigmushroom != null && worldgenbigmushroom.generate(p_149884_1_, p_149884_5_, p_149884_2_, p_149884_3_, p_149884_4_))
        {
            return true;
        }
        else
        {
            p_149884_1_.setBlock(p_149884_2_, p_149884_3_, p_149884_4_, this, l, 3);
            return false;
        }
    }

    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient)
    {
        return true;
    }

    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z)
    {
        return (double)random.nextFloat() < 0.4D;
    }

    public void fertilize(World worldIn, Random random, int x, int y, int z)
    {
        this.fertilizeMushroom(worldIn, x, y, z, random);
    }
}