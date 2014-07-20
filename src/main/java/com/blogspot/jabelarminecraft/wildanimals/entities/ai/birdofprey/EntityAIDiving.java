package com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

public class EntityAIDiving extends EntityAIBase
{
    protected EntityBirdOfPrey theEntity;
    protected World worldObject;
    protected double anchorX;
    protected double anchorY;
    protected double anchorZ;

    public EntityAIDiving(EntityBirdOfPrey par1EntityBirdOfPrey)
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
