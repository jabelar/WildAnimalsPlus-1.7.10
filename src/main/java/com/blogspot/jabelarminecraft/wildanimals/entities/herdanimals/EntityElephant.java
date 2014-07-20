package com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityElephant extends EntityHerdAnimal
{	
	public EntityElephant(World par1World) 
	{
		super(par1World);
	    
		// DEBUG
        System.out.println("EntityElephant constructor()");

        initExtProps();
        setSize(width*extPropsCompound.getFloat("scaleFactor"), height*extPropsCompound.getFloat("scaleFactor"));
	}
	
    @Override
	public void initExtProps()
    {
    	super.initExtProps();
    	extPropsCompound.setFloat("scaleFactor", 2.0F); // elephants are big!
    }

    @Override
	protected void applyEntityAttributes()
    {
    	// DEBUG
    	System.out.println("EntityElephant applyEnityAttributes()");
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D); // elephants are tough
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D); // can't knockback an elephant
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
	public boolean isAIEnabled()
    {
        return true;
    }
	
    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected String getLivingSound()
    {
        return "wildanimals:mob.elephant.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return "wildanimals:mob.elephant.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return "wildanimals:mob.elephant.hurt";
    }

}
