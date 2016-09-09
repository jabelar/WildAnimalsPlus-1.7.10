package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C07PacketPlayerDigging extends Packet
{
    private int diggedBlockX;
    private int diggedBlockY;
    private int diggedBlockZ;
    private int diggingBlockFace;
    /** Status of the digging (started, ongoing, broken). */
    private int status;
    private static final String __OBFID = "CL_00001365";

    public C07PacketPlayerDigging() {}

    @SideOnly(Side.CLIENT)
    public C07PacketPlayerDigging(int p_i45258_1_, int p_i45258_2_, int p_i45258_3_, int p_i45258_4_, int p_i45258_5_)
    {
        this.status = p_i45258_1_;
        this.diggedBlockX = p_i45258_2_;
        this.diggedBlockY = p_i45258_3_;
        this.diggedBlockZ = p_i45258_4_;
        this.diggingBlockFace = p_i45258_5_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.status = data.readUnsignedByte();
        this.diggedBlockX = data.readInt();
        this.diggedBlockY = data.readUnsignedByte();
        this.diggedBlockZ = data.readInt();
        this.diggingBlockFace = data.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeByte(this.status);
        data.writeInt(this.diggedBlockX);
        data.writeByte(this.diggedBlockY);
        data.writeInt(this.diggedBlockZ);
        data.writeByte(this.diggingBlockFace);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayerDigging(this);
    }

    /**
     * Returns X coordinate of the block that was digged.
     */
    public int getDiggedBlockX()
    {
        return this.diggedBlockX;
    }

    /**
     * Returns Y coordinate of the block that was digged.
     */
    public int getDiggedBlockY()
    {
        return this.diggedBlockY;
    }

    /**
     * Returns Z coordinate of the block that was digged.
     */
    public int getDiggedBlockZ()
    {
        return this.diggedBlockZ;
    }

    public int getDiggingBlockFace()
    {
        return this.diggingBlockFace;
    }

    /**
     * Status of the digging (started, ongoing, broken).
     */
    public int getDiggedBlockStatus()
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
}