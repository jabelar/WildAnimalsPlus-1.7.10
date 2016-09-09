package net.minecraft.item;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLog;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.BonemealEvent;

public class ItemDye extends Item
{
    public static final String[] dyeColorNames = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
    public static final String[] dyeIcons = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
    public static final int[] dyeColors = new int[] {1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};
    /** Contains all dye icons, parsed from ItemDye.dyeIcons */
    @SideOnly(Side.CLIENT)
    private IIcon[] dyeIconArray;
    private static final String __OBFID = "CL_00000022";

    public ItemDye()
    {
        this.setHasSubtypes(true);
        this.setMaxDurability(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        int j = MathHelper.clamp_int(p_77617_1_, 0, 15);
        return this.dyeIconArray[j];
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        int i = MathHelper.clamp_int(stack.getMetadata(), 0, 15);
        return super.getUnlocalizedName() + "." + dyeColorNames[i];
    }

    /**
     * Description : Callback for item usage. If the item does something special on right clicking, he will have one of
     * those. Return True if something happen and false if it don't. This is for ITEMS, not BLOCKS. Args : stack,
     * player, world, x, y, z, side, hitX, hitY, hitZ
     */
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if (!p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_))
        {
            return false;
        }
        else
        {
            if (p_77648_1_.getMetadata() == 15)
            {
                if (applyBonemeal(p_77648_1_, p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_, p_77648_2_))
                {
                    if (!p_77648_3_.isRemote)
                    {
                        p_77648_3_.playAuxSFX(2005, p_77648_4_, p_77648_5_, p_77648_6_, 0);
                    }

                    return true;
                }
            }
            else if (p_77648_1_.getMetadata() == 3)
            {
                Block block = p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_);
                int i1 = p_77648_3_.getBlockMetadata(p_77648_4_, p_77648_5_, p_77648_6_);

                if (block == Blocks.log && BlockLog.func_150165_c(i1) == 3)
                {
                    if (p_77648_7_ == 0)
                    {
                        return false;
                    }

                    if (p_77648_7_ == 1)
                    {
                        return false;
                    }

                    if (p_77648_7_ == 2)
                    {
                        --p_77648_6_;
                    }

                    if (p_77648_7_ == 3)
                    {
                        ++p_77648_6_;
                    }

                    if (p_77648_7_ == 4)
                    {
                        --p_77648_4_;
                    }

                    if (p_77648_7_ == 5)
                    {
                        ++p_77648_4_;
                    }

                    if (p_77648_3_.isAirBlock(p_77648_4_, p_77648_5_, p_77648_6_))
                    {
                        int j1 = Blocks.cocoa.onBlockPlaced(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_, 0);
                        p_77648_3_.setBlock(p_77648_4_, p_77648_5_, p_77648_6_, Blocks.cocoa, j1, 2);

                        if (!p_77648_2_.capabilities.isCreativeMode)
                        {
                            --p_77648_1_.stackSize;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public static boolean func_150919_a(ItemStack stack, World worldIn, int x, int y, int z)
    {
        if (worldIn instanceof WorldServer)
            return applyBonemeal(stack, worldIn, x, y, z, FakePlayerFactory.getMinecraft((WorldServer)worldIn));
        return false;
    }

    public static boolean applyBonemeal(ItemStack stack, World worldIn, int x, int y, int z, EntityPlayer player)
    {
        Block block = worldIn.getBlock(x, y, z);

        BonemealEvent event = new BonemealEvent(player, worldIn, block, x, y, z);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        if (event.getResult() == Result.ALLOW)
        {
            if (!worldIn.isRemote)
            {
                stack.stackSize--;
            }
            return true;
        }

        if (block instanceof IGrowable)
        {
            IGrowable igrowable = (IGrowable)block;

            if (igrowable.canFertilize(worldIn, x, y, z, worldIn.isRemote))
            {
                if (!worldIn.isRemote)
                {
                    if (igrowable.shouldFertilize(worldIn, worldIn.rand, x, y, z))
                    {
                        igrowable.fertilize(worldIn, worldIn.rand, x, y, z);
                    }

                    --stack.stackSize;
                }

                return true;
            }
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void func_150918_a(World p_150918_0_, int p_150918_1_, int p_150918_2_, int p_150918_3_, int p_150918_4_)
    {
        if (p_150918_4_ == 0)
        {
            p_150918_4_ = 15;
        }

        Block block = p_150918_0_.getBlock(p_150918_1_, p_150918_2_, p_150918_3_);

        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(p_150918_0_, p_150918_1_, p_150918_2_, p_150918_3_);

            for (int i1 = 0; i1 < p_150918_4_; ++i1)
            {
                double d0 = itemRand.nextGaussian() * 0.02D;
                double d1 = itemRand.nextGaussian() * 0.02D;
                double d2 = itemRand.nextGaussian() * 0.02D;
                p_150918_0_.spawnParticle("happyVillager", (double)((float)p_150918_1_ + itemRand.nextFloat()), (double)p_150918_2_ + (double)itemRand.nextFloat() * block.getBlockBoundsMaxY(), (double)((float)p_150918_3_ + itemRand.nextFloat()), d0, d1, d2);
            }
        }
        else
        {
            for (int i1 = 0; i1 < p_150918_4_; ++i1)
            {
                double d0 = itemRand.nextGaussian() * 0.02D;
                double d1 = itemRand.nextGaussian() * 0.02D;
                double d2 = itemRand.nextGaussian() * 0.02D;
                p_150918_0_.spawnParticle("happyVillager", (double)((float)p_150918_1_ + itemRand.nextFloat()), (double)p_150918_2_ + (double)itemRand.nextFloat() * 1.0f, (double)((float)p_150918_3_ + itemRand.nextFloat()), d0, d1, d2);
            }
        }
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     *  
     * @param stack the item stack of the item being used
     * @param player the player who used the item
     * @param target the target we hit with the item in hand
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target)
    {
        if (target instanceof EntitySheep)
        {
            EntitySheep entitysheep = (EntitySheep)target;
            int i = BlockColored.func_150032_b(stack.getMetadata());

            if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != i)
            {
                entitysheep.setFleeceColor(i);
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        for (int i = 0; i < 16; ++i)
        {
            p_150895_3_.add(new ItemStack(p_150895_1_, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        this.dyeIconArray = new IIcon[dyeIcons.length];

        for (int i = 0; i < dyeIcons.length; ++i)
        {
            this.dyeIconArray[i] = register.registerIcon(this.getIconString() + "_" + dyeIcons[i]);
        }
    }
}