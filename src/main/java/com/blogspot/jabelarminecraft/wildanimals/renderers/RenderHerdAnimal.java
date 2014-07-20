package com.blogspot.jabelarminecraft.wildanimals.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityHerdAnimal;

public class RenderHerdAnimal extends RenderLiving
{
    protected ResourceLocation herdAnimalTexture;

    public RenderHerdAnimal(ModelBase par1ModelBase, float parShadowSize)
    {
        super(par1ModelBase, parShadowSize);
        setEntityTexture();
        
    }
	
    @Override
	protected void preRenderCallback(EntityLivingBase entity, float f){
    	preRenderCallbackHerdAnimal((EntityHerdAnimal) entity, f);
    }

    
	protected void preRenderCallbackHerdAnimal(EntityHerdAnimal entity, float f)
	{
    }

    protected void setEntityTexture()
    {
    	herdAnimalTexture = new ResourceLocation("wildanimals:textures/entity/herdanimals/elephant.png");
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityHerdAnimal par1EntityHerdAnimal)
    {
        return herdAnimalTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityHerdAnimal)par1Entity);
    }
}