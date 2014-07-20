package com.blogspot.jabelarminecraft.wildanimals.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWildAnimals extends ModelBase
{
    // create an animation cycle
    // for movement based animations you need to measure distance moved
    // and perform number of cycles per block distance moved.
    protected double distanceMovedTotal = 0.0D;
    // don't make this too large or animations will be skipped
    protected static final double CYCLES_PER_BLOCK = 3.0D; 
    protected int cycleIndex = 0;
    
	// ***************************************
	// Helper functions
	// ***************************************
    
    protected void updateDistanceMovedTotal(Entity parEntity) 
    {
        distanceMovedTotal += parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, 
              parEntity.prevPosZ);
    }
    
    protected double getDistanceMovedTotal(Entity parEntity) 
    {
        return (distanceMovedTotal);
    }

    protected float degToRad(float degrees)
    {
        return degrees * (float)Math.PI / 180 ;
    }
    
    protected void setRotation(ModelRenderer model, float rotX, float rotY, float rotZ)
    {
        model.rotateAngleX = degToRad(rotX);
        model.rotateAngleY = degToRad(rotY);
        model.rotateAngleZ = degToRad(rotZ);        
    }

    // spin methods are good for testing and debug rotation points and offsets in the model
    protected void spinX(ModelRenderer model)
    {
        model.rotateAngleX += degToRad(0.5F);
    }
    
    protected void spinY(ModelRenderer model)
    {
        model.rotateAngleY += degToRad(0.5F);
    }
    
    protected void spinZ(ModelRenderer model)
    {
        model.rotateAngleZ += degToRad(0.5F);
    }
    
    // This is really useful for converting the source from a Techne model export
    // which will have absolute rotation points that need to be converted before
    // creating the addChild() relationship
    protected void convertToChildOf(ModelRenderer parChild, ModelRenderer parParent)
    {
    	// move child rotation point to be relative to parent
    	parChild.rotationPointX -= parParent.rotationPointX;
    	parChild.rotationPointY -= parParent.rotationPointY;
    	parChild.rotationPointZ -= parParent.rotationPointZ;
    	// make rotations relative to parent
    	parChild.rotateAngleX -= parParent.rotateAngleX;
    	parChild.rotateAngleY -= parParent.rotateAngleY;
    	parChild.rotateAngleZ -= parParent.rotateAngleZ;
    	// create relationship
    	parParent.addChild(parChild);
    }
}
