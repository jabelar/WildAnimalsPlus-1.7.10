package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockGravel extends BlockFalling
{
    private static final String __OBFID = "CL_00000252";

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        if (fortune > 3)
        {
            fortune = 3;
        }

        return random.nextInt(10 - fortune * 3) == 0 ? Items.flint : Item.getItemFromBlock(this);
    }
}