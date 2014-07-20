package com.blogspot.jabelarminecraft.wildanimals.entities.extendedproperties;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.entities.IWildAnimalsEntity;

public class ExtendedPropertiesWildAnimals implements IExtendedEntityProperties
{
	protected Entity theEntity;
	protected World theWorld;
	
	@Override
	public void saveNBTData(NBTTagCompound parCompound) 
	{
		// DEBUG
		System.out.println("ExtendedPropertiesWildAnimals saveNBTData(), Entity = "+theEntity.getEntityId()+", client side = "+theWorld.isRemote);
		
		// good idea to keep your extended properties in a sub-compound to avoid conflicts with other
		// possible extended properties, even from other mods (like if a mod extends all EntityAnimal)
		parCompound.setTag(WildAnimals.EXT_PROPS_NAME, ((IWildAnimalsEntity)theEntity).getExtProps()); // set as a sub-compound
	}

	@Override
	public void loadNBTData(NBTTagCompound parCompound) 
	{
		// DEBUG
		System.out.println("ExtendedPropertiesWildAnimals loadNBTData(), Entity = "+theEntity.getEntityId()+", client side = "+theWorld.isRemote);

		// Get the sub-compound
		((IWildAnimalsEntity)theEntity).setExtProps((NBTTagCompound) parCompound.getTag(WildAnimals.EXT_PROPS_NAME));
	}

	@Override
	public void init(Entity entity, World world) 
	{
		// DEBUG
		System.out.println("ExtendedPropertiesWildAnimals init(), Entity = "+entity.getEntityId()+", client side = "+world.isRemote);

		theEntity = entity;
	    theWorld = world;
	}
  
}
