package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block
{
    private static final String __OBFID = "CL_00000328";

    public BlockTripWire()
    {
        super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.setTickRandomly(true);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 10;
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
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 1;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 30;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.string;
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        boolean flag = (l & 2) == 2;
        boolean flag1 = !World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);

        if (flag != flag1)
        {
            this.dropBlockAsItem(worldIn, x, y, z, l, 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 2) == 2;

        if (!flag1)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        }
        else if (!flag)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.string;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        int l = World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) ? 0 : 2;
        worldIn.setBlockMetadataWithNotify(x, y, z, l, 3);
        this.func_150138_a(worldIn, x, y, z, l);
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        this.func_150138_a(worldIn, x, y, z, meta | 1);
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
            }
        }
    }

    private void func_150138_a(World p_150138_1_, int p_150138_2_, int p_150138_3_, int p_150138_4_, int p_150138_5_)
    {
        int i1 = 0;

        while (i1 < 2)
        {
            int j1 = 1;

            while (true)
            {
                if (j1 < 42)
                {
                    int k1 = p_150138_2_ + Direction.offsetX[i1] * j1;
                    int l1 = p_150138_4_ + Direction.offsetZ[i1] * j1;
                    Block block = p_150138_1_.getBlock(k1, p_150138_3_, l1);

                    if (block == Blocks.tripwire_hook)
                    {
                        int i2 = p_150138_1_.getBlockMetadata(k1, p_150138_3_, l1) & 3;

                        if (i2 == Direction.rotateOpposite[i1])
                        {
                            Blocks.tripwire_hook.func_150136_a(p_150138_1_, k1, p_150138_3_, l1, false, p_150138_1_.getBlockMetadata(k1, p_150138_3_, l1), true, j1, p_150138_5_);
                        }
                    }
                    else if (block == Blocks.tripwire)
                    {
                        ++j1;
                        continue;
                    }
                }

                ++i1;
                break;
            }
        }
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            if ((worldIn.getBlockMetadata(x, y, z) & 1) != 1)
            {
                this.func_150140_e(worldIn, x, y, z);
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!worldIn.isRemote)
        {
            if ((worldIn.getBlockMetadata(x, y, z) & 1) == 1)
            {
                this.func_150140_e(worldIn, x, y, z);
            }
        }
    }

    private void func_150140_e(World p_150140_1_, int p_150140_2_, int p_150140_3_, int p_150140_4_)
    {
        int l = p_150140_1_.getBlockMetadata(p_150140_2_, p_150140_3_, p_150140_4_);
        boolean flag = (l & 1) == 1;
        boolean flag1 = false;
        List list = p_150140_1_.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getBoundingBox((double)p_150140_2_ + this.minX, (double)p_150140_3_ + this.minY, (double)p_150140_4_ + this.minZ, (double)p_150140_2_ + this.maxX, (double)p_150140_3_ + this.maxY, (double)p_150140_4_ + this.maxZ));

        if (!list.isEmpty())
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                Entity entity = (Entity)iterator.next();

                if (!entity.doesEntityNotTriggerPressurePlate())
                {
                    flag1 = true;
                    break;
                }
            }
        }

        if (flag1 && !flag)
        {
            l |= 1;
        }

        if (!flag1 && flag)
        {
            l &= -2;
        }

        if (flag1 != flag)
        {
            p_150140_1_.setBlockMetadataWithNotify(p_150140_2_, p_150140_3_, p_150140_4_, l, 3);
            this.func_150138_a(p_150140_1_, p_150140_2_, p_150140_3_, p_150140_4_, l);
        }

        if (flag1)
        {
            p_150140_1_.scheduleBlockUpdate(p_150140_2_, p_150140_3_, p_150140_4_, this, this.tickRate(p_150140_1_));
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_150139_a(IBlockAccess p_150139_0_, int p_150139_1_, int p_150139_2_, int p_150139_3_, int p_150139_4_, int p_150139_5_)
    {
        int j1 = p_150139_1_ + Direction.offsetX[p_150139_5_];
        int k1 = p_150139_3_ + Direction.offsetZ[p_150139_5_];
        Block block = p_150139_0_.getBlock(j1, p_150139_2_, k1);
        boolean flag = (p_150139_4_ & 2) == 2;
        int l1;

        if (block == Blocks.tripwire_hook)
        {
            l1 = p_150139_0_.getBlockMetadata(j1, p_150139_2_, k1);
            int i2 = l1 & 3;
            return i2 == Direction.rotateOpposite[p_150139_5_];
        }
        else if (block == Blocks.tripwire)
        {
            l1 = p_150139_0_.getBlockMetadata(j1, p_150139_2_, k1);
            boolean flag1 = (l1 & 2) == 2;
            return flag == flag1;
        }
        else
        {
            return false;
        }
    }
}