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

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!par2World.isRemote)
        {
            entityEgg = new EntityWildAnimalsEgg(par2World, par3EntityPlayer);
            entityEgg.setEntityToSpawn(entityToSpawnNameFull);
            par2World.spawnEntityInWorld(entityEgg);
            entityEgg.setEntityItem(par1ItemStack);
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
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 > 0 ? theIcon : super.getIconFromDamageForRenderPass(par1, par2);
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