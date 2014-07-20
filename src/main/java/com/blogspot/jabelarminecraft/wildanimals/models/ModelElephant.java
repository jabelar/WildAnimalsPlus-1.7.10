 package com.blogspot.jabelarminecraft.wildanimals.models;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityElephant;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelElephant extends ModelWildAnimals
{
    public ModelRendererWildAnimals head;
    public ModelRendererWildAnimals body;
    public ModelRendererWildAnimals legRearRight;
    public ModelRendererWildAnimals legRearLeft;
    public ModelRendererWildAnimals legFrontRight;
    public ModelRendererWildAnimals legFrontLeft;
    public ModelRendererWildAnimals ear1;
    public ModelRendererWildAnimals ear2;
    public ModelRendererWildAnimals trunk1;
    public ModelRendererWildAnimals trunk2;
    public ModelRendererWildAnimals tusk1;
    public ModelRendererWildAnimals tusk2;
    public ModelRendererWildAnimals childHead; // allows customization of baby animal
    public ModelRendererWildAnimals childEar1; 
    public ModelRendererWildAnimals childEar2; 
    public ModelRendererWildAnimals childTrunk1; 
    
    // need some variables to help revert positions after a rearing animation
    protected float headRotPointXDefault;
    protected float headRotPointYDefault;
    protected float headRotPointZDefault;
    protected float bodyRotPointXDefault;
    protected float bodyRotPointYDefault;
    protected float bodyRotPointZDefault;
    protected float legFrontRightRotPointXDefault;
    protected float legFrontRightRotPointYDefault;
    protected float legFrontRightRotPointZDefault;
    protected float legFrontLeftRotPointXDefault;
    protected float legFrontLeftRotPointYDefault;
    protected float legFrontLeftRotPointZDefault;
    protected float childHeadRotPointXDefault;
    protected float childHeadRotPointYDefault;
    protected float childHeadRotPointZDefault;

    // create an animation cycle for the rearing
    // rearingCount will be the animation cycle counter
    protected static float[][] rearingOffsetCycle = new float[][]
    {
    	// {headOffsetY, headoffsetZ, bodyOffsetY, bodyOffsetZ, frontRightLegOffsetY, frontRightLegOffsetZ, frontLeftLegOffsetY, frontLegOffsetZ,
    	//       childHeadOffsetY, childheadOffsetZ}
    	// animation starts from bottom as rearingCounter counts down
	    { -2F, 2F, -0.4F, 1F, -3, 1F, -3F, 1F, -1F, 1F },
	    { -4F, 4F, -0.8F, 2F, -6F, 1.75F, -6F, 2F, -2F, 2F },
	    { -6F, 5F, -1.2F, 3F, -8F, 2.5F, -8F, 3F, -3F, 3F },
	    { -8F, 6F, -1.5F, 4F, -10F, 3F, -10F, 4F, -4F, 4F },
	    { -9.5F, 7F, -1.8F, 5F, -11F, 3.5F, -11, 4.5F, -4.5F, 5F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -10.5F, 8.0F, -2.0F, 6.0F, -12F, 4F, -12F, 5F, -5F, 6F },
	    { -9.5F, 7F, -1.8F, 5F, -11F, 3.5F, -11, 4.5F, -4.5F, 5F },
	    { -8F, 6F, -1.5F, 4F, -10F, 3F, -10F, 4F, -4F, 4F },
	    { -6F, 5F, -1.2F, 3F, -8F, 2.5F, -8F, 3F, -3F, 3F },
	    { -4F, 4F, -0.8F, 2F, -6F, 1.75F, -6F, 2F, -2F, 2F },
	    { -2F, 2F, -0.4F, 1F, -3, 1F, -3F, 1F, -1F, 1F },
    };

    protected static float[][] rearingAngleCycle = new float[][]
    {
    	// {mainAngle, trunk1Angle, trunk2Angle}
    	// animation starts from bottom as rearingCounter counts down
	    { -10F, -150F, -20F },
	    { -20F, -150F, -20F },
	    { -30F, -150F, -20F },
	    { -40F, -150F, -20F },
	    { -50F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -60F, -150F, -20F },
	    { -50F, -150F, -20F },
	    { -40F, -150F, -20F },
	    { -30F, -150F, -20F },
	    { -20F, -150F, -20F },
	    { -10F, -150F, -20F },
	    };

    protected float field_78145_g = 8.0F;
    protected float field_78151_h = 4.0F;
    // private static final String __OBFID = "CL_00000851";

    public ModelElephant()
    {
    	int par1 = 12;
    	int textureWidth = 256;
    	int textureHeight = 128;
    	
    	// head
        head = new ModelRendererWildAnimals(this, 0, 0);
        head.setTextureSize(textureWidth, textureHeight);
        head.addBox(-4F, -4F, -6F, 10, 10, 7);
        // remember default rotation point to allow for return after rearing animation
        headRotPointXDefault = -1F;
        headRotPointYDefault = 18 - par1;
        headRotPointZDefault = -10F;
        head.setRotationPoint(headRotPointXDefault, headRotPointYDefault, headRotPointZDefault);
        // add head's children models (ears, trunk, tusks)
        ear1 = new ModelRendererWildAnimals(this, 34, 8);
        ear1.setTextureSize(textureWidth, textureHeight);
        ear1.addBox(-7F, -4F, -0.5F, 7, 8, 1);
        ear1.setRotationPoint(-3F, -1F, -2F);
        head.addChild(ear1);
        ear2 = new ModelRendererWildAnimals(this, 34, 8);
        ear2.setTextureSize(textureWidth, textureHeight);
        ear2.addBox(0F, -4F, -0.5F, 7, 8, 1);
        ear2.setRotationPoint(4F, -1F, -2F);
        head.addChild(ear2);
        trunk1 = new ModelRendererWildAnimals(this, 20, 50);
        trunk1.setTextureSize(textureWidth, textureHeight);
        trunk1.addBox(-2F, 0F, -1.5F, 4, 11, 3);
        trunk1.setRotationPoint(1F, 0F, -6F);
        head.addChild(trunk1);
        // trunk tip is child of trunk
        trunk2 = new ModelRendererWildAnimals(this, 20, 50);
        trunk2.setTextureSize(textureWidth, textureHeight);
        trunk2.addBox(-2F, 0F, -1.5F, 4, 5, 3);
        trunk2.setRotationPoint(0F, 10F, 0F);
        trunk1.addChild(trunk2);
        // tusks
        tusk1 = new ModelRendererWildAnimals(this, 34, 1);
        tusk1.setTextureSize(textureWidth, textureHeight);
        tusk1.addBox(-0.5F, -0.5F, 0F, 1, 1, 6);
        tusk1.setRotationPoint(-1.5F, 2F, -6F);
        tusk1.rotateAngleX = degToRad(-160);
        head.addChild(tusk1);
        tusk2 = new ModelRendererWildAnimals(this, 34, 1);
        tusk2.setTextureSize(textureWidth, textureHeight);
        tusk2.addBox(4.5F, -0.5F, 0F, 1, 1, 6);
        tusk2.setRotationPoint(-1.5F, 2F, -6F);
        tusk2.rotateAngleX = degToRad(-160);
        head.addChild(tusk2);
        body = new ModelRendererWildAnimals(this, 0, 17);
        body.setTextureSize(textureWidth, textureHeight);
        body.addBox(-8F, -10F, -7F, 16, 21, 12);
        bodyRotPointXDefault = 0F;
        bodyRotPointYDefault = 17 - par1;
        bodyRotPointZDefault = 1F;
        body.setRotationPoint(bodyRotPointXDefault, bodyRotPointYDefault, bodyRotPointZDefault);
        legRearRight = new ModelRendererWildAnimals(this, 0, 50);
        legRearRight.setTextureSize(textureWidth, textureHeight);
        legRearRight.addBox(-3F, 0F, -2F, 5, 13, 5);
        legRearRight.setRotationPoint(-5F, 11F, 8F);
        legRearLeft = new ModelRendererWildAnimals(this, 0, 50);
        legRearLeft.setTextureSize(textureWidth, textureHeight);
        legRearLeft.addBox(-1F, 0F, -1F, 5, 13, 5);
        legRearLeft.setRotationPoint(4F, 11F, 7F);
        legFrontRight = new ModelRendererWildAnimals(this, 0, 50);
        legFrontRight.setTextureSize(textureWidth, textureHeight);
        legFrontRight.addBox(-3F, 0F, -3F, 5, 13, 5);
        legFrontRightRotPointXDefault = -5F;
        legFrontRightRotPointYDefault = 11F;
        legFrontRightRotPointZDefault = -6F;
        legFrontRight.setRotationPoint(legFrontRightRotPointXDefault, legFrontRightRotPointYDefault, legFrontRightRotPointZDefault);
        legFrontLeft = new ModelRendererWildAnimals(this, 0, 50);
        legFrontLeft.setTextureSize(textureWidth, textureHeight);
        legFrontLeft.addBox(-1F, 0F, -3F, 5, 13, 5);
        legFrontLeftRotPointXDefault = 4F;
        legFrontLeftRotPointYDefault = 11F;
        legFrontLeftRotPointZDefault = -6F;
        legFrontLeft.setRotationPoint(legFrontLeftRotPointXDefault, legFrontLeftRotPointYDefault, legFrontLeftRotPointZDefault);

    	// head for baby entity
        childHead = new ModelRendererWildAnimals(this, 0, 0);
        childHead.setTextureSize(textureWidth, textureHeight);
        childHead.addBox(-4F, -4F, -6F, 10, 10, 7);
        childHeadRotPointXDefault = 0F;
        childHeadRotPointYDefault = 18 - par1;
        childHeadRotPointZDefault = -9.0F;
        childHead.setRotationPoint(childHeadRotPointXDefault, childHeadRotPointYDefault, childHeadRotPointZDefault);
        // add head's children models (ears, trunk, tusks)
        childEar1 = new ModelRendererWildAnimals(this, 34, 8);
        childEar1.setTextureSize(textureWidth, textureHeight);
        childEar1.addBox(-7F, -4F, -0.5F, 7, 8, 1);
        childEar1.setRotationPoint(-3F, -1F, -2F);
        childHead.addChild(childEar1);
        childEar2 = new ModelRendererWildAnimals(this, 34, 8);
        childEar2.setTextureSize(textureWidth, textureHeight);
        childEar2.addBox(0F, -4F, -0.5F, 7, 8, 1);
        childEar2.setRotationPoint(4F, -1F, -2F);
        childHead.addChild(childEar2);
        childTrunk1 = new ModelRendererWildAnimals(this, 20, 50);
        childTrunk1.setTextureSize(textureWidth, textureHeight);
        childTrunk1.addBox(-2F, 0F, -1.5F, 4, 8, 3);
        childTrunk1.setRotationPoint(1F, 0F, -6F);
        childHead.addChild(childTrunk1);

    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
	public void render(Entity parEntity, float parTime, float parSwingSuppress, float par4, float parHeadAngleY, float parHeadAngleX, float par7)
    {
    	renderElephant((EntityElephant) parEntity, parTime, parSwingSuppress, par4, parHeadAngleY, parHeadAngleX, par7);
    }
    
	public void renderElephant(EntityElephant parEntity, float parTime, float parSwingSuppress, float par4, float parHeadAngleY, float parHeadAngleX, float par7)
    {
        setRotationAngles(parTime, parSwingSuppress, par4, parHeadAngleY, parHeadAngleX, par7, parEntity);

        // scale the whole thing for big or small entities
        GL11.glPushMatrix();
        GL11.glTranslatef(0F, -1.5F, 0F);
    	GL11.glScalef(parEntity.getScaleFactor(), parEntity.getScaleFactor(), parEntity.getScaleFactor());

        if (this.isChild)
        {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, this.field_78145_g * par7, this.field_78151_h * par7);
            childHead.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
            body.render(par7);
            // scale legs slightly to reduce render flicker on overlapping areas
            GL11.glPushMatrix();
            GL11.glScalef(0.99F, 1.00F, 0.99F);
            legRearRight.render(par7);
            legRearLeft.render(par7);
            legFrontRight.render(par7);
            legFrontLeft.render(par7);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
        else
        {
            head.render(par7);
            body.render(par7);
            // scale legs slightly to reduce render flicker on overlapping areas
            GL11.glPushMatrix();
            GL11.glScalef(0.99F, 1.00F, 0.99F);
            legRearRight.render(par7);
            legRearLeft.render(par7);
            legFrontRight.render(par7);
            legFrontLeft.render(par7);
            GL11.glPopMatrix();
        }
        
        // don't forget to pop the matrix for overall scaling
        GL11.glPopMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float parTime, float parSwingSuppress, float par3, float parHeadAngleY, float parHeadAngleX, float par6, EntityElephant parEntity)
    {
    	// return rotation point in case there was previous rearing animation
    	head.setRotationPoint(headRotPointXDefault, headRotPointYDefault, headRotPointZDefault);
        body.setRotationPoint(bodyRotPointXDefault, bodyRotPointYDefault, bodyRotPointZDefault);
        legFrontRight.setRotationPoint(legFrontRightRotPointXDefault, legFrontRightRotPointYDefault, legFrontRightRotPointZDefault);
        legFrontLeft.setRotationPoint(legFrontLeftRotPointXDefault, legFrontLeftRotPointYDefault, legFrontLeftRotPointZDefault);
        childHead.setRotationPoint(childHeadRotPointXDefault, childHeadRotPointYDefault, childHeadRotPointZDefault);

        head.rotateAngleX = degToRad(parHeadAngleX);
        head.rotateAngleY = degToRad(parHeadAngleY);
        childHead.rotateAngleX = degToRad(parHeadAngleX);
        childHead.rotateAngleY = degToRad(parHeadAngleY);
        body.rotateAngleX = ((float)Math.PI / 2F);
        // swingSuppress goes to 0 when still so gates the movement
        legRearRight.rotateAngleX = MathHelper.cos(parTime * 0.6662F) * 1.4F * parSwingSuppress;
        legRearLeft.rotateAngleX = MathHelper.cos(parTime * 0.6662F + (float)Math.PI) * 1.4F * parSwingSuppress;
        legFrontRight.rotateAngleX = MathHelper.cos(parTime * 0.6662F + (float)Math.PI) * 1.4F * parSwingSuppress;
        legFrontLeft.rotateAngleX = MathHelper.cos(parTime * 0.6662F) * 1.4F * parSwingSuppress;
        trunk1.rotateAngleX = MathHelper.cos(degToRad(parEntity.ticksExisted*7)) * degToRad(15);
        childTrunk1.rotateAngleX = MathHelper.cos(degToRad(parEntity.ticksExisted*7)) * degToRad(15);
        trunk2.rotateAngleX = trunk1.rotateAngleX * 3;

        // flick ears
        ear1.rotateAngleY = (float) Math.pow(MathHelper.cos(degToRad(parEntity.ticksExisted*3)), 6) * degToRad(15);
        ear2.rotateAngleY = (float) Math.pow(MathHelper.cos(degToRad(parEntity.ticksExisted*3)), 6) * degToRad(-15);
        childEar1.rotateAngleY = (float) Math.pow(MathHelper.cos(degToRad(parEntity.ticksExisted*3)), 6) * degToRad(15);
        childEar2.rotateAngleY = (float) Math.pow(MathHelper.cos(degToRad(parEntity.ticksExisted*3)), 6) * degToRad(-15);

        // raise trunk if in water 
        if (parEntity.isInWater())
        {
        	trunk1.rotateAngleX = degToRad(-150);
        	trunk2.rotateAngleX = degToRad(-20);
        	childTrunk1.rotateAngleX = degToRad(-150);
        }
        
        // perform rearing animation if appropriate
        if (parEntity.isRearing())
        {
        	int rearingCounter = parEntity.getRearingCounter();
        	
        	// move retain connection between body parts, hind legs stay where they were
        	head.setRotationPoint(headRotPointXDefault, headRotPointYDefault+rearingOffsetCycle[rearingCounter][0], headRotPointZDefault+rearingOffsetCycle[rearingCounter][1]);
            body.setRotationPoint(bodyRotPointXDefault, bodyRotPointYDefault+rearingOffsetCycle[rearingCounter][2], bodyRotPointZDefault+rearingOffsetCycle[rearingCounter][3]);
            legFrontRight.setRotationPoint(legFrontRightRotPointXDefault, legFrontRightRotPointYDefault+rearingOffsetCycle[rearingCounter][4], legFrontRightRotPointZDefault+rearingOffsetCycle[rearingCounter][5]);
            legFrontLeft.setRotationPoint(legFrontLeftRotPointXDefault, legFrontLeftRotPointYDefault+rearingOffsetCycle[rearingCounter][6], legFrontLeftRotPointZDefault+rearingOffsetCycle[rearingCounter][7]);
            childHead.setRotationPoint(childHeadRotPointXDefault, childHeadRotPointYDefault+rearingOffsetCycle[rearingCounter][8], childHeadRotPointZDefault+rearingOffsetCycle[rearingCounter][9]);
            
            // rotate for rearing
        	body.rotateAngleX += degToRad(rearingAngleCycle[rearingCounter][0]);
            head.rotateAngleX += degToRad(rearingAngleCycle[rearingCounter][0]);
        	childHead.rotateAngleX += degToRad(rearingAngleCycle[rearingCounter][0]);
        	trunk1.rotateAngleX = degToRad(rearingAngleCycle[rearingCounter][1]);
        	trunk2.rotateAngleX = degToRad(rearingAngleCycle[rearingCounter][2]);
        	childTrunk1.rotateAngleX = degToRad(rearingAngleCycle[rearingCounter][1]);
        	legFrontRight.rotateAngleX = degToRad(rearingAngleCycle[rearingCounter][0]);
        	legFrontLeft.rotateAngleX = degToRad(rearingAngleCycle[rearingCounter][0]);
        }
    }
}