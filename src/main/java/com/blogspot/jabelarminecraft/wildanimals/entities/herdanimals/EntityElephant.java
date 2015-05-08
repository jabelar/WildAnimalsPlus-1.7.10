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

package com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityElephant extends EntityHerdAnimal
{	
	public EntityElephant(World par1World) 
	{
		super(par1World);
	    
		// DEBUG
        System.out.println("EntityElephant constructor()");

        initSyncDataCompound();
        setSize(width*getScaleFactor(), height*getScaleFactor());
	}
	
    @Override
	public void initSyncDataCompound()
    {
    	super.initSyncDataCompound();
    	syncDataCompound.setFloat("scaleFactor", 2.0F); // elephants are big!
    }

    @Override
	protected void applyEntityAttributes()
    {
    	// DEBUG
    	System.out.println("EntityElephant applyEnityAttributes()");
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D); // elephants are tough
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D); // can't knockback an elephant
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
	public boolean isAIEnabled()
    {
        return true;
    }
	
    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected String getLivingSound()
    {
        return "wildanimals:mob.elephant.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return "wildanimals:mob.elephant.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return "wildanimals:mob.elephant.hurt";
    }

}
