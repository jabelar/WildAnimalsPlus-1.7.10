package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconInner;
    private static final String __OBFID = "CL_00000211";

    protected BlockCake()
    {
        super(Material.cake);
        this.setTickRandomly(true);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f1 = (float)(1 + l * 2) / 16.0F;
        float f2 = 0.5F;
        this.setBlockBounds(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.0625F;
        float f1 = 0.5F;
        this.setBlockBounds(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f1 = (float)(1 + l * 2) / 16.0F;
        float f2 = 0.5F;
        return AxisAlignedBB.getBoundingBox((double)((float)x + f1), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)y + f2 - f), (double)((float)(z + 1) - f));
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f1 = (float)(1 + l * 2) / 16.0F;
        float f2 = 0.5F;
        return AxisAlignedBB.getBoundingBox((double)((float)x + f1), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)y + f2), (double)((float)(z + 1) - f));
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : (meta > 0 && side == 4 ? this.iconInner : this.blockIcon));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iconInner = reg.registerIcon(this.getTextureName() + "_inner");
        this.iconTop = reg.registerIcon(this.getTextureName() + "_top");
        this.iconBottom = reg.registerIcon(this.getTextureName() + "_bottom");
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
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        this.func_150036_b(worldIn, x, y, z, player);
        return true;
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player)
    {
        this.func_150036_b(worldIn, x, y, z, player);
    }

    private void func_150036_b(World p_150036_1_, int p_150036_2_, int p_150036_3_, int p_150036_4_, EntityPlayer p_150036_5_)
    {
        if (p_150036_5_.canEat(false))
        {
            p_150036_5_.getFoodStats().addStats(2, 0.1F);
            int l = p_150036_1_.getBlockMetadata(p_150036_2_, p_150036_3_, p_150036_4_) + 1;

            if (l >= 6)
            {
                p_150036_1_.setBlockToAir(p_150036_2_, p_150036_3_, p_150036_4_);
            }
            else
            {
                p_150036_1_.setBlockMetadataWithNotify(p_150036_2_, p_150036_3_, p_150036_4_, l, 2);
            }
        }
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return !super.canPlaceBlockAt(worldIn, x, y, z) ? false : this.canBlockStay(worldIn, x, y, z);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!this.canBlockStay(worldIn, x, y, z))
        {
            worldIn.setBlockToAir(x, y, z);
        }
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World worldIn, int x, int y, int z)
    {
        return worldIn.getBlock(x, y - 1, z).getMaterial().isSolid();
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return null;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.cake;
    }
}