package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload extends Packet
{
    private String channel;
    private int length;
    private byte[] data;
    private static final String __OBFID = "CL_00001356";

    public C17PacketCustomPayload() {}

    @SideOnly(Side.CLIENT)
    public C17PacketCustomPayload(String p_i45248_1_, ByteBuf p_i45248_2_)
    {
        this(p_i45248_1_, p_i45248_2_.array());
    }

    @SideOnly(Side.CLIENT)
    public C17PacketCustomPayload(String p_i45249_1_, byte[] p_i45249_2_)
    {
        this.channel = p_i45249_1_;
        this.data = p_i45249_2_;

        if (p_i45249_2_ != null)
        {
            this.length = p_i45249_2_.length;

            if (this.length >= 32767)
            {
                throw new IllegalArgumentException("Payload may not be larger than 32k");
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.channel = data.readStringFromBuffer(20);
        this.length = data.readShort();

        if (this.length > 0 && this.length < 32767)
        {
            this.data = new byte[this.length];
            data.readBytes(this.data);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeStringToBuffer(this.channel);
        data.writeShort((short)this.length);

        if (this.data != null)
        {
            data.writeBytes(this.data);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processVanilla250Packet(this);
    }

    public String getChannel()
    {
        return this.channel;
    }

    public byte[] getData()
    {
        return this.data;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}