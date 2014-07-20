package com.blogspot.jabelarminecraft.wildanimals.networking.entities;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.IWildAnimalsEntity;
import com.blogspot.jabelarminecraft.wildanimals.networking.PacketTypeServer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// this class is intended to be sent from server to client to keep custom entities synced
public class ProcessPacketClientSide
{	
	public ProcessPacketClientSide()
	{
		// don't need anything here
	}

	// *****************************************
	// Received By Client Packet Processing
	// *****************************************

	@SideOnly(Side.CLIENT)
	public static void processPacketOnClient(ByteBuf parBB, Side parSide) throws IOException
	{
		if (parSide == Side.CLIENT) // packet received on client side
		{
			// DEBUG
			System.out.println("Received ProcessPacketClientSide on Client Side");

			World theWorld = Minecraft.getMinecraft().theWorld;
			
			// create an input stream copy for methods that don't use ByteBufUtils
			ByteBufInputStream bbis = new ByteBufInputStream(parBB);
			
			// process data stream
			// first read packet type
			int packetTypeID = bbis.readInt();
			
			switch (packetTypeID)
			{
				case PacketTypeServer.ENTITY_SYNC_NBT: // a packet sent to sync entity via extended props NBT
				{
					// find entity instance
					int entityID = bbis.readInt();

					// DEBUG
					System.out.println("Entity ID = "+entityID);

					IWildAnimalsEntity foundEntity = (IWildAnimalsEntity) getEntityByID(entityID, theWorld);

					// DEBUG
					if (foundEntity != null)
					{
						System.out.println("Entity Class Name = "+foundEntity.getClass().getSimpleName());
						// write tag information from packet
						foundEntity.setExtPropsFromBuffer(bbis);
					}
					else
					{
						System.out.println("***ERROR*** Entity Class Name = null");
					}
					break;
				}
			}
			
			// don't forget to close stream to avoid memory leak
			bbis.close();			
		}
	}
	
	// some helper functions
	public static Entity getEntityByID(int entityID, World world)        
	{         
		for(Object o: world.getLoadedEntityList())                
		{                        
			if(((Entity)o).getEntityId() == entityID)                        
			{                                
				System.out.println("Found the entity");                                
				return ((Entity)o);                        
			}                
		}                
		return null;        
	}	
}