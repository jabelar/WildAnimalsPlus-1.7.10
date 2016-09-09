package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler
{
    private static final Logger logger = LogManager.getLogger();
    public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
    public static final Marker logMarkerStat = MarkerManager.getMarker("NETWORK_STAT", logMarkerNetwork);
    public static final AttributeKey attrKeyConnectionState = new AttributeKey("protocol");
    public static final AttributeKey attrKeyReceivable = new AttributeKey("receivable_packets");
    public static final AttributeKey attrKeySendable = new AttributeKey("sendable_packets");
    public static final NioEventLoopGroup eventLoops = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    public static final NetworkStatistics STATISTICS = new NetworkStatistics();
    /** Whether this NetworkManager deals with the client or server side of the connection */
    private final boolean isClientSide;
    /** The queue for received, unprioritized packets that will be processed at the earliest opportunity */
    private final Queue receivedPacketsQueue = Queues.newConcurrentLinkedQueue();
    /** The queue for packets that require transmission */
    private final Queue outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
    /** The active channel */
    private Channel channel;
    /** The address of the remote party */
    private SocketAddress socketAddress;
    /** The INetHandler instance responsible for processing received packets */
    private INetHandler packetListener;
    /** The current connection state, being one of: HANDSHAKING, PLAY, STATUS, LOGIN */
    private EnumConnectionState connectionState;
    /** A String indicating why the network has shutdown. */
    private IChatComponent terminationReason;
    private boolean isEncrypted;
    private static final String __OBFID = "CL_00001240";

    public NetworkManager(boolean isClient)
    {
        this.isClientSide = isClient;
    }

    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
    {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();
        this.setConnectionState(EnumConnectionState.HANDSHAKING);
    }

    /**
     * Sets the new connection state and registers which packets this channel may send and receive
     */
    public void setConnectionState(EnumConnectionState newState)
    {
        this.connectionState = (EnumConnectionState)this.channel.attr(attrKeyConnectionState).getAndSet(newState);
        this.channel.attr(attrKeyReceivable).set(newState.func_150757_a(this.isClientSide));
        this.channel.attr(attrKeySendable).set(newState.func_150754_b(this.isClientSide));
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext p_channelInactive_1_)
    {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_)
    {
        ChatComponentTranslation chatcomponenttranslation;

        if (p_exceptionCaught_2_ instanceof TimeoutException)
        {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.timeout", new Object[0]);
        }
        else
        {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.genericReason", new Object[] {"Internal Exception: " + p_exceptionCaught_2_});
        }

        this.closeChannel(chatcomponenttranslation);
    }

    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_)
    {
        if (this.channel.isOpen())
        {
            if (p_channelRead0_2_.hasPriority())
            {
                p_channelRead0_2_.processPacket(this.packetListener);
            }
            else
            {
                this.receivedPacketsQueue.add(p_channelRead0_2_);
            }
        }
    }

    /**
     * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
     * connection state (protocol)
     */
    public void setNetHandler(INetHandler handler)
    {
        Validate.notNull(handler, "packetListener", new Object[0]);
        logger.debug("Set listener of {} to {}", new Object[] {this, handler});
        this.packetListener = handler;
    }

    /**
     * Will flush the outbound queue and dispatch the supplied Packet if the channel is ready, otherwise it adds the
     * packet to the outbound queue and registers the GenericFutureListener to fire after transmission
     */
    public void scheduleOutboundPacket(Packet inPacket, GenericFutureListener ... futureListeners)
    {
        if (this.channel != null && this.channel.isOpen())
        {
            this.flushOutboundQueue();
            this.dispatchPacket(inPacket, futureListeners);
        }
        else
        {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(inPacket, futureListeners));
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do that.
     */
    private void dispatchPacket(final Packet inPacket, final GenericFutureListener[] futureListeners)
    {
        final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
        final EnumConnectionState enumconnectionstate1 = (EnumConnectionState)this.channel.attr(attrKeyConnectionState).get();

        if (enumconnectionstate1 != enumconnectionstate && !( inPacket instanceof FMLProxyPacket))
        {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop())
        {
            if (enumconnectionstate != enumconnectionstate1 && !( inPacket instanceof FMLProxyPacket))
            {
                this.setConnectionState(enumconnectionstate);
            }

            this.channel.writeAndFlush(inPacket).addListeners(futureListeners).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.channel.eventLoop().execute(new Runnable()
            {
                private static final String __OBFID = "CL_00001241";
                public void run()
                {
                    if (enumconnectionstate != enumconnectionstate1  && !( inPacket instanceof FMLProxyPacket))
                    {
                        NetworkManager.this.setConnectionState(enumconnectionstate);
                    }

                    NetworkManager.this.channel.writeAndFlush(inPacket).addListeners(futureListeners).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    /**
     * Will iterate through the outboundPacketQueue and dispatch all Packets
     */
    private void flushOutboundQueue()
    {
        if (this.channel != null && this.channel.isOpen())
        {
            while (!this.outboundPacketsQueue.isEmpty())
            {
                NetworkManager.InboundHandlerTuplePacketListener inboundhandlertuplepacketlistener = (NetworkManager.InboundHandlerTuplePacketListener)this.outboundPacketsQueue.poll();
                this.dispatchPacket(inboundhandlertuplepacketlistener.packet, inboundhandlertuplepacketlistener.futureListeners);
            }
        }
    }

    /**
     * Checks timeouts and processes all packets received
     */
    public void processReceivedPackets()
    {
        this.flushOutboundQueue();
        EnumConnectionState enumconnectionstate = (EnumConnectionState)this.channel.attr(attrKeyConnectionState).get();

        if (this.connectionState != enumconnectionstate)
        {
            if (this.connectionState != null)
            {
                this.packetListener.onConnectionStateTransition(this.connectionState, enumconnectionstate);
            }

            this.connectionState = enumconnectionstate;
        }

        if (this.packetListener != null)
        {
            for (int i = 1000; !this.receivedPacketsQueue.isEmpty() && i >= 0; --i)
            {
                Packet packet = (Packet)this.receivedPacketsQueue.poll();
                packet.processPacket(this.packetListener);
            }

            this.packetListener.onNetworkTick();
        }

        this.channel.flush();
    }

    /**
     * Returns the socket address of the remote side. Server-only.
     */
    public SocketAddress getRemoteAddress()
    {
        return this.socketAddress;
    }

    /**
     * Closes the channel, the parameter can be used for an exit message (not certain how it gets sent)
     */
    public void closeChannel(IChatComponent message)
    {
        if (this.channel.isOpen())
        {
            this.channel.close();
            this.terminationReason = message;
        }
    }

    /**
     * True if this NetworkManager uses a memory connection (single player game). False may imply both an active TCP
     * connection or simply no active connection at all
     */
    public boolean isLocalChannel()
    {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the address and port supplied and configures
     * the channel pipeline. Returns the newly created instance.
     */
    @SideOnly(Side.CLIENT)
    public static NetworkManager provideLanClient(InetAddress p_150726_0_, int p_150726_1_)
    {
        final NetworkManager networkmanager = new NetworkManager(true);
        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(eventLoops)).handler(new ChannelInitializer()
        {
            private static final String __OBFID = "CL_00001242";
            protected void initChannel(Channel p_initChannel_1_)
            {
                try
                {
                    p_initChannel_1_.config().setOption(ChannelOption.IP_TOS, Integer.valueOf(24));
                }
                catch (ChannelException channelexception1)
                {
                    ;
                }

                try
                {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(false));
                }
                catch (ChannelException channelexception)
                {
                    ;
                }

                p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(20)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(NetworkManager.STATISTICS)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(NetworkManager.STATISTICS)).addLast("packet_handler", networkmanager);
            }
        })).channel(NioSocketChannel.class)).connect(p_150726_0_, p_150726_1_).syncUninterruptibly();
        return networkmanager;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the socket supplied and configures the channel
     * pipeline. Returns the newly created instance.
     */
    @SideOnly(Side.CLIENT)
    public static NetworkManager provideLocalClient(SocketAddress p_150722_0_)
    {
        final NetworkManager networkmanager = new NetworkManager(true);
        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(eventLoops)).handler(new ChannelInitializer()
        {
            private static final String __OBFID = "CL_00001243";
            protected void initChannel(Channel p_initChannel_1_)
            {
                p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
        })).channel(LocalChannel.class)).connect(p_150722_0_).syncUninterruptibly();
        return networkmanager;
    }

    /**
     * Adds an encoder+decoder to the channel pipeline. The parameter is the secret key used for encrypted communication
     */
    public void enableEncryption(SecretKey key)
    {
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.func_151229_a(2, key)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.func_151229_a(1, key)));
        this.isEncrypted = true;
    }

    /**
     * Returns true if this NetworkManager has an active channel, false otherwise
     */
    public boolean isChannelOpen()
    {
        return this.channel != null && this.channel.isOpen();
    }

    /**
     * Gets the current handler for processing packets
     */
    public INetHandler getNetHandler()
    {
        return this.packetListener;
    }

    /**
     * If this channel is closed, returns the exit message, null otherwise.
     */
    public IChatComponent getExitMessage()
    {
        return this.terminationReason;
    }

    /**
     * Switches the channel to manual reading modus
     */
    public void disableAutoRead()
    {
        this.channel.config().setAutoRead(false);
    }

    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Object p_channelRead0_2_)
    {
        this.channelRead0(p_channelRead0_1_, (Packet)p_channelRead0_2_);
    }

    public Channel channel()
    {
        /** The active channel */
        return channel;
    }

    static class InboundHandlerTuplePacketListener
        {
            private final Packet packet;
            private final GenericFutureListener[] futureListeners;
            private static final String __OBFID = "CL_00001244";

            public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener ... inFutureListeners)
            {
                this.packet = inPacket;
                this.futureListeners = inFutureListeners;
            }
        }
}