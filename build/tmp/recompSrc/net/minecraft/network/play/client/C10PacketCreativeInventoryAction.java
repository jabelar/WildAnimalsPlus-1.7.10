package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C10PacketCreativeInventoryAction extends Packet
{
    private int slotId;
    private ItemStack stack;
    private static final String __OBFID = "CL_00001369";

    public C10PacketCreativeInventoryAction() {}

    @SideOnly(Side.CLIENT)
    public C10PacketCreativeInventoryAction(int p_i45263_1_, ItemStack p_i45263_2_)
    {
        this.slotId = p_i45263_1_;
        this.stack = p_i45263_2_ != null ? p_i45263_2_.copy() : null;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processCreativeInventoryAction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.slotId = data.readShort();
        this.stack = data.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeShort(this.slotId);
        data.writeItemStackToBuffer(this.stack);
    }

    public int getSlotId()
    {
        return this.slotId;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}