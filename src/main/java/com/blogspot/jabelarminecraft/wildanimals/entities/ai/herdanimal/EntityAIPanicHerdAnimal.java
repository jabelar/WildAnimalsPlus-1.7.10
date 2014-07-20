package com.blogspot.jabelarminecraft.wildanimals.entities.ai.herdanimal;

import net.minecraft.entity.ai.EntityAIBase;

import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;

public class EntityAIPanicHerdAnimal extends EntityAIBase
{
    private final EntityHerdAnimal theEntity;

    public EntityAIPanicHerdAnimal(EntityHerdAnimal par1Entity)
    {
        theEntity = par1Entity;
        setMutexBits(1);

        // DEBUG
        System.out.println("EntityAIPanicHerdAnimal constructor(), client side = "+theEntity.worldObj.isRemote);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        if (theEntity.getAITarget() == null && theEntity.isBurning())
        {
            return false;
        }
        else
        {
            if (theEntity.isRearingFirstTick()) // only set the first tick that is rearing
            {
                return true;                    
            }
            else
            {
                return false;
            }
        }       
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        // DEBUG
        System.out.println("AIPanic startExecute(), isRearing = "+theEntity.isRearing()+", client side = "+theEntity.worldObj.isRemote);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
        theEntity.decrementRearingCounter();;
        Boolean continueExecuting = theEntity.getRearingCounter()>0; 
        if (!continueExecuting)
        {
            theEntity.setRearing(false);
            theEntity.setAttackTarget(theEntity.getLastAttacker()); // now attack back
        }
        // DEBUG
        if (theEntity.getAITarget() != null)
        {
        System.out.println("AIPanic continueExecuting = "+continueExecuting+", rearingCounter = "+theEntity.getRearingCounter()+", isRearing = "
                +theEntity.isRearing()+", Attack Target = "+theEntity.getAITarget().getClass().getSimpleName()+", client side = "+theEntity.worldObj.isRemote);
        }
        else
        {
            System.out.println("AIPanic continueExecuting = "+continueExecuting+", rearingCounter = "+theEntity.getRearingCounter()+", isRearing = "
                    +theEntity.isRearing()+", Attack Target = null"+", client side = "+theEntity.worldObj.isRemote);
        }
        return (continueExecuting);
    }
}