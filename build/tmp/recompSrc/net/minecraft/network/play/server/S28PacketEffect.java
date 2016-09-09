package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S28PacketEffect extends Packet
{
    private int soundType;
    /** can be a block/item id or other depending on the soundtype */
    private int soundData;
    private int xPos;
    private int yPos;
    private int zPos;
    /** If true the sound is played across the server */
    private boolean serverWide;
    private static final String __OBFID = "CL_00001307";

    public S28PacketEffect() {}

    public S28PacketEffect(int p_i45198_1_, int p_i45198_2_, int p_i45198_3_, int p_i45198_4_, int p_i45198_5_, boolean p_i45198_6_)
    {
        this.soundType = p_i45198_1_;
        this.xPos = p_i45198_2_;
        this.yPos = p_i45198_3_;
        this.zPos = p_i45198_4_;
        this.soundData = p_i45198_5_;
        this.serverWide = p_i45198_6_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.soundType = data.readInt();
        this.xPos = data.readInt();
        this.yPos = data.readByte() & 255;
        this.zPos = data.readInt();
        this.soundData = data.readInt();
        this.serverWide = data.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.soundType);
        data.writeInt(this.xPos);
        data.writeByte(this.yPos & 255);
        data.writeInt(this.zPos);
        data.writeInt(this.soundData);
        data.writeBoolean(this.serverWide);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEffect(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public boolean isSoundServerwide()
    {
        return this.serverWide;
    }

    @SideOnly(Side.CLIENT)
    public int getSoundType()
    {
        return this.soundType;
    }

    @SideOnly(Side.CLIENT)
    public int getSoundData()
    {
        return this.soundData;
    }

    @SideOnly(Side.CLIENT)
    public int getPosX()
    {
        return this.xPos;
    }

    @SideOnly(Side.CLIENT)
    public int getPosY()
    {
        return this.yPos;
    }

    @SideOnly(Side.CLIENT)
    public int getPosZ()
    {
        return this.zPos;
    }
}