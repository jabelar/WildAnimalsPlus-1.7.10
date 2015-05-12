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

package com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBirdOfPrey extends EntityFlying implements IModEntity
{
    protected NBTTagCompound syncDataCompound = new NBTTagCompound();

    // create state constants, did not use enum because need to cast to int anyway for packets
    public final int STATE_PERCHED = 0;
    public final int STATE_TAKING_OFF = 1;
    public final int STATE_SOARING = 2;
    public final int STATE_DIVING = 3;
    public final int STATE_LANDING = 4;
    public final int STATE_TRAVELLING = 5;
    public final int STATE_ATTACKING = 6;
    
    // use fields for sounds to allow easy changes in child classes
    protected String soundHurt = "wildanimals:mob.birdsofprey.death";
    protected String soundDeath = "wildanimals:mob.birdsofprey.death";
    protected String soundCall = "wildanimals:mob.birdsofprey.cry";
    protected String soundFlapping = "wildanimals:mob.birdsofprey.flapping";
    
    // to ensure that multiple entities don't get synced
    // create a random factor per entity
    protected int randFactor;
    
    Class[] preyArray = new Class[] {EntityChicken.class, EntityBat.class, EntitySerpent.class};

    public EntityBirdOfPrey(World parWorld)
    {
        super(parWorld);
        
        // DEBUG
        System.out.println("EntityBirdOfPrey constructor(), "+"on Client="
                +parWorld.isRemote+", EntityID = "+getEntityId()+", ModEntityID = "+entityUniqueID);

        setSize(1.0F, 1.0F);
        randFactor = rand.nextInt(10);
        // DEBUG
        System.out.println("randFactor = "+randFactor);
        initSyncDataCompound();
        setupAI();
     }
        
    @Override
    public void initSyncDataCompound()
    {
        syncDataCompound.setFloat("scaleFactor", 1.0F);
        syncDataCompound.setInteger("state", STATE_TAKING_OFF);
        syncDataCompound.setInteger("stateCounter", 0);
        syncDataCompound.setBoolean("soarClockwise", worldObj.rand.nextBoolean());
        syncDataCompound.setDouble("soarHeight", 126-randFactor);
        syncDataCompound.setInteger("stateCounter", 0);
        syncDataCompound.setDouble("anchorX", posX);
        syncDataCompound.setDouble("anchorY", posY);
        syncDataCompound.setDouble("anchorZ", posZ);
        syncDataCompound.setString("ownerUUIDString", "");
    }
    
    // use clear tasks then build up their custom ai task list specifically
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
    }

    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
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
     * This process the current state.
     */
    @Override
    protected void updateAITick()
    {
        dataWatcher.updateObject(18, Float.valueOf(this.getHealth()));
        
        if (ticksExisted == 1)
        {
            sendEntitySyncPacket();
        }
        
//        // DEBUG
//        System.out.println("State = "+getState());
        
        switch (getState())
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
	
//	protected MovingObjectPosition isSomethingWithinReach()
//	{
//	    
//	}
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
            rotationYaw += 1.5F;
        }
        else
        {
            rotationYaw -= 1.5F;
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
        if (getSoarClockwise())
        {
            rotationYaw += 1.5F;
        }
        else
        {
            rotationYaw -= 1.5F;
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

	/**
     * This checks whether state should change
     */
    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        
        switch (getState())
        {
            case STATE_PERCHED:
            {
                // check if block perched upon has disappeared
//                // DEBUG
//                System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
                if (worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)) == Blocks.air)
                {
                    setState(STATE_TAKING_OFF);
                }
                else // still solidly perched
                {
                    // can occasionally adjust or flap, look around, or play sound to create variety
                    if (rand.nextInt(2400) == 0)
                    {
                        setState(STATE_TAKING_OFF);
                        // rotationYawHead = rand.nextInt(360);
                    }

                    // entity can get scared if player gets too close
                    EntityPlayer closestPlayer = worldObj.getClosestPlayerToEntity(this, 4.0D);
                    if (closestPlayer != null)
                    {
                        ItemStack theHeldItemStack = closestPlayer.inventory.getCurrentItem();
                        if (theHeldItemStack != null)
                        {
                            // if not holding taming food, bird will get spooked
                            if (!isTamingFood(theHeldItemStack))
                            {
                                setState(STATE_TAKING_OFF);
                            }
                        }
                    }
                }
                break;            
            }
            case STATE_TAKING_OFF:
            {
            	if (posY >= getSoarHeight())
            	{
            		setState(STATE_SOARING);
            	}
                break;
            }
            case STATE_SOARING:
            {
                // climb again if drifting too low
                // put some randomness in so entities aren't synced after loading save game
                if (posY < getSoarHeight()*0.9D)
                {
                    setState(STATE_TRAVELLING);
                }
                
                considerAttacking();
                
                if (getAttackTarget() == null)
                {
                    considerPerching();
                }
                else
                {
                    setState(STATE_ATTACKING);
                }
                
                break;
            }
            case STATE_DIVING:
            {
                // see if made it to perch
//                // DEBUG
//                System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
                if (worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)) != Blocks.air)
                {
                    setState(STATE_PERCHED);
                }
                break;
            }
            case STATE_LANDING:
            {
                // check if actually landed on a block
                if (worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).isNormalCube())
                {
                    setState(STATE_PERCHED);
                }
                break;
            }
            case STATE_TRAVELLING:
            {
                if (posY >= getSoarHeight())
                {
                    // DEBUG
                    System.out.println("State changed to soaring");
                    setState(STATE_SOARING);
                }
                break;
            }
            case STATE_ATTACKING:
            {
                // check if target has been killed or despawned
                if (getAttackTarget() == null)
                {
                    setState(STATE_TAKING_OFF);
                }
                else if (getAttackTarget().isDead)
                {
                    setAttackTarget(null);
                    setState(STATE_TAKING_OFF);
                }
                // check for hitting target
                else if (getDistanceToEntity(getAttackTarget())<2.0F)
                {
                    getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                    setState(STATE_TAKING_OFF);
                }
                break;
            }
            default:
            {
                // DEBUG
                System.out.println("EntityBirdOfPrey OnLivingUpdate() **ERROR** unhandled state");
                break;
            }
        }
    }
    
    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }
