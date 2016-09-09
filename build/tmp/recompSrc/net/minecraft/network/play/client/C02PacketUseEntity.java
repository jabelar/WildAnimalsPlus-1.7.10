package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.World;

public class C02PacketUseEntity extends Packet
{
    private int entityId;
    private C02PacketUseEntity.Action action;
    private static final String __OBFID = "CL_00001357";

    public C02PacketUseEntity() {}

    @SideOnly(Side.CLIENT)
    public C02PacketUseEntity(Entity p_i45251_1_, C02PacketUseEntity.Action p_i45251_2_)
    {
        this.entityId = p_i45251_1_.getEntityId();
        this.action = p_i45251_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.entityId = data.readInt();
        this.action = C02PacketUseEntity.Action.ACTIONS[data.readByte() % C02PacketUseEntity.Action.ACTIONS.length];
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeInt(this.entityId);
        data.writeByte(this.action.id);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processUseEntity(this);
    }

    public Entity getEntityFromWorld(World worldIn)
    {
        return worldIn.getEntityByID(this.entityId);
    }

    public C02PacketUseEntity.Action getAction()
    {
        return this.action;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }

    public static enum Action
    {
        INTERACT(0),
        ATTACK(1);
        private static final C02PacketUseEntity.Action[] ACTIONS = new C02PacketUseEntity.Action[values().length];
        private final int id;

        private static final String __OBFID = "CL_00001358";

        private Action(int actionId)
        {
            this.id = actionId;
        }

        static
        {
            C02PacketUseEntity.Action[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                C02PacketUseEntity.Action var3 = var0[var2];
                ACTIONS[var3.id] = var3;
            }
        }
    }
}