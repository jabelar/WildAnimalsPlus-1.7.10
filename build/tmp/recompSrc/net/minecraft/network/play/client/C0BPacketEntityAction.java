package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction extends Packet
{
    private int field_149517_a;
    private int field_149515_b;
    private int field_149516_c;
    private static final String __OBFID = "CL_00001366";

    public C0BPacketEntityAction() {}

    @SideOnly(Side.CLIENT)
    public C0BPacketEntityAction(Entity p_i45259_1_, int p_i45259_2_)
    {
        this(p_i45259_1_, p_i45259_2_, 0);
    }

    @SideOnly(Side.CLIENT)
    public C0BPacketEntityAction(Entity p_i45260_1_, int p_i45260_2_, int p_i45260_3_)
    {
        this.field_149517_a = p_i45260_1_.getEntityId();
        this.field_149515_b = p_i45260_2_;
        this.field_149516_c = p_i45260_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149517_a = data.readInt();
        this.field_149515_b = data.readByte();
        this.field_149516_c = data.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.field_149517_a);
        data.writeByte(this.field_149515_b);
        data.writeInt(this.field_149516_c);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processEntityAction(this);
    }

    public int func_149513_d()
    {
        return this.field_149515_b;
    }

    public int func_149512_e()
    {
        return this.field_149516_c;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}