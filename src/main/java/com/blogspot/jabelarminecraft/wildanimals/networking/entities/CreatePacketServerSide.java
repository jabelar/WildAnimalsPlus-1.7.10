package com.blogspot.jabelarminecraft.wildanimals.networking.entities;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.IOException;

import net.minecraft.entity.Entity;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.entities.IWildAnimalsEntity;
import com.blogspot.jabelarminecraft.wildanimals.networking.PacketTypeServer;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

// this class is intended to be sent from server to client to keep custom entities synced
public class CreatePacketServerSide
{
	
	public CreatePacketServerSide()
	{
		// don't need anything here
	}

	// *****************************************
	// Server To Client Packet Creation
	// *****************************************
	
	// method to send sync packet from server to client, should send whenever a custom field is set (from setter method)
	// send sync packet to client, if on server side
	public static void sendToAll(FMLProxyPacket parPacket)
	{
		WildAnimals.channel.sendToAll(parPacket);
	}

	 
	public static void sendS2CEntityNBTSync(Entity parEntity) throws IOException
	{
			sendToAll(createEntityNBTPacket(parEntity));
	}

	public static FMLProxyPacket createEntityNBTPacket(Entity parEntity) throws IOException
	{
		// DEBUG
		System.out.println("Sending Entity NBT Sync Packet on Server Side");

		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());

		// create payload by writing to data stream
		// first identity packet type
		bbos.writeInt(PacketTypeServer.ENTITY_SYNC_NBT);
		
		// write entity instance id (not the class registry id!)
		bbos.writeInt(parEntity.getEntityId());

		((IWildAnimalsEntity) parEntity).getExtPropsToBuffer(bbos);;

		// put payload into a packet		
		FMLProxyPacket thePacket = new FMLProxyPacket(bbos.buffer(), WildAnimals.NETWORK_CHANNEL_NAME);

		// don't forget to close stream to avoid memory leak
		bbos.close();
		
		return thePacket;
	}
}
