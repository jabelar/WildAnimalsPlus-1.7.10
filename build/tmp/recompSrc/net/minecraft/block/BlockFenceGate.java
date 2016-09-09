package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional
{
    private static final String __OBFID = "CL_00000243";

    public BlockFenceGate()
    {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.planks.getBlockTextureFromSide(side);
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return !worldIn.getBlock(x, y - 1, z).getMaterial().isSolid() ? false : super.canPlaceBlockAt(worldIn, x, y, z);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        /**
         * Returns if the fence gate is open according to its metadata.
         */
        return isFenceGateOpen(l) ? null : (l != 2 && l != 0 ? AxisAlignedBB.getBoundingBox((double)((float)x + 0.375F), (double)y, (double)z, (double)((float)x + 0.625F), (double)((float)y + 1.5F), (double)(z + 1)) : AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)((float)z + 0.375F), (double)(x + 1), (double)((float)y + 1.5F), (double)((float)z + 0.625F)));
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = getDirection(worldIn.getBlockMetadata(x, y, z));

        if (l != 2 && l != 0)
        {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        /**
         * Returns if the fence gate is open according to its metadata.
         */
        return isFenceGateOpen(worldIn.getBlockMetadata(x, y, z));
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 21;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = (MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
        worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);

        if (isFenceGateOpen(i1))
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, i1 & -5, 2);
        }
        else
        {
            int j1 = (MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
            int k1 = getDirection(i1);

            if (k1 == (j1 + 2) % 4)
            {
                i1 = j1;
            }

            worldIn.setBlockMetadataWithNotify(x, y, z, i1 | 4, 2);
        }

        worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
        return true;
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!worldIn.isRemote)
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            boolean flag = worldIn.isBlockIndirectlyGettingPowered(x, y, z);

            if (flag || neighbor.canProvidePower())
            {
                if (flag && !isFenceGateOpen(l))
                {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l | 4, 2);
                    worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
                }
                else if (!flag && isFenceGateOpen(l))
                {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l & -5, 2);
                    worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
                }
            }
        }
    }

    /**
     * Returns if the fence gate is open according to its metadata.
     */
    public static boolean isFenceGateOpen(int p_149896_0_)
    {
        return (p_149896_0_ & 4) != 0;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}
}