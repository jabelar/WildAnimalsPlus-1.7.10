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

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.IWildAnimalsEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.EntityAIDiving;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.EntityAILanding;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.EntityAIPerched;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.EntityAISoaring;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.EntityAITakingOff;
import com.blogspot.jabelarminecraft.wildanimals.networking.entities.CreatePacketServerSide;

public class EntityBirdOfPrey extends EntityAnimal implements IEntityOwnable, IWildAnimalsEntity
{
    // for variable fields that need to be synced and saved put them in a compound
    // this is used for the extended properties interface, plus in custom packet
    public NBTTagCompound extPropsCompound = new NBTTagCompound();

    // good to have instances of AI so task list can be modified, including in sub-classes
    protected EntityAIBase aiSwimming = new EntityAISwimming(this);
    protected EntityAIBase aiLeapAtTarget = new EntityAILeapAtTarget(this, 0.4F);
    protected EntityAIBase aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.0D, true);
    protected EntityAIBase aiPerched = new EntityAIPerched(this);
    protected EntityAIBase aiTakingOff = new EntityAITakingOff(this);
    protected EntityAIBase aiSoaring = new EntityAISoaring(this);
    protected EntityAIBase aiDiving = new EntityAIDiving(this);
    protected EntityAIBase aiLanding = new EntityAILanding(this);
    protected EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F);
    protected EntityAIBase aiLookIdle = new EntityAILookIdle(this);
    protected EntityAIBase aiHurtByTarget = new EntityAIHurtByTarget(this, true);
    protected EntityAIBase aiPanic = new EntityAIPanic(this, 2.0D);
    protected final EntityAIBase aiTargetChicken = new EntityAINearestAttackableTarget(this, EntityChicken.class, 200, false);
    private float field_70926_e;

    // create state constants, did not use enum because need to cast to int anyway for packets
    protected final int STATE_PERCHED = 0;
    protected final int STATE_TAKING_OFF = 1;
    protected final int STATE_SOARING = 2;
    protected final int STATE_DIVING = 3;
    protected final int STATE_LANDING = 4;
    
    // use fields for sounds to allow easy changes in child classes
    protected String soundHurt = "wildanimals:mob.birdofprey.death";
    protected String soundDeath = "wildanimals:mob.birdofprey.death";
    protected String soundCall = "wildanimals:mob.birdofprey.hiss";

    public EntityBirdOfPrey(World par1World) throws IOException
    {
        super(par1World);
        
        // DEBUG
        System.out.println("EntityBirdOfPrey constructor(), "+"on Client="
                +par1World.isRemote+", EntityID = "+getEntityId()+", ModEntityID = "+entityUniqueID);

        setSize(2.0F, 3.0F);
        initExtProps();
        setupAI();        
     }
        
    @Override
    public void initExtProps()
    {
        extPropsCompound.setFloat("scaleFactor", 1.0F);
        extPropsCompound.setInteger("state", STATE_PERCHED);
        extPropsCompound.setInteger("stateCounter", 0);
        extPropsCompound.setDouble("anchorX", posX);
        extPropsCompound.setDouble("anchorY", posY);
        extPropsCompound.setDouble("anchorZ", posZ);
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
        tasks.addTask(1, aiSwimming);
        tasks.addTask(2, aiPanic);
        tasks.addTask(3, aiLeapAtTarget);
        tasks.addTask(4, aiAttackOnCollide);
        tasks.addTask(5, aiPerched);
        tasks.addTask(6, aiTakingOff);
        tasks.addTask(7, aiSoaring);
        tasks.addTask(8, aiDiving);
        tasks.addTask(9, aiLanding);
        tasks.addTask(10, aiWatchClosest);
        tasks.addTask(11, aiLookIdle);
        targetTasks.addTask(9, aiHurtByTarget);
        targetTasks.addTask(10, aiTargetChicken);
    }

    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.50000001192092896D);
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
    
    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        
        // should reorganize this as a switch statement based on state
        switch (getState())
        {
            case STATE_PERCHED:
            {
                // check if block perched upon has disappeared
                if (!worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).isNormalCube())
                {
                    setState(STATE_TAKING_OFF);
                    worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)posX, (int)posY, (int)posZ, 0);
                }
                else // still solidly perched
                {
                    // can occasionally adjust or flap, look around, or play sound to create variety
                    if (rand.nextInt(200) == 0)
                    {
                        rotationYawHead = rand.nextInt(360);
                    }

                    // entity can get scared if player gets too close
                    if (worldObj.getClosestPlayerToEntity(this, 4.0D) != null)
                    {
                        setState(STATE_TAKING_OFF);
                        worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)posX, (int)posY, (int)posZ, 0);
                    }
                }
                break;            
            }
            case STATE_TAKING_OFF:
            {
                break;
            }
            case STATE_SOARING:
            {
                break;
            }
            case STATE_DIVING:
            {
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
            default:
            {
                // DEBUG
                System.out.println("EntityBirdOfPrey OnLivingUpdate() **ERROR** unhandled state");
                break;
            }
        }
        
        // from EntityBat
        
