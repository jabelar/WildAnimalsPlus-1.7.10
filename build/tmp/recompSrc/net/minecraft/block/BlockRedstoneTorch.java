package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch
{
    private boolean field_150113_a;
    private static Map field_150112_b = new HashMap();
    private static final String __OBFID = "CL_00000298";

    private boolean func_150111_a(World p_150111_1_, int p_150111_2_, int p_150111_3_, int p_150111_4_, boolean p_150111_5_)
    {
        if (!field_150112_b.containsKey(p_150111_1_))
        {
            field_150112_b.put(p_150111_1_, new ArrayList());
        }

        List list = (List)field_150112_b.get(p_150111_1_);

        if (p_150111_5_)
        {
            list.add(new BlockRedstoneTorch.Toggle(p_150111_2_, p_150111_3_, p_150111_4_, p_150111_1_.getTotalWorldTime()));
        }

        int l = 0;

        for (int i1 = 0; i1 < list.size(); ++i1)
        {
            BlockRedstoneTorch.Toggle toggle = (BlockRedstoneTorch.Toggle)list.get(i1);

            if (toggle.field_150847_a == p_150111_2_ && toggle.field_150845_b == p_150111_3_ && toggle.field_150846_c == p_150111_4_)
            {
                ++l;

                if (l >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(boolean p_i45423_1_)
    {
        this.field_150113_a = p_i45423_1_;
        this.setTickRandomly(true);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 2;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        if (worldIn.getBlockMetadata(x, y, z) == 0)
        {
            super.onBlockAdded(worldIn, x, y, z);
        }

        if (this.field_150113_a)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        if (this.field_150113_a)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        }
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        if (!this.field_150113_a)
        {
            return 0;
        }
        else
        {
            int i1 = worldIn.getBlockMetadata(x, y, z);
            return i1 == 5 && side == 1 ? 0 : (i1 == 3 && side == 3 ? 0 : (i1 == 4 && side == 2 ? 0 : (i1 == 1 && side == 5 ? 0 : (i1 == 2 && side == 4 ? 0 : 15))));
        }
    }

    private boolean func_150110_m(World p_150110_1_, int p_150110_2_, int p_150110_3_, int p_150110_4_)
    {
        int l = p_150110_1_.getBlockMetadata(p_150110_2_, p_150110_3_, p_150110_4_);
        return l == 5 && p_150110_1_.getIndirectPowerOutput(p_150110_2_, p_150110_3_ - 1, p_150110_4_, 0) ? true : (l == 3 && p_150110_1_.getIndirectPowerOutput(p_150110_2_, p_150110_3_, p_150110_4_ - 1, 2) ? true : (l == 4 && p_150110_1_.getIndirectPowerOutput(p_150110_2_, p_150110_3_, p_150110_4_ + 1, 3) ? true : (l == 1 && p_150110_1_.getIndirectPowerOutput(p_150110_2_ - 1, p_150110_3_, p_150110_4_, 4) ? true : l == 2 && p_150110_1_.getIndirectPowerOutput(p_150110_2_ + 1, p_150110_3_, p_150110_4_, 5))));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        boolean flag = this.func_150110_m(worldIn, x, y, z);
        List list = (List)field_150112_b.get(worldIn);

        while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - ((BlockRedstoneTorch.Toggle)list.get(0)).field_150844_d > 60L)
        {
            list.remove(0);
        }

        if (this.field_150113_a)
        {
            if (flag)
            {
                worldIn.setBlock(x, y, z, Blocks.unlit_redstone_torch, worldIn.getBlockMetadata(x, y, z), 3);

                if (this.func_150111_a(worldIn, x, y, z, true))
                {
                    worldIn.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int l = 0; l < 5; ++l)
                    {
                        double d0 = (double)x + random.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double)y + random.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double)z + random.nextDouble() * 0.6D + 0.2D;
                        worldIn.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
        else if (!flag && !this.func_150111_a(worldIn, x, y, z, false))
        {
            worldIn.setBlock(x, y, z, Blocks.redstone_torch, worldIn.getBlockMetadata(x, y, z), 3);
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!this.func_150108_b(worldIn, x, y, z, neighbor))
        {
            boolean flag = this.func_150110_m(worldIn, x, y, z);

            if (this.field_150113_a && flag || !this.field_150113_a && !flag)
            {
                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
            }
        }
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return side == 0 ? this.isProvidingWeakPower(worldIn, x, y, z, side) : 0;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        if (this.field_150113_a)
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            double d0 = (double)((float)x + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)y + 0.7F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)z + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;

            if (l == 1)
            {
                worldIn.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            }
            else if (l == 2)
            {
                worldIn.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            }
            else if (l == 3)
            {
                worldIn.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            }
            else if (l == 4)
            {
                worldIn.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                worldIn.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    public boolean isAssociatedBlock(Block other)
    {
        return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
    }

    static class Toggle
        {
            int field_150847_a;
            int field_150845_b;
            int field_150846_c;
            long field_150844_d;
            private static final String __OBFID = "CL_00000299";

            public Toggle(int p_i45422_1_, int p_i45422_2_, int p_i45422_3_, long p_i45422_4_)
            {
                this.field_150847_a = p_i45422_1_;
                this.field_150845_b = p_i45422_2_;
                this.field_150846_c = p_i45422_3_;
                this.field_150844_d = p_i45422_4_;
            }
        }
}