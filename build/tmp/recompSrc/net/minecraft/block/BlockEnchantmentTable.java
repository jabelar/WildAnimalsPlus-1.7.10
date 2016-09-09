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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon field_149950_a;
    @SideOnly(Side.CLIENT)
    private IIcon field_149949_b;
    private static final String __OBFID = "CL_00000235";

    protected BlockEnchantmentTable()
    {
        super(Material.rock);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        super.randomDisplayTick(worldIn, x, y, z, random);

        for (int l = x - 2; l <= x + 2; ++l)
        {
            for (int i1 = z - 2; i1 <= z + 2; ++i1)
            {
                if (l > x - 2 && l < x + 2 && i1 == z - 1)
                {
                    i1 = z + 2;
                }

                if (random.nextInt(16) == 0)
                {
                    for (int j1 = y; j1 <= y + 1; ++j1)
                    {
                        if (worldIn.getBlock(l, j1, i1) == Blocks.bookshelf)
                        {
                            if (!worldIn.isAirBlock((l - x) / 2 + x, j1, (i1 - z) / 2 + z))
                            {
                                break;
                            }

                            worldIn.spawnParticle("enchantmenttable", (double)x + 0.5D, (double)y + 2.0D, (double)z + 0.5D, (double)((float)(l - x) + random.nextFloat()) - 0.5D, (double)((float)(j1 - y) - random.nextFloat() - 1.0F), (double)((float)(i1 - z) + random.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 0 ? this.field_149949_b : (side == 1 ? this.field_149950_a : this.blockIcon);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityEnchantmentTable();
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
            TileEntityEnchantmentTable tileentityenchantmenttable = (TileEntityEnchantmentTable)worldIn.getTileEntity(x, y, z);
            player.displayGUIEnchantment(x, y, z, tileentityenchantmenttable.func_145921_b() ? tileentityenchantmenttable.func_145919_a() : null);
            return true;
        }
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);

        if (itemIn.hasDisplayName())
        {
            ((TileEntityEnchantmentTable)worldIn.getTileEntity(x, y, z)).func_145920_a(itemIn.getDisplayName());
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_" + "side");
        this.field_149950_a = reg.registerIcon(this.getTextureName() + "_" + "top");
        this.field_149949_b = reg.registerIcon(this.getTextureName() + "_" + "bottom");
    }
}