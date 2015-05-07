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

package com.blogspot.jabelarminecraft.wildanimals.entities;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NBTTagCompound;

public interface IModEntity {
	
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
