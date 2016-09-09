package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryLargeChest implements IInventory
{
    /** Name of the chest. */
    private String name;
    /** Inventory object corresponding to double chest upper part */
    private IInventory upperChest;
    /** Inventory object corresponding to double chest lower part */
    private IInventory lowerChest;
    private static final String __OBFID = "CL_00001507";

    public InventoryLargeChest(String p_i1559_1_, IInventory p_i1559_2_, IInventory p_i1559_3_)
    {
        this.name = p_i1559_1_;

        if (p_i1559_2_ == null)
        {
            p_i1559_2_ = p_i1559_3_;
        }

        if (p_i1559_3_ == null)
        {
            p_i1559_3_ = p_i1559_2_;
        }

        this.upperChest = p_i1559_2_;
        this.lowerChest = p_i1559_3_;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    /**
     * Return whether the given inventory is part of this large chest.
     */
    public boolean isPartOfLargeChest(IInventory p_90010_1_)
    {
        return this.upperChest == p_90010_1_ || this.lowerChest == p_90010_1_;
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return this.upperChest.isCustomInventoryName() ? this.upperChest.getInventoryName() : (this.lowerChest.isCustomInventoryName() ? this.lowerChest.getInventoryName() : this.name);
    }

    /**
     * Returns if the inventory is named
     */
    public boolean isCustomInventoryName()
    {
        return this.upperChest.isCustomInventoryName() || this.lowerChest.isCustomInventoryName();
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int slotIn)
    {
        return slotIn >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(slotIn - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(slotIn);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count) : this.upperChest.decrStackSize(index, count);
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlotOnClosing(index - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlotOnClosing(index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if (index >= this.upperChest.getSizeInventory())
        {
            this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
        }
        else
        {
            this.upperChest.setInventorySlotContents(index, stack);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return this.upperChest.getInventoryStackLimit();
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        this.upperChest.markDirty();
        this.lowerChest.markDirty();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.upperChest.isUseableByPlayer(player) && this.lowerChest.isUseableByPlayer(player);
    }

    public void openChest()
    {
        this.upperChest.openChest();
        this.lowerChest.openChest();
    }

    public void closeChest()
    {
        this.upperChest.closeChest();
        this.lowerChest.closeChest();
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }
}