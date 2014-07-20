package com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.blogspot.jabelarminecraft.wildanimals.entities.IWildAnimalsEntity;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.herdanimal.EntityAIHurtByTargetHerdAnimal;
import com.blogspot.jabelarminecraft.wildanimals.entities.ai.herdanimal.EntityAIPanicHerdAnimal;
import com.blogspot.jabelarminecraft.wildanimals.networking.entities.CreatePacketClientSide;
import com.blogspot.jabelarminecraft.wildanimals.networking.entities.CreatePacketServerSide;

public class EntityHerdAnimal extends EntityAnimal implements IWildAnimalsEntity
{
    protected static final int REARING_TICKS_MAX = 20;
    
    protected boolean isHitWithoutResistance = false ;

    // for variable fields that need to be synced and saved put them in a compound
    // this is used for the extended properties interface, plus in custom packet
    public NBTTagCompound extPropsCompound = new NBTTagCompound();
    
    public EntityHerdAnimal(World par1World)
    {
        super(par1World);
        
        // DEBUG
        System.out.println("EntityHerdAnimal constructor(), entity.worldObj.isRemote = "+this.worldObj.isRemote);

        setSize(0.9F, 1.3F);
        initExtProps();
        setupAI();        
     }
    
    @Override
    public void initExtProps()
    {
        extPropsCompound.setFloat("scaleFactor", 1.0F);
        extPropsCompound.setInteger("rearingCounter", 0);
        extPropsCompound.setBoolean("isRearing", false);
    }
    
