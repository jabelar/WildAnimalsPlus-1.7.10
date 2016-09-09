package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block
{
    private static final String[] field_149793_a = new String[] {"skin_brown", "skin_red"};
    private final int field_149792_b;
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149794_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_149795_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_149796_O;
    private static final String __OBFID = "CL_00000258";

    public BlockHugeMushroom(Material p_i45412_1_, int p_i45412_2_)
    {
        super(p_i45412_1_);
        this.field_149792_b = p_i45412_2_;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return meta == 10 && side > 1 ? this.field_149795_N : (meta >= 1 && meta <= 9 && side == 1 ? this.field_149794_M[this.field_149792_b] : (meta >= 1 && meta <= 3 && side == 2 ? this.field_149794_M[this.field_149792_b] : (meta >= 7 && meta <= 9 && side == 3 ? this.field_149794_M[this.field_149792_b] : ((meta == 1 || meta == 4 || meta == 7) && side == 4 ? this.field_149794_M[this.field_149792_b] : ((meta == 3 || meta == 6 || meta == 9) && side == 5 ? this.field_149794_M[this.field_149792_b] : (meta == 14 ? this.field_149794_M[this.field_149792_b] : (meta == 15 ? this.field_149795_N : this.field_149796_O)))))));
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        int i = random.nextInt(10) - 7;

        if (i < 0)
        {
            i = 0;
        }

        return i;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Item.getItemById(Block.getIdFromBlock(Blocks.brown_mushroom) + this.field_149792_b);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Item.getItemById(Block.getIdFromBlock(Blocks.brown_mushroom) + this.field_149792_b);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149794_M = new IIcon[field_149793_a.length];

        for (int i = 0; i < this.field_149794_M.length; ++i)
        {
            this.field_149794_M[i] = reg.registerIcon(this.getTextureName() + "_" + field_149793_a[i]);
        }

        this.field_149796_O = reg.registerIcon(this.getTextureName() + "_" + "inside");
        this.field_149795_N = reg.registerIcon(this.getTextureName() + "_" + "skin_stem");
    }
}