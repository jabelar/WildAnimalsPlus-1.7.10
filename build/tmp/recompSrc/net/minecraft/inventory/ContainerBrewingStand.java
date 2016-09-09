package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class ContainerBrewingStand extends Container
{
    private TileEntityBrewingStand tileBrewingStand;
    /** Instance of Slot. */
    private final Slot theSlot;
    private int brewTime;
    private static final String __OBFID = "CL_00001737";

    public ContainerBrewingStand(InventoryPlayer p_i1805_1_, TileEntityBrewingStand p_i1805_2_)
    {
        this.tileBrewingStand = p_i1805_2_;
        this.addSlotToContainer(new ContainerBrewingStand.Potion(p_i1805_1_.player, p_i1805_2_, 0, 56, 46));
        this.addSlotToContainer(new ContainerBrewingStand.Potion(p_i1805_1_.player, p_i1805_2_, 1, 79, 53));
        this.addSlotToContainer(new ContainerBrewingStand.Potion(p_i1805_1_.player, p_i1805_2_, 2, 102, 46));
        this.theSlot = this.addSlotToContainer(new ContainerBrewingStand.Ingredient(p_i1805_2_, 3, 79, 17));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(p_i1805_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(p_i1805_1_, i, 8 + i * 18, 142));
        }
    }

    public void onCraftGuiOpened(ICrafting p_75132_1_)
    {
        super.onCraftGuiOpened(p_75132_1_);
        p_75132_1_.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.brewTime != this.tileBrewingStand.getBrewTime())
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
            }
        }

        this.brewTime = this.tileBrewingStand.getBrewTime();
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int p_75137_1_, int p_75137_2_)
    {
        if (p_75137_1_ == 0)
        {
            this.tileBrewingStand.setBrewTime(p_75137_2_);
        }
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tileBrewingStand.isUseableByPlayer(player);
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

            if ((index < 0 || index > 2) && index != 3)
            {
                if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 3, 4, false))
                    {
                        return null;
                    }
                }
                else if (ContainerBrewingStand.Potion.canHoldPotion(itemstack))
                {
                    if (!this.mergeItemStack(itemstack1, 0, 3, false))
                    {
                        return null;
                    }
                }
                else if (index >= 4 && index < 31)
                {
                    if (!this.mergeItemStack(itemstack1, 31, 40, false))
                    {
                        return null;
                    }
                }
                else if (index >= 31 && index < 40)
                {
                    if (!this.mergeItemStack(itemstack1, 4, 31, false))
                    {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(itemstack1, 4, 40, false))
                {
                    return null;
                }
            }
            else
            {
                if (!this.mergeItemStack(itemstack1, 4, 40, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    class Ingredient extends Slot
    {
        private static final String __OBFID = "CL_00001738";

        public Ingredient(IInventory p_i1803_2_, int p_i1803_3_, int p_i1803_4_, int p_i1803_5_)
        {
            super(p_i1803_2_, p_i1803_3_, p_i1803_4_, p_i1803_5_);
        }

        /**
         * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
         */
        public boolean isItemValid(ItemStack stack)
        {
            return stack != null ? stack.getItem().isPotionIngredient(stack) : false;
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit()
        {
            return 64;
        }
    }

    static class Potion extends Slot
        {
            /** The player that has this container open. */
            private EntityPlayer player;
            private static final String __OBFID = "CL_00001740";

            public Potion(EntityPlayer p_i1804_1_, IInventory p_i1804_2_, int p_i1804_3_, int p_i1804_4_, int p_i1804_5_)
            {
                super(p_i1804_2_, p_i1804_3_, p_i1804_4_, p_i1804_5_);
                this.player = p_i1804_1_;
            }

            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            public boolean isItemValid(ItemStack stack)
            {
                /**
                 * Returns true if this itemstack can be filled with a potion
                 */
                return canHoldPotion(stack);
            }

            /**
             * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
             * the case of armor slots)
             */
            public int getSlotStackLimit()
            {
                return 1;
            }

            public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
            {
                if (p_82870_2_.getItem() instanceof ItemPotion && p_82870_2_.getMetadata() > 0)
                {
                    this.player.addStat(AchievementList.potion, 1);
                }

                super.onPickupFromSlot(p_82870_1_, p_82870_2_);
            }

            /**
             * Returns true if this itemstack can be filled with a potion
             */
            public static boolean canHoldPotion(ItemStack p_75243_0_)
            {
                return p_75243_0_ != null && (p_75243_0_.getItem() instanceof ItemPotion || p_75243_0_.getItem() == Items.glass_bottle);
            }
        }
}