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

package com.blogspot.jabelarminecraft.wildanimals.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey.AIStates;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBirdOfPrey extends ModelWildAnimals
{
	//fields
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer bodyWingless;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer tail;
    public ModelRenderer tailLeft;
    public ModelRenderer tailRight;
    public ModelRenderer beak1;
    public ModelRenderer beak2;
    public ModelRenderer wingLeft1;
    public ModelRenderer wingLeft2;
    public ModelRenderer wingRight1;
    public ModelRenderer wingRight2;

    // create an animation cycles
    protected float[][] perchedCycle = new float[][]
    {
        // bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
	    { -70F, 70F, 70F, 0F, 0F, 0F, 90F, 0F }
    };
    
    protected float[][] takingOffCycle = new float[][]
    {
    	// bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
	    { -5F, 5F, 70F, -10F, 0F, 0F, 20F, -20F },
	    { -4F, 4F, 70F, -10F, 0F, 0F, 15F, -15F },
	    { -2F, 2F, 70F, -10F, 0F, 0F, 10F, -10F },
	    { -1F, 1F, 70F, -10F, 0F, 0F, 5F, -5F },
	    { 0F, 0F, 70F, -10F, 0F, 0F, 0F, 0F },
	    { 1F, -1F, 70F, -10F, 0F, 0F, -5F, 5F },
	    { 2F, -2F, 70F, -10F, 0F, 0F, -10F, 10F },
	    { 4F, -4F, 70F, -10F, 0F, 0F, -15F, 15F },
	    { 5F, -5F, 70F, -10F, 0F, 0F, -20F, 20F },
	    { 6F, -6F, 70F, -10F, 0F, 0F, -25F, 20F },
	    { 7F, -7F, 70F, -10F, 0F, 0F, -30F, 20F },
	    { 4F, -4F, 70F, -10F, 0F, 0F, -15F, 15F },
	    { 0F, 0F, 70F, -10F, 0F, 0F, -0F, 0F },
	    { -3F, 3F, 70F, -10F, 0F, 0F, 10F, -10F },
	    { -5F, 5F, 70F, -10F, 0F, 0F, 20F, -20F },
	    { -7F, 7F, 70F, -10F, 0F, 0F, 30F, -20F },
	    { -10F, 10F, 70F, -10F, 0F, 0F, 40F, -20F },
	    { -12F, 12F, 70F, -10F, 0F, 0F, 50F, -20F },
	    { -10F, 10F, 70F, -10F, 0F, 0F, 45F, -20F },
	    { -10F, 10F, 70F, -10F, 0F, 0F, 40F, -20F },
	    { -8F, 8F, 70F, -10F, 0F, 0F, 35F, -20F },
	    { -7F, 7F, 70F, -10F, 0F, 0F, 30F, -20F },
	    { -6F, 6F, 70F, -10F, 0F, 0F, 25F, -20F }
    };
    
    protected float[][] soaringCycle = new float[][]
    {
        // bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
	    { 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F }
    };
    
    protected float[][] divingCycle = new float[][]
    {
        // bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
        { 90F, 0F, 0F, 0F, 0F, -50F, 0F, 0F }
    };
    
    protected float[][] attackingCycle = new float[][]
    {
        // bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
        { 90F, 0F, 0F, 0F, 0F, -50F, 0F, 0F }
    };
    
    protected float[][] landingCycle = new float[][]
    {
            // bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
	    { -70F, 70F, 70F, 0F, 0F, 50F, 0F, 0F }
    };
    
    protected float[][] travellingCycle = new float[][]
    {
            // bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleY, wing1AngleZ, wing2AngleZ
        { -5F, 5F, 70F, -10F, 0F, 0F, 20F, -20F },
        { -4F, 4F, 70F, -10F, 0F, 0F, 15F, -15F },
        { -2F, 2F, 70F, -10F, 0F, 0F, 10F, -10F },
        { -1F, 1F, 70F, -10F, 0F, 0F, 5F, -5F },
        { 0F, 0F, 70F, -10F, 0F, 0F, 0F, 0F },
        { 1F, -1F, 70F, -10F, 0F, 0F, -5F, 5F },
        { 2F, -2F, 70F, -10F, 0F, 0F, -10F, 10F },
        { 4F, -4F, 70F, -10F, 0F, 0F, -15F, 15F },
        { 5F, -5F, 70F, -10F, 0F, 0F, -20F, 20F },
        { 6F, -6F, 70F, -10F, 0F, 0F, -25F, 20F },
        { 7F, -7F, 70F, -10F, 0F, 0F, -30F, 20F },
        { 4F, -4F, 70F, -10F, 0F, 0F, -15F, 15F },
        { 0F, 0F, 70F, -10F, 0F, 0F, -0F, 0F },
        { -3F, 3F, 70F, -10F, 0F, 0F, 10F, -10F },
        { -5F, 5F, 70F, -10F, 0F, 0F, 20F, -20F },
        { -7F, 7F, 70F, -10F, 0F, 0F, 30F, -20F },
        { -10F, 10F, 70F, -10F, 0F, 0F, 40F, -20F },
        { -12F, 12F, 70F, -10F, 0F, 0F, 50F, -20F },
        { -10F, 10F, 70F, -10F, 0F, 0F, 45F, -20F },
        { -10F, 10F, 70F, -10F, 0F, 0F, 40F, -20F },
        { -8F, 8F, 70F, -10F, 0F, 0F, 35F, -20F },
        { -7F, 7F, 70F, -10F, 0F, 0F, 30F, -20F },
        { -6F, 6F, 70F, -10F, 0F, 0F, 25F, -20F }
    };
  
	public ModelBirdOfPrey()
	{
	    textureWidth = 128;
	    textureHeight = 64;
	    
	    body = new ModelRenderer(this, 76, 0);
	    body.addBox(-3.5F, -3.5F, -9F, 7, 7, 18);
	    body.setRotationPoint(0F, 14F, 0F);
	    body.setTextureSize(textureWidth, textureHeight);
	    body.mirror = true;
	    setRotation(body, 0F, 0F, 0F);
	    head = new ModelRenderer(this, 58, 44);
	    head.addBox(-3F, -3F, -8F, 6, 6, 8);
	    head.setRotationPoint(0F, -1F, -9F);
	    head.setTextureSize(textureWidth, textureHeight);
	    head.mirror = true;
	    setRotation(head, 0F, 0F, 0F);
	    body.addChild(head);
	    beak1 = new ModelRenderer(this, 95, 50);
	    beak1.addBox(-1.5F, -1.0F, -11F, 3, 3, 4);
		beak1.setRotationPoint(0F, 0F, 0F);
		beak1.setTextureSize(textureWidth, textureHeight);
		beak1.mirror = true;
		head.addChild(beak1);
		setRotation(beak1, 0F, 0F, 0F);
		beak2 = new ModelRenderer(this, 114, 50);
		beak2.addBox(-1.0F, -0.5F, -12F, 2, 3, 2);
		beak2.setRotationPoint(0F, 0F, 0F);
		beak2.setTextureSize(textureWidth, textureHeight);
		beak2.mirror = true;
		setRotation(beak2, 0F, 0F, 0F);
		beak1.addChild(beak2);
	    leg1 = new ModelRenderer(this, 111, 30);
	    leg1.addBox(-1F, 0F, -1F, 2, 7, 2);
	    leg1.setRotationPoint(-2F, 3F, 1F);
	    leg1.setTextureSize(textureWidth, textureHeight);
	    leg1.mirror = true;
	    setRotation(leg1, 0F, 0F, 0F);
	    body.addChild(leg1);
	    leg2 = new ModelRenderer(this, 111, 30);
	    leg2.addBox(-1F, 0F, -1F, 2, 7, 2);
	    leg2.setRotationPoint(2F, 3F, 1F);
	    leg2.setTextureSize(textureWidth, textureHeight);
	    leg2.mirror = true;
	    setRotation(leg2, 0F, 0F, 0F);
	    body.addChild(leg2);
	    tail = new ModelRenderer(this, 0, 50);
	    tail.addBox(-2.5F, -0.5F, 0F, 5, 1, 13);
	    tail.setRotationPoint(0F, 1F, 9F);
	    tail.setTextureSize(textureWidth, textureHeight);
	    tail.mirror = true;
	    setRotation(tail, 0F, 0F, 0F);
	    body.addChild(tail);
	    tailLeft = new ModelRenderer(this, 1, 51);
	    tailLeft.addBox(-3.5F, -0.5F, 0F, 7, 1, 12);
	    tailLeft.setRotationPoint(0F, 0F, 0F);
	    tailLeft.setTextureSize(textureWidth, textureHeight);
	    tailLeft.mirror = true;
	    setRotation(tailLeft, 0F, 30F, 0F);
	    tail.addChild(tailLeft);
	    tailRight = new ModelRenderer(this, 1, 51);
	    tailRight.addBox(-3.5F, -0.5F, 0F, 7, 1, 12);
	    tailRight.setRotationPoint(0F, 0F, 0F);
	    tailRight.setTextureSize(textureWidth, textureHeight);
	    tailRight.mirror = true;
	    setRotation(tailRight, 0F, -30F, 0F);
	    tail.addChild(tailRight);
		wingLeft1 = new ModelRenderer(this, 0, 18);
		wingLeft1.addBox(0F, -0.5F, 0F, 22, 1, 16);
		wingLeft1.setRotationPoint(1F, -1F, -8F);
		wingLeft1.setTextureSize(textureWidth, textureHeight);
		wingLeft1.mirror = true;
		setRotation(wingLeft1, 0F, 0F, 0F);
		body.addChild(wingLeft1);
		wingLeft2 = new ModelRenderer(this, 0, 0);
		wingLeft2.addBox(0F, -0.5F, 0F, 22, 1, 14);
		wingLeft2.setRotationPoint(21F, 0F, 0F);
		wingLeft2.setTextureSize(textureWidth, textureHeight);
		wingLeft2.mirror = true;
		setRotation(wingLeft2, 0F, -20F, 0F);
		wingLeft1.addChild(wingLeft2);
		wingRight1 = new ModelRenderer(this, 0, 18);
		wingRight1.addBox(-22F, 0F, 0F, 22, 1, 16);
		wingRight1.setRotationPoint(-1F, -1F, -8F);
		wingRight1.setTextureSize(textureWidth, textureHeight);
		wingRight1.mirror = true;
		setRotation(wingRight1, 0F, 0F, 0F);
		body.addChild(wingRight1);
		wingRight2 = new ModelRenderer(this, 0, 0);
		wingRight2.addBox(-22F, 0F, 0F, 22, 1, 16);
		wingRight2.setRotationPoint(-21F, 0F, 0F);
		wingRight2.setTextureSize(textureWidth, textureHeight);
		wingRight2.mirror = true;
		setRotation(wingRight2, 0F, 20F, 0F);
		wingRight1.addChild(wingRight2);
			      
        bodyWingless = new ModelRenderer(this, 76, 0);
        bodyWingless.addBox(-3.5F, -3.5F, -9F, 7, 7, 18);
        bodyWingless.setRotationPoint(0F, 14F, 0F);
        bodyWingless.setTextureSize(textureWidth, textureHeight);
        bodyWingless.mirror = true;
        setRotation(bodyWingless, 0F, 0F, 0F);
        bodyWingless.addChild(head);
        bodyWingless.addChild(leg1);
        bodyWingless.addChild(leg2);
        bodyWingless.addChild(tail);
	}
  
	@Override
	public void render(Entity parEntity, float f, float f1, float f2, float f3, float f4, float f5)
	{
	 	// best to cast to actual expected entity, to allow access to custom fields related to animation
  		renderBirdOfPrey((EntityBirdOfPrey) parEntity, f5);
	}
	  
	public void renderBirdOfPrey(EntityBirdOfPrey parEntity, float parRenderFloat)
	{
		setRotationAngles(parEntity);

        // scale the whole thing for big or small entities
        GL11.glPushMatrix();
    	GL11.glScalef(parEntity.getScaleFactor(), parEntity.getScaleFactor(), parEntity.getScaleFactor());

		// should only need to render body because all rest are children
    	if (parEntity.getState() == AIStates.STATE_PERCHED
    	        || parEntity.getState() == AIStates.STATE_PERCHED_TAMED)
    	{
    	    bodyWingless.render(parRenderFloat);
    	}
    	else
    	{
    	    body.render(parRenderFloat);
    	}

		// don't forget to pop the matrix for overall scaling
        GL11.glPopMatrix();
	}
  
 
	public void setRotationAngles(EntityBirdOfPrey parEntity)
	{
		// by using the getEntityID in the cycle index calculation
		// it helps provide "randomness" so that
		// all entities of same type aren't at same point in animation
		// because ticksExisted gets reset when world is loaded
		// so initial randomness due to when entity was spawned will be reset
		if (parEntity.getState() == AIStates.STATE_TAKING_OFF)
		{
			doAnimate(parEntity, takingOffCycle);
		}
		else if (parEntity.getState() == AIStates.STATE_DIVING)
		{
			doAnimate(parEntity, divingCycle);
		}
		else if (parEntity.getState() == AIStates.STATE_LANDING)
		{
			doAnimate(parEntity, landingCycle);
		}
		else if (parEntity.getState() == AIStates.STATE_PERCHED
		        || parEntity.getState() == AIStates.STATE_PERCHED_TAMED)
		{
			doAnimate(parEntity, perchedCycle);
		}
		else if (parEntity.getState() == AIStates.STATE_SOARING
		        || parEntity.getState() == AIStates.STATE_SOARING_TAMED)
		{
			doAnimate(parEntity, soaringCycle);
		}
        else if (parEntity.getState() == AIStates.STATE_TRAVELLING
                || parEntity.getState() == AIStates.STATE_SEEKING)
        {
            doAnimate(parEntity, travellingCycle);
        }
        else if (parEntity.getState() == AIStates.STATE_ATTACKING)
        {
            doAnimate(parEntity, attackingCycle);
        }

	}
	
	public void doAnimate(EntityBirdOfPrey parEntity, float[][] parCycleArray)
	{
		cycleIndex = (int)Math.floor((parEntity.ticksExisted+parEntity.getRandFactor()*2)%parCycleArray.length)/2;
		// will need to set based on entity state
    	// bodyAngleX, headAngleX, legsAngleX, tailAngleX, wing1AngleX, wing1AngleZ, wing2AngleZ
        setRotation(body, parCycleArray[cycleIndex][0], 0, 0);
        setRotation(bodyWingless, parCycleArray[cycleIndex][0], 0, 0);
		setRotation(head, parCycleArray[cycleIndex][1], 0, 0);
		// both legs have same angle
		setRotation(leg1, parCycleArray[cycleIndex][2], 0, 0);
		setRotation(leg2, parCycleArray[cycleIndex][2], 0, 0);
		setRotation(tail, parCycleArray[cycleIndex][3], 0, 0);
		// both legs have same (well negative) angle
		setRotation(wingLeft1, parCycleArray[cycleIndex][4], parCycleArray[cycleIndex][5], parCycleArray[cycleIndex][6]);
		setRotation(wingRight1, parCycleArray[cycleIndex][4], -parCycleArray[cycleIndex][5], -parCycleArray[cycleIndex][6]);
		setRotation(wingLeft2, 0, -21F, -parCycleArray[cycleIndex][7]);
		setRotation(wingRight2, 0, 21F, parCycleArray[cycleIndex][7]);
	}
}
