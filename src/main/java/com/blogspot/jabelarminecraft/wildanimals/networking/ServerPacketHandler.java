package com.blogspot.jabelarminecraft.wildanimals.networking;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.networking.entities.ProcessPacketServerSide;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class ServerPacketHandler 
{
	protected String channelName;
	protected EntityPlayerMP thePlayer;
	
	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) throws IOException 
	{
		channelName = event.packet.channel();
		
		// Thanks to GoToLink for helping figure out how to get player entity
		NetHandlerPlayServer theNetHandlerPlayServer = (NetHandlerPlayServer)event.handler;
		thePlayer = theNetHandlerPlayServer.playerEntity;
		
		// if you want the server (the configurationManager is useful as it has player lists and such
		// you can use something like
		// MinecraftServer server MinecraftServer.getServer();

		if (channelName.equals(WildAnimals.NETWORK_CHANNEL_NAME))
		{
			// DEBUG
			System.out.println("Server received packet from player = "+thePlayer.getEntityId());

			ProcessPacketServerSide.processPacketOnServer(event.packet.payload(), event.packet.getTarget(), thePlayer);
		}
	}
}
