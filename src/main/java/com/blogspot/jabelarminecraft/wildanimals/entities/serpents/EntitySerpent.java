package com.blogspot.jabelarminecraft.wildanimals.entities.serpents;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.IWildAnimalsEntity;
import com.blogspot.jabelarminecraft.wildanimals.networking.entities.CreatePacketServerSide;

public class EntitySerpent extends EntityAnimal implements IWildAnimalsEntity
{
	// for variable fields that need to be synced and saved put them in a compound
	// this is used for the extended properties interface, plus in custom packet
	public NBTTagCompound extPropsCompound = new NBTTagCompound();

	// good to have instances of AI so task list can be modified, including in sub-classes
    protected EntityAIBase aiSwimming = new EntityAISwimming(this);
    protected EntityAIBase aiLeapAtTarget = new EntityAILeapAtTarget(this, 0.4F);
    protected EntityAIBase aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.0D, true);
    protected EntityAIBase aiMate = new EntityAIMate(this, 1.0D);
    protected EntityAIBase aiWander = new EntityAIWander(this, 1.0D);
    protected EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F);
    protected EntityAIBase aiLookIdle = new EntityAILookIdle(this);
    protected EntityAIBase aiHurtByTarget = new EntityAIHurtByTarget(this, true);
    protected EntityAIBase aiPanic = new EntityAIPanic(this, 2.0D);
	protected final EntityAIBase aiTargetChicken = new EntityAINearestAttackableTarget(this, EntityChicken.class, 200, false);
	private float field_70926_e;
	
	// use fields for sounds to allow easy changes in child classes
	protected String soundHurt = "wildanimals:mob.serpent.death";
	protected String soundDeath = "wildanimals:mob.serpent.death";
	protected String soundCall = "wildanimals:mob.serpent.hiss";

	public EntitySerpent(World par1World)
	{
		super(par1World);
        
        // DEBUG
        System.out.println("EntitySerpent constructor(), "+"on Client="
        		+par1World.isRemote+", EntityID = "+getEntityId()+", ModEntityID = "+entityUniqueID);

        setSize(1.0F, 0.25F);
        initExtProps();
        setupAI();		
 	}
		
	@Override
	public void initExtProps() 
	{
		extPropsCompound.setFloat("scaleFactor", 1.0F);
	}
    
    // use clear tasks for subclasses then build up their ai task list specifically
    @Override
	public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

	@Override
	public void setupAI() 
	{
        getNavigator().setAvoidsWater(true);
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(1, aiSwimming);
        tasks.addTask(2, aiPanic);
        tasks.addTask(3, aiLeapAtTarget);
        tasks.addTask(4, aiAttackOnCollide);
        tasks.addTask(5, aiMate);
        tasks.addTask(6, aiWander);
        tasks.addTask(7, aiWatchClosest);
        tasks.addTask(8, aiLookIdle);
        targetTasks.addTask(9, aiHurtByTarget);
        targetTasks.addTask(10, aiTargetChicken);
	}    

    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
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
     * Sets the active target the Task system uses for tracking
     */
    @Override
	public void setAttackTarget(EntityLivingBase par1EntityLivingBase)
    {
        super.setAttackTarget(par1EntityLivingBase);
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    @Override
	protected void updateAITick()
    {
        dataWatcher.updateObject(18, Float.valueOf(getHealth()));
    }

    @Override
	protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(18, new Float(getHealth()));
        dataWatcher.addObject(19, new Byte((byte)0));
        dataWatcher.addObject(20, new Byte((byte)BlockColored.func_150032_b(1)));
    }

    @Override
    // play step sound
	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
    	// serpents are silent when moving
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        // store additional custom variables for save, example: par1NBTTagCompound.setBoolean("Angry", isAngry());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        // retrieve additional custom variables from save, example: setAngry(par1NBTTagCompound.getBoolean("Angry"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected String getLivingSound()
    {
        return soundCall;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return soundHurt; 
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return soundDeath;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
	protected float getSoundVolume()
    {
        return 0.3F;
    }

    @Override
	protected Item getDropItem()
    {
        return Item.getItemById(-1);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {
        super.onUpdate();
        if (func_70922_bv())
        {
            field_70926_e += (1.0F - field_70926_e) * 0.4F;
        }
        else
        {
            field_70926_e += (0.0F - field_70926_e) * 0.4F;
        }

        if (func_70922_bv())
        {
            numTicksToChaseTarget = 10;
        }

        if (isWet())
        {
        	// can do special things if in water (or in rain)
        }
        else 
        {
        }
    }
    /**
     * Called when the entity is attacked.
     */
    @Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            Entity entity = par1DamageSource.getEntity();

//            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
//            {
//                par2 = (par2 + 1.0F) / 2.0F;
//            }

            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    @Override
	public boolean attackEntityAsMob(Entity par1Entity)
    {
        int i = 2;
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), i);
    }
    
    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	public boolean interact(EntityPlayer par1EntityPlayer)
    {
        
        // DEBUG
        System.out.println("EntitySerpent interact()");
        
        par1EntityPlayer.inventory.getCurrentItem();

        return super.interact(par1EntityPlayer);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    @Override
	public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return par1ItemStack == null ? false : (!(par1ItemStack.getItem() instanceof ItemFood) ? false : ((ItemFood)par1ItemStack.getItem()).isWolfsFavoriteMeat());
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
	public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    @Override
	public EntitySerpent createChild(EntityAgeable par1EntityAgeable)
    {
        
        // DEBUG
        System.out.println("EntitySerpent createChild()");
 
        EntitySerpent entitySerpent = new EntitySerpent(worldObj);

        // transfer any attributes from parent to child here, if desired (like owner for tamed entities)

        return entitySerpent;
    }

    public void func_70918_i(boolean par1)
    {
        if (par1)
        {
            dataWatcher.updateObject(19, Byte.valueOf((byte)1));
        }
        else
        {
            dataWatcher.updateObject(19, Byte.valueOf((byte)0));
        }
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    @Override
	public boolean canMateWith(EntityAnimal par1EntityAnimal)
    {
        if (par1EntityAnimal == this)
        {
            return false;
        }
        else if (!(par1EntityAnimal instanceof EntitySerpent))
        {
            return false;
        }
        else
        {
            EntitySerpent entitySerpent = (EntitySerpent)par1EntityAnimal;
            return (isInLove() && entitySerpent.isInLove());
        }
    }

    public boolean func_70922_bv()
    {
        return dataWatcher.getWatchableObjectByte(19) == 1;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
	protected boolean canDespawn()
    {
        return ticksExisted > 2400;
    }

    public boolean func_142018_a(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase)
    {
        if (!(par1EntityLivingBase instanceof EntityCreeper) && !(par1EntityLivingBase instanceof EntityGhast))
        { 
            return par1EntityLivingBase instanceof EntityPlayer && par2EntityLivingBase instanceof EntityPlayer && !((EntityPlayer)par2EntityLivingBase).canAttackPlayer((EntityPlayer)par1EntityLivingBase) ? false : !(par1EntityLivingBase instanceof EntityHorse) || !((EntityHorse)par1EntityLivingBase).isTame();
        }
        else
        {
            return false;
        }
    }

    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
    
    @Override
	public void setScaleFactor(float parScaleFactor)
    {
    	extPropsCompound.setFloat("scaleFactor", Math.abs(parScaleFactor));
   	
    	// don't forget to sync client and server
    	sendEntitySyncPacket();
    }
    
    @Override
	public float getScaleFactor()
    {
    	return extPropsCompound.getFloat("scaleFactor");
    }

    @Override
	public NBTTagCompound getExtProps()
    {
    	return extPropsCompound;
    }

	@Override
	public void setExtProps(NBTTagCompound parCompound) 
	{
		extPropsCompound = parCompound;
		
		// probably need to be careful sync'ing here as this is called by
		// sync process itself -- don't create infinite loop
	}

	@Override
	// no need to return the buffer because the buffer is operated on directly
	public void getExtPropsToBuffer(ByteBufOutputStream parBBOS) 
	{
		try {
			parBBOS.writeFloat(extPropsCompound.getFloat("scaleFactor"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	// no need to return anything because the extended properties tag is updated directly
	public void setExtPropsFromBuffer(ByteBufInputStream parBBIS) 
	{
		try {
			extPropsCompound.setFloat("scaleFactor", parBBIS.readFloat());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendEntitySyncPacket() 
	{
    	if (!worldObj.isRemote)
    	{
        	try {
				CreatePacketServerSide.sendS2CEntityNBTSync(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   		
    	}
	}
}