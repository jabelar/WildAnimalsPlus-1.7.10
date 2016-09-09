package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class BlockDoublePlant extends BlockBush implements IGrowable, IShearable
{
    public static final String[] field_149892_a = new String[] {"sunflower", "syringa", "grass", "fern", "rose", "paeonia"};
    @SideOnly(Side.CLIENT)
    private IIcon[] doublePlantBottomIcons;
    @SideOnly(Side.CLIENT)
    private IIcon[] doublePlantTopIcons;
    @SideOnly(Side.CLIENT)
    public IIcon[] sunflowerIcons;
    private static final String __OBFID = "CL_00000231";

    public BlockDoublePlant()
    {
        super(Material.plants);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.setUnlocalizedName("doublePlant");
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 40;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public int func_149885_e(IBlockAccess p_149885_1_, int p_149885_2_, int p_149885_3_, int p_149885_4_)
    {
        int l = p_149885_1_.getBlockMetadata(p_149885_2_, p_149885_3_, p_149885_4_);
        return !func_149887_c(l) ? l & 7 : p_149885_1_.getBlockMetadata(p_149885_2_, p_149885_3_ - 1, p_149885_4_) & 7;
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return super.canPlaceBlockAt(worldIn, x, y, z) && worldIn.isAirBlock(x, y + 1, z);
    }

    /**
     * checks if the block can stay, if not drop as item
     */
    protected void checkAndDropBlock(World worldIn, int x, int y, int z)
    {
        if (!this.canBlockStay(worldIn, x, y, z))
        {
            int l = worldIn.getBlockMetadata(x, y, z);

            if (!func_149887_c(l))
            {
                this.dropBlockAsItem(worldIn, x, y, z, l, 0);

                if (worldIn.getBlock(x, y + 1, z) == this)
                {
                    worldIn.setBlock(x, y + 1, z, Blocks.air, 0, 2);
                }
            }

            worldIn.setBlock(x, y, z, Blocks.air, 0, 2);
        }
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World worldIn, int x, int y, int z)
    {
        if (worldIn.getBlock(x, y, z) != this) return super.canBlockStay(worldIn, x, y, z); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
        int l = worldIn.getBlockMetadata(x, y, z);
        return func_149887_c(l) ? worldIn.getBlock(x, y - 1, z) == this : worldIn.getBlock(x, y + 1, z) == this && super.canBlockStay(worldIn, x, y, z);
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        if (func_149887_c(meta))
        {
            return null;
        }
        else
        {
            int k = func_149890_d(meta);
            return k != 3 && k != 2 ? Item.getItemFromBlock(this) : null;
        }
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return func_149887_c(meta) ? 0 : meta & 7;
    }

    public static boolean func_149887_c(int p_149887_0_)
    {
        return (p_149887_0_ & 8) != 0;
    }

    public static int func_149890_d(int p_149890_0_)
    {
        return p_149890_0_ & 7;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return func_149887_c(meta) ? this.doublePlantBottomIcons[0] : this.doublePlantBottomIcons[meta & 7];
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149888_a(boolean p_149888_1_, int p_149888_2_)
    {
        return p_149888_1_ ? this.doublePlantTopIcons[p_149888_2_] : this.doublePlantBottomIcons[p_149888_2_];
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = this.func_149885_e(worldIn, x, y, z);
        return l != 2 && l != 3 ? 16777215 : worldIn.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
    }

    public void func_149889_c(World p_149889_1_, int p_149889_2_, int p_149889_3_, int p_149889_4_, int p_149889_5_, int p_149889_6_)
    {
        p_149889_1_.setBlock(p_149889_2_, p_149889_3_, p_149889_4_, this, p_149889_5_, p_149889_6_);
        p_149889_1_.setBlock(p_149889_2_, p_149889_3_ + 1, p_149889_4_, this, 8, p_149889_6_);
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
    {
        int l = ((MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
        worldIn.setBlock(x, y + 1, z, this, 8 | l, 2);
    }

    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta)
    {
        if (worldIn.isRemote || player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != Items.shears || func_149887_c(meta) || !this.func_149886_b(worldIn, x, y, z, meta, player))
        {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player)
    {
        if (func_149887_c(meta))
        {
            if (worldIn.getBlock(x, y - 1, z) == this)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    int i1 = worldIn.getBlockMetadata(x, y - 1, z);
                    int j1 = func_149890_d(i1);

                    if (j1 != 3 && j1 != 2)
                    {
                        worldIn.breakBlock(x, y - 1, z, true);
                    }
                    else
                    {
                        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
                        {
                            this.func_149886_b(worldIn, x, y, z, i1, player);
                        }

                        worldIn.setBlockToAir(x, y - 1, z);
                    }
                }
                else
                {
                    worldIn.setBlockToAir(x, y - 1, z);
                }
            }
        }
        else if (player.capabilities.isCreativeMode && worldIn.getBlock(x, y + 1, z) == this)
        {
            worldIn.setBlock(x, y + 1, z, Blocks.air, 0, 2);
        }

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    private boolean func_149886_b(World p_149886_1_, int p_149886_2_, int p_149886_3_, int p_149886_4_, int p_149886_5_, EntityPlayer p_149886_6_)
    {
        int i1 = func_149890_d(p_149886_5_);

        if (i1 != 3 && i1 != 2)
        {
            return false;
        }
        else
        {
            p_149886_6_.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
            byte b0 = 1;

            if (i1 == 3)
            {
                b0 = 2;
            }

            this.dropBlockAsItem(p_149886_1_, p_149886_2_, p_149886_3_, p_149886_4_, new ItemStack(Blocks.tallgrass, 2, b0));
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.doublePlantBottomIcons = new IIcon[field_149892_a.length];
        this.doublePlantTopIcons = new IIcon[field_149892_a.length];

        for (int i = 0; i < this.doublePlantBottomIcons.length; ++i)
        {
            this.doublePlantBottomIcons[i] = reg.registerIcon("double_plant_" + field_149892_a[i] + "_bottom");
            this.doublePlantTopIcons[i] = reg.registerIcon("double_plant_" + field_149892_a[i] + "_top");
        }

        this.sunflowerIcons = new IIcon[2];
        this.sunflowerIcons[0] = reg.registerIcon("double_plant_sunflower_front");
        this.sunflowerIcons[1] = reg.registerIcon("double_plant_sunflower_back");
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        for (int i = 0; i < this.doublePlantBottomIcons.length; ++i)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        int l = worldIn.getBlockMetadata(x, y, z);
        return func_149887_c(l) ? func_149890_d(worldIn.getBlockMetadata(x, y - 1, z)) : func_149890_d(l);
    }

    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient)
    {
        int l = this.func_149885_e(worldIn, x, y, z);
        return l != 2 && l != 3;
    }

    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z)
    {
        return true;
    }

    public void fertilize(World worldIn, Random random, int x, int y, int z)
    {
        int l = this.func_149885_e(worldIn, x, y, z);
        this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(this, 1, l));
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        int type = func_149890_d(metadata);
        return func_149887_c(metadata) && (type == 3 || type == 4);
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int type = func_149890_d(world.getBlockMetadata(x, y, z));
        if (type == 3 || type == 2)
            ret.add(new ItemStack(Blocks.tallgrass, 2, type == 3 ? 2 : 1));
        return ret;
    }
}