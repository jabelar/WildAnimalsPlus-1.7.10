package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockPane extends Block
{
    private final String field_150100_a;
    private final boolean field_150099_b;
    private final String field_150101_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_150102_N;
    private static final String __OBFID = "CL_00000322";

    protected BlockPane(String p_i45432_1_, String p_i45432_2_, Material p_i45432_3_, boolean p_i45432_4_)
    {
        super(p_i45432_3_);
        this.field_150100_a = p_i45432_2_;
        this.field_150099_b = p_i45432_4_;
        this.field_150101_M = p_i45432_1_;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return !this.field_150099_b ? null : super.getItemDropped(meta, random, fortune);
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
        return this.blockMaterial == Material.glass ? 41 : 18;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return worldIn.getBlock(x, y, z) == this ? false : super.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
        boolean flag  = this.canPaneConnectTo(worldIn, x, y, z - 1, NORTH);
        boolean flag1 = this.canPaneConnectTo(worldIn, x, y, z + 1, SOUTH);
        boolean flag2 = this.canPaneConnectTo(worldIn, x - 1, y, z, WEST );
        boolean flag3 = this.canPaneConnectTo(worldIn, x + 1, y, z, EAST );

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            }
            else if (!flag2 && flag3)
            {
                this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            }
            else if (!flag && flag1)
            {
                this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            }
        }
        else
        {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag  = this.canPaneConnectTo(worldIn, x, y, z - 1, NORTH);
        boolean flag1 = this.canPaneConnectTo(worldIn, x, y, z + 1, SOUTH);
        boolean flag2 = this.canPaneConnectTo(worldIn, x - 1, y, z, WEST );
        boolean flag3 = this.canPaneConnectTo(worldIn, x + 1, y, z, EAST );

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                f = 0.0F;
            }
            else if (!flag2 && flag3)
            {
                f1 = 1.0F;
            }
        }
        else
        {
            f = 0.0F;
            f1 = 1.0F;
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                f2 = 0.0F;
            }
            else if (!flag && flag1)
            {
                f3 = 1.0F;
            }
        }
        else
        {
            f2 = 0.0F;
            f3 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public final boolean canPaneConnectToBlock(Block p_150098_1_)
    {
        return p_150098_1_.isFullBlock() || p_150098_1_ == this || p_150098_1_ == Blocks.glass || p_150098_1_ == Blocks.stained_glass || p_150098_1_ == Blocks.stained_glass_pane || p_150098_1_ instanceof BlockPane;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_150097_e()
    {
        return this.field_150102_N;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int meta)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, meta);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.field_150101_M);
        this.field_150102_N = reg.registerIcon(this.field_150100_a);
    }

    public boolean canPaneConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return canPaneConnectToBlock(world.getBlock(x, y, z)) || 
                world.isSideSolid(x, y, z, dir.getOpposite(), false);
    }
}