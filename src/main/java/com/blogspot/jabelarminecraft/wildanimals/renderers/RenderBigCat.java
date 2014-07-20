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

    public RenderBigCat(ModelBase par1ModelBase, ModelBase par2ModelBase, float parShadowSize, ResourceLocation parNormalTexture, ResourceLocation parTamedTexture, 
    		ResourceLocation parAngryTexture, ResourceLocation parCollarTexture)
    {
        super(par1ModelBase, parShadowSize);
        this.setRenderPassModel(par2ModelBase);
        normalTexture = parNormalTexture ;
        tamedTexture = parTamedTexture ;
        angryTexture = parAngryTexture ;
        collarTexture = parCollarTexture ;
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityBigCat par1EntityBigCat, float par2)
    {
        return par1EntityBigCat.getTailRotation();
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityBigCat par1EntityBigCat, int par2, float par3)
    {
        if (par2 == 0 && par1EntityBigCat.getBigCatShaking())
        {
            float f1 = par1EntityBigCat.getBrightness(par3) * par1EntityBigCat.getShadingWhileShaking(par3);
            this.bindTexture(normalTexture);
            GL11.glColor3f(f1, f1, f1);
            return 1;
        }
        else if (par2 == 1 && par1EntityBigCat.isTamed())
        {
            this.bindTexture(collarTexture);
            int j = par1EntityBigCat.getCollarColor();
            GL11.glColor3f(EntitySheep.fleeceColorTable[j][0], EntitySheep.fleeceColorTable[j][1], EntitySheep.fleeceColorTable[j][2]);
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
    protected ResourceLocation getEntityTexture(EntityBigCat par1EntityBigCat)
    {
        return par1EntityBigCat.isTamed() ? tamedTexture : (par1EntityBigCat.isAngry() ? angryTexture : normalTexture);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    @Override
	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    {
        return this.shouldRenderPass((EntityBigCat)par1EntityLivingBase, par2, par3);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    @Override
	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2)
    {
        return this.handleRotationFloat((EntityBigCat)par1EntityLivingBase, par2);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityBigCat)par1Entity);
    }
}