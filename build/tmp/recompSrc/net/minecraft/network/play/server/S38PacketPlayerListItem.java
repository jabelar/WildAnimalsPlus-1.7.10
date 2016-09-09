package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S38PacketPlayerListItem extends Packet
{
    private String field_149126_a;
    private boolean field_149124_b;
    private int field_149125_c;
    private static final String __OBFID = "CL_00001318";

    public S38PacketPlayerListItem() {}

    public S38PacketPlayerListItem(String p_i45209_1_, boolean p_i45209_2_, int p_i45209_3_)
    {
        this.field_149126_a = p_i45209_1_;
        this.field_149124_b = p_i45209_2_;
        this.field_149125_c = p_i45209_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149126_a = data.readStringFromBuffer(16);
        this.field_149124_b = data.readBoolean();
        this.field_149125_c = data.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeStringToBuffer(this.field_149126_a);
        data.writeBoolean(this.field_149124_b);
        data.writeShort(this.field_149125_c);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handlePlayerListItem(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public String func_149122_c()
    {
        return this.field_149126_a;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149121_d()
    {
        return this.field_149124_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149120_e()
    {
        return this.field_149125_c;
    }
}