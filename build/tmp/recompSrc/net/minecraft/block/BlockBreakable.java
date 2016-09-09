package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block
{
    private boolean ignoreSimilarity;
    private String name;
    private static final String __OBFID = "CL_00000254";

    protected BlockBreakable(String name, Material materialIn, boolean ignoreSimilarity)
    {
        super(materialIn);
        this.ignoreSimilarity = ignoreSimilarity;
        this.name = name;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        Block block = worldIn.getBlock(x, y, z);

        if (this == Blocks.glass || this == Blocks.stained_glass)
        {
            if (worldIn.getBlockMetadata(x, y, z) != worldIn.getBlockMetadata(x - Facing.offsetsXForSide[side], y - Facing.offsetsYForSide[side], z - Facing.offsetsZForSide[side]))
            {
                return true;
            }

            if (block == this)
            {
                return false;
            }
        }

        return !this.ignoreSimilarity && block == this ? false : super.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.name);
    }
}