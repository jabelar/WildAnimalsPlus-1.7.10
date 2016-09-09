package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockNetherWart extends BlockBush
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149883_a;
    private static final String __OBFID = "CL_00000274";

    protected BlockNetherWart()
    {
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.soul_sand;
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World worldIn, int x, int y, int z)
    {
        return super.canBlockStay(worldIn, x, y, z);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        int l = worldIn.getBlockMetadata(x, y, z);

        if (l < 3 && random.nextInt(10) == 0)
        {
            ++l;
            worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
        }

        super.updateTick(worldIn, x, y, z, random);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return meta >= 3 ? this.field_149883_a[2] : (meta > 0 ? this.field_149883_a[1] : this.field_149883_a[0]);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 6;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    @SuppressWarnings("unused")
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
        
        if (false && !worldIn.isRemote)
        {
            int j1 = 1;

            if (meta >= 3)
            {
                j1 = 2 + worldIn.rand.nextInt(3);

                if (fortune > 0)
                {
                    j1 += worldIn.rand.nextInt(fortune + 1);
                }
            }

            for (int k1 = 0; k1 < j1; ++k1)
            {
                this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Items.nether_wart));
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.nether_wart;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149883_a = new IIcon[3];

        for (int i = 0; i < this.field_149883_a.length; ++i)
        {
            this.field_149883_a[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int count = 1;

        if (metadata >= 3)
        {
            count = 2 + world.rand.nextInt(3) + (fortune > 0 ? world.rand.nextInt(fortune + 1) : 0);
        }

        for (int i = 0; i < count; i++)
        {
            ret.add(new ItemStack(Items.nether_wart));
        }

        return ret;
    }
}