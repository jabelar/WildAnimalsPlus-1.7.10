package com.blogspot.jabelarminecraft.wildanimals.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSeaHorse extends ModelWildAnimals
{
    public ModelRenderer head;
    public ModelRenderer body1;
    public ModelRenderer body2;
    public ModelRenderer body3;
    public ModelRenderer body4;
    public ModelRenderer body5;
    public ModelRenderer body6;
    public ModelRenderer body7;
    public ModelRenderer body8;
    public ModelRenderer body9;
    
    public int textureWidth = 64;
    public int textureHeight = 32;

    protected int cycleIndex;
    protected static float[][] undulationCycle = new float[][]{
    		  { 45F, -45F, -45F, 0F, 45F, 45F, 0F, -45F },
    		  { 0F, 45F, -45F, -45F, 0F, 45F, 45F, 0F },
    		  { -45F, 90F, 0F, -45F, -45F, 0F, 45F, 45F },
    		  { -45F, 45F, 45F, 0F, -45F, -45F, 0F, 45F },
    		  { 0F, -45F, 45F, 45F, 0F, -45F, -45F, 0F },
    		  { 45F, -90F, 0F, 45F, 45F, 0F, -45F, -45F },
    }
;
    
    protected float field_78145_g = 8.0F;
    protected float field_78151_h = 4.0F;
    // private static final String __OBFID = "CL_00000851";

    public ModelSeaHorse()
    {   
    	
	    head = new ModelRenderer(this, 0, 0);
	    head.addBox(-2.5F, -1F, 0F, 5, 2, 5);
	    head.setRotationPoint(0F, 0F, 2F);
	    head.setTextureSize(textureWidth, textureHeight);
	    head.mirror = true;
	    setRotation(head, 0F, 0F, 0F);
	    body1 = new ModelRenderer(this, 0, 0);
	    body1.addBox(-1.5F, -1F, -2F, 3, 2, 4);
	    body1.setRotationPoint(0F, 0F, 0F);
	    body1.setTextureSize(textureWidth, textureHeight);
	    body1.mirror = true;
	    setRotation(body1, 0F, 0F, 0F);
	    body2 = new ModelRenderer(this, 0, 0);
	    body2.addBox(-1.5F, -1F, -4F, 3, 2, 4);
	    body2.setRotationPoint(0F, 0F, -2F);
	    body2.setTextureSize(textureWidth, textureHeight);
	    body2.mirror = true;
	    body1.addChild(body2);
	    setRotation(body2, 0F, 0F, 0F);
	    body3 = new ModelRenderer(this, 0, 0);
	    body3.addBox(-1.5F, -1F, -4F, 3, 2, 4);
	    body3.setRotationPoint(0F, 0F, -4F);
	    body3.setTextureSize(textureWidth, textureHeight);
	    body3.mirror = true;
	    setRotation(body3, 0F, 0F, 0F);
	    body2.addChild(body3);
	    body4 = new ModelRenderer(this, 0, 0);
	    body4.addBox(-1.5F, -1F, -4F, 3, 2, 4);
	    body4.setRotationPoint(0F, 0F, -4F);
	    body4.setTextureSize(textureWidth, textureHeight);
	    body4.mirror = true;
	    setRotation(body4, 0F, 0F, 0F);
	    body3.addChild(body4);
	    body5 = new ModelRenderer(this, 0, 0);
	    body5.addBox(-1.5F, -1F, -4F, 3, 2, 4);
	    body5.setRotationPoint(0F, 0F, -4F);
	    body5.setTextureSize(textureWidth, textureHeight);
	    body5.mirror = true;
	    setRotation(body5, 0F, 0F, 0F);
	    body4.addChild(body5);
	    body6 = new ModelRenderer(this, 0, 0);
	    body6.addBox(-1.5F, -1F, -4F, 3, 2, 4);
	    body6.setRotationPoint(0F, 0F, -4F);
	    body6.setTextureSize(textureWidth, textureHeight);
	    body6.mirror = true;
	    setRotation(body6, 0F, 0F, 0F);
	    body5.addChild(body6);
	    body7 = new ModelRenderer(this, 0, 0);
	    body7.addBox(-1F, -1F, -4F, 2, 2, 4);
	    body7.setRotationPoint(0F, 0F, -4F);
	    body7.setTextureSize(textureWidth, textureHeight);
	    body7.mirror = true;
	    setRotation(body7, 0F, 0F, 0F);
	    body6.addChild(body7);
	    body8 = new ModelRenderer(this, 0, 0);
	    body8.addBox(-1F, -1F, -4F, 2, 2, 4);
	    body8.setRotationPoint(0F, 0F, -4F);
	    body8.setTextureSize(textureWidth, textureHeight);
	    body8.mirror = true;
	    setRotation(body8, 0F, 0F, 0F);
	    body7.addChild(body8);
	    body9 = new ModelRenderer(this, 0, 0);
	    body9.addBox(-0.5F, -0.5F, -4F, 1, 1, 4);
	    body9.setRotationPoint(0F, 0F, -4F);
	    body9.setTextureSize(textureWidth, textureHeight);
	    body9.mirror = true;
	    setRotation(body9, 0F, 0F, 0F);
	    body8.addChild(body9);
    }	

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
	public void render(Entity parEntity, float parTime, float parSwingSuppress, float par4, float parHeadAngleY, float parHeadAngleX, float par7)
    {
    	renderSerpent((EntitySerpent) parEntity, parTime, parSwingSuppress, par4, parHeadAngleY, parHeadAngleX, par7);
    }
    
	public void renderSerpent(EntitySerpent parEntity, float parTime, float parSwingSuppress, float par4, float parHeadAngleY, float parHeadAngleX, float par7)
    {
        setRotationAngles(parTime, parSwingSuppress, par4, parHeadAngleY, parHeadAngleX, par7, parEntity);

        // scale the whole thing for big or small entities
        GL11.glPushMatrix();
    	GL11.glScalef(parEntity.getScaleFactor(), parEntity.getScaleFactor(), parEntity.getScaleFactor());

        if (this.isChild)
        {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, this.field_78145_g * par7, this.field_78151_h * par7);
            head.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
            body1.render(par7); // all rest of body are children of body1
            GL11.glPopMatrix();
        }
        else
        {
            head.render(par7);
            body1.render(par7); // all rest of body are children of body1
        }

        // don't forget to pop the matrix for overall scaling
        GL11.glPopMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
	public void setRotationAngles(float parTime, float parSwingSuppress, float par3, float parHeadAngleY, float parHeadAngleX, float par6, Entity parEntity)
    {
        head.rotateAngleX = degToRad(parHeadAngleX);
        head.rotateAngleY = degToRad(parHeadAngleY);
        body1.rotateAngleX = ((float)Math.PI / 2F);
        // swingSuppress goes to 0 when still so gates the movement
        if (parSwingSuppress > 0.1F)
        {
            cycleIndex = parEntity.ticksExisted%6;
            body2.rotateAngleX = degToRad(undulationCycle[cycleIndex][0]) ;
            body3.rotateAngleX = degToRad(undulationCycle[cycleIndex][1]) ;
            body4.rotateAngleX = degToRad(undulationCycle[cycleIndex][2]) ;
            body5.rotateAngleX = degToRad(undulationCycle[cycleIndex][3]) ;
            body6.rotateAngleX = degToRad(undulationCycle[cycleIndex][4]) ;
            body7.rotateAngleX = degToRad(undulationCycle[cycleIndex][5]) ;
            body8.rotateAngleX = degToRad(undulationCycle[cycleIndex][6]) ;
            body9.rotateAngleX = degToRad(undulationCycle[cycleIndex][7]) ;
        	
        }
    }
}