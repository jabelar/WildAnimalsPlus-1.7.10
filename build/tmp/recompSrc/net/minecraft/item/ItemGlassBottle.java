package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item
{
    private static final String __OBFID = "CL_00001776";

    public ItemGlassBottle()
    {
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        return Items.potionitem.getIconFromDamage(0);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, player, true);

        if (movingobjectposition == null)
        {
            return itemStackIn;
        }
        else
        {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                if (!worldIn.canMineBlock(player, i, j, k))
                {
                    return itemStackIn;
                }

                if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStackIn))
                {
                    return itemStackIn;
                }

                if (worldIn.getBlock(i, j, k).getMaterial() == Material.water)
                {
                    --itemStackIn.stackSize;

                    if (itemStackIn.stackSize <= 0)
                    {
                        return new ItemStack(Items.potionitem);
                    }

                    if (!player.inventory.addItemStackToInventory(new ItemStack(Items.potionitem)))
                    {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.potionitem, 1, 0), false);
                    }
                }
            }

            return itemStackIn;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {}
}