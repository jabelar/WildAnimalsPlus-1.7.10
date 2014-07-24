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
import net.minecraft.util.ResourceLocation;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

public class RenderBirdOfPrey extends RenderLiving
{
    protected ResourceLocation birdOfPreyTexture;

    public RenderBirdOfPrey(ModelBase par1ModelBase, float parShadowSize)
    {
        super(par1ModelBase, parShadowSize);
        setEntityTexture();
        
    }
	
    @Override
	protected void preRenderCallback(EntityLivingBase entity, float f){
    	preRenderCallbackBirdOfPrey((EntityBirdOfPrey) entity, f);
    }

    
	protected void preRenderCallbackBirdOfPrey(EntityBirdOfPrey entity, float f)
	{
    }

    protected void setEntityTexture()
    {
    	birdOfPreyTexture = new ResourceLocation("wildanimals:textures/entity/birdsofprey/eagle.png");
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBirdOfPrey par1EntityBirdOfPrey)
    {
        return birdOfPreyTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityBirdOfPrey)par1Entity);
    }
}