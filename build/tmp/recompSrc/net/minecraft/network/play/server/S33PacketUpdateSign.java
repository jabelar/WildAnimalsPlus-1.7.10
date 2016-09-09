package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S33PacketUpdateSign extends Packet
{
    private int field_149352_a;
    private int field_149350_b;
    private int field_149351_c;
    private String[] field_149349_d;
    private static final String __OBFID = "CL_00001338";

    public S33PacketUpdateSign() {}

    public S33PacketUpdateSign(int p_i45231_1_, int p_i45231_2_, int p_i45231_3_, String[] p_i45231_4_)
    {
        this.field_149352_a = p_i45231_1_;
        this.field_149350_b = p_i45231_2_;
        this.field_149351_c = p_i45231_3_;
        this.field_149349_d = new String[] {p_i45231_4_[0], p_i45231_4_[1], p_i45231_4_[2], p_i45231_4_[3]};
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149352_a = data.readInt();
        this.field_149350_b = data.readShort();
        this.field_149351_c = data.readInt();
        this.field_149349_d = new String[4];

        for (int i = 0; i < 4; ++i)
        {
            this.field_149349_d[i] = data.readStringFromBuffer(15);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.field_149352_a);
        data.writeShort(this.field_149350_b);
        data.writeInt(this.field_149351_c);

        for (int i = 0; i < 4; ++i)
        {
            data.writeStringToBuffer(this.field_149349_d[i]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUpdateSign(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public int func_149346_c()
    {
        return this.field_149352_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149345_d()
    {
        return this.field_149350_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149344_e()
    {
        return this.field_149351_c;
    }

    @SideOnly(Side.CLIENT)
    public String[] func_149347_f()
    {
        return this.field_149349_d;
    }
}