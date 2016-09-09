package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150055_b;
    private static final String __OBFID = "CL_00000225";

    public BlockRailDetector()
    {
        super(true);
        this.setTickRandomly(true);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 20;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            int l = worldIn.getBlockMetadata(x, y, z);

            if ((l & 8) == 0)
            {
                this.func_150054_a(worldIn, x, y, z, l);
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
            int l = worldIn.getBlockMetadata(x, y, z);

            if ((l & 8) != 0)
            {
                this.func_150054_a(worldIn, x, y, z, l);
            }
        }
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return (worldIn.getBlockMetadata(x, y, z) & 8) != 0 ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return (worldIn.getBlockMetadata(x, y, z) & 8) == 0 ? 0 : (side == 1 ? 15 : 0);
    }

    private void func_150054_a(World p_150054_1_, int p_150054_2_, int p_150054_3_, int p_150054_4_, int p_150054_5_)
    {
        boolean flag = (p_150054_5_ & 8) != 0;
        boolean flag1 = false;
        float f = 0.125F;
        List list = p_150054_1_.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox((double)((float)p_150054_2_ + f), (double)p_150054_3_, (double)((float)p_150054_4_ + f), (double)((float)(p_150054_2_ + 1) - f), (double)((float)(p_150054_3_ + 1) - f), (double)((float)(p_150054_4_ + 1) - f)));

        if (!list.isEmpty())
        {
            flag1 = true;
        }

        if (flag1 && !flag)
        {
            p_150054_1_.setBlockMetadataWithNotify(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_5_ | 8, 3);
            p_150054_1_.notifyBlocksOfNeighborChange(p_150054_2_, p_150054_3_, p_150054_4_, this);
            p_150054_1_.notifyBlocksOfNeighborChange(p_150054_2_, p_150054_3_ - 1, p_150054_4_, this);
            p_150054_1_.markBlockRangeForRenderUpdate(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_2_, p_150054_3_, p_150054_4_);
        }

        if (!flag1 && flag)
        {
            p_150054_1_.setBlockMetadataWithNotify(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_5_ & 7, 3);
            p_150054_1_.notifyBlocksOfNeighborChange(p_150054_2_, p_150054_3_, p_150054_4_, this);
            p_150054_1_.notifyBlocksOfNeighborChange(p_150054_2_, p_150054_3_ - 1, p_150054_4_, this);
            p_150054_1_.markBlockRangeForRenderUpdate(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_2_, p_150054_3_, p_150054_4_);
        }

        if (flag1)
        {
            p_150054_1_.scheduleBlockUpdate(p_150054_2_, p_150054_3_, p_150054_4_, this, this.tickRate(p_150054_1_));
        }

        p_150054_1_.updateNeighborsAboutBlockChange(p_150054_2_, p_150054_3_, p_150054_4_, this);
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        super.onBlockAdded(worldIn, x, y, z);
        this.func_150054_a(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z));
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side)
    {
        if ((worldIn.getBlockMetadata(x, y, z) & 8) > 0)
        {
            float f = 0.125F;
            List list = worldIn.getEntitiesWithinAABB(EntityMinecartCommandBlock.class, AxisAlignedBB.getBoundingBox((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)(y + 1) - f), (double)((float)(z + 1) - f)));

            if (list.size() > 0)
            {
                return ((EntityMinecartCommandBlock)list.get(0)).func_145822_e().getSuccessCount();
            }

            List list1 = worldIn.selectEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)(y + 1) - f), (double)((float)(z + 1) - f)), IEntitySelector.selectInventories);

            if (list1.size() > 0)
            {
                return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
            }
        }

        return 0;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_150055_b = new IIcon[2];
        this.field_150055_b[0] = reg.registerIcon(this.getTextureName());
        this.field_150055_b[1] = reg.registerIcon(this.getTextureName() + "_powered");
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return (meta & 8) != 0 ? this.field_150055_b[1] : this.field_150055_b[0];
    }
}