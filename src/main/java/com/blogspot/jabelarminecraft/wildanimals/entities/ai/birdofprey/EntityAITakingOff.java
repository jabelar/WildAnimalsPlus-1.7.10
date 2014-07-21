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

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

public class EntityAITakingOff extends EntityAIBase
{
    protected EntityBirdOfPrey theEntity;
    protected World worldObject;
    protected double anchorX;
    protected double anchorY;
    protected double anchorZ;

    public EntityAITakingOff(EntityBirdOfPrey par1EntityBirdOfPrey)
    {
        theEntity = par1EntityBirdOfPrey;
        worldObject = theEntity.worldObj;
        anchorX = theEntity.getAnchorX();
        anchorY = theEntity.getAnchorY();
        anchorZ = theEntity.getAnchorZ();
        
        setMutexBits(1); // see tutorial at www.jabelarminecraft.blogspot.com

        // DEBUG
    	System.out.println("EntityAIPerched constructor(), isClientWorld = "+worldObject.isRemote);
    }

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return false;
	}
	
    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
    	// DEBUG
    	System.out.println("AIPerched startExecute(), state = "+theEntity.getState()+", isClientWorld = "+worldObject.isRemote);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean continueExecuting()
    {
    	Boolean continueExecuting = true; 
    	// do code to see if continuation should end here
    	
    	// clean up if finished execution
    	if (!continueExecuting)
    	{
    	}
    	
    	// DEBUG
    	System.out.println("AIPerched continueExecuting = "+continueExecuting+", isClientWorld = "+worldObject.isRemote);
    	return (continueExecuting);
    }
}
