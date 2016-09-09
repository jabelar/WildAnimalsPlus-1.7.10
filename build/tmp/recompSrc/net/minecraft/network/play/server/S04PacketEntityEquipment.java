package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment extends Packet
{
    private int field_149394_a;
    private int field_149392_b;
    private ItemStack field_149393_c;
    private static final String __OBFID = "CL_00001330";

    public S04PacketEntityEquipment() {}

    public S04PacketEntityEquipment(int p_i45221_1_, int p_i45221_2_, ItemStack p_i45221_3_)
    {
        this.field_149394_a = p_i45221_1_;
        this.field_149392_b = p_i45221_2_;
        this.field_149393_c = p_i45221_3_ == null ? null : p_i45221_3_.copy();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149394_a = data.readInt();
        this.field_149392_b = data.readShort();
        this.field_149393_c = data.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.field_149394_a);
        data.writeShort(this.field_149392_b);
        data.writeItemStackToBuffer(this.field_149393_c);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityEquipment(this);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack func_149390_c()
    {
        return this.field_149393_c;
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("entity=%d, slot=%d, item=%s", new Object[] {Integer.valueOf(this.field_149394_a), Integer.valueOf(this.field_149392_b), this.field_149393_c});
    }

    @SideOnly(Side.CLIENT)
    public int func_149389_d()
    {
        return this.field_149394_a;
    }

    @SideOnly(Side.CLIENT)
    public int func_149388_e()
    {
        return this.field_149392_b;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}