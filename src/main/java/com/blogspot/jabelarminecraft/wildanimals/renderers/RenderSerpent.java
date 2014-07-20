package com.blogspot.jabelarminecraft.wildanimals.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;

public class RenderSerpent extends RenderLiving
{
    protected ResourceLocation serpentTexture;

    public RenderSerpent(ModelBase par1ModelBase, float parShadowSize)
    {
        super(par1ModelBase, parShadowSize);
        setEntityTexture();
        
    }
	
    @Override
	protected void preRenderCallback(EntityLivingBase entity, float f){
    	preRenderCallbackSerpent((EntitySerpent) entity, f);
    }

    
	protected void preRenderCallbackSerpent(EntitySerpent entity, float f)
	{
    }

    protected void setEntityTexture()
    {
    	serpentTexture = new ResourceLocation("wildanimals:textures/entity/serpents/python.png");
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySerpent par1EntitySerpent)
    {
        return serpentTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntitySerpent)par1Entity);
    }
}