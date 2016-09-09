package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockGrass extends Block implements IGrowable
{
    private static final Logger logger = LogManager.getLogger();
    @SideOnly(Side.CLIENT)
    private IIcon field_149991_b;
    @SideOnly(Side.CLIENT)
    private IIcon field_149993_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_149994_N;
    private static final String __OBFID = "CL_00000251";

    protected BlockGrass()
    {
        super(Material.grass);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.field_149991_b : (side == 0 ? Blocks.dirt.getBlockTextureFromSide(side) : this.blockIcon);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (!worldIn.isRemote)
        {
            if (worldIn.getBlockLightValue(x, y + 1, z) < 4 && worldIn.getBlockLightOpacity(x, y + 1, z) > 2)
            {
                worldIn.setBlock(x, y, z, Blocks.dirt);
            }
            else if (worldIn.getBlockLightValue(x, y + 1, z) >= 9)
            {
                for (int l = 0; l < 4; ++l)
                {
                    int i1 = x + random.nextInt(3) - 1;
                    int j1 = y + random.nextInt(5) - 3;
                    int k1 = z + random.nextInt(3) - 1;
                    Block block = worldIn.getBlock(i1, j1 + 1, k1);

                    if (worldIn.getBlock(i1, j1, k1) == Blocks.dirt && worldIn.getBlockMetadata(i1, j1, k1) == 0 && worldIn.getBlockLightValue(i1, j1 + 1, k1) >= 4 && worldIn.getBlockLightOpacity(i1, j1 + 1, k1) <= 2)
                    {
                        worldIn.setBlock(i1, j1, k1, Blocks.grass);
                    }
                }
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Blocks.dirt.getItemDropped(0, random, fortune);
    }

    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient)
    {
        return true;
    }

    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        if (side == 1)
        {
            return this.field_149991_b;
        }
        else if (side == 0)
        {
            return Blocks.dirt.getBlockTextureFromSide(side);
        }
        else
        {
            Material material = worldIn.getBlock(x, y + 1, z).getMaterial();
            return material != Material.snow && material != Material.craftedSnow ? this.blockIcon : this.field_149993_M;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.field_149991_b = reg.registerIcon(this.getTextureName() + "_top");
        this.field_149993_M = reg.registerIcon(this.getTextureName() + "_side_snowed");
        this.field_149994_N = reg.registerIcon(this.getTextureName() + "_side_overlay");
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        double d0 = 0.5D;
        double d1 = 1.0D;
        return ColorizerGrass.getGrassColor(d0, d1);
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta)
    {
        return this.getBlockColor();
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z)
    {
        int l = 0;
        int i1 = 0;
        int j1 = 0;

        for (int k1 = -1; k1 <= 1; ++k1)
        {
            for (int l1 = -1; l1 <= 1; ++l1)
            {
                int i2 = worldIn.getBiomeGenForCoords(x + l1, z + k1).getBiomeGrassColor(x + l1, y, z + k1);
                l += (i2 & 16711680) >> 16;
                i1 += (i2 & 65280) >> 8;
                j1 += i2 & 255;
            }
        }

        return (l / 9 & 255) << 16 | (i1 / 9 & 255) << 8 | j1 / 9 & 255;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getIconSideOverlay()
    {
        return Blocks.grass.field_149994_N;
    }

    public void fertilize(World worldIn, Random random, int x, int y, int z)
    {
        int l = 0;

        while (l < 128)
        {
            int i1 = x;
            int j1 = y + 1;
            int k1 = z;
            int l1 = 0;

            while (true)
            {
                if (l1 < l / 16)
                {
                    i1 += random.nextInt(3) - 1;
                    j1 += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                    k1 += random.nextInt(3) - 1;

                    if (worldIn.getBlock(i1, j1 - 1, k1) == Blocks.grass && !worldIn.getBlock(i1, j1, k1).isNormalCube())
                    {
                        ++l1;
                        continue;
                    }
                }
                else if (worldIn.getBlock(i1, j1, k1).blockMaterial == Material.air)
                {
                    if (random.nextInt(8) != 0)
                    {
                        if (Blocks.tallgrass.canBlockStay(worldIn, i1, j1, k1))
                        {
                            worldIn.setBlock(i1, j1, k1, Blocks.tallgrass, 1, 3);
                        }
                    }
                    else
                    {
                        worldIn.getBiomeGenForCoords(i1, k1).plantFlower(worldIn, random, i1, j1, k1);
                    }
                }

                ++l;
                break;
            }
        }
    }
}