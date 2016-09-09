package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer
{
    private static final String __OBFID = "CL_00000307";

    protected BlockSkull()
    {
        super(Material.circuits);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z) & 7;

        switch (l)
        {
            case 1:
            default:
                this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case 2:
                this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case 3:
                this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case 4:
                this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 5:
                this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
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

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySkull();
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.skull;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        TileEntity tileentity = worldIn.getTileEntity(x, y, z);
        return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull)tileentity).getSkullType() : super.getDamageValue(worldIn, x, y, z);
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return meta;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            meta |= 8;
            worldIn.setBlockMetadataWithNotify(x, y, z, meta, 4);
        }
        this.dropBlockAsItem(worldIn, x, y, z, meta, 0);

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World worldIn, int x, int y, int z, int meta, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        {
            if ((meta & 8) == 0)
            {
                ItemStack itemstack = new ItemStack(Items.skull, 1, this.getDamageValue(worldIn, x, y, z));
                TileEntitySkull tileentityskull = (TileEntitySkull)worldIn.getTileEntity(x, y, z);

                if (tileentityskull == null) return ret;

                if (tileentityskull.getSkullType() == 3 && tileentityskull.func_152108_a() != null)
                {
                    itemstack.setTagCompound(new NBTTagCompound());
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    NBTUtil.writeGameProfileToNBT(nbttagcompound, tileentityskull.func_152108_a());
                    itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
                }

                ret.add(itemstack);
            }
        }
        return ret;
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.skull;
    }

    public void makeWither(World p_149965_1_, int p_149965_2_, int p_149965_3_, int p_149965_4_, TileEntitySkull p_149965_5_)
    {
        if (p_149965_5_.getSkullType() == 1 && p_149965_3_ >= 2 && p_149965_1_.difficultySetting != EnumDifficulty.PEACEFUL && !p_149965_1_.isRemote)
        {
            int l;
            EntityWither entitywither;
            Iterator iterator;
            EntityPlayer entityplayer;
            int i1;

            for (l = -2; l <= 0; ++l)
            {
                if (p_149965_1_.getBlock(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l) == Blocks.soul_sand && p_149965_1_.getBlock(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l + 1) == Blocks.soul_sand && p_149965_1_.getBlock(p_149965_2_, p_149965_3_ - 2, p_149965_4_ + l + 1) == Blocks.soul_sand && p_149965_1_.getBlock(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l + 2) == Blocks.soul_sand && this.func_149966_a(p_149965_1_, p_149965_2_, p_149965_3_, p_149965_4_ + l, 1) && this.func_149966_a(p_149965_1_, p_149965_2_, p_149965_3_, p_149965_4_ + l + 1, 1) && this.func_149966_a(p_149965_1_, p_149965_2_, p_149965_3_, p_149965_4_ + l + 2, 1))
                {
                    p_149965_1_.setBlockMetadataWithNotify(p_149965_2_, p_149965_3_, p_149965_4_ + l, 8, 2);
                    p_149965_1_.setBlockMetadataWithNotify(p_149965_2_, p_149965_3_, p_149965_4_ + l + 1, 8, 2);
                    p_149965_1_.setBlockMetadataWithNotify(p_149965_2_, p_149965_3_, p_149965_4_ + l + 2, 8, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_, p_149965_4_ + l, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_, p_149965_4_ + l + 1, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_, p_149965_4_ + l + 2, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l + 1, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l + 2, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_, p_149965_3_ - 2, p_149965_4_ + l + 1, getBlockById(0), 0, 2);

                    if (!p_149965_1_.isRemote)
                    {
                        entitywither = new EntityWither(p_149965_1_);
                        entitywither.setLocationAndAngles((double)p_149965_2_ + 0.5D, (double)p_149965_3_ - 1.45D, (double)(p_149965_4_ + l) + 1.5D, 90.0F, 0.0F);
                        entitywither.renderYawOffset = 90.0F;
                        entitywither.func_82206_m();

                        if (!p_149965_1_.isRemote)
                        {
                            iterator = p_149965_1_.getEntitiesWithinAABB(EntityPlayer.class, entitywither.boundingBox.expand(50.0D, 50.0D, 50.0D)).iterator();

                            while (iterator.hasNext())
                            {
                                entityplayer = (EntityPlayer)iterator.next();
                                entityplayer.triggerAchievement(AchievementList.spawnWither);
                            }
                        }

                        p_149965_1_.spawnEntityInWorld(entitywither);
                    }

                    for (i1 = 0; i1 < 120; ++i1)
                    {
                        p_149965_1_.spawnParticle("snowballpoof", (double)p_149965_2_ + p_149965_1_.rand.nextDouble(), (double)(p_149965_3_ - 2) + p_149965_1_.rand.nextDouble() * 3.9D, (double)(p_149965_4_ + l + 1) + p_149965_1_.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_, p_149965_4_ + l, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_, p_149965_4_ + l + 1, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_, p_149965_4_ + l + 2, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l + 1, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_ - 1, p_149965_4_ + l + 2, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_, p_149965_3_ - 2, p_149965_4_ + l + 1, getBlockById(0));
                    return;
                }
            }

            for (l = -2; l <= 0; ++l)
            {
                if (p_149965_1_.getBlock(p_149965_2_ + l, p_149965_3_ - 1, p_149965_4_) == Blocks.soul_sand && p_149965_1_.getBlock(p_149965_2_ + l + 1, p_149965_3_ - 1, p_149965_4_) == Blocks.soul_sand && p_149965_1_.getBlock(p_149965_2_ + l + 1, p_149965_3_ - 2, p_149965_4_) == Blocks.soul_sand && p_149965_1_.getBlock(p_149965_2_ + l + 2, p_149965_3_ - 1, p_149965_4_) == Blocks.soul_sand && this.func_149966_a(p_149965_1_, p_149965_2_ + l, p_149965_3_, p_149965_4_, 1) && this.func_149966_a(p_149965_1_, p_149965_2_ + l + 1, p_149965_3_, p_149965_4_, 1) && this.func_149966_a(p_149965_1_, p_149965_2_ + l + 2, p_149965_3_, p_149965_4_, 1))
                {
                    p_149965_1_.setBlockMetadataWithNotify(p_149965_2_ + l, p_149965_3_, p_149965_4_, 8, 2);
                    p_149965_1_.setBlockMetadataWithNotify(p_149965_2_ + l + 1, p_149965_3_, p_149965_4_, 8, 2);
                    p_149965_1_.setBlockMetadataWithNotify(p_149965_2_ + l + 2, p_149965_3_, p_149965_4_, 8, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l, p_149965_3_, p_149965_4_, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l + 1, p_149965_3_, p_149965_4_, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l + 2, p_149965_3_, p_149965_4_, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l, p_149965_3_ - 1, p_149965_4_, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l + 1, p_149965_3_ - 1, p_149965_4_, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l + 2, p_149965_3_ - 1, p_149965_4_, getBlockById(0), 0, 2);
                    p_149965_1_.setBlock(p_149965_2_ + l + 1, p_149965_3_ - 2, p_149965_4_, getBlockById(0), 0, 2);

                    if (!p_149965_1_.isRemote)
                    {
                        entitywither = new EntityWither(p_149965_1_);
                        entitywither.setLocationAndAngles((double)(p_149965_2_ + l) + 1.5D, (double)p_149965_3_ - 1.45D, (double)p_149965_4_ + 0.5D, 0.0F, 0.0F);
                        entitywither.func_82206_m();

                        if (!p_149965_1_.isRemote)
                        {
                            iterator = p_149965_1_.getEntitiesWithinAABB(EntityPlayer.class, entitywither.boundingBox.expand(50.0D, 50.0D, 50.0D)).iterator();

                            while (iterator.hasNext())
                            {
                                entityplayer = (EntityPlayer)iterator.next();
                                entityplayer.triggerAchievement(AchievementList.spawnWither);
                            }
                        }

                        p_149965_1_.spawnEntityInWorld(entitywither);
                    }

                    for (i1 = 0; i1 < 120; ++i1)
                    {
                        p_149965_1_.spawnParticle("snowballpoof", (double)(p_149965_2_ + l + 1) + p_149965_1_.rand.nextDouble(), (double)(p_149965_3_ - 2) + p_149965_1_.rand.nextDouble() * 3.9D, (double)p_149965_4_ + p_149965_1_.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    p_149965_1_.notifyBlockChange(p_149965_2_ + l, p_149965_3_, p_149965_4_, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_ + l + 1, p_149965_3_, p_149965_4_, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_ + l + 2, p_149965_3_, p_149965_4_, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_ + l, p_149965_3_ - 1, p_149965_4_, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_ + l + 1, p_149965_3_ - 1, p_149965_4_, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_ + l + 2, p_149965_3_ - 1, p_149965_4_, getBlockById(0));
                    p_149965_1_.notifyBlockChange(p_149965_2_ + l + 1, p_149965_3_ - 2, p_149965_4_, getBlockById(0));
                    return;
                }
            }
        }
    }

    private boolean func_149966_a(World p_149966_1_, int p_149966_2_, int p_149966_3_, int p_149966_4_, int p_149966_5_)
    {
        if (p_149966_1_.getBlock(p_149966_2_, p_149966_3_, p_149966_4_) != this)
        {
            return false;
        }
        else
        {
            TileEntity tileentity = p_149966_1_.getTileEntity(p_149966_2_, p_149966_3_, p_149966_4_);
            return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull)tileentity).getSkullType() == p_149966_5_ : false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.soul_sand.getBlockTextureFromSide(side);
    }

    /**
     * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers.
     */
    @SideOnly(Side.CLIENT)
    public String getItemIconName()
    {
        return this.getTextureName() + "_" + ItemSkull.field_94587_a[0];
    }
}