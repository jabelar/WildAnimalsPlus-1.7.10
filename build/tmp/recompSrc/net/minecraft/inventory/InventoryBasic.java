package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBasic implements IInventory
{
    private String inventoryTitle;
    private int slotsCount;
    private ItemStack[] inventoryContents;
    private List field_70480_d;
    private boolean hasCustomName;
    private static final String __OBFID = "CL_00001514";

    public InventoryBasic(String p_i1561_1_, boolean p_i1561_2_, int p_i1561_3_)
    {
        this.inventoryTitle = p_i1561_1_;
        this.hasCustomName = p_i1561_2_;
        this.slotsCount = p_i1561_3_;
        this.inventoryContents = new ItemStack[p_i1561_3_];
    }

    public void func_110134_a(IInvBasic p_110134_1_)
    {
        if (this.field_70480_d == null)
        {
            this.field_70480_d = new ArrayList();
        }

        this.field_70480_d.add(p_110134_1_);
    }

    public void func_110132_b(IInvBasic p_110132_1_)
    {
        this.field_70480_d.remove(p_110132_1_);
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int slotIn)
    {
        return slotIn >= 0 && slotIn < this.inventoryContents.length ? this.inventoryContents[slotIn] : null;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventoryContents[index] != null)
        {
            ItemStack itemstack;

            if (this.inventoryContents[index].stackSize <= count)
            {
                itemstack = this.inventoryContents[index];
                this.inventoryContents[index] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.inventoryContents[index].splitStack(count);

                if (this.inventoryContents[index].stackSize == 0)
                {
                    this.inventoryContents[index] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (this.inventoryContents[index] != null)
        {
            ItemStack itemstack = this.inventoryContents[index];
            this.inventoryContents[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventoryContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return this.inventoryTitle;
    }

    /**
     * Returns if the inventory is named
     */
    public boolean isCustomInventoryName()
    {
        return this.hasCustomName;
    }

    public void func_110133_a(String p_110133_1_)
    {
        this.hasCustomName = true;
        this.inventoryTitle = p_110133_1_;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.field_70480_d != null)
        {
            for (int i = 0; i < this.field_70480_d.size(); ++i)
            {
                ((IInvBasic)this.field_70480_d.get(i)).onInventoryChanged(this);
            }
        }
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }
}