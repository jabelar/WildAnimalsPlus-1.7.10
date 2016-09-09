package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public abstract class BlockButton extends Block
{
    private final boolean wooden;
    private static final String __OBFID = "CL_00000209";

    protected BlockButton(boolean wooden)
    {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.wooden = wooden;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.wooden ? 30 : 20;
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
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return (dir == NORTH && worldIn.isSideSolid(x, y, z + 1, NORTH)) ||
               (dir == SOUTH && worldIn.isSideSolid(x, y, z - 1, SOUTH)) ||
               (dir == WEST  && worldIn.isSideSolid(x + 1, y, z, WEST)) ||
               (dir == EAST  && worldIn.isSideSolid(x - 1, y, z, EAST));
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return (worldIn.isSideSolid(x - 1, y, z, EAST)) ||
               (worldIn.isSideSolid(x + 1, y, z, WEST)) ||
               (worldIn.isSideSolid(x, y, z - 1, SOUTH)) ||
               (worldIn.isSideSolid(x, y, z + 1, NORTH));
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        int j1 = worldIn.getBlockMetadata(x, y, z);
        int k1 = j1 & 8;
        j1 &= 7;

        ForgeDirection dir = ForgeDirection.getOrientation(side);

        if (dir == NORTH && worldIn.isSideSolid(x, y, z + 1, NORTH))
        {
            j1 = 4;
        }
        else if (dir == SOUTH && worldIn.isSideSolid(x, y, z - 1, SOUTH))
        {
            j1 = 3;
        }
        else if (dir == WEST && worldIn.isSideSolid(x + 1, y, z, WEST))
        {
            j1 = 2;
        }
        else if (dir == EAST && worldIn.isSideSolid(x - 1, y, z, EAST))
        {
            j1 = 1;
        }
        else
        {
            j1 = this.findSolidSide(worldIn, x, y, z);
        }

        return j1 + k1;
    }

    private int findSolidSide(World worldIn, int x, int y, int z)
    {
        if (worldIn.isSideSolid(x - 1, y, z, EAST)) return 1;
        if (worldIn.isSideSolid(x + 1, y, z, WEST)) return 2;
        if (worldIn.isSideSolid(x, y, z - 1, SOUTH)) return 3;
        if (worldIn.isSideSolid(x, y, z + 1, NORTH)) return 4;
        return 1;
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (this.canStay(worldIn, x, y, z))
        {
            int l = worldIn.getBlockMetadata(x, y, z) & 7;
            boolean flag = false;

            if (!worldIn.isSideSolid(x - 1, y, z, EAST) && l == 1)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x + 1, y, z, WEST) && l == 2)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y, z - 1, SOUTH) && l == 3)
            {
                flag = true;
            }

            if (!worldIn.isSideSolid(x, y, z + 1, NORTH) && l == 4)
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    private boolean canStay(World worldIn, int x, int y, int z)
    {
        if (!this.canPlaceBlockAt(worldIn, x, y, z))
        {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        this.setBlockBoundsFromMeta(l);
    }

    private void setBlockBoundsFromMeta(int meta)
    {
        int j = meta & 7;
        boolean flag = (meta & 8) > 0;
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.1875F;
        float f3 = 0.125F;

        if (flag)
        {
            f3 = 0.0625F;
        }

        if (j == 1)
        {
            this.setBlockBounds(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
        }
        else if (j == 2)
        {
            this.setBlockBounds(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
        }
        else if (j == 3)
        {
            this.setBlockBounds(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
        }
        else if (j == 4)
        {
            this.setBlockBounds(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
        }
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {}

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);
        int j1 = i1 & 7;
        int k1 = 8 - (i1 & 8);

        if (k1 == 0)
        {
            return true;
        }
        else
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, j1 + k1, 3);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, 0.6F);
            this.updateNeighbor(worldIn, x, y, z, j1);
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
            return true;
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        if ((meta & 8) > 0)
        {
            int i1 = meta & 7;
            this.updateNeighbor(worldIn, x, y, z, i1);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return (worldIn.getBlockMetadata(x, y, z) & 8) > 0 ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);

        if ((i1 & 8) == 0)
        {
            return 0;
        }
        else
        {
            int j1 = i1 & 7;
            return j1 == 5 && side == 1 ? 15 : (j1 == 4 && side == 2 ? 15 : (j1 == 3 && side == 3 ? 15 : (j1 == 2 && side == 4 ? 15 : (j1 == 1 && side == 5 ? 15 : 0))));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!worldIn.isRemote)
        {
            int l = worldIn.getBlockMetadata(x, y, z);

            if ((l & 8) != 0)
            {
                if (this.wooden)
                {
                    this.activateButton(worldIn, x, y, z);
                }
                else
                {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l & 7, 3);
                    int i1 = l & 7;
                    this.updateNeighbor(worldIn, x, y, z, i1);
                    worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, 0.5F);
                    worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.1875F;
        float f1 = 0.125F;
        float f2 = 0.125F;
        this.setBlockBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            if (this.wooden)
            {
                if ((worldIn.getBlockMetadata(x, y, z) & 8) == 0)
                {
                    this.activateButton(worldIn, x, y, z);
                }
            }
        }
    }

    private void activateButton(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        int i1 = l & 7;
        boolean flag = (l & 8) != 0;
        this.setBlockBoundsFromMeta(l);
        List list = worldIn.getEntitiesWithinAABB(EntityArrow.class, AxisAlignedBB.getBoundingBox((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + this.maxY, (double)z + this.maxZ));
        boolean flag1 = !list.isEmpty();

        if (flag1 && !flag)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, i1 | 8, 3);
            this.updateNeighbor(worldIn, x, y, z, i1);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (!flag1 && flag)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, i1, 3);
            this.updateNeighbor(worldIn, x, y, z, i1);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (flag1)
        {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
        }
    }

    private void updateNeighbor(World worldIn, int x, int y, int z, int p_150042_5_)
    {
        worldIn.notifyBlocksOfNeighborChange(x, y, z, this);

        if (p_150042_5_ == 1)
        {
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        }
        else if (p_150042_5_ == 2)
        {
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        }
        else if (p_150042_5_ == 3)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        }
        else if (p_150042_5_ == 4)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        }
        else
        {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}
}