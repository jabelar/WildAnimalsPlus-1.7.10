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

package com.blogspot.jabelarminecraft.wildanimals.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;

import cpw.mods.fml.client.config.GuiConfig;
 
public class WildAnimalsConfigGUI extends GuiConfig 
{
    public WildAnimalsConfigGUI(GuiScreen parent) 
    {
        super(
                parent,
                new ConfigElement(
                        WildAnimals.config.getCategory(Configuration.CATEGORY_GENERAL)
                 ).getChildElements(),
                 WildAnimals.MODID, 
                 false, 
                 true, 
                 EnumChatFormatting.RED+"Changes Only Take Effect If You Completely Quit Minecraft Then Load World Again"
             );
         titleLine2 = GuiConfig.getAbridgedConfigPath(WildAnimals.config.toString());
    }
}