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

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.herdanimal;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.AxisAlignedBB;

import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;

public class EntityAIHurtByTargetHerdAnimal extends EntityAITarget
{
    boolean entityCallsForHelp;
    protected EntityHerdAnimal taskOwnerHerdAnimal;

    public EntityAIHurtByTargetHerdAnimal(EntityCreature par1EntityCreature, boolean par2)
    {
        super(par1EntityCreature, false);
        entityCallsForHelp = par2;
        taskOwnerHerdAnimal = (EntityHerdAnimal)taskOwner;
        setMutexBits(1);
        
        // DEBUG
    	System.out.println("EntityAIHurtByTargetHerdAnimal constructor(), client side = "+taskOwnerHerdAnimal.worldObj.isRemote);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {	
    	if (isSuitableTarget(taskOwnerHerdAnimal.getAITarget(), false) && !taskOwnerHerdAnimal.isRearing())
    	{
        	// DEBUG
			System.out.println("EntityAIHurtByTargetHerdAnimal shouldExecute() is returning true"+", client side = "+taskOwnerHerdAnimal.worldObj.isRemote);
    		
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
    	// DEBUG
    	System.out.println("EntityAIHurtByTargetHerdAnimal startExecute()"+", client side = "+taskOwnerHerdAnimal.worldObj.isRemote);
    	taskOwnerHerdAnimal.setAttackTarget(taskOwnerHerdAnimal.getAITarget());

        if (entityCallsForHelp)
        {
            double d0 = getTargetDistance();
            List list = taskOwnerHerdAnimal.worldObj.getEntitiesWithinAABB(taskOwnerHerdAnimal.getClass(), AxisAlignedBB.getBoundingBox(taskOwner.posX, taskOwner.posY, taskOwner.posZ, taskOwner.posX + 1.0D, taskOwner.posY + 1.0D, taskOwner.posZ + 1.0D).expand(d0, 10.0D, d0));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityCreature entitycreature = (EntityCreature)iterator.next();

                if (taskOwnerHerdAnimal != entitycreature && entitycreature.getAttackTarget() == null && !entitycreature.isOnSameTeam(taskOwner.getAITarget()))
                {
                    entitycreature.setAttackTarget(taskOwnerHerdAnimal.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}