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

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.AIStates;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.ProcessStateBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.UpdateStateBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBirdOfPrey extends EntityFlying implements IModEntity
{
    protected NBTTagCompound syncDataCompound = new NBTTagCompound();

    public ProcessStateBirdOfPrey aiHelper;
    public UpdateStateBirdOfPrey aiUpdateState;
    
    // use fields for sounds to allow easy changes in child classes
    protected String soundHurt = "wildanimals:mob.birdsofprey.death";
    protected String soundDeath = "wildanimals:mob.birdsofprey.death";
    protected String soundCall = "wildanimals:mob.birdsofprey.cry";
    protected String soundFlapping = "wildanimals:mob.birdsofprey.flapping";
    
    // to ensure that multiple entities don't get synced
    // create a random factor per entity
    protected int randFactor;
    
    private Class[] preyArray = new Class[] {EntityChicken.class, EntityBat.class, EntitySerpent.class};

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
        setupAI();
        
        initSyncDataCompound();
     }
        
    @Override
    public void initSyncDataCompound()
    {
        syncDataCompound.setFloat("scaleFactor", 1.0F);
        syncDataCompound.setInteger("state", AIStates.STATE_TAKING_OFF);
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
        aiHelper = new ProcessStateBirdOfPrey(this);
        aiUpdateState = new UpdateStateBirdOfPrey(this);
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
        dataWatcher.updateObject(18, Float.valueOf(getHealth()));
        
        if (ticksExisted == 1)
        {
            sendEntitySyncPacket();
        }
        
        aiHelper.updateAITick();
    }

	/**
     * This checks whether state should change
     */
    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        
        aiUpdateState.updateAIState();
        
    }
    
    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
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
    public void setAttackTarget(EntityLivingBase theTargetEntity)
    {
        // DEBUG
        System.out.println("Setting attack target to = "+theTargetEntity);
        super.setAttackTarget(theTargetEntity);
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
    public int getTalkInterval()
    {
        return 400;
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
        if (getState() == AIStates.STATE_TAKING_OFF || getState() == AIStates.STATE_TRAVELLING)
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
    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource parDamageSource, float parDamageAmount)
    {
        if (isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            boolean result = super.attackEntityFrom(parDamageSource, parDamageAmount);
            if (parDamageSource.getEntity() instanceof EntityLivingBase)
            {
                setRevengeTarget((EntityLivingBase) parDamageSource.getEntity());
            }
            // DEBUG
            System.out.println("Eagle has been attacked by "+parDamageSource.getEntity()+" with source = "+parDamageSource.getSourceOfDamage()+" and revenge target set to "+getAITarget());
            return result;
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity parEntity)
    {
        return parEntity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
    }
    
    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
    public boolean interact(EntityPlayer parPlayer)
    {
        
        // DEBUG
        System.out.println("EntityBirdOfPrey interact()");
        
        ItemStack theHeldItemStack = parPlayer.inventory.getCurrentItem();
        if (theHeldItemStack != null)
        {
            // check if raw salmon
            if (isTamingFood(theHeldItemStack))
            {
                if (rand.nextInt(3) == 0)
                {
                    spawnTamingParticles(true);
                    setAttackTarget(null);
                    setHealth((float)getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());
                    setOwner(parPlayer);
    
                    // DEBUG
                    System.out.println("It likes the raw salmon");
                    --theHeldItemStack.stackSize;
                }
                else
                {
                    spawnTamingParticles(false);
                }
            }
        }

        return super.interact(parPlayer);
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
        return false;
    }
 
    @Override
    public void setDead()
    {
        // DEBUG
        System.out.println("Setting dead");
        
        super.setDead();
    }

    @Override
    public Team getTeam()
    {
        if (getOwner() != null)
        {
            EntityLivingBase entityLivingBase = getOwner();

            if (entityLivingBase != null)
            {
                return entityLivingBase.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isOnSameTeam(EntityLivingBase parEntityLivingBase)
    {
        if (getOwner() != null)
        {
            EntityLivingBase entityOwner = getOwner();

            if (parEntityLivingBase == entityOwner)
            {
                return true;
            }

            if (entityOwner != null)
            {
                return entityOwner.isOnSameTeam(parEntityLivingBase);
            }
        }

        return super.isOnSameTeam(parEntityLivingBase);
    }
    
    /**
     * Play the taming effect, will either be hearts or smoke depending on status
     */
    protected void spawnTamingParticles(boolean shouldSpawnHearts)
    {
        if (worldObj.isRemote)
        {
            return;
        }
        
        String particleType = "heart";

        if (!shouldSpawnHearts)
        {
            particleType = "smoke";
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            worldObj.spawnParticle(particleType, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2);
        }
    }
    
    public boolean isTamed()
    {
        return (getOwner() != null);
    }

    @Override
    public void writeToNBT(NBTTagCompound parCompound)
    {
        // DEBUG
        System.out.println("Writing NBT");
        super.writeToNBT(parCompound);
        parCompound.setTag("extendedPropsJabelar", syncDataCompound);
    }

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
        // DEBUG
        System.out.println("Setting new owner with UUID ="+parOwnerUUIDString);
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
            return uuid == null ? null : Utilities.getPlayerOnServerFromUUID(uuid);
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            return null;
        }
    }
    
    public boolean setOwner(EntityLivingBase parNewOwner)
    {
        if (getOwner() != null)
        {
            return false;
        }
        else if (parNewOwner == null)
        {
            return false;
        }
        else
        {
            setOwnerUUIDString(parNewOwner.getUniqueID().toString());
            return true;
        }
    }
    
    public boolean isOwner(EntityLivingBase parEntity)
    {
        return parEntity == getOwner();
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

    public Class[] getPreyArray()
    {
        return preyArray;
    }

    public void setPreyArray(Class[] preyArray)
    {
        this.preyArray = preyArray;
    }

}