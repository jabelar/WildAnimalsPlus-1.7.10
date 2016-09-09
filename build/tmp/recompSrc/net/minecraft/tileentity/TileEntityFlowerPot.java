package net.minecraft.tileentity;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityFlowerPot extends TileEntity
{
    private Item flowerPotItem;
    private int flowerPotData;
    private static final String __OBFID = "CL_00000356";

    public TileEntityFlowerPot() {}

    public TileEntityFlowerPot(Item p_i45442_1_, int p_i45442_2_)
    {
        this.flowerPotItem = p_i45442_1_;
        this.flowerPotData = p_i45442_2_;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("Item", Item.getIdFromItem(this.flowerPotItem));
        compound.setInteger("Data", this.flowerPotData);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.flowerPotItem = Item.getItemById(compound.getInteger("Item"));
        this.flowerPotData = compound.getInteger("Data");
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 5, nbttagcompound);
    }

    public void func_145964_a(Item p_145964_1_, int p_145964_2_)
    {
        this.flowerPotItem = p_145964_1_;
        this.flowerPotData = p_145964_2_;
    }

    public Item getFlowerPotItem()
    {
        return this.flowerPotItem;
    }

    public int getFlowerPotData()
    {
        return this.flowerPotData;
    }
}