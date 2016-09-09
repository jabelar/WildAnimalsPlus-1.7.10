package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockCarrot extends BlockCrops
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149868_a;
    private static final String __OBFID = "CL_00000212";

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

            return this.field_149868_a[meta >> 1];
        }
        else
        {
            return this.field_149868_a[3];
        }
    }

    protected Item getSeed()
    {
        return Items.carrot;
    }

    protected Item getCrop()
    {
        return Items.carrot;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149868_a = new IIcon[4];

        for (int i = 0; i < this.field_149868_a.length; ++i)
        {
            this.field_149868_a[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }
}