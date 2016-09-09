package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S02PacketChat extends Packet
{
    private IChatComponent chatComponent;
    private boolean isChat;
    private static final String __OBFID = "CL_00001289";

    public S02PacketChat()
    {
        this.isChat = true;
    }

    public S02PacketChat(IChatComponent component)
    {
        this(component, true);
    }

    public S02PacketChat(IChatComponent component, boolean chat)
    {
        this.isChat = true;
        this.chatComponent = component;
        this.isChat = chat;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.chatComponent = IChatComponent.Serializer.jsonToComponent(data.readStringFromBuffer(32767));
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeStringToBuffer(IChatComponent.Serializer.componentToJson(this.chatComponent));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleChat(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("message=\'%s\'", new Object[] {this.chatComponent});
    }

    @SideOnly(Side.CLIENT)
    public IChatComponent func_148915_c()
    {
        return this.chatComponent;
    }

    public boolean isChat()
    {
        return this.isChat;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}