package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S18PacketEntityTeleport extends Packet
{
    private int field_149458_a;
    private int field_149456_b;
    private int field_149457_c;
    private int field_149454_d;
    private byte field_149455_e;
    private byte field_149453_f;
    private static final String __OBFID = "CL_00001340";

    public S18PacketEntityTeleport() {}

    public S18PacketEntityTeleport(Entity p_i45233_1_)
    {
        this.field_149458_a = p_i45233_1_.getEntityId();
        this.field_149456_b = MathHelper.floor_double(p_i45233_1_.posX * 32.0D);
        this.field_149457_c = MathHelper.floor_double(p_i45233_1_.posY * 32.0D);
        this.field_149454_d = MathHelper.floor_double(p_i45233_1_.posZ * 32.0D);
        this.field_149455_e = (byte)((int)(p_i45233_1_.rotationYaw * 256.0F / 360.0F));
        this.field_149453_f = (byte)((int)(p_i45233_1_.rotationPitch * 256.0F / 360.0F));
    }

    public S18PacketEntityTeleport(int p_i45234_1_, int p_i45234_2_, int p_i45234_3_, int p_i45234_4_, byte p_i45234_5_, byte p_i45234_6_)
    {
        this.field_149458_a = p_i45234_1_;
        this.field_149456_b = p_i45234_2_;
        this.field_149457_c = p_i45234_3_;
        this.field_149454_d = p_i45234_4_;
        this.field_149455_e = p_i45234_5_;
        this.field_149453_f = p_i45234_6_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149458_a = data.readInt();
        this.field_149456_b = data.readInt();
        this.field_149457_c = data.readInt();
        this.field_149454_d = data.readInt();
        this.field_149455_e = data.readByte();
        this.field_149453_f = data.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.field_149458_a);
        data.writeInt(this.field_149456_b);
        data.writeInt(this.field_149457_c);
        data.writeInt(this.field_149454_d);
        data.writeByte(this.field_149455_e);
        data.writeByte(this.field_149453_f);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityTeleport(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public int func_149451_c()
    {
        return this.field_149458_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149449_d()
    {
        return this.field_149456_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149448_e()
    {
        return this.field_149457_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_149446_f()
    {
        return this.field_149454_d;
    }

    @SideOnly(Side.CLIENT)
    public byte func_149450_g()
    {
        return this.field_149455_e;
    }

    @SideOnly(Side.CLIENT)
    public byte func_149447_h()
    {
        return this.field_149453_f;
    }
}