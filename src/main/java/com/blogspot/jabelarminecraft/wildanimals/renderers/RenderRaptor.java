package com.blogspot.jabelarminecraft.wildanimals.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;

public class RenderRaptor extends RenderLiving
{
    protected ResourceLocation birdOfPreyTexture;

    public RenderRaptor(ModelBase par1ModelBase, float parShadowSize)
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
    	birdOfPreyTexture = new ResourceLocation("wildanimals:textures/entity/birdsofprey/raptor.png");
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