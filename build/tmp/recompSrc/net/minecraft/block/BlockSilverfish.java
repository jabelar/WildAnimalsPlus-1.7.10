package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class BlockSilverfish extends Block
{
    public static final String[] field_150198_a = new String[] {"stone", "cobble", "brick", "mossybrick", "crackedbrick", "chiseledbrick"};
    private static final String __OBFID = "CL_00000271";

    public BlockSilverfish()
    {
        super(Material.clay);
        this.setHardness(0.0F);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        switch (meta)
        {
            case 1:
                return Blocks.cobblestone.getBlockTextureFromSide(side);
            case 2:
                return Blocks.stonebrick.getBlockTextureFromSide(side);
            case 3:
                return Blocks.stonebrick.getIcon(side, 1);
            case 4:
                return Blocks.stonebrick.getIcon(side, 2);
            case 5:
                return Blocks.stonebrick.getIcon(side, 3);
            default:
                return Blocks.stone.getBlockTextureFromSide(side);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}

    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta)
    {
        if (!worldIn.isRemote)
        {
            EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
            entitysilverfish.setLocationAndAngles((double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);
            worldIn.spawnEntityInWorld(entitysilverfish);
            entitysilverfish.spawnExplosionParticle();
        }

        super.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public static boolean func_150196_a(Block p_150196_0_)
    {
        return p_150196_0_ == Blocks.stone || p_150196_0_ == Blocks.cobblestone || p_150196_0_ == Blocks.stonebrick;
    }

    public static int func_150195_a(Block p_150195_0_, int p_150195_1_)
    {
        if (p_150195_1_ == 0)
        {
            if (p_150195_0_ == Blocks.cobblestone)
            {
                return 1;
            }

            if (p_150195_0_ == Blocks.stonebrick)
            {
                return 2;
            }
        }
        else if (p_150195_0_ == Blocks.stonebrick)
        {
            switch (p_150195_1_)
            {
                case 1:
                    return 3;
                case 2:
                    return 4;
                case 3:
                    return 5;
            }
        }

        return 0;
    }

    public static ImmutablePair func_150197_b(int p_150197_0_)
    {
        switch (p_150197_0_)
        {
            case 1:
                return new ImmutablePair(Blocks.cobblestone, Integer.valueOf(0));
            case 2:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(0));
            case 3:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(1));
            case 4:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(2));
            case 5:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(3));
            default:
                return new ImmutablePair(Blocks.stone, Integer.valueOf(0));
        }
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int meta)
    {
        switch (meta)
        {
            case 1:
                return new ItemStack(Blocks.cobblestone);
            case 2:
                return new ItemStack(Blocks.stonebrick);
            case 3:
                return new ItemStack(Blocks.stonebrick, 1, 1);
            case 4:
                return new ItemStack(Blocks.stonebrick, 1, 2);
            case 5:
                return new ItemStack(Blocks.stonebrick, 1, 3);
            default:
                return new ItemStack(Blocks.stone);
        }
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        if (!worldIn.isRemote)
        {
            EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
            entitysilverfish.setLocationAndAngles((double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);
            worldIn.spawnEntityInWorld(entitysilverfish);
            entitysilverfish.spawnExplosionParticle();
        }
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        return worldIn.getBlockMetadata(x, y, z);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        for (int i = 0; i < field_150198_a.length; ++i)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }
}