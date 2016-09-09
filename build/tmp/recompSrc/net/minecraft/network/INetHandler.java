package net.minecraft.network;

import net.minecraft.util.IChatComponent;

public interface INetHandler
{
    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    void onDisconnect(IChatComponent reason);

    /**
     * Allows validation of the connection state transition. Parameters: from, to (connection state). Typically throws
     * IllegalStateException or UnsupportedOperationException if validation fails
     */
    void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState);

    /**
     * For scheduled network tasks. Used in NetHandlerPlayServer to send keep-alive packets and in NetHandlerLoginServer
     * for a login-timeout
     */
    void onNetworkTick();
}