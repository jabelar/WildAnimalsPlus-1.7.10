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
import net.minecraft.util.Vec3;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

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
            processSoaring();
            break;
        case AIStates.STATE_PERCHED_TAMED:
            processPerched();
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
        theBird.rotationPitch = 0.0F;
    }

    /**
     * 
     */
    protected void processDiving() 
    {
        theBird.rotationPitch = 90.0F;
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
        theBird.rotationPitch = 0.0F;
        
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
        theBird.rotationPitch = 0.0F; // although the model is upright, want to make sure look vector and sense of motion preserved.
        
        stopMoving();
    }
    
    protected void processSoaring()
    {
        theBird.rotationPitch = 0.0F;
        
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
        theBird.rotationPitch = 0.0F;
        
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
            theBird.rotationPitch = Utilities.getPitchFromVec(Vec3.createVectorHelper(
                    theBird.motionX, 
                    theBird.motionY,
                    theBird.motionZ));
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
    


}
