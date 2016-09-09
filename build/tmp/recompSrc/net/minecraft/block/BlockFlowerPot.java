package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer
{
    private static final String __OBFID = "CL_00000247";

    public BlockFlowerPot()
    {
        super(Material.circuits);
        this.setBlockBoundsForItemRender();
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.375F;
        float f1 = f / 2.0F;
        this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 33;
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
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
        {
            TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(worldIn, x, y, z);

            if (tileentityflowerpot != null)
            {
                if (tileentityflowerpot.getFlowerPotItem() != null)
                {
                    return false;
                }
                else
                {
                    Block block = Block.getBlockFromItem(itemstack.getItem());

                    if (!this.func_149928_a(block, itemstack.getMetadata()))
                    {
                        return false;
                    }
                    else
                    {
                        tileentityflowerpot.func_145964_a(itemstack.getItem(), itemstack.getMetadata());
                        tileentityflowerpot.markDirty();

                        if (!worldIn.setBlockMetadataWithNotify(x, y, z, itemstack.getMetadata(), 2))
                        {
                            worldIn.markBlockForUpdate(x, y, z);
                        }

                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                        }

                        return true;
                    }
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean func_149928_a(Block p_149928_1_, int p_149928_2_)
    {
        return p_149928_1_ != Blocks.yellow_flower && p_149928_1_ != Blocks.red_flower && p_149928_1_ != Blocks.cactus && p_149928_1_ != Blocks.brown_mushroom && p_149928_1_ != Blocks.red_mushroom && p_149928_1_ != Blocks.sapling && p_149928_1_ != Blocks.deadbush ? p_149928_1_ == Blocks.tallgrass && p_149928_2_ == 2 : true;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(worldIn, x, y, z);
        return tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null ? tileentityflowerpot.getFlowerPotItem() : Items.flower_pot;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(worldIn, x, y, z);
        return tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null ? tileentityflowerpot.getFlowerPotData() : 0;
    }

    /**
     * Returns true only if block is flowerPot
     */
    @SideOnly(Side.CLIENT)
    public boolean isFlowerPot()
    {
        return true;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return super.canPlaceBlockAt(worldIn, x, y, z) && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z))
        {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, x, y, z, meta, player);

        if (player.capabilities.isCreativeMode)
        {
            TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(worldIn, x, y, z);

            if (tileentityflowerpot != null)
            {
                tileentityflowerpot.func_145964_a(Item.getItemById(0), 0);
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.flower_pot;
    }

    private TileEntityFlowerPot func_149929_e(World p_149929_1_, int p_149929_2_, int p_149929_3_, int p_149929_4_)
    {
        TileEntity tileentity = p_149929_1_.getTileEntity(p_149929_2_, p_149929_3_, p_149929_4_);
        return tileentity != null && tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)tileentity : null;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        Object object = null;
        byte b0 = 0;

        switch (meta)
        {
            case 1:
                object = Blocks.red_flower;
                b0 = 0;
                break;
            case 2:
                object = Blocks.yellow_flower;
                break;
            case 3:
                object = Blocks.sapling;
                b0 = 0;
                break;
            case 4:
                object = Blocks.sapling;
                b0 = 1;
                break;
            case 5:
                object = Blocks.sapling;
                b0 = 2;
                break;
            case 6:
                object = Blocks.sapling;
                b0 = 3;
                break;
            case 7:
                object = Blocks.red_mushroom;
                break;
            case 8:
                object = Blocks.brown_mushroom;
                break;
            case 9:
                object = Blocks.cactus;
                break;
            case 10:
                object = Blocks.deadbush;
                break;
            case 11:
                object = Blocks.tallgrass;
                b0 = 2;
                break;
            case 12:
                object = Blocks.sapling;
                b0 = 4;
                break;
            case 13:
                object = Blocks.sapling;
                b0 = 5;
        }

        return new TileEntityFlowerPot(Item.getItemFromBlock((Block)object), b0);
    }

    /*============================FORGE START=====================================*/
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);
        TileEntityFlowerPot te = this.func_149929_e(world, x, y, z);
        if (te != null && te.getFlowerPotItem() != null)
            ret.add(new ItemStack(te.getFlowerPotItem(), 1, te.getFlowerPotData()));
        return ret;
    }
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }
    /*===========================FORGE END==========================================*/
}