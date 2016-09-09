package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload extends Packet
{
    private String channel;
    private byte[] data;
    private static final String __OBFID = "CL_00001297";

    public S3FPacketCustomPayload() {}

    public S3FPacketCustomPayload(String channelName, ByteBuf dataIn)
    {
        this(channelName, dataIn.array());
    }

    public S3FPacketCustomPayload(String channelName, byte[] dataIn)
    {
        this.channel = channelName;
        this.data = dataIn;

        //TODO: Remove this when FML protocol is re-written. To restore vanilla compatibility.
        if (dataIn.length > 0x1FFF9A) // Max size of ANY MC packet is 0x1FFFFF minus max size of this packet (101)
        {
            throw new IllegalArgumentException("Payload may not be larger than 2097050 bytes");
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.channel = data.readStringFromBuffer(20);
        this.data = new byte[cpw.mods.fml.common.network.ByteBufUtils.readVarShort(data)];
        data.readBytes(this.data);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeStringToBuffer(this.channel);
        cpw.mods.fml.common.network.ByteBufUtils.writeVarShort(data, this.data.length);
        data.writeBytes(this.data);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleCustomPayload(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public String func_149169_c()
    {
        return this.channel;
    }

    @SideOnly(Side.CLIENT)
    public byte[] func_149168_d()
    {
        return this.data;
    }
}