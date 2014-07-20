package com.blogspot.jabelarminecraft.wildanimals.entities;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NBTTagCompound;

public interface IWildAnimalsEntity {
	
    // set up AI tasks
    void setupAI();
    
    // use clear tasks for subclasses then build up their ai task list specifically
    void clearAITasks();

	// methods for extended properties
	
    void initExtProps();
    
	public NBTTagCompound getExtProps();
	
	public void setExtProps(NBTTagCompound parCompound);
	
	// buffer doesn't need to be returned as it is manipulated directly
	public void getExtPropsToBuffer(ByteBufOutputStream parBBOS);  
	
	public void setExtPropsFromBuffer(ByteBufInputStream parBBIS);

	// common encapsulation methods
    public void setScaleFactor(float parScaleFactor);
    
    public float getScaleFactor();
    
    // method to send sync of extended properties from server to clients
    public void sendEntitySyncPacket();

}
