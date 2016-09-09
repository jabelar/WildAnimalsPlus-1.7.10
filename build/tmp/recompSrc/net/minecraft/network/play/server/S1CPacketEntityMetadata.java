package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata extends Packet
{
    private int field_149379_a;
    private List field_149378_b;
    private static final String __OBFID = "CL_00001326";

    public S1CPacketEntityMetadata() {}

    public S1CPacketEntityMetadata(int p_i45217_1_, DataWatcher p_i45217_2_, boolean p_i45217_3_)
    {
        this.field_149379_a = p_i45217_1_;

        if (p_i45217_3_)
        {
            this.field_149378_b = p_i45217_2_.getAllWatched();
        }
        else
        {
            this.field_149378_b = p_i45217_2_.getChanged();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149379_a = data.readInt();
        this.field_149378_b = DataWatcher.readWatchedListFromPacketBuffer(data);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.field_149379_a);
        DataWatcher.writeWatchedListToPacketBuffer(this.field_149378_b, data);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityMetadata(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public List func_149376_c()
    {
        return this.field_149378_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_149375_d()
    {
        return this.field_149379_a;
    }
}