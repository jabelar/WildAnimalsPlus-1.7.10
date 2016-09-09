package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput extends Packet
{
    /** Positive for left strafe, negative for right */
    private float strafeSpeed;
    /** Positive for forward, negative for backward */
    private float forwardSpeed;
    private boolean jumping;
    private boolean sneaking;
    private static final String __OBFID = "CL_00001367";

    public C0CPacketInput() {}

    @SideOnly(Side.CLIENT)
    public C0CPacketInput(float p_i45261_1_, float p_i45261_2_, boolean p_i45261_3_, boolean p_i45261_4_)
    {
        this.strafeSpeed = p_i45261_1_;
        this.forwardSpeed = p_i45261_2_;
        this.jumping = p_i45261_3_;
        this.sneaking = p_i45261_4_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.strafeSpeed = data.readFloat();
        this.forwardSpeed = data.readFloat();
        this.jumping = data.readBoolean();
        this.sneaking = data.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeFloat(this.strafeSpeed);
        data.writeFloat(this.forwardSpeed);
        data.writeBoolean(this.jumping);
        data.writeBoolean(this.sneaking);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processInput(this);
    }

    public float getStrafeSpeed()
    {
        return this.strafeSpeed;
    }

    public float getForwardSpeed()
    {
        return this.forwardSpeed;
    }

    public boolean isJumping()
    {
        return this.jumping;
    }

    public boolean isSneaking()
    {
        return this.sneaking;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}