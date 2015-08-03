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

package com.blogspot.jabelarminecraft.wildanimals.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBigCat extends RenderLiving
{
    protected ResourceLocation normalTexture ;
    protected ResourceLocation tamedTexture ;
    protected ResourceLocation angryTexture ;
    protected ResourceLocation collarTexture ;

    public RenderBigCat(
            ModelBase parModelBase1, 
            ModelBase parModelBase2, 
            float parShadowSize, 
            ResourceLocation parNormalTexture, 
            ResourceLocation parTamedTexture, 
    		ResourceLocation parAngryTexture, 
    		ResourceLocation parCollarTexture)
    {
        super(parModelBase1, parShadowSize);
        setRenderPassModel(parModelBase2);
        normalTexture = parNormalTexture ;
        tamedTexture = parTamedTexture ;
        angryTexture = parAngryTexture ;
        collarTexture = parCollarTexture ;
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityBigCat parEntityBigCat, float par2)
    {
        return parEntityBigCat.getTailRotation();
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityBigCat parEntityBigCat, int parRenderPass, float parShakeShadingFactor)
    {
        if (parRenderPass == 0 && parEntityBigCat.getBigCatShaking())
        {
            float colorComponent = parEntityBigCat.getBrightness(parShakeShadingFactor) * parEntityBigCat.getShadingWhileShaking(parShakeShadingFactor);
            bindTexture(normalTexture);
            GL11.glColor3f(colorComponent, colorComponent, colorComponent);
            return 1;
        }
        else if (parRenderPass == 1 && parEntityBigCat.isTamed())
        {
            bindTexture(collarTexture);
            int collarColor = parEntityBigCat.getCollarColor();
            GL11.glColor3f(EntitySheep.fleeceColorTable[collarColor][0], EntitySheep.fleeceColorTable[collarColor][1], EntitySheep.fleeceColorTable[collarColor][2]);
            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBigCat parEntityBigCat)
    {
        return parEntityBigCat.isTamed() ? tamedTexture : (parEntityBigCat.isAngry() ? angryTexture : normalTexture);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    @Override
	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    {
        return shouldRenderPass((EntityBigCat)par1EntityLivingBase, par2, par3);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    @Override
	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2)
    {
        return handleRotationFloat((EntityBigCat)par1EntityLivingBase, par2);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return getEntityTexture((EntityBigCat)par1Entity);
    }
}