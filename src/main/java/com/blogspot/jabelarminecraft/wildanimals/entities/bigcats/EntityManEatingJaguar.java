package com.blogspot.jabelarminecraft.wildanimals.entities.bigcats;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAISeePlayerBigCat;

public class EntityManEatingJaguar extends EntityJaguar
{
	protected final EntityAIBase aiSeePlayer = new EntityAISeePlayerBigCat(this, 32.0D);
	protected final EntityAIBase aiTargetNonTamedAnimal = new EntityAITargetNonTamed(this, EntityAnimal.class, 200, false);
	
    public EntityManEatingJaguar(World par1World)
    {
        super(par1World);
        
        // DEBUG
        System.out.println("EntityManEatingJaguar constructor()");
     
        this.setSize(0.6F, 0.8F);
                
        // rebuild AI task list specific to this sub-class
        clearAITasks();
        this.tasks.addTask(1, this.aiSwimming);
        this.tasks.addTask(2, this.aiLeapAtTarget);
        this.tasks.addTask(3, this.aiAttackOnCollide);
        this.tasks.addTask(4, this.aiSeePlayer);
        this.tasks.addTask(5, this.aiWander);
        this.tasks.addTask(6, this.aiWatchClosest);
        this.tasks.addTask(7, this.aiLookIdle);
        this.targetTasks.addTask(1, this.aiHurtByTarget);
        this.targetTasks.addTask(2, this.aiTargetNonTamedAnimal);
        
        this.setTamed(false);

    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    }
 

    @Override
	protected void entityInit()
    {
        super.entityInit();
        // note that custom datawatcher objects are created in EntityJaguar superclass
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.isShaking && !this.field_70928_h && !this.hasPath() && this.onGround)
        {
            this.field_70928_h = true;
            this.timeBigCatIsShaking = 0.0F;
            this.prevTimeBigCatIsShaking = 0.0F;
            this.worldObj.setEntityState(this, (byte)8);
        }
    }


    @Override
	public void setTamed(boolean par1)
    {
    	// man-eating jaguar can't be tamed
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	public boolean interact(EntityPlayer par1EntityPlayer)
    {
    	// no special interaction for a man-eater!
        return super.interact(par1EntityPlayer);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    @Override
	public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return false; // can't breed man-eating jaguar
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 8;
    }
}