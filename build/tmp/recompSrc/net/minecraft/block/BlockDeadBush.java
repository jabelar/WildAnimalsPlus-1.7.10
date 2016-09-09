package net.minecraft.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class BlockDeadBush extends BlockBush implements IShearable
{
    private static final String __OBFID = "CL_00000224";

    protected BlockDeadBush()
    {
        super(Material.vine);
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.sand || ground == Blocks.hardened_clay || ground == Blocks.stained_hardened_clay || ground == Blocks.dirt;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return null;
    }

    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta)
    {
        {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }

    @Override public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) { return true; }
    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune)
    {
        return new ArrayList<ItemStack>(Arrays.asList(new ItemStack(Blocks.deadbush, 1, world.getBlockMetadata(x, y, z))));
    }
}