package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block
{
    public static final String[] field_150009_a = new String[] {"default", "default", "podzol"};
    @SideOnly(Side.CLIENT)
    private IIcon field_150008_b;
    @SideOnly(Side.CLIENT)
    private IIcon field_150010_M;
    private static final String __OBFID = "CL_00000228";

    protected BlockDirt()
    {
        super(Material.ground);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta == 2)
        {
            if (side == 1)
            {
                return this.field_150008_b;
            }

            if (side != 0)
            {
                return this.field_150010_M;
            }
        }

        return this.blockIcon;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);

        if (i1 == 2)
        {
            if (side == 1)
            {
                return this.field_150008_b;
            }

            if (side != 0)
            {
                Material material = worldIn.getBlock(x, y + 1, z).getMaterial();

                if (material == Material.snow || material == Material.craftedSnow)
                {
                    return Blocks.grass.getIcon(worldIn, x, y, z, side);
                }

                Block block = worldIn.getBlock(x, y + 1, z);

                if (block != Blocks.dirt && block != Blocks.grass)
                {
                    return this.field_150010_M;
                }
            }
        }

        return this.blockIcon;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int meta)
    {
        if (meta == 1)
        {
            meta = 0;
        }

        return super.createStackedBlock(meta);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 2));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        this.field_150008_b = reg.registerIcon(this.getTextureName() + "_" + "podzol_top");
        this.field_150010_M = reg.registerIcon(this.getTextureName() + "_" + "podzol_side");
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);

        if (l == 1)
        {
            l = 0;
        }

        return l;
    }
}