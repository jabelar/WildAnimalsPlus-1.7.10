package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon field_150088_a;
    private static final String __OBFID = "CL_00000367";

    public BlockPistonExtension()
    {
        super(Material.piston);
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
    }

    @SideOnly(Side.CLIENT)
    public void func_150086_a(IIcon p_150086_1_)
    {
        this.field_150088_a = p_150086_1_;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            int i1 = getDirectionMeta(meta);
            Block block = worldIn.getBlock(x - Facing.offsetsXForSide[i1], y - Facing.offsetsYForSide[i1], z - Facing.offsetsZForSide[i1]);

            if (block == Blocks.piston || block == Blocks.sticky_piston)
            {
                worldIn.setBlockToAir(x - Facing.offsetsXForSide[i1], y - Facing.offsetsYForSide[i1], z - Facing.offsetsZForSide[i1]);
            }
        }

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        int i1 = Facing.oppositeSide[getDirectionMeta(meta)];
        x += Facing.offsetsXForSide[i1];
        y += Facing.offsetsYForSide[i1];
        z += Facing.offsetsZForSide[i1];
        Block block1 = worldIn.getBlock(x, y, z);

        if (block1 == Blocks.piston || block1 == Blocks.sticky_piston)
        {
            meta = worldIn.getBlockMetadata(x, y, z);

            if (BlockPistonBase.isExtended(meta))
            {
                block1.dropBlockAsItem(worldIn, x, y, z, meta, 0);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_150087_e()
    {
        this.field_150088_a = null;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int k = getDirectionMeta(meta);
        return side == k ? (this.field_150088_a != null ? this.field_150088_a : ((meta & 8) != 0 ? BlockPistonBase.getPistonBaseIcon("piston_top_sticky") : BlockPistonBase.getPistonBaseIcon("piston_top_normal"))) : (k < 6 && side == Facing.oppositeSide[k] ? BlockPistonBase.getPistonBaseIcon("piston_top_normal") : BlockPistonBase.getPistonBaseIcon("piston_side"));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 17;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return false;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.25F;
        float f1 = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.25F;
        float f4 = 0.75F;

        switch (getDirectionMeta(l))
        {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.25F;

        switch (getDirectionMeta(l))
        {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        int l = getDirectionMeta(worldIn.getBlockMetadata(x, y, z));
        Block block1 = worldIn.getBlock(x - Facing.offsetsXForSide[l], y - Facing.offsetsYForSide[l], z - Facing.offsetsZForSide[l]);

        if (block1 != Blocks.piston && block1 != Blocks.sticky_piston)
        {
            worldIn.setBlockToAir(x, y, z);
        }
        else
        {
            block1.onNeighborBlockChange(worldIn, x - Facing.offsetsXForSide[l], y - Facing.offsetsYForSide[l], z - Facing.offsetsZForSide[l], neighbor);
        }
    }

    public static int getDirectionMeta(int p_150085_0_)
    {
        return MathHelper.clamp_int(p_150085_0_ & 7, 0, Facing.offsetsXForSide.length - 1);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        return (l & 8) != 0 ? Item.getItemFromBlock(Blocks.sticky_piston) : Item.getItemFromBlock(Blocks.piston);
    }
}