//            // check if block has been added at spawnpoint
//            if (this.spawnPosition != null && (!this.worldObj.isAirBlock(this.spawnPosition.posX, this.spawnPosition.posY, this.spawnPosition.posZ) || this.spawnPosition.posY < 1))
//            {
//                this.spawnPosition = null;
//            }
//
//            // create new spawn point
//            if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.getDistanceSquared((int)this.posX, (int)this.posY, (int)this.posZ) < 4.0F)
//            {
//                this.spawnPosition = new ChunkCoordinates((int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int)this.posY + this.rand.nextInt(6) - 2, (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
//            }
//
//            double d0 = (double)this.spawnPosition.posX + 0.5D - this.posX;
//            double d1 = (double)this.spawnPosition.posY + 0.1D - this.posY;
//            double d2 = (double)this.spawnPosition.posZ + 0.5D - this.posZ;
//            this.motionX += (Math.signum(d0) * 0.5D - this.motionX) * 0.10000000149011612D;
//            this.motionY += (Math.signum(d1) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
//            this.motionZ += (Math.signum(d2) * 0.5D - this.motionZ) * 0.10000000149011612D;
//            float f = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
//            float f1 = MathHelper.wrapAngleTo180_float(f - this.rotationYaw);
//            this.moveForward = 0.5F;
//            this.rotationYaw += f1;

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
        dataWatcher.updateObject(18, Float.valueOf(this.getHealth()));
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
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        // to store additional custom fields use the extended properties interface
        // rather than adding directly to this compound.
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        // retrieve additional custom variables using extended properties interface
        // rather than retrieving directly from this compound
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
        
        par1EntityPlayer.inventory.getCurrentItem();

        return super.interact(par1EntityPlayer);
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
    public int getMaxSpawnedInChunk()
    {
        return 8;
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

    public void setState(int parState)
    {
        // DEBUG
        System.out.println("EntityBirdOfPrey setState() state changed to "+parState);

        extPropsCompound.setInteger("state", parState);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public int getState() 
    {
        return extPropsCompound.getInteger("state");
    } 

    public void setStateCounter(int parCount)
    {
        extPropsCompound.setInteger("stateCounter", parCount);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public void decrementStateCounter()
    {
        extPropsCompound.setInteger("stateCounter", extPropsCompound.getInteger("stateCounter")-1);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public int getStateCounter() 
    {
        return extPropsCompound.getInteger("stateCounter");
    } 

    public void setAnchor(double parX, double parY, double parZ)
    {
        extPropsCompound.setDouble("anchorX", parX);
        extPropsCompound.setDouble("anchorY", parY);
        extPropsCompound.setDouble("anchorZ", parZ);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public double getAnchorX() 
    {
        return extPropsCompound.getDouble("anchorX");
    } 

    public double getAnchorY() 
    {
        return extPropsCompound.getDouble("anchorY");
    } 

    public double getAnchorZ() 
    {
        return extPropsCompound.getDouble("anchorZ");
    } 

    @Override
    public String func_152113_b() // used to be getOwnerName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity getOwner() 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable var1) {
        // TODO Auto-generated method stub
        return null;
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
            parBBOS.writeFloat(extPropsCompound.getInteger("state"));
            parBBOS.writeFloat(extPropsCompound.getInteger("stateCounter"));
            parBBOS.writeDouble(extPropsCompound.getDouble("anchorX"));
            parBBOS.writeDouble(extPropsCompound.getDouble("anchorY"));
            parBBOS.writeDouble(extPropsCompound.getDouble("anchorZ"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    // no need to return anything because the extended properties tag is updated directly
    public void setExtPropsFromBuffer(ByteBufInputStream parBBIS)
    {
    	MovingObjectPosition mop = Minecraft.getMinecraft().thePlayer.rayTrace(200, 1.0F);
        try {
            extPropsCompound.setFloat("scaleFactor", parBBIS.readFloat());
            extPropsCompound.setInteger("state", parBBIS.readInt());
            extPropsCompound.setInteger("stateCounter", parBBIS.readInt());
            extPropsCompound.setDouble("anchorX", parBBIS.readDouble());
            extPropsCompound.setDouble("anchorY", parBBIS.readDouble());
            extPropsCompound.setDouble("anchorZ", parBBIS.readDouble());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void sendEntitySyncPacket() 
    {
        if (!worldObj.isRemote)
        {
            try {
                CreatePacketServerSide.sendS2CEntityNBTSync(this);
            } catch (IOException e) {
                e.printStackTrace();
            }           
        }
    }
}