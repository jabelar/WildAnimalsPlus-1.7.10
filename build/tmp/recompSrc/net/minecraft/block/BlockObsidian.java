package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class BlockObsidian extends BlockStone
{
    private static final String __OBFID = "CL_00000279";

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 1;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Item.getItemFromBlock(Blocks.obsidian);
    }

    public MapColor getMapColor(int meta)
    {
        return MapColor.obsidianColor;
    }
}