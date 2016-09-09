package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntity implements IInventory
{
    private ItemStack[] field_146022_i = new ItemStack[9];
    private Random field_146021_j = new Random();
    protected String field_146020_a;
    private static final String __OBFID = "CL_00000352";

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 9;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int slotIn)
    {
        return this.field_146022_i[slotIn];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.field_146022_i[index] != null)
        {
            ItemStack itemstack;

            if (this.field_146022_i[index].stackSize <= count)
            {
                itemstack = this.field_146022_i[index];
                this.field_146022_i[index] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.field_146022_i[index].splitStack(count);

                if (this.field_146022_i[index].stackSize == 0)
                {
                    this.field_146022_i[index] = null;
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
        if (this.field_146022_i[index] != null)
        {
            ItemStack itemstack = this.field_146022_i[index];
            this.field_146022_i[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public int func_146017_i()
    {
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.field_146022_i.length; ++k)
        {
            if (this.field_146022_i[k] != null && this.field_146021_j.nextInt(j++) == 0)
            {
                i = k;
            }
        }

        return i;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.field_146022_i[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public int func_146019_a(ItemStack p_146019_1_)
    {
        for (int i = 0; i < this.field_146022_i.length; ++i)
        {
            if (this.field_146022_i[i] == null || this.field_146022_i[i].getItem() == null)
            {
                this.setInventorySlotContents(i, p_146019_1_);
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return this.isCustomInventoryName() ? this.field_146020_a : "container.dispenser";
    }

    public void func_146018_a(String p_146018_1_)
    {
        this.field_146020_a = p_146018_1_;
    }

    /**
     * Returns if the inventory is named
     */
    public boolean isCustomInventoryName()
    {
        return this.field_146020_a != null;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.field_146022_i = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.field_146022_i.length)
            {
                this.field_146022_i[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        if (compound.hasKey("CustomName", 8))
        {
            this.field_146020_a = compound.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_146022_i.length; ++i)
        {
            if (this.field_146022_i[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.field_146022_i[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.isCustomInventoryName())
        {
            compound.setString("CustomName", this.field_146020_a);
        }
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
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
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