package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored
{
    private static final String __OBFID = "CL_00000074";

    public ItemLilyPad(Block p_i45357_1_)
    {
        super(p_i45357_1_, false);
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

                if (worldIn.getBlock(i, j, k).getMaterial() == Material.water && worldIn.getBlockMetadata(i, j, k) == 0 && worldIn.isAirBlock(i, j + 1, k))
                {
                    // special case for handling block placement with water lilies
                    net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, i, j + 1, k);
                    worldIn.setBlock(i, j + 1, k, Blocks.waterlily);
                    if (net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, net.minecraftforge.common.util.ForgeDirection.UP).isCanceled()) 
                    {
                        blocksnapshot.restore(true, false);
                        return itemStackIn;
                    }

                    if (!player.capabilities.isCreativeMode)
                    {
                        --itemStackIn.stackSize;
                    }
                }
            }

            return itemStackIn;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_)
    {
        return Blocks.waterlily.getRenderColor(p_82790_1_.getMetadata());
    }
}