package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item
{
    private static final String __OBFID = "CL_00000001";

    public ItemCarrotOnAStick()
    {
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setMaxStackSize(1);
        this.setMaxDurability(25);
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player)
    {
        if (player.isRiding() && player.ridingEntity instanceof EntityPig)
        {
            EntityPig entitypig = (EntityPig)player.ridingEntity;

            if (entitypig.getAIControlledByPlayer().isControlledByPlayer() && itemStackIn.getMaxDurability() - itemStackIn.getMetadata() >= 7)
            {
                entitypig.getAIControlledByPlayer().boostSpeed();
                itemStackIn.damageItem(7, player);

                if (itemStackIn.stackSize == 0)
                {
                    ItemStack itemstack1 = new ItemStack(Items.fishing_rod);
                    itemstack1.setTagCompound(itemStackIn.stackTagCompound);
                    return itemstack1;
                }
            }
        }

        return itemStackIn;
    }
}