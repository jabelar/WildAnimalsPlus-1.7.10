package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block
{
    private boolean canProvidePower = true;
    private Set field_150179_b = new HashSet();
    @SideOnly(Side.CLIENT)
    private IIcon redstoneCrossIcon;
    @SideOnly(Side.CLIENT)
    private IIcon redstoneLineIcon;
    @SideOnly(Side.CLIENT)
    private IIcon redstoneCrossOverlayIcon;
    @SideOnly(Side.CLIENT)
    private IIcon redstoneLineOverlayIcon;
    private static final String __OBFID = "CL_00000295";

    public BlockRedstoneWire()
    {
        super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 5;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z)
    {
        return 8388608;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || worldIn.getBlock(x, y - 1, z) == Blocks.glowstone;
    }

    private void func_150177_e(World p_150177_1_, int p_150177_2_, int p_150177_3_, int p_150177_4_)
    {
        this.func_150175_a(p_150177_1_, p_150177_2_, p_150177_3_, p_150177_4_, p_150177_2_, p_150177_3_, p_150177_4_);
        ArrayList arraylist = new ArrayList(this.field_150179_b);
        this.field_150179_b.clear();

        for (int l = 0; l < arraylist.size(); ++l)
        {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(l);
            p_150177_1_.notifyBlocksOfNeighborChange(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ, this);
        }
    }

    private void func_150175_a(World p_150175_1_, int p_150175_2_, int p_150175_3_, int p_150175_4_, int p_150175_5_, int p_150175_6_, int p_150175_7_)
    {
        int k1 = p_150175_1_.getBlockMetadata(p_150175_2_, p_150175_3_, p_150175_4_);
        byte b0 = 0;
        int i3 = this.func_150178_a(p_150175_1_, p_150175_5_, p_150175_6_, p_150175_7_, b0);
        this.canProvidePower = false;
        int l1 = p_150175_1_.getStrongestIndirectPower(p_150175_2_, p_150175_3_, p_150175_4_);
        this.canProvidePower = true;

        if (l1 > 0 && l1 > i3 - 1)
        {
            i3 = l1;
        }

        int i2 = 0;

        for (int j2 = 0; j2 < 4; ++j2)
        {
            int k2 = p_150175_2_;
            int l2 = p_150175_4_;

            if (j2 == 0)
            {
                k2 = p_150175_2_ - 1;
            }

            if (j2 == 1)
            {
                ++k2;
            }

            if (j2 == 2)
            {
                l2 = p_150175_4_ - 1;
            }

            if (j2 == 3)
            {
                ++l2;
            }

            if (k2 != p_150175_5_ || l2 != p_150175_7_)
            {
                i2 = this.func_150178_a(p_150175_1_, k2, p_150175_3_, l2, i2);
            }

            if (p_150175_1_.getBlock(k2, p_150175_3_, l2).isNormalCube() && !p_150175_1_.getBlock(p_150175_2_, p_150175_3_ + 1, p_150175_4_).isNormalCube())
            {
                if ((k2 != p_150175_5_ || l2 != p_150175_7_) && p_150175_3_ >= p_150175_6_)
                {
                    i2 = this.func_150178_a(p_150175_1_, k2, p_150175_3_ + 1, l2, i2);
                }
            }
            else if (!p_150175_1_.getBlock(k2, p_150175_3_, l2).isNormalCube() && (k2 != p_150175_5_ || l2 != p_150175_7_) && p_150175_3_ <= p_150175_6_)
            {
                i2 = this.func_150178_a(p_150175_1_, k2, p_150175_3_ - 1, l2, i2);
            }
        }

        if (i2 > i3)
        {
            i3 = i2 - 1;
        }
        else if (i3 > 0)
        {
            --i3;
        }
        else
        {
            i3 = 0;
        }

        if (l1 > i3 - 1)
        {
            i3 = l1;
        }

        if (k1 != i3)
        {
            p_150175_1_.setBlockMetadataWithNotify(p_150175_2_, p_150175_3_, p_150175_4_, i3, 2);
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_ - 1, p_150175_3_, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_ + 1, p_150175_3_, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_ - 1, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_ + 1, p_150175_4_));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_, p_150175_4_ - 1));
            this.field_150179_b.add(new ChunkPosition(p_150175_2_, p_150175_3_, p_150175_4_ + 1));
        }
    }

    private void func_150172_m(World p_150172_1_, int p_150172_2_, int p_150172_3_, int p_150172_4_)
    {
        if (p_150172_1_.getBlock(p_150172_2_, p_150172_3_, p_150172_4_) == this)
        {
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_, p_150172_3_, p_150172_4_, this);
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_ - 1, p_150172_3_, p_150172_4_, this);
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_ + 1, p_150172_3_, p_150172_4_, this);
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_, p_150172_3_, p_150172_4_ - 1, this);
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_, p_150172_3_, p_150172_4_ + 1, this);
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_, p_150172_3_ - 1, p_150172_4_, this);
            p_150172_1_.notifyBlocksOfNeighborChange(p_150172_2_, p_150172_3_ + 1, p_150172_4_, this);
        }
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        super.onBlockAdded(worldIn, x, y, z);

        if (!worldIn.isRemote)
        {
            this.func_150177_e(worldIn, x, y, z);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            this.func_150172_m(worldIn, x - 1, y, z);
            this.func_150172_m(worldIn, x + 1, y, z);
            this.func_150172_m(worldIn, x, y, z - 1);
            this.func_150172_m(worldIn, x, y, z + 1);

            if (worldIn.getBlock(x - 1, y, z).isNormalCube())
            {
                this.func_150172_m(worldIn, x - 1, y + 1, z);
            }
            else
            {
                this.func_150172_m(worldIn, x - 1, y - 1, z);
            }

            if (worldIn.getBlock(x + 1, y, z).isNormalCube())
            {
                this.func_150172_m(worldIn, x + 1, y + 1, z);
            }
            else
            {
                this.func_150172_m(worldIn, x + 1, y - 1, z);
            }

            if (worldIn.getBlock(x, y, z - 1).isNormalCube())
            {
                this.func_150172_m(worldIn, x, y + 1, z - 1);
            }
            else
            {
                this.func_150172_m(worldIn, x, y - 1, z - 1);
            }

            if (worldIn.getBlock(x, y, z + 1).isNormalCube())
            {
                this.func_150172_m(worldIn, x, y + 1, z + 1);
            }
            else
            {
                this.func_150172_m(worldIn, x, y - 1, z + 1);
            }
        }
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);

        if (!worldIn.isRemote)
        {
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            this.func_150177_e(worldIn, x, y, z);
            this.func_150172_m(worldIn, x - 1, y, z);
            this.func_150172_m(worldIn, x + 1, y, z);
            this.func_150172_m(worldIn, x, y, z - 1);
            this.func_150172_m(worldIn, x, y, z + 1);

            if (worldIn.getBlock(x - 1, y, z).isNormalCube())
            {
                this.func_150172_m(worldIn, x - 1, y + 1, z);
            }
            else
            {
                this.func_150172_m(worldIn, x - 1, y - 1, z);
            }

            if (worldIn.getBlock(x + 1, y, z).isNormalCube())
            {
                this.func_150172_m(worldIn, x + 1, y + 1, z);
            }
            else
            {
                this.func_150172_m(worldIn, x + 1, y - 1, z);
            }

            if (worldIn.getBlock(x, y, z - 1).isNormalCube())
            {
                this.func_150172_m(worldIn, x, y + 1, z - 1);
            }
            else
            {
                this.func_150172_m(worldIn, x, y - 1, z - 1);
            }

            if (worldIn.getBlock(x, y, z + 1).isNormalCube())
            {
                this.func_150172_m(worldIn, x, y + 1, z + 1);
            }
            else
            {
                this.func_150172_m(worldIn, x, y - 1, z + 1);
            }
        }
    }

    private int func_150178_a(World p_150178_1_, int p_150178_2_, int p_150178_3_, int p_150178_4_, int p_150178_5_)
    {
        if (p_150178_1_.getBlock(p_150178_2_, p_150178_3_, p_150178_4_) != this)
        {
            return p_150178_5_;
        }
        else
        {
            int i1 = p_150178_1_.getBlockMetadata(p_150178_2_, p_150178_3_, p_150178_4_);
            return i1 > p_150178_5_ ? i1 : p_150178_5_;
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!worldIn.isRemote)
        {
            boolean flag = this.canPlaceBlockAt(worldIn, x, y, z);

            if (flag)
            {
                this.func_150177_e(worldIn, x, y, z);
            }
            else
            {
                this.dropBlockAsItem(worldIn, x, y, z, 0, 0);
                worldIn.setBlockToAir(x, y, z);
            }

            super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.redstone;
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return !this.canProvidePower ? 0 : this.isProvidingWeakPower(worldIn, x, y, z, side);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        if (!this.canProvidePower)
        {
            return 0;
        }
        else
        {
            int i1 = worldIn.getBlockMetadata(x, y, z);

            if (i1 == 0)
            {
                return 0;
            }
            else if (side == 1)
            {
                return i1;
            }
            else
            {
                boolean flag = func_150176_g(worldIn, x - 1, y, z, 1) || !worldIn.getBlock(x - 1, y, z).isNormalCube() && func_150176_g(worldIn, x - 1, y - 1, z, -1);
                boolean flag1 = func_150176_g(worldIn, x + 1, y, z, 3) || !worldIn.getBlock(x + 1, y, z).isNormalCube() && func_150176_g(worldIn, x + 1, y - 1, z, -1);
                boolean flag2 = func_150176_g(worldIn, x, y, z - 1, 2) || !worldIn.getBlock(x, y, z - 1).isNormalCube() && func_150176_g(worldIn, x, y - 1, z - 1, -1);
                boolean flag3 = func_150176_g(worldIn, x, y, z + 1, 0) || !worldIn.getBlock(x, y, z + 1).isNormalCube() && func_150176_g(worldIn, x, y - 1, z + 1, -1);

                if (!worldIn.getBlock(x, y + 1, z).isNormalCube())
                {
                    if (worldIn.getBlock(x - 1, y, z).isNormalCube() && func_150176_g(worldIn, x - 1, y + 1, z, -1))
                    {
                        flag = true;
                    }

                    if (worldIn.getBlock(x + 1, y, z).isNormalCube() && func_150176_g(worldIn, x + 1, y + 1, z, -1))
                    {
                        flag1 = true;
                    }

                    if (worldIn.getBlock(x, y, z - 1).isNormalCube() && func_150176_g(worldIn, x, y + 1, z - 1, -1))
                    {
                        flag2 = true;
                    }

                    if (worldIn.getBlock(x, y, z + 1).isNormalCube() && func_150176_g(worldIn, x, y + 1, z + 1, -1))
                    {
                        flag3 = true;
                    }
                }

                return !flag2 && !flag1 && !flag && !flag3 && side >= 2 && side <= 5 ? i1 : (side == 2 && flag2 && !flag && !flag1 ? i1 : (side == 3 && flag3 && !flag && !flag1 ? i1 : (side == 4 && flag && !flag2 && !flag3 ? i1 : (side == 5 && flag1 && !flag2 && !flag3 ? i1 : 0))));
            }
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return this.canProvidePower;
    }

    /**
     * Returns true if redstone wire can connect to the specified block. Params: World, X, Y, Z, side (not a normal
     * notch-side, this can be 0, 1, 2, 3 or -1)
     */
    public static boolean isPowerProviderOrWire(IBlockAccess p_150174_0_, int p_150174_1_, int p_150174_2_, int p_150174_3_, int p_150174_4_)
    {
        Block block = p_150174_0_.getBlock(p_150174_1_, p_150174_2_, p_150174_3_);

        if (block == Blocks.redstone_wire)
        {
            return true;
        }
        else if (!Blocks.unpowered_repeater.func_149907_e(block))
        {
            return block.canConnectRedstone(p_150174_0_, p_150174_1_, p_150174_2_, p_150174_3_, p_150174_4_);
        }
        else
        {
            int i1 = p_150174_0_.getBlockMetadata(p_150174_1_, p_150174_2_, p_150174_3_);
            return p_150174_4_ == (i1 & 3) || p_150174_4_ == Direction.rotateOpposite[i1 & 3];
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        int l = worldIn.getBlockMetadata(x, y, z);

        if (l > 0)
        {
            double d0 = (double)x + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.2D;
            double d1 = (double)((float)y + 0.0625F);
            double d2 = (double)z + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.2D;
            float f = (float)l / 15.0F;
            float f1 = f * 0.6F + 0.4F;

            if (l == 0)
            {
                f1 = 0.0F;
            }

            float f2 = f * f * 0.7F - 0.5F;
            float f3 = f * f * 0.6F - 0.7F;

            if (f2 < 0.0F)
            {
                f2 = 0.0F;
            }

            if (f3 < 0.0F)
            {
                f3 = 0.0F;
            }

            worldIn.spawnParticle("reddust", d0, d1, d2, (double)f1, (double)f2, (double)f3);
        }
    }

    public static boolean func_150176_g(IBlockAccess p_150176_0_, int p_150176_1_, int p_150176_2_, int p_150176_3_, int p_150176_4_)
    {
        if (isPowerProviderOrWire(p_150176_0_, p_150176_1_, p_150176_2_, p_150176_3_, p_150176_4_))
        {
            return true;
        }
        else if (p_150176_0_.getBlock(p_150176_1_, p_150176_2_, p_150176_3_) == Blocks.powered_repeater)
        {
            int i1 = p_150176_0_.getBlockMetadata(p_150176_1_, p_150176_2_, p_150176_3_);
            return p_150176_4_ == (i1 & 3);
        }
        else
        {
            return false;
        }
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.redstone;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.redstoneCrossIcon = reg.registerIcon(this.getTextureName() + "_" + "cross");
        this.redstoneLineIcon = reg.registerIcon(this.getTextureName() + "_" + "line");
        this.redstoneCrossOverlayIcon = reg.registerIcon(this.getTextureName() + "_" + "cross_overlay");
        this.redstoneLineOverlayIcon = reg.registerIcon(this.getTextureName() + "_" + "line_overlay");
        this.blockIcon = this.redstoneCrossIcon;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getRedstoneWireIcon(String p_150173_0_)
    {
        return p_150173_0_.equals("cross") ? Blocks.redstone_wire.redstoneCrossIcon : (p_150173_0_.equals("line") ? Blocks.redstone_wire.redstoneLineIcon : (p_150173_0_.equals("cross_overlay") ? Blocks.redstone_wire.redstoneCrossOverlayIcon : (p_150173_0_.equals("line_overlay") ? Blocks.redstone_wire.redstoneLineOverlayIcon : null)));
    }
}