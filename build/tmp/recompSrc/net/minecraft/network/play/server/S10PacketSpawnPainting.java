package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S10PacketSpawnPainting extends Packet
{
    private int field_148973_a;
    private int field_148971_b;
    private int field_148972_c;
    private int field_148969_d;
    private int field_148970_e;
    private String field_148968_f;
    private static final String __OBFID = "CL_00001280";

    public S10PacketSpawnPainting() {}

    public S10PacketSpawnPainting(EntityPainting p_i45170_1_)
    {
        this.field_148973_a = p_i45170_1_.getEntityId();
        this.field_148971_b = p_i45170_1_.field_146063_b;
        this.field_148972_c = p_i45170_1_.field_146064_c;
        this.field_148969_d = p_i45170_1_.field_146062_d;
        this.field_148970_e = p_i45170_1_.hangingDirection;
        this.field_148968_f = p_i45170_1_.art.title;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_148973_a = data.readVarIntFromBuffer();
        this.field_148968_f = data.readStringFromBuffer(EntityPainting.EnumArt.maxArtTitleLength);
        this.field_148971_b = data.readInt();
        this.field_148972_c = data.readInt();
        this.field_148969_d = data.readInt();
        this.field_148970_e = data.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeVarIntToBuffer(this.field_148973_a);
        data.writeStringToBuffer(this.field_148968_f);
        data.writeInt(this.field_148971_b);
        data.writeInt(this.field_148972_c);
        data.writeInt(this.field_148969_d);
        data.writeInt(this.field_148970_e);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnPainting(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, type=%s, x=%d, y=%d, z=%d", new Object[] {Integer.valueOf(this.field_148973_a), this.field_148968_f, Integer.valueOf(this.field_148971_b), Integer.valueOf(this.field_148972_c), Integer.valueOf(this.field_148969_d)});
    }

    @SideOnly(Side.CLIENT)
    public int func_148965_c()
    {
        return this.field_148973_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_148964_d()
    {
        return this.field_148971_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_148963_e()
    {
        return this.field_148972_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_148962_f()
    {
        return this.field_148969_d;
    }

    @SideOnly(Side.CLIENT)
    public int func_148966_g()
    {
        return this.field_148970_e;
    }

    @SideOnly(Side.CLIENT)
    public String func_148961_h()
    {
        return this.field_148968_f;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}