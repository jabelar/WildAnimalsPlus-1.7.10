package net.minecraft.entity.passive;

import net.minecraft.entity.player.EntityPlayer;

public class EntityPassiveAccessor 

{
	public static int getPassiveXP(EntityAnimal theEntity, EntityPlayer thePlayer) 
	{
		// DEBUG
		System.out.println("Last attack by player: Accessed Passive Entity XP");
		return theEntity.getExperiencePoints(thePlayer);
	}
}
