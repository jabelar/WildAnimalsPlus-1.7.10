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

package com.blogspot.jabelarminecraft.wildanimals.entities.eggs;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityWildAnimalsEgg extends EntityThrowable
{  
    protected String entityToSpawnName = "";
    protected EntityAnimal entityToSpawn;
    protected int colorBase;
    protected int colorSpots;

    public EntityWildAnimalsEgg(World par1World)
    {
        super(par1World);
    }

    public EntityWildAnimalsEgg(World par1World, EntityLivingBase par2EntityLivingBase, int parColorBase, int parColorSpots)
    {
        super(par1World, par2EntityLivingBase);
        colorBase = parColorBase;
        colorSpots = parColorSpots;
    }
    
    public void setEntityToSpawn(String parEntityToSpawnName)
    {
        entityToSpawnName = parEntityToSpawnName;
    }
    
    public String getEntityToSpawn()
    {
        return entityToSpawnName;
    }

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
            setDead();
        }
    }

    public int getColorBase()
    {
    	return colorBase;
    }
    
    public int getColorSpots()
    {
		return colorSpots;  	
    }
}