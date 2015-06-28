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

package com.blogspot.jabelarminecraft.wildanimals.entities.bigcats;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.bigcat.EntityAIBegBigCat;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBigCat extends EntityTameable implements IModEntity
{
    protected NBTTagCompound syncDataCompound = new NBTTagCompound();

    /**
     * fields for controlling head tilt, like when interested or shaking
     */
    protected float targetHeadAngle;
    protected float prevHeadAngle;
    /**
     * true if the bigCat is wet else false
     */
    protected boolean isShaking;
    protected boolean startedShaking;
    /**
     * This time increases while bigCat is shaking and emitting water particles.
     */
    protected float timeBigCatIsShaking;
    protected float prevTimeBigCatIsShaking;
	
    // good to have instances of AI so task list can be modified, including in sub-classes
    protected EntityAIBase aiSwimming = new EntityAISwimming(this);
    protected EntityAIBase aiLeapAtTarget = new EntityAILeapAtTarget(this, 0.4F);
    protected EntityAIBase aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.0D, true);
    protected EntityAIBase aiFollowOwner = new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F);
    protected EntityAIBase aiMate = new EntityAIMate(this, 1.0D);
    protected EntityAIBase aiWander = new EntityAIWander(this, 1.0D);
    protected EntityAIBase aiBeg = new EntityAIBegBigCat(this, 8.0F); // in vanilla begging is only for wolf
    protected EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F);
    protected EntityAIBase aiLookIdle = new EntityAILookIdle(this);
    protected EntityAIBase aiOwnerHurtByTarget = new EntityAIOwnerHurtByTarget(this);
    protected EntityAIBase aiOwnerHurtTarget = new EntityAIOwnerHurtTarget(this);
    protected EntityAIBase aiHurtByTarget = new EntityAIHurtByTarget(this, true);
	protected EntityAIBase aiTargetNonTamedSheep = new EntityAITargetNonTamed(this, EntitySheep.class, 200, false);
	protected EntityAIBase aiTargetNonTamedCow = new EntityAITargetNonTamed(this, EntityCow.class, 200, false);
	protected EntityAIBase aiTargetNonTamedPig = new EntityAITargetNonTamed(this, EntityPig.class, 200, false);
	protected EntityAIBase aiTargetNonTamedChicken = new EntityAITargetNonTamed(this, EntityChicken.class, 200, false);
	protected EntityAIBase aiTargetNonTamedHerdAnimal = new EntityAITargetNonTamed(this, EntityHerdAnimal.class, 200, false);

	
    public EntityBigCat(World par1World)
    {
        super(par1World);
        
        // DEBUG
        System.out.println("EntityBigCat constructor(), "+"on Client="+par1World.isRemote);

        setSize(1.0F, 1.33F);
        initSyncDataCompound();
        setupAI();		
        setTamed(false);
 	}
	
	@Override
	public void setupAI() 
	{
        getNavigator().setAvoidsWater(true);
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(1, aiSwimming);
        tasks.addTask(2, aiSit);
        tasks.addTask(3, aiLeapAtTarget);
        tasks.addTask(4, aiAttackOnCollide);
        tasks.addTask(5, aiFollowOwner);
        tasks.addTask(6, aiMate);
        tasks.addTask(7, aiWander);
        tasks.addTask(8, aiBeg); // in vanilla begging is only for wolf
        tasks.addTask(9, aiWatchClosest);
        tasks.addTask(10, aiLookIdle);
        targetTasks.addTask(1, aiOwnerHurtByTarget);
        targetTasks.addTask(2, aiOwnerHurtTarget);
        targetTasks.addTask(3, aiHurtByTarget);
        targetTasks.addTask(4, aiTargetNonTamedSheep);
        targetTasks.addTask(4, aiTargetNonTamedCow);
        targetTasks.addTask(4, aiTargetNonTamedPig);
        targetTasks.addTask(4, aiTargetNonTamedChicken);
        targetTasks.addTask(4, aiTargetNonTamedHerdAnimal);
    }

	@Override
	public void initSyncDataCompound() 
	{
    	syncDataCompound.setFloat("scaleFactor", 1.2F);
	}
    
    // use clear tasks for subclasses then build up their ai task list specifically
    @Override
	public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes(); 

        // standard attributes registered to EntityLivingBase
        if (isTamed())
        {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        }
        else
        {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
        }
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);

        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
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
	public void setAttackTarget(EntityLivingBase parEntityLivingBase)
    {
        super.setAttackTarget(parEntityLivingBase);

        if (parEntityLivingBase == null)
        {
            setAngry(false);
        }
        else if (!isTamed())
        {
            setAngry(true);
        }
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
	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("wildanimals:mob.bigCat.step", 0.15F, 1.0F); // this is randomized from 1 to 5
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
	public void writeEntityToNBT(NBTTagCompound parTagCompound)
    {
        super.writeEntityToNBT(parTagCompound);
        parTagCompound.setBoolean("Angry", isAngry());
        parTagCompound.setByte("CollarColor", (byte)getCollarColor());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound parTagCompound)
    {
        super.readEntityFromNBT(parTagCompound);
        setAngry(parTagCompound.getBoolean("Angry"));

        if (parTagCompound.hasKey("CollarColor", 99))
        {
            setCollarColor(parTagCompound.getByte("CollarColor"));
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected String getLivingSound()
    {
        return isAngry() ? "wildanimals:mob.bigCat.growl" : (rand.nextInt(3) == 0 ? (isTamed() && dataWatcher.getWatchableObjectFloat(18) < 10.0F ? "wildanimals:mob.bigCat.whine" : "wildanimals:mob.bigCat.panting") : "wildanimals:mob.bigCat.bark");
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return "wildanimals:mob.bigCat.hurt"; // It uses sounds.json file to randomize and adds 1, 2 or 3 and .ogg
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return "wildanimals:mob.bigCat.death";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
	protected float getSoundVolume()
    {
        return 0.4F;
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

        if (!worldObj.isRemote && isShaking && !startedShaking && !hasPath() && onGround)
        {
            startedShaking = true;
            timeBigCatIsShaking = 0.0F;
            prevTimeBigCatIsShaking = 0.0F;
            worldObj.setEntityState(this, (byte)8);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {
        super.onUpdate();
        prevHeadAngle = targetHeadAngle;

        if (getInterested())
        {
            targetHeadAngle += (1.0F - targetHeadAngle) * 0.4F;
            numTicksToChaseTarget = 10;
        }
        else
        {
            targetHeadAngle += (0.0F - targetHeadAngle) * 0.4F;
        }

        if (isWet())
        {
            isShaking = true;
            startedShaking = false;
            timeBigCatIsShaking = 0.0F;
            prevTimeBigCatIsShaking = 0.0F;
        }
        else if ((isShaking || startedShaking) && startedShaking)
        {
            if (timeBigCatIsShaking == 0.0F)
            {
                playSound("wildanimals:mob.bigCat.shake", getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            }

            prevTimeBigCatIsShaking = timeBigCatIsShaking;
            timeBigCatIsShaking += 0.05F;

            if (prevTimeBigCatIsShaking >= 2.0F)
            {
                isShaking = false;
                startedShaking = false;
                prevTimeBigCatIsShaking = 0.0F;
                timeBigCatIsShaking = 0.0F;
            }

            if (timeBigCatIsShaking > 0.4F)
            {
                float f = (float)boundingBox.minY;
                int i = (int)(MathHelper.sin((timeBigCatIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

                for (int j = 0; j < i; ++j)
                {
                    float f1 = (rand.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
                    float f2 = (rand.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
                    worldObj.spawnParticle("splash", posX + f1, f + 0.8F, posZ + f2, motionX, motionY, motionZ);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean getBigCatShaking()
    {
        return isShaking;
    }

    /**
     * Used when calculating the amount of shading to apply while the bigCat is shaking.
     */
    @SideOnly(Side.CLIENT)
    public float getShadingWhileShaking(float parShakeRate)
    {
        return 0.75F + (prevTimeBigCatIsShaking + (timeBigCatIsShaking - prevTimeBigCatIsShaking) * parShakeRate) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float parShakeRate, float par2)
    {
        float f2 = (prevTimeBigCatIsShaking + (timeBigCatIsShaking - prevTimeBigCatIsShaking) * parShakeRate + par2) / 1.8F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }
        else if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        return MathHelper.sin(f2 * (float)Math.PI) * MathHelper.sin(f2 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    @Override
	public float getEyeHeight()
    {
        return height * 0.8F;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float parRateOfAngleChange)
    {
        return (prevHeadAngle + (targetHeadAngle - prevHeadAngle) * parRateOfAngleChange) * 0.15F * (float)Math.PI;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    @Override
	public int getVerticalFaceSpeed()
    {
        return isSitting() ? 20 : super.getVerticalFaceSpeed();
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
            aiSit.setSitting(false);

            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.entity.EntityLivingBase#attackEntityAsMob(net.minecraft.entity.Entity)
     */
    @Override
	public boolean attackEntityAsMob(Entity par1Entity)
    {
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
    }

    @Override
	public void setTamed(boolean parValue)
    {
        super.setTamed(parValue);

        if (parValue)
        {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        }
        else
        {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	public boolean interact(EntityPlayer par1EntityPlayer)
    {
        
        // DEBUG
        System.out.println("EntityBigCat interact()");
 
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

        // heal tamed with food
        if (isTamed())
        {
            if (itemstack != null)
            {
                if (itemstack.getItem() instanceof ItemFood)
                {
                    ItemFood itemfood = (ItemFood)itemstack.getItem();

                    if (itemfood.isWolfsFavoriteMeat() && dataWatcher.getWatchableObjectFloat(18) < 20.0F)
                    {
                        if (!par1EntityPlayer.capabilities.isCreativeMode)
                        {
                            --itemstack.stackSize;
                        }

                        heal(itemfood.func_150905_g(itemstack));

                        if (itemstack.stackSize <= 0)
                        {
                            par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                        }

                        return true;
                    }
                }
                else if (itemstack.getItem() == Items.dye)
                {
                    int i = BlockColored.func_150032_b(itemstack.getItemDamage());

                    if (i != getCollarColor())
                    {
                        setCollarColor(i);

                        if (!par1EntityPlayer.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
                        {
                            par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                        }

                        return true;
                    }
                }
            }

            if (par1EntityPlayer.getCommandSenderName().equalsIgnoreCase(func_152113_b()) && !worldObj.isRemote && !isBreedingItem(itemstack))
            {
                setSitting(!isSitting());
                aiSit.setSitting(isSitting());
                isJumping = false;
                setPathToEntity((PathEntity)null);
                setTarget((Entity)null);
                setAttackTarget((EntityLivingBase)null);
            }
        }
        
        // tame with bone
        else if (itemstack != null && itemstack.getItem() == Items.bone && !isAngry())
        {
            if (!par1EntityPlayer.capabilities.isCreativeMode)
            {
                --itemstack.stackSize;
            }

            if (itemstack.stackSize <= 0)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
            }

            // Try taming
            if (!worldObj.isRemote)
            {
                if (rand.nextInt(3) == 0)
                {
                    setTamed(true);
                    setPathToEntity((PathEntity)null);
                    setAttackTarget((EntityLivingBase)null);
                    aiSit.setSitting(true);
                    setHealth(20.0F);
                    func_152115_b(par1EntityPlayer.getCommandSenderName()); // used to be setOwner()
                    playTameEffect(true);
                    worldObj.setEntityState(this, (byte)7);
                }
                else
                {
                    playTameEffect(false);
                    worldObj.setEntityState(this, (byte)6);
                }
            }
        }
        
        // grow with meat
        else if (itemstack != null && itemstack.getItem() == Items.beef && !isAngry())
        {
            if (!par1EntityPlayer.capabilities.isCreativeMode)
            {
                --itemstack.stackSize;
            }

            if (itemstack.stackSize <= 0)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
            }

            if (!worldObj.isRemote)
            {
                if (rand.nextInt(3) == 0)
                {
                    setGrowingAge(getAge()+500);
                    worldObj.setEntityState(this, (byte)7);
                }
                else
                {
                    playTameEffect(false);
                    worldObj.setEntityState(this, (byte)6);
                }
            }

        return true;
        }

        return super.interact(par1EntityPlayer);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 8)
        {
            startedShaking = true;
            timeBigCatIsShaking = 0.0F;
            prevTimeBigCatIsShaking = 0.0F;
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    @SideOnly(Side.CLIENT)
    public float getTailRotation()
    {
        return isAngry() ? 1.5393804F : (isTamed() ? (0.55F - (20.0F - dataWatcher.getWatchableObjectFloat(18)) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F));
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

    /**
     * Determines whether this bigCat is angry or not.
     */
    public boolean isAngry()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    /**
     * Sets whether this bigCat is angry or not.
     */
    public void setAngry(boolean parValue)
    {
        byte b0 = dataWatcher.getWatchableObjectByte(16);

        if (parValue)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 2)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -3)));
        }
    }

    /**
     * Return this bigCat's collar color.
     */
    public int getCollarColor()
    {
        return dataWatcher.getWatchableObjectByte(20) & 15;
    }

    /**
     * Set this bigCat's collar color.
     */
    public void setCollarColor(int par1)
    {
        dataWatcher.updateObject(20, Byte.valueOf((byte)(par1 & 15)));
    }

    @Override
	public EntityBigCat createChild(EntityAgeable par1EntityAgeable)
    {
        
        // DEBUG
        System.out.println("EntityBigCat createChild()");
 
        EntityBigCat entitybigCat = new EntityBigCat(worldObj);
        String s = func_152113_b(); // used to be getOwnerName();

        if (s != null && s.trim().length() > 0)
        {
            entitybigCat.func_152115_b(s); // used to be setOwner(s);
            entitybigCat.setTamed(true);
        }

        return entitybigCat;
    }

    public void setInterested(boolean parValue)
    {
        // DEBUG
        System.out.println("Setting interested = "+parValue);
        if (parValue)
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
	public boolean canMateWith(EntityAnimal parEntityAnimal)
    {
        if (parEntityAnimal == this)
        {
            return false;
        }
        else if (!isTamed())
        {
            return false;
        }
        else if (!(parEntityAnimal instanceof EntityBigCat))
        {
            return false;
        }
        else
        {
            EntityBigCat entitybigCat = (EntityBigCat)parEntityAnimal;
            // DEBUG
            System.out.println("Found mate = "+entitybigCat);
            return !entitybigCat.isTamed() ? false : (entitybigCat.isSitting() ? false : isInLove() && entitybigCat.isInLove());
        }
    }

    public boolean getInterested()
    {
        return dataWatcher.getWatchableObjectByte(19) == 1;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
	protected boolean canDespawn()
    {
        return !isTamed() && ticksExisted > 2400;
    }

    @Override
	public boolean func_142018_a(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase)
    {
        if (!(par1EntityLivingBase instanceof EntityCreeper) && !(par1EntityLivingBase instanceof EntityGhast))
        {
            if (par1EntityLivingBase instanceof EntityBigCat)
            {
                EntityBigCat entitybigCat = (EntityBigCat)par1EntityLivingBase;

                if (entitybigCat.isTamed() && entitybigCat.getOwner() == par2EntityLivingBase)
                {
                    return false;
                }
            }

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
    	syncDataCompound.setFloat("scaleFactor", Math.abs(parScaleFactor));
   	
    	// don't forget to sync client and server
    	sendEntitySyncPacket();
    }
    
    @Override
	public float getScaleFactor()
    {
    	return syncDataCompound.getFloat("scaleFactor");
    }
    
    @Override
    public void sendEntitySyncPacket()
    {
        Utilities.sendEntitySyncPacketToClient(this);
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
}