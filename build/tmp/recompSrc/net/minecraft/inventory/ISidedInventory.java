package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

public interface ISidedInventory extends IInventory
{
    /**
     * param side
     */
    int[] getSlotsForFace(int p_94128_1_);

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_);

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_);
}