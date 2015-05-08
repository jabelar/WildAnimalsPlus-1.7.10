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

package com.blogspot.jabelarminecraft.wildanimals.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityJaguar;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLion;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLynx;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.eggs.EntityItemWildAnimalsEgg;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityElephant;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelBigCat;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelElephant;
import com.blogspot.jabelarminecraft.wildanimals.models.ModelSerpent;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderBigCat;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderHerdAnimal;
import com.blogspot.jabelarminecraft.wildanimals.renderers.RenderSerpent;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
	{
		// DEBUG
        System.out.println("on Client side");
        
		// do common stuff
		super.fmlLifeCycleEvent(event);

        // do client-specific stuff
        registerRenderers();
	}
	@Override
	public void fmlLifeCycleEvent(FMLInitializationEvent event)
	{
		// DEBUG
        System.out.println("on Client side");

        // do common stuff
		super.fmlLifeCycleEvent(event);

		// do client-specific stuff
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
	{
		// DEBUG
        System.out.println("on Client side");

        // do common stuff
		super.fmlLifeCycleEvent(event);

		// do client-specific stuff
	}

	public void registerRenderers() 
    {
        // the float parameter passed to the Render class is the shadow size for the entity
      
		// generic spawn egg
	    RenderingRegistry.registerEntityRenderingHandler(EntityItemWildAnimalsEgg.class, new RenderItem());

        // Big cats
	    RenderingRegistry.registerEntityRenderingHandler(EntityTiger.class, new RenderBigCat(new ModelBigCat(), new ModelBigCat(), 
	    		0.5F,
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger.png"), 
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_tame.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_angry.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png"))); // not sure about second model
	    RenderingRegistry.registerEntityRenderingHandler(EntityManEatingTiger.class, new RenderBigCat(new ModelBigCat(), new ModelBigCat(), 
	    		0.5F,
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger.png"), 
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_tame.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_angry.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png"))); // not sure about second model
	    RenderingRegistry.registerEntityRenderingHandler(EntityLion.class, new RenderBigCat(new ModelBigCat(), new ModelBigCat(), 
	    		0.5F,
	    		new ResourceLocation("wildanimals:textures/entity/lion/lion.png"), 
	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_tame.png"),
	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_angry.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png"))); // not sure about second model
	    RenderingRegistry.registerEntityRenderingHandler(EntityLynx.class, new RenderBigCat(new ModelBigCat(), new ModelBigCat(), 
	    		0.5F,
	    		new ResourceLocation("wildanimals:textures/entity/lion/lion.png"), 
	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_tame.png"),
	    		new ResourceLocation("wildanimals:textures/entity/lion/lion_angry.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png"))); // not sure about second model
	    RenderingRegistry.registerEntityRenderingHandler(EntityJaguar.class, new RenderBigCat(new ModelBigCat(), new ModelBigCat(), 
	    		0.5F,
	    		new ResourceLocation("wildanimals:textures/entity/panther/panther.png"), 
	    		new ResourceLocation("wildanimals:textures/entity/panther/panther_tame.png"),
	    		new ResourceLocation("wildanimals:textures/entity/panther/panther_angry.png"),
	    		new ResourceLocation("wildanimals:textures/entity/tiger/tiger_collar.png"))); // not sure about second model
	    
	    // Herd animals
	    RenderingRegistry.registerEntityRenderingHandler(EntityElephant.class, new RenderHerdAnimal(new ModelElephant(), 0.5F));

	    // Serpents
	    RenderingRegistry.registerEntityRenderingHandler(EntitySerpent.class, new RenderSerpent(new ModelSerpent(), 0.0F));

	    // Birds of Prey
	    RenderingRegistry.registerEntityRenderingHandler(EntityBirdOfPrey.class, new RenderBirdOfPrey(new ModelBirdOfPrey(), 0.5F));
    }
	
    @Override
    public void sendMessageToPlayer(ChatComponentText msg) 
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
    }
    
	/*	 
	 * Thanks to CoolAlias for this tip!
	 */
	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) 
    {
        // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
        // your packets will not work because you will be getting a client
        // player even when you are on the server! Sounds absurd, but it's true.

        // Solution is to double-check side before returning the player:
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntityFromContext(ctx));
    }
    

//    
//    @Override
//	public void fmlLifeCycleEvent(FMLServerStartingEvent event) 
//    {
//		// DEBUG
//        System.out.println("on Client side");
//        
//	    event.registerServerCommand(new CommandConjure());
//	}
}