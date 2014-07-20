package com.blogspot.jabelarminecraft.wildanimals.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;

import cpw.mods.fml.client.config.GuiConfig;
 
public class WildAnimalsConfigGUI extends GuiConfig 
{
    public WildAnimalsConfigGUI(GuiScreen parent) 
    {
        super(parent,
                new ConfigElement(WildAnimals.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                WildAnimals.MODID, false, false, GuiConfig.getAbridgedConfigPath(WildAnimals.config.toString()));
    }
}