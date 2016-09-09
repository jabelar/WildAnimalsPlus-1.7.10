package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer
{
    private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private final byte[] field_147330_e = new byte[4];
    private final MinecraftServer server;
    public final NetworkManager networkManager;
    private NetHandlerLoginServer.LoginState currentLoginState;
    /** How long has player been trying to login into the server. */
    private int connectionTimer;
    private GameProfile loginGameProfile;
    private String serverId;
    private SecretKey secretKey;
    private static final String __OBFID = "CL_00001458";

    public NetHandlerLoginServer(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_)
    {
        this.currentLoginState = NetHandlerLoginServer.LoginState.HELLO;
        this.serverId = "";
        this.server = p_i45298_1_;
        this.networkManager = p_i45298_2_;
        RANDOM.nextBytes(this.field_147330_e);
    }

    /**
     * For scheduled network tasks. Used in NetHandlerPlayServer to send keep-alive packets and in NetHandlerLoginServer
     * for a login-timeout
     */
    public void onNetworkTick()
    {
        if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT)
        {
            this.func_147326_c();
        }

        if (this.connectionTimer++ == FMLNetworkHandler.LOGIN_TIMEOUT)
        {
            this.closeConnection("Took too long to log in");
        }
    }

    public void closeConnection(String reason)
    {
        try
        {
            logger.info("Disconnecting " + this.func_147317_d() + ": " + reason);
            ChatComponentText chatcomponenttext = new ChatComponentText(reason);
            this.networkManager.scheduleOutboundPacket(new S00PacketDisconnect(chatcomponenttext), new GenericFutureListener[0]);
            this.networkManager.closeChannel(chatcomponenttext);
        }
        catch (Exception exception)
        {
            logger.error("Error whilst disconnecting player", exception);
        }
    }

    public void func_147326_c()
    {
        if (!this.loginGameProfile.isComplete())
        {
            this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
        }

        String s = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);

        if (s != null)
        {
            this.closeConnection(s);
        }
        else
        {
            this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
            this.networkManager.scheduleOutboundPacket(new S02PacketLoginSuccess(this.loginGameProfile), new GenericFutureListener[0]);
            FMLNetworkHandler.fmlServerHandshake(this.server.getConfigurationManager(), this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
        }
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
        logger.info(this.func_147317_d() + " lost connection: " + reason.getUnformattedText());
    }

    public String func_147317_d()
    {
        return this.loginGameProfile != null ? this.loginGameProfile.toString() + " (" + this.networkManager.getRemoteAddress().toString() + ")" : String.valueOf(this.networkManager.getRemoteAddress());
    }

    /**
     * Allows validation of the connection state transition. Parameters: from, to (connection state). Typically throws
     * IllegalStateException or UnsupportedOperationException if validation fails
     */
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState)
    {
        Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.ACCEPTED || this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected change in protocol", new Object[0]);
        Validate.validState(newState == EnumConnectionState.PLAY || newState == EnumConnectionState.LOGIN, "Unexpected protocol " + newState, new Object[0]);
    }

    public void processLoginStart(C00PacketLoginStart packetIn)
    {
        Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet", new Object[0]);
        this.loginGameProfile = packetIn.getProfile();

        if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel())
        {
            this.currentLoginState = NetHandlerLoginServer.LoginState.KEY;
            this.networkManager.scheduleOutboundPacket(new S01PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.field_147330_e), new GenericFutureListener[0]);
        }
        else
        {
            this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
        }
    }

    public void processEncryptionResponse(C01PacketEncryptionResponse packetIn)
    {
        Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet", new Object[0]);
        PrivateKey privatekey = this.server.getKeyPair().getPrivate();

        if (!Arrays.equals(this.field_147330_e, packetIn.func_149299_b(privatekey)))
        {
            throw new IllegalStateException("Invalid nonce!");
        }
        else
        {
            this.secretKey = packetIn.func_149300_a(privatekey);
            this.currentLoginState = NetHandlerLoginServer.LoginState.AUTHENTICATING;
            this.networkManager.enableEncryption(this.secretKey);
            (new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet())
            {
                private static final String __OBFID = "CL_00001459";
                public void run()
                {
                    GameProfile gameprofile = NetHandlerLoginServer.this.loginGameProfile;

                    try
                    {
                        String s = (new BigInteger(CryptManager.getServerIdHash(NetHandlerLoginServer.this.serverId, NetHandlerLoginServer.this.server.getKeyPair().getPublic(), NetHandlerLoginServer.this.secretKey))).toString(16);
                        NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID)null, gameprofile.getName()), s);

                        if (NetHandlerLoginServer.this.loginGameProfile != null)
                        {
                            NetHandlerLoginServer.logger.info("UUID of player " + NetHandlerLoginServer.this.loginGameProfile.getName() + " is " + NetHandlerLoginServer.this.loginGameProfile.getId());
                            NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        }
                        else if (NetHandlerLoginServer.this.server.isSinglePlayer())
                        {
                            NetHandlerLoginServer.logger.warn("Failed to verify username but will let them in anyway!");
                            NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
                            NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        }
                        else
                        {
                            NetHandlerLoginServer.this.closeConnection("Failed to verify username!");
                            NetHandlerLoginServer.logger.error("Username \'" + NetHandlerLoginServer.this.loginGameProfile.getName() + "\' tried to join with an invalid session");
                        }
                    }
                    catch (AuthenticationUnavailableException authenticationunavailableexception)
                    {
                        if (NetHandlerLoginServer.this.server.isSinglePlayer())
                        {
                            NetHandlerLoginServer.logger.warn("Authentication servers are down but will let them in anyway!");
                            NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
                            NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        }
                        else
                        {
                            NetHandlerLoginServer.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
                            NetHandlerLoginServer.logger.error("Couldn\'t verify username because servers are unavailable");
                        }
                    }
                }
            }).start();
        }
    }

    protected GameProfile getOfflineProfile(GameProfile original)
    {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
        return new GameProfile(uuid, original.getName());
    }

    static enum LoginState
    {
        HELLO,
        KEY,
        AUTHENTICATING,
        READY_TO_ACCEPT,
        ACCEPTED;

        private static final String __OBFID = "CL_00001463";
    }
}