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

import net.minecraft.util.Vec3;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

/**
 * @author jabelar
 *
 */
public class ProcessStateBirdOfPrey
{
    EntityBirdOfPrey theBird;
    
    // create state constants, did not use enum because need to cast to int anyway for packets
    public final int STATE_PERCHED = 0;
    public final int STATE_TAKING_OFF = 1;
    public final int STATE_SOARING = 2;
    public final int STATE_DIVING = 3;
    public final int STATE_LANDING = 4;
    public final int STATE_TRAVELLING = 5;
    public final int STATE_ATTACKING = 6;
    public final int STATE_SOARING_TAMED = 7;
    public final int STATE_PERCHED_TAMED = 8;
    
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
        case STATE_PERCHED:
            processPerched();
            break;
        case STATE_TAKING_OFF:
            processTakingOff();
            break;
        case STATE_SOARING:
            processSoaring();
            break;
        case STATE_DIVING:
            processDiving();
            break;
        case STATE_LANDING:
            processLanding();
            break;
        case STATE_TRAVELLING:
            processTravelling();
            break;
        case STATE_ATTACKING:
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
        motionX = getAnchorX() - posX;
        motionZ = getAnchorZ() - posZ;
        motionY = -1.0D;
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
        if (posY < getSoarHeight())
        {
            motionY = 0.1D;
        }

        moveForward(1.0D);

        // turn
        if (getSoarClockwise())
        {
            rotationYaw += turnRate;
        }
        else
        {
            rotationYaw -= turnRate;
        }
    }

    /**
     * 
     */
    protected void processPerched() 
    {
        // TODO Auto-generated method stub
        
    }
    
    protected void processSoaring()
    {
        // drift down slowly
        motionY = -0.01D;

        moveForward(1.0D);
        
        // turn
        if (isTamed())
        {
            // turn towards owner
            // got the dot product idea from https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
            Vec3 vecToOwner = Vec3.createVectorHelper(getOwner().posX - posX, 0, getOwner().posZ - posZ).normalize();
            if (getLookVec().dotProduct(vecToOwner) > 0.0D)
            {
                rotationYaw -= turnRate;
            }
            else
            {
                rotationYaw += turnRate;
            }
        }
        else
        {
            if (getSoarClockwise())
            {
                rotationYaw += turnRate;
            }
            else
            {
                rotationYaw -= turnRate;
            }
        }
    }
    
    protected void processTravelling()
    {
        // climb to soaring height
        if (posY < getSoarHeight())
        {
            motionY = 0.1D;
        }

        moveForward(1.0D);
    }
    
    protected void processAttacking()
    {
        if (getAttackTarget() != null)
        {
            motionY = -2.0D;
            double ticksToHitTarget = (posY - getAttackTarget().posY) / Math.abs(motionY);
            motionX = (getAttackTarget().posX - posX) / ticksToHitTarget;
            motionZ = (getAttackTarget().posZ - posZ) / ticksToHitTarget;
        }        
    }

}
