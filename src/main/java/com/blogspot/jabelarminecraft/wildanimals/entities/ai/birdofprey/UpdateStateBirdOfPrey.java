/**
    Copyright (C) 2015 by jabelar

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

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.utilities.Utilities;

/**
 * @author jabelar
 *
 */
public class UpdateStateBirdOfPrey
{
    public EntityBirdOfPrey theBird;
    
    // defines area on ground that bird looks for prey in
    // region is double this size as this gives dimensions in each direction
    public double attackRegionSize = 5.0D;
    
    // the 1 in X chance that it will decide to perch if over a perchable block
    public int perchChance = 100;
    // the 1 in X chance that when perched it will decide to take off
    public int takeOffChance = 2400;
    
    public UpdateStateBirdOfPrey(EntityBirdOfPrey parBirdOfPrey)
    {
        theBird = parBirdOfPrey;
    }
    
    public void updateAIState()
    {
        switch (theBird.getState())
        {
            case AIStates.STATE_PERCHED:
            {
                updateStatePerched();
                break;            
            }
            case AIStates.STATE_TAKING_OFF:
            {
                updateStateTakingOff();
                break;
            }
            case AIStates.STATE_SOARING:
            {
                updateStateSoaring();
                break;
            }
            case AIStates.STATE_DIVING:
            {
                updateStateDiving();
                break;
            }
            case AIStates.STATE_LANDING:
            {
                updateStateLanding();
                break;
            }
            case AIStates.STATE_TRAVELLING:
            {
                updateStateTravelling();
                break;
            }
            case AIStates.STATE_ATTACKING:
            {
                updateStateAttacking();
                break;
            }
            case AIStates.STATE_SOARING_TAMED:
            {
                updateStateSoaringTamed();
                break;
            }
            case AIStates.STATE_PERCHED_TAMED:
            {
                updateStatePerchedTamed();
                break;            
            }
            case AIStates.STATE_SEEKING:
            {
                updateStateSeeking();
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
    
    /**
     * 
     */
    private void updateStateSeeking()
    {
        if (theBird.isTamed())
        {
                processOwnerAttack();
     
            if (theBird.getAttackTarget() != null)
            {
                theBird.setState(AIStates.STATE_ATTACKING);
            }
            else
            {
                theBird.setState(AIStates.STATE_TRAVELLING);
            }
        }
        else
        {
            // DEBUG
            System.out.println("Seeking but isn't tamed");
        }
    }

    /**
     * 
     */
    private void updateStatePerchedTamed()
    {
        // check if block perched upon has disappeared
//            // DEBUG
//            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
        if (theBird.worldObj.getBlock(
                MathHelper.floor_double(theBird.posX), 
                (int)theBird.posY - 1, 
                MathHelper.floor_double(theBird.posZ)) == Blocks.air)
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else // still solidly perched
        {
            // can occasionally adjust or flap, look around, or play sound to create variety
            if (theBird.getRNG().nextInt(takeOffChance) == 0)
            {
                theBird.setState(AIStates.STATE_TAKING_OFF);
                // rotationYawHead = rand.nextInt(360);
            }

            // entity can get scared if player gets too close
            EntityPlayer closestPlayer = theBird.worldObj.getClosestPlayerToEntity(theBird, 4.0D);
            if (closestPlayer != null)
            {
                ItemStack theHeldItemStack = closestPlayer.inventory.getCurrentItem();
                if (theHeldItemStack != null)
                {
                    // if not holding taming food, bird will get spooked
                    if (!theBird.isTamingFood(theHeldItemStack))
                    {
                        theBird.setState(AIStates.STATE_TAKING_OFF);
                    }
                }
            }
        }
    }

    /**
     * 
     */
    private void updateStateSoaringTamed()
    {
        // climb again if drifting too low
        if (theBird.posY < theBird.getSoarHeight()*0.9D)
        {
            // point towards owner
            theBird.rotationYaw = Utilities.getYawFromVec(Vec3.createVectorHelper(
                    theBird.getOwner().posX - theBird.posX,
                    theBird.getOwner().posY - theBird.posY,
                    theBird.getOwner().posZ - theBird.posZ));
            theBird.setState(AIStates.STATE_TRAVELLING);
        }
        
        considerAttacking();
        
        if (theBird.getAttackTarget() == null)
        {
            considerPerching();
        }
        else
        {
            theBird.setState(AIStates.STATE_SEEKING);
        }
    }

    /**
     * 
     */
    private void updateStateAttacking()
    {
        EntityLivingBase target = theBird.getAttackTarget();
        // check if target has been killed or despawned
        if (target == null)
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else if (target.isDead)
        {
            theBird.setAttackTarget(null);
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else if (!Utilities.isCourseTraversable(theBird, target.posX, target.posY, target.posZ))
        {
            // theBird.setAttackTarget(null);
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        // check for hitting target
        else if (theBird.getDistanceToEntity(theBird.getAttackTarget())<2.0F)
        {
            theBird.getAttackTarget().attackEntityFrom(
                    DamageSource.causeMobDamage(theBird), 
                    (float) theBird.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
    }

    /**
     * 
     */
    private void updateStateTravelling()
    {
        if (theBird.posY >= theBird.getSoarHeight())
        {
//            // DEBUG
//            System.out.println("State changed to soaring");
            if (theBird.isTamed())
            {
                theBird.setState(AIStates.STATE_SOARING_TAMED);
            }
            else
            {
                theBird.setState(AIStates.STATE_SOARING);
            }
        }
    }

    /**
     * 
     */
    private void updateStateLanding()
    {
        // check if actually landed on a block
        if (theBird.worldObj.getBlock(
                MathHelper.floor_double(theBird.posX), 
                (int)theBird.posY - 1, 
                MathHelper.floor_double(theBird.posZ)).isNormalCube())
        {
            if (theBird.isTamed())
            {
                theBird.setState(AIStates.STATE_PERCHED_TAMED);
            }
            else
            {
                theBird.setState(AIStates.STATE_PERCHED);
            }
        }
    }

    private void updateStateDiving()
    {
//            // DEBUG
//            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
        // handle case where perch target block might get destroyed before eagle gets to it
        // or might get obstructed.
        if (theBird.worldObj.getTopBlock((int)theBird.posX, (int)theBird.posZ) instanceof BlockLeaves
                && Utilities.isCourseTraversable(
                            theBird,
                            theBird.posX, 
                            theBird.worldObj.getHeightValue(
                                    (int)theBird.posX, 
                                    (int)theBird.posZ), 
                            theBird.posZ))
        {
            // see if made it to perch
            if (theBird.worldObj.getBlock(
                    MathHelper.floor_double(theBird.posX), 
                    (int)theBird.posY - 1, 
                    MathHelper.floor_double(theBird.posZ)) != Blocks.air)
            {
                if (theBird.isTamed())
                {
                    theBird.setState(AIStates.STATE_PERCHED_TAMED);
                }
                else
                {
                    theBird.setState(AIStates.STATE_PERCHED);
                }
            }
        }
        else
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
    }

    private void updateStateSoaring()
    {
        if (theBird.isTamed())
        {
            theBird.setState(AIStates.STATE_SOARING_TAMED);
        }
        else
        {
            // climb again if drifting too low
            if (theBird.posY < theBird.getSoarHeight()*0.9D)
            {
                theBird.setState(AIStates.STATE_TRAVELLING);
            }
            
            considerAttacking();
            
            if (theBird.getAttackTarget() == null)
            {
                considerPerching();
            }
            else
            {
                theBird.setState(AIStates.STATE_ATTACKING);
            }
        }
    }

    /**
     * 
     */
    private void updateStateTakingOff()
    {
        if (theBird.posY >= theBird.getSoarHeight())
        {
            
            if (theBird.isTamed())
            {
                theBird.setState(AIStates.STATE_SOARING_TAMED);
            }
            else
            {
                theBird.setState(AIStates.STATE_SOARING);
            }
        }
    }

    /**
     * 
     */
    private void updateStatePerched()
    {
        // check if block perched upon has disappeared
//            // DEBUG
//            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
        
        if (theBird.isTamed())
        {
            theBird.setState(AIStates.STATE_PERCHED_TAMED);
        }
        else if (theBird.worldObj.getBlock(
                MathHelper.floor_double(theBird.posX), 
                (int)theBird.posY - 1, 
                MathHelper.floor_double(theBird.posZ)) == Blocks.air)
        {
            theBird.setState(AIStates.STATE_TAKING_OFF);
        }
        else // still solidly perched
        {
            // can occasionally adjust or flap, look around, or play sound to create variety
            if (theBird.getRNG().nextInt(takeOffChance) == 0)
            {
                theBird.setState(AIStates.STATE_TAKING_OFF);
                // rotationYawHead = rand.nextInt(360);
            }

            // entity can get scared if player gets too close
            EntityPlayer closestPlayer = theBird.worldObj.getClosestPlayerToEntity(theBird, 4.0D);
            if (closestPlayer != null)
            {
                ItemStack theHeldItemStack = closestPlayer.inventory.getCurrentItem();
                if (theHeldItemStack != null)
                {
                    // if not holding taming food, bird will get spooked
                    if (!theBird.isTamingFood(theHeldItemStack))
                    {
                        theBird.setState(AIStates.STATE_TAKING_OFF);
                    }
                }
            }
        }
    }

    public void considerPerching()
    {
        if (theBird.isTamed())
        {
            return;
        }
        else
        {
            // always try to perch starting at dusk
            if (theBird.worldObj.getWorldTime()%24000 > 12000 
                    || theBird.getRNG().nextInt(perchChance) == 0)
            {
                if (theBird.worldObj.getTopBlock((int)theBird.posX, (int)theBird.posZ) instanceof BlockLeaves)
                {
                    if (Utilities.isCourseTraversable(
                            theBird,
                            theBird.posX, 
                            theBird.worldObj.getHeightValue(
                                    (int)theBird.posX, 
                                    (int)theBird.posZ), 
                            theBird.posZ))
                    {
                        theBird.setState(AIStates.STATE_DIVING);
                        theBird.setAnchor(
                                theBird.posX, 
                                theBird.worldObj.getHeightValue(
                                        (int)theBird.posX, 
                                        (int)theBird.posZ), 
                                theBird.posZ);
                    }
                }
            }
        }
    }
    
    public void considerAttacking()
    {
        // DEBUG
        if (theBird.getAttackTarget() != null) System.out.println("Attack target = "+theBird.getAttackTarget());
        
        // handle case where previous target becomes unsuitable
        if (Utilities.isSuitableTarget(theBird, theBird.getAttackTarget(), false))
        {
            // DEBUG
            System.out.println(theBird.getAttackTarget()+" is no longer a suitable target");
            theBird.setAttackTarget(null);
        }
        
        if (theBird.isTamed())
        {
            processOwnerAttack();
        }
        else
        {
            processNaturalAttack(); // go for its natural prey
        }
        
        // check for revenge targets (the getAITarget() method really gives a revenge target)
        if (theBird.getAttackTarget() == null && theBird.getAITarget() != null)
        {
            // DEBUG
            System.out.println("There is a revenge target");
            theBird.setAttackTarget(theBird.getAITarget());
        }
    }

    // detect if owner has attacked something, if so set attack target to owner's target
    public void processOwnerAttack()
    {
        EntityLivingBase theOwner = theBird.getOwner();

        if (theOwner == null)
        {
            return;
        }
        else
        {
            EntityLivingBase possibleTarget = theOwner.getLastAttacker(); // note the get last attacker actually returns last attacked
            if (Utilities.isSuitableTarget(theOwner, possibleTarget, true) && 
                    Utilities.isCourseTraversable(theBird, possibleTarget.posX, possibleTarget.posY, possibleTarget.posZ))
            {
//                // attack region on ground
//                AxisAlignedBB attackRegion = AxisAlignedBB.getBoundingBox(
//                        theBird.posX - attackRegionSize, 
//                        theBird.worldObj.getHeightValue((int)theBird.posX, (int)theBird.posZ) - attackRegionSize, 
//                        theBird.posZ - attackRegionSize, 
//                        theBird.posX + attackRegionSize, 
//                        theBird.worldObj.getHeightValue((int)theBird.posX, (int)theBird.posZ) + attackRegionSize, 
//                        theBird.posZ + attackRegionSize);
//                if (attackRegion.isVecInside(Vec3.createVectorHelper(
//                        possibleTarget.posX,
//                        possibleTarget.posY,
//                        possibleTarget.posZ)))
//                {
                    // DEBUG
                    System.out.println("Setting eagle target to owners target");
                    theBird.setAttackTarget(possibleTarget); 
//                }
            }
        }
    }

    // detect if there is an attack target in region on ground directly below eagle
    public void processNaturalAttack()
    {
        // find target on ground
        AxisAlignedBB attackRegion = AxisAlignedBB.getBoundingBox(
                theBird.posX - attackRegionSize, 
                theBird.worldObj.getHeightValue((int)theBird.posX, (int)theBird.posZ) - attackRegionSize, 
                theBird.posZ - attackRegionSize, 
                theBird.posX + attackRegionSize, 
                theBird.worldObj.getHeightValue((int)theBird.posX, (int)theBird.posZ) + attackRegionSize, 
                theBird.posZ + attackRegionSize);

        for (int i=0; i<theBird.getPreyArray().length; i++)
        {
            List possibleTargetEntities = theBird.worldObj.getEntitiesWithinAABB(theBird.getPreyArray()[i], attackRegion);
            Iterator<Object> targetIterator = possibleTargetEntities.iterator();
            while (targetIterator.hasNext())
            {
                EntityLivingBase possibleTarget = (EntityLivingBase)(targetIterator.next());
                if (Utilities.isCourseTraversable(theBird, possibleTarget.posX, possibleTarget.posY, possibleTarget.posZ))
                {
                    theBird.setAttackTarget(possibleTarget);
                }
            }
        }
    }
}
