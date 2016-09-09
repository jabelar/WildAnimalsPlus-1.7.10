package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFarmland extends Block
{
    /** The texture to use for the top of dry farmland. */
    @SideOnly(Side.CLIENT)
    private IIcon iconDry;
    /** The texture to use for the top of wet (watered) farmland. */
    @SideOnly(Side.CLIENT)
    private IIcon iconWet;
    private static final String __OBFID = "CL_00000241";

    protected BlockFarmland()
    {
        super(Material.ground);
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox((double)(x + 0), (double)(y + 0), (double)(z + 0), (double)(x + 1), (double)(y + 1), (double)(z + 1));
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
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? (meta > 0 ? this.iconDry : this.iconWet) : Blocks.dirt.getBlockTextureFromSide(side);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!this.func_149821_m(worldIn, x, y, z) && !worldIn.isRainingAt(x, y + 1, z))
        {
            int l = worldIn.getBlockMetadata(x, y, z);

            if (l > 0)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, l - 1, 2);
            }
            else if (!this.func_149822_e(worldIn, x, y, z))
            {
                worldIn.setBlock(x, y, z, Blocks.dirt);
            }
        }
        else
        {
            worldIn.setBlockMetadataWithNotify(x, y, z, 7, 2);
        }
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    public void onFallenUpon(World worldIn, int x, int y, int z, Entity entityIn, float fallDistance)
    {
        if (!worldIn.isRemote && worldIn.rand.nextFloat() < fallDistance - 0.5F)
        {
            if (!(entityIn instanceof EntityPlayer) && !worldIn.getGameRules().getGameRuleBooleanValue("mobGriefing"))
            {
                return;
            }

            worldIn.setBlock(x, y, z, Blocks.dirt);
        }
    }

    private boolean func_149822_e(World p_149822_1_, int p_149822_2_, int p_149822_3_, int p_149822_4_)
    {
        byte b0 = 0;

        for (int l = p_149822_2_ - b0; l <= p_149822_2_ + b0; ++l)
        {
            for (int i1 = p_149822_4_ - b0; i1 <= p_149822_4_ + b0; ++i1)
            {
                Block block = p_149822_1_.getBlock(l, p_149822_3_ + 1, i1);

                if (block instanceof IPlantable && canSustainPlant(p_149822_1_, p_149822_2_, p_149822_3_, p_149822_4_, ForgeDirection.UP, (IPlantable)block))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean func_149821_m(World p_149821_1_, int p_149821_2_, int p_149821_3_, int p_149821_4_)
    {
        for (int l = p_149821_2_ - 4; l <= p_149821_2_ + 4; ++l)
        {
            for (int i1 = p_149821_3_; i1 <= p_149821_3_ + 1; ++i1)
            {
                for (int j1 = p_149821_4_ - 4; j1 <= p_149821_4_ + 4; ++j1)
                {
                    if (p_149821_1_.getBlock(l, i1, j1).getMaterial() == Material.water)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        Material material = worldIn.getBlock(x, y + 1, z).getMaterial();

        if (material.isSolid())
        {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Blocks.dirt.getItemDropped(0, random, fortune);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Item.getItemFromBlock(Blocks.dirt);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.iconDry = reg.registerIcon(this.getTextureName() + "_wet");
        this.iconWet = reg.registerIcon(this.getTextureName() + "_dry");
    }
}