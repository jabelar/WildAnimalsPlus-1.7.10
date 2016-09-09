package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer
{
    private final Random field_149922_a = new Random();
    @SideOnly(Side.CLIENT)
    private IIcon hopperOutsideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon hopperTopIcon;
    @SideOnly(Side.CLIENT)
    private IIcon hopperInsideIcon;
    private static final String __OBFID = "CL_00000257";

    public BlockHopper()
    {
        super(Material.iron);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
        int j1 = Facing.oppositeSide[side];

        if (j1 == 1)
        {
            j1 = 0;
        }

        return j1;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityHopper();
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);

        if (itemIn.hasDisplayName())
        {
            TileEntityHopper tileentityhopper = func_149920_e(worldIn, x, y, z);
            tileentityhopper.func_145886_a(itemIn.getDisplayName());
        }
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        super.onBlockAdded(worldIn, x, y, z);
        this.updateBlockData(worldIn, x, y, z);
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
            TileEntityHopper tileentityhopper = func_149920_e(worldIn, x, y, z);

            if (tileentityhopper != null)
            {
                player.func_146093_a(tileentityhopper);
            }

            return true;
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        this.updateBlockData(worldIn, x, y, z);
    }

    /**
     * Updates the block's metadata to reflect the current power state (stored in the 4th bit).
     */
    private void updateBlockData(World p_149919_1_, int p_149919_2_, int p_149919_3_, int p_149919_4_)
    {
        int l = p_149919_1_.getBlockMetadata(p_149919_2_, p_149919_3_, p_149919_4_);
        int i1 = getDirectionFromMetadata(l);
        boolean flag = !p_149919_1_.isBlockIndirectlyGettingPowered(p_149919_2_, p_149919_3_, p_149919_4_);
        boolean flag1 = getActiveStateFromMetadata(l);

        if (flag != flag1)
        {
            p_149919_1_.setBlockMetadataWithNotify(p_149919_2_, p_149919_3_, p_149919_4_, i1 | (flag ? 0 : 8), 4);
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        TileEntityHopper tileentityhopper = (TileEntityHopper)worldIn.getTileEntity(x, y, z);

        if (tileentityhopper != null)
        {
            for (int i1 = 0; i1 < tileentityhopper.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = tileentityhopper.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int j1 = this.field_149922_a.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize)
                        {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        EntityItem entityitem = new EntityItem(worldIn, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getMetadata()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.field_149922_a.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.field_149922_a.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.field_149922_a.nextGaussian() * f3);
                        worldIn.spawnEntityInWorld(entityitem);
                    }
                }
            }

            worldIn.updateNeighborsAboutBlockChange(x, y, z, blockBroken);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 38;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
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

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.hopperTopIcon : this.hopperOutsideIcon;
    }

    /**
     * Get's the hopper's orientation from the last three bits of metadata. Returns 0-5 for down, unattached, north,
     * south, east, and west.
     */
    public static int getDirectionFromMetadata(int p_149918_0_)
    {
        return p_149918_0_ & 7;
    }

    /**
     * Get's the hopper's active status from the 8-bit of the metadata. Note that the metadata stores whether the block
     * is powered, so this returns true when that bit is 0.
     */
    public static boolean getActiveStateFromMetadata(int p_149917_0_)
    {
        return (p_149917_0_ & 8) != 8;
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side)
    {
        return Container.calcRedstoneFromInventory(func_149920_e(worldIn, x, y, z));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.hopperOutsideIcon = reg.registerIcon("hopper_outside");
        this.hopperTopIcon = reg.registerIcon("hopper_top");
        this.hopperInsideIcon = reg.registerIcon("hopper_inside");
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getHopperIcon(String p_149916_0_)
    {
        return p_149916_0_.equals("hopper_outside") ? Blocks.hopper.hopperOutsideIcon : (p_149916_0_.equals("hopper_inside") ? Blocks.hopper.hopperInsideIcon : null);
    }

    public static TileEntityHopper func_149920_e(IBlockAccess p_149920_0_, int p_149920_1_, int p_149920_2_, int p_149920_3_)
    {
        return (TileEntityHopper)p_149920_0_.getTileEntity(p_149920_1_, p_149920_2_, p_149920_3_);
    }

    /**
     * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers.
     */
    @SideOnly(Side.CLIENT)
    public String getItemIconName()
    {
        return "hopper";
    }
}