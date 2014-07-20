package com.blogspot.jabelarminecraft.wildanimals.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;

public class CommandConjure implements ICommand
{
	private final List aliases;
	
	protected String fullEntityName;
	protected Entity conjuredEntity;
	
	public CommandConjure()
	{
		    aliases = new ArrayList();
		    aliases.add("conjure");
		    aliases.add("conj");
	}
	
	@Override
	public int compareTo(Object o) 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "conjure";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) 
	{
		return "conjure <text>";
	}

	@Override
	public List getCommandAliases() 
	{
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] argString) 
	{
		World world = sender.getEntityWorld();
		
		if (world.isRemote)
		{
			System.out.println("Not processing on Client side");
		}
		else
		{
			System.out.println("Processing on Server side");

			if(argString.length == 0)
		    {
		    	sender.addChatMessage(new ChatComponentText("Invalid argument"));
		    	return;
		    }
			
		    sender.addChatMessage(new ChatComponentText("Conjuring: [" + argString[0] + "]"));
		    
		    fullEntityName = WildAnimals.MODID+"."+argString[0];
		    if (EntityList.stringToClassMapping.containsKey(fullEntityName))
		    {
	            conjuredEntity = EntityList.createEntityByName(fullEntityName, world);
	    		conjuredEntity.setPosition(sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posY, 
	    				sender.getPlayerCoordinates().posZ);
	    		world.spawnEntityInWorld(conjuredEntity);
		    }
		    else
		    {
		    	sender.addChatMessage(new ChatComponentText("Entity not found"));
		    }  
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		// TODO Auto-generated method stub
		return false;
	}

}
