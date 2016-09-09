package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

public class S27PacketExplosion extends Packet
{
    private double field_149158_a;
    private double field_149156_b;
    private double field_149157_c;
    private float field_149154_d;
    private List field_149155_e;
    private float field_149152_f;
    private float field_149153_g;
    private float field_149159_h;
    private static final String __OBFID = "CL_00001300";

    public S27PacketExplosion() {}

    public S27PacketExplosion(double p_i45193_1_, double p_i45193_3_, double p_i45193_5_, float p_i45193_7_, List p_i45193_8_, Vec3 p_i45193_9_)
    {
        this.field_149158_a = p_i45193_1_;
        this.field_149156_b = p_i45193_3_;
        this.field_149157_c = p_i45193_5_;
        this.field_149154_d = p_i45193_7_;
        this.field_149155_e = new ArrayList(p_i45193_8_);

        if (p_i45193_9_ != null)
        {
            this.field_149152_f = (float)p_i45193_9_.xCoord;
            this.field_149153_g = (float)p_i45193_9_.yCoord;
            this.field_149159_h = (float)p_i45193_9_.zCoord;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149158_a = (double)data.readFloat();
        this.field_149156_b = (double)data.readFloat();
        this.field_149157_c = (double)data.readFloat();
        this.field_149154_d = data.readFloat();
        int i = data.readInt();
        this.field_149155_e = new ArrayList(i);
        int j = (int)this.field_149158_a;
        int k = (int)this.field_149156_b;
        int l = (int)this.field_149157_c;

        for (int i1 = 0; i1 < i; ++i1)
        {
            int j1 = data.readByte() + j;
            int k1 = data.readByte() + k;
            int l1 = data.readByte() + l;
            this.field_149155_e.add(new ChunkPosition(j1, k1, l1));
        }

        this.field_149152_f = data.readFloat();
        this.field_149153_g = data.readFloat();
        this.field_149159_h = data.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeFloat((float)this.field_149158_a);
        data.writeFloat((float)this.field_149156_b);
        data.writeFloat((float)this.field_149157_c);
        data.writeFloat(this.field_149154_d);
        data.writeInt(this.field_149155_e.size());
        int i = (int)this.field_149158_a;
        int j = (int)this.field_149156_b;
        int k = (int)this.field_149157_c;
        Iterator iterator = this.field_149155_e.iterator();

        while (iterator.hasNext())
        {
            ChunkPosition chunkposition = (ChunkPosition)iterator.next();
            int l = chunkposition.chunkPosX - i;
            int i1 = chunkposition.chunkPosY - j;
            int j1 = chunkposition.chunkPosZ - k;
            data.writeByte(l);
            data.writeByte(i1);
            data.writeByte(j1);
        }

        data.writeFloat(this.field_149152_f);
        data.writeFloat(this.field_149153_g);
        data.writeFloat(this.field_149159_h);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleExplosion(this);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    @SideOnly(Side.CLIENT)
    public float func_149149_c()
    {
        return this.field_149152_f;
    }

    @SideOnly(Side.CLIENT)
    public float func_149144_d()
    {
        return this.field_149153_g;
    }

    @SideOnly(Side.CLIENT)
    public float func_149147_e()
    {
        return this.field_149159_h;
    }

    @SideOnly(Side.CLIENT)
    public double func_149148_f()
    {
        return this.field_149158_a;
    }

    @SideOnly(Side.CLIENT)
    public double func_149143_g()
    {
        return this.field_149156_b;
    }

    @SideOnly(Side.CLIENT)
    public double func_149145_h()
    {
        return this.field_149157_c;
    }

    @SideOnly(Side.CLIENT)
    public float func_149146_i()
    {
        return this.field_149154_d;
    }

    @SideOnly(Side.CLIENT)
    public List func_149150_j()
    {
        return this.field_149155_e;
    }
}