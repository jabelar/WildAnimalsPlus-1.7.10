package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer
{
    private static final String __OBFID = "CL_00000278";

    public BlockNote()
    {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        boolean flag = worldIn.isBlockIndirectlyGettingPowered(x, y, z);
        TileEntityNote tileentitynote = (TileEntityNote)worldIn.getTileEntity(x, y, z);

        if (tileentitynote != null && tileentitynote.previousRedstoneState != flag)
        {
            if (flag)
            {
                tileentitynote.triggerNote(worldIn, x, y, z);
            }

            tileentitynote.previousRedstoneState = flag;
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
            TileEntityNote tileentitynote = (TileEntityNote)worldIn.getTileEntity(x, y, z);

            if (tileentitynote != null)
            {
                int old = tileentitynote.note;
                tileentitynote.changePitch();
                if (old == tileentitynote.note) return false;
                tileentitynote.triggerNote(worldIn, x, y, z);
            }

            return true;
        }
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            TileEntityNote tileentitynote = (TileEntityNote)worldIn.getTileEntity(x, y, z);

            if (tileentitynote != null)
            {
                tileentitynote.triggerNote(worldIn, x, y, z);
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityNote();
    }

    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData)
    {
        int meta = worldIn.getBlockMetadata(x, y, z);
        net.minecraftforge.event.world.NoteBlockEvent.Play e = new net.minecraftforge.event.world.NoteBlockEvent.Play(worldIn, x, y, z, meta, eventData, eventId);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) return false;
        eventId = e.instrument.ordinal();
        eventData = e.getVanillaNoteId(); 
        float f = (float)Math.pow(2.0D, (double)(eventData - 12) / 12.0D);
        String s = "harp";

        if (eventId == 1)
        {
            s = "bd";
        }

        if (eventId == 2)
        {
            s = "snare";
        }

        if (eventId == 3)
        {
            s = "hat";
        }

        if (eventId == 4)
        {
            s = "bassattack";
        }

        worldIn.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "note." + s, 3.0F, f);
        worldIn.spawnParticle("note", (double)x + 0.5D, (double)y + 1.2D, (double)z + 0.5D, (double)eventData / 24.0D, 0.0D, 0.0D);
        return true;
    }
}