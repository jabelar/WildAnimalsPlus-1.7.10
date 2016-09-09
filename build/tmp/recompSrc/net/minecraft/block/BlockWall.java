package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block
{
    public static final String[] field_150092_a = new String[] {"normal", "mossy"};
    private static final String __OBFID = "CL_00000331";

    public BlockWall(Block p_i45435_1_)
    {
        super(p_i45435_1_.blockMaterial);
        this.setHardness(p_i45435_1_.blockHardness);
        this.setResistance(p_i45435_1_.blockResistance / 3.0F);
        this.setStepSound(p_i45435_1_.stepSound);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return meta == 1 ? Blocks.mossy_cobblestone.getBlockTextureFromSide(side) : Blocks.cobblestone.getBlockTextureFromSide(side);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 32;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        boolean flag = this.canConnectWallTo(worldIn, x, y, z - 1);
        boolean flag1 = this.canConnectWallTo(worldIn, x, y, z + 1);
        boolean flag2 = this.canConnectWallTo(worldIn, x - 1, y, z);
        boolean flag3 = this.canConnectWallTo(worldIn, x + 1, y, z);
        float f = 0.25F;
        float f1 = 0.75F;
        float f2 = 0.25F;
        float f3 = 0.75F;
        float f4 = 1.0F;

        if (flag)
        {
            f2 = 0.0F;
        }

        if (flag1)
        {
            f3 = 1.0F;
        }

        if (flag2)
        {
            f = 0.0F;
        }

        if (flag3)
        {
            f1 = 1.0F;
        }

        if (flag && flag1 && !flag2 && !flag3)
        {
            f4 = 0.8125F;
            f = 0.3125F;
            f1 = 0.6875F;
        }
        else if (!flag && !flag1 && flag2 && flag3)
        {
            f4 = 0.8125F;
            f2 = 0.3125F;
            f3 = 0.6875F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, f4, f3);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        this.maxY = 1.5D;
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    /**
     * Return whether an adjacent block can connect to a wall.
     */
    public boolean canConnectWallTo(IBlockAccess p_150091_1_, int p_150091_2_, int p_150091_3_, int p_150091_4_)
    {
        Block block = p_150091_1_.getBlock(p_150091_2_, p_150091_3_, p_150091_4_);
        return block != this && block != Blocks.fence_gate ? (block.blockMaterial.isOpaque() && block.renderAsNormalBlock() ? block.blockMaterial != Material.gourd : false) : true;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return meta;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return side == 0 ? super.shouldSideBeRendered(worldIn, x, y, z, side) : true;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}
}