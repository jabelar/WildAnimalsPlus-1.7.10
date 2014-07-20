package com.blogspot.jabelarminecraft.wildanimals.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelRendererWildAnimals
{
    /** The size of the texture file's width in pixels. */
    public float textureWidth;
    /** The size of the texture file's height in pixels. */
    public float textureHeight;
    /** The X offset into the texture used for displaying this model */
    protected int textureOffsetX;
    /** The Y offset into the texture used for displaying this model */
    protected int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    private boolean compiled;
    /** The GL display list rendered by the Tessellator for this model */
    private int displayList;
    public boolean mirror;
    public boolean showModel;
    /** Hides the model. */
    public boolean isHidden;
    public List cubeList;
    public List childModels;
    public final String boxName;
    protected final ModelBase baseModel;
    public float offsetX;
    public float offsetY;
    public float offsetZ;

    public ModelRendererWildAnimals(ModelBase par1ModelBase, String par2Str)
    {
        this.textureWidth = 64.0F;
        this.textureHeight = 32.0F;
        this.showModel = true;
        this.cubeList = new ArrayList();
        this.baseModel = par1ModelBase;
        par1ModelBase.boxList.add(this);
        this.boxName = par2Str;
        this.setTextureSize(par1ModelBase.textureWidth, par1ModelBase.textureHeight);
    }

    public ModelRendererWildAnimals(ModelBase par1ModelBase)
    {
        this(par1ModelBase, (String)null);
    }

    public ModelRendererWildAnimals(ModelBase par1ModelBase, int par2, int par3)
    {
        this(par1ModelBase);
        this.setTextureOffset(par2, par3);
    }

    /**
     * Sets the current box's rotation points and rotation angles to another box.
     */
    public void addChild(ModelRendererWildAnimals par1ModelRenderer)
    {
        if (this.childModels == null)
        {
            this.childModels = new ArrayList();
        }

        this.childModels.add(par1ModelRenderer);
    }

    public ModelRendererWildAnimals setTextureOffset(int par1, int par2)
    {
        this.textureOffsetX = par1;
        this.textureOffsetY = par2;
        return this;
    }

    public ModelRendererWildAnimals addBox(String par1Str, float par2, float par3, float par4, int par5, int par6, int par7)
    {
        par1Str = this.boxName + "." + par1Str;
        TextureOffset textureoffset = this.baseModel.getTextureOffset(par1Str);
        this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
        this.cubeList.add((new ModelBoxWildAnimals(this, this.textureOffsetX, this.textureOffsetY, par2, par3, par4, par5, par6, par7, 0.0F)).func_78244_a(par1Str));
        return this;
    }

    public ModelRendererWildAnimals addBox(float par1, float par2, float par3, int par4, int par5, int par6)
    {
        this.cubeList.add(new ModelBoxWildAnimals(this, this.textureOffsetX, this.textureOffsetY, par1, par2, par3, par4, par5, par6, 0.0F));
        return this;
    }

    /**
     * Creates a textured box. Args: originX, originY, originZ, width, height, depth, scaleFactor.
     */
    public void addBox(float par1, float par2, float par3, int par4, int par5, int par6, float par7)
    {
        this.cubeList.add(new ModelBoxWildAnimals(this, this.textureOffsetX, this.textureOffsetY, par1, par2, par3, par4, par5, par6, par7));
    }

    public void setRotationPoint(float par1, float par2, float par3)
    {
        this.rotationPointX = par1;
        this.rotationPointY = par2;
        this.rotationPointZ = par3;
    }

    @SideOnly(Side.CLIENT)
    public void render(float par1)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(par1);
                }

                GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
                int i;

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F)
                    {
                        GL11.glCallList(this.displayList);

                        if (this.childModels != null)
                        {
                            for (i = 0; i < this.childModels.size(); ++i)
                            {
                                ((ModelRendererWildAnimals)this.childModels.get(i)).render(par1);
                            }
                        }
                    }
                    else
                    {
                        GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                        GL11.glCallList(this.displayList);

                        if (this.childModels != null)
                        {
                            for (i = 0; i < this.childModels.size(); ++i)
                            {
                                ((ModelRendererWildAnimals)this.childModels.get(i)).render(par1);
                            }
                        }

                        GL11.glTranslatef(-this.rotationPointX * par1, -this.rotationPointY * par1, -this.rotationPointZ * par1);
                    }
                }
                else
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    GL11.glCallList(this.displayList);

                    if (this.childModels != null)
                    {
                        for (i = 0; i < this.childModels.size(); ++i)
                        {
                            ((ModelRendererWildAnimals)this.childModels.get(i)).render(par1);
                        }
                    }

                    GL11.glPopMatrix();
                }

                GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderWithRotation(float par1)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(par1);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

                if (this.rotateAngleY != 0.0F)
                {
                    GL11.glRotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F)
                {
                    GL11.glRotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

                if (this.rotateAngleZ != 0.0F)
                {
                    GL11.glRotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                GL11.glCallList(this.displayList);
                GL11.glPopMatrix();
            }
        }
    }

    /**
     * Allows the changing of Angles after a box has been rendered
     */
    @SideOnly(Side.CLIENT)
    public void postRender(float par1)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(par1);
                }

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F)
                    {
                        GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);
                    }
                }
                else
                {
                    GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GL11.glRotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                    }
                }
            }
        }
    }

    /**
     * Compiles a GL display list for this model
     */
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float par1)
    {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < this.cubeList.size(); ++i)
        {
            ((ModelBoxWildAnimals)this.cubeList.get(i)).render(tessellator, par1);
        }

        GL11.glEndList();
        this.compiled = true;
    }

    /**
     * Returns the model renderer with the new texture parameters.
     */
    public ModelRendererWildAnimals setTextureSize(int par1, int par2)
    {
        this.textureWidth = par1;
        this.textureHeight = par2;
        return this;
    }
}

