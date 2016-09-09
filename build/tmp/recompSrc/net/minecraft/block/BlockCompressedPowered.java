package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends BlockCompressed
{
    private static final String __OBFID = "CL_00000287";

    public BlockCompressedPowered(MapColor p_i45416_1_)
    {
        super(p_i45416_1_);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return 15;
    }
}