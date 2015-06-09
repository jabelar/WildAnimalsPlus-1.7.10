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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

public class EntityAIBegBigCat extends EntityAIBase
{
    private final EntityBigCat theBigCat;
    private EntityPlayer thePlayer;
    private final World worldObject;
    private final float minPlayerDistance;
    private int field_75384_e;

    public EntityAIBegBigCat(EntityBigCat par1EntityBigCat, float par2)
    {
        this.theBigCat = par1EntityBigCat;
        this.worldObject = par1EntityBigCat.worldObj;
        this.minPlayerDistance = par2;
        this.setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {
        this.thePlayer = this.worldObject.getClosestPlayerToEntity(this.theBigCat, this.minPlayerDistance);
        return this.thePlayer == null ? false : this.hasPlayerGotBoneInHand(this.thePlayer);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean continueExecuting()
    {
        return !this.thePlayer.isEntityAlive() ? false : (this.theBigCat.getDistanceSqToEntity(this.thePlayer) > this.minPlayerDistance * this.minPlayerDistance ? false : this.field_75384_e > 0 && this.hasPlayerGotBoneInHand(this.thePlayer));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
        this.theBigCat.setInterested(true);
        this.field_75384_e = 40 + this.theBigCat.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    @Override
	public void resetTask()
    {
        this.theBigCat.setInterested(false);
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

    /**
     * Gets if the Player has the Bone in the hand.
     */
    private boolean hasPlayerGotBoneInHand(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        return itemstack == null ? false : (!this.theBigCat.isTamed() && itemstack.getItem() == Items.bone ? true : this.theBigCat.isBreedingItem(itemstack));
    }
}