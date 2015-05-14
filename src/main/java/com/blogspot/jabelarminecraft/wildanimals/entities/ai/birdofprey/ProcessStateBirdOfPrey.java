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

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

/**
 * @author jabelar
 *
 */
public class ProcessStateBirdOfPrey
{
    EntityBirdOfPrey theBird;
    
    public ProcessStateBirdOfPrey(EntityBirdOfPrey parBirdOfPrey)
    {
        theBird = parBirdOfPrey;
    }

    public void updateAITick()
    {
//        // DEBUG
//        System.out.println("State = "+getState());
        
        switch (theBird.getState())
        {
        case AIStates.STATE_PERCHED:
            processPerched();
            break;
        case AIStates.STATE_TAKING_OFF:
            processTakingOff();
            break;
        case AIStates.STATE_SOARING:
            processSoaring();
            break;
        case AIStates.STATE_DIVING:
            processDiving();
            break;
        case AIStates.STATE_LANDING:
            processLanding();
            break;
        case AIStates.STATE_TRAVELLING:
            processTravelling();
            break;
        case AIStates.STATE_ATTACKING:
            processAttacking();
            break;
        case AIStates.STATE_SOARING_TAMED:
            processAttacking();
            break;
        case AIStates.STATE_PERCHED_TAMED:
            processAttacking();
            break;
        default:
            // DEBUG
            System.out.println("Unknown state");
            break;
                
        }

    }

    /**
     * 
     */
    protected void processLanding() 
    {
    }

    /**
     * 
     */
    protected void processDiving() 
    {
        theBird.motionX = theBird.getAnchorX() - theBird.posX;
        theBird.motionZ = theBird.getAnchorZ() - theBird.posZ;
        theBird.motionY = -1.0D;
    }
    
//  protected MovingObjectPosition isSomethingWithinReach()
//  {
//      
//  }
//
    /**
     * 
     */
    protected void processTakingOff() 
    {
        // climb to soaring height
        if (theBird.posY < theBird.getSoarHeight())
        {
            theBird.motionY = 0.1D;
        }

        moveForward(1.0D);

        // turn
        if (theBird.getSoarClockwise())
        {
            theBird.rotationYaw += theBird.turnRate;
        }
        else
        {
            theBird.rotationYaw -= theBird.turnRate;
        }
    }

    /**
     * 
     */
    protected void processPerched() 
    {
        stopMoving();
    }
    
    protected void processSoaring()
    {
        // drift down slowly
        theBird.motionY = -0.01D;

        moveForward(1.0D);
        
        // turn
        if (theBird.isTamed())
        {
            // turn towards owner
            // got the dot product idea from https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
            Vec3 vecToOwner = Vec3.createVectorHelper(
                    theBird.getOwner().posX - theBird.posX, 
                    0, 
                    theBird.getOwner().posZ - theBird.posZ).normalize();
            if (theBird.getLookVec().dotProduct(vecToOwner) > 0.0D)
            {
                theBird.rotationYaw -= theBird.turnRate;
            }
            else
            {
                theBird.rotationYaw += theBird.turnRate;
            }
        }
        else
        {
            if (theBird.getSoarClockwise())
            {
                theBird.rotationYaw += theBird.turnRate;
            }
            else
            {
                theBird.rotationYaw -= theBird.turnRate;
            }
        }
    }
    
    protected void processTravelling()
    {
        // climb to soaring height
        if (theBird.posY < theBird.getSoarHeight())
        {
            theBird.motionY = 0.1D;
        }

        moveForward(1.0D);
    }
    
    protected void processAttacking()
    {
        if (theBird.getAttackTarget() != null)
        {
            theBird.motionY = -2.0D;
            double ticksToHitTarget = (theBird.posY - theBird.getAttackTarget().posY) / Math.abs(theBird.motionY);
            theBird.motionX = (theBird.getAttackTarget().posX - theBird.posX) / ticksToHitTarget;
            theBird.motionZ = (theBird.getAttackTarget().posZ - theBird.posZ) / ticksToHitTarget;
        }        
    }
    
    
    public void moveForward(double parSpeedFactor)
    {
        theBird.motionX = theBird.getLookVec().xCoord * parSpeedFactor * theBird.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
        theBird.motionZ = theBird.getLookVec().zCoord * parSpeedFactor * theBird.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }
    
    protected void stopMoving()
    {
        theBird.motionX = 0;
        theBird.motionY = 0;
        theBird.motionZ = 0;
    }
    
    /**
     * True if the entity has an unobstructed line of travel to the waypoint.
     */
    public boolean isCourseTraversable(double parX, double parY, double parZ)
    {
        double theDistance = MathHelper.sqrt_double(parX * parX + parY * parY + parZ * parZ);

        double incrementX = (parX - theBird.posX) / theDistance;
        double incrementY = (parY - theBird.posY) / theDistance;
        double incrementZ = (parZ - theBird.posZ) / theDistance;
        AxisAlignedBB axisalignedbb = theBird.boundingBox.copy();

        for (int i = 1; i < theDistance; ++i)
        {
            axisalignedbb.offset(incrementX, incrementY, incrementZ);

            if (!theBird.worldObj.getCollidingBoundingBoxes(theBird, axisalignedbb).isEmpty())
            {
                return false;
            }
        }

        return true;
    }


}
