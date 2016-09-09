package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockSand extends BlockFalling
{
    public static final String[] field_149838_a = new String[] {"default", "red"};
    @SideOnly(Side.CLIENT)
    private static IIcon sandIcon;
    @SideOnly(Side.CLIENT)
    private static IIcon redSandIcon;
    private static final String __OBFID = "CL_00000303";

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return meta == 1 ? redSandIcon : sandIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        sandIcon = reg.registerIcon("sand");
        redSandIcon = reg.registerIcon("red_sand");
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
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    public MapColor getMapColor(int meta)
    {
        return meta == 1 ? MapColor.dirtColor : MapColor.sandColor;
    }
}