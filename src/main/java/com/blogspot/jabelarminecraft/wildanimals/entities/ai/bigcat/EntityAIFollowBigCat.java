/**
    Copyright (C) 2015 by jabelar

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

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

/**
 * @author jabelar
 *
 */
public class EntityAIFollowBigCat extends EntityAIBase
{
    public final EntityBigCat thePet;
    public EntityLivingBase theOwner;
    public UUID theOwnerUUID;
    public World theWorld;
    public final double followSpeedFactor;
    public final PathNavigate petPathfinder;
    public int decisionPeriod;
    public float maxDist;
    public float minDist;
    public boolean avoidsWaterWhenNotFollowing;

    public EntityAIFollowBigCat(EntityBigCat parPet, double parFollowSpeedFactor, float parMinDist, float parMaxDist)
    {
        thePet = parPet;
        theWorld = parPet.worldObj;
        followSpeedFactor = parFollowSpeedFactor;
        petPathfinder = parPet.getNavigator();
        minDist = parMinDist;
        maxDist = parMaxDist;
        setMutexBits(0);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        if (!thePet.isTamed())
        {
            return false;
        }
        
        theOwner = thePet.getOwner();

        if (theOwner == null)
        {
//            // DEBUG
//            System.out.println("Follow AI not starting execution because the owner is null");
            return false;
        }
        else if (thePet.isSitting())
        {
            // DEBUG
            System.out.println("Follow AI not starting execution because the pet is sitting");
            return false;
        }
        else if (thePet.getDistanceSqToEntity(theOwner) < minDist * minDist)
        {
            // DEBUG
            System.out.println("Follow AI not starting execution because the owner is too close");
            return false;
        }
        else
        {
            // DEBUG
            System.out.println("Follow AI is starting execution");
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
        boolean continueExecuting = 
                !petPathfinder.noPath() 
                && thePet.getDistanceSqToEntity(theOwner) > maxDist * maxDist 
                && !thePet.isSitting();
        // DEBUG
        if (!continueExecuting) System.out.println("Continue executing = "+continueExecuting);
        return continueExecuting;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        decisionPeriod = 0;
        avoidsWaterWhenNotFollowing = thePet.getNavigator().getAvoidsWater();
        thePet.getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask()
    {
        theOwner = null;
        petPathfinder.clearPathEntity();
        thePet.getNavigator().setAvoidsWater(avoidsWaterWhenNotFollowing);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask()
    {
        thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, thePet.getVerticalFaceSpeed());

        if (!thePet.isSitting())
        {
            if (--decisionPeriod <= 0)
            {
                decisionPeriod = 10;

                if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeedFactor))
                {
                    if (!thePet.getLeashed())
                    {
                        if (thePet.getDistanceSqToEntity(theOwner) >= 144.0D)
                        {
                            int i = MathHelper.floor_double(theOwner.posX) - 2;
                            int j = MathHelper.floor_double(theOwner.posZ) - 2;
                            int k = MathHelper.floor_double(theOwner.boundingBox.minY);

                            for (int l = 0; l <= 4; ++l)
                            {
                                for (int i1 = 0; i1 <= 4; ++i1)
                                {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && World.doesBlockHaveSolidTopSurface(theWorld, i + l, k - 1, j + i1) && !theWorld.getBlock(i + l, k, j + i1).isNormalCube() && !theWorld.getBlock(i + l, k + 1, j + i1).isNormalCube())
                                    {
                                        thePet.setLocationAndAngles(i + l + 0.5F, k, j + i1 + 0.5F, thePet.rotationYaw, thePet.rotationPitch);
                                        petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}