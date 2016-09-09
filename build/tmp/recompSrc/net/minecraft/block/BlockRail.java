package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase
{
    @SideOnly(Side.CLIENT)
    private IIcon field_150056_b;
    private static final String __OBFID = "CL_00000293";

    protected BlockRail()
    {
        super(false);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return meta >= 6 ? this.field_150056_b : this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        this.field_150056_b = reg.registerIcon(this.getTextureName() + "_turned");
    }

    /**
     * Called when the rail is given a redstone signal.
     */
    protected void onRedstoneSignal(World p_150048_1_, int p_150048_2_, int p_150048_3_, int p_150048_4_, int p_150048_5_, int p_150048_6_, Block p_150048_7_)
    {
        if (p_150048_7_.canProvidePower() && (new BlockRailBase.Rail(p_150048_1_, p_150048_2_, p_150048_3_, p_150048_4_)).countAdjacentRails() == 3)
        {
            this.refreshTrackShape(p_150048_1_, p_150048_2_, p_150048_3_, p_150048_4_, false);
        }
    }
}