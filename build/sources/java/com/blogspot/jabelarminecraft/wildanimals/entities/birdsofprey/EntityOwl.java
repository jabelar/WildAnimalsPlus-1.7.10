/**
    Copyright (C) 2015 by jabelar

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

package com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey;

import net.minecraft.world.World;

/**
 * @author jabelar
 * 
 */
public class EntityOwl extends EntityBirdOfPrey
{
    protected final static float SCALE_FACTOR = 0.7F;
    
    public EntityOwl(World parWorld)
    {
        super(parWorld);
        setScaleFactor(SCALE_FACTOR);
        setSize(width*SCALE_FACTOR, height*SCALE_FACTOR);
    }
    
    @Override
    public boolean isNocturnal()
    {
        return true;
    }
}
