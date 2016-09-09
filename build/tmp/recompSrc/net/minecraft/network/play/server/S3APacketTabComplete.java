package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.ArrayUtils;

public class S3APacketTabComplete extends Packet
{
    private String[] field_149632_a;
    private static final String __OBFID = "CL_00001288";

    public S3APacketTabComplete() {}

    public S3APacketTabComplete(String[] p_i45178_1_)
    {
        this.field_149632_a = p_i45178_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149632_a = new String[data.readVarIntFromBuffer()];

        for (int i = 0; i < this.field_149632_a.length; ++i)
        {
            this.field_149632_a[i] = data.readStringFromBuffer(32767);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeVarIntToBuffer(this.field_149632_a.length);
        String[] astring = this.field_149632_a;
        int i = astring.length;

        for (int j = 0; j < i; ++j)
        {
            String s = astring[j];
            data.writeStringToBuffer(s);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleTabComplete(this);
    }

    @SideOnly(Side.CLIENT)
    public String[] func_149630_c()
    {
        return this.field_149632_a;
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("candidates=\'%s\'", new Object[] {ArrayUtils.toString(this.field_149632_a)});
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}