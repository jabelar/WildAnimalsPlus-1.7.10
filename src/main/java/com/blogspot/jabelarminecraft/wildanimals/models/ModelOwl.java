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

package com.blogspot.jabelarminecraft.wildanimals.models;

/**
 * @author jabelar
 *
 */
public class ModelOwl extends ModelBirdOfPrey
{
    public ModelOwl()
    {
        super();
        head.cubeList.clear();
        head.addBox(-8F, -8F, -4F, 11, 11, 4);
        head.setRotationPoint(0F, -1F, -9F);
    }
    
}
