package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public abstract class Container
{
    /** the list of all items(stacks) for the corresponding slot */
    public List inventoryItemStacks = new ArrayList();
    /** the list of all slots in the inventory */
    public List inventorySlots = new ArrayList();
    public int windowId;
    @SideOnly(Side.CLIENT)
    private short transactionID;
    /** The current drag mode (0 : evenly split, 1 : one item by slot, 2 : not used ?) */
    private int dragMode = -1;
    /** The current drag event (0 : start, 1 : add slot : 2 : end) */
    private int dragEvent;
    /** The list of slots where the itemstack holds will be distributed */
    private final Set dragSlots = new HashSet();
    /** list of all people that need to be notified when this craftinventory changes */
    protected List crafters = new ArrayList();
    private Set playerList = new HashSet();
    private static final String __OBFID = "CL_00001730";

    /**
     * Adds an item slot to this container
     */
    protected Slot addSlotToContainer(Slot p_75146_1_)
    {
        p_75146_1_.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(p_75146_1_);
        this.inventoryItemStacks.add((Object)null);
        return p_75146_1_;
    }

    public void onCraftGuiOpened(ICrafting p_75132_1_)
    {
        if (this.crafters.contains(p_75132_1_))
        {
            throw new IllegalArgumentException("Listener already listening");
        }
        else
        {
            this.crafters.add(p_75132_1_);
            p_75132_1_.updateCraftingInventory(this, this.getInventory());
            this.detectAndSendChanges();
        }
    }

    /**
     * returns a list if itemStacks, for each slot.
     */
    public List getInventory()
    {
        ArrayList arraylist = new ArrayList();

        for (int i = 0; i < this.inventorySlots.size(); ++i)
        {
            arraylist.add(((Slot)this.inventorySlots.get(i)).getStack());
        }

        return arraylist;
    }

    /**
     * Remove this crafting listener from the listener list.
     */
    @SideOnly(Side.CLIENT)
    public void removeCraftingFromCrafters(ICrafting p_82847_1_)
    {
        this.crafters.remove(p_82847_1_);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        for (int i = 0; i < this.inventorySlots.size(); ++i)
        {
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                itemstack1 = itemstack == null ? null : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                for (int j = 0; j < this.crafters.size(); ++j)
                {
                    ((ICrafting)this.crafters.get(j)).sendSlotContents(this, i, itemstack1);
                }
            }
        }
    }

    /**
     * enchants the item on the table using the specified slot; also deducts XP from player
     */
    public boolean enchantItem(EntityPlayer player, int id)
    {
        return false;
    }

    public Slot getSlotFromInventory(IInventory p_75147_1_, int p_75147_2_)
    {
        for (int j = 0; j < this.inventorySlots.size(); ++j)
        {
            Slot slot = (Slot)this.inventorySlots.get(j);

            if (slot.isHere(p_75147_1_, p_75147_2_))
            {
                return slot;
            }
        }

        return null;
    }

    public Slot getSlot(int p_75139_1_)
    {
        return (Slot)this.inventorySlots.get(p_75139_1_);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = (Slot)this.inventorySlots.get(index);
        return slot != null ? slot.getStack() : null;
    }

    /**
     * Handles slot click. Args : slotId, clickedButton, mode (0 = basic click, 1 = shift click, 2 = Hotbar, 3 =
     * pickBlock, 4 = Drop, 5 = ?, 6 = Double click), player
     *  
     * @param slotId Will be either the slot id in the current Container or -999 if dragging
     * @param mode 0 for basic click, 1 for shift-click, 2 for hotbar, 3 for pickBlock, 4 for drop, 5 for item
     * distribution drag, 6 for double click
     */
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player)
    {
        ItemStack itemstack = null;
        InventoryPlayer inventoryplayer = player.inventory;
        int i1;
        ItemStack itemstack3;

        if (mode == 5)
        {
            int l = this.dragEvent;
            this.dragEvent = getDragEvent(clickedButton);

            if ((l != 1 || this.dragEvent != 2) && l != this.dragEvent)
            {
                this.resetDrag();
            }
            else if (inventoryplayer.getItemStack() == null)
            {
                this.resetDrag();
            }
            else if (this.dragEvent == 0)
            {
                this.dragMode = extractDragMode(clickedButton);

                if (isValidDragMode(this.dragMode))
                {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                }
                else
                {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1)
            {
                Slot slot = (Slot)this.inventorySlots.get(slotId);

                if (slot != null && canAddItemToSlot(slot, inventoryplayer.getItemStack(), true) && slot.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize > this.dragSlots.size() && this.canDragIntoSlot(slot))
                {
                    this.dragSlots.add(slot);
                }
            }
            else if (this.dragEvent == 2)
            {
                if (!this.dragSlots.isEmpty())
                {
                    itemstack3 = inventoryplayer.getItemStack().copy();
                    i1 = inventoryplayer.getItemStack().stackSize;
                    Iterator iterator = this.dragSlots.iterator();

                    while (iterator.hasNext())
                    {
                        Slot slot1 = (Slot)iterator.next();

                        if (slot1 != null && canAddItemToSlot(slot1, inventoryplayer.getItemStack(), true) && slot1.isItemValid(inventoryplayer.getItemStack()) && inventoryplayer.getItemStack().stackSize >= this.dragSlots.size() && this.canDragIntoSlot(slot1))
                        {
                            ItemStack itemstack1 = itemstack3.copy();
                            int j1 = slot1.getHasStack() ? slot1.getStack().stackSize : 0;
                            computeStackSize(this.dragSlots, this.dragMode, itemstack1, j1);

                            if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                            {
                                itemstack1.stackSize = itemstack1.getMaxStackSize();
                            }

                            if (itemstack1.stackSize > slot1.getSlotStackLimit())
                            {
                                itemstack1.stackSize = slot1.getSlotStackLimit();
                            }

                            i1 -= itemstack1.stackSize - j1;
                            slot1.putStack(itemstack1);
                        }
                    }

                    itemstack3.stackSize = i1;

                    if (itemstack3.stackSize <= 0)
                    {
                        itemstack3 = null;
                    }

                    inventoryplayer.setItemStack(itemstack3);
                }

                this.resetDrag();
            }
            else
            {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0)
        {
            this.resetDrag();
        }
        else
        {
            Slot slot2;
            int l1;
            ItemStack itemstack5;

            if ((mode == 0 || mode == 1) && (clickedButton == 0 || clickedButton == 1))
            {
                if (slotId == -999)
                {
                    if (inventoryplayer.getItemStack() != null && slotId == -999)
                    {
                        if (clickedButton == 0)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), true);
                            inventoryplayer.setItemStack((ItemStack)null);
                        }

                        if (clickedButton == 1)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack().splitStack(1), true);

                            if (inventoryplayer.getItemStack().stackSize == 0)
                            {
                                inventoryplayer.setItemStack((ItemStack)null);
                            }
                        }
                    }
                }
                else if (mode == 1)
                {
                    if (slotId < 0)
                    {
                        return null;
                    }

                    slot2 = (Slot)this.inventorySlots.get(slotId);

                    if (slot2 != null && slot2.canTakeStack(player))
                    {
                        itemstack3 = this.transferStackInSlot(player, slotId);

                        if (itemstack3 != null)
                        {
                            Item item = itemstack3.getItem();
                            itemstack = itemstack3.copy();

                            if (slot2.getStack() != null && slot2.getStack().getItem() == item)
                            {
                                this.retrySlotClick(slotId, clickedButton, true, player);
                            }
                        }
                    }
                }
                else
                {
                    if (slotId < 0)
                    {
                        return null;
                    }

                    slot2 = (Slot)this.inventorySlots.get(slotId);

                    if (slot2 != null)
                    {
                        itemstack3 = slot2.getStack();
                        ItemStack itemstack4 = inventoryplayer.getItemStack();

                        if (itemstack3 != null)
                        {
                            itemstack = itemstack3.copy();
                        }

                        if (itemstack3 == null)
                        {
                            if (itemstack4 != null && slot2.isItemValid(itemstack4))
                            {
                                l1 = clickedButton == 0 ? itemstack4.stackSize : 1;

                                if (l1 > slot2.getSlotStackLimit())
                                {
                                    l1 = slot2.getSlotStackLimit();
                                }

                                if (itemstack4.stackSize >= l1)
                                {
                                    slot2.putStack(itemstack4.splitStack(l1));
                                }

                                if (itemstack4.stackSize == 0)
                                {
                                    inventoryplayer.setItemStack((ItemStack)null);
                                }
                            }
                        }
                        else if (slot2.canTakeStack(player))
                        {
                            if (itemstack4 == null)
                            {
                                l1 = clickedButton == 0 ? itemstack3.stackSize : (itemstack3.stackSize + 1) / 2;
                                itemstack5 = slot2.decrStackSize(l1);
                                inventoryplayer.setItemStack(itemstack5);

                                if (itemstack3.stackSize == 0)
                                {
                                    slot2.putStack((ItemStack)null);
                                }

                                slot2.onPickupFromSlot(player, inventoryplayer.getItemStack());
                            }
                            else if (slot2.isItemValid(itemstack4))
                            {
                                if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getMetadata() == itemstack4.getMetadata() && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4))
                                {
                                    l1 = clickedButton == 0 ? itemstack4.stackSize : 1;

                                    if (l1 > slot2.getSlotStackLimit() - itemstack3.stackSize)
                                    {
                                        l1 = slot2.getSlotStackLimit() - itemstack3.stackSize;
                                    }

                                    if (l1 > itemstack4.getMaxStackSize() - itemstack3.stackSize)
                                    {
                                        l1 = itemstack4.getMaxStackSize() - itemstack3.stackSize;
                                    }

                                    itemstack4.splitStack(l1);

                                    if (itemstack4.stackSize == 0)
                                    {
                                        inventoryplayer.setItemStack((ItemStack)null);
                                    }

                                    itemstack3.stackSize += l1;
                                }
                                else if (itemstack4.stackSize <= slot2.getSlotStackLimit())
                                {
                                    slot2.putStack(itemstack4);
                                    inventoryplayer.setItemStack(itemstack3);
                                }
                            }
                            else if (itemstack3.getItem() == itemstack4.getItem() && itemstack4.getMaxStackSize() > 1 && (!itemstack3.getHasSubtypes() || itemstack3.getMetadata() == itemstack4.getMetadata()) && ItemStack.areItemStackTagsEqual(itemstack3, itemstack4))
                            {
                                l1 = itemstack3.stackSize;

                                if (l1 > 0 && l1 + itemstack4.stackSize <= itemstack4.getMaxStackSize())
                                {
                                    itemstack4.stackSize += l1;
                                    itemstack3 = slot2.decrStackSize(l1);

                                    if (itemstack3.stackSize == 0)
                                    {
                                        slot2.putStack((ItemStack)null);
                                    }

                                    slot2.onPickupFromSlot(player, inventoryplayer.getItemStack());
                                }
                            }
                        }

                        slot2.onSlotChanged();
                    }
                }
            }
            else if (mode == 2 && clickedButton >= 0 && clickedButton < 9)
            {
                slot2 = (Slot)this.inventorySlots.get(slotId);

                if (slot2.canTakeStack(player))
                {
                    itemstack3 = inventoryplayer.getStackInSlot(clickedButton);
                    boolean flag = itemstack3 == null || slot2.inventory == inventoryplayer && slot2.isItemValid(itemstack3);
                    l1 = -1;

                    if (!flag)
                    {
                        l1 = inventoryplayer.getFirstEmptyStack();
                        flag |= l1 > -1;
                    }

                    if (slot2.getHasStack() && flag)
                    {
                        itemstack5 = slot2.getStack();
                        inventoryplayer.setInventorySlotContents(clickedButton, itemstack5.copy());

                        if ((slot2.inventory != inventoryplayer || !slot2.isItemValid(itemstack3)) && itemstack3 != null)
                        {
                            if (l1 > -1)
                            {
                                inventoryplayer.addItemStackToInventory(itemstack3);
                                slot2.decrStackSize(itemstack5.stackSize);
                                slot2.putStack((ItemStack)null);
                                slot2.onPickupFromSlot(player, itemstack5);
                            }
                        }
                        else
                        {
                            slot2.decrStackSize(itemstack5.stackSize);
                            slot2.putStack(itemstack3);
                            slot2.onPickupFromSlot(player, itemstack5);
                        }
                    }
                    else if (!slot2.getHasStack() && itemstack3 != null && slot2.isItemValid(itemstack3))
                    {
                        inventoryplayer.setInventorySlotContents(clickedButton, (ItemStack)null);
                        slot2.putStack(itemstack3);
                    }
                }
            }
            else if (mode == 3 && player.capabilities.isCreativeMode && inventoryplayer.getItemStack() == null && slotId >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotId);

                if (slot2 != null && slot2.getHasStack())
                {
                    itemstack3 = slot2.getStack().copy();
                    itemstack3.stackSize = itemstack3.getMaxStackSize();
                    inventoryplayer.setItemStack(itemstack3);
                }
            }
            else if (mode == 4 && inventoryplayer.getItemStack() == null && slotId >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotId);

                if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player))
                {
                    itemstack3 = slot2.decrStackSize(clickedButton == 0 ? 1 : slot2.getStack().stackSize);
                    slot2.onPickupFromSlot(player, itemstack3);
                    player.dropPlayerItemWithRandomChoice(itemstack3, true);
                }
            }
            else if (mode == 6 && slotId >= 0)
            {
                slot2 = (Slot)this.inventorySlots.get(slotId);
                itemstack3 = inventoryplayer.getItemStack();

                if (itemstack3 != null && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(player)))
                {
                    i1 = clickedButton == 0 ? 0 : this.inventorySlots.size() - 1;
                    l1 = clickedButton == 0 ? 1 : -1;

                    for (int i2 = 0; i2 < 2; ++i2)
                    {
                        for (int j2 = i1; j2 >= 0 && j2 < this.inventorySlots.size() && itemstack3.stackSize < itemstack3.getMaxStackSize(); j2 += l1)
                        {
                            Slot slot3 = (Slot)this.inventorySlots.get(j2);

                            if (slot3.getHasStack() && canAddItemToSlot(slot3, itemstack3, true) && slot3.canTakeStack(player) && this.func_94530_a(itemstack3, slot3) && (i2 != 0 || slot3.getStack().stackSize != slot3.getStack().getMaxStackSize()))
                            {
                                int k1 = Math.min(itemstack3.getMaxStackSize() - itemstack3.stackSize, slot3.getStack().stackSize);
                                ItemStack itemstack2 = slot3.decrStackSize(k1);
                                itemstack3.stackSize += k1;

                                if (itemstack2.stackSize <= 0)
                                {
                                    slot3.putStack((ItemStack)null);
                                }

                                slot3.onPickupFromSlot(player, itemstack2);
                            }
                        }
                    }
                }

                this.detectAndSendChanges();
            }
        }

        return itemstack;
    }

    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
    {
        return true;
    }

    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer p_75133_4_)
    {
        this.slotClick(p_75133_1_, p_75133_2_, 1, p_75133_4_);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer p_75134_1_)
    {
        InventoryPlayer inventoryplayer = p_75134_1_.inventory;

        if (inventoryplayer.getItemStack() != null)
        {
            p_75134_1_.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), false);
            inventoryplayer.setItemStack((ItemStack)null);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
        this.detectAndSendChanges();
    }

    /**
     * args: slotID, itemStack to put in slot
     */
    public void putStackInSlot(int p_75141_1_, ItemStack p_75141_2_)
    {
        this.getSlot(p_75141_1_).putStack(p_75141_2_);
    }

    /**
     * places itemstacks in first x slots, x being aitemstack.lenght
     */
    @SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] p_75131_1_)
    {
        for (int i = 0; i < p_75131_1_.length; ++i)
        {
            this.getSlot(i).putStack(p_75131_1_[i]);
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int p_75137_1_, int p_75137_2_) {}

    /**
     * Gets a unique transaction ID. Parameter is unused.
     */
    @SideOnly(Side.CLIENT)
    public short getNextTransactionID(InventoryPlayer p_75136_1_)
    {
        ++this.transactionID;
        return this.transactionID;
    }

    /**
     * gets whether or not the player can craft in this inventory or not
     */
    public boolean getCanCraft(EntityPlayer p_75129_1_)
    {
        return !this.playerList.contains(p_75129_1_);
    }

    /**
     * sets whether the player can craft in this inventory or not
     */
    public void setCanCraft(EntityPlayer p_75128_1_, boolean p_75128_2_)
    {
        if (p_75128_2_)
        {
            this.playerList.remove(p_75128_1_);
        }
        else
        {
            this.playerList.add(p_75128_1_);
        }
    }

    public abstract boolean canInteractWith(EntityPlayer player);

    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_)
    {
        boolean flag1 = false;
        int k = p_75135_2_;

        if (p_75135_4_)
        {
            k = p_75135_3_ - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (p_75135_1_.isStackable())
        {
            while (p_75135_1_.stackSize > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_))
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 != null && itemstack1.getItem() == p_75135_1_.getItem() && (!p_75135_1_.getHasSubtypes() || p_75135_1_.getMetadata() == itemstack1.getMetadata()) && ItemStack.areItemStackTagsEqual(p_75135_1_, itemstack1))
                {
                    int l = itemstack1.stackSize + p_75135_1_.stackSize;

                    if (l <= p_75135_1_.getMaxStackSize())
                    {
                        p_75135_1_.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.stackSize < p_75135_1_.getMaxStackSize())
                    {
                        p_75135_1_.stackSize -= p_75135_1_.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = p_75135_1_.getMaxStackSize();
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (p_75135_4_)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        if (p_75135_1_.stackSize > 0)
        {
            if (p_75135_4_)
            {
                k = p_75135_3_ - 1;
            }
            else
            {
                k = p_75135_2_;
            }

            while (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_)
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 == null)
                {
                    slot.putStack(p_75135_1_.copy());
                    slot.onSlotChanged();
                    p_75135_1_.stackSize = 0;
                    flag1 = true;
                    break;
                }

                if (p_75135_4_)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        return flag1;
    }

    /**
     * Extracts the drag mode. Args : eventButton. Return (0 : evenly split, 1 : one item by slot, 2 : not used ?)
     */
    public static int extractDragMode(int p_94529_0_)
    {
        return p_94529_0_ >> 2 & 3;
    }

    /**
     * Args : clickedButton, Returns (0 : start drag, 1 : add slot, 2 : end drag)
     */
    public static int getDragEvent(int p_94532_0_)
    {
        return p_94532_0_ & 3;
    }

    @SideOnly(Side.CLIENT)
    public static int func_94534_d(int p_94534_0_, int p_94534_1_)
    {
        return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
    }

    /**
     * Args : dragMode. Returns true if dragMode = 0 (evenly split) or = 1 (one item by slot)
     */
    public static boolean isValidDragMode(int p_94528_0_)
    {
        return p_94528_0_ == 0 || p_94528_0_ == 1;
    }

    /**
     * Reset the drag fields
     */
    protected void resetDrag()
    {
        this.dragEvent = 0;
        this.dragSlots.clear();
    }

    /**
     * Checks if it's possible to add the given itemstack to the given slot.
     *  
     * @param stackSizeMatters Will check if adding items to the slot will stack larger than the max stack size
     */
    public static boolean canAddItemToSlot(Slot slotIn, ItemStack stack, boolean stackSizeMatters)
    {
        boolean flag1 = slotIn == null || !slotIn.getHasStack();

        if (slotIn != null && slotIn.getHasStack() && stack != null && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack))
        {
            int i = stackSizeMatters ? 0 : stack.stackSize;
            flag1 |= slotIn.getStack().stackSize + i <= stack.getMaxStackSize();
        }

        return flag1;
    }

    /**
     * Compute the new stack size, Returns the stack with the new size. Args : dragSlots, dragMode, dragStack,
     * slotStackSize
     */
    public static void computeStackSize(Set p_94525_0_, int p_94525_1_, ItemStack p_94525_2_, int p_94525_3_)
    {
        switch (p_94525_1_)
        {
            case 0:
                p_94525_2_.stackSize = MathHelper.floor_float((float)p_94525_2_.stackSize / (float)p_94525_0_.size());
                break;
            case 1:
                p_94525_2_.stackSize = 1;
        }

        p_94525_2_.stackSize += p_94525_3_;
    }

    /**
     * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if
     * the slot can be added to a list of Slots to split the held ItemStack across.
     */
    public boolean canDragIntoSlot(Slot p_94531_1_)
    {
        return true;
    }

    public static int calcRedstoneFromInventory(IInventory p_94526_0_)
    {
        if (p_94526_0_ == null)
        {
            return 0;
        }
        else
        {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < p_94526_0_.getSizeInventory(); ++j)
            {
                ItemStack itemstack = p_94526_0_.getStackInSlot(j);

                if (itemstack != null)
                {
                    f += (float)itemstack.stackSize / (float)Math.min(p_94526_0_.getInventoryStackLimit(), itemstack.getMaxStackSize());
                    ++i;
                }
            }

            f /= (float)p_94526_0_.getSizeInventory();
            return MathHelper.floor_float(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }
}