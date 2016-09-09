package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockClay extends Block
{
    private static final String __OBFID = "CL_00000215";

    public BlockClay()
    {
        super(Material.clay);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.clay_ball;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 4;
    }
}