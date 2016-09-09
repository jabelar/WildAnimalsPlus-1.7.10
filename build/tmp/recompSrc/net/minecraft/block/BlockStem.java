package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockStem extends BlockBush implements IGrowable
{
    private final Block field_149877_a;
    @SideOnly(Side.CLIENT)
    private IIcon field_149876_b;
    private static final String __OBFID = "CL_00000316";

    protected BlockStem(Block p_i45430_1_)
    {
        this.field_149877_a = p_i45430_1_;
        this.setTickRandomly(true);
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.farmland;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        super.updateTick(worldIn, x, y, z, random);

        if (worldIn.getBlockLightValue(x, y + 1, z) >= 9)
        {
            float f = this.func_149875_n(worldIn, x, y, z);

            if (random.nextInt((int)(25.0F / f) + 1) == 0)
            {
                int l = worldIn.getBlockMetadata(x, y, z);

                if (l < 7)
                {
                    ++l;
                    worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
                }
                else
                {
                    if (worldIn.getBlock(x - 1, y, z) == this.field_149877_a)
                    {
                        return;
                    }

                    if (worldIn.getBlock(x + 1, y, z) == this.field_149877_a)
                    {
                        return;
                    }

                    if (worldIn.getBlock(x, y, z - 1) == this.field_149877_a)
                    {
                        return;
                    }

                    if (worldIn.getBlock(x, y, z + 1) == this.field_149877_a)
                    {
                        return;
                    }

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

                    Block block = worldIn.getBlock(j1, y - 1, k1);

                    if (worldIn.isAirBlock(j1, y, k1) && (block.canSustainPlant(worldIn, j1, y - 1, k1, UP, this) || block == Blocks.dirt || block == Blocks.grass))
                    {
                        worldIn.setBlock(j1, y, k1, this.field_149877_a);
                    }
                }
            }
        }
    }

    public void fertilizeStem(World p_149874_1_, int p_149874_2_, int p_149874_3_, int p_149874_4_)
    {
        int l = p_149874_1_.getBlockMetadata(p_149874_2_, p_149874_3_, p_149874_4_) + MathHelper.getRandomIntegerInRange(p_149874_1_.rand, 2, 5);

        if (l > 7)
        {
            l = 7;
        }

        p_149874_1_.setBlockMetadataWithNotify(p_149874_2_, p_149874_3_, p_149874_4_, l, 2);
    }

    private float func_149875_n(World p_149875_1_, int p_149875_2_, int p_149875_3_, int p_149875_4_)
    {
        float f = 1.0F;
        Block block = p_149875_1_.getBlock(p_149875_2_, p_149875_3_, p_149875_4_ - 1);
        Block block1 = p_149875_1_.getBlock(p_149875_2_, p_149875_3_, p_149875_4_ + 1);
        Block block2 = p_149875_1_.getBlock(p_149875_2_ - 1, p_149875_3_, p_149875_4_);
        Block block3 = p_149875_1_.getBlock(p_149875_2_ + 1, p_149875_3_, p_149875_4_);
        Block block4 = p_149875_1_.getBlock(p_149875_2_ - 1, p_149875_3_, p_149875_4_ - 1);
        Block block5 = p_149875_1_.getBlock(p_149875_2_ + 1, p_149875_3_, p_149875_4_ - 1);
        Block block6 = p_149875_1_.getBlock(p_149875_2_ + 1, p_149875_3_, p_149875_4_ + 1);
        Block block7 = p_149875_1_.getBlock(p_149875_2_ - 1, p_149875_3_, p_149875_4_ + 1);
        boolean flag = block2 == this || block3 == this;
        boolean flag1 = block == this || block1 == this;
        boolean flag2 = block4 == this || block5 == this || block6 == this || block7 == this;

        for (int l = p_149875_2_ - 1; l <= p_149875_2_ + 1; ++l)
        {
            for (int i1 = p_149875_4_ - 1; i1 <= p_149875_4_ + 1; ++i1)
            {
                Block block8 = p_149875_1_.getBlock(l, p_149875_3_ - 1, i1);
                float f1 = 0.0F;

                if (block8.canSustainPlant(p_149875_1_, l, p_149875_3_ - 1, i1, UP, this))
                {
                    f1 = 1.0F;

                    if (block8.isFertile(p_149875_1_, l, p_149875_3_ - 1, i1))
                    {
                        f1 = 3.0F;
                    }
                }

                if (l != p_149875_2_ || i1 != p_149875_4_)
                {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        if (flag2 || flag && flag1)
        {
            f /= 2.0F;
        }

        return f;
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta)
    {
        int j = meta * 32;
        int k = 255 - meta * 8;
        int l = meta * 4;
        return j << 16 | k << 8 | l;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z)
    {
        return this.getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.maxY = (double)((float)(worldIn.getBlockMetadata(x, y, z) * 2 + 2) / 16.0F);
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.maxY, 0.5F + f);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 19;
    }

    /**
     * Returns the current state of the stem. Returns -1 if the stem is not fully grown, or a value between 0 and 3
     * based on the direction the stem is facing.
     */
    @SideOnly(Side.CLIENT)
    public int getState(IBlockAccess p_149873_1_, int p_149873_2_, int p_149873_3_, int p_149873_4_)
    {
        int l = p_149873_1_.getBlockMetadata(p_149873_2_, p_149873_3_, p_149873_4_);
        return l < 7 ? -1 : (p_149873_1_.getBlock(p_149873_2_ - 1, p_149873_3_, p_149873_4_) == this.field_149877_a ? 0 : (p_149873_1_.getBlock(p_149873_2_ + 1, p_149873_3_, p_149873_4_) == this.field_149877_a ? 1 : (p_149873_1_.getBlock(p_149873_2_, p_149873_3_, p_149873_4_ - 1) == this.field_149877_a ? 2 : (p_149873_1_.getBlock(p_149873_2_, p_149873_3_, p_149873_4_ + 1) == this.field_149877_a ? 3 : -1))));
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    @SuppressWarnings("unused")
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);

        if (false && !worldIn.isRemote)
        {
            Item item = null;

            if (this.field_149877_a == Blocks.pumpkin)
            {
                item = Items.pumpkin_seeds;
            }

            if (this.field_149877_a == Blocks.melon_block)
            {
                item = Items.melon_seeds;
            }

            for (int j1 = 0; j1 < 3; ++j1)
            {
                if (worldIn.rand.nextInt(15) <= meta)
                {
                    this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(item));
                }
            }
        }
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
        return 1;
    }

    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient)
    {
        return worldIn.getBlockMetadata(x, y, z) != 7;
    }

    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z)
    {
        return true;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return this.field_149877_a == Blocks.pumpkin ? Items.pumpkin_seeds : (this.field_149877_a == Blocks.melon_block ? Items.melon_seeds : Item.getItemById(0));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_disconnected");
        this.field_149876_b = reg.registerIcon(this.getTextureName() + "_connected");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getStemIcon()
    {
        return this.field_149876_b;
    }

    public void fertilize(World worldIn, Random random, int x, int y, int z)
    {
        this.fertilizeStem(worldIn, x, y, z);
    }


    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        Item item = null;
        item = field_149877_a == Blocks.pumpkin ? Items.pumpkin_seeds : item;
        item = field_149877_a == Blocks.melon_block ? Items.melon_seeds : item;

        for (int i = 0; item != null && i < 3; i++)
        {
            if (world.rand.nextInt(15) <= meta)
                ret.add(new ItemStack(item));
        }

        return ret;
    }
}