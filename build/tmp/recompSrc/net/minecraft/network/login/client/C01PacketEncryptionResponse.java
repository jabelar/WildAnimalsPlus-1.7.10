package net.minecraft.network.login.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.util.CryptManager;

public class C01PacketEncryptionResponse extends Packet
{
    private byte[] field_149302_a = new byte[0];
    private byte[] field_149301_b = new byte[0];
    private static final String __OBFID = "CL_00001380";

    public C01PacketEncryptionResponse() {}

    @SideOnly(Side.CLIENT)
    public C01PacketEncryptionResponse(SecretKey p_i45271_1_, PublicKey p_i45271_2_, byte[] p_i45271_3_)
    {
        this.field_149302_a = CryptManager.encryptData(p_i45271_2_, p_i45271_1_.getEncoded());
        this.field_149301_b = CryptManager.encryptData(p_i45271_2_, p_i45271_3_);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.field_149302_a = readBlob(data);
        this.field_149301_b = readBlob(data);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        writeBlob(data, this.field_149302_a);
        writeBlob(data, this.field_149301_b);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginServer handler)
    {
        handler.processEncryptionResponse(this);
    }

    public SecretKey func_149300_a(PrivateKey key)
    {
        return CryptManager.decryptSharedKey(key, this.field_149302_a);
    }

    public byte[] func_149299_b(PrivateKey p_149299_1_)
    {
        return p_149299_1_ == null ? this.field_149301_b : CryptManager.decryptData(p_149299_1_, this.field_149301_b);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerLoginServer)handler);
    }
}