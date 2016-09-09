package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity
{
    private final MobSpawnerBaseLogic field_145882_a = new MobSpawnerBaseLogic()
    {
        private static final String __OBFID = "CL_00000361";
        public void func_98267_a(int p_98267_1_)
        {
            TileEntityMobSpawner.this.worldObj.addBlockEvent(TileEntityMobSpawner.this.xCoord, TileEntityMobSpawner.this.yCoord, TileEntityMobSpawner.this.zCoord, Blocks.mob_spawner, p_98267_1_, 0);
        }
        public World getSpawnerWorld()
        {
            return TileEntityMobSpawner.this.worldObj;
        }
        public int getSpawnerX()
        {
            return TileEntityMobSpawner.this.xCoord;
        }
        public int getSpawnerY()
        {
            return TileEntityMobSpawner.this.yCoord;
        }
        public int getSpawnerZ()
        {
            return TileEntityMobSpawner.this.zCoord;
        }
        public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart p_98277_1_)
        {
            super.setRandomEntity(p_98277_1_);

            if (this.getSpawnerWorld() != null)
            {
                this.getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.xCoord, TileEntityMobSpawner.this.yCoord, TileEntityMobSpawner.this.zCoord);
            }
        }
    };
    private static final String __OBFID = "CL_00000360";

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.field_145882_a.readFromNBT(compound);
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.field_145882_a.writeToNBT(compound);
    }

    public void updateEntity()
    {
        this.field_145882_a.updateSpawner();
        super.updateEntity();
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        nbttagcompound.removeTag("SpawnPotentials");
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbttagcompound);
    }

    public boolean receiveClientEvent(int id, int type)
    {
        return this.field_145882_a.setDelayToMin(id) ? true : super.receiveClientEvent(id, type);
    }

    public MobSpawnerBaseLogic func_145881_a()
    {
        return this.field_145882_a;
    }
}