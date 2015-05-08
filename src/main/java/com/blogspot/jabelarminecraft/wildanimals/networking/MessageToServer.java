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

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * @author jabelar
 *
 */
public class MessageToServer implements IMessage 
{
    
    private String text;

    public MessageToServer() 
    { 
    	// need this constructor
    }

    public MessageToServer(String parText) 
    {
        text = parText;
        // DEBUG
        System.out.println("MyMessage constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
        text = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
    	// DEBUG
    	System.out.println("fromBytes = "+text);
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
        ByteBufUtils.writeUTF8String(buf, text);
        // DEBUG
        System.out.println("toBytes = "+text);
    }

    public static class Handler implements IMessageHandler<MessageToServer, IMessage> 
    {
        
        @Override
        public IMessage onMessage(MessageToServer message, MessageContext ctx) 
        {
            System.out.println(String.format("Received %s from %s", message.text, WildAnimals.proxy.getPlayerEntityFromContext(ctx).getDisplayName()));
            return null; // no response in this case
        }
    }
}
