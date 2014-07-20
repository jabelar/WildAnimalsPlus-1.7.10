package com.blogspot.jabelarminecraft.wildanimals.networking.entities;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;

import com.blogspot.jabelarminecraft.wildanimals.networking.PacketTypeClient;

import cpw.mods.fml.relauncher.Side;

// this class is intended to be sent from server to client to keep custom entities synced
public class ProcessPacketServerSide
{
	
	public ProcessPacketServerSide()
	{
		// don't need anything here
	}

	// *****************************************
	// Received By Server Packet Processing
	// *****************************************

	public static void processPacketOnServer(ByteBuf parBB, Side parSide, EntityPlayerMP parPlayer) throws IOException
	{
		if (parSide == Side.SERVER) // packet received on server side
		{
			// DEBUG
			System.out.println("Received Packet on Server Side from Player = "+parPlayer.getEntityId());

			ByteBufInputStream bbis = new ByteBufInputStream(parBB);
			
			// process data stream
			// first read packet type
			int packetTypeID = bbis.readInt();

			// DEBUG
			System.out.println("Packet type ID = "+packetTypeID);
		
			switch (packetTypeID)
			{
				case PacketTypeClient.TEST:
				{
					// DEBUG
					System.out.println("Test packet received");
					
					int testVal = bbis.readInt();
					
					// DEBUG
					System.out.println("Test payload value = "+testVal);
					
					break ;
				}
			
			}
			
			// don't forget to close stream to avoid memory leak
			bbis.close();			
		}
	}
}