    // set up AI tasks
    @Override
    public void setupAI()
    {
        getNavigator().setAvoidsWater(true);
        clearAITasks(); // clear any tasks assigned in super classes
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanicHerdAnimal(this));
        // the leap and the collide together form an actual attack
        tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(3, new EntityAIAttackOnCollide(this, 1.0D, true));
        tasks.addTask(5, new EntityAIMate(this, 1.0D));
        tasks.addTask(6, new EntityAITempt(this, 1.25D, Items.wheat, false));
        tasks.addTask(7, new EntityAIFollowParent(this, 1.25D));
        tasks.addTask(8, new EntityAIWander(this, 1.0D));
        tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(10, new EntityAILookIdle(this));
        targetTasks.addTask(0, new EntityAIHurtByTargetHerdAnimal(this, true));         
    }
    
    // use clear tasks for subclasses then build up their ai task list specifically
    @Override
    public void clearAITasks()
    {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    // you don't have to call this as it is called automatically during entityLiving subclass creation
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        // standard attributes registered to EntityLivingBase
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.8D); // hard to knock back
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
        // need to register any additional attributes
        getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound()
    {
        return "mob.cow.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound()
    {
        return "mob.cow.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected String getDeathSound()
    {
        return "mob.cow.hurt";
    }

    @Override
    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.cow.step", 0.15F, 1.0F);
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
    public void onUpdate()
    {
        super.onUpdate();
    }
    
    @Override
    protected Item getDropItem()
    {
        return Items.leather;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    @Override
    protected void dropFewItems(boolean par1, int par2)
    {
        int j = rand.nextInt(3) + rand.nextInt(1 + par2);
        int k;

        for (k = 0; k < j; ++k)
        {
            dropItem(Items.leather, 1);
        }

        j = rand.nextInt(3) + 1 + rand.nextInt(1 + par2);

        for (k = 0; k < j; ++k)
        {
            if (isBurning())
            {
                dropItem(Items.cooked_beef, 1);
            }
            else
            {
                dropItem(Items.beef, 1);
            }
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        // DEBUG
        // send a test packet from client to server
         if (par1EntityPlayer.worldObj.isRemote)  
        {
             CreatePacketClientSide.sendTestPacket(69);
        }
        
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() == Items.bucket && !par1EntityPlayer.capabilities.isCreativeMode)
        {
            if (itemstack.stackSize-- == 1)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, new ItemStack(Items.milk_bucket));
            }
            else if (!par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket)))
            {
                par1EntityPlayer.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket, 1, 0), false);
            }

            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }

    @Override
    public EntityHerdAnimal createChild(EntityAgeable par1EntityAgeable)
    {
        return new EntityHerdAnimal(worldObj);
    }
    
    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float parDamageAmount)
    {
        // allow event cancellation
        if (ForgeHooks.onLivingAttack(this, par1DamageSource, parDamageAmount)) return false;
        
        if (isEntityInvulnerable())
        {
            return false; // not really "attacked" if invulnerable
        }
        else
        {
            if (worldObj.isRemote) // don't process attack on client side
            {
                return false; 
            }
            else // on server side so process attack
            {
                entityToAttack = null;
                resetInLove();;
                entityAge = 0;

                if (getHealth() <= 0.0F) // not really "attacked" if already dead
                {
                    return false;
                }
                else if (par1DamageSource.isFireDamage() && isPotionActive(Potion.fireResistance)) // fire resistance negates fire attack
                {
                    return false;
                }
                else
                {
                    // process case of falling anvil
                    if ((par1DamageSource == DamageSource.anvil || par1DamageSource == DamageSource.fallingBlock) && getEquipmentInSlot(4) != null)
                    {
                        getEquipmentInSlot(4).damageItem((int)(parDamageAmount * 4.0F + rand.nextFloat() * parDamageAmount * 2.0F), this);
                        parDamageAmount *= 0.75F;
                    }

                    limbSwingAmount = 1.5F;
                    isHitWithoutResistance = true;

                    // process temporary resistance to damage after last damage
                    if (hurtResistantTime > maxHurtResistantTime / 2.0F) // more than half of max resistance time left
                    {
                        if (parDamageAmount <= lastDamage) // resist damage that is less than the last damage
                        {
                            return false;
                        }

                        // top up the damage to the larger amount during the resistance period
                        damageEntity(par1DamageSource, parDamageAmount - lastDamage);
                        lastDamage = parDamageAmount;
                        isHitWithoutResistance = false;
                    }
                    else // no resistance so normal hit
                    {
                        lastDamage = parDamageAmount;
                        prevHealth = getHealth();
                        hurtResistantTime = maxHurtResistantTime; // start the resistance period
                        damageEntity(par1DamageSource, parDamageAmount);
                        hurtTime = maxHurtTime = 10;
                        setRearing(true);
                   }

                    // process based on what is attacking
                    attackedAtYaw = 0.0F;
                    Entity entity = par1DamageSource.getEntity();
                    if (entity != null)
                    {
                        if (entity instanceof EntityLivingBase) // set revenge on any living entity that attacks
                        {
                            // DEBUG
                            System.out.println("Setting revenge target = "+entity.getClass().getSimpleName());
                            setRevengeTarget((EntityLivingBase)entity);
                            // DEBUG
                            System.out.println("Attack target = "+this.getAITarget().getClass().getSimpleName());
                        }

                        if (entity instanceof EntityPlayer) // identify attacking player or wolf with kill time determination
                        {
                            recentlyHit = 100;
                            attackingPlayer = (EntityPlayer)entity;
                        }
                        else if (entity instanceof EntityWolf)
                        {
                            EntityWolf entitywolf = (EntityWolf)entity;

                            if (entitywolf.isTamed())
                            {
                                recentlyHit = 100;
                                attackingPlayer = null;
                            }
                        }
                    }

                    if (isHitWithoutResistance)
                    {
                        worldObj.setEntityState(this, (byte)2);

                        // process knockback
                        if (par1DamageSource != DamageSource.drown)
                        {
                            setBeenAttacked(); // checks against knockback resistance, really should be merged into knockback() method
                        }

                        if (entity != null) // if damage was done by an entity
                        {
                            double d1 = entity.posX - posX;
                            double d0;

                            for (d0 = entity.posZ - posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                            {
                                d1 = (Math.random() - Math.random()) * 0.01D;
                            }

                            attackedAtYaw = (float)(Math.atan2(d0, d1) * 180.0D / Math.PI) - rotationYaw;
                            knockBack(entity, parDamageAmount, d1, d0); 
                        }
                        else // not an entity that caused damage
                        {
                            attackedAtYaw = (int)(Math.random() * 2.0D) * 180;
                        }
                    }

                    // play sounds for hurt or death
                    // isHitWithoutResistance check helps ensure sound is played once and has time to complete
                    String s;

                    if (getHealth() <= 0.0F) // dead
                    {
                        s = getDeathSound();

                        if (isHitWithoutResistance && s != null)
                        {
                            playSound(s, getSoundVolume(), getSoundPitch());
                        }

                        onDeath(par1DamageSource);
                    }
                    else // hurt
                    {
                        s = getHurtSound();

                        if (isHitWithoutResistance && s != null)
                        {
                            playSound(s, getSoundVolume(), getSoundPitch());
                        }
                    }

                    return true;
                }
            }            
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity)
    {
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue());
    }

    // *****************************************************
    // ENCAPSULATION SETTER AND GETTER METHODS
    // Don't forget to send sync packets in setters
    // *****************************************************
        
    public void setRearing(Boolean parSetRearing)
    {
        if (parSetRearing && getAITarget()==null) // don't rear if already has target
        {
            setRearingCounter(REARING_TICKS_MAX);
            extPropsCompound.setBoolean("isRearing", true);
            // DEBUG
            System.out.println("Rearing instead of fleeing");
            System.out.println("rearingCounter = "+getRearingCounter());
          }
        else
        {
            setRearingCounter(0);
            extPropsCompound.setBoolean("isRearing", false);
            // DEBUG
            System.out.println("Finished Rearing");
            System.out.println("rearingCounter = "+getRearingCounter());
           }
        
         // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public boolean isRearing()
    {
        return extPropsCompound.getBoolean("isRearing");
    }
    
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
    
    public void setRearingCounter(int parTicks)
    {
        extPropsCompound.setInteger("rearingCounter", parTicks);
           
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public void decrementRearingCounter()
    {
        extPropsCompound.setInteger("rearingCounter", getRearingCounter()-1);
           
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }
    
    public int getRearingCounter()
    {
        return extPropsCompound.getInteger("rearingCounter");
    }

    public boolean isRearingFirstTick()
    {
        return (extPropsCompound.getInteger("rearingCounter")==REARING_TICKS_MAX);
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
            parBBOS.writeInt(extPropsCompound.getInteger("rearingCounter"));
            parBBOS.writeBoolean(extPropsCompound.getBoolean("isRearing"));
        } catch (IOException e) { e.printStackTrace(); }        
    }

    @Override
    // no need to return anything because the extended properties tag is updated directly
    public void setExtPropsFromBuffer(ByteBufInputStream parBBIS) 
    {
        try {
            extPropsCompound.setFloat("scaleFactor", parBBIS.readFloat());
            extPropsCompound.setInteger("rearingCounter", parBBIS.readInt());
            extPropsCompound.setBoolean("isRearing", parBBIS.readBoolean());
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }           
        }
    }
}