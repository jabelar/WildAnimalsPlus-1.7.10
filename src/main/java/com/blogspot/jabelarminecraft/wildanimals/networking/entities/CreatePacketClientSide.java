/**
    Copyright (C) 2014 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
*/

package com.blogspot.jabelarminecraft.wildanimals.networking.entities;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.IOException;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.networking.PacketTypeClient;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

// this class is intended to be sent from server to client to keep custom entities synced
public class CreatePacketClientSide
{	
	public CreatePacketClientSide()
	{
		// don't need anything here
	}

	// *****************************************
	// Client to Server Packet Creation
	// *****************************************

	public static FMLProxyPacket createClientToServerTestPacket(int parTestValue) throws IOException
	{
			// DEBUG
			System.out.println("Sending ProcessPacketClientSide on Client Side");

			ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

			// create payload by writing to data stream
			// first identity packet type
			bbos.writeInt(PacketTypeClient.TEST);
			
			// write test data
			bbos.writeInt(parTestValue);

			// put payload into a packet		
			FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), WildAnimals.NETWORK_CHANNEL_NAME);

			// don't forget to close stream to avoid memory leak
			bbos.close();
			
			return thePacket;
	}
	
	public static void sendToServer(FMLProxyPacket parPacket)
	{
		WildAnimals.channel.sendToServer(parPacket);
	}
	 
	public static void sendTestPacket(int parTestData)
	{
		try 
		{
			sendToServer(createClientToServerTestPacket(parTestData));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}