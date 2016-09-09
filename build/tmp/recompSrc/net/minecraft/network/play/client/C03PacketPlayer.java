package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer extends Packet
{
    protected double x;
    protected double y;
    protected double z;
    protected double stance;
    protected float yaw;
    protected float pitch;
    protected boolean field_149474_g;
    protected boolean field_149480_h;
    protected boolean rotating;
    private static final String __OBFID = "CL_00001360";

    public C03PacketPlayer() {}

    @SideOnly(Side.CLIENT)
    public C03PacketPlayer(boolean p_i45256_1_)
    {
        this.field_149474_g = p_i45256_1_;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayer(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149474_g = data.readUnsignedByte() != 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeByte(this.field_149474_g ? 1 : 0);
    }

    public double getPositionX()
    {
        return this.x;
    }

    public double getPositionY()
    {
        return this.y;
    }

    public double getPositionZ()
    {
        return this.z;
    }

    public double getStance()
    {
        return this.stance;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public boolean func_149465_i()
    {
        return this.field_149474_g;
    }

    public boolean func_149466_j()
    {
        return this.field_149480_h;
    }

    public boolean getRotating()
    {
        return this.rotating;
    }

    public void func_149469_a(boolean p_149469_1_)
    {
        this.field_149480_h = p_149469_1_;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer
        {
            private static final String __OBFID = "CL_00001361";

            public C04PacketPlayerPosition()
            {
                this.field_149480_h = true;
            }

            @SideOnly(Side.CLIENT)
            public C04PacketPlayerPosition(double p_i45253_1_, double p_i45253_3_, double p_i45253_5_, double p_i45253_7_, boolean p_i45253_9_)
            {
                this.x = p_i45253_1_;
                this.y = p_i45253_3_;
                this.stance = p_i45253_5_;
                this.z = p_i45253_7_;
                this.field_149474_g = p_i45253_9_;
                this.field_149480_h = true;
            }

            /**
             * Reads the raw packet data from the data stream.
             */
            public void readPacketData(PacketBuffer data) throws IOException
            {
                this.x = data.readDouble();
                this.y = data.readDouble();
                this.stance = data.readDouble();
                this.z = data.readDouble();
                super.readPacketData(data);
            }

            /**
             * Writes the raw packet data to the data stream.
             */
            public void writePacketData(PacketBuffer data) throws IOException
            {
                data.writeDouble(this.x);
                data.writeDouble(this.y);
                data.writeDouble(this.stance);
                data.writeDouble(this.z);
                super.writePacketData(data);
            }

            /**
             * Passes this Packet on to the NetHandler for processing.
             */
            public void processPacket(INetHandler handler)
            {
                super.processPacket((INetHandlerPlayServer)handler);
            }
        }

    public static class C05PacketPlayerLook extends C03PacketPlayer
        {
            private static final String __OBFID = "CL_00001363";

            public C05PacketPlayerLook()
            {
                this.rotating = true;
            }

            @SideOnly(Side.CLIENT)
            public C05PacketPlayerLook(float p_i45255_1_, float p_i45255_2_, boolean p_i45255_3_)
            {
                this.yaw = p_i45255_1_;
                this.pitch = p_i45255_2_;
                this.field_149474_g = p_i45255_3_;
                this.rotating = true;
            }

            /**
             * Reads the raw packet data from the data stream.
             */
            public void readPacketData(PacketBuffer data) throws IOException
            {
                this.yaw = data.readFloat();
                this.pitch = data.readFloat();
                super.readPacketData(data);
            }

            /**
             * Writes the raw packet data to the data stream.
             */
            public void writePacketData(PacketBuffer data) throws IOException
            {
                data.writeFloat(this.yaw);
                data.writeFloat(this.pitch);
                super.writePacketData(data);
            }

            /**
             * Passes this Packet on to the NetHandler for processing.
             */
            public void processPacket(INetHandler handler)
            {
                super.processPacket((INetHandlerPlayServer)handler);
            }
        }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer
        {
            private static final String __OBFID = "CL_00001362";

            public C06PacketPlayerPosLook()
            {
                this.field_149480_h = true;
                this.rotating = true;
            }

            @SideOnly(Side.CLIENT)
            public C06PacketPlayerPosLook(double p_i45254_1_, double p_i45254_3_, double p_i45254_5_, double p_i45254_7_, float p_i45254_9_, float p_i45254_10_, boolean p_i45254_11_)
            {
                this.x = p_i45254_1_;
                this.y = p_i45254_3_;
                this.stance = p_i45254_5_;
                this.z = p_i45254_7_;
                this.yaw = p_i45254_9_;
                this.pitch = p_i45254_10_;
                this.field_149474_g = p_i45254_11_;
                this.rotating = true;
                this.field_149480_h = true;
            }

            /**
             * Reads the raw packet data from the data stream.
             */
            public void readPacketData(PacketBuffer data) throws IOException
            {
                this.x = data.readDouble();
                this.y = data.readDouble();
                this.stance = data.readDouble();
                this.z = data.readDouble();
                this.yaw = data.readFloat();
                this.pitch = data.readFloat();
                super.readPacketData(data);
            }

            /**
             * Writes the raw packet data to the data stream.
             */
            public void writePacketData(PacketBuffer data) throws IOException
            {
                data.writeDouble(this.x);
                data.writeDouble(this.y);
                data.writeDouble(this.stance);
                data.writeDouble(this.z);
                data.writeFloat(this.yaw);
                data.writeFloat(this.pitch);
                super.writePacketData(data);
            }

            /**
             * Passes this Packet on to the NetHandler for processing.
             */
            public void processPacket(INetHandler handler)
            {
                super.processPacket((INetHandlerPlayServer)handler);
            }
        }
}