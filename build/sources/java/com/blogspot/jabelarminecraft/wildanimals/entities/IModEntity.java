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

import net.minecraft.nbt.NBTTagCompound;

public interface IModEntity 
{
    // set up AI tasks
    void setupAI();
    
    // use clear tasks for subclasses then build up their ai task list specifically
    void clearAITasks();
    
    // initialize the tag compound used for syncing custom entity data
    void initSyncDataCompound();
    
    NBTTagCompound getSyncDataCompound();
    
    void setSyncDataCompound(NBTTagCompound parCompound);
    
    // method to send sync of extended properties from server to clients
    void sendEntitySyncPacket();

    // common encapsulation methods
    void setScaleFactor(float parScaleFactor);
    
    float getScaleFactor();

}
