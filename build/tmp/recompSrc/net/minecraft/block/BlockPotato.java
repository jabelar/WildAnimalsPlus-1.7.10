package net.minecraft.block;

import java.util.ArrayList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149869_a;
    private static final String __OBFID = "CL_00000286";

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta < 7)
        {
            if (meta == 6)
            {
                meta = 5;
            }

            return this.field_149869_a[meta >> 1];
        }
        else
        {
            return this.field_149869_a[3];
        }
    }

    protected Item getSeed()
    {
        return Items.potato;
    }

    protected Item getCrop()
    {
        return Items.potato;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);
        if (metadata >= 7 && world.rand.nextInt(50) == 0)
            ret.add(new ItemStack(Items.poisonous_potato));
        return ret;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149869_a = new IIcon[4];

        for (int i = 0; i < this.field_149869_a.length; ++i)
        {
            this.field_149869_a[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }
}