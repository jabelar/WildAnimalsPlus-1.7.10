package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockColored extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150033_a;
    private static final String __OBFID = "CL_00000217";

    public BlockColored(Material p_i45398_1_)
    {
        super(p_i45398_1_);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.field_150033_a[meta % this.field_150033_a.length];
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return meta;
    }

    public static int func_150032_b(int p_150032_0_)
    {
        return func_150031_c(p_150032_0_);
    }

    public static int func_150031_c(int p_150031_0_)
    {
        return ~p_150031_0_ & 15;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 16; ++i)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_150033_a = new IIcon[16];

        for (int i = 0; i < this.field_150033_a.length; ++i)
        {
            this.field_150033_a[i] = reg.registerIcon(this.getTextureName() + "_" + ItemDye.dyeIcons[func_150031_c(i)]);
        }
    }

    public MapColor getMapColor(int meta)
    {
        return MapColor.getMapColorForBlockColored(meta);
    }
}