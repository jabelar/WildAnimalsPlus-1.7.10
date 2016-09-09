package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneBrick extends Block
{
    public static final String[] field_150142_a = new String[] {"default", "mossy", "cracked", "chiseled"};
    public static final String[] field_150141_b = new String[] {null, "mossy", "cracked", "carved"};
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150143_M;
    private static final String __OBFID = "CL_00000318";

    public BlockStoneBrick()
    {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta < 0 || meta >= field_150141_b.length)
        {
            meta = 0;
        }

        return this.field_150143_M[meta];
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return meta;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 4; ++i)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_150143_M = new IIcon[field_150141_b.length];

        for (int i = 0; i < this.field_150143_M.length; ++i)
        {
            String s = this.getTextureName();

            if (field_150141_b[i] != null)
            {
                s = s + "_" + field_150141_b[i];
            }

            this.field_150143_M[i] = reg.registerIcon(s);
        }
    }
}