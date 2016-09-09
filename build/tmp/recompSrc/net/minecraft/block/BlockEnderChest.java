package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer
{
    private static final String __OBFID = "CL_00000238";

    protected BlockEnderChest()
    {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 22;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Item.getItemFromBlock(Blocks.obsidian);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 8;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        byte b0 = 0;
        int l = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            b0 = 2;
        }

        if (l == 1)
        {
            b0 = 5;
        }

        if (l == 2)
        {
            b0 = 3;
        }

        if (l == 3)
        {
            b0 = 4;
        }

        worldIn.setBlockMetadataWithNotify(x, y, z, b0, 2);
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
        TileEntityEnderChest tileentityenderchest = (TileEntityEnderChest)worldIn.getTileEntity(x, y, z);

        if (inventoryenderchest != null && tileentityenderchest != null)
        {
            if (worldIn.getBlock(x, y + 1, z).isNormalCube())
            {
                return true;
            }
            else if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                inventoryenderchest.setChestTileEntity(tileentityenderchest);
                player.displayGUIChest(inventoryenderchest);
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityEnderChest();
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        for (int l = 0; l < 3; ++l)
        {
            double d6 = (double)((float)x + random.nextFloat());
            double d1 = (double)((float)y + random.nextFloat());
            d6 = (double)((float)z + random.nextFloat());
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = random.nextInt(2) * 2 - 1;
            int j1 = random.nextInt(2) * 2 - 1;
            d3 = ((double)random.nextFloat() - 0.5D) * 0.125D;
            d4 = ((double)random.nextFloat() - 0.5D) * 0.125D;
            d5 = ((double)random.nextFloat() - 0.5D) * 0.125D;
            double d2 = (double)z + 0.5D + 0.25D * (double)j1;
            d5 = (double)(random.nextFloat() * 1.0F * (float)j1);
            double d0 = (double)x + 0.5D + 0.25D * (double)i1;
            d3 = (double)(random.nextFloat() * 1.0F * (float)i1);
            worldIn.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("obsidian");
    }
}