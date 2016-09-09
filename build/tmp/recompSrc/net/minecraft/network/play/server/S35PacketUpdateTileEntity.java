package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S35PacketUpdateTileEntity extends Packet
{
    private int x;
    private int y;
    private int z;
    /** Used only for vanilla tile entities */
    private int metadata;
    private NBTTagCompound nbt;
    private static final String __OBFID = "CL_00001285";

    public S35PacketUpdateTileEntity() {}

    public S35PacketUpdateTileEntity(int xcoord, int ycoord, int zcoord, int meta, NBTTagCompound nbtTag)
    {
        this.x = xcoord;
        this.y = ycoord;
        this.z = zcoord;
        this.metadata = meta;
        this.nbt = nbtTag;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.x = data.readInt();
        this.y = data.readShort();
        this.z = data.readInt();
        this.metadata = data.readUnsignedByte();
        this.nbt = data.readNBTTagCompoundFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.x);
        data.writeShort(this.y);
        data.writeInt(this.z);
        data.writeByte((byte)this.metadata);
        data.writeNBTTagCompoundToBuffer(this.nbt);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUpdateTileEntity(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public int getX()
    {
        return this.x;
    }

    @SideOnly(Side.CLIENT)
    public int getY()
    {
        return this.y;
    }

    @SideOnly(Side.CLIENT)
    public int getZ()
    {
        return this.z;
    }

    @SideOnly(Side.CLIENT)
    public int getTileEntityType()
    {
        return this.metadata;
    }

    @SideOnly(Side.CLIENT)
    public NBTTagCompound getNbtCompound()
    {
        return this.nbt;
    }
}