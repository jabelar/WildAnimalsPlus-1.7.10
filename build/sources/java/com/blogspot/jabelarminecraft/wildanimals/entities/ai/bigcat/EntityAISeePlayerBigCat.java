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

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

public class EntityAISeePlayerBigCat extends EntityAIBase
{
    private final EntityBigCat theBigCat;
    private EntityPlayer thePlayer;
    private final World worldObject;
    private final double minPlayerDistance;
    private int field_75384_e;
    // private static final String __OBFID = "CL_00001576";

    public EntityAISeePlayerBigCat(EntityBigCat par1EntityBigCat, double d)
    {
        this.theBigCat = par1EntityBigCat;
        this.worldObject = par1EntityBigCat.worldObj;
        this.minPlayerDistance = d;
        // this.setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {
        this.thePlayer = this.worldObject.getClosestPlayerToEntity(this.theBigCat, this.minPlayerDistance);
        return this.thePlayer != null ;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean continueExecuting()
    {
        return !this.thePlayer.isEntityAlive() ? false : (this.theBigCat.getDistanceSqToEntity(this.thePlayer) <= this.minPlayerDistance * this.minPlayerDistance);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
        this.theBigCat.setInterested(true);
        this.field_75384_e = 40 + this.theBigCat.getRNG().nextInt(40);
    	this.theBigCat.setAngry(true);
    	// target the player
        this.theBigCat.setAttackTarget(this.thePlayer);
        // look at player
    }

    /**
     * Resets the task
     */
    @Override
	public void resetTask()
    {
        this.theBigCat.setInterested(false);
        this.theBigCat.setAngry(false);
        this.theBigCat.setAttackTarget(null);
        this.thePlayer = null;
    }

    /**
     * Updates the task
     */
    @Override
	public void updateTask()
    {
        this.theBigCat.getLookHelper().setLookPosition(this.thePlayer.posX, this.thePlayer.posY + this.thePlayer.getEyeHeight(), this.thePlayer.posZ, 10.0F, this.theBigCat.getVerticalFaceSpeed());
        --this.field_75384_e;
    }

}