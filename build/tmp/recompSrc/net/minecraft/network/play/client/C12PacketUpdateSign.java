package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C12PacketUpdateSign extends Packet
{
    private int x;
    private int y;
    private int z;
    private String[] lines;
    private static final String __OBFID = "CL_00001370";

    public C12PacketUpdateSign() {}

    @SideOnly(Side.CLIENT)
    public C12PacketUpdateSign(int p_i45264_1_, int p_i45264_2_, int p_i45264_3_, String[] p_i45264_4_)
    {
        this.x = p_i45264_1_;
        this.y = p_i45264_2_;
        this.z = p_i45264_3_;
        this.lines = new String[] {p_i45264_4_[0], p_i45264_4_[1], p_i45264_4_[2], p_i45264_4_[3]};
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.x = data.readInt();
        this.y = data.readShort();
        this.z = data.readInt();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i)
        {
            this.lines[i] = data.readStringFromBuffer(15);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.x);
        data.writeShort(this.y);
        data.writeInt(this.z);

        for (int i = 0; i < 4; ++i)
        {
            data.writeStringToBuffer(this.lines[i]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processUpdateSign(this);
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }

    public String[] getLines()
    {
        return this.lines;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}