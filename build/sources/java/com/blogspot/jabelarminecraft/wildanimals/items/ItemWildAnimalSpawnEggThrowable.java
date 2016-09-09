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

package com.blogspot.jabelarminecraft.wildanimals.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.entities.eggs.EntityWildAnimalsEgg;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWildAnimalSpawnEggThrowable extends Item
{
    public int colorBase;
    public int colorSpots;
    public String entityToSpawnName = "";
    protected String entityToSpawnNameFull = "";
    protected EntityWildAnimalsEgg entityEgg;
    private IIcon theIcon;
    
    public ItemWildAnimalSpawnEggThrowable(String parEntityToSpawnName, int parPrimaryColor, int parSecondaryColor)
    {
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setEntityToSpawnName(parEntityToSpawnName);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, 
          EntityPlayer par3EntityPlayer)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / 
              (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!par2World.isRemote)
        {
            entityEgg = new EntityWildAnimalsEgg(par2World, par3EntityPlayer, colorBase, colorSpots);
            entityEgg.setEntityToSpawn(entityToSpawnNameFull);
            par2World.spawnEntityInWorld(entityEgg);
        }

        return par1ItemStack;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int parColorType)
    {
        return (parColorType == 0) ? colorBase : colorSpots;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
       public String getItemStackDisplayName(ItemStack par1ItemStack)
       {
           return "Spawn "+entityToSpawnName;
       }  


    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        theIcon = par1IconRegister.registerIcon(getIconString() + "_overlay");
    }
    
    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int parDamageVal, int parRenderPass)
    {
        return parRenderPass > 0 ? theIcon : super.getIconFromDamageForRenderPass(parDamageVal, parRenderPass);
    }
    
    public void setColors(int parColorBase, int parColorSpots)
    {
    	colorBase = parColorBase;
    	colorSpots = parColorSpots;
    }
    
    public int getColorBase()
    {
    	return colorBase;
    }
    
    public int getColorSpots()
    {
    	return colorSpots;
    }
    
    public void setEntityToSpawnName(String parEntityToSpawnName)
    {
        entityToSpawnName = parEntityToSpawnName;
        entityToSpawnNameFull = WildAnimals.MODID+"."+entityToSpawnName; // need to extend with name of mod

    }

}