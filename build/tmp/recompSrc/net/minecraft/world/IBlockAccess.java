package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public interface IBlockAccess
{
    Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_);

    TileEntity getTileEntity(int x, int y, int z);

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    @SideOnly(Side.CLIENT)
    int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_);

    /**
     * Returns the block metadata at coords x,y,z
     */
    int getBlockMetadata(int p_72805_1_, int p_72805_2_, int p_72805_3_);

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    int isBlockProvidingPowerTo(int x, int y, int z, int directionIn);

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    boolean isAirBlock(int x, int y, int z);

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    @SideOnly(Side.CLIENT)
    BiomeGenBase getBiomeGenForCoords(int x, int z);

    /**
     * Returns maximum world height.
     */
    @SideOnly(Side.CLIENT)
    int getHeight();

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    @SideOnly(Side.CLIENT)
    boolean extendedLevelsInChunkCache();

    /**
     * FORGE: isSideSolid, pulled up from {@link World}
     *
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @param side Side
     * @param _default default return value
     * @return if the block is solid on the side
     */
    boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default);
}