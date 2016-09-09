package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block
{
    private String name;
    private static final String __OBFID = "CL_00000194";

    protected BlockBasePressurePlate(String name, Material materialIn)
    {
        super(materialIn);
        this.name = name;
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
        this.setBlockBoundsFromMeta(this.getMetaFromPower(15));
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.setBlockBoundsFromMeta(worldIn.getBlockMetadata(x, y, z));
    }

    protected void setBlockBoundsFromMeta(int meta)
    {
        boolean flag = this.getPowerFromMeta(meta) > 0;
        float f = 0.0625F;

        if (flag)
        {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
        }
        else
        {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 20;
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

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        return true;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || BlockFence.isFence(worldIn.getBlock(x, y - 1, z));
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        boolean flag = false;

        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !BlockFence.isFence(worldIn.getBlock(x, y - 1, z)))
        {
            flag = true;
        }

        if (flag)
        {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!worldIn.isRemote)
        {
            int l = this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));

            if (l > 0)
            {
                this.setStateIfMobInteractsWithPlate(worldIn, x, y, z, l);
            }
        }
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            int l = this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));

            if (l == 0)
            {
                this.setStateIfMobInteractsWithPlate(worldIn, x, y, z, l);
            }
        }
    }

    protected void setStateIfMobInteractsWithPlate(World worldIn, int x, int y, int z, int power)
    {
        int i1 = this.getPlateState(worldIn, x, y, z);
        boolean flag = power > 0;
        boolean flag1 = i1 > 0;

        if (power != i1)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, this.getMetaFromPower(i1), 2);
            this.updateNeighbors(worldIn, x, y, z);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        }

        if (!flag1 && flag)
        {
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.1D, (double)z + 0.5D, "random.click", 0.3F, 0.5F);
        }
        else if (flag1 && !flag)
        {
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.1D, (double)z + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (flag1)
        {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
        }
    }

    protected AxisAlignedBB getSensitiveAABB(int x, int y, int z)
    {
        float f = 0.125F;
        return AxisAlignedBB.getBoundingBox((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)y + 0.25D, (double)((float)(z + 1) - f));
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        if (this.getPowerFromMeta(meta) > 0)
        {
            this.updateNeighbors(worldIn, x, y, z);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    protected void updateNeighbors(World worldIn, int x, int y, int z)
    {
        worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
        worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return side == 1 ? this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z)) : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.5F;
        float f1 = 0.125F;
        float f2 = 0.5F;
        this.setBlockBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    public int getMobilityFlag()
    {
        return 1;
    }

    protected abstract int getPlateState(World p_150065_1_, int p_150065_2_, int p_150065_3_, int p_150065_4_);

    protected abstract int getPowerFromMeta(int p_150060_1_);

    protected abstract int getMetaFromPower(int p_150066_1_);

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.name);
    }
}