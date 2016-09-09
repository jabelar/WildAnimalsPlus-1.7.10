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

package com.blogspot.jabelarminecraft.wildanimals;

import com.blogspot.jabelarminecraft.wildanimals.proxy.CommonProxy;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class FMLEventHandler 
{
	/*
	 * Common events
	 */

	// events in the cpw.mods.fml.common.event package are actually handled with
	// @EventHandler annotation in the main mod class or the proxies.
	
	/*
	 * Game input events
	 */

//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(InputEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(KeyInputEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(MouseInputEvent event)
//	{
//
//	}
	
	/*
	 * Config events
	 */
	 @SubscribeEvent
	 public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) 
	 {
	     if(event.modID.equals(WildAnimals.MODID))
	     {
	    	 WildAnimals.config.save();
	         CommonProxy.syncConfig();
	     }
	 }
	   
	/*
	 * Player events
	 */

//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(PlayerEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(ItemCraftedEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(ItemPickupEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(ItemSmeltedEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(PlayerChangedDimensionEvent event)
//	{
//		
//	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerLoggedInEvent event)
	{
		if (event.player.getDisplayName()=="MistMaestro")
		{
			// DEBUG
			System.out.println("Welcome Master!");
		}
		
	}

//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(PlayerLoggedOutEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(PlayerRespawnEvent event)
//	{
//
//	}

	/*
	 * Tick events
	 */

//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(ClientTickEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(PlayerTickEvent event)
//	{
// 
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(RenderTickEvent event)
//	{
//		
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(WorldTickEvent event)
//	{
//		// force update of beanstalk block
//		// world.scheduleBlockUpdate(x, y, z, block, block.tickRate(world))
//	}
//
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onEvent(ServerTickEvent event)
//	{
//		
//	}
}
