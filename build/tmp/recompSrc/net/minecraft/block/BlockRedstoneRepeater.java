package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode
{
    /** The offsets for the two torches in redstone repeater blocks. */
    public static final double[] repeaterTorchOffset = new double[] { -0.0625D, 0.0625D, 0.1875D, 0.3125D};
    /** The states in which the redstone repeater blocks can be. */
    private static final int[] repeaterState = new int[] {1, 2, 3, 4};
    private static final String __OBFID = "CL_00000301";

    protected BlockRedstoneRepeater(boolean p_i45424_1_)
    {
        super(p_i45424_1_);
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);
        int j1 = (i1 & 12) >> 2;
        j1 = j1 + 1 << 2 & 12;
        worldIn.setBlockMetadataWithNotify(x, y, z, j1 | i1 & 3, 3);
        return true;
    }

    protected int func_149901_b(int p_149901_1_)
    {
        return repeaterState[(p_149901_1_ & 12) >> 2] * 2;
    }

    protected BlockRedstoneDiode getBlockPowered()
    {
        return Blocks.powered_repeater;
    }

    protected BlockRedstoneDiode getBlockUnpowered()
    {
        return Blocks.unpowered_repeater;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.repeater;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.repeater;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 15;
    }

    public boolean func_149910_g(IBlockAccess p_149910_1_, int p_149910_2_, int p_149910_3_, int p_149910_4_, int p_149910_5_)
    {
        return this.func_149902_h(p_149910_1_, p_149910_2_, p_149910_3_, p_149910_4_, p_149910_5_) > 0;
    }

    protected boolean func_149908_a(Block p_149908_1_)
    {
        return isRedstoneRepeaterBlockID(p_149908_1_);
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        this.func_149911_e(worldIn, x, y, z);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        if (this.isRepeaterPowered)
        {
            int l = worldIn.getBlockMetadata(x, y, z);
            int i1 = getDirection(l);
            double d0 = (double)((float)x + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)y + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)z + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.0D;
            double d4 = 0.0D;

            if (random.nextInt(2) == 0)
            {
                switch (i1)
                {
                    case 0:
                        d4 = -0.3125D;
                        break;
                    case 1:
                        d3 = 0.3125D;
                        break;
                    case 2:
                        d4 = 0.3125D;
                        break;
                    case 3:
                        d3 = -0.3125D;
                }
            }
            else
            {
                int j1 = (l & 12) >> 2;

                switch (i1)
                {
                    case 0:
                        d4 = repeaterTorchOffset[j1];
                        break;
                    case 1:
                        d3 = -repeaterTorchOffset[j1];
                        break;
                    case 2:
                        d4 = -repeaterTorchOffset[j1];
                        break;
                    case 3:
                        d3 = repeaterTorchOffset[j1];
                }
            }

            worldIn.spawnParticle("reddust", d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }
}