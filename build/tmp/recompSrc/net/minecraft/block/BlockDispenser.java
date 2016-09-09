package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer
{
    /** Registry for all dispense behaviors. */
    public static final IRegistry dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
    protected Random field_149942_b = new Random();
    @SideOnly(Side.CLIENT)
    protected IIcon field_149944_M;
    @SideOnly(Side.CLIENT)
    protected IIcon field_149945_N;
    @SideOnly(Side.CLIENT)
    protected IIcon field_149946_O;
    private static final String __OBFID = "CL_00000229";

    protected BlockDispenser()
    {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 4;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        super.onBlockAdded(worldIn, x, y, z);
        this.func_149938_m(worldIn, x, y, z);
    }

    private void func_149938_m(World p_149938_1_, int p_149938_2_, int p_149938_3_, int p_149938_4_)
    {
        if (!p_149938_1_.isRemote)
        {
            Block block = p_149938_1_.getBlock(p_149938_2_, p_149938_3_, p_149938_4_ - 1);
            Block block1 = p_149938_1_.getBlock(p_149938_2_, p_149938_3_, p_149938_4_ + 1);
            Block block2 = p_149938_1_.getBlock(p_149938_2_ - 1, p_149938_3_, p_149938_4_);
            Block block3 = p_149938_1_.getBlock(p_149938_2_ + 1, p_149938_3_, p_149938_4_);
            byte b0 = 3;

            if (block.isFullBlock() && !block1.isFullBlock())
            {
                b0 = 3;
            }

            if (block1.isFullBlock() && !block.isFullBlock())
            {
                b0 = 2;
            }

            if (block2.isFullBlock() && !block3.isFullBlock())
            {
                b0 = 5;
            }

            if (block3.isFullBlock() && !block2.isFullBlock())
            {
                b0 = 4;
            }

            p_149938_1_.setBlockMetadataWithNotify(p_149938_2_, p_149938_3_, p_149938_4_, b0, 2);
        }
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int k = meta & 7;
        return side == k ? (k != 1 && k != 0 ? this.field_149945_N : this.field_149946_O) : (k != 1 && k != 0 ? (side != 1 && side != 0 ? this.blockIcon : this.field_149944_M) : this.field_149944_M);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("furnace_side");
        this.field_149944_M = reg.registerIcon("furnace_top");
        this.field_149945_N = reg.registerIcon(this.getTextureName() + "_front_horizontal");
        this.field_149946_O = reg.registerIcon(this.getTextureName() + "_front_vertical");
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
            TileEntityDispenser tileentitydispenser = (TileEntityDispenser)worldIn.getTileEntity(x, y, z);

            if (tileentitydispenser != null)
            {
                player.func_146102_a(tileentitydispenser);
            }

            return true;
        }
    }

    protected void func_149941_e(World p_149941_1_, int p_149941_2_, int p_149941_3_, int p_149941_4_)
    {
        BlockSourceImpl blocksourceimpl = new BlockSourceImpl(p_149941_1_, p_149941_2_, p_149941_3_, p_149941_4_);
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser)blocksourceimpl.getBlockTileEntity();

        if (tileentitydispenser != null)
        {
            int l = tileentitydispenser.func_146017_i();

            if (l < 0)
            {
                p_149941_1_.playAuxSFX(1001, p_149941_2_, p_149941_3_, p_149941_4_, 0);
            }
            else
            {
                ItemStack itemstack = tileentitydispenser.getStackInSlot(l);
                IBehaviorDispenseItem ibehaviordispenseitem = this.func_149940_a(itemstack);

                if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider)
                {
                    ItemStack itemstack1 = ibehaviordispenseitem.dispense(blocksourceimpl, itemstack);
                    tileentitydispenser.setInventorySlotContents(l, itemstack1.stackSize == 0 ? null : itemstack1);
                }
            }
        }
    }

    protected IBehaviorDispenseItem func_149940_a(ItemStack p_149940_1_)
    {
        return (IBehaviorDispenseItem)dispenseBehaviorRegistry.getObject(p_149940_1_.getItem());
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        boolean flag = worldIn.isBlockIndirectlyGettingPowered(x, y, z) || worldIn.isBlockIndirectlyGettingPowered(x, y + 1, z);
        int l = worldIn.getBlockMetadata(x, y, z);
        boolean flag1 = (l & 8) != 0;

        if (flag && !flag1)
        {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
            worldIn.setBlockMetadataWithNotify(x, y, z, l | 8, 4);
        }
        else if (!flag && flag1)
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, l & -9, 4);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!worldIn.isRemote)
        {
            this.func_149941_e(worldIn, x, y, z);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDispenser();
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = BlockPistonBase.determineOrientation(worldIn, x, y, z, placer);
        worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);

        if (itemIn.hasDisplayName())
        {
            ((TileEntityDispenser)worldIn.getTileEntity(x, y, z)).func_146018_a(itemIn.getDisplayName());
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser)worldIn.getTileEntity(x, y, z);

        if (tileentitydispenser != null)
        {
            for (int i1 = 0; i1 < tileentitydispenser.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = tileentitydispenser.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.field_149942_b.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int j1 = this.field_149942_b.nextInt(21) + 10;

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
                        entityitem.motionX = (double)((float)this.field_149942_b.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.field_149942_b.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.field_149942_b.nextGaussian() * f3);
                        worldIn.spawnEntityInWorld(entityitem);
                    }
                }
            }

            worldIn.updateNeighborsAboutBlockChange(x, y, z, blockBroken);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    /**
     * Gets an IPosition instance for the dispenser's output given a block source
     */
    public static IPosition getIPositionFromBlockSource(IBlockSource p_149939_0_)
    {
        EnumFacing enumfacing = getFacingDirection(p_149939_0_.getBlockMetadata());
        double d0 = p_149939_0_.getX() + 0.7D * (double)enumfacing.getFrontOffsetX();
        double d1 = p_149939_0_.getY() + 0.7D * (double)enumfacing.getFrontOffsetY();
        double d2 = p_149939_0_.getZ() + 0.7D * (double)enumfacing.getFrontOffsetZ();
        return new PositionImpl(d0, d1, d2);
    }

    /**
     * Gets an EnumFacing for the given dispenser metadata value
     */
    public static EnumFacing getFacingDirection(int p_149937_0_)
    {
        return EnumFacing.getFront(p_149937_0_ & 7);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side)
    {
        return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(x, y, z));
    }
}