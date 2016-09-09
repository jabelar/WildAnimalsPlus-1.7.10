package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneSlab extends BlockSlab
{
    public static final String[] field_150006_b = new String[] {"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
    @SideOnly(Side.CLIENT)
    private IIcon field_150007_M;
    private static final String __OBFID = "CL_00000320";

    public BlockStoneSlab(boolean p_i45431_1_)
    {
        super(p_i45431_1_, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int k = meta & 7;

        if (this.isFullBlock && (meta & 8) != 0)
        {
            side = 1;
        }

        return k == 0 ? (side != 1 && side != 0 ? this.field_150007_M : this.blockIcon) : (k == 1 ? Blocks.sandstone.getBlockTextureFromSide(side) : (k == 2 ? Blocks.planks.getBlockTextureFromSide(side) : (k == 3 ? Blocks.cobblestone.getBlockTextureFromSide(side) : (k == 4 ? Blocks.brick_block.getBlockTextureFromSide(side) : (k == 5 ? Blocks.stonebrick.getIcon(side, 0) : (k == 6 ? Blocks.nether_brick.getBlockTextureFromSide(1) : (k == 7 ? Blocks.quartz_block.getBlockTextureFromSide(side) : this.blockIcon)))))));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("stone_slab_top");
        this.field_150007_M = reg.registerIcon("stone_slab_side");
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int meta)
    {
        return new ItemStack(Item.getItemFromBlock(Blocks.stone_slab), 2, meta & 7);
    }

    /**
     * Returns the slab block name with the type associated with it
     */
    public String getFullSlabName(int p_150002_1_)
    {
        if (p_150002_1_ < 0 || p_150002_1_ >= field_150006_b.length)
        {
            p_150002_1_ = 0;
        }

        return super.getUnlocalizedName() + "." + field_150006_b[p_150002_1_];
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab))
        {
            for (int i = 0; i <= 7; ++i)
            {
                if (i != 2)
                {
                    list.add(new ItemStack(itemIn, 1, i));
                }
            }
        }
    }
}