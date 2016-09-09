package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems extends Packet
{
    private int field_148914_a;
    private ItemStack[] field_148913_b;
    private static final String __OBFID = "CL_00001294";

    public S30PacketWindowItems() {}

    public S30PacketWindowItems(int p_i45186_1_, List p_i45186_2_)
    {
        this.field_148914_a = p_i45186_1_;
        this.field_148913_b = new ItemStack[p_i45186_2_.size()];

        for (int j = 0; j < this.field_148913_b.length; ++j)
        {
            ItemStack itemstack = (ItemStack)p_i45186_2_.get(j);
            this.field_148913_b[j] = itemstack == null ? null : itemstack.copy();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_148914_a = data.readUnsignedByte();
        short short1 = data.readShort();
        this.field_148913_b = new ItemStack[short1];

        for (int i = 0; i < short1; ++i)
        {
            this.field_148913_b[i] = data.readItemStackFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeByte(this.field_148914_a);
        data.writeShort(this.field_148913_b.length);
        ItemStack[] aitemstack = this.field_148913_b;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j)
        {
            ItemStack itemstack = aitemstack[j];
            data.writeItemStackToBuffer(itemstack);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleWindowItems(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public int func_148911_c()
    {
        return this.field_148914_a;
    }

    @SideOnly(Side.CLIENT)
    public ItemStack[] func_148910_d()
    {
        return this.field_148913_b;
    }
}