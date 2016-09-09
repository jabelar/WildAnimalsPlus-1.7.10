package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block
{
    /** This piston is the sticky one? */
    private final boolean isSticky;
    /** Only visible when piston is extended */
    @SideOnly(Side.CLIENT)
    private IIcon innerTopIcon;
    /** Bottom side texture */
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    /** Top icon of piston depends on (either sticky or normal) */
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    private static final String __OBFID = "CL_00000366";

    public BlockPistonBase(boolean p_i45443_1_)
    {
        super(Material.piston);
        this.isSticky = p_i45443_1_;
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getPistonExtensionTexture()
    {
        return this.topIcon;
    }

    @SideOnly(Side.CLIENT)
    public void func_150070_b(float p_150070_1_, float p_150070_2_, float p_150070_3_, float p_150070_4_, float p_150070_5_, float p_150070_6_)
    {
        this.setBlockBounds(p_150070_1_, p_150070_2_, p_150070_3_, p_150070_4_, p_150070_5_, p_150070_6_);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 16;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int k = getPistonOrientation(meta);
        return k > 5 ? this.topIcon : (side == k ? (!isExtended(meta) && this.minX <= 0.0D && this.minY <= 0.0D && this.minZ <= 0.0D && this.maxX >= 1.0D && this.maxY >= 1.0D && this.maxZ >= 1.0D ? this.topIcon : this.innerTopIcon) : (side == Facing.oppositeSide[k] ? this.bottomIcon : this.blockIcon));
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getPistonBaseIcon(String p_150074_0_)
    {
        return p_150074_0_ == "piston_side" ? Blocks.piston.blockIcon : (p_150074_0_ == "piston_top_normal" ? Blocks.piston.topIcon : (p_150074_0_ == "piston_top_sticky" ? Blocks.sticky_piston.topIcon : (p_150074_0_ == "piston_inner" ? Blocks.piston.innerTopIcon : null)));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("piston_side");
        this.topIcon = reg.registerIcon(this.isSticky ? "piston_top_sticky" : "piston_top_normal");
        this.innerTopIcon = reg.registerIcon("piston_inner");
        this.bottomIcon = reg.registerIcon("piston_bottom");
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        return false;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = determineOrientation(worldIn, x, y, z, placer);
        worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);

        if (!worldIn.isRemote)
        {
            this.updatePistonState(worldIn, x, y, z);
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!worldIn.isRemote)
        {
            this.updatePistonState(worldIn, x, y, z);
        }
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(x, y, z) == null)
        {
            this.updatePistonState(worldIn, x, y, z);
        }
    }

    /**
     * handles attempts to extend or retract the piston.
     */
    private void updatePistonState(World p_150078_1_, int p_150078_2_, int p_150078_3_, int p_150078_4_)
    {
        int l = p_150078_1_.getBlockMetadata(p_150078_2_, p_150078_3_, p_150078_4_);
        int i1 = getPistonOrientation(l);

        if (i1 != 7)
        {
            boolean flag = this.isIndirectlyPowered(p_150078_1_, p_150078_2_, p_150078_3_, p_150078_4_, i1);

            if (flag && !isExtended(l))
            {
                if (canExtend(p_150078_1_, p_150078_2_, p_150078_3_, p_150078_4_, i1))
                {
                    p_150078_1_.addBlockEvent(p_150078_2_, p_150078_3_, p_150078_4_, this, 0, i1);
                }
            }
            else if (!flag && isExtended(l))
            {
                p_150078_1_.setBlockMetadataWithNotify(p_150078_2_, p_150078_3_, p_150078_4_, i1, 2);
                p_150078_1_.addBlockEvent(p_150078_2_, p_150078_3_, p_150078_4_, this, 1, i1);
            }
        }
    }

    private boolean isIndirectlyPowered(World p_150072_1_, int p_150072_2_, int p_150072_3_, int p_150072_4_, int p_150072_5_)
    {
        return p_150072_5_ != 0 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ - 1, p_150072_4_, 0) ? true : (p_150072_5_ != 1 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_, 1) ? true : (p_150072_5_ != 2 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ - 1, 2) ? true : (p_150072_5_ != 3 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ + 1, 3) ? true : (p_150072_5_ != 5 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_, p_150072_4_, 5) ? true : (p_150072_5_ != 4 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_, p_150072_4_, 4) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_, 0) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 2, p_150072_4_, 1) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ - 1, 2) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ + 1, 3) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_ + 1, p_150072_4_, 4) ? true : p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_ + 1, p_150072_4_, 5)))))))))));
    }

    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData)
    {
        if (!worldIn.isRemote)
        {
            boolean flag = this.isIndirectlyPowered(worldIn, x, y, z, eventData);

            if (flag && eventId == 1)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, eventData | 8, 2);
                return false;
            }

            if (!flag && eventId == 0)
            {
                return false;
            }
        }

        if (eventId == 0)
        {
            if (!this.tryExtend(worldIn, x, y, z, eventData))
            {
                return false;
            }

            worldIn.setBlockMetadataWithNotify(x, y, z, eventData | 8, 2);
            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (eventId == 1)
        {
            TileEntity tileentity1 = worldIn.getTileEntity(x + Facing.offsetsXForSide[eventData], y + Facing.offsetsYForSide[eventData], z + Facing.offsetsZForSide[eventData]);

            if (tileentity1 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)tileentity1).clearPistonTileEntity();
            }

            worldIn.setBlock(x, y, z, Blocks.piston_extension, eventData, 3);
            worldIn.setTileEntity(x, y, z, BlockPistonMoving.getTileEntity(this, eventData, eventData, false, true));

            if (this.isSticky)
            {
                int j1 = x + Facing.offsetsXForSide[eventData] * 2;
                int k1 = y + Facing.offsetsYForSide[eventData] * 2;
                int l1 = z + Facing.offsetsZForSide[eventData] * 2;
                Block block = worldIn.getBlock(j1, k1, l1);
                int i2 = worldIn.getBlockMetadata(j1, k1, l1);
                boolean flag1 = false;

                if (block == Blocks.piston_extension)
                {
                    TileEntity tileentity = worldIn.getTileEntity(j1, k1, l1);

                    if (tileentity instanceof TileEntityPiston)
                    {
                        TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;

                        if (tileentitypiston.getPistonOrientation() == eventData && tileentitypiston.isExtending())
                        {
                            tileentitypiston.clearPistonTileEntity();
                            block = tileentitypiston.getStoredBlockID();
                            i2 = tileentitypiston.getBlockMetadata();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && block.getMaterial() != Material.air && canPushBlock(block, worldIn, j1, k1, l1, false) && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston))
                {
                    x += Facing.offsetsXForSide[eventData];
                    y += Facing.offsetsYForSide[eventData];
                    z += Facing.offsetsZForSide[eventData];
                    worldIn.setBlock(x, y, z, Blocks.piston_extension, i2, 3);
                    worldIn.setTileEntity(x, y, z, BlockPistonMoving.getTileEntity(block, i2, eventData, false, false));
                    worldIn.setBlockToAir(j1, k1, l1);
                }
                else if (!flag1)
                {
                    worldIn.setBlockToAir(x + Facing.offsetsXForSide[eventData], y + Facing.offsetsYForSide[eventData], z + Facing.offsetsZForSide[eventData]);
                }
            }
            else
            {
                worldIn.setBlockToAir(x + Facing.offsetsXForSide[eventData], y + Facing.offsetsYForSide[eventData], z + Facing.offsetsZForSide[eventData]);
            }

            worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);

        if (isExtended(l))
        {
            float f = 0.25F;

            switch (getPistonOrientation(l))
            {
                case 0:
                    this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 1:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;
                case 2:
                    this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;
                case 3:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;
                case 4:
                    this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 5:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public static int getPistonOrientation(int p_150076_0_)
    {
        return p_150076_0_ & 7;
    }

    /**
     * Determine if the metadata is related to something powered.
     */
    public static boolean isExtended(int p_150075_0_)
    {
        return (p_150075_0_ & 8) != 0;
    }

    /**
     * gets the way this piston should face for that entity that placed it.
     */
    public static int determineOrientation(World p_150071_0_, int p_150071_1_, int p_150071_2_, int p_150071_3_, EntityLivingBase p_150071_4_)
    {
        if (MathHelper.abs((float)p_150071_4_.posX - (float)p_150071_1_) < 2.0F && MathHelper.abs((float)p_150071_4_.posZ - (float)p_150071_3_) < 2.0F)
        {
            double d0 = p_150071_4_.posY + 1.82D - (double)p_150071_4_.yOffset;

            if (d0 - (double)p_150071_2_ > 2.0D)
            {
                return 1;
            }

            if ((double)p_150071_2_ - d0 > 0.0D)
            {
                return 0;
            }
        }

        int l = MathHelper.floor_double((double)(p_150071_4_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }

    /**
     * returns true if the piston can push the specified block
     */
    private static boolean canPushBlock(Block p_150080_0_, World p_150080_1_, int p_150080_2_, int p_150080_3_, int p_150080_4_, boolean p_150080_5_)
    {
        if (p_150080_0_ == Blocks.obsidian)
        {
            return false;
        }
        else
        {
            if (p_150080_0_ != Blocks.piston && p_150080_0_ != Blocks.sticky_piston)
            {
                if (p_150080_0_.getBlockHardness(p_150080_1_, p_150080_2_, p_150080_3_, p_150080_4_) == -1.0F)
                {
                    return false;
                }

                if (p_150080_0_.getMobilityFlag() == 2)
                {
                    return false;
                }

                if (p_150080_0_.getMobilityFlag() == 1)
                {
                    if (!p_150080_5_)
                    {
                        return false;
                    }

                    return true;
                }
            }
            else if (isExtended(p_150080_1_.getBlockMetadata(p_150080_2_, p_150080_3_, p_150080_4_)))
            {
                return false;
            }

            return !(p_150080_1_.getBlock(p_150080_2_, p_150080_3_, p_150080_4_).hasTileEntity(p_150080_1_.getBlockMetadata(p_150080_2_, p_150080_3_, p_150080_4_)));
            
        }
    }

    /**
     * checks to see if this piston could push the blocks in front of it.
     */
    private static boolean canExtend(World p_150077_0_, int p_150077_1_, int p_150077_2_, int p_150077_3_, int p_150077_4_)
    {
        int i1 = p_150077_1_ + Facing.offsetsXForSide[p_150077_4_];
        int j1 = p_150077_2_ + Facing.offsetsYForSide[p_150077_4_];
        int k1 = p_150077_3_ + Facing.offsetsZForSide[p_150077_4_];
        int l1 = 0;

        while (true)
        {
            if (l1 < 13)
            {
                if (j1 <= 0 || j1 >= p_150077_0_.getHeight())
                {
                    return false;
                }

                Block block = p_150077_0_.getBlock(i1, j1, k1);

                if (!block.isAir(p_150077_0_, i1, j1, k1))
                {
                    if (!canPushBlock(block, p_150077_0_, i1, j1, k1, true))
                    {
                        return false;
                    }

                    if (block.getMobilityFlag() != 1)
                    {
                        if (l1 == 12)
                        {
                            return false;
                        }

                        i1 += Facing.offsetsXForSide[p_150077_4_];
                        j1 += Facing.offsetsYForSide[p_150077_4_];
                        k1 += Facing.offsetsZForSide[p_150077_4_];
                        ++l1;
                        continue;
                    }
                }
            }

            return true;
        }
    }

    /**
     * attempts to extend the piston. returns false if impossible.
     */
    private boolean tryExtend(World p_150079_1_, int p_150079_2_, int p_150079_3_, int p_150079_4_, int p_150079_5_)
    {
        int i1 = p_150079_2_ + Facing.offsetsXForSide[p_150079_5_];
        int j1 = p_150079_3_ + Facing.offsetsYForSide[p_150079_5_];
        int k1 = p_150079_4_ + Facing.offsetsZForSide[p_150079_5_];
        int l1 = 0;

        while (true)
        {
            if (l1 < 13)
            {
                if (j1 <= 0 || j1 >= p_150079_1_.getHeight())
                {
                    return false;
                }

                Block block = p_150079_1_.getBlock(i1, j1, k1);

                if (!block.isAir(p_150079_1_, i1, j1, k1))
                {
                    if (!canPushBlock(block, p_150079_1_, i1, j1, k1, true))
                    {
                        return false;
                    }

                    if (block.getMobilityFlag() != 1)
                    {
                        if (l1 == 12)
                        {
                            return false;
                        }

                        i1 += Facing.offsetsXForSide[p_150079_5_];
                        j1 += Facing.offsetsYForSide[p_150079_5_];
                        k1 += Facing.offsetsZForSide[p_150079_5_];
                        ++l1;
                        continue;
                    }

                    //With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                    float chance = block instanceof BlockSnow ? -1.0f : 1.0f;
                    block.dropBlockAsItemWithChance(p_150079_1_, i1, j1, k1, p_150079_1_.getBlockMetadata(i1, j1, k1), chance, 0);
                    p_150079_1_.setBlockToAir(i1, j1, k1);
                }
            }

            l1 = i1;
            int k3 = j1;
            int i2 = k1;
            int j2 = 0;
            Block[] ablock;
            int k2;
            int l2;
            int i3;

            for (ablock = new Block[13]; i1 != p_150079_2_ || j1 != p_150079_3_ || k1 != p_150079_4_; k1 = i3)
            {
                k2 = i1 - Facing.offsetsXForSide[p_150079_5_];
                l2 = j1 - Facing.offsetsYForSide[p_150079_5_];
                i3 = k1 - Facing.offsetsZForSide[p_150079_5_];
                Block block1 = p_150079_1_.getBlock(k2, l2, i3);
                int j3 = p_150079_1_.getBlockMetadata(k2, l2, i3);

                if (block1 == this && k2 == p_150079_2_ && l2 == p_150079_3_ && i3 == p_150079_4_)
                {
                    p_150079_1_.setBlock(i1, j1, k1, Blocks.piston_extension, p_150079_5_ | (this.isSticky ? 8 : 0), 4);
                    p_150079_1_.setTileEntity(i1, j1, k1, BlockPistonMoving.getTileEntity(Blocks.piston_head, p_150079_5_ | (this.isSticky ? 8 : 0), p_150079_5_, true, false));
                }
                else
                {
                    p_150079_1_.setBlock(i1, j1, k1, Blocks.piston_extension, j3, 4);
                    p_150079_1_.setTileEntity(i1, j1, k1, BlockPistonMoving.getTileEntity(block1, j3, p_150079_5_, true, false));
                }

                ablock[j2++] = block1;
                i1 = k2;
                j1 = l2;
            }

            i1 = l1;
            j1 = k3;
            k1 = i2;

            for (j2 = 0; i1 != p_150079_2_ || j1 != p_150079_3_ || k1 != p_150079_4_; k1 = i3)
            {
                k2 = i1 - Facing.offsetsXForSide[p_150079_5_];
                l2 = j1 - Facing.offsetsYForSide[p_150079_5_];
                i3 = k1 - Facing.offsetsZForSide[p_150079_5_];
                p_150079_1_.notifyBlocksOfNeighborChange(k2, l2, i3, ablock[j2++]);
                i1 = k2;
                j1 = l2;
            }

            return true;
        }
    }
}