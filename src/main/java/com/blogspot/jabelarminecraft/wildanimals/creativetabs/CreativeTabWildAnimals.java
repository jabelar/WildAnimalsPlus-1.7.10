package com.blogspot.jabelarminecraft.wildanimals.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabWildAnimals extends CreativeTabs 
{
	public CreativeTabWildAnimals(String tabLabel)
	{
		super(tabLabel);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return null; // TODO return proper icon
	}
}