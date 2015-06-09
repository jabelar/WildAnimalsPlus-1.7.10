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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityBigCat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBigCat extends ModelWildAnimals
{
  //fields
    ModelRenderer head;
    ModelRenderer body;
    ModelRenderer leg1;
    ModelRenderer leg2;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer tail;
  
  public ModelBigCat()
  {
    textureWidth = 64;
    textureHeight = 32;
    head = new ModelRenderer(this, 24, 0);
    head.addBox(-3F, -3F, -2F, 6, 5, 4);
    head.setRotationPoint(-1F, 15.5F, -7F);
    head.setTextureSize(64, 32);
    head.mirror = true;
    head.setTextureOffset(18, 14).addBox(-3F, -4F, -2F, 1, 1, 1, 0.0F); // ear1
    head.setTextureOffset(18, 18).addBox(2F, -4F, -2F, 1, 1, 1, 0.0F); // ear2
    head.setTextureOffset(0, 28).addBox(-1.5F, -1F, -3F, 3, 3, 1, 0.0F); // nose
    body = new ModelRenderer(this, 23, 11);
    body.addBox(-3F, -7F, -3F, 4, 14, 6);
    body.setRotationPoint(0F, 15.5F, 0F);
    body.setTextureSize(64, 32);
    body.mirror = true;
    leg1 = new ModelRenderer(this, 55, 12);
    leg1.addBox(-1F, 0F, -1F, 2, 7, 2);
    leg1.setRotationPoint(-2.5F, 17F, 5F);
    leg1.setTextureSize(64, 32);
    leg1.mirror = true;
    leg2 = new ModelRenderer(this, 55, 22);
    leg2.addBox(-1F, 0F, -1F, 2, 7, 2);
    leg2.setRotationPoint(0.5F, 17F, 5F);
    leg2.setTextureSize(64, 32);
    leg2.mirror = true;
    leg3 = new ModelRenderer(this, 45, 12);
    leg3.addBox(-1F, 0F, -1F, 2, 7, 2);
    leg3.setRotationPoint(-2.5F, 17F, -6F);
    leg3.setTextureSize(64, 32);
    leg3.mirror = true;
    leg4 = new ModelRenderer(this, 45, 22);
    leg4.addBox(-1F, 0F, -1F, 2, 7, 2);
    leg4.setRotationPoint(0.5F, 17F, -6F);
    leg4.setTextureSize(64, 32);
    leg4.mirror = true;
    tail = new ModelRenderer(this, 9, 13);
    tail.addBox(-1F, 0F, -1F, 2, 11, 2);
    tail.setRotationPoint(-1F, 13F, 6F);
    tail.setTextureSize(64, 32);
    tail.mirror = true;

  }
  
@Override
public void render(Entity parEntity, float parTime, float parSwingSuppress, float par4, float parHeadAngleY, float parHeadAngleX, float par7)
{
	// best to cast to actual expected entity, to allow access to custom fields related to animation
	renderBigCat((EntityBigCat) parEntity, parTime, parSwingSuppress, par4, parHeadAngleY, parHeadAngleX, par7);
}

public void renderBigCat(EntityBigCat entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);

    // scale the whole thing for big or small entities
    GL11.glPushMatrix();
    GL11.glTranslatef(0F, -0.2F, 0F); // need to figure out how to calculate this from scaleFactor (need to manually tweak currently)
	GL11.glScalef(entity.getScaleFactor(), entity.getScaleFactor(), entity.getScaleFactor());

	if (!isChild)
    {
        head.renderWithRotation(f5);
        body.render(f5);
        leg1.render(f5);
        leg2.render(f5);
        leg3.render(f5);
        leg4.render(f5);
        tail.renderWithRotation(f5);
    }
    else
    {
        float f6 = 2.0F;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 5.0F * f5, 2.0F * f5);
        head.renderWithRotation(f5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
        GL11.glTranslatef(-0.05F, 24.0F * f5, 0.0F);
        body.render(f5);
        leg1.render(f5);
        leg2.render(f5);
        leg3.render(f5);
        leg4.render(f5);
        tail.renderWithRotation(f5);
        GL11.glPopMatrix();
    }

    // don't forget to pop the matrix for overall scaling
    GL11.glPopMatrix();
  }
   
  
  /**
   * Used for easily adding entity-dependent animations. The second and third float params here are the same second
   * and third as in the setRotationAngles method.
   */
  @Override
public void setLivingAnimations(EntityLivingBase parEntityLivingBase, float parLimbSwingAngle, float parMaxLimbSwingDistance, float parHeadAngleChangeRate)
  {
      EntityBigCat entityBigCat = (EntityBigCat)parEntityLivingBase;

      if (entityBigCat.isAngry())
      {
          tail.rotateAngleY = 0.0F;
      }
      else
      {
          tail.rotateAngleY = MathHelper.cos(parLimbSwingAngle * 0.6662F) * 1.4F * parMaxLimbSwingDistance;
      }

      if (entityBigCat.isSitting())
      {
          body.setRotationPoint(0.0F, 18.0F, 0.0F);
          body.rotateAngleX = ((float)Math.PI / 4F);
          tail.setRotationPoint(-1.0F, 21.0F, 6.0F);
          leg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
          leg1.rotateAngleX = ((float)Math.PI * 3F / 2F);
          leg2.setRotationPoint(0.5F, 22.0F, 2.0F);
          leg2.rotateAngleX = ((float)Math.PI * 3F / 2F);
          leg3.rotateAngleX = 5.811947F;
          leg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
          leg4.rotateAngleX = 5.811947F;
          leg4.setRotationPoint(0.51F, 17.0F, -4.0F);
      }
      else
      {
          body.setRotationPoint(0.0F, 14.0F, 2.0F);
          body.rotateAngleX = ((float)Math.PI / 2F);
          tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
          leg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
          leg2.setRotationPoint(0.5F, 16.0F, 7.0F);
          leg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
          leg4.setRotationPoint(0.5F, 16.0F, -4.0F);
          leg1.rotateAngleX = MathHelper.cos(parLimbSwingAngle * 0.6662F) * 1.4F * parMaxLimbSwingDistance;
          leg2.rotateAngleX = MathHelper.cos(parLimbSwingAngle * 0.6662F + (float)Math.PI) * 1.4F * parMaxLimbSwingDistance;
          leg3.rotateAngleX = MathHelper.cos(parLimbSwingAngle * 0.6662F + (float)Math.PI) * 1.4F * parMaxLimbSwingDistance;
          leg4.rotateAngleX = MathHelper.cos(parLimbSwingAngle * 0.6662F) * 1.4F * parMaxLimbSwingDistance;
      }

      head.rotateAngleZ = entityBigCat.getInterestedAngle(parHeadAngleChangeRate) + entityBigCat.getShakeAngle(parHeadAngleChangeRate, 0.0F);
      body.rotateAngleZ = entityBigCat.getShakeAngle(parHeadAngleChangeRate, -0.16F);
      tail.rotateAngleZ = entityBigCat.getShakeAngle(parHeadAngleChangeRate, -0.2F);
  }

  /**
   * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
   * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
   * "far" arms and legs can swing at most.
   */
  @Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
  {
      super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
      head.rotateAngleX = par5 / (180F / (float)Math.PI);
      head.rotateAngleY = par4 / (180F / (float)Math.PI);
      tail.rotateAngleX = par3;
  }
}
