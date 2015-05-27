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

package com.blogspot.jabelarminecraft.wildanimals.utilities;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.BlockSnapshot;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.entities.IModEntity;
import com.blogspot.jabelarminecraft.wildanimals.networking.MessageSyncEntityToClient;
import com.blogspot.jabelarminecraft.wildanimals.networking.MessageSyncEntityToServer;

/**
 * @author jabelar
 *
 */
public class Utilities 
{
	/*
	 * Text Utilities
	 */
	
	public static String stringToRainbow(String parString, boolean parReturnToBlack)
	{
		int stringLength = parString.length();
		if (stringLength < 1)
		{
			return "";
		}
		String outputString = "";
		EnumChatFormatting[] colorChar = 
			{
			EnumChatFormatting.RED,
			EnumChatFormatting.GOLD,
			EnumChatFormatting.YELLOW,
			EnumChatFormatting.GREEN,
			EnumChatFormatting.AQUA,
			EnumChatFormatting.BLUE,
			EnumChatFormatting.LIGHT_PURPLE,
			EnumChatFormatting.DARK_PURPLE
			};
		for (int i = 0; i < stringLength; i++)
		{
			outputString = outputString+colorChar[i%8]+parString.substring(i, i+1);
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+EnumChatFormatting.BLACK;
		}
		return outputString+EnumChatFormatting.WHITE;
	}

	// by default return to white (for chat formatting).
	public static String stringToRainbow(String parString)
	{
		return stringToRainbow(parString, false);
	}
	
	public static String stringToGolden(String parString, int parShineLocation, boolean parReturnToBlack)
	{
		int stringLength = parString.length();
		if (stringLength < 1)
		{
			return "";
		}
		String outputString = "";
		for (int i = 0; i < stringLength; i++)
		{
			if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==0)
			{
				outputString = outputString+EnumChatFormatting.WHITE+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==1)
			{
				outputString = outputString+EnumChatFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==87)
			{
				outputString = outputString+EnumChatFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else
			{
				outputString = outputString+EnumChatFormatting.GOLD+parString.substring(i, i+1);								
			}
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+EnumChatFormatting.BLACK;
		}
		return outputString+EnumChatFormatting.WHITE;
	}

	// by default return to white (for chat formatting).
	public static String stringToGolden(String parString, int parShineLocation)
	{
		return stringToGolden(parString, parShineLocation, false);
	}

	public static Entity getEntityByID(int entityID, World world)        
	{         
	    for(Object o: world.getLoadedEntityList())                
	    {                        
	        if(((Entity)o).getEntityId() == entityID)                        
	        {   
	        	// DEBUG
	            // System.out.println("Found the entity");                                
	            return ((Entity)o);                        
	        }                
	    }                
	    return null;        
	} 

	/**
	* Based on code from http://pages.cs.wisc.edu/~ltorrey/cs302/examples/PigLatinTranslator.java
	* Method to translate a sentence word by word.
	* @param s The sentence in English
	* @return The pig latin version
	*/
	public static String toPigLatin(String s) 
	{
		String latin = "";
	    int i = 0;
	    while (i<s.length()) 
	    {
	    	// Take care of punctuation and spaces
	    	while (i<s.length() && !isLetter(s.charAt(i))) 
	    	{
	    		latin = latin + s.charAt(i);
	    		i++;
	    	}

	    	// If there aren't any words left, stop.
	    	if (i>=s.length()) break;

	    	// Otherwise we're at the beginning of a word.
	    	int begin = i;
	    	while (i<s.length() && isLetter(s.charAt(i))) 
	    	{
	    		i++;
	    	}

	    	// Now we're at the end of a word, so translate it.
	    	int end = i;
	    	latin = latin + pigWord(s.substring(begin, end));
	    }
	    return latin;
	}

	/**
	* Method to test whether a character is a letter or not.
	* @param c The character to test
	* @return True if it's a letter
	*/
	private static boolean isLetter(char c) 
	{
		return ( (c >='A' && c <='Z') || (c >='a' && c <='z') );
	}

	/**
	* Method to translate one word into pig latin.
	* @param word The word in english
	* @return The pig latin version
	*/
	private static String pigWord(String word) 
	{
		int split = firstVowel(word);
		return word.substring(split)+"-"+word.substring(0, split)+"ay";
	}

