package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLiquid extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149806_a;
    private static final String __OBFID = "CL_00000265";

    protected BlockLiquid(Material p_i45413_1_)
    {
        super(p_i45413_1_);
        float f = 0.0F;
        float f1 = 0.0F;
        this.setBlockBounds(0.0F + f1, 0.0F + f, 0.0F + f1, 1.0F + f1, 1.0F + f, 1.0F + f1);
        this.setTickRandomly(true);
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Returns the percentage of the liquid block that is air, based on the given flow decay of the liquid
     */
    public static float getLiquidHeightPercent(int p_149801_0_)
    {
        if (p_149801_0_ >= 8)
        {
            p_149801_0_ = 0;
        }

        return (float)(p_149801_0_ + 1) / 9.0F;
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return 16777215;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z)
    {
        if (this.blockMaterial != Material.water)
        {
            return 16777215;
        }
        else
        {
            int l = 0;
            int i1 = 0;
            int j1 = 0;

            for (int k1 = -1; k1 <= 1; ++k1)
            {
                for (int l1 = -1; l1 <= 1; ++l1)
                {
                    int i2 = worldIn.getBiomeGenForCoords(x + l1, z + k1).getWaterColorMultiplier();
                    l += (i2 & 16711680) >> 16;
                    i1 += (i2 & 65280) >> 8;
                    j1 += i2 & 255;
                }
            }

            return (l / 9 & 255) << 16 | (i1 / 9 & 255) << 8 | j1 / 9 & 255;
        }
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side != 0 && side != 1 ? this.field_149806_a[1] : this.field_149806_a[0];
    }

    protected int func_149804_e(World p_149804_1_, int p_149804_2_, int p_149804_3_, int p_149804_4_)
    {
        return p_149804_1_.getBlock(p_149804_2_, p_149804_3_, p_149804_4_).getMaterial() == this.blockMaterial ? p_149804_1_.getBlockMetadata(p_149804_2_, p_149804_3_, p_149804_4_) : -1;
    }

    /**
     * Returns the flow decay but converts values indicating falling liquid (values >=8) to their effective source block
     * value of zero
     */
    protected int getEffectiveFlowDecay(IBlockAccess p_149798_1_, int p_149798_2_, int p_149798_3_, int p_149798_4_)
    {
        if (p_149798_1_.getBlock(p_149798_2_, p_149798_3_, p_149798_4_).getMaterial() != this.blockMaterial)
        {
            return -1;
        }
        else
        {
            int l = p_149798_1_.getBlockMetadata(p_149798_2_, p_149798_3_, p_149798_4_);

            if (l >= 8)
            {
                l = 0;
            }

            return l;
        }
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns whether the raytracing must ignore this block. Args : metadata, stopOnLiquid
     */
    public boolean canStopRayTrace(int meta, boolean includeLiquid)
    {
        return includeLiquid && meta == 0;
    }

    public boolean isBlockSolid(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        Material material = worldIn.getBlock(x, y, z).getMaterial();
        return material == this.blockMaterial ? false : (side == 1 ? true : (material == Material.ice ? false : super.isBlockSolid(worldIn, x, y, z, side)));
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        Material material = worldIn.getBlock(x, y, z).getMaterial();
        return material == this.blockMaterial ? false : (side == 1 ? true : super.shouldSideBeRendered(worldIn, x, y, z, side));
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 4;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Returns a vector indicating the direction and intensity of liquid flow
     */
    private Vec3 getFlowVector(IBlockAccess p_149800_1_, int p_149800_2_, int p_149800_3_, int p_149800_4_)
    {
        Vec3 vec3 = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        int l = this.getEffectiveFlowDecay(p_149800_1_, p_149800_2_, p_149800_3_, p_149800_4_);

        for (int i1 = 0; i1 < 4; ++i1)
        {
            int j1 = p_149800_2_;
            int k1 = p_149800_4_;

            if (i1 == 0)
            {
                j1 = p_149800_2_ - 1;
            }

            if (i1 == 1)
            {
                k1 = p_149800_4_ - 1;
            }

            if (i1 == 2)
            {
                ++j1;
            }

            if (i1 == 3)
            {
                ++k1;
            }

            int l1 = this.getEffectiveFlowDecay(p_149800_1_, j1, p_149800_3_, k1);
            int i2;

            if (l1 < 0)
            {
                if (!p_149800_1_.getBlock(j1, p_149800_3_, k1).getMaterial().blocksMovement())
                {
                    l1 = this.getEffectiveFlowDecay(p_149800_1_, j1, p_149800_3_ - 1, k1);

                    if (l1 >= 0)
                    {
                        i2 = l1 - (l - 8);
                        vec3 = vec3.addVector((double)((j1 - p_149800_2_) * i2), (double)((p_149800_3_ - p_149800_3_) * i2), (double)((k1 - p_149800_4_) * i2));
                    }
                }
            }
            else if (l1 >= 0)
            {
                i2 = l1 - l;
                vec3 = vec3.addVector((double)((j1 - p_149800_2_) * i2), (double)((p_149800_3_ - p_149800_3_) * i2), (double)((k1 - p_149800_4_) * i2));
            }
        }

        if (p_149800_1_.getBlockMetadata(p_149800_2_, p_149800_3_, p_149800_4_) >= 8)
        {
            boolean flag = false;

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_, p_149800_3_, p_149800_4_ - 1, 2))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_, p_149800_3_, p_149800_4_ + 1, 3))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_ - 1, p_149800_3_, p_149800_4_, 4))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_ + 1, p_149800_3_, p_149800_4_, 5))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_, p_149800_3_ + 1, p_149800_4_ - 1, 2))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_, p_149800_3_ + 1, p_149800_4_ + 1, 3))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_ - 1, p_149800_3_ + 1, p_149800_4_, 4))
            {
                flag = true;
            }

            if (flag || this.isBlockSolid(p_149800_1_, p_149800_2_ + 1, p_149800_3_ + 1, p_149800_4_, 5))
            {
                flag = true;
            }

            if (flag)
            {
                vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
            }
        }

        vec3 = vec3.normalize();
        return vec3;
    }

    public void modifyEntityVelocity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity)
    {
        Vec3 vec31 = this.getFlowVector(worldIn, x, y, z);
        velocity.xCoord += vec31.xCoord;
        velocity.yCoord += vec31.yCoord;
        velocity.zCoord += vec31.zCoord;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.hasNoSky ? 10 : 30) : 0);
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int i1 = worldIn.getLightBrightnessForSkyBlocks(x, y + 1, z, 0);
        int j1 = l & 255;
        int k1 = i1 & 255;
        int l1 = l >> 16 & 255;
        int i2 = i1 >> 16 & 255;
        return (j1 > k1 ? j1 : k1) | (l1 > i2 ? l1 : i2) << 16;
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return this.blockMaterial == Material.water ? 1 : 0;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        int l;

        if (this.blockMaterial == Material.water)
        {
            if (random.nextInt(10) == 0)
            {
                l = worldIn.getBlockMetadata(x, y, z);

                if (l <= 0 || l >= 8)
                {
                    worldIn.spawnParticle("suspended", (double)((float)x + random.nextFloat()), (double)((float)y + random.nextFloat()), (double)((float)z + random.nextFloat()), 0.0D, 0.0D, 0.0D);
                }
            }

            for (l = 0; l < 0; ++l)
            {
                int i1 = random.nextInt(4);
                int j1 = x;
                int k1 = z;

                if (i1 == 0)
                {
                    j1 = x - 1;
                }

                if (i1 == 1)
                {
                    ++j1;
                }

                if (i1 == 2)
                {
                    k1 = z - 1;
                }

                if (i1 == 3)
                {
                    ++k1;
                }

                if (worldIn.getBlock(j1, y, k1).getMaterial() == Material.air && (worldIn.getBlock(j1, y - 1, k1).getMaterial().blocksMovement() || worldIn.getBlock(j1, y - 1, k1).getMaterial().isLiquid()))
                {
                    float f = 0.0625F;
                    double d0 = (double)((float)x + random.nextFloat());
                    double d1 = (double)((float)y + random.nextFloat());
                    double d2 = (double)((float)z + random.nextFloat());

                    if (i1 == 0)
                    {
                        d0 = (double)((float)x - f);
                    }

                    if (i1 == 1)
                    {
                        d0 = (double)((float)(x + 1) + f);
                    }

                    if (i1 == 2)
                    {
                        d2 = (double)((float)z - f);
                    }

                    if (i1 == 3)
                    {
                        d2 = (double)((float)(z + 1) + f);
                    }

                    double d3 = 0.0D;
                    double d4 = 0.0D;

                    if (i1 == 0)
                    {
                        d3 = (double)(-f);
                    }

                    if (i1 == 1)
                    {
                        d3 = (double)f;
                    }

                    if (i1 == 2)
                    {
                        d4 = (double)(-f);
                    }

                    if (i1 == 3)
                    {
                        d4 = (double)f;
                    }

                    worldIn.spawnParticle("splash", d0, d1, d2, d3, 0.0D, d4);
                }
            }
        }

        if (this.blockMaterial == Material.water && random.nextInt(64) == 0)
        {
            l = worldIn.getBlockMetadata(x, y, z);

            if (l > 0 && l < 8)
            {
                worldIn.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "liquid.water", random.nextFloat() * 0.25F + 0.75F, random.nextFloat() * 1.0F + 0.5F, false);
            }
        }

        double d5;
        double d6;
        double d7;

        if (this.blockMaterial == Material.lava && worldIn.getBlock(x, y + 1, z).getMaterial() == Material.air && !worldIn.getBlock(x, y + 1, z).isOpaqueCube())
        {
            if (random.nextInt(100) == 0)
            {
                d5 = (double)((float)x + random.nextFloat());
                d6 = (double)y + this.maxY;
                d7 = (double)((float)z + random.nextFloat());
                worldIn.spawnParticle("lava", d5, d6, d7, 0.0D, 0.0D, 0.0D);
                worldIn.playSound(d5, d6, d7, "liquid.lavapop", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0)
            {
                worldIn.playSound((double)x, (double)y, (double)z, "liquid.lava", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

        if (random.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !worldIn.getBlock(x, y - 2, z).getMaterial().blocksMovement())
        {
            d5 = (double)((float)x + random.nextFloat());
            d6 = (double)y - 1.05D;
            d7 = (double)((float)z + random.nextFloat());

            if (this.blockMaterial == Material.water)
            {
                worldIn.spawnParticle("dripWater", d5, d6, d7, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                worldIn.spawnParticle("dripLava", d5, d6, d7, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        this.func_149805_n(worldIn, x, y, z);
    }

    /**
     * the sin and cos of this number determine the surface gradient of the flowing block.
     */
    @SideOnly(Side.CLIENT)
    public static double getFlowDirection(IBlockAccess p_149802_0_, int p_149802_1_, int p_149802_2_, int p_149802_3_, Material p_149802_4_)
    {
        Vec3 vec3 = null;

        if (p_149802_4_ == Material.water)
        {
            vec3 = Blocks.flowing_water.getFlowVector(p_149802_0_, p_149802_1_, p_149802_2_, p_149802_3_);
        }

        if (p_149802_4_ == Material.lava)
        {
            vec3 = Blocks.flowing_lava.getFlowVector(p_149802_0_, p_149802_1_, p_149802_2_, p_149802_3_);
        }

        return vec3.xCoord == 0.0D && vec3.zCoord == 0.0D ? -1000.0D : Math.atan2(vec3.zCoord, vec3.xCoord) - (Math.PI / 2D);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        this.func_149805_n(worldIn, x, y, z);
    }

    private void func_149805_n(World p_149805_1_, int p_149805_2_, int p_149805_3_, int p_149805_4_)
    {
        if (p_149805_1_.getBlock(p_149805_2_, p_149805_3_, p_149805_4_) == this)
        {
            if (this.blockMaterial == Material.lava)
            {
                boolean flag = false;

                if (flag || p_149805_1_.getBlock(p_149805_2_, p_149805_3_, p_149805_4_ - 1).getMaterial() == Material.water)
                {
                    flag = true;
                }

                if (flag || p_149805_1_.getBlock(p_149805_2_, p_149805_3_, p_149805_4_ + 1).getMaterial() == Material.water)
                {
                    flag = true;
                }

                if (flag || p_149805_1_.getBlock(p_149805_2_ - 1, p_149805_3_, p_149805_4_).getMaterial() == Material.water)
                {
                    flag = true;
                }

                if (flag || p_149805_1_.getBlock(p_149805_2_ + 1, p_149805_3_, p_149805_4_).getMaterial() == Material.water)
                {
                    flag = true;
                }

                if (flag || p_149805_1_.getBlock(p_149805_2_, p_149805_3_ + 1, p_149805_4_).getMaterial() == Material.water)
                {
                    flag = true;
                }

                if (flag)
                {
                    int l = p_149805_1_.getBlockMetadata(p_149805_2_, p_149805_3_, p_149805_4_);

                    if (l == 0)
                    {
                        p_149805_1_.setBlock(p_149805_2_, p_149805_3_, p_149805_4_, Blocks.obsidian);
                    }
                    else if (l <= 4)
                    {
                        p_149805_1_.setBlock(p_149805_2_, p_149805_3_, p_149805_4_, Blocks.cobblestone);
                    }

                    this.func_149799_m(p_149805_1_, p_149805_2_, p_149805_3_, p_149805_4_);
                }
            }
        }
    }

    protected void func_149799_m(World p_149799_1_, int p_149799_2_, int p_149799_3_, int p_149799_4_)
    {
        p_149799_1_.playSoundEffect((double)((float)p_149799_2_ + 0.5F), (double)((float)p_149799_3_ + 0.5F), (double)((float)p_149799_4_ + 0.5F), "random.fizz", 0.5F, 2.6F + (p_149799_1_.rand.nextFloat() - p_149799_1_.rand.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l)
        {
            p_149799_1_.spawnParticle("largesmoke", (double)p_149799_2_ + Math.random(), (double)p_149799_3_ + 1.2D, (double)p_149799_4_ + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        if (this.blockMaterial == Material.lava)
        {
            this.field_149806_a = new IIcon[] {reg.registerIcon("lava_still"), reg.registerIcon("lava_flow")};
        }
        else
        {
            this.field_149806_a = new IIcon[] {reg.registerIcon("water_still"), reg.registerIcon("water_flow")};
        }
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getLiquidIcon(String p_149803_0_)
    {
        return p_149803_0_ == "water_still" ? Blocks.flowing_water.field_149806_a[0] : (p_149803_0_ == "water_flow" ? Blocks.flowing_water.field_149806_a[1] : (p_149803_0_ == "lava_still" ? Blocks.flowing_lava.field_149806_a[0] : (p_149803_0_ == "lava_flow" ? Blocks.flowing_lava.field_149806_a[1] : null)));
    }
}