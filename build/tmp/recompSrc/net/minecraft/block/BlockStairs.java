package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block
{
    private static final int[][] field_150150_a = new int[][] {{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
    private final Block field_150149_b;
    private final int field_150151_M;
    private boolean field_150152_N;
    private int field_150153_O;
    private static final String __OBFID = "CL_00000314";

    protected BlockStairs(Block p_i45428_1_, int p_i45428_2_)
    {
        super(p_i45428_1_.blockMaterial);
        this.field_150149_b = p_i45428_1_;
        this.field_150151_M = p_i45428_2_;
        this.setHardness(p_i45428_1_.blockHardness);
        this.setResistance(p_i45428_1_.blockResistance / 3.0F);
        this.setStepSound(p_i45428_1_.stepSound);
        this.setLightOpacity(255);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        if (this.field_150152_N)
        {
            this.setBlockBounds(0.5F * (float)(this.field_150153_O % 2), 0.5F * (float)(this.field_150153_O / 2 % 2), 0.5F * (float)(this.field_150153_O / 4 % 2), 0.5F + 0.5F * (float)(this.field_150153_O % 2), 0.5F + 0.5F * (float)(this.field_150153_O / 2 % 2), 0.5F + 0.5F * (float)(this.field_150153_O / 4 % 2));
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
        return 10;
    }

    /**
     * Sets the bounding box for the base of the stairs
     */
    public void setBaseBounds(IBlockAccess p_150147_1_, int p_150147_2_, int p_150147_3_, int p_150147_4_)
    {
        int l = p_150147_1_.getBlockMetadata(p_150147_2_, p_150147_3_, p_150147_4_);

        if ((l & 4) != 0)
        {
            this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Checks if a block is stairs
     */
    public static boolean isBlockStairs(Block p_150148_0_)
    {
        return p_150148_0_ instanceof BlockStairs;
    }

    private boolean func_150146_f(IBlockAccess p_150146_1_, int p_150146_2_, int p_150146_3_, int p_150146_4_, int p_150146_5_)
    {
        Block block = p_150146_1_.getBlock(p_150146_2_, p_150146_3_, p_150146_4_);
        /**
         * Checks if a block is stairs
         */
        return isBlockStairs(block) && p_150146_1_.getBlockMetadata(p_150146_2_, p_150146_3_, p_150146_4_) == p_150146_5_;
    }

    public boolean func_150145_f(IBlockAccess p_150145_1_, int p_150145_2_, int p_150145_3_, int p_150145_4_)
    {
        int l = p_150145_1_.getBlockMetadata(p_150145_2_, p_150145_3_, p_150145_4_);
        int i1 = l & 3;
        float f = 0.5F;
        float f1 = 1.0F;

        if ((l & 4) != 0)
        {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag = true;
        Block block;
        int j1;
        int k1;

        if (i1 == 0)
        {
            f2 = 0.5F;
            f5 = 1.0F;
            block = p_150145_1_.getBlock(p_150145_2_ + 1, p_150145_3_, p_150145_4_);
            j1 = p_150145_1_.getBlockMetadata(p_150145_2_ + 1, p_150145_3_, p_150145_4_);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                k1 = j1 & 3;

                if (k1 == 3 && !this.func_150146_f(p_150145_1_, p_150145_2_, p_150145_3_, p_150145_4_ + 1, l))
                {
                    f5 = 0.5F;
                    flag = false;
                }
                else if (k1 == 2 && !this.func_150146_f(p_150145_1_, p_150145_2_, p_150145_3_, p_150145_4_ - 1, l))
                {
                    f4 = 0.5F;
                    flag = false;
                }
            }
        }
        else if (i1 == 1)
        {
            f3 = 0.5F;
            f5 = 1.0F;
            block = p_150145_1_.getBlock(p_150145_2_ - 1, p_150145_3_, p_150145_4_);
            j1 = p_150145_1_.getBlockMetadata(p_150145_2_ - 1, p_150145_3_, p_150145_4_);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                k1 = j1 & 3;

                if (k1 == 3 && !this.func_150146_f(p_150145_1_, p_150145_2_, p_150145_3_, p_150145_4_ + 1, l))
                {
                    f5 = 0.5F;
                    flag = false;
                }
                else if (k1 == 2 && !this.func_150146_f(p_150145_1_, p_150145_2_, p_150145_3_, p_150145_4_ - 1, l))
                {
                    f4 = 0.5F;
                    flag = false;
                }
            }
        }
        else if (i1 == 2)
        {
            f4 = 0.5F;
            f5 = 1.0F;
            block = p_150145_1_.getBlock(p_150145_2_, p_150145_3_, p_150145_4_ + 1);
            j1 = p_150145_1_.getBlockMetadata(p_150145_2_, p_150145_3_, p_150145_4_ + 1);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                k1 = j1 & 3;

                if (k1 == 1 && !this.func_150146_f(p_150145_1_, p_150145_2_ + 1, p_150145_3_, p_150145_4_, l))
                {
                    f3 = 0.5F;
                    flag = false;
                }
                else if (k1 == 0 && !this.func_150146_f(p_150145_1_, p_150145_2_ - 1, p_150145_3_, p_150145_4_, l))
                {
                    f2 = 0.5F;
                    flag = false;
                }
            }
        }
        else if (i1 == 3)
        {
            block = p_150145_1_.getBlock(p_150145_2_, p_150145_3_, p_150145_4_ - 1);
            j1 = p_150145_1_.getBlockMetadata(p_150145_2_, p_150145_3_, p_150145_4_ - 1);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                k1 = j1 & 3;

                if (k1 == 1 && !this.func_150146_f(p_150145_1_, p_150145_2_ + 1, p_150145_3_, p_150145_4_, l))
                {
                    f3 = 0.5F;
                    flag = false;
                }
                else if (k1 == 0 && !this.func_150146_f(p_150145_1_, p_150145_2_ - 1, p_150145_3_, p_150145_4_, l))
                {
                    f2 = 0.5F;
                    flag = false;
                }
            }
        }

        this.setBlockBounds(f2, f, f4, f3, f1, f5);
        return flag;
    }

    public boolean func_150144_g(IBlockAccess p_150144_1_, int p_150144_2_, int p_150144_3_, int p_150144_4_)
    {
        int l = p_150144_1_.getBlockMetadata(p_150144_2_, p_150144_3_, p_150144_4_);
        int i1 = l & 3;
        float f = 0.5F;
        float f1 = 1.0F;

        if ((l & 4) != 0)
        {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.5F;
        float f5 = 1.0F;
        boolean flag = false;
        Block block;
        int j1;
        int k1;

        if (i1 == 0)
        {
            block = p_150144_1_.getBlock(p_150144_2_ - 1, p_150144_3_, p_150144_4_);
            j1 = p_150144_1_.getBlockMetadata(p_150144_2_ - 1, p_150144_3_, p_150144_4_);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                k1 = j1 & 3;

                if (k1 == 3 && !this.func_150146_f(p_150144_1_, p_150144_2_, p_150144_3_, p_150144_4_ - 1, l))
                {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag = true;
                }
                else if (k1 == 2 && !this.func_150146_f(p_150144_1_, p_150144_2_, p_150144_3_, p_150144_4_ + 1, l))
                {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag = true;
                }
            }
        }
        else if (i1 == 1)
        {
            block = p_150144_1_.getBlock(p_150144_2_ + 1, p_150144_3_, p_150144_4_);
            j1 = p_150144_1_.getBlockMetadata(p_150144_2_ + 1, p_150144_3_, p_150144_4_);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                f2 = 0.5F;
                f3 = 1.0F;
                k1 = j1 & 3;

                if (k1 == 3 && !this.func_150146_f(p_150144_1_, p_150144_2_, p_150144_3_, p_150144_4_ - 1, l))
                {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag = true;
                }
                else if (k1 == 2 && !this.func_150146_f(p_150144_1_, p_150144_2_, p_150144_3_, p_150144_4_ + 1, l))
                {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag = true;
                }
            }
        }
        else if (i1 == 2)
        {
            block = p_150144_1_.getBlock(p_150144_2_, p_150144_3_, p_150144_4_ - 1);
            j1 = p_150144_1_.getBlockMetadata(p_150144_2_, p_150144_3_, p_150144_4_ - 1);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                f4 = 0.0F;
                f5 = 0.5F;
                k1 = j1 & 3;

                if (k1 == 1 && !this.func_150146_f(p_150144_1_, p_150144_2_ - 1, p_150144_3_, p_150144_4_, l))
                {
                    flag = true;
                }
                else if (k1 == 0 && !this.func_150146_f(p_150144_1_, p_150144_2_ + 1, p_150144_3_, p_150144_4_, l))
                {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag = true;
                }
            }
        }
        else if (i1 == 3)
        {
            block = p_150144_1_.getBlock(p_150144_2_, p_150144_3_, p_150144_4_ + 1);
            j1 = p_150144_1_.getBlockMetadata(p_150144_2_, p_150144_3_, p_150144_4_ + 1);

            if (isBlockStairs(block) && (l & 4) == (j1 & 4))
            {
                k1 = j1 & 3;

                if (k1 == 1 && !this.func_150146_f(p_150144_1_, p_150144_2_ - 1, p_150144_3_, p_150144_4_, l))
                {
                    flag = true;
                }
                else if (k1 == 0 && !this.func_150146_f(p_150144_1_, p_150144_2_ + 1, p_150144_3_, p_150144_4_, l))
                {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag = true;
                }
            }
        }

        if (flag)
        {
            this.setBlockBounds(f2, f, f4, f3, f1, f5);
        }

        return flag;
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
        this.setBaseBounds(worldIn, x, y, z);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        boolean flag = this.func_150145_f(worldIn, x, y, z);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);

        if (flag && this.func_150144_g(worldIn, x, y, z))
        {
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player)
    {
        this.field_150149_b.onBlockClicked(worldIn, x, y, z, player);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        this.field_150149_b.randomDisplayTick(worldIn, x, y, z, random);
    }

    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta)
    {
        this.field_150149_b.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance(Entity exploder)
    {
        return this.field_150149_b.getExplosionResistance(exploder);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.field_150149_b.tickRate(worldIn);
    }

    public void modifyEntityVelocity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity)
    {
        this.field_150149_b.modifyEntityVelocity(worldIn, x, y, z, entityIn, velocity);
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z)
    {
        return this.field_150149_b.getMixedBrightnessForBlock(worldIn, x, y, z);
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return this.field_150149_b.getRenderBlockPass();
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.field_150149_b.getIcon(side, this.field_150151_M);
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return this.field_150149_b.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return this.field_150149_b.isCollidable();
    }

    /**
     * Returns whether the raytracing must ignore this block. Args : metadata, stopOnLiquid
     */
    public boolean canStopRayTrace(int meta, boolean includeLiquid)
    {
        return this.field_150149_b.canStopRayTrace(meta, includeLiquid);
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return this.field_150149_b.canPlaceBlockAt(worldIn, x, y, z);
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        this.onNeighborBlockChange(worldIn, x, y, z, Blocks.air);
        this.field_150149_b.onBlockAdded(worldIn, x, y, z);
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        this.field_150149_b.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public void onEntityWalking(World worldIn, int x, int y, int z, Entity entityIn)
    {
        this.field_150149_b.onEntityWalking(worldIn, x, y, z, entityIn);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        this.field_150149_b.updateTick(worldIn, x, y, z, random);
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        return this.field_150149_b.onBlockActivated(worldIn, x, y, z, player, 0, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Called upon the block being destroyed by an explosion
     */
    public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn)
    {
        this.field_150149_b.onBlockDestroyedByExplosion(worldIn, x, y, z, explosionIn);
    }

    public MapColor getMapColor(int meta)
    {
        return this.field_150149_b.getMapColor(this.field_150151_M);
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int i1 = worldIn.getBlockMetadata(x, y, z) & 4;

        if (l == 0)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, 2 | i1, 2);
        }

        if (l == 1)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, 1 | i1, 2);
        }

        if (l == 2)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, 3 | i1, 2);
        }

        if (l == 3)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, 0 | i1, 2);
        }
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        return side != 0 && (side == 1 || (double)subY <= 0.5D) ? meta : meta | 4;
    }

    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec)
    {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = l & 3;
        boolean flag = (l & 4) == 4;
        int[] aint = field_150150_a[i1 + (flag?4:0)];
        this.field_150152_N = true;
        int k1;
        int l1;
        int i2;

        for (int j1 = 0; j1 < 8; ++j1)
        {
            this.field_150153_O = j1;
            int[] aint1 = aint;
            k1 = aint.length;

            for (l1 = 0; l1 < k1; ++l1)
            {
                i2 = aint1[l1];

                if (i2 == j1)
                {
                    ;
                }
            }

            amovingobjectposition[j1] = super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
        }

        int[] aint2 = aint;
        int k2 = aint.length;

        for (k1 = 0; k1 < k2; ++k1)
        {
            l1 = aint2[k1];
            amovingobjectposition[l1] = null;
        }

        MovingObjectPosition movingobjectposition1 = null;
        double d1 = 0.0D;
        MovingObjectPosition[] amovingobjectposition1 = amovingobjectposition;
        i2 = amovingobjectposition.length;

        for (int j2 = 0; j2 < i2; ++j2)
        {
            MovingObjectPosition movingobjectposition = amovingobjectposition1[j2];

            if (movingobjectposition != null)
            {
                double d0 = movingobjectposition.hitVec.squareDistanceTo(endVec);

                if (d0 > d1)
                {
                    movingobjectposition1 = movingobjectposition;
                    d1 = d0;
                }
            }
        }

        return movingobjectposition1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}
}