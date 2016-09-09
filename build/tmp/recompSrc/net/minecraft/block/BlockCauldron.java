package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon iconInner;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    private static final String __OBFID = "CL_00000213";

    public BlockCauldron()
    {
        super(Material.iron);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : this.blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.iconInner = reg.registerIcon(this.getTextureName() + "_" + "inner");
        this.iconTop = reg.registerIcon(this.getTextureName() + "_top");
        this.iconBottom = reg.registerIcon(this.getTextureName() + "_" + "bottom");
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBoundsForItemRender();
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getCauldronIcon(String iconName)
    {
        return iconName.equals("inner") ? Blocks.cauldron.iconInner : (iconName.equals("bottom") ? Blocks.cauldron.iconBottom : null);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 24;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn)
    {
        int l = getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));
        float f = (float)y + (6.0F + (float)(3 * l)) / 16.0F;

        if (!worldIn.isRemote && entityIn.isBurning() && l > 0 && entityIn.boundingBox.minY <= (double)f)
        {
            entityIn.extinguish();
            this.setWaterLevel(worldIn, x, y, z, l - 1);
        }
    }

    /**
     * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
     * Return : Swing hand (client), abort the block placement (server)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            ItemStack itemstack = player.inventory.getCurrentItem();

            if (itemstack == null)
            {
                return true;
            }
            else
            {
                int i1 = worldIn.getBlockMetadata(x, y, z);
                int j1 = getPowerFromMeta(i1);

                if (itemstack.getItem() == Items.water_bucket)
                {
                    if (j1 < 3)
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                        }

                        this.setWaterLevel(worldIn, x, y, z, 3);
                    }

                    return true;
                }
                else
                {
                    if (itemstack.getItem() == Items.glass_bottle)
                    {
                        if (j1 > 0)
                        {
                            if (!player.capabilities.isCreativeMode)
                            {
                                ItemStack itemstack1 = new ItemStack(Items.potionitem, 1, 0);

                                if (!player.inventory.addItemStackToInventory(itemstack1))
                                {
                                    worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)x + 0.5D, (double)y + 1.5D, (double)z + 0.5D, itemstack1));
                                }
                                else if (player instanceof EntityPlayerMP)
                                {
                                    ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                                }

                                --itemstack.stackSize;

                                if (itemstack.stackSize <= 0)
                                {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                                }
                            }

                            this.setWaterLevel(worldIn, x, y, z, j1 - 1);
                        }
                    }
                    else if (j1 > 0 && itemstack.getItem() instanceof ItemArmor && ((ItemArmor)itemstack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.CLOTH)
                    {
                        ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                        itemarmor.removeColor(itemstack);
                        this.setWaterLevel(worldIn, x, y, z, j1 - 1);
                        return true;
                    }

                    return false;
                }
            }
        }
    }

    public void setWaterLevel(World worldIn, int x, int y, int z, int level)
    {
        worldIn.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(level, 0, 3), 2);
        worldIn.updateNeighborsAboutBlockChange(x, y, z, this);
    }

    /**
     * currently only used by BlockCauldron to incrament meta-data during rain
     */
    public void fillWithRain(World worldIn, int x, int y, int z)
    {
        if (worldIn.rand.nextInt(20) == 1)
        {
            int l = worldIn.getBlockMetadata(x, y, z);

            if (l < 3)
            {
                worldIn.setBlockMetadataWithNotify(x, y, z, l + 1, 2);
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Items.cauldron;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Items.cauldron;
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side)
    {
        int i1 = worldIn.getBlockMetadata(x, y, z);
        return getPowerFromMeta(i1);
    }

    public static int getPowerFromMeta(int meta)
    {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public static float getRenderLiquidLevel(int meta)
    {
        int j = MathHelper.clamp_int(meta, 0, 3);
        return (float)(6 + 3 * j) / 16.0F;
    }
}