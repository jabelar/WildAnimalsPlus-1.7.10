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

package com.blogspot.jabelarminecraft.wildanimals.networking;

import java.io.IOException;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.networking.entities.ProcessPacketClientSide;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

// Remember "client" run configuration includes server side execution too
public class ClientPacketHandler extends ServerPacketHandler
{
	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent event) throws IOException 
	{
		channelName = event.packet.channel();

		if (channelName.equals(WildAnimals.NETWORK_CHANNEL_NAME))
		{
			// DEBUG
			System.out.println("Client received packet from server");
			
			ProcessPacketClientSide.processPacketOnClient(event.packet.payload(), event.packet.getTarget());
		}
	}		
}
