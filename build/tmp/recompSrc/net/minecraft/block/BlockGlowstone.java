package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockGlowstone extends Block
{
    private static final String __OBFID = "CL_00000250";

    public BlockGlowstone(Material p_i45409_1_)
    {
        super(p_i45409_1_);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(int maxBonus, Random random)
    {
        return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(maxBonus + 1), 1, 4);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 2 + random.nextInt(3);
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.glowstone_dust;
    }

    public MapColor getMapColor(int meta)
    {
        return MapColor.sandColor;
    }
}