//public class ModelRendererWildAnimals extends ModelRenderer
//{
//    
//	protected float renderScalingX;
//	protected float renderScalingY;
//	protected float renderScalingZ;
//	
//	/*
//	 * Some parent fields are private without setters and getters
//	 * so create fields here
//	 */
//    /** The GL display list rendered by the Tessellator for this model */
//    protected int displayList;
//    /** The X offset into the texture used for displaying this model */
//    protected int textureOffsetX;
//    /** The Y offset into the texture used for displaying this model */
//    protected int textureOffsetY;
//    protected boolean compiled;
//    protected ModelBase baseModel;
//
//    public ModelRendererWildAnimals(ModelBase par1ModelBase, String par2Str)
//    {
//        super(par1ModelBase, par2Str);
//        baseModel = par1ModelBase;
//    }
//
//	public ModelRendererWildAnimals(ModelBase par1ModelBase) {
//        this(par1ModelBase, (String)null);
//	}
//
//    public ModelRendererWildAnimals(ModelBase par1ModelBase, int par2, int par3)
//    {
//        this(par1ModelBase);
//        setTextureOffset(par2, par3);
//    }
//
//
//	public void setRenderScaling(float parScaleX, float parScaleY, float parScaleZ) 
//	{ 
//		 // Set Variables
//		 renderScalingX = parScaleX;
//		 renderScalingY = parScaleY;
//		 renderScalingZ = parScaleZ;
//	}
//	
//	public void preventOverlapFlicker()
//	{
//		setRenderScaling(0.99F, 0.99F, 0.99F);
//	}
//	
//	public void allowOverlapFlicker()
//	{
//		setRenderScaling(1.0F, 1.0F, 1.0F);
//	}
//	   
//	protected void customCallList() 
//	{
//	   
//		 // Scale the render
//		 GL11.glScalef(1 * renderScalingX, 1 * renderScalingY, 1 * renderScalingZ);
//		      
//		 // Make the display call
//		 GL11.glCallList(displayList);
//		   
//		 // Remove the scale
//		 GL11.glScalef(1 / renderScalingX, 1 / renderScalingY, 1 / renderScalingZ);
//	      
//	   }
//
//    @Override
//	public ModelRendererWildAnimals setTextureOffset(int par1, int par2)
//    {
//        textureOffsetX = par1;
//        textureOffsetY = par2;
//        return this;
//    }
//
//    @Override
//	@SideOnly(Side.CLIENT)
//    public void render(float par1)
//    {
//        if (!isHidden)
//        {
//            if (showModel)
//            {
//                if (!compiled)
//                {
//                    compileDisplayList(par1);
//                }
//
//                GL11.glTranslatef(offsetX, offsetY, offsetZ);
//                int i;
//
//                if (rotateAngleX == 0.0F && rotateAngleY == 0.0F && rotateAngleZ == 0.0F)
//                {
//                    if (rotationPointX == 0.0F && rotationPointY == 0.0F && rotationPointZ == 0.0F)
//                    {
//                        // replace GL11.glCallList(displayList);
//                    	customCallList();
//
//                        if (childModels != null)
//                        {
//                            for (i = 0; i < childModels.size(); ++i)
//                            {
//                                ((ModelRendererWildAnimals)childModels.get(i)).render(par1);
//                            }
//                        }
//                    }
//                    else
//                    {
//                        GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
//                        // replace GL11.glCallList(displayList);
//                        customCallList();
//
//                        if (childModels != null)
//                        {
//                            for (i = 0; i < childModels.size(); ++i)
//                            {
//                                ((ModelRendererWildAnimals)childModels.get(i)).render(par1);
//                            }
//                        }
//
//                        GL11.glTranslatef(-rotationPointX * par1, -rotationPointY * par1, -rotationPointZ * par1);
//                    }
//                }
//                else
//                {
//                    GL11.glPushMatrix();
//                    GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
//
//                    if (rotateAngleZ != 0.0F)
//                    {
//                        GL11.glRotatef(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
//                    }
//
//                    if (rotateAngleY != 0.0F)
//                    {
//                        GL11.glRotatef(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
//                    }
//
//                    if (rotateAngleX != 0.0F)
//                    {
//                        GL11.glRotatef(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
//                    }
//
//                    //GL11.glCallList(displayList);
//                    customCallList();
//
//                    if (childModels != null)
//                    {
//                        for (i = 0; i < childModels.size(); ++i)
//                        {
//                            ((ModelRendererWildAnimals)childModels.get(i)).render(par1);
//                        }
//                    }
//
//                    GL11.glPopMatrix();
//                }
//
//                GL11.glTranslatef(-offsetX, -offsetY, -offsetZ);
//            }
//        }
//    }
//
//    @Override
//	@SideOnly(Side.CLIENT)
//    public void renderWithRotation(float par1)
//    {
//        if (!isHidden)
//        {
//            if (showModel)
//            {
//                if (!compiled)
//                {
//                    compileDisplayList(par1);
//                }
//
//                GL11.glPushMatrix();
//                GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
//
//                if (rotateAngleY != 0.0F)
//                {
//                    GL11.glRotatef(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
//                }
//
//                if (rotateAngleX != 0.0F)
//                {
//                    GL11.glRotatef(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
//                }
//
//                if (rotateAngleZ != 0.0F)
//                {
//                    GL11.glRotatef(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
//                }
//
//                GL11.glCallList(displayList);
//                GL11.glPopMatrix();
//            }
//        }
//    }
//
//    /**
//     * Allows the changing of Angles after a box has been rendered
//     */
//    @Override
//	@SideOnly(Side.CLIENT)
//    public void postRender(float par1)
//    {
//        if (!isHidden)
//        {
//            if (showModel)
//            {
//                if (!compiled)
//                {
//                    compileDisplayList(par1);
//                }
//
//                if (rotateAngleX == 0.0F && rotateAngleY == 0.0F && rotateAngleZ == 0.0F)
//                {
//                    if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F)
//                    {
//                        GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
//                    }
//                }
//                else
//                {
//                    GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
//
//                    if (rotateAngleZ != 0.0F)
//                    {
//                        GL11.glRotatef(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
//                    }
//
//                    if (rotateAngleY != 0.0F)
//                    {
//                        GL11.glRotatef(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
//                    }
//
//                    if (rotateAngleX != 0.0F)
//                    {
//                        GL11.glRotatef(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Compiles a GL display list for this model
//     */
//    @SideOnly(Side.CLIENT)
//    private void compileDisplayList(float par1)
//    {
//        displayList = GLAllocation.generateDisplayLists(1);
//        GL11.glNewList(displayList, GL11.GL_COMPILE);
//        Tessellator tessellator = Tessellator.instance;
//
//        for (int i = 0; i < cubeList.size(); ++i)
//        {
//            ((ModelBoxWildAnimals)cubeList.get(i)).render(tessellator, par1);
//        }
//
//        GL11.glEndList();
//        compiled = true;
//    }
//}