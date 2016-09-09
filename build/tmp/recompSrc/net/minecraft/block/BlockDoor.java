package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150017_a;
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150016_b;
    private static final String __OBFID = "CL_00000230";

    protected BlockDoor(Material p_i45402_1_)
    {
        super(p_i45402_1_);
        float f = 0.5F;
        float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.field_150016_b[0];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        if (side != 1 && side != 0)
        {
            int i1 = this.getFullMetadata(worldIn, x, y, z);
            int j1 = i1 & 3;
            boolean flag = (i1 & 4) != 0;
            boolean flag1 = false;
            boolean flag2 = (i1 & 8) != 0;

            if (flag)
            {
                if (j1 == 0 && side == 2)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 1 && side == 5)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 2 && side == 3)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 3 && side == 4)
                {
                    flag1 = !flag1;
                }
            }
            else
            {
                if (j1 == 0 && side == 5)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 1 && side == 3)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 2 && side == 4)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 3 && side == 2)
                {
                    flag1 = !flag1;
                }

                if ((i1 & 16) != 0)
                {
                    flag1 = !flag1;
                }
            }

            return flag2 ? this.field_150017_a[flag1?1:0] : this.field_150016_b[flag1?1:0];
        }
        else
        {
            return this.field_150016_b[0];
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_150017_a = new IIcon[2];
        this.field_150016_b = new IIcon[2];
        this.field_150017_a[0] = reg.registerIcon(this.getTextureName() + "_upper");
        this.field_150016_b[0] = reg.registerIcon(this.getTextureName() + "_lower");
        this.field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
        this.field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = this.getFullMetadata(worldIn, x, y, z);
        return (l & 4) != 0;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 7;
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.func_150011_b(this.getFullMetadata(worldIn, x, y, z));
    }

    public int func_150013_e(IBlockAccess p_150013_1_, int p_150013_2_, int p_150013_3_, int p_150013_4_)
    {
        return this.getFullMetadata(p_150013_1_, p_150013_2_, p_150013_3_, p_150013_4_) & 3;
    }

    public boolean func_150015_f(IBlockAccess p_150015_1_, int p_150015_2_, int p_150015_3_, int p_150015_4_)
    {
        return (this.getFullMetadata(p_150015_1_, p_150015_2_, p_150015_3_, p_150015_4_) & 4) != 0;
    }

    private void func_150011_b(int p_150011_1_)
    {
        float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int j = p_150011_1_ & 3;
        boolean flag = (p_150011_1_ & 4) != 0;
        boolean flag1 = (p_150011_1_ & 16) != 0;

        if (j == 0)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
        else if (j == 1)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
        }
        else if (j == 2)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            }
            else
            {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        else if (j == 3)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
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
        if (this.blockMaterial == Material.iron)
        {
            return false; //Allow items to interact with the door
        }
        else
        {
            int i1 = this.getFullMetadata(worldIn, x, y, z);
            int j1 = i1 & 7;
            j1 ^= 4;

            if ((i1 & 8) == 0)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, j1, 2);
                worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
            else
            {
                worldIn.setBlockMetadataWithNotify(x, y - 1, z, j1, 2);
                worldIn.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);
            }

            worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
            return true;
        }
    }

    public void func_150014_a(World p_150014_1_, int p_150014_2_, int p_150014_3_, int p_150014_4_, boolean p_150014_5_)
    {
        int l = this.getFullMetadata(p_150014_1_, p_150014_2_, p_150014_3_, p_150014_4_);
        boolean flag1 = (l & 4) != 0;

        if (flag1 != p_150014_5_)
        {
            int i1 = l & 7;
            i1 ^= 4;

            if ((l & 8) == 0)
            {
                p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_, p_150014_4_, i1, 2);
                p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
            }
            else
            {
                p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_ - 1, p_150014_4_, i1, 2);
                p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_ - 1, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
            }

            p_150014_1_.playAuxSFXAtEntity((EntityPlayer)null, 1003, p_150014_2_, p_150014_3_, p_150014_4_, 0);
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        int l = worldIn.getBlockMetadata(x, y, z);

        if ((l & 8) == 0)
        {
            boolean flag = false;

            if (worldIn.getBlock(x, y + 1, z) != this)
            {
                worldIn.setBlockToAir(x, y, z);
                flag = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z))
            {
                worldIn.setBlockToAir(x, y, z);
                flag = true;

                if (worldIn.getBlock(x, y + 1, z) == this)
                {
                    worldIn.setBlockToAir(x, y + 1, z);
                }
            }

            if (flag)
            {
                if (!worldIn.isRemote)
                {
                    this.dropBlockAsItem(worldIn, x, y, z, l, 0);
                }
            }
            else
            {
                boolean flag1 = worldIn.isBlockIndirectlyGettingPowered(x, y, z) || worldIn.isBlockIndirectlyGettingPowered(x, y + 1, z);

                if ((flag1 || neighbor.canProvidePower()) && neighbor != this)
                {
                    this.func_150014_a(worldIn, x, y, z, flag1);
                }
            }
        }
        else
        {
            if (worldIn.getBlock(x, y - 1, z) != this)
            {
                worldIn.setBlockToAir(x, y, z);
            }

            if (neighbor != this)
            {
                this.onNeighborBlockChange(worldIn, x, y - 1, z, neighbor);
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return (meta & 8) != 0 ? null : (this.blockMaterial == Material.iron ? Items.iron_door : Items.wooden_door);
    }

    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return y >= worldIn.getHeight() - 1 ? false : World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && super.canPlaceBlockAt(worldIn, x, y, z) && super.canPlaceBlockAt(worldIn, x, y + 1, z);
    }

    public int getMobilityFlag()
    {
        return 1;
    }

    public int getFullMetadata(IBlockAccess p_150012_1_, int p_150012_2_, int p_150012_3_, int p_150012_4_)
    {
        int l = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_, p_150012_4_);
        boolean flag = (l & 8) != 0;
        int i1;
        int j1;

        if (flag)
        {
            i1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ - 1, p_150012_4_);
            j1 = l;
        }
        else
        {
            i1 = l;
            j1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ + 1, p_150012_4_);
        }

        boolean flag1 = (j1 & 1) != 0;
        return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return this.blockMaterial == Material.iron ? Items.iron_door : Items.wooden_door;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode && (meta & 8) != 0 && worldIn.getBlock(x, y - 1, z) == this)
        {
            worldIn.setBlockToAir(x, y - 1, z);
        }
    }
}