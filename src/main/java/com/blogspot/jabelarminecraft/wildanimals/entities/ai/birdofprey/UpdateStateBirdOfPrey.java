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

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

/**
 * @author jabelar
 *
 */
public class UpdateStateBirdOfPrey
{
    public EntityBirdOfPrey theBird;
    
    public UpdateStateBirdOfPrey(EntityBirdOfPrey parBirdOfPrey)
    {
        theBird = parBirdOfPrey;
    }
    
    public void updateAIState()
    {
        switch (getState())
        {
            case STATE_PERCHED:
            {
                // check if block perched upon has disappeared
    //            // DEBUG
    //            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
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
    //            // DEBUG
    //            System.out.println("Block underneath = "+worldObj.getBlock(MathHelper.floor_double(posX), (int)posY - 1, MathHelper.floor_double(posZ)).getUnlocalizedName());
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

}
