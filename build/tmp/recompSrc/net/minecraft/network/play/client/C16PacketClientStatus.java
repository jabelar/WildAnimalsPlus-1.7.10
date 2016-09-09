package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus extends Packet
{
    private C16PacketClientStatus.EnumState status;
    private static final String __OBFID = "CL_00001348";

    public C16PacketClientStatus() {}

    public C16PacketClientStatus(C16PacketClientStatus.EnumState statusIn)
    {
        this.status = statusIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.status = C16PacketClientStatus.EnumState.BY_ID[data.readByte() % C16PacketClientStatus.EnumState.BY_ID.length];
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeByte(this.status.id);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processClientStatus(this);
    }

    public C16PacketClientStatus.EnumState getStatus()
    {
        return this.status;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }

    public static enum EnumState
    {
        PERFORM_RESPAWN(0),
        REQUEST_STATS(1),
        OPEN_INVENTORY_ACHIEVEMENT(2);
        private final int id;
        private static final C16PacketClientStatus.EnumState[] BY_ID = new C16PacketClientStatus.EnumState[values().length];

        private static final String __OBFID = "CL_00001349";

        private EnumState(int statusId)
        {
            this.id = statusId;
        }

        static
        {
            C16PacketClientStatus.EnumState[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                C16PacketClientStatus.EnumState var3 = var0[var2];
                BY_ID[var3.id] = var3;
            }
        }
    }
}