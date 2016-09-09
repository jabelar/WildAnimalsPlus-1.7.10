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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon field_150200_a;
    @SideOnly(Side.CLIENT)
    private IIcon field_150199_b;
    private static final String __OBFID = "CL_00000273";

    protected BlockMycelium()
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
        return side == 1 ? this.field_150200_a : (side == 0 ? Blocks.dirt.getBlockTextureFromSide(side) : this.blockIcon);
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
                        worldIn.setBlock(i1, j1, k1, this);
                    }
                }
            }
        }
    }

    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return Blocks.dirt.getItemDropped(0, random, fortune);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        if (side == 1)
        {
            return this.field_150200_a;
        }
        else if (side == 0)
        {
            return Blocks.dirt.getBlockTextureFromSide(side);
        }
        else
        {
            Material material = worldIn.getBlock(x, y + 1, z).getMaterial();
            return material != Material.snow && material != Material.craftedSnow ? this.blockIcon : this.field_150199_b;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.field_150200_a = reg.registerIcon(this.getTextureName() + "_top");
        this.field_150199_b = reg.registerIcon("grass_side_snowed");
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        super.randomDisplayTick(worldIn, x, y, z, random);

        if (random.nextInt(10) == 0)
        {
            worldIn.spawnParticle("townaura", (double)((float)x + random.nextFloat()), (double)((float)y + 1.1F), (double)((float)z + random.nextFloat()), 0.0D, 0.0D, 0.0D);
        }
    }
}