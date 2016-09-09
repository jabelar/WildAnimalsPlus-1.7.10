package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item
{
    private final ItemStack returnStack;
    private float maxDamagePercent;
    private boolean enchantable;
    private static final String __OBFID = "CL_00001664";

    public WeightedRandomFishable(ItemStack p_i45317_1_, int p_i45317_2_)
    {
        super(p_i45317_2_);
        this.returnStack = p_i45317_1_;
    }

    public ItemStack getItemStack(Random p_150708_1_)
    {
        ItemStack itemstack = this.returnStack.copy();

        if (this.maxDamagePercent > 0.0F)
        {
            int i = (int)(this.maxDamagePercent * (float)this.returnStack.getMaxDurability());
            int j = itemstack.getMaxDurability() - p_150708_1_.nextInt(p_150708_1_.nextInt(i) + 1);

            if (j > i)
            {
                j = i;
            }

            if (j < 1)
            {
                j = 1;
            }

            itemstack.setMetadata(j);
        }

        if (this.enchantable)
        {
            EnchantmentHelper.addRandomEnchantment(p_150708_1_, itemstack, 30);
        }

        return itemstack;
    }

    public WeightedRandomFishable setMaxDamagePercent(float p_150709_1_)
    {
        this.maxDamagePercent = p_150709_1_;
        return this;
    }

    public WeightedRandomFishable setEnchantable()
    {
        this.enchantable = true;
        return this;
    }
}