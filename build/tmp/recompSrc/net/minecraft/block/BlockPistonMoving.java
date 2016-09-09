package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer
{
    private static final String __OBFID = "CL_00000368";

    public BlockPistonMoving()
    {
        super(Material.piston);
        this.setHardness(-1.0F);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return null;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z) {}

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        TileEntity tileentity = worldIn.getTileEntity(x, y, z);

        if (tileentity instanceof TileEntityPiston)
        {
            ((TileEntityPiston)tileentity).clearPistonTileEntity();
        }
        else
        {
            super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        }
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return false;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(x, y, z) == null)
        {
            worldIn.setBlockToAir(x, y, z);
            return true;
        }
        else
        {
            return false;
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return null;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!worldIn.isRemote)
        {
            worldIn.getTileEntity(x, y, z);
        }
    }

    public static TileEntity getTileEntity(Block p_149962_0_, int p_149962_1_, int p_149962_2_, boolean p_149962_3_, boolean p_149962_4_)
    {
        return new TileEntityPiston(p_149962_0_, p_149962_1_, p_149962_2_, p_149962_3_, p_149962_4_);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        TileEntityPiston tileentitypiston = this.func_149963_e(worldIn, x, y, z);

        if (tileentitypiston == null)
        {
            return null;
        }
        else
        {
            float f = tileentitypiston.func_145860_a(0.0F);

            if (tileentitypiston.isExtending())
            {
                f = 1.0F - f;
            }

            return this.func_149964_a(worldIn, x, y, z, tileentitypiston.getStoredBlockID(), f, tileentitypiston.getPistonOrientation());
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        TileEntityPiston tileentitypiston = this.func_149963_e(worldIn, x, y, z);

        if (tileentitypiston != null)
        {
            Block block = tileentitypiston.getStoredBlockID();

            if (block == this || block.getMaterial() == Material.air)
            {
                return;
            }

            block.setBlockBoundsBasedOnState(worldIn, x, y, z);
            float f = tileentitypiston.func_145860_a(0.0F);

            if (tileentitypiston.isExtending())
            {
                f = 1.0F - f;
            }

            int l = tileentitypiston.getPistonOrientation();
            this.minX = block.getBlockBoundsMinX() - (double)((float)Facing.offsetsXForSide[l] * f);
            this.minY = block.getBlockBoundsMinY() - (double)((float)Facing.offsetsYForSide[l] * f);
            this.minZ = block.getBlockBoundsMinZ() - (double)((float)Facing.offsetsZForSide[l] * f);
            this.maxX = block.getBlockBoundsMaxX() - (double)((float)Facing.offsetsXForSide[l] * f);
            this.maxY = block.getBlockBoundsMaxY() - (double)((float)Facing.offsetsYForSide[l] * f);
            this.maxZ = block.getBlockBoundsMaxZ() - (double)((float)Facing.offsetsZForSide[l] * f);
        }
    }

    public AxisAlignedBB func_149964_a(World p_149964_1_, int p_149964_2_, int p_149964_3_, int p_149964_4_, Block p_149964_5_, float p_149964_6_, int p_149964_7_)
    {
        if (p_149964_5_ != this && p_149964_5_.getMaterial() != Material.air)
        {
            AxisAlignedBB axisalignedbb = p_149964_5_.getCollisionBoundingBoxFromPool(p_149964_1_, p_149964_2_, p_149964_3_, p_149964_4_);

            if (axisalignedbb == null)
            {
                return null;
            }
            else
            {
                if (Facing.offsetsXForSide[p_149964_7_] < 0)
                {
                    axisalignedbb.minX -= (double)((float)Facing.offsetsXForSide[p_149964_7_] * p_149964_6_);
                }
                else
                {
                    axisalignedbb.maxX -= (double)((float)Facing.offsetsXForSide[p_149964_7_] * p_149964_6_);
                }

                if (Facing.offsetsYForSide[p_149964_7_] < 0)
                {
                    axisalignedbb.minY -= (double)((float)Facing.offsetsYForSide[p_149964_7_] * p_149964_6_);
                }
                else
                {
                    axisalignedbb.maxY -= (double)((float)Facing.offsetsYForSide[p_149964_7_] * p_149964_6_);
                }

                if (Facing.offsetsZForSide[p_149964_7_] < 0)
                {
                    axisalignedbb.minZ -= (double)((float)Facing.offsetsZForSide[p_149964_7_] * p_149964_6_);
                }
                else
                {
                    axisalignedbb.maxZ -= (double)((float)Facing.offsetsZForSide[p_149964_7_] * p_149964_6_);
                }

                return axisalignedbb;
            }
        }
        else
        {
            return null;
        }
    }

    private TileEntityPiston func_149963_e(IBlockAccess p_149963_1_, int p_149963_2_, int p_149963_3_, int p_149963_4_)
    {
        TileEntity tileentity = p_149963_1_.getTileEntity(p_149963_2_, p_149963_3_, p_149963_4_);
        return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Item.getItemById(0);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("piston_top_normal");
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        TileEntityPiston te = this.func_149963_e(world, x, y, z);
        if (te != null)
            return te.getStoredBlockID().getDrops(world, x, y, z, te.getBlockMetadata(), 0);
        return new ArrayList<ItemStack>();
    }
}