//    
//    @Override
//    public AxisAlignedBB getCollisionBox(Entity parEntity)
//    {
//        return getBoundingBox();
//    }
    
    public void considerPerching()
    {
        Block topBlock = worldObj.getTopBlock((int)posX, (int)posZ);
        if (topBlock instanceof BlockLeaves)
        {
            if (rand.nextInt(100) == 0)
            {
                setState(STATE_DIVING);
                setAnchor(posX, worldObj.getHeightValue((int)posX, (int)posZ), posZ);
            }
        }
    }
    
    public void considerAttacking()
    {
        AxisAlignedBB attackRegion = AxisAlignedBB.getBoundingBox(posX - 5.0D, worldObj.getHeightValue((int)posX, (int)posZ) - 5.0D, posZ - 5.0D, posX + 5.0D, worldObj.getHeightValue((int)posX, (int)posZ) + 5.0D, posZ + 5.0D);

        List possibleTargetEntities = worldObj.getEntitiesWithinAABB(EntitySerpent.class, attackRegion);
        Iterator<Object> targetIterator = possibleTargetEntities.iterator();
        while (targetIterator.hasNext())
        {
            setAttackTarget((EntityLivingBase) targetIterator.next());
        }
        possibleTargetEntities = worldObj.getEntitiesWithinAABB(EntityChicken.class, attackRegion);
        targetIterator = possibleTargetEntities.iterator();
        while (targetIterator.hasNext())
        {
            setAttackTarget((EntityLivingBase) targetIterator.next());
        }
        possibleTargetEntities = worldObj.getEntitiesWithinAABB(EntityBat.class, attackRegion);
        targetIterator = possibleTargetEntities.iterator();
        while (targetIterator.hasNext())
        {
            setAttackTarget((EntityLivingBase) targetIterator.next());
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double parDistance)
    {
        return true;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    @Override
    protected void fall(float p_70069_1_) {}

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    @Override
    protected void updateFallState(double p_70064_1_, boolean p_70064_3_) {}

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    @Override
    public boolean doesEntityNotTriggerPressurePlate()
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


    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(18, new Float(this.getHealth()));
        dataWatcher.addObject(19, new Byte((byte)0));
        dataWatcher.addObject(20, new Byte((byte)BlockColored.func_150032_b(1)));
    }

    @Override
    // play step sound
    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        // birds are silent when moving
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound()
    {
        if (getState() == STATE_TAKING_OFF || getState() == STATE_TRAVELLING)
        {
            return soundFlapping;
        }
        else
        {
            return soundCall;
        }
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
    }
    
    protected void moveForward(double parSpeedFactor)
    {
        motionX = getLookVec().xCoord * parSpeedFactor * getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
        motionZ = getLookVec().zCoord * parSpeedFactor * getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }
    
    protected void stopMoving()
    {
    	motionX = 0;
    	motionY = 0;
    	motionZ = 0;
    }
    
    /**
     * True if the entity has an unobstructed line of travel to the waypoint.
     */
    public boolean isCourseTraversable(double parX, double parY, double parZ)
    {
        double theDistance = MathHelper.sqrt_double(parX * parX + parY * parY + parZ * parZ);

        double incrementX = (parX - this.posX) / theDistance;
        double incrementY = (parY - this.posY) / theDistance;
        double incrementZ = (parZ - this.posZ) / theDistance;
        AxisAlignedBB axisalignedbb = this.boundingBox.copy();

        for (int i = 1; i < theDistance; ++i)
        {
            axisalignedbb.offset(incrementX, incrementY, incrementZ);

            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
            {
                return false;
            }
        }

        return true;
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

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                par2 = (par2 + 1.0F) / 2.0F;
            }

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
        System.out.println("EntityBirdOfPrey interact()");
        
        ItemStack theHeldItemStack = par1EntityPlayer.inventory.getCurrentItem();
        if (theHeldItemStack != null)
        {
            // check if raw salmon
            if (isTamingFood(theHeldItemStack))
            {
                // DEBUG
                System.out.println("It likes the raw salmon");
            }
        }

        return super.interact(par1EntityPlayer);
    }
    
    public boolean isTamingFood(ItemStack parItemStack)
    {
        // check for raw salmon
        return (parItemStack.getItem() == Items.fish && parItemStack.getItemDamage() == 1);
    }
    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
    public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn()
    {
        // don't despawn owned entities!
        if (getOwner() != null)
        {
            return false;
        }
        return ticksExisted > 2400;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound parCompound)
    {
        // DEBUG
        System.out.println("Writing NBT");
        super.writeToNBT(parCompound);
        parCompound.setTag("extendedPropsJabelar", syncDataCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound parCompound)
    {
        // DEBUG
        System.out.println("Reading NBT");
        super.readFromNBT(parCompound);
        syncDataCompound = (NBTTagCompound) parCompound.getTag("extendedPropsJabelar");
        // DEBUG
        System.out.println("State = "+getState());
    }

    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
    
    @Override
    public void setScaleFactor(float parScaleFactor)
    {
        syncDataCompound.setFloat("scaleFactor", Math.abs(parScaleFactor));
       
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    @Override
    public float getScaleFactor()
    {
        return syncDataCompound.getFloat("scaleFactor");
    }
    
    public void setOwnerUUIDString(String parOwnerUUIDString)
    {
        syncDataCompound.setString("ownerUUIDString", parOwnerUUIDString);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public String getOwnerUUIDString()
    {
        return syncDataCompound.getString("ownerUUIDString");
    }

    public void setState(int parState)
    {
        // DEBUG
        System.out.println("EntityBirdOfPrey setState() state changed to "+parState);

        syncDataCompound.setInteger("state", parState);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public int getState() 
    {
        return syncDataCompound.getInteger("state");
    } 

    public void setStateCounter(int parCount)
    {
        syncDataCompound.setInteger("stateCounter", parCount);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public void decrementStateCounter()
    {
        syncDataCompound.setInteger("stateCounter", syncDataCompound.getInteger("stateCounter")-1);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public int getStateCounter() 
    {
        return syncDataCompound.getInteger("stateCounter");
    } 

    public void setAnchor(double parX, double parY, double parZ)
    {
        syncDataCompound.setDouble("anchorX", parX);
        syncDataCompound.setDouble("anchorY", parY);
        syncDataCompound.setDouble("anchorZ", parZ);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public double getAnchorX() 
    {
        return syncDataCompound.getDouble("anchorX");
    } 

    public double getAnchorY() 
    {
        return syncDataCompound.getDouble("anchorY");
    } 

    public double getAnchorZ() 
    {
        return syncDataCompound.getDouble("anchorZ");
    } 

    public EntityPlayer getOwner()
    {
        try
        {
            UUID uuid = UUID.fromString(getOwnerUUIDString());
            return uuid == null ? null : Utilities.getPlayerFromUUID(worldObj, uuid);
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            return null;
        }
    }

    
    @Override
    public void sendEntitySyncPacket()
    {
        Utilities.sendEntitySyncPacketToClient(this);
    }
    
    public void setSoarClockwise(boolean parClockwise)
    {
        syncDataCompound.setBoolean("soarClockwise", parClockwise);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public boolean getSoarClockwise()
    {
        return syncDataCompound.getBoolean("soarClockwise");
    }
    
    public void setSoarHeight(double parHeight)
    {
        syncDataCompound.setDouble("soarHeight", parHeight);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public double getSoarHeight()
    {
        return syncDataCompound.getInteger("soarHeight");
    }

    @Override
    public NBTTagCompound getSyncDataCompound()
    {
        return syncDataCompound;
    }
    
    @Override
    public void setSyncDataCompound(NBTTagCompound parCompound)
    {
        syncDataCompound = parCompound;
    }    
    
    public int getRandFactor()
    {
        return randFactor;
    }

}