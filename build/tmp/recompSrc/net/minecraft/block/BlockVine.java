package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;

public class BlockVine extends Block implements IShearable
{
    private static final String __OBFID = "CL_00000330";

    public BlockVine()
    {
        super(Material.vine);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 20;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        float f = 0.0625F;
        int l = worldIn.getBlockMetadata(x, y, z);
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = l > 0;

        if ((l & 2) != 0)
        {
            f4 = Math.max(f4, 0.0625F);
            f1 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((l & 8) != 0)
        {
            f1 = Math.min(f1, 0.9375F);
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((l & 4) != 0)
        {
            f6 = Math.max(f6, 0.0625F);
            f3 = 0.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if ((l & 1) != 0)
        {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (!flag && this.func_150093_a(worldIn.getBlock(x, y + 1, z)))
        {
            f2 = Math.min(f2, 0.9375F);
            f5 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
        }

        this.setBlockBounds(f1, f2, f3, f4, f5, f6);
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
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        switch (side)
        {
            case 1:
                return this.func_150093_a(worldIn.getBlock(x, y + 1, z));
            case 2:
                return this.func_150093_a(worldIn.getBlock(x, y, z + 1));
            case 3:
                return this.func_150093_a(worldIn.getBlock(x, y, z - 1));
            case 4:
                return this.func_150093_a(worldIn.getBlock(x + 1, y, z));
            case 5:
                return this.func_150093_a(worldIn.getBlock(x - 1, y, z));
            default:
                return false;
        }
    }

    private boolean func_150093_a(Block p_150093_1_)
    {
        return p_150093_1_.renderAsNormalBlock() && p_150093_1_.blockMaterial.blocksMovement();
    }

    private boolean func_150094_e(World p_150094_1_, int p_150094_2_, int p_150094_3_, int p_150094_4_)
    {
        int l = p_150094_1_.getBlockMetadata(p_150094_2_, p_150094_3_, p_150094_4_);
        int i1 = l;

        if (l > 0)
        {
            for (int j1 = 0; j1 <= 3; ++j1)
            {
                int k1 = 1 << j1;

                if ((l & k1) != 0 && !this.func_150093_a(p_150094_1_.getBlock(p_150094_2_ + Direction.offsetX[j1], p_150094_3_, p_150094_4_ + Direction.offsetZ[j1])) && (p_150094_1_.getBlock(p_150094_2_, p_150094_3_ + 1, p_150094_4_) != this || (p_150094_1_.getBlockMetadata(p_150094_2_, p_150094_3_ + 1, p_150094_4_) & k1) == 0))
                {
                    i1 &= ~k1;
                }
            }
        }

        if (i1 == 0 && !this.func_150093_a(p_150094_1_.getBlock(p_150094_2_, p_150094_3_ + 1, p_150094_4_)))
        {
            return false;
        }
        else
        {
            if (i1 != l)
            {
                p_150094_1_.setBlockMetadataWithNotify(p_150094_2_, p_150094_3_, p_150094_4_, i1, 2);
            }

            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z)
    {
        return worldIn.getBiomeGenForCoords(x, z).getBiomeFoliageColor(x, y, z);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!worldIn.isRemote && !this.func_150094_e(worldIn, x, y, z))
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
        if (!worldIn.isRemote && worldIn.rand.nextInt(4) == 0)
        {
            byte b0 = 4;
            int l = 5;
            boolean flag = false;
            int i1;
            int j1;
            int k1;
            label134:

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
                                flag = true;
                                break label134;
                            }
                        }
                    }
                }
            }

            i1 = worldIn.getBlockMetadata(x, y, z);
            j1 = worldIn.rand.nextInt(6);
            k1 = Direction.facingToDirection[j1];
            int l1;

            if (j1 == 1 && y < 255 && worldIn.isAirBlock(x, y + 1, z))
            {
                if (flag)
                {
                    return;
                }

                int j2 = worldIn.rand.nextInt(16) & i1;

                if (j2 > 0)
                {
                    for (l1 = 0; l1 <= 3; ++l1)
                    {
                        if (!this.func_150093_a(worldIn.getBlock(x + Direction.offsetX[l1], y + 1, z + Direction.offsetZ[l1])))
                        {
                            j2 &= ~(1 << l1);
                        }
                    }

                    if (j2 > 0)
                    {
                        worldIn.setBlock(x, y + 1, z, this, j2, 2);
                    }
                }
            }
            else
            {
                Block block;
                int i2;

                if (j1 >= 2 && j1 <= 5 && (i1 & 1 << k1) == 0)
                {
                    if (flag)
                    {
                        return;
                    }

                    block = worldIn.getBlock(x + Direction.offsetX[k1], y, z + Direction.offsetZ[k1]);

                    if (block.blockMaterial == Material.air)
                    {
                        l1 = k1 + 1 & 3;
                        i2 = k1 + 3 & 3;

                        if ((i1 & 1 << l1) != 0 && this.func_150093_a(worldIn.getBlock(x + Direction.offsetX[k1] + Direction.offsetX[l1], y, z + Direction.offsetZ[k1] + Direction.offsetZ[l1])))
                        {
                            worldIn.setBlock(x + Direction.offsetX[k1], y, z + Direction.offsetZ[k1], this, 1 << l1, 2);
                        }
                        else if ((i1 & 1 << i2) != 0 && this.func_150093_a(worldIn.getBlock(x + Direction.offsetX[k1] + Direction.offsetX[i2], y, z + Direction.offsetZ[k1] + Direction.offsetZ[i2])))
                        {
                            worldIn.setBlock(x + Direction.offsetX[k1], y, z + Direction.offsetZ[k1], this, 1 << i2, 2);
                        }
                        else if ((i1 & 1 << l1) != 0 && worldIn.isAirBlock(x + Direction.offsetX[k1] + Direction.offsetX[l1], y, z + Direction.offsetZ[k1] + Direction.offsetZ[l1]) && this.func_150093_a(worldIn.getBlock(x + Direction.offsetX[l1], y, z + Direction.offsetZ[l1])))
                        {
                            worldIn.setBlock(x + Direction.offsetX[k1] + Direction.offsetX[l1], y, z + Direction.offsetZ[k1] + Direction.offsetZ[l1], this, 1 << (k1 + 2 & 3), 2);
                        }
                        else if ((i1 & 1 << i2) != 0 && worldIn.isAirBlock(x + Direction.offsetX[k1] + Direction.offsetX[i2], y, z + Direction.offsetZ[k1] + Direction.offsetZ[i2]) && this.func_150093_a(worldIn.getBlock(x + Direction.offsetX[i2], y, z + Direction.offsetZ[i2])))
                        {
                            worldIn.setBlock(x + Direction.offsetX[k1] + Direction.offsetX[i2], y, z + Direction.offsetZ[k1] + Direction.offsetZ[i2], this, 1 << (k1 + 2 & 3), 2);
                        }
                        else if (this.func_150093_a(worldIn.getBlock(x + Direction.offsetX[k1], y + 1, z + Direction.offsetZ[k1])))
                        {
                            worldIn.setBlock(x + Direction.offsetX[k1], y, z + Direction.offsetZ[k1], this, 0, 2);
                        }
                    }
                    else if (block.blockMaterial.isOpaque() && block.renderAsNormalBlock())
                    {
                        worldIn.setBlockMetadataWithNotify(x, y, z, i1 | 1 << k1, 2);
                    }
                }
                else if (y > 1)
                {
                    block = worldIn.getBlock(x, y - 1, z);

                    if (block.blockMaterial == Material.air)
                    {
                        l1 = worldIn.rand.nextInt(16) & i1;

                        if (l1 > 0)
                        {
                            worldIn.setBlock(x, y - 1, z, this, l1, 2);
                        }
                    }
                    else if (block == this)
                    {
                        l1 = worldIn.rand.nextInt(16) & i1;
                        i2 = worldIn.getBlockMetadata(x, y - 1, z);

                        if (i2 != (i2 | l1))
                        {
                            worldIn.setBlockMetadataWithNotify(x, y - 1, z, i2 | l1, 2);
                        }
                    }
                }
            }
        }
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        byte b0 = 0;

        switch (side)
        {
            case 2:
                b0 = 1;
                break;
            case 3:
                b0 = 4;
                break;
            case 4:
                b0 = 8;
                break;
            case 5:
                b0 = 2;
        }

        return b0 != 0 ? b0 : meta;
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

    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta)
    {
        {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z)
    {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1));
        return ret;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        return true;
    }
}