package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional
{
    public static final int[][] bedDirections = new int[][] {{0, 1}, { -1, 0}, {0, -1}, {1, 0}};
    @SideOnly(Side.CLIENT)
    private IIcon[] iconEnd;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconTop;
    private static final String __OBFID = "CL_00000198";

    public BlockBed()
    {
        super(Material.cloth);
        this.setBedBounds();
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            int i1 = worldIn.getBlockMetadata(x, y, z);

            if (!isBlockHeadOfBed(i1))
            {
                int j1 = getDirection(i1);
                x += bedDirections[j1][0];
                z += bedDirections[j1][1];

                if (worldIn.getBlock(x, y, z) != this)
                {
                    return true;
                }

                i1 = worldIn.getBlockMetadata(x, y, z);
            }

            if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(x, z) != BiomeGenBase.hell)
            {
                if (isBedOccupied(i1))
                {
                    EntityPlayer entityplayer1 = null;
                    Iterator iterator = worldIn.playerEntities.iterator();

                    while (iterator.hasNext())
                    {
                        EntityPlayer entityplayer2 = (EntityPlayer)iterator.next();

                        if (entityplayer2.isPlayerSleeping())
                        {
                            ChunkCoordinates chunkcoordinates = entityplayer2.playerLocation;

                            if (chunkcoordinates.posX == x && chunkcoordinates.posY == y && chunkcoordinates.posZ == z)
                            {
                                entityplayer1 = entityplayer2;
                            }
                        }
                    }

                    if (entityplayer1 != null)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied", new Object[0]));
                        return true;
                    }

                    setBedOccupied(worldIn, x, y, z, false);
                }

                EntityPlayer.EnumStatus enumstatus = player.sleepInBedAt(x, y, z);

                if (enumstatus == EntityPlayer.EnumStatus.OK)
                {
                    setBedOccupied(worldIn, x, y, z, true);
                    return true;
                }
                else
                {
                    if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
                    }
                    else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
                    }

                    return true;
                }
            }
            else
            {
                double d2 = (double)x + 0.5D;
                double d0 = (double)y + 0.5D;
                double d1 = (double)z + 0.5D;
                worldIn.setBlockToAir(x, y, z);
                int k1 = getDirection(i1);
                x += bedDirections[k1][0];
                z += bedDirections[k1][1];

                if (worldIn.getBlock(x, y, z) == this)
                {
                    worldIn.setBlockToAir(x, y, z);
                    d2 = (d2 + (double)x + 0.5D) / 2.0D;
                    d0 = (d0 + (double)y + 0.5D) / 2.0D;
                    d1 = (d1 + (double)z + 0.5D) / 2.0D;
                }

                worldIn.newExplosion((Entity)null, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 5.0F, true, true);
                return true;
            }
        }
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 0)
        {
            return Blocks.planks.getBlockTextureFromSide(side);
        }
        else
        {
            int k = getDirection(meta);
            int l = Direction.bedDirection[k][side];
            int i1 = isBlockHeadOfBed(meta) ? 1 : 0;
            return (i1 != 1 || l != 2) && (i1 != 0 || l != 3) ? (l != 5 && l != 4 ? this.iconTop[i1] : this.iconSide[i1]) : this.iconEnd[i1];
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.iconTop = new IIcon[] {reg.registerIcon(this.getTextureName() + "_feet_top"), reg.registerIcon(this.getTextureName() + "_head_top")};
        this.iconEnd = new IIcon[] {reg.registerIcon(this.getTextureName() + "_feet_end"), reg.registerIcon(this.getTextureName() + "_head_end")};
        this.iconSide = new IIcon[] {reg.registerIcon(this.getTextureName() + "_feet_side"), reg.registerIcon(this.getTextureName() + "_head_side")};
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 14;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.setBedBounds();
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = getDirection(l);

        if (isBlockHeadOfBed(l))
        {
            if (worldIn.getBlock(x - bedDirections[i1][0], y, z - bedDirections[i1][1]) != this)
            {
                worldIn.setBlockToAir(x, y, z);
            }
        }
        else if (worldIn.getBlock(x + bedDirections[i1][0], y, z + bedDirections[i1][1]) != this)
        {
            worldIn.setBlockToAir(x, y, z);

            if (!worldIn.isRemote)
            {
                this.dropBlockAsItem(worldIn, x, y, z, l, 0);
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        /**
         * Returns whether or not this bed block is the head of the bed.
         */
        return isBlockHeadOfBed(meta) ? Item.getItemById(0) : Items.bed;
    }

    private void setBedBounds()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    /**
     * Returns whether or not this bed block is the head of the bed.
     */
    public static boolean isBlockHeadOfBed(int meta)
    {
        return (meta & 8) != 0;
    }

    public static boolean isBedOccupied(int meta)
    {
        return (meta & 4) != 0;
    }

    public static void setBedOccupied(World worldIn, int x, int y, int z, boolean occupied)
    {
        int l = worldIn.getBlockMetadata(x, y, z);

        if (occupied)
        {
            l |= 4;
        }
        else
        {
            l &= -5;
        }

        worldIn.setBlockMetadataWithNotify(x, y, z, l, 4);
    }

    public static ChunkCoordinates getSafeExitLocation(World worldIn, int x, int y, int z, int safeIndex)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);
        int j1 = BlockDirectional.getDirection(i1);

        for (int k1 = 0; k1 <= 1; ++k1)
        {
            int l1 = x - bedDirections[j1][0] * k1 - 1;
            int i2 = z - bedDirections[j1][1] * k1 - 1;
            int j2 = l1 + 2;
            int k2 = i2 + 2;

            for (int l2 = l1; l2 <= j2; ++l2)
            {
                for (int i3 = i2; i3 <= k2; ++i3)
                {
                    if (World.doesBlockHaveSolidTopSurface(worldIn, l2, y - 1, i3) && !worldIn.getBlock(l2, y, i3).getMaterial().isOpaque() && !worldIn.getBlock(l2, y + 1, i3).getMaterial().isOpaque())
                    {
                        if (safeIndex <= 0)
                        {
                            return new ChunkCoordinates(l2, y, i3);
                        }

                        --safeIndex;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        if (!isBlockHeadOfBed(meta))
        {
            super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, 0);
        }
    }

    public int getMobilityFlag()
    {
        return 1;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.bed;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode && isBlockHeadOfBed(meta))
        {
            int i1 = getDirection(meta);
            x -= bedDirections[i1][0];
            z -= bedDirections[i1][1];

            if (worldIn.getBlock(x, y, z) == this)
            {
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }
}