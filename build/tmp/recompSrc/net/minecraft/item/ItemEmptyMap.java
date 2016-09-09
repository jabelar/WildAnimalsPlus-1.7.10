package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase
{
    private static final String __OBFID = "CL_00000024";

    protected ItemEmptyMap()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player)
    {
        ItemStack itemstack1 = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
        String s = "map_" + itemstack1.getMetadata();
        MapData mapdata = new MapData(s);
        worldIn.setItemData(s, mapdata);
        mapdata.scale = 0;
        int i = 128 * (1 << mapdata.scale);
        mapdata.xCenter = (int)(Math.round(player.posX / (double)i) * (long)i);
        mapdata.zCenter = (int)(Math.round(player.posZ / (double)i) * (long)i);
        mapdata.dimension = worldIn.provider.dimensionId;
        mapdata.markDirty();
        --itemStackIn.stackSize;

        if (itemStackIn.stackSize <= 0)
        {
            return itemstack1;
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(itemstack1.copy()))
            {
                player.dropPlayerItemWithRandomChoice(itemstack1, false);
            }

            return itemStackIn;
        }
    }
}