package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container
{
    private IInventory field_111243_a;
    private EntityHorse theHorse;
    private static final String __OBFID = "CL_00001751";

    public ContainerHorseInventory(IInventory p_i1817_1_, final IInventory p_i1817_2_, final EntityHorse p_i1817_3_)
    {
        this.field_111243_a = p_i1817_2_;
        this.theHorse = p_i1817_3_;
        byte b0 = 3;
        p_i1817_2_.openChest();
        int i = (b0 - 4) * 18;
        this.addSlotToContainer(new Slot(p_i1817_2_, 0, 8, 18)
        {
            private static final String __OBFID = "CL_00001752";
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            public boolean isItemValid(ItemStack stack)
            {
                return super.isItemValid(stack) && stack.getItem() == Items.saddle && !this.getHasStack();
            }
        });
        this.addSlotToContainer(new Slot(p_i1817_2_, 1, 8, 36)
        {
            private static final String __OBFID = "CL_00001753";
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            public boolean isItemValid(ItemStack stack)
            {
                return super.isItemValid(stack) && p_i1817_3_.canWearArmor() && EntityHorse.func_146085_a(stack.getItem());
            }
            /**
             * Actualy only call when we want to render the white square effect over the slots. Return always True,
             * except for the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
             */
            @SideOnly(Side.CLIENT)
            public boolean canBeHovered()
            {
                return p_i1817_3_.canWearArmor();
            }
        });
        int j;
        int k;

        if (p_i1817_3_.isChested())
        {
            for (j = 0; j < b0; ++j)
            {
                for (k = 0; k < 5; ++k)
                {
                    this.addSlotToContainer(new Slot(p_i1817_2_, 2 + k + j * 5, 80 + k * 18, 18 + j * 18));
                }
            }
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(p_i1817_1_, k + j * 9 + 9, 8 + k * 18, 102 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(p_i1817_1_, j, 8 + j * 18, 160 + i));
        }
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return this.field_111243_a.isUseableByPlayer(player) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(player) < 8.0F;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.field_111243_a.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.field_111243_a.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, false))
                {
                    return null;
                }
            }
            else if (this.getSlot(0).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return null;
                }
            }
            else if (this.field_111243_a.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.field_111243_a.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer p_75134_1_)
    {
        super.onContainerClosed(p_75134_1_);
        this.field_111243_a.closeChest();
    }
}