package net.minecraft.network.play.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.EnumDifficulty;

public class C15PacketClientSettings extends Packet
{
    private String lang;
    private int view;
    private EntityPlayer.EnumChatVisibility chatVisibility;
    private boolean enableColors;
    private EnumDifficulty difficulty;
    private boolean showCape;
    private static final String __OBFID = "CL_00001350";

    public C15PacketClientSettings() {}

    @SideOnly(Side.CLIENT)
    public C15PacketClientSettings(String p_i45243_1_, int p_i45243_2_, EntityPlayer.EnumChatVisibility p_i45243_3_, boolean p_i45243_4_, EnumDifficulty p_i45243_5_, boolean p_i45243_6_)
    {
        this.lang = p_i45243_1_;
        this.view = p_i45243_2_;
        this.chatVisibility = p_i45243_3_;
        this.enableColors = p_i45243_4_;
        this.difficulty = p_i45243_5_;
        this.showCape = p_i45243_6_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.lang = data.readStringFromBuffer(7);
        this.view = data.readByte();
        this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(data.readByte());
        this.enableColors = data.readBoolean();
        this.difficulty = EnumDifficulty.getDifficultyEnum(data.readByte());
        this.showCape = data.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeStringToBuffer(this.lang);
        data.writeByte(this.view);
        data.writeByte(this.chatVisibility.getChatVisibility());
        data.writeBoolean(this.enableColors);
        data.writeByte(this.difficulty.getDifficultyId());
        data.writeBoolean(this.showCape);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processClientSettings(this);
    }

    public String getLang()
    {
        return this.lang;
    }

    public int getView()
    {
        return this.view;
    }

    public EntityPlayer.EnumChatVisibility getChatVisibility()
    {
        return this.chatVisibility;
    }

    public boolean isColorsEnabled()
    {
        return this.enableColors;
    }

    public EnumDifficulty getDifficulty()
    {
        return this.difficulty;
    }

    public boolean isShowCape()
    {
        return this.showCape;
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("lang=\'%s\', view=%d, chat=%s, col=%b, difficulty=%s, cape=%b", new Object[] {this.lang, Integer.valueOf(this.view), this.chatVisibility, Boolean.valueOf(this.enableColors), this.difficulty, Boolean.valueOf(this.showCape)});
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}