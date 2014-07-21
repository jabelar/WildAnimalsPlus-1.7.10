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


package com.blogspot.jabelarminecraft.wildanimals.entities.eggs;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemWildAnimalsEgg extends EntityItem
{
	public EntityItemWildAnimalsEgg(World parWorld)
	{
		super(parWorld);
	}

	public EntityItemWildAnimalsEgg(World parWorld, double parX, double parY,
			double parZ, ItemStack parItemStack) 
	{
		super(parWorld, parX, parY, parZ, parItemStack);
		// TODO Auto-generated constructor stub
	}

}
