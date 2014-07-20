package com.blogspot.jabelarminecraft.wildanimals.entities.eggs;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.items.ItemWildAnimalSpawnEggThrowable;

public class EntityWildAnimalsEgg extends EntityThrowable
{  
    protected String entityToSpawnName = "";
    protected EntityAnimal entityToSpawn;
    protected EntityItemWildAnimalsEgg entityItem; // tracks throwable aid rendering
//    protected int colorBase;
//    protected int colorSpots;

    public EntityWildAnimalsEgg(World par1World)
    {
        super(par1World);
    }

    public EntityWildAnimalsEgg(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }
    
    public void setEntityToSpawn(String parEntityToSpawnName)
    {
        entityToSpawnName = parEntityToSpawnName;
    }
    
    public void setEntityItem(ItemStack parItemStack)
    {
    	// DEBUG
    	System.out.println("EntityWildAnimalsEgg setEntityItem with Stack "+parItemStack.toString());
        entityItem = new EntityItemWildAnimalsEgg(worldObj, posX, posY, posZ, parItemStack);
        worldObj.spawnEntityInWorld(entityItem);

    }
    
    public void setEntityItem(ItemWildAnimalSpawnEggThrowable parItem)
    {
    	// DEBUG
    	System.out.println("EntityWildAnimalsEgg setEntityItem "+parItem.toString());
        entityItem = new EntityItemWildAnimalsEgg(worldObj, posX, posY, posZ, new ItemStack(parItem));
        worldObj.spawnEntityInWorld(entityItem);
    }
    
    public String getEntityToSpawn()
    {
        return entityToSpawnName;
    }
    
//    public void setColors(int parColorBase, int parColorSpots) 
//    {
//    	colorBase = parColorBase;
//    	colorSpots = parColorSpots;
//    }
//    
//    public int getColorBase()
//    {
//    	return colorBase;
//    }
//    
//    public int getColorSpots()
//    {
//    	return colorSpots;
//    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
    {
        if (par1MovingObjectPosition.entityHit != null)
        {
            par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
        }

        if (!worldObj.isRemote) // never spawn entity on client side
        {
                entityToSpawn = (EntityAnimal) EntityList.createEntityByName(entityToSpawnName, worldObj);
                entityToSpawn.setGrowingAge(-24000);
                entityToSpawn.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
                worldObj.spawnEntityInWorld(entityToSpawn);
        }

        for (int j = 0; j < 8; ++j)
        {
            worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!worldObj.isRemote)
        {
        	entityItem.setDead();
            setDead();
        }
    }
    
    @Override
	public void onUpdate()
    {
    	super.onUpdate();
    	if (entityItem != null) // first tick might be null
    	{
    		// entity item needs to track entity
        	entityItem.setPosition(posX, posY, posZ);
        	entityItem.motionX = motionX;
        	entityItem.motionY = motionY;
        	entityItem.motionZ = motionZ;
    	}
    }   
}