package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook extends Packet
{
    private double field_148940_a;
    private double field_148938_b;
    private double field_148939_c;
    private float field_148936_d;
    private float field_148937_e;
    private boolean field_148935_f;
    private static final String __OBFID = "CL_00001273";

    public S08PacketPlayerPosLook() {}

    public S08PacketPlayerPosLook(double p_i45164_1_, double p_i45164_3_, double p_i45164_5_, float p_i45164_7_, float p_i45164_8_, boolean p_i45164_9_)
    {
        this.field_148940_a = p_i45164_1_;
        this.field_148938_b = p_i45164_3_;
        this.field_148939_c = p_i45164_5_;
        this.field_148936_d = p_i45164_7_;
        this.field_148937_e = p_i45164_8_;
        this.field_148935_f = p_i45164_9_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_148940_a = data.readDouble();
        this.field_148938_b = data.readDouble();
        this.field_148939_c = data.readDouble();
        this.field_148936_d = data.readFloat();
        this.field_148937_e = data.readFloat();
        this.field_148935_f = data.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeDouble(this.field_148940_a);
        data.writeDouble(this.field_148938_b);
        data.writeDouble(this.field_148939_c);
        data.writeFloat(this.field_148936_d);
        data.writeFloat(this.field_148937_e);
        data.writeBoolean(this.field_148935_f);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handlePlayerPosLook(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public double func_148932_c()
    {
        return this.field_148940_a;
    }

    @SideOnly(Side.CLIENT)
    public double func_148928_d()
    {
        return this.field_148938_b;
    }

    @SideOnly(Side.CLIENT)
    public double func_148933_e()
    {
        return this.field_148939_c;
    }

    @SideOnly(Side.CLIENT)
    public float func_148931_f()
    {
        return this.field_148936_d;
    }

    @SideOnly(Side.CLIENT)
    public float func_148930_g()
    {
        return this.field_148937_e;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_148929_h()
    {
        return this.field_148935_f;
    }
}