package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149989_a;
    private static final String __OBFID = "CL_00000216";

    public BlockCocoa()
    {
        super(Material.plants);
        this.setTickRandomly(true);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.field_149989_a[2];
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!this.canBlockStay(worldIn, x, y, z))
        {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlock(x, y, z, getBlockById(0), 0, 2);
        }
        else if (worldIn.rand.nextInt(5) == 0)
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            int i1 = func_149987_c(l);

            if (i1 < 2)
            {
                ++i1;
                worldIn.setBlockMetadataWithNotify(x, y, z, i1 << 2 | getDirection(l), 2);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getCocoaIcon(int p_149988_1_)
    {
        if (p_149988_1_ < 0 || p_149988_1_ >= this.field_149989_a.length)
        {
            p_149988_1_ = this.field_149989_a.length - 1;
        }

        return this.field_149989_a[p_149988_1_];
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World worldIn, int x, int y, int z)
    {
        int l = getDirection(worldIn.getBlockMetadata(x, y, z));
        x += Direction.offsetX[l];
        z += Direction.offsetZ[l];
        Block block = worldIn.getBlock(x, y, z);
        return block == Blocks.log && BlockLog.func_150165_c(worldIn.getBlockMetadata(x, y, z)) == 3;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 28;
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
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = getDirection(l);
        int j1 = func_149987_c(l);
        int k1 = 4 + j1 * 2;
        int l1 = 5 + j1 * 2;
        float f = (float)k1 / 2.0F;

        switch (i1)
        {
            case 0:
                this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float)l1) / 16.0F, (15.0F - (float)k1) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                break;
            case 1:
                this.setBlockBounds(0.0625F, (12.0F - (float)l1) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float)k1) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                break;
            case 2:
                this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float)l1) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float)k1) / 16.0F);
                break;
            case 3:
                this.setBlockBounds((15.0F - (float)k1) / 16.0F, (12.0F - (float)l1) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = ((MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 0) % 4;
        worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        if (side == 1 || side == 0)
        {
            side = 2;
        }

        return Direction.rotateOpposite[Direction.facingToDirection[side]];
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!this.canBlockStay(worldIn, x, y, z))
        {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlock(x, y, z, getBlockById(0), 0, 2);
        }
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    public static int func_149987_c(int p_149987_0_)
    {
        return (p_149987_0_ & 12) >> 2;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune)
    {
        ArrayList<ItemStack> dropped = super.getDrops(world, x, y, z, meta, fortune);
        int j1 = func_149987_c(meta);
        byte b0 = 1;

        if (j1 >= 2)
        {
            b0 = 3;
        }

        for (int k1 = 0; k1 < b0; ++k1)
        {
            dropped.add(new ItemStack(Items.dye, 1, 3));
        }
        return dropped;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.dye;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        return 3;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149989_a = new IIcon[3];

        for (int i = 0; i < this.field_149989_a.length; ++i)
        {
            this.field_149989_a[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }

    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = func_149987_c(l);
        return i1 < 2;
    }

    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z)
    {
        return true;
    }

    public void fertilize(World worldIn, Random random, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = BlockDirectional.getDirection(l);
        int j1 = func_149987_c(l);
        ++j1;
        worldIn.setBlockMetadataWithNotify(x, y, z, j1 << 2 | i1, 2);
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return null;
    }
}