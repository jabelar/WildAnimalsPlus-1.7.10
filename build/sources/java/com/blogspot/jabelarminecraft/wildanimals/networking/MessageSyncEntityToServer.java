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

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * @author jabelar
 *
 */
public class MessageSyncEntityToServer implements IMessage 
{
    private int entityId ;
    private NBTTagCompound entitySyncDataCompound;

    public MessageSyncEntityToServer() 
    { 
    	// need this constructor
    }

    public MessageSyncEntityToServer(int parEntityId, NBTTagCompound parTagCompound) 
    {
    	entityId = parEntityId;
        entitySyncDataCompound = parTagCompound;
        // DEBUG
        System.out.println("SyncEntityToClient constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
    	entityId = ByteBufUtils.readVarInt(buf, 4);
    	entitySyncDataCompound = ByteBufUtils.readTag(buf); // this class is very useful in general for writing more complex objects
    	// DEBUG
    	System.out.println("fromBytes");
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
    	ByteBufUtils.writeVarInt(buf, entityId, 4);
    	ByteBufUtils.writeTag(buf, entitySyncDataCompound);
        // DEBUG
        System.out.println("toBytes encoded");
    }

    public static class Handler implements IMessageHandler<MessageSyncEntityToServer, IMessage> 
    {
        
        @Override
        public IMessage onMessage(MessageSyncEntityToServer message, MessageContext ctx) 
        {
        	EntityPlayer thePlayer = WildAnimals.proxy.getPlayerEntityFromContext(ctx);
        	IModEntity theEntity = (IModEntity)thePlayer.worldObj.getEntityByID(message.entityId);
        	if (theEntity != null)
        	{
        		theEntity.setSyncDataCompound(message.entitySyncDataCompound);
        		// DEBUG
        		System.out.println("MessageSyncEnitityToClient onMessage(), entity ID = "+message.entityId);
        	}
        	else
        	{
        		// DEBUG
        		System.out.println("Can't find entity with ID = "+message.entityId+" on server");
        	}
        	return null; // no response in this case
        }
    }
}
