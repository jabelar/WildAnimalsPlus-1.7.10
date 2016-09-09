package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S36PacketSignEditorOpen extends Packet
{
    private int field_149133_a;
    private int field_149131_b;
    private int field_149132_c;
    private static final String __OBFID = "CL_00001316";

    public S36PacketSignEditorOpen() {}

    public S36PacketSignEditorOpen(int p_i45207_1_, int p_i45207_2_, int p_i45207_3_)
    {
        this.field_149133_a = p_i45207_1_;
        this.field_149131_b = p_i45207_2_;
        this.field_149132_c = p_i45207_3_;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSignEditorOpen(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149133_a = data.readInt();
        this.field_149131_b = data.readInt();
        this.field_149132_c = data.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.field_149133_a);
        data.writeInt(this.field_149131_b);
        data.writeInt(this.field_149132_c);
    }

    @SideOnly(Side.CLIENT)
    public int func_149129_c()
    {
        return this.field_149133_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149128_d()
    {
        return this.field_149131_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149127_e()
    {
        return this.field_149132_c;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}