	/**
	* Method to find the index of the first vowel in a word.
	* @param word The word to search
	* @return The index of the first vowel
	*/
	private static int firstVowel(String word) 
	{
		word = word.toLowerCase();
	    for (int i=0; i<word.length(); i++)
	    {
	    	if (word.charAt(i)=='a' || word.charAt(i)=='e' ||
	    	      word.charAt(i)=='i' || word.charAt(i)=='o' ||
	              word.charAt(i)=='u')
	    	{
	    		return i;
	    	}
	    }
	    	return 0;
	}
	  
	/*
	 * Networking packet utilities
	 */
	
    public static void sendEntitySyncPacketToClient(IModEntity parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (!theEntity.worldObj.isRemote)
        {
        	// DEBUG
        	System.out.println("sendEntitySyncPacket from server");
            WildAnimals.network.sendToAll(new MessageSyncEntityToClient(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }

    public static void sendEntitySyncPacketToServer(IModEntity parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (theEntity.worldObj.isRemote)
        {
        	// DEBUG
        	System.out.println("sendEntitySyncPacket from client");
            WildAnimals.network.sendToServer(new MessageSyncEntityToServer(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }
    
    /**
     * Sets the block ID and metadata at a given location. Args: X, Y, Z, new block ID, new metadata, flags. Flag 1 will
     * cause a block update. Flag 2 will send the change to clients (you almost always want parChunk). Flag 4 prevents the
     * block from being re-rendered, if parChunk is a client world. Flags can be added together.
     */
    public static boolean setBlockFast(World parWorld, int parX, int parY, int parZ, Block parBlock, int parMetaData, int parFlag)
    {
        // Make sure position is within valid range
        if (parX >= -30000000 && parZ >= -30000000 && parX < 30000000 && parZ < 30000000)
        {
            if (parY < 0)
            {
                return false;
            }
            else if (parY >= 256)
            {
                return false;
            }
            else
            {
                Chunk chunk = parWorld.getChunkFromChunkCoords(parX >> 4, parZ >> 4);
                Block existingBlock = null;
                BlockSnapshot blockSnapshot = null;

                if ((parFlag & 1) != 0)
                {
                    existingBlock = chunk.getBlock(parX & 15, parY, parZ & 15);
                }

                if (parWorld.captureBlockSnapshots && !parWorld.isRemote)
                {
                    blockSnapshot = BlockSnapshot.getBlockSnapshot(parWorld, parX, parY, parZ, parFlag);
                    parWorld.capturedBlockSnapshots.add(blockSnapshot);
                }

                boolean setBlockSuceeded = setBlockInChunkFast(chunk, parX & 15, parY, parZ & 15, parBlock, parMetaData);

                if (!setBlockSuceeded && blockSnapshot != null)
                {
                    parWorld.capturedBlockSnapshots.remove(blockSnapshot);
                    blockSnapshot = null;
                }

                if (setBlockSuceeded && blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    // Modularize client and physic updates
                    parWorld.markAndNotifyBlock(parX, parY, parZ, chunk, existingBlock, parBlock, parFlag);
                }

                return setBlockSuceeded;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean setBlockInChunkFast(Chunk parChunk, int parX, int parY, int parZ, Block parBlock, int parMetaData)
    {
        int mapKey = parZ << 4 | parX;

        if (parY >= parChunk.precipitationHeightMap[mapKey] - 1)
        {
            parChunk.precipitationHeightMap[mapKey] = -999;
        }

        Block existingBlock = parChunk.getBlock(parX, parY, parZ);
        int existingMetaData = parChunk.getBlockMetadata(parX, parY, parZ);

        if (existingBlock == parBlock && existingMetaData == parMetaData)
        {
            return false;
        }
        else
        {
            ExtendedBlockStorage extendedblockstorage = parChunk.getBlockStorageArray()[parY >> 4];

            if (extendedblockstorage == null)
            {
                if (parBlock == Blocks.air)
                {
                    return false;
                }

                extendedblockstorage = parChunk.getBlockStorageArray()[parY >> 4] = new ExtendedBlockStorage(parY >> 4 << 4, !parChunk.worldObj.provider.hasNoSky);
            }

            int worldPosX = parChunk.xPosition * 16 + parX;
            int worldPosZ = parChunk.zPosition * 16 + parZ;

            if (!parChunk.worldObj.isRemote)
            {
                existingBlock.onBlockPreDestroy(parChunk.worldObj, worldPosX, parY, worldPosZ, existingMetaData);
            }

            extendedblockstorage.func_150818_a(parX, parY & 15, parZ, parBlock);
            extendedblockstorage.setExtBlockMetadata(parX, parY & 15, parZ, parMetaData); // This line duplicates the one below, so breakBlock fires with valid worldstate

            if (!parChunk.worldObj.isRemote)
            {
                existingBlock.breakBlock(parChunk.worldObj, worldPosX, parY, worldPosZ, existingBlock, existingMetaData);
                // After breakBlock a phantom TE might have been created with incorrect meta. This attempts to kill that phantom TE so the normal one can be create properly later
                TileEntity te = parChunk.getTileEntityUnsafe(parX & 0x0F, parY, parZ & 0x0F);
                if (te != null && te.shouldRefresh(existingBlock, parChunk.getBlock(parX & 0x0F, parY, parZ & 0x0F), existingMetaData, parChunk.getBlockMetadata(parX & 0x0F, parY, parZ & 0x0F), parChunk.worldObj, worldPosX, parY, worldPosZ))
                {
                    parChunk.removeTileEntity(parX & 0x0F, parY, parZ & 0x0F);
                }
            }
            else if (existingBlock.hasTileEntity(existingMetaData))
            {
                TileEntity te = parChunk.getTileEntityUnsafe(parX & 0x0F, parY, parZ & 0x0F);
                if (te != null && te.shouldRefresh(existingBlock, parBlock, existingMetaData, parMetaData, parChunk.worldObj, worldPosX, parY, worldPosZ))
                {
                    parChunk.worldObj.removeTileEntity(worldPosX, parY, worldPosZ);
                }
            }

            if (extendedblockstorage.getBlockByExtId(parX, parY & 15, parZ) != parBlock)
            {
                return false;
            }
            else
            {
                extendedblockstorage.setExtBlockMetadata(parX, parY & 15, parZ, parMetaData);

                TileEntity tileentity;

                if (!parChunk.worldObj.isRemote)
                {
                    parBlock.onBlockAdded(parChunk.worldObj, worldPosX, parY, worldPosZ);
                }

                if (parBlock.hasTileEntity(parMetaData))
                {
                    tileentity = parChunk.func_150806_e(parX, parY, parZ);

                    if (tileentity != null)
                    {
                        tileentity.updateContainingBlockInfo();
                        tileentity.blockMetadata = parMetaData;
                    }
                }

                parChunk.isModified = true;
                return true;
            }
        }
    }

    // just putting a human-readable name to the function
    // note this will only work in the current dimension
    public static EntityPlayer getPlayerFromUUID(World parWorld, UUID parUUID)
    {
        return parWorld.func_152378_a(parUUID);    
    }
    
    // this will work across all dimensions
    // thanks to diesieben07 for this tip on http://www.minecraftforge.net/forum/index.php?topic=27715.0
    public static EntityPlayer getPlayerOnServerFromUUID(UUID parUUID) 
    {
        if (parUUID == null) 
        {
            return null;
        }
        List<EntityPlayerMP> allPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayerMP player : allPlayers) 
        {
            if (player.getUniqueID().equals(parUUID)) 
            {
                return player;
            }
        }
        return null;
    }

    
    /**
     * A method used to see if an entity is a suitable target through a number of checks.
     */
    public static boolean isSuitableTarget(EntityLivingBase theAttackerEntity, 
            EntityLivingBase parPossibleTargetEntity,
            boolean parShouldCheckSight)
    {
        if (parPossibleTargetEntity == null)
        {
//            // DEBUG
//            System.out.println("Target isn't suitable because it is null");
            return false;
        }
        else if (parPossibleTargetEntity == theAttackerEntity)
        {
            // DEBUG
            System.out.println("Target isn't suitable because it is itself");
            return false;
        }
        else if (!parPossibleTargetEntity.isEntityAlive())
        {
            // DEBUG
            System.out.println("Target isn't suitable because it is dead");
            return false;
        }
        else if (theAttackerEntity.isOnSameTeam(parPossibleTargetEntity))
        {
            // DEBUG
            System.out.println("Target isn't suitable because it is on same team");
            return false;
        }
//        else if (parPossibleTargetEntity instanceof EntityPlayer && ((EntityPlayer)parPossibleTargetEntity).capabilities.disableDamage)
//        {
//            // DEBUG
//            System.out.println("Target isn't suitable because player can't take damage");
//            return false;
//        }
        else if (theAttackerEntity instanceof EntityLiving && parShouldCheckSight)
        {
            // DEBUG
            System.out.println("The attacker can see target = "+((EntityLiving)theAttackerEntity).getEntitySenses().canSee(parPossibleTargetEntity));
            return ((EntityLiving)theAttackerEntity).getEntitySenses().canSee(parPossibleTargetEntity);
        }
        else
        {
            return true;
        }
    }

    
//    // This is mostly copied from the EntityRenderer#getMouseOver() method
//    public static MovingObjectPosition getMouseOverExtended(float parDist)
//    {
//        double dist = parDist;
//        Minecraft mc = FMLClientHandler.instance().getClient();
//        EntityLivingBase theRenderViewEntity = mc.renderViewEntity;
//        AxisAlignedBB theViewBoundingBox = AxisAlignedBB.getBoundingBox(
//                theRenderViewEntity.posX-0.5D,
//                theRenderViewEntity.posY-0.0D,
//                theRenderViewEntity.posZ-0.5D,
//                theRenderViewEntity.posX+0.5D,
//                theRenderViewEntity.posY+1.5D,
//                theRenderViewEntity.posZ+0.5D
//                );
//        MovingObjectPosition returnMOP = null;
//        if (mc.theWorld != null)
//        {
//            returnMOP = theRenderViewEntity.rayTrace(parDist, 0);
//            Vec3 pos = theRenderViewEntity.getPosition(0).addVector(0.0D, theRenderViewEntity.getEyeHeight(), 0.0D);
//            if (returnMOP != null)
//            {
//                dist = returnMOP.hitVec.distanceTo(pos);
//            }
//            
//            Vec3 lookvec = theRenderViewEntity.getLook(0);
//            Vec3 var8 = pos.addVector(lookvec.xCoord * dist, lookvec.yCoord * dist, lookvec.zCoord * dist);
//            Entity pointedEntity = null;
//            float var9 = 1.0F;
//            @SuppressWarnings("unchecked")
//            List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity, theViewBoundingBox.addCoord(lookvec.xCoord * dist, lookvec.yCoord * dist, lookvec.zCoord * dist).expand(var9, var9, var9));
//            
//            for (Entity entity : list)
//            {
//                if (entity.canBeCollidedWith())
//                {
//                    float bordersize = entity.getCollisionBorderSize();
//                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(entity.posX-entity.width/2, entity.posY, entity.posZ-entity.width/2, entity.posX+entity.width/2, entity.posY+entity.height, entity.posZ+entity.width/2);
//                    aabb.expand(bordersize, bordersize, bordersize);
//                    MovingObjectPosition mop0 = aabb.calculateIntercept(pos, var8);
//                    
//                    if (aabb.isVecInside(pos))
//                    {
//                        if (0.0D < dist || dist == 0.0D)
//                        {
//                            pointedEntity = entity;
//                            dist = 0.0D;
//                        }
//                    } else if (mop0 != null)
//                    {
//                        double d1 = pos.distanceTo(mop0.hitVec);
//                        
//                        if (d1 < d || d == 0.0D)
//                        {
//                            pointedEntity = entity;
//                            d = d1;
//                        }
//                    }
//                }
//            }
//            
//            if (pointedEntity != null && (d < calcdist || returnMOP == null))
//            {
//                returnMOP = new MovingObjectPosition(pointedEntity);
//            }
//        
//        }
//        return returnMOP;
//    }

    public static float getYawFromVec(Vec3 parVec)
    {
        // The coordinate system for Minecraft is a bit backwards as explained 
        // at https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
        return (float) -Math.toDegrees(Math.atan2(parVec.xCoord, parVec.zCoord));
    }
    
    public static float getPitchFromVec(Vec3 parVec)
    {
        // The coordinate system for Minecraft is a bit backwards as explained 
        // at https://github.com/chraft/c-raft/wiki/Vectors,-Location,-Yaw-and-Pitch-in-C%23raft
        Vec3 theVec = parVec.normalize();
        return (float) Math.toDegrees(Math.asin(theVec.yCoord));
    }
    
    /**
     * True if the entity has an unobstructed line of travel to the waypoint.
     */
    public static boolean isCourseTraversable(Entity parEntity, double parX, double parY, double parZ)
    {
        double theDistance = MathHelper.sqrt_double(parX * parX + parY * parY + parZ * parZ);

        double incrementX = (parX - parEntity.posX) / theDistance;
        double incrementY = (parY - parEntity.posY) / theDistance;
        double incrementZ = (parZ - parEntity.posZ) / theDistance;
        AxisAlignedBB entityBoundingBox = parEntity.boundingBox.copy();

        for (int i = 1; i < theDistance; ++i)
        {
            entityBoundingBox.offset(incrementX, incrementY, incrementZ);

            if (!parEntity.worldObj.getCollidingBoundingBoxes(parEntity, entityBoundingBox).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}
