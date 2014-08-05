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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.entities.eggs.EntityWildAnimalsEgg;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author jabelar
 *
 */
@SideOnly(Side.CLIENT)
public class RenderThrowableSpawnEgg extends Render
{
    protected static final ResourceLocation eggTextures = new ResourceLocation(WildAnimals.MODID+":"+"textures/items/spawn_egg.png");

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityWildAnimalsEgg parEntityWildAnimalsEgg, double parPosX, double parPosY, double parPosZ, float p_76986_8_, float parTweenFactor)
    {
        bindEntityTexture(parEntityWildAnimalsEgg);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)parPosX, (float)parPosY, (float)parPosZ);
        GL11.glRotatef(parEntityWildAnimalsEgg.prevRotationYaw + (parEntityWildAnimalsEgg.rotationYaw - parEntityWildAnimalsEgg.prevRotationYaw) * parTweenFactor - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(parEntityWildAnimalsEgg.prevRotationPitch + (parEntityWildAnimalsEgg.rotationPitch - parEntityWildAnimalsEgg.prevRotationPitch) * parTweenFactor, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.0F;
        float f5 = 5 / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = 5 / 32.0F;
        float f9 = 10 / 32.0F;
        float scaleFactor = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float f11 = - parTweenFactor;

        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(scaleFactor, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f9);
        tessellator.draw();
        GL11.glNormal3f(-scaleFactor, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, scaleFactor);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, f2, f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, f3, f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, f3, f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, f2, f5);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityWildAnimalsEgg parEntityWildAnimalsEgg)
    {
        return eggTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity parEntity)
    {
        return getEntityTexture((EntityWildAnimalsEgg)parEntity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
	public void doRender(Entity parEntity, double parPosX, double parPosY, double parPosZ, float p_76986_8_, float parTweenFactor)
    {
        doRender((EntityWildAnimalsEgg)parEntity, parPosX, parPosY, parPosZ, p_76986_8_, parTweenFactor);
    }
}