package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S0APacketUseBed extends Packet
{
    private int playerID;
    private int x;
    private int y;
    private int z;
    private static final String __OBFID = "CL_00001319";

    public S0APacketUseBed() {}

    public S0APacketUseBed(EntityPlayer player, int xPos, int yPos, int zPos)
    {
        this.x = xPos;
        this.y = yPos;
        this.z = zPos;
        this.playerID = player.getEntityId();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.playerID = data.readInt();
        this.x = data.readInt();
        this.y = data.readByte();
        this.z = data.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.playerID);
        data.writeInt(this.x);
        data.writeByte(this.y);
        data.writeInt(this.z);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUseBed(this);
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getPlayer(World p_149091_1_)
    {
        return (EntityPlayer)p_149091_1_.getEntityByID(this.playerID